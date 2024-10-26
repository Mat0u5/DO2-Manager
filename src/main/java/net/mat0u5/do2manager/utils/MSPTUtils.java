package net.mat0u5.do2manager.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class MSPTUtils {
    private static final double DESIRED_MAX_MSPT = 45;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static MinecraftServer server;
    public boolean running = true;

    public void startBoosted(MinecraftServer server) {
        this.server = server;
        executorService.submit(() -> runComplexFunction(true));
    }
    public void start(MinecraftServer server) {
        this.server = server;
        executorService.submit(() -> runComplexFunction(false));
    }
    private void runComplexFunction(boolean boosted) {
        while (running) {
            try {
                // Check the server's MSPT
                float currentMSPT = server.getAverageTickTime();

                // Adjust the workload based on current MSPT
                if (currentMSPT < DESIRED_MAX_MSPT) {
                    if (boosted) {
                        server.execute(this::complexFunction);
                        waitForNextServerTicks(20);
                    }
                    else {
                        complexFunction();
                    }
                } else {
                    long sleep = (long) Math.max(1,(currentMSPT-45));
                    Thread.sleep(sleep);
                }
            } catch (InterruptedException e) {
                System.out.println("Interrupting thread");
                Thread.currentThread().interrupt();
            }
        }
    }
    protected abstract void complexFunction();
    protected abstract void stoppedFunction();

    public void stop() {
        running = false;
        executorService.shutdown();
        stoppedFunction();
    }
    public static void waitForNextServerTicks(int ticks) {
        CountDownLatch latch = new CountDownLatch(ticks);

        // Register a callback to be executed at the end of the next server tick
        ServerTickEvents.END_SERVER_TICK.register(minecraftServer -> {
            if (minecraftServer == server) {
                // Decrease the count of the latch, allowing the waiting thread to proceed
                latch.countDown();
            }
        });

        try {
            // Wait for the latch to be counted down, effectively blocking this thread
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
