package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.ScoreboardUtils;
import net.mat0u5.do2manager.utils.TextUtils;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.FakeSign;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class TestingCommand {
    public static int execute(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        self.sendMessage(Text.of(RunInfoParser.getFastestPlayerRunMatchingCurrent(self).loot_drops.toString()));

        return 1;
    }
    public static int executeCmd(String args) {
        OtherUtils.executeCommand(Main.server,args);
        return 1;
    }
    public static int executeAddRun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        int runNum = RunInfoParser.getRunNum(server)+1;
        DO2Run TestRun = new DO2Run();
        TestRun.run_type = "casual";
        TestRun.runners = List.of(self.getUuidAsString());
        TestRun.finishers = List.of(self.getUuidAsString());
        TestRun.card_plays = List.of(new ItemStack(Items.STONE),new ItemStack(Items.IRON_AXE));
        TestRun.compass_item = new ItemStack(Items.COMPASS);
        TestRun.artifact_item = new ItemStack(Items.IRON_NUGGET);
        TestRun.deck_item = new ItemStack(Items.DIAMOND);
        TestRun.inventory_save = ItemManager.getPlayerInventory(self);
        TestRun.items_bought = null;
        TestRun.death_pos = "";
        TestRun.death_message = "";
        TestRun.difficulty = 5;
        TestRun.run_number = runNum;
        TestRun.run_length = 1000;
        TestRun.timestamp_lvl2_entry = 2;
        TestRun.timestamp_lvl3_entry = 3;
        TestRun.timestamp_lvl4_entry = 4;
        TestRun.timestamp_lvl4_exit = 5;
        TestRun.timestamp_lvl3_exit = 6;
        TestRun.timestamp_lvl2_exit = 7;
        TestRun.timestamp_lvl1_exit = 8;
        TestRun.timestamp_artifact = 9;
        TestRun.loot_drops = ScoreboardUtils.getLootEvents();
        TestRun.special_events = Main.currentRun.special_events;

        DatabaseManager.addRun(TestRun);
        DatabaseManager.addRunDetailed(TestRun);
        DatabaseManager.addRunSpeedrun(TestRun);

        self.sendMessage(Text.translatable("§6Command Worked.."));
        return 1;
    }
    public static int executeGetInv(ServerCommandSource source, int runNum) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        DO2Run run = DatabaseManager.getRunByRunNumber(runNum);
        ItemManager.giveItemStack(self, run.inventory_save);
        self.sendMessage(Text.translatable("§6Command Worked.."));
        return 1;
    }
    public static int executeTest(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        Main.reloadAllRuns();


        return 1;
    }
}
