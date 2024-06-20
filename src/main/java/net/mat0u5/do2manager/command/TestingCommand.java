package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.DO2_GSON;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.List;

public class TestingCommand {
    public static int execute(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        DatabaseManager.printAllPlayers();
        try {
            DatabaseManager.updateTable();
        }catch (Exception e){}
        self.sendMessage(Text.translatable("§6Command Worked.."));
        return 1;
    }
    public static int executeGetItem(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        List<ItemStack> playerItems = DatabaseManager.getItemsByPlayerUUID(self.getUuidAsString());
        for (ItemStack item : playerItems) {
            ItemManager.giveItemStack(self,item);
        }

        self.sendMessage(Text.translatable("§6Command Worked.."));
        return 1;
    }
    public static int executeSetItem(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        DatabaseManager.addItem(self.getUuidAsString(),self.getStackInHand(Hand.MAIN_HAND));

        self.sendMessage(Text.translatable("§6Command Worked.."));
        return 1;
    }
    public static int executeAddRun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        int runNum = RunInfoParser.getRunNum(server);

        DatabaseManager.addRun(runNum, "casual",self.getUuidAsString(),null, 32456);
        DatabaseManager.addRunDetailed(runNum, "card1,card2",5, DO2_GSON.serializeItemStack(new ItemStack(Items.COMPASS, 1)), DO2_GSON.serializeItemStack(new ItemStack(Items.IRON_NUGGET, 1)), DO2_GSON.serializeItemStack(self.getStackInHand(Hand.MAIN_HAND)), DO2_GSON.serializePlayerInventory(self), "itemSBOUGHt", "-520 69 420", "ravager hihi");
        DatabaseManager.addRunSpeedrun(runNum,1,2,3,4,5,6,7,8);

        self.sendMessage(Text.translatable("§6Command Worked.."));
        return 1;
    }
    public static int executeGetInv(ServerCommandSource source, int runNum) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        //ItemManager.giveItemStack(self, DatabaseManager.getInvByRunNumber(self,runNum));
        ItemStack items = DO2_GSON.deserializeItemStack(Main.config.getProperty("artifact_item"));
        ItemManager.giveItemStack(self,items);
        self.sendMessage(Text.translatable("§6Command Worked.."));
        return 1;
    }
    public static int executeTest(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        System.out.println(RunInfoParser.getDeck(server));
        self.sendMessage(Text.translatable("§6Command Worked.."));
        return 1;
    }
}
