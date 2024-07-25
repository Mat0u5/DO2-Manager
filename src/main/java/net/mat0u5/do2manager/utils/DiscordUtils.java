package net.mat0u5.do2manager.utils;

import com.google.gson.JsonObject;
import net.mat0u5.do2manager.Main;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordUtils {
    public static JsonObject getDefaultJSON() {
        JsonObject json = new JsonObject();
        json.addProperty("username", "Aggro-Net");
        json.addProperty("avatar_url", "https://cdn.discordapp.com/avatars/1190831390237937876/5158470a4333a126c5b7fb545e4fea19?size=1024");
        return json;
    }
    public static void sendMessageToDiscord(String message) {
        JsonObject json = getDefaultJSON();
        json.addProperty("content", message);
        sendMessageToDiscord(json);
    }
    public static void sendMessageToDiscord(String message, String username, String avatarURL) {
        JsonObject json = new JsonObject();
        json.addProperty("content", message);
        json.addProperty("username", username);
        json.addProperty("avatar_url", avatarURL);
        sendMessageToDiscord(json);
    }
    public static void sendMessageToDiscord(JsonObject json) {
        try {
            // Create the connection
            URL url = new URL(getWebhookURL());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            // Send the JSON payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response
            int responseCode = connection.getResponseCode();
            if (responseCode != 204) {
                throw new RuntimeException("Failed to send message to Discord: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getWebhookURL() {
        return Main.config.getProperty("webhook_url");
    }
    public static String getWebhookToken() {
        return Main.config.getProperty("webhook_token");
    }
}
