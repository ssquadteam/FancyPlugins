package de.oliver.deployment;

import com.google.gson.Gson;
import de.oliver.deployment.git.GitService;
import de.oliver.deployment.modrinth.ModrinthService;
import de.oliver.deployment.notification.DiscordWebhook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

        String discordWebhookUrl = System.getenv("DISCORD_WEBHOOK_URL");
        if (discordWebhookUrl != null) {
            DiscordWebhook.Data data = new DiscordWebhook.Data("Deployment completed", List.of(
                    new DiscordWebhook.Data.Embed(
                            "New version of "+configuration.projectName(),
                            """
                                    **Version:** %s
                                    **Channel:** %s
                                    **Commit:** [%s](https://github.com/FancyMcPlugins/fancyplugins/commit/%s)
                                    **Download:** %s
                                    """.formatted(
                                            configuration.readVersion(),
                                            GitService.getCommitHash().substring(0, 7),
                                            configuration.channel(),
                                            "https://modrinth.com/plugin/"+configuration.projectName()+"/version/"+configuration.readVersion()),

                            0x00FF00
                    )
            ));
            DiscordWebhook discordWebhook = new DiscordWebhook();
            discordWebhook.sendWebhook(discordWebhookUrl, data);
        } else {
            System.out.println("Discord webhook URL not set. Skipping notification.");
        }

    }

}
