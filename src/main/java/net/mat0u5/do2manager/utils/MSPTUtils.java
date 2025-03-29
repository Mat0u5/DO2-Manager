package net.mat0u5.do2manager.utils;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class MSPTUtils {
    private static final double DESIRED_MAX_MSPT = 45;
    private MinecraftServer server;

    public void onTick() {
    }
    protected abstract void complexFunction();
    protected abstract void stoppedFunction();

}
