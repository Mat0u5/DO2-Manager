package net.mat0u5.do2manager.command.validator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class CommandValidator {

    private static final Map<UUID, PendingCommand> pendingCommands = new ConcurrentHashMap<>();

    public static void addPendingCommand(UUID playerId, String command, String warning) {
        String confirmId = UUID.randomUUID().toString().substring(0, 8);
        pendingCommands.put(playerId, new PendingCommand(command, warning, confirmId, System.currentTimeMillis()));

        long cutoff = System.currentTimeMillis() - 300000; // 5 minutes
        pendingCommands.entrySet().removeIf(entry -> entry.getValue().timestamp < cutoff);
    }

    public static PendingCommand getPendingCommand(UUID playerId) {
        return pendingCommands.get(playerId);
    }

    public static void removePendingCommand(UUID playerId) {
        pendingCommands.remove(playerId);
    }

    public static class PendingCommand {
        public final String command;
        public final String warning;
        public final String confirmId;
        public final long timestamp;

        public PendingCommand(String command, String warning, String confirmId, long timestamp) {
            this.command = command;
            this.warning = warning;
            this.confirmId = confirmId;
            this.timestamp = timestamp;
        }
    }
}
