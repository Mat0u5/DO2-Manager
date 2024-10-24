package net.mat0u5.do2manager.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mat0u5.do2manager.Main;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.server.network.ServerPlayerEntity;

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
        sendMessageToDiscord(json, getWebhookURL());
    }
    public static void sendMessageToDiscord(JsonObject json, String webhook) {
        try {
            // Create the connection
            URL url = new URL(webhook);
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
    public static void sendMessageToDiscordFromAggroNet(JsonObject json, String channelId) {
        try {
            // Discord API URL for sending messages
            String apiUrl = "https://discord.com/api/v10/channels/" + channelId + "/messages";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");

            // Set authorization header with the bot token
            connection.setRequestProperty("Authorization", "Bot " + getWebhookToken());
            connection.setRequestProperty("Content-Type", "application/json");

            // Send the JSON payload
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response
            int responseCode = connection.getResponseCode();
            if (responseCode != 200 && responseCode != 201) {
                throw new RuntimeException("Failed to send message to Discord: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getWebhookURL() {
        return Main.config.getProperty("webhook_url");
    }
    public static String getWebhookStaffURL() {
        return Main.config.getProperty("webhook_url_staff");
    }
    public static String getWebhookPatchNotesURL() {
        return Main.config.getProperty("webhook_url_patch_notes");
    }
    public static String getWebhookToken() {
        return Main.config.getProperty("webhook_token");
    }
    public static String getChatChannelId() {
        return Main.config.getProperty("server_chat_channel_id");
    }

    public void updateDiscordChannelDescription() {
        List<ServerPlayerEntity> players = Main.server.getPlayerManager().getPlayerList();
        List<String> playerNames = new ArrayList<>();
        for (ServerPlayerEntity player : players) {
            playerNames.add(player.getNameForScoreboard());
        }
        if (playerNames.contains("TangoCam")) playerNames.remove("TangoCam");
        String description = "Players online (" + playerNames.size() + "): " + String.join(", ",playerNames);
        DiscordBot discordBot = new DiscordBot();
        discordBot.startBot(getWebhookToken(), getChatChannelId(),true,description);
    }
    public static void sendChangeInfo(String from, String change, String reason, String affected) {
        long timestamp = System.currentTimeMillis() / 1000;
        JsonObject json = DiscordUtils.getDefaultJSON();
        JsonObject embed = new JsonObject();
        embed.addProperty("description", "__**New Patch:**__   *(From "+from+")*" +
                "\n\n**Change**: "+change+
                (reason.isEmpty()?"":("\n**Reason**: "+reason)) +
                (affected.isEmpty()?"":("\n**Affected Gameplay**: "+affected))+
                "\n\n**Date**: <t:"+timestamp+":f>"
        );
        embed.addProperty("color", 5419198);
        JsonArray embeds = new JsonArray();
        embeds.add(embed);
        json.add("embeds", embeds);
        json.addProperty("username", from);
        json.addProperty("avatar_url", "https://mc-heads.net/avatar/"+from);
        sendMessageToDiscord(json,getWebhookPatchNotesURL());
    }
}
