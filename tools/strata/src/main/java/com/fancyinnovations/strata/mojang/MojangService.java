package com.fancyinnovations.strata.mojang;

import com.fancyinnovations.strata.Strata;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MojangService {

    private static final String MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";

    private final Strata strata;
    private final HttpClient http;

    private PistonVersionManifestV2 cachedManifest;
    private Map<String, PistonVersionDetails> cachedVersionDetails;

    public MojangService(Strata strata) {
        this.strata = strata;
        this.http = HttpClient.newHttpClient();
        this.cachedManifest = null;
        this.cachedVersionDetails = new ConcurrentHashMap<>();
    }

    public void loadCache() {
        Path cachePath = strata.getCacheDir().toPath();

        Path versionManifestPath = cachePath.resolve("piston/version_manifest_v2.json");
        if (Files.exists(versionManifestPath)) {
            try {
                cachedManifest = Strata.GSON.fromJson(
                        Files.readString(versionManifestPath),
                        PistonVersionManifestV2.class
                );

                strata.getLogger().info("Loaded cached version manifest v2 from disk");
            } catch (IOException e) {
                strata.getLogger().error(
                        "Failed to load cached version manifest v2 from disk",
                        StringProperty.of("path", versionManifestPath.toString()),
                        ThrowableProperty.of(e)
                );
            }
        }

        Path versionsPath = cachePath.resolve("piston/versions");
        if (Files.exists(versionsPath) && Files.isDirectory(versionsPath)) {
            try {
                Files.list(versionsPath)
                        .filter(Files::isRegularFile)
                        .forEach(path -> {
                            try {
                                PistonVersionDetails details = Strata.GSON.fromJson(
                                        Files.readString(path),
                                        PistonVersionDetails.class
                                );
                                cachedVersionDetails.put(details.id(), details);
                            } catch (IOException e) {
                                strata.getLogger().error(
                                        "Failed to load cached version details from disk",
                                        StringProperty.of("path", path.toString()),
                                        ThrowableProperty.of(e)
                                );
                            }
                        });
            } catch (IOException e) {
                strata.getLogger().error(
                        "Failed to list cached version details from disk",
                        StringProperty.of("path", versionsPath.toString()),
                        ThrowableProperty.of(e)
                );
            }

            strata.getLogger().info("Loaded cached version details from disk: " + cachedVersionDetails.size() + " versions");
        }
    }

    public PistonVersionDetails getVersion(String version) {
        PistonVersionManifestV2 manifest = fetchPistonVersionManifest();
        if (manifest == null) {
            return null;
        }

        PistonVersionManifestV2.Version ver = manifest.getVersion(version);
        if (ver == null) {
            strata.getLogger().warn(
                    "Version not found in manifest",
                    StringProperty.of("version", version)
            );
            return null;
        }

        return fetchPistonVersionDetails(ver);
    }

    public PistonVersionDetails getLatestRelease() {
        PistonVersionManifestV2 manifest = fetchPistonVersionManifest();
        if (manifest == null) {
            return null;
        }

        PistonVersionManifestV2.Version version = manifest.getVersion(manifest.latest().release());
        if (version == null) {
            strata.getLogger().warn(
                    "Latest release version not found in manifest",
                    StringProperty.of("version", manifest.latest().release())
            );
            return null;
        }

        return fetchPistonVersionDetails(version);
    }

    public PistonVersionDetails getLatestSnapshot() {
        PistonVersionManifestV2 manifest = fetchPistonVersionManifest();
        if (manifest == null) {
            return null;
        }

        PistonVersionManifestV2.Version version = manifest.getVersion(manifest.latest().snapshot());
        if (version == null) {
            strata.getLogger().warn(
                    "Latest snapshot version not found in manifest",
                    StringProperty.of("version", manifest.latest().snapshot())
            );
            return null;
        }

        return fetchPistonVersionDetails(version);
    }

    /**
     * Downloads the server (bundle) jar for the given version details and returns the path to the downloaded file.
     * If the file already exists in the cache, it will return the cached file path instead.
     *
     * @param details The version details for which to download the server jar.
     */
    public void downloadServerBundle(PistonVersionDetails details) {
        String serverJarUrl = details.downloads().server().url();
        String versionId = details.id();

        Path bundlesPath = strata.getCacheDir().toPath().resolve("server-bundles");
        Path serverBundlePath = bundlesPath.resolve("server-bundle-" + versionId + ".jar");

        if (Files.exists(serverBundlePath)) {
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverJarUrl))
                .build();

        HttpResponse<byte[]> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException e) {
            strata.getLogger().error(
                    "Failed to download server jar",
                    StringProperty.of("version", versionId),
                    StringProperty.of("url", serverJarUrl),
                    ThrowableProperty.of(e)
            );
            return;
        }

        try {
            Files.createDirectories(bundlesPath);
            Files.write(serverBundlePath, response.body());
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to save downloaded server jar to disk",
                    StringProperty.of("version", versionId),
                    StringProperty.of("path", serverBundlePath.toString()),
                    ThrowableProperty.of(e)
            );
        }

        strata.getLogger().info(
                "Downloaded server jar for version " + versionId,
                StringProperty.of("version", versionId),
                StringProperty.of("url", serverJarUrl),
                StringProperty.of("savedPath", serverBundlePath.toString())
        );
    }

    private PistonVersionManifestV2 fetchPistonVersionManifest() {
        if (cachedManifest != null) {
            return cachedManifest;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MANIFEST_URL))
                .build();

        HttpResponse<String> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            strata.getLogger().error(
                    "Failed to fetch version manifest v2 from Mojang",
                    StringProperty.of("url", MANIFEST_URL),
                    ThrowableProperty.of(e)
            );
            return null;
        }

        PistonVersionManifestV2 manifest = Strata.GSON.fromJson(response.body(), PistonVersionManifestV2.class);

        cachedManifest = manifest;
        try {
            Files.createDirectories(strata.getCacheDir().toPath().resolve("piston"));
            Files.writeString(strata.getCacheDir().toPath().resolve("piston/version_manifest_v2.json"), response.body());
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to cache version manifest v2 to disk",
                    StringProperty.of("path", strata.getCacheDir().toPath().resolve("piston/version_manifest_v2.json").toString()),
                    ThrowableProperty.of(e)
            );
        }

        strata.getLogger().info(
                "Fetched version manifest v2 from Mojang and cached to disk",
                StringProperty.of("url", MANIFEST_URL),
                StringProperty.of("versionCount", String.valueOf(manifest.versions().length))
        );

        return manifest;
    }

    private PistonVersionDetails fetchPistonVersionDetails(PistonVersionManifestV2.Version version) {
        if (cachedVersionDetails.containsKey(version.id())) {
            return cachedVersionDetails.get(version.id());
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(version.url()))
                .build();

        HttpResponse<String> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            strata.getLogger().error(
                    "Failed to fetch version details from Mojang",
                    StringProperty.of("version", version.id()),
                    StringProperty.of("url", version.url()),
                    ThrowableProperty.of(e)
            );
            return null;
        }

        PistonVersionDetails details = Strata.GSON.fromJson(response.body(), PistonVersionDetails.class);

        cachedVersionDetails.put(version.id(), details);
        try {
            Files.createDirectories(strata.getCacheDir().toPath().resolve("piston/versions"));
            Files.writeString(strata.getCacheDir().toPath().resolve("piston/versions/" + version.id() + ".json"), response.body());
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to cache version details to disk",
                    StringProperty.of("version", version.id()),
                    StringProperty.of("path", strata.getCacheDir().toPath().resolve("piston/versions/" + version.id() + ".json").toString()),
                    ThrowableProperty.of(e)
            );
        }

        strata.getLogger().info(
                "Fetched version details for version " + version.id() + " from Mojang and cached to disk",
                StringProperty.of("version", version.id()),
                StringProperty.of("url", version.url())
        );

        return details;
    }
}
