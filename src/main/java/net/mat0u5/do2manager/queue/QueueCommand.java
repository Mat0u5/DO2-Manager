package net.mat0u5.do2manager.queue;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.mat0u5.do2manager.Main;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static net.mat0u5.do2manager.Main.dungeonQueue;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;

public class QueueCommand {
    public static int joinQueue(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.addToQueue(self);
        return 1;
    }

    public static int leaveQueue(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.removeFromQueue(self);
        return 1;
    }

    public static int skipTurn(ServerCommandSource source,int skipTurns) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.skipTurns(self, skipTurns);
        return 1;
    }
    public static int runStart(ServerCommandSource source, Collection<? extends ServerPlayerEntity> targets) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        if (targets.isEmpty()) return -1;

        dungeonQueue.putAtEnd(targets);
        return 1;
    }

    public static int addPlayerToQueue(ServerCommandSource source, ServerPlayerEntity target) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.addToQueue(target);
        return 1;
    }

    public static int removePlayerFromQueue(ServerCommandSource source, ServerPlayerEntity target) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.removeFromQueue(target);
        return 1;
    }

    public static int moveQueue(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.moveQueue();
        return 1;
    }
    public static int listQueue(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        dungeonQueue.messageQueueToPlayer(self);
        return 1;
    }
    public static SuggestionProvider<ServerCommandSource> getQueuePlayersSuggestionProvider() {
        return (context, builder) -> {
            List<String> queue = dungeonQueue.getQueue();
            for (String playerName : queue) {
                builder.suggest(playerName);
            }
            return builder.buildFuture();
        };
    }

}
