package net.mat0u5.do2manager.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

public class RestartCommand {
    private static boolean restartQueued = false;


    public static int queueRestart(CommandSourceStack source, boolean enable) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();

        restartQueued = enable;
        String message = (restartQueued)?"Server restart has been queued.":"A queued server restart has been cancelled.";
        if (self == null ) System.out.println(message);
        else self.displayClientMessage(Component.translatable(message), false);
        return 1;
    }

    public static boolean isRestartQueued() {
        return restartQueued;
    }

    public static void setRestartQueued(boolean queued) {
        restartQueued = queued;
    }
}
