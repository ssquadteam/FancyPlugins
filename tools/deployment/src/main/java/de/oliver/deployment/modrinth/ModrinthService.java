package de.oliver.deployment.modrinth;

import com.google.gson.Gson;
import de.oliver.deployment.Configuration;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModrinthService {
    private static final String BASE_URL = "https://api.modrinth.com/v2/version";
    private final OkHttpClient client = new OkHttpClient();
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
                config.supportedVersions(),
                config.channel(),
                config.loaders(),
                config.featured(),
                "draft",
                config.projectID(),
                new String[]{"plugin"},
                "plugin"
        );

        File pluginFile = new File(config.pluginJarPath());

        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("data", new Gson().toJson(req))
                .addFormDataPart("plugin", pluginFile.getName(),
                        RequestBody.create(pluginFile, MediaType.parse("application/java-archive")))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL)
                .addHeader("Authorization", apiKey)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Response: " + response.code() + " - " + response.body().string());
        }
    }
}