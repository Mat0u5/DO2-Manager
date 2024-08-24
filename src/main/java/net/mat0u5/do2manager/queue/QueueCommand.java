package net.mat0u5.do2manager.queue;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collection;
import java.util.List;

import static net.mat0u5.do2manager.Main.dungeonQueue;

public class QueueCommand {
    public static int joinQueue(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.addToQueue(self,false);
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

        dungeonQueue.skipTurns(self, skipTurns,false);
        return 1;
    }
    public static int skipTurnOther(ServerCommandSource source, ServerPlayerEntity target,int skipTurns) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.skipTurns(target, skipTurns,true);
        return 1;
    }
    public static int runFinish(ServerCommandSource source, Collection<? extends ServerPlayerEntity> targets) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        if (targets.isEmpty()) return -1;

        dungeonQueue.putAtEnd(targets);
        return 1;
    }

    public static int addPlayerToQueue(ServerCommandSource source, ServerPlayerEntity target) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.addToQueue(target,true);
        return 1;
    }

    public static int removePlayerFromQueue(ServerCommandSource source, String target) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        dungeonQueue.removeFromQueueStr(target);
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
