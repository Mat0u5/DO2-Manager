package net.mat0u5.do2manager.queue;

import net.mat0u5.do2manager.Main;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.UUID;

public class QueueEvents {
    private static HashMap<UUID, Long> disconnectTimes = new HashMap<>();
    private static final int MAX_LOGOUT_TIME_BEFORE_QUEUE_LEAVE_MILIS = 2 * 60 * 1000;// 2 mins

    public static void onPlayerJoin(ServerPlayerEntity player) {
        UUID playerUuid = player.getUuid();
        if (disconnectTimes.containsKey(playerUuid)) {
            long disconnectTime = disconnectTimes.get(playerUuid);
            if (System.currentTimeMillis() - disconnectTime > MAX_LOGOUT_TIME_BEFORE_QUEUE_LEAVE_MILIS) {
                Main.dungeonQueue.removeFromQueue(playerUuid);
            }
            disconnectTimes.remove(playerUuid);
        }
    }
    public static void onPlayerLeave(ServerPlayerEntity player) {
        UUID playerUuid = player.getUuid();
        if (Main.dungeonQueue.getNextPlayer().equals(playerUuid)) {
            Main.dungeonQueue.removeFromQueue(playerUuid);
        }
        disconnectTimes.put(playerUuid, System.currentTimeMillis());
    }
}
