package net.mat0u5.do2manager.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class RestartCommand {
    private static boolean restartQueued = false;


    public static int queueRestart(ServerCommandSource source, boolean enable) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        restartQueued = enable;
        String message = (restartQueued)?"Server restart has been queued.":"A queued server restart has been cancelled.";
        if (self == null ) System.out.println(message);
        else self.sendMessage(Text.translatable(message), false);
        return 1;
    }

    public static boolean isRestartQueued() {
        return restartQueued;
    }

    public static void setRestartQueued(boolean queued) {
        restartQueued = queued;
    }
}
