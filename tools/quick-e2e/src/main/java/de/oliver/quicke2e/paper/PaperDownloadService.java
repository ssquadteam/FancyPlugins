package de.oliver.quicke2e.paper;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PaperDownloadService {

    private static final Gson GSON = new Gson();
    private static final String BASE_URL = "https://api.papermc.io/v2";
    private static final String destination = "servers";

    private final HttpClient client;

    public PaperDownloadService() {
        this.client = HttpClient.newHttpClient();
    }

    public void downloadServerFile(
            String project,
            String version,
            String build
    ) {
        Path folderPath = Paths.get(String.format("%s/%s_%s_%s/", destination, project, version, build));
        if (!folderPath.toFile().exists()) {
            folderPath.toFile().mkdirs();
        }

        String buildNumber = build.equals("latest") ? getLatestBuildNumber(project, version) : build;

        Path filePath = Paths.get(String.format("%s/%s_%s_%s/%s-%s-%s.jar", destination, project, version, build, project, version, buildNumber));

        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("%s/projects/%s/versions/%s/builds/%s/downloads/%s-%s-%s.jar", BASE_URL, project, version, buildNumber, project, version, buildNumber)))
                .build();

        client.sendAsync(req, HttpResponse.BodyHandlers.ofFile(filePath))
                .thenAccept(_ -> System.out.println("Downloaded server file to " + filePath))
                .join();
    }

    private String getLatestBuildNumber(
            String project,
            String version
    ) {
        HttpRequest req = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format("%s/projects/%s/versions/%s", BASE_URL, project, version)))
                .build();

        HttpResponse<String> resp = client.sendAsync(req, HttpResponse.BodyHandlers.ofString()).join();
        BuildsResponse builds = GSON.fromJson(resp.body(), BuildsResponse.class);

        return builds.builds()[builds.builds().length - 1];
    }

}
