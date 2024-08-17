package net.mat0u5.do2manager.queue;

import net.mat0u5.do2manager.Main;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;

import java.util.HashMap;

public class QueueEvents {
    private static HashMap<String, Integer> disconnectTimes = new HashMap<>();
    private static final int MAX_LOGOUT_TIME_BEFORE_QUEUE_LEAVE = 300;// 5 mins
    private static int checkDisconnectTimes = 20;

    public static void onPlayerJoin(ServerPlayerEntity player) {
        String playerName = player.getEntityName();
        if (disconnectTimes.containsKey(playerName)) {
            disconnectTimes.remove(playerName);
        }
        if (Main.dungeonQueue.containsPlayer(player)) {
            player.sendMessage(Text.of("§3You're currently still in the queue!"));
            Main.dungeonQueue.messageQueueToPlayer(player);
        }
        else {
            Text baseMessage = Text.literal("§3Click ");
            Text clickableHere = Text.literal("here")
                    .styled(style -> style
                            .withColor(Formatting.GREEN)
                            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/queue join"))
                            .withUnderline(true)
                    );
            Text fullMessage = ((MutableText) baseMessage)
                    .append(clickableHere)
                    .append(Text.literal("§3 §b(or use the §3/queue§b command)§3 to join the dungeon queue!")
                            .formatted(Formatting.YELLOW)
                    );
            player.sendMessage(fullMessage, false);
        }
    }
    public static void onPlayerLeave(ServerPlayerEntity player) {
        String playerName = player.getEntityName();
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
        checkDisconnectTimes = 20;
        for (String playerName : disconnectTimes.keySet()) {
            int timeLeft = disconnectTimes.get(playerName);
            if (timeLeft <= 0) {
                Main.dungeonQueue.removeFromOffline(playerName);
                disconnectTimes.remove(playerName);
            }
            else {
                disconnectTimes.replace(playerName,timeLeft-1);
            }
        }
    }
}
