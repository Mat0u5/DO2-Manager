package net.mat0u5.do2manager.queue;

import java.util.LinkedList;
import java.util.UUID;

public class DungeonQueue {
    private final LinkedList<UUID> queue = new LinkedList<>();

    public void addToQueue(UUID playerUuid) {
        if (containsPlayer(playerUuid)) {
            return;
        }
        queue.add(playerUuid);
        queueUpdated();
    }

    public void removeFromQueue(UUID playerUuid) {
        if (!containsPlayer(playerUuid)) {
            return;
        }
        queue.remove(playerUuid);
        queueUpdated();
    }
    public boolean containsPlayer(UUID playerUuid) {
        return queue.contains(playerUuid);
    }

    public void moveQueue() {
        if (queue.isEmpty()) {
            return;
        }

        UUID playerUuid = queue.poll();  // Remove the first player
        queue.add(playerUuid);  // Add them to the end of the queue
        queueUpdated();
    }

    public UUID getNextPlayer() {
        return queue.peek();  // Returns the next player without removing them
    }

    public LinkedList<UUID> getQueue() {
        return queue;
    }
    public void queueUpdated() {

    }

}
