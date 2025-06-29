package net.mat0u5.do2manager.command.validator;


import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;

public class CommandAnalyzer {

    // Configurable limits
    private static final int MAX_ENTITIES = 50;  // Max entities to affect before confirmation
    private static final int MAX_BLOCKS = 5000;  // Max blocks to modify before confirmation

    public static boolean shouldConfirm(String command, ServerCommandSource source) {
        String commandName = getCommandName(command);

        switch (commandName.toLowerCase()) {
            case "kill":
                return shouldConfirmKill(command, source);
            case "fill":
                return shouldConfirmFill(command, source);
            case "clone":
                return shouldConfirmClone(command, source);
            case "effect":
            case "tp":
            case "teleport":
            case "gamemode":
            case "give":
            case "clear":
                return shouldConfirmEntityCommand(command, source);
            case "scoreboard":
                return shouldConfirmScoreboard(command);
            default:
                return false;
        }
    }

    private static String getCommandName(String command) {
        String[] parts = command.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }

    private static boolean shouldConfirmKill(String command, ServerCommandSource source) {
        try {
            // Parse the target selector manually
            String[] parts = command.split("\\s+", 2);
            if (parts.length < 2) return false;

            String selector = parts[1];
            int estimatedCount = estimateEntityCount(selector, source);
            return estimatedCount > MAX_ENTITIES;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean shouldConfirmEntityCommand(String command, ServerCommandSource source) {
        try {
            // Find the entity selector in the command
            String[] parts = command.split("\\s+");
            for (String part : parts) {
                if (part.startsWith("@")) {
                    int estimatedCount = estimateEntityCount(part, source);
                    if (estimatedCount > MAX_ENTITIES) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    private static int estimateEntityCount(String selector, ServerCommandSource source) {
        if (selector.equals("@a")) {
            // Count all players
            return source.getServer().getPlayerManager().getPlayerList().size();
        } else if (selector.equals("@e")) {
            // Estimate all entities (this is tricky, we'll be conservative)
            return 999; // Assume high count to trigger confirmation
        } else if (selector.equals("@r")) {
            // Random player - just 1
            return 1;
        } else if (selector.equals("@s")) {
            // Self - just 1
            return 1;
        } else if (selector.equals("@p")) {
            // Nearest player - just 1
            return 1;
        } else if (selector.startsWith("@")) {
            // Complex selector - we can't easily determine count, so be conservative
            // Parse some basic parameters if possible
            if (selector.contains("limit=")) {
                try {
                    String limitStr = selector.substring(selector.indexOf("limit=") + 6);
                    limitStr = limitStr.split("[,\\]]")[0];
                    return Integer.parseInt(limitStr);
                } catch (Exception e) {
                    // If we can't parse limit, assume it could be many
                    return MAX_ENTITIES + 1;
                }
            }
            // No explicit limit, could be many entities
            return MAX_ENTITIES + 1;
        }
        // Not a selector, probably a player name
        return 1;
    }

    private static boolean shouldConfirmFill(String command, ServerCommandSource source) {
        String[] parts = command.split("\\s+");
        if (parts.length >= 7) {
            try {
                Vec3d playerPos = source.getPosition();

                Vec3d from = parseCoordinates(parts[1], parts[2], parts[3], playerPos);
                Vec3d to = parseCoordinates(parts[4], parts[5], parts[6], playerPos);

                int volume = calculateVolume(from, to);
                return volume > MAX_BLOCKS;
            } catch (Exception e) {
                // If we can't parse coordinates, assume it might be large
                return true;
            }
        }
        return false;
    }

    private static boolean shouldConfirmClone(String command, ServerCommandSource source) {
        String[] parts = command.split("\\s+");
        if (parts.length >= 7) {
            try {
                Vec3d playerPos = source.getPosition();

                Vec3d from = parseCoordinates(parts[1], parts[2], parts[3], playerPos);
                Vec3d to = parseCoordinates(parts[4], parts[5], parts[6], playerPos);

                int volume = calculateVolume(from, to);
                return volume > MAX_BLOCKS;
            } catch (Exception e) {
                // If we can't parse coordinates, assume it might be large
                return true;
            }
        }
        return false;
    }

    private static Vec3d parseCoordinates(String xStr, String yStr, String zStr, Vec3d playerPos) {
        double x = parseCoordinate(xStr, playerPos.x);
        double y = parseCoordinate(yStr, playerPos.y);
        double z = parseCoordinate(zStr, playerPos.z);
        return new Vec3d(x, y, z);
    }

    private static double parseCoordinate(String coord, double playerCoord) throws NumberFormatException {
        if (coord.startsWith("~")) {
            String relative = coord.substring(1);
            if (relative.isEmpty()) {
                return playerCoord;
            } else {
                return playerCoord + Double.parseDouble(relative);
            }
        } else if (coord.startsWith("^")) {
            // Local coordinates - too complex to parse without full context, assume 0 offset
            String local = coord.substring(1);
            return local.isEmpty() ? 0 : Double.parseDouble(local);
        } else {
            return Double.parseDouble(coord);
        }
    }

    private static boolean shouldConfirmScoreboard(String command) {
        // Only confirm scoreboard reset commands that affect all players or many objectives
        return command.toLowerCase().contains("reset") &&
                (command.contains("@a") || command.contains("*"));
    }

    private static int calculateVolume(Vec3d from, Vec3d to) {
        int width = (int) Math.abs(to.x - from.x) + 1;
        int height = (int) Math.abs(to.y - from.y) + 1;
        int depth = (int) Math.abs(to.z - from.z) + 1;
        return width * height * depth;
    }

    public static String generateWarning(String command, ServerCommandSource source) {
        String commandName = getCommandName(command);

        switch (commandName.toLowerCase()) {
            case "kill":
                return generateKillWarning(command, source);
            case "fill":
                return generateFillWarning(command, source);
            case "clone":
                return generateCloneWarning(command, source);
            case "effect":
            case "tp":
            case "teleport":
            case "gamemode":
            case "give":
            case "clear":
                return generateEntityWarning(command, source, commandName);
            case "scoreboard":
                return "This will reset scoreboard data permanently!";
            default:
                return "This command may have significant effects!";
        }
    }

    private static String generateKillWarning(String command, ServerCommandSource source) {
        try {
            String[] parts = command.split("\\s+", 2);
            if (parts.length < 2) return "This will kill entities!";

            String selector = parts[1];
            int estimatedCount = estimateEntityCount(selector, source);

            if (estimatedCount == 999) {
                return "This will kill ALL entities in the world!";
            } else {
                return String.format("This will kill approximately %d entities!", estimatedCount);
            }
        } catch (Exception e) {
            return "This will kill multiple entities!";
        }
    }

    private static String generateFillWarning(String command, ServerCommandSource source) {
        try {
            String[] parts = command.split("\\s+");
            if (parts.length >= 7) {
                Vec3d playerPos = source.getPosition();
                Vec3d from = parseCoordinates(parts[1], parts[2], parts[3], playerPos);
                Vec3d to = parseCoordinates(parts[4], parts[5], parts[6], playerPos);
                int volume = calculateVolume(from, to);
                return String.format("This will modify %,d blocks!", volume);
            }
        } catch (Exception e) {
            // Fall through to generic message
        }
        return "This will modify a large area of blocks!";
    }

    private static String generateCloneWarning(String command, ServerCommandSource source) {
        try {
            String[] parts = command.split("\\s+");
            if (parts.length >= 7) {
                Vec3d playerPos = source.getPosition();
                Vec3d from = parseCoordinates(parts[1], parts[2], parts[3], playerPos);
                Vec3d to = parseCoordinates(parts[4], parts[5], parts[6], playerPos);
                int volume = calculateVolume(from, to);
                return String.format("This will copy/move %,d blocks!", volume);
            }
        } catch (Exception e) {
            // Fall through to generic message
        }
        return "This will copy/move a large area of blocks!";
    }

    private static String generateEntityWarning(String command, ServerCommandSource source, String commandName) {
        try {
            // Find the largest entity selector in the command
            String[] parts = command.split("\\s+");
            int maxCount = 0;
            for (String part : parts) {
                if (part.startsWith("@")) {
                    int count = estimateEntityCount(part, source);
                    maxCount = Math.max(maxCount, count);
                }
            }

            if (maxCount == 999) {
                return String.format("This will %s ALL entities!", commandName.toLowerCase());
            } else if (maxCount > 0) {
                return String.format("This will %s approximately %d entities!", commandName.toLowerCase(), maxCount);
            }
        } catch (Exception e) {
            // Fall through to generic message
        }
        return String.format("This will %s multiple entities!", commandName.toLowerCase());
    }

    public static void sendConfirmationMessage(ServerPlayerEntity player, String command, ServerCommandSource source) {
        String warning = generateWarning(command, source);
        CommandValidator.addPendingCommand(player.getUuid(), command, warning);
        CommandValidator.PendingCommand pending = CommandValidator.getPendingCommand(player.getUuid());

        MutableText confirmText = Text.literal("[CONFIRM]")
                .formatted(Formatting.GREEN, Formatting.BOLD)
                .styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/confirmcmd " + pending.confirmId))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.literal("Click to execute the command"))));

        MutableText cancelText = Text.literal("[CANCEL]")
                .formatted(Formatting.RED, Formatting.BOLD)
                .styled(style -> style
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                "/confirmcmd cancel"))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                Text.literal("Click to cancel"))));

        MutableText message = Text.literal("⚠ DANGEROUS COMMAND WARNING ⚠")
                .formatted(Formatting.YELLOW, Formatting.BOLD)
                .append(Text.literal("\n" + warning).formatted(Formatting.WHITE))
                .append(Text.literal("\nCommand: ").formatted(Formatting.GRAY))
                .append(Text.literal(command).formatted(Formatting.YELLOW))
                .append(Text.literal("\n\n"))
                .append(confirmText)
                .append(Text.literal("  "))
                .append(cancelText);

        player.sendMessage(message, false);
    }
}
