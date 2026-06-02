package net.mat0u5.do2manager.command.validator;

import com.mojang.brigadier.context.CommandContext;
import net.mat0u5.do2manager.Main;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.ScoreHolder;
import java.util.Collection;
import java.util.List;

public class CommandAnalyzer {

    // Configurable limits
    private static final int MAX_PLAYERS = 1;
    private static final int MAX_LIVING_ENTITIES = 25;
    private static final int MAX_ENTITIES = 50;
    private static final int MAX_SCOREBOARD_MODIFICATIONS = 50;
    private static final int MAX_BLOCKS = 10_000;

    public static boolean shouldConfirm(String command, CommandContext<CommandSourceStack> context) {
        String commandName = getCommandName(command);
        CommandSourceStack source = context.getSource();

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

    private static Collection<? extends Entity> getEntities(String argumentName, CommandContext<CommandSourceStack> context) {
        return getEntities(List.of(argumentName), context);
    }
    private static Collection<? extends Entity> getEntities(List<String> arguments, CommandContext<CommandSourceStack> context) {
        try {
            for (String argumentName : arguments) {
                try {
                    Collection<? extends Entity> entities = (context.getArgument(argumentName, EntitySelector.class)).findEntities(context.getSource());
                    if (!entities.isEmpty()) return entities;
                }catch(IllegalArgumentException e) {}

                try {
                    Collection<? extends Entity> players = (context.getArgument(argumentName, EntitySelector.class)).findPlayers(context.getSource());
                    if (!players.isEmpty()) return players;
                }catch(IllegalArgumentException e) {}
            }
        }catch(Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error3:" + e.getMessage());
        }
        return List.of();
    }

    private static Collection<ScoreHolder> getScoreHolders(String argumentName, CommandContext<CommandSourceStack> context) {
        try {
            try {
                return ScoreHolderArgument.getNamesWithDefaultWildcard(context, argumentName);
            }catch(IllegalArgumentException e) {}
        }catch(Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error4:" + e.getMessage());
        }
        return List.of();
    }

    private static boolean entityConstraints(List<String> arguments, CommandContext<CommandSourceStack> context) {
        try {
            Collection<? extends Entity> entities = getEntities(arguments, context);

            int playerEntityCount = 0;
            int livingEntityCount = 0;
            int entityCount = entities.size();

            for (Entity entity : entities) {
                if (entity instanceof ServerPlayer) {
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

    private static boolean shouldConfirmFill(String command, CommandSourceStack source) {
        String[] parts = command.split("\\s+");
        if (parts.length >= 7) {
            try {
                Vec3 playerPos = source.getPosition();

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

    private static boolean shouldConfirmClone(String command, CommandSourceStack source) {
        String[] parts = command.split("\\s+");
        if (parts.length >= 7) {
            try {
                Vec3 playerPos = source.getPosition();

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

    private static BlockPos parseBlockCoordinates(String xStr, String yStr, String zStr, Vec3 playerPos) {
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

    private static boolean shouldConfirmScoreboard(String command, String argumentName, CommandContext<CommandSourceStack> context) {
        if (command.toLowerCase().contains("scoreboard players reset ")) {
            try {
                Objective test = ObjectiveArgument.getObjective(context, "objective");
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

    public static String generateWarning(String command, CommandContext<CommandSourceStack> context) {
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

    private static String generateKillWarning(String argumentName, CommandContext<CommandSourceStack> context) {
        try {
            int actualCount = getEntities(argumentName, context).size();

            return String.format("This will kill %d entities!", actualCount);
        } catch (Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error8:" + e.getMessage());
            return "This will kill entities (count could not be determined)!";
        }
    }

    private static String generateFillWarning(String command, CommandContext<CommandSourceStack> context) {
        try {
            String[] parts = command.split("\\s+");
            if (parts.length >= 7) {
                Vec3 playerPos = context.getSource().getPosition();
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

    private static String generateCloneWarning(String command, CommandContext<CommandSourceStack> context) {
        try {
            String[] parts = command.split("\\s+");
            if (parts.length >= 7) {
                Vec3 playerPos = context.getSource().getPosition();
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

    private static String generateEntityWarning(List<String> arguments, CommandContext<CommandSourceStack> context) {
        try {
            int actualCount = getEntities(arguments, context).size();
            return String.format("This will affect %d entities!", actualCount);
        } catch (Exception e) {
            Main.LOGGER.error("[CommandAnalyzer] error11:" + e.getMessage());
        }
        return "This will affect ? entities (count could not be determined)!";
    }

    private static String generateScoreboardWarning(String command, String argumentName, CommandContext<CommandSourceStack> context) {
        try {
            if (command.toLowerCase().contains("scoreboard players reset ")) {
                try {
                    Objective test = ObjectiveArgument.getObjective(context, "objective");
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

    public static void sendConfirmationMessage(ServerPlayer player, String command, CommandContext<CommandSourceStack> context) {
        String warning = generateWarning(command, context);
        CommandValidator.addPendingCommand(player.getUUID(), command, warning);
        CommandValidator.PendingCommand pending = CommandValidator.getPendingCommand(player.getUUID());

        MutableComponent confirmText = Component.literal("[CONFIRM]")
                .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
                .withStyle(style -> style
                        .withClickEvent(new ClickEvent.RunCommand(
                                "/confirmcmd " + pending.confirmId))
                        .withHoverEvent(new HoverEvent.ShowText(
                                Component.literal("Click to execute the command"))));

        MutableComponent cancelText = Component.literal("[CANCEL]")
                .withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
                .withStyle(style -> style
                        .withClickEvent(new ClickEvent.RunCommand(
                                "/confirmcmd cancel"))
                        .withHoverEvent(new HoverEvent.ShowText(
                                Component.literal("Click to cancel"))));

        MutableComponent message = Component.literal("⚠ DANGEROUS COMMAND WARNING ⚠")
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.BOLD)
                .append(Component.literal("\n" + warning).withStyle(ChatFormatting.WHITE))
                .append(Component.literal("\nCommand: ").withStyle(ChatFormatting.GRAY))
                .append(Component.literal(command).withStyle(ChatFormatting.YELLOW))
                .append(Component.literal("\n\n"))
                .append(confirmText)
                .append(Component.literal("  "))
                .append(cancelText);

        player.displayClientMessage(message, false);
    }
}
