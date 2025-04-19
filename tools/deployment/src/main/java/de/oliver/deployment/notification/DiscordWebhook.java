package de.oliver.deployment.notification;

import de.oliver.fancyanalytics.sdk.utils.HttpRequest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.List;

public class DiscordWebhook {

    public void sendWebhook(String webhookUrl, Data data) {
        HttpRequest request = new HttpRequest(webhookUrl)
                .withMethod("POST")
                .withHeader("Content-Type", "application/json")
                .withBody(data);

        try {
            HttpResponse<String> resp = request.send();
            if (resp.statusCode() != 204) {
                System.out.println("Failed to send message with discord webhook: " + resp.body());
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            System.out.println("Failed to send webhook");
            e.printStackTrace();
        }
    }

    public record Data(String content, List<Embed> embeds) {

        public record Embed(String title, String description, int color) {
        }
    }

}
