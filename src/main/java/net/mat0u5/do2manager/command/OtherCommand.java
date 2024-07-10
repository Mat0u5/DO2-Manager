package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.CommandBlockScanner;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class OtherCommand {
    public static int executeSpeedrun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        Main.config.setProperty("current_run_is_speedrun","true");
        OtherUtils.broadcastMessage(server, Text.of("§6This run has been marked as a speedrun."));
        return 1;
    }
    public static int executeLock(ServerCommandSource source, int fromX, int fromY, int fromZ, int toX, int toY, int toZ, String type) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        self.sendMessage(Text.of("Started Block Lock Search..."));
        CommandBlockScanner.scanArea(type,server.getOverworld(),new BlockPos(fromX, fromY, fromZ),new BlockPos(toX, toY, toZ), source.getPlayer());
        return 1;
    }
}
