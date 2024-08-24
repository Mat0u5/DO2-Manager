package net.mat0u5.do2manager.queue;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

public class DungeonQueue {
    private final LinkedList<String> queue = new LinkedList<>();

    public void addToQueue(PlayerEntity player, boolean forced) {
        String playerName = player.getEntityName();
        if (containsPlayer(player)) {
            return;
        }
        queue.add(playerName);
        String msg = "§b"+playerName + "§7 has joined the queue!";
        if (forced) msg = "§b"+playerName + "§7 has been added to the queue!";
        queueUpdated(msg);
    }
    public void putAtEnd(PlayerEntity player) {
        putAtEnd(player,true);
    }
    public void putAtEnd(PlayerEntity player, boolean sendFeedback) {
        String playerName = player.getEntityName();
        if (!containsPlayer(player)) {
            return;
        }
        queue.remove(playerName);
        queue.add(playerName);
        if (sendFeedback) queueUpdated("§b"+playerName + "§7 has finished a run!");
    }
    public void putAtEnd(Collection<? extends ServerPlayerEntity> players) {
        if (queue.isEmpty()) return;
        List<String> playersList = new ArrayList<>();
        for (ServerPlayerEntity player : players) {
            putAtEnd(player,false);
            playersList.add(player.getEntityName());
        }
        queueUpdated("§b"+ String.join(", ", playersList)+ "§7 "+(playersList.size()>1?"have":"has")+" finished a run!");
    }
    public void skipTurns(PlayerEntity player, int turnsNum, boolean forced) {
        String playerName = player.getEntityName();
        if (!containsPlayer(player)) {
            return;
        }
        int index = queue.indexOf(playerName)+turnsNum;
        queue.remove(playerName);
        index = Math.min(queue.size(),index);
        queue.add(index,playerName);
        String msg = "§b"+playerName + "§7 has skipped " + turnsNum + " of their turns!";
        if (forced) msg = "§b"+playerName + "§7's turn has been skipped!";
        queueUpdated(msg);
    }

    public void removeFromQueue(PlayerEntity player) {
        String playerName = player.getEntityName();
        if (!containsPlayer(player)) {
            return;
        }
        queue.remove(playerName);
        queueUpdated("§b"+playerName + "§7 has left the queue!");
    }
    public void removeFromQueueStr(String playerName) {
        if (!queue.contains(playerName)) {
            return;
        }
        queue.remove(playerName);
        queueUpdated("§b"+playerName + "§7 has been removed the queue!");
    }
    public void removeFromOffline(String playerName) {
        if (!queue.contains(playerName)) {
            return;
        }
        queue.remove(playerName);
        queueUpdated("§b"+playerName + "§7 has been removed from the queue, because they have been offline for 2.5 minutes!");
    }
    public boolean queueHasOnlinePlayer() {
        for (String playerName : queue) {
            if (OtherUtils.isPlayerOnline(playerName)) {
                return true;
            }
        }
        return false;
    }
    public void removeFromDisconnect(String playerName) {
        skipOfflinePlayer(playerName);
    }
    public void skipOfflinePlayer(String playerName) {
        if (!queue.contains(playerName)) {
            return;
        }
        if (!QueueEvents.disconnectTimes.containsKey(playerName)) {
            removeFromOffline(playerName);
            return;
        }
        if (!queueHasOnlinePlayer()) {
            return;
        }
        int index = queue.indexOf(playerName);
        queue.remove(playerName);
        int pos = 1;
        while(!OtherUtils.isPlayerOnline(queue.get(pos-1)) && pos < 10) {
            pos++;
        }
        index += pos;
        index = Math.min(queue.size(),index);
        queue.add(index,playerName);
        queueUpdated("§b"+playerName+"§7's turn has been skipped because are offline and it's their turn.");
    }
    public boolean containsPlayer(PlayerEntity player) {
        String playerName = player.getEntityName();
        return queue.contains(playerName);
    }

    public void moveQueue() {
        if (queue.isEmpty()) {
            return;
        }

        String playerName = queue.poll();  // Remove the first player
        queue.add(playerName);  // Add them to the end of the queue
        queueUpdated("§7The queue has been manually moved.");
    }

    public String getNextPlayer() {
        return queue.peek();  // Returns the next player without removing them
    }

    public LinkedList<String> getQueue() {
        return queue;
    }
    public void queueUpdated(String updateMessage) {
        OtherUtils.broadcastMessage(Text.of(updateMessage));
        if (queue.isEmpty()) return;
        String firstPlayer = queue.getFirst();
        if (!OtherUtils.isPlayerOnline(firstPlayer)) {
            skipOfflinePlayer(firstPlayer);
        }
        messageQueueToPlayers();
    }
    public void messageQueueToPlayers() {
        OtherUtils.broadcastMessage(getQueueListed());
    }
    public void messageQueueToPlayer(PlayerEntity player) {
        player.sendMessage(getQueueListed());
    }
    public Text getQueueListed() {
        if (queue.isEmpty()) {
            return Text.of("§cThe queue is currently empty.");
        }
        Text result = Text.of("§7Current Queue Order: §b"+ String.join("§7, §b",queue)+"\n§7 -> §b"+queue.getFirst()+"§7 is the next in queue!");
        return result;
    }
    public String getQueueAsString() {
        return String.join(",",queue);
    }
    public void getQueueFromString(String queueStr) {
        if (queueStr.isEmpty()) return;
        if (queueStr.contains(",")) {
            for (String playerName : queueStr.split(",")) {
                queue.add(playerName);
                QueueEvents.disconnectTimes.put(playerName, 150);
            }
        }
        else {
            queue.add(queueStr);
            QueueEvents.disconnectTimes.put(queueStr, 150);
        }
    }
    public void loadQueueFromConfig() {
        if (Main.config.getProperty("current_queue") == null) return;
        if (Main.config.getProperty("current_queue").isEmpty()) return;
        getQueueFromString(Main.config.getProperty("current_queue"));
    }

}
