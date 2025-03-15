package de.oliver.deployment;

import com.google.gson.Gson;
import de.oliver.deployment.modrinth.ModrinthService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private static final Gson GSON = new Gson();

    public static void main(String[] args) throws IOException {
        String configFilePath = args[0];
        String configData = Files.readString(Path.of(configFilePath));
        Configuration configuration = GSON.fromJson(configData, Configuration.class);

        String modrinthApiKey = System.getenv("MODRINTH_API_KEY");
        ModrinthService modrinthService = new ModrinthService(modrinthApiKey);
        try {
            modrinthService.deployPlugin(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
