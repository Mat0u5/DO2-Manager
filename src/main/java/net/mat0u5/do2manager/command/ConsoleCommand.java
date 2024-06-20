package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.ScoreboardUtils;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

public class ConsoleCommand {
    public static boolean isRanByPlayer(ServerCommandSource source) {
        if (source.getEntity() != null) {
            final PlayerEntity self = source.getPlayer();
            self.sendMessage(Text.translatable("\n§c-------------------------\nAll commands under '/decked-out console-only' §4§l§ncannot be run by players. §r§c" +
                    "\nThey are ONLY meant for command blocks and the console, \nas they have delicate (and messy) syntax and are used for modifying the database.\n" +
                    "§l§nDO NOT MODIFY§r§c any command blocks that use these commands, unless you know what you're doing!\n-------------------------\n"));
            return true;
        }
        return false;
    }
    public static int execute(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;
        System.out.println("TEST-COMMAND-SUCCESSFUL");
        return 1;
    }
    public static int database_runTracking_GetInfo(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;
        System.out.println("CMD_OUTPUT: RunNum: " + RunInfoParser.getRunNum(server));
        System.out.println("CMD_OUTPUT: RunLength: " + RunInfoParser.getRunLength(server));
        System.out.println("CMD_OUTPUT: RunLengthFormatted: " + RunInfoParser.getFormattedRunLength(server));
        List<PlayerEntity> runners = RunInfoParser.getCurrentRunners(server);
        if (runners.isEmpty()) System.out.println("CMD_OUTPUT: CurrentRunner: null");
        for (PlayerEntity runner : runners) {
            System.out.println("CMD_OUTPUT: CurrentRunner: " + runner.getEntityName());
        }

        return 1;
    }
}
