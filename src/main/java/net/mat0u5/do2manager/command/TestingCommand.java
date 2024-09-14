package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.ScoreboardUtils;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.*;

public class TestingCommand {
    public static int execute(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        new DiscordUtils().updateDiscordChannelDescription();

        return 1;
    }
    public static int executeCmd(String args) {
        OtherUtils.executeCommand(Main.server,args);
        return 1;
    }
    public static int executeAddRun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();


        self.sendMessage(Text.translatable("§6Command Worked.."));
        return 1;
    }
    public static int executeGetInv(ServerCommandSource source, int runNum) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        self.sendMessage(Text.translatable("§6Command Worked.."));
        return 1;
    }
    public static int executeTest(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();



        return 1;
    }
}
