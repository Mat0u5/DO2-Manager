package net.mat0u5.do2manager.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
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
import java.util.concurrent.CompletableFuture;

public class TestingCommand {
    public static int execute(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        //new DiscordUtils().updateDiscordChannelDescription();
System.out.println(Main.allPlayers);

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
    public static int updateGameProfiles(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        for (String uuid : Main.allPlayers.keySet()) {
            String name = Main.allPlayers.get(uuid);
            ItemManager.getPlayerProfileAsync(name).thenAccept(gameProfile->{
                if (gameProfile.isPresent()) {
                    System.out.println("TESTTT_"+uuid+"__"+name+"__"+gameProfile.get().getProperties());
                    DatabaseManager.addPlayer(uuid,name,gameProfile.get());
                }
            });
        }

        self.sendMessage(Text.translatable("§6Updating Profiles.."));
        return 1;
    }
}
