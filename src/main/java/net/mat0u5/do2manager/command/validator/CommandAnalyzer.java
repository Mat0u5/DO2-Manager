package net.mat0u5.do2manager.command.validator;

import com.mojang.brigadier.context.CommandContext;
import net.mat0u5.do2manager.Main;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.argument.ScoreHolderArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;
import java.util.List;

public class CommandAnalyzer {

    // Configurable limits
    private static final int MAX_PLAYERS = 1;
    private static final int MAX_LIVING_ENTITIES = 25;
    private static final int MAX_ENTITIES = 50;
    private static final int MAX_SCOREBOARD_MODIFICATIONS = 50;
    private static final int MAX_BLOCKS = 10_000;

    public static boolean shouldConfirm(String command, CommandContext<ServerCommandSource> context) {
        String commandName = getCommandName(command);
        ServerCommandSource source = context.getSource();

        switch (commandName.toLowerCase()) {
            case "fill":
                return shouldConfirmFill(command, source);
            case "clone":
                return shouldConfirmClone(command, source);
            case "execute":
            case "kill":
            case "effect":
            case "tp":
            case "teleport":
            case "give":
            case "clear":
            case "gamemode":
                return entityConstraints(List.of("target", "targets", "entity", "entities"), context);
            case "scoreboard":
                return shouldConfirmScoreboard(command, "targets", context);
            default:
                return false;
        }
    }

    private static String getCommandName(String command) {
        String[] parts = command.trim().split("\\s+");
        return parts.length > 0 ? parts[0] : "";
    }

    private static Collection<? extends Entity> getEntities(String argumentName, CommandContext<ServerCommandSource> context) {
        return getEntities(List.of(argumentName), context);
    }
    private static Collection<? extends Entity> getEntities(List<String> arguments, CommandContext<ServerCommandSource> context) {
        try {
            for (String argumentName : arguments) {
                try {
                    Collection<? extends Entity> entities = (context.getArgument(argumentName, EntitySelector.class)).getEntities(context.getSource());
                    if (!entities.isEmpty()) return entities;
                }catch(IllegalArgumentException e) {}

                try {
                    Collection<? extends Entity> players = (context.getArgument(argumentName, EntitySelector.class)).getPlayers(context.getSource());
                    if (!players.isEmpty()) return players;
                }catch(IllegalArgumentException e) {}
            }
        }catch(Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error3:" + e.getMessage());
        }
        return List.of();
    }

    private static Collection<ScoreHolder> getScoreHolders(String argumentName, CommandContext<ServerCommandSource> context) {
        try {
            try {
                return ScoreHolderArgumentType.getScoreboardScoreHolders(context, argumentName);
            }catch(IllegalArgumentException e) {}
        }catch(Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error4:" + e.getMessage());
        }
        return List.of();
    }

    private static boolean entityConstraints(List<String> arguments, CommandContext<ServerCommandSource> context) {
        try {
            Collection<? extends Entity> entities = getEntities(arguments, context);

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

            if (entityCount > MAX_ENTITIES) return true;
            if (livingEntityCount > MAX_LIVING_ENTITIES) return true;
            if (playerEntityCount > MAX_PLAYERS) return true;
            return false;
        } catch (Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error5:" + e.getMessage());
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

    private static boolean shouldConfirmScoreboard(String command, String argumentName, CommandContext<ServerCommandSource> context) {
        if (command.toLowerCase().contains("scoreboard players reset ")) {
            try {
                ScoreboardObjective test = ScoreboardObjectiveArgumentType.getObjective(context, "objective");
            } catch(Exception e) {
                return true;
            }
        }
        return getScoreHolders(argumentName, context).size() > MAX_SCOREBOARD_MODIFICATIONS;
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
            case "execute":
            case "effect":
            case "tp":
            case "teleport":
            case "give":
            case "clear":
            case "gamemode":
                return generateEntityWarning(List.of("target", "targets", "entity", "entities"), context);
            case "scoreboard":
                return generateScoreboardWarning(command, "targets", context);
            default:
                return "This command may have significant effects!";
        }
    }

    private static String generateKillWarning(String argumentName, CommandContext<ServerCommandSource> context) {
        try {
            int actualCount = getEntities(argumentName, context).size();

            return String.format("This will kill %d entities!", actualCount);
        } catch (Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error8:" + e.getMessage());
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
            Main.LOGGER.error("[CommandAnalyzer] error9:" + e.getMessage());
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
            Main.LOGGER.error("[CommandAnalyzer] error10:" + e.getMessage());
        }
        return "This will clone blocks (area could not be determined)!";
    }

    private static String generateEntityWarning(List<String> arguments, CommandContext<ServerCommandSource> context) {
        try {
            int actualCount = getEntities(arguments, context).size();
            return String.format("This will affect %d entities!", actualCount);
        } catch (Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error11:" + e.getMessage());
        }
        return "This will affect ? entities (count could not be determined)!";
    }

    private static String generateScoreboardWarning(String command, String argumentName, CommandContext<ServerCommandSource> context) {
        try {
            if (command.toLowerCase().contains("scoreboard players reset ")) {
                try {
                    ScoreboardObjective test = ScoreboardObjectiveArgumentType.getObjective(context, "objective");
                } catch(Exception e) {
                    return String.format("No objective specified, this will affect all scores!");
                }
            }
            int actualCount = getScoreHolders(argumentName, context).size();
            return String.format("This will affect %d score holders!", actualCount);
        } catch (Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error12:" + e.getMessage());
        }
        return "This will affect ? score holders (count could not be determined)!";
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
