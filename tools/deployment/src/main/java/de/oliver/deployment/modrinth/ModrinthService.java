package de.oliver.deployment.modrinth;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.oliver.deployment.Configuration;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class ModrinthService {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String BASE_URL = "https://api.modrinth.com/version";
    private final OkHttpClient client = new OkHttpClient(
            new OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .callTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
    );
    private final String apiKey;

    public ModrinthService(String apiKey) {
        this.apiKey = apiKey;
    }

    public void deployPlugin(Configuration config) throws IOException {
        String changelog = Files.readString(Path.of(config.changeLogPath()));
        String version = Files.readString(Path.of(config.versionPath()));

        CreateVersionRequest req = new CreateVersionRequest(
                version,
                version,
                changelog,
                new String[0],
                config.supportedVersions(),
                config.channel(),
                config.loaders(),
                config.featured(),
                "draft",
                "draft",
                config.projectID(),
                new String[]{"plugin", "data"},
                "plugin"
        );

        String pluginJarPath = config.pluginJarPath().replace("%VERSION%", version);
        File pluginFile = new File(pluginJarPath);

        String jsonData = GSON.toJson(req);

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", jsonData)
                .addFormDataPart("plugin", pluginFile.getName(),
                        RequestBody.create(pluginFile, MediaType.parse("application/java-archive")))
                .build();

        System.out.println(jsonData);

        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .addHeader("Authorization", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response: " + response.code() + " - " + response.body().string());
        }
    }
}