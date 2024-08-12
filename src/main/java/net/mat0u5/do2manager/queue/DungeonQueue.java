package net.mat0u5.do2manager.queue;

import java.util.LinkedList;
import java.util.UUID;

public class DungeonQueue {
    private final LinkedList<UUID> queue = new LinkedList<>();

    public void addToQueue(UUID playerUuid) {
        if (!queue.contains(playerUuid)) {
            queue.add(playerUuid);
        }
    }

    public void removeFromQueue(UUID playerUuid) {
        queue.remove(playerUuid);
    }

    public void moveQueue() {
        if (!queue.isEmpty()) {
            UUID playerUuid = queue.poll();  // Remove the first player
            queue.add(playerUuid);  // Add them to the end of the queue
        }
    }

    public UUID getNextPlayer() {
        return queue.peek();  // Returns the next player without removing them
    }

    public LinkedList<UUID> getQueue() {
        return queue;
    }

}
