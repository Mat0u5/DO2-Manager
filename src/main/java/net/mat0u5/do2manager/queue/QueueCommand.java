package net.mat0u5.do2manager.queue;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.mat0u5.do2manager.Main;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;
import java.util.UUID;

import static net.mat0u5.do2manager.Main.dungeonQueue;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;

public class QueueCommand {
    public static int joinQueue(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        ServerPlayerEntity player = context.getSource().getPlayer();
        UUID playerUuid = player.getUuid();
        dungeonQueue.addToQueue(playerUuid);
        return 1;
    }

    public static int leaveQueue(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        ServerPlayerEntity player = context.getSource().getPlayer();
        UUID playerUuid = player.getUuid();
        dungeonQueue.removeFromQueue(playerUuid);
        self.sendMessage(Text.of("You have left the queue!"));
        return 1;
    }

    public static int skipTurn(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        ServerPlayerEntity player = context.getSource().getPlayer();
        UUID playerUuid = player.getUuid();
        if (dungeonQueue.containsPlayer(playerUuid)) {
            dungeonQueue.removeFromQueue(playerUuid);
            self.sendMessage(Text.of("You have skipped your turn!"));
        }
        dungeonQueue.addToQueue(playerUuid);
        return 1;
    }

    public static int addPlayerToQueue(ServerCommandSource source, ServerPlayerEntity target) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.addToQueue(target.getUuid());
        self.sendMessage(Text.of("Player added to queue!"));
        return 1;
    }

    public static int removePlayerFromQueue(ServerCommandSource source, ServerPlayerEntity target) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.removeFromQueue(target.getUuid());
        self.sendMessage(Text.of("Player removed from queue!"));
        return 1;
    }

    public static int moveQueue(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.moveQueue();
        self.sendMessage(Text.of("Queue moved!"));
        return 1;
    }
    public static int listQueue(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        List<UUID> queue = dungeonQueue.getQueue();

        if (queue.isEmpty()) {
            self.sendMessage(Text.of("The queue is currently empty."));
            return 0;
        }

        StringBuilder playerList = new StringBuilder("Players in queue:\n");
        for (UUID playerUuid : queue) {
            ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(playerUuid);
            if (player != null) {
                playerList.append("- ").append(player.getName().getString()).append("\n");
            } else {
                playerList.append("- [Unknown Player] (UUID: ").append(playerUuid.toString()).append(")\n");
            }
        }

        self.sendMessage(Text.of(playerList.toString()));
        return 1;
    }
    public static SuggestionProvider<ServerCommandSource> getQueuePlayersSuggestionProvider() {
        return (context, builder) -> {
            List<UUID> queue = dungeonQueue.getQueue();
            for (UUID playerUuid : queue) {
                ServerPlayerEntity player = context.getSource().getServer().getPlayerManager().getPlayer(playerUuid);
                if (player != null) {
                    builder.suggest(player.getName().getString());
                }
            }
            return builder.buildFuture();
        };
    }

}
