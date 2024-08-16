package net.mat0u5.do2manager.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class DiscordBot extends ListenerAdapter {
    private JDA jda;
    private String channelId;
    private String token;

    public void startBot(String token, String channelId) {
        this.channelId = channelId;
        this.token = token;
    }

    public void updateChannelDescription(String newDescription) {
        /*new Thread(() -> {
            jda = JDABuilder.createDefault(token)
                    .addEventListeners(this)
                    .build();
            System.out.println("TEST1");
            if (channelId.isEmpty()) return;
            System.out.println("TEST2");
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                System.out.println("TEST3");
                channel.getManager().setTopic(newDescription).queue();
                System.out.println("TEST4");
            }
        }).start();*/
    }

    @Override
    public void onReady(ReadyEvent event) {
        System.out.println("Bot is ready!");
    }
}
