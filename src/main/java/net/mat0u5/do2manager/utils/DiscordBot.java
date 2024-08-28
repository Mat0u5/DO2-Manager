package net.mat0u5.do2manager.utils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiscordBot extends ListenerAdapter {
    private JDA jda;
    private String channelId;
    private String token;
    private boolean updateAfter = false;
    private String description;
    private static ExecutorService executor;

    public void startBot(String token, String channelId) {
        stopBot();
        this.channelId = channelId;
        this.token = token;
        jda = JDABuilder.createDefault(token)
                .addEventListeners(this)
                .build();
        executor = Executors.newSingleThreadExecutor();
    }
    public void startBot(String token, String channelId, boolean updateAfter, String description) {
        stopBot();
        this.channelId = channelId;
        this.token = token;
        this.updateAfter = updateAfter;
        this.description = description;
        jda = JDABuilder.createDefault(token)
                .addEventListeners(this)
                .build();
        executor = Executors.newSingleThreadExecutor();
    }

    // Check if executor is shutdown or terminated before submitting a task
    public void updateChannelDescription(String newDescription) {
        // Check if the executor is already shutdown before attempting to submit the task
        if (executor.isShutdown() || executor.isTerminated()) {
            System.err.println("Executor is shut down. Cannot update channel description.");
            return;
        }

        executor.submit(() -> {
            if (channelId == null || channelId.isEmpty()) {
                stopBot(); // Shut down resources if no valid channel ID is provided
                return;
            }

            // Fetch the TextChannel by ID
            TextChannel channel = jda.getTextChannelById(channelId);
            if (channel != null) {
                // Update the channel topic and shut down after completion
                channel.getManager().setTopic(newDescription).queue(
                        success -> stopBot(),  // On success, shut down JDA and executor
                        error -> stopBot()     // On error, still shut down JDA and executor
                );
            } else {
                stopBot(); // Shut down resources if the channel is not found
            }
        });
    }

    public void stopBot() {
        if (jda != null) {
            jda.shutdownNow(); // Immediately shutdown JDA to free resources
            jda = null; // Set to null to prevent further interaction
        }
        shutdownExecutor();
    }

    private void shutdownExecutor() {
        // Allow current tasks to complete before shutting down the executor
        if (executor == null) return;
        if (executor.isShutdown()) return;
        try {
            executor.shutdown(); // Gracefully shutdown
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) { // Wait for tasks to finish
                executor.shutdownNow(); // Force shutdown if tasks don't finish in time
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }


    @Override
    public void onReady(ReadyEvent event) {
        if (updateAfter) updateChannelDescription(description);
    }
}
