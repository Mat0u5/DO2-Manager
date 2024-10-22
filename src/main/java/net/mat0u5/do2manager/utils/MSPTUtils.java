package net.mat0u5.do2manager.utils;

import net.minecraft.server.MinecraftServer;

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
                double currentMSPT = getCurrentMSPT();

                // Adjust the workload based on current MSPT
                if (currentMSPT < DESIRED_MAX_MSPT) {
                    if (boosted) {
                        server.execute(this::complexFunction);
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
    public static double getCurrentMSPT() {
        long[] tickTimes = server.getTickTimes();

        // Calculate the average tick time
        long totalTickTime = 0;
        for (long tickTime : tickTimes) {
            totalTickTime += tickTime;
        }

        double averageTickTime = totalTickTime / (double) tickTimes.length;

        // Convert from nanoseconds to milliseconds
        double averageMSPT = averageTickTime / 1_000_000.0;

        return averageMSPT;
    }
}
