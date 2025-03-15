package de.oliver.deployment;

import com.google.gson.Gson;

public class Main {

    private static final Gson GSON = new Gson();

    public static void main(String[] args) {
        String configFilePath = args[0];
        Configuration configuration = GSON.fromJson(configFilePath, Configuration.class);
    }

}
