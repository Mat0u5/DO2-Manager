package net.mat0u5.do2manager.events;


import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.command.RestartCommand;
import net.mat0u5.do2manager.queue.QueueEvents;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.block.CommandBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Events {

    private static boolean isServerShuttingDown = false;
    public static long clickEventCooldown = 0;
    public static long lastPlayerLogoutTime = -1;
    public static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public static long discordDescriptionUpdate = 0;

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register(Events::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPING.register(Events::onServerStopping);
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> PlayerEvents.onPlayerJoin(server, handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (!isServerShuttingDown) {
                scheduler.schedule(() -> PlayerEvents.onPlayerDisconnect(server, handler.getPlayer()), 1, TimeUnit.SECONDS);
            }
        });
        ServerTickEvents.END_SERVER_TICK.register(Events::onServerTickEnd);

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, amount) -> {
            if (entity instanceof ServerPlayerEntity) {
                PlayerEvents.onPlayerDeath((ServerPlayerEntity) entity, damageSource);
            }
            return true;
        });
        UseBlockCallback.EVENT.register(PlayerEvents::onBlockUse);


        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!world.isClient) {
                if (state.getBlock() instanceof CommandBlock) {
                    CommandBlockEvents.onCommandBlockBroken(player, pos, state.getBlock());
                }
            }
        });
    }

    // Method to handle the server stopping event
    private static void onServerStopping(MinecraftServer server) {
        System.out.println("[DO2-Manager] - Detected Server Shutdown");
        isServerShuttingDown = true;
        Main.saveRunInfoToConfig();
    }
    private static void onServerTickEnd(MinecraftServer server) {
        try {
            QueueEvents.onTickEnd();
            if (lastPlayerLogoutTime != -1 && RestartCommand.isRestartQueued() && OtherUtils.isServerEmptyOrOnlyTangoCam(server)) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastPlayerLogoutTime >= TimeUnit.MINUTES.toMillis(3)) {
                    OtherUtils.restartServer(server);
                }
            }
            if (clickEventCooldown > 0) clickEventCooldown--;
            discordDescriptionUpdate++;
            if (discordDescriptionUpdate >=12500) {
                discordDescriptionUpdate=0;
                new DiscordUtils().updateDiscordChannelDescription();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void onServerStart(MinecraftServer server) {
        Main.server = server;
        System.out.println("MinecraftServer instance captured.");
    }
}
