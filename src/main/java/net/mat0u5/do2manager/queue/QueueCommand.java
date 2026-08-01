package net.mat0u5.do2manager.queue;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;

import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import java.util.Collection;
import java.util.List;

import static net.mat0u5.do2manager.Main.dungeonQueue;

public class QueueCommand {
    public static int joinQueue(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        String command = "execute as "+self.getStringUUID()+" run trigger do2queue set 2";

        OtherUtils.executeCommand(server,command);
        return 1;
    }

    public static int leaveQueue(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        String command = "execute as "+self.getStringUUID()+" run trigger do2queue set 3";

        OtherUtils.executeCommand(server,command);
        return 1;
    }

    public static int skipTurn(CommandSourceStack source,int skipTurns) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        String command = "execute as "+self.getStringUUID()+" run trigger do2queue set 4";

        OtherUtils.executeCommand(server,command);
        return 1;
    }
    public static int skipTurnOther(CommandSourceStack source, ServerPlayer target,int skipTurns) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();

        String command = "execute as "+self.getStringUUID()+" run trigger do2queue set 4";
            OtherUtils.executeCommand(server,command);

        return 1;
    }
    public static int runFinish(CommandSourceStack source, Collection<? extends ServerPlayer> targets) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        String command = "execute as "+self.getStringUUID()+" run trigger do2queue set 5";

        OtherUtils.executeCommand(server,command);
        return 1;
    }

    public static int addPlayerToQueue(CommandSourceStack source, ServerPlayer target) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();

        String command = "execute as "+self.getStringUUID()+" run trigger do2queue set 2";
            OtherUtils.executeCommand(server,command);

        return 1;
    }

    public static int removePlayerFromQueue(CommandSourceStack source, String target) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();

        String command = "execute as "+self.getStringUUID()+" run trigger do2queue set 3";
            OtherUtils.executeCommand(server,command);

        return 1;
    }

    //public static int moveQueue(CommandContext<CommandSourceStack> context) {
        //CommandSourceStack source = context.getSource();
        //MinecraftServer server = source.getServer();
        //final Player self = source.getPlayer();

        //dungeonQueue.moveQueue();
        //return 1;
    //}
    public static int listQueue(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        String command = "execute as "+self.getStringUUID()+" run trigger do2queue set 6";
                OtherUtils.executeCommand(server,command);

        return 1;
    }
    public static SuggestionProvider<CommandSourceStack> getQueuePlayersSuggestionProvider() {
        return (context, builder) -> {
            List<String> queue = dungeonQueue.getQueue();
            for (String playerName : queue) {
                builder.suggest(playerName);
            }
            return builder.buildFuture();
        };
    }

}
