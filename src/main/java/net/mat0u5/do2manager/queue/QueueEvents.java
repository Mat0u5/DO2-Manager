package net.mat0u5.do2manager.queue;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import java.util.HashMap;

public class QueueEvents {
    public static HashMap<String, Integer> disconnectTimes = new HashMap<>();
    private static final int MAX_LOGOUT_TIME_BEFORE_QUEUE_LEAVE = 150;// 2.5 mins
    private static int checkDisconnectTimes = 20;

    public static void onPlayerJoin(ServerPlayer player) {
        String playerName = player.getScoreboardName();
        if (disconnectTimes.containsKey(playerName)) {
            disconnectTimes.remove(playerName);
        }
        if (Main.dungeonQueue.containsPlayer(player)) {
            player.sendSystemMessage(Component.nullToEmpty("§7You're currently still in the queue!"));
            Main.dungeonQueue.messageQueueToPlayer(player);
        }
        else {
            Component baseMessage = Component.literal("§7Click ");
            Component clickableHere = Component.literal("here")
                    .withStyle(style -> style
                            .withColor(ChatFormatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/queue join"))
                            .withUnderlined(true)
                    );
            Component fullMessage = ((MutableComponent) baseMessage)
                    .append(clickableHere)
                    .append(Component.literal("§7 §7(or use the §b/queue§7 command)§7 to join the dungeon queue!")
                            .withStyle(ChatFormatting.YELLOW)
                    );
            player.displayClientMessage(fullMessage, false);
        }
    }
    public static void onPlayerLeave(ServerPlayer player) {
        String playerName = player.getScoreboardName();
        if (Main.dungeonQueue.getNextPlayer().equalsIgnoreCase(playerName)) {
            Main.dungeonQueue.removeFromDisconnect(playerName);
        }
        else {
            disconnectTimes.put(playerName, MAX_LOGOUT_TIME_BEFORE_QUEUE_LEAVE);
        }
    }
    public static void onTickEnd() {
        checkDisconnectTimes--;
        if (checkDisconnectTimes > 0) return;
        if (disconnectTimes.isEmpty()) return;
        checkDisconnectTimes = 20;
        for (String playerName : disconnectTimes.keySet()) {
            int timeLeft = disconnectTimes.get(playerName);
            if (timeLeft <= 0) {
                Main.dungeonQueue.removeFromOffline(playerName);
                if (!OtherUtils.isPlayerOnline(playerName)) {
                    disconnectTimes.remove(playerName);
                }
            }
            else {
                disconnectTimes.replace(playerName,timeLeft-1);
            }
        }
    }
}
