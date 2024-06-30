package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

public class TestingCommand {
    public static int execute(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        String old = Main.config.getProperty("testing");
        if (old == null || old.isEmpty()) Main.config.setProperty("testing","true");
        else if (old.equalsIgnoreCase("false")) Main.config.setProperty("testing","true");
        else if  (old.equalsIgnoreCase("true")) Main.config.setProperty("testing","false");
        self.sendMessage(Text.of("Testing is now: " + Main.config.getProperty("testing")));
        return 1;
    }
    public static int executeAddRun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        int runNum = RunInfoParser.getRunNum(server);
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
        TestRun.run_number = -24;
        TestRun.run_length = 1000;
        TestRun.timestamp_lvl2_entry = 2;
        TestRun.timestamp_lvl3_entry = 3;
        TestRun.timestamp_lvl4_entry = 4;
        TestRun.timestamp_lvl4_exit = 5;
        TestRun.timestamp_lvl3_exit = 6;
        TestRun.timestamp_lvl2_exit = 7;
        TestRun.timestamp_lvl1_exit = 8;
        TestRun.timestamp_artifact = 9;

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

        //PlayerEntity player = OtherUtils.getPlayerFromUUIDString(server,"983895d7-82b6-4bcb-a8ad-17d93245e0a4");
        //PlayerEntity player2 = OtherUtils.getPlayerFromName(server,"OntiMoose");
        //System.out.println("test2: " + ItemManager.getPlayerInventory(player2));
        /*DO2Run runn = new DO2Run();
        runn.compass_item = self.getMainHandStack().copy();
        self.sendMessage(Text.of("Compass Level: " + runn.getCompassLevel()));*/
        self.sendMessage(Text.of("Emberz: " + RunInfoParser.getPlayerEmbers(server)));

        return 1;
    }
}
