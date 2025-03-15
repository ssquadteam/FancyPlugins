package de.oliver.deployment;

import com.google.gson.Gson;
import de.oliver.deployment.modrinth.ModrinthService;

import java.io.IOException;

public class Main {

    private static final Gson GSON = new Gson();

    public static void main(String[] args) {
        String configFilePath = args[0];
        Configuration configuration = GSON.fromJson(configFilePath, Configuration.class);

        String modrinthApiKey = System.getenv("MODRINTH_API_KEY");
        ModrinthService modrinthService = new ModrinthService(modrinthApiKey);
        try {
            modrinthService.deployPlugin(configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
