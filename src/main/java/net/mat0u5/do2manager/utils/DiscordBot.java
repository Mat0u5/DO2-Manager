package net.mat0u5.do2manager.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Timer;

public class DiscordBot extends ListenerAdapter {
    private JDA jda;
    private String channelId;
    private String token;
    private boolean updateAfter = false;
    private String description;

    public void startBot(String token, String channelId) {
        this.channelId = channelId;
        this.token = token;
        jda = JDABuilder.createDefault(token)
                .addEventListeners(this)
                .build();
    }
    public void startBot(String token, String channelId, boolean updateAfter, String description) {
        this.channelId = channelId;
        this.token = token;
        this.updateAfter = updateAfter;
        this.description = description;
        jda = JDABuilder.createDefault(token)
                .addEventListeners(this)
                .build();
    }

    public void updateChannelDescription(String newDescription) {
        new Thread(() -> {
            if (channelId.isEmpty()) return;
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                channel.getManager().setTopic(newDescription).queue();
            }
        }).start();
    }

    @Override
    public void onReady(ReadyEvent event) {
        if (updateAfter) updateChannelDescription(description);
    }
}
