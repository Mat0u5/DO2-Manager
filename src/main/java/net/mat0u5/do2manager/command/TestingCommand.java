package net.mat0u5.do2manager.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DO2RunIterator;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.ScoreboardUtils;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.RavagerEntity;
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
    public static int executeTestEntity(ServerCommandSource source, Entity target) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        if (target instanceof RavagerEntity) {
            RavagerEntity ravager = (RavagerEntity) target;
            source.sendMessage(Text.of("attack range: " + ravager.squaredAttackRange(self)));
            source.sendMessage(Text.of("attack range2: " + ravager.getSquaredDistanceToAttackPosOf(self)));
            source.sendMessage(Text.of("attack range3: " + ravager.getWidth()));
        }
        else {
            source.sendMessage(Text.of("Not a ravager"));
        }

        return 1;
    }
}
