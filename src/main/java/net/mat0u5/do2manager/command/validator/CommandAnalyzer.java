package net.mat0u5.do2manager.command.validator;

import com.mojang.brigadier.context.CommandContext;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.command.EntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class CommandAnalyzer {

    // Configurable limits
    private static final int MAX_PLAYERS = 1;
    private static final int MAX_LIVING_ENTITIES = 25;
    private static final int MAX_ENTITIES = 50;
    private static final int MAX_BLOCKS = 10_000;

    public static boolean shouldConfirm(String command, CommandContext<ServerCommandSource> context) {
        String commandName = getCommandName(command);
        ServerCommandSource source = context.getSource();

        switch (commandName.toLowerCase()) {
            case "kill":
                return entityConstraints("targets", context);
            case "fill":
                return shouldConfirmFill(command, source);
            case "clone":
                return shouldConfirmClone(command, source);
            case "effect":
            case "tp":
            case "teleport":
            case "give": //getPlayers
            case "clear": //getPlayers
                return entityConstraints("targets", context);
            case "gamemode": //getPlayers
                return entityConstraints("target", context);
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

    private static List<? extends Entity> getEntities(String argumentName, CommandContext<ServerCommandSource> context) {
        try {
            return (context.getArgument(argumentName, EntitySelector.class)).getEntities(context.getSource());
        }catch(Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error3:" + e.getMessage());
            OtherUtils.broadcastMessage(Text.of("error3: " + e.getMessage()));
        }
        return List.of();
    }

    private static boolean entityConstraints(String argumentName, CommandContext<ServerCommandSource> context) {
        try {
            List<? extends Entity> entities = getEntities(argumentName, context);

            int playerEntityCount = 0;
            int livingEntityCount = 0;
            int entityCount = entities.size();

            for (Entity entity : entities) {
                if (entity instanceof ServerPlayerEntity) {
                    playerEntityCount++;
                }
                if (entity instanceof LivingEntity) {
                    livingEntityCount++;
                }
            }

            OtherUtils.broadcastMessage(Text.of("Selector matched " + entityCount + " entities."));
            OtherUtils.broadcastMessage(Text.of("Selector matched " + livingEntityCount + " living entities."));
            OtherUtils.broadcastMessage(Text.of("Selector matched " + playerEntityCount + " players."));


            if (entityCount > MAX_ENTITIES) return true;
            if (livingEntityCount > MAX_LIVING_ENTITIES) return true;
            if (playerEntityCount > MAX_PLAYERS) return true;
            return false;
        } catch (Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error5:" + e.getMessage());
            OtherUtils.broadcastMessage(Text.of("error5: " + e.getMessage()));
            return true;
        }
    }

    private static boolean shouldConfirmFill(String command, ServerCommandSource source) {
        String[] parts = command.split("\\s+");
        if (parts.length >= 7) {
            try {
                Vec3d playerPos = source.getPosition();

                BlockPos from = parseBlockCoordinates(parts[1], parts[2], parts[3], playerPos);
                BlockPos to = parseBlockCoordinates(parts[4], parts[5], parts[6], playerPos);

                int volume = calculateBlockVolume(from, to);
                return volume > MAX_BLOCKS;
            } catch (Exception e) {
                Main.LOGGER.error("[CommandAnalyzer] error6:" + e.getMessage());
                OtherUtils.broadcastMessage(Text.of("error6: " + e.getMessage()));
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

                BlockPos from = parseBlockCoordinates(parts[1], parts[2], parts[3], playerPos);
                BlockPos to = parseBlockCoordinates(parts[4], parts[5], parts[6], playerPos);

                int volume = calculateBlockVolume(from, to);
                return volume > MAX_BLOCKS;
            } catch (Exception e) {
                Main.LOGGER.error("[CommandAnalyzer] error7:" + e.getMessage());
                OtherUtils.broadcastMessage(Text.of("error7: " + e.getMessage()));
                return true;
            }
        }
        return false;
    }

    private static BlockPos parseBlockCoordinates(String xStr, String yStr, String zStr, Vec3d playerPos) {
        int x = parseBlockCoordinate(xStr, (int)playerPos.x);
        int y = parseBlockCoordinate(yStr, (int)playerPos.y);
        int z = parseBlockCoordinate(zStr, (int)playerPos.z);
        return new BlockPos(x, y, z);
    }

    private static int parseBlockCoordinate(String coord, int playerCoord) throws NumberFormatException {
        if (coord.startsWith("~")) {
            String relative = coord.substring(1);
            if (relative.isEmpty()) {
                return playerCoord;
            } else {
                return playerCoord + Integer.parseInt(relative);
            }
        } else if (coord.startsWith("^")) {
            // Local coordinates - this is complex and would require full rotation context
            // For now, we'll treat it as relative to player position
            String local = coord.substring(1);
            return local.isEmpty() ? playerCoord : playerCoord + Integer.parseInt(local);
        } else {
            return Integer.parseInt(coord);
        }
    }

    private static boolean shouldConfirmScoreboard(String command) {
        return command.toLowerCase().contains("reset") && (command.contains("@a") || command.contains("*"));
    }

    private static int calculateBlockVolume(BlockPos from, BlockPos to) {
        int width = Math.abs(to.getX() - from.getX()) + 1;
        int height = Math.abs(to.getY() - from.getY()) + 1;
        int depth = Math.abs(to.getZ() - from.getZ()) + 1;
        return width * height * depth;
    }

    public static String generateWarning(String command, CommandContext<ServerCommandSource> context) {
        String commandName = getCommandName(command);

        switch (commandName.toLowerCase()) {
            case "kill":
                return generateKillWarning("targets", context);
            case "fill":
                return generateFillWarning(command, context);
            case "clone":
                return generateCloneWarning(command, context);
            case "effect":
            case "tp":
            case "teleport":
            case "give":
            case "clear":
                return generateEntityWarning("targets", context);
            case "gamemode":
                return generateEntityWarning("target", context);
            case "scoreboard":
                return "This will reset scoreboard data permanently!";
            default:
                return "This command may have significant effects!";
        }
    }

    private static String generateKillWarning(String argumentName, CommandContext<ServerCommandSource> context) {
        try {
            int actualCount = getEntities(argumentName, context).size();

            if (actualCount == 0) {
                return "No entities match this selector.";
            } else if (actualCount == 1) {
                return "This will kill 1 entity!";
            } else {
                return String.format("This will kill %d entities!", actualCount);
            }
        } catch (Exception e) {
            return "This will kill entities (count could not be determined)!";
        }
    }

    private static String generateFillWarning(String command, CommandContext<ServerCommandSource> context) {
        try {
            String[] parts = command.split("\\s+");
            if (parts.length >= 7) {
                Vec3d playerPos = context.getSource().getPosition();
                BlockPos from = parseBlockCoordinates(parts[1], parts[2], parts[3], playerPos);
                BlockPos to = parseBlockCoordinates(parts[4], parts[5], parts[6], playerPos);
                int volume = calculateBlockVolume(from, to);
                return String.format("This will modify %,d blocks!", volume);
            }
        } catch (Exception e) {
        }
        return "This will modify blocks (area could not be determined)!";
    }

    private static String generateCloneWarning(String command, CommandContext<ServerCommandSource> context) {
        try {
            String[] parts = command.split("\\s+");
            if (parts.length >= 7) {
                Vec3d playerPos = context.getSource().getPosition();
                BlockPos from = parseBlockCoordinates(parts[1], parts[2], parts[3], playerPos);
                BlockPos to = parseBlockCoordinates(parts[4], parts[5], parts[6], playerPos);
                int volume = calculateBlockVolume(from, to);
                return String.format("This will clone %,d blocks!", volume);
            }
        } catch (Exception e) {
        }
        return "This will clone blocks (area could not be determined)!";
    }

    private static String generateEntityWarning(String command, CommandContext<ServerCommandSource> context) {
        try {
            // Find the largest entity selector in the command
            String[] parts = command.split("\\s+");
            int maxCount = 0;
            for (String part : parts) {
                if (part.startsWith("@")) {
                    int count = getEntities(part, context).size();
                    maxCount = Math.max(maxCount, count);
                }
            }

            if (maxCount == 0) {
                return String.format("No entities match the command.");
            } else if (maxCount == 1) {
                return "This will affect 1 entity!";
            } else {
                return String.format("This will affect %d entities!", maxCount);
            }
        } catch (Exception e) {
            // Fall through to generic message
        }
        return "This will affect ? entities (count could not be determined)!";
    }

    public static void sendConfirmationMessage(ServerPlayerEntity player, String command, CommandContext<ServerCommandSource> context) {
        String warning = generateWarning(command, context);
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
