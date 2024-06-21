package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.config.ConfigManager;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.DO2_GSON;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.mat0u5.do2manager.utils.OtherUtils.stringToInt;

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
    public static int database_runTracking_modifyVar(ServerCommandSource source, String query) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        query = OtherUtils.removeQuotes(query);
        int setVarIndex = OtherUtils.findStringPosInString(query, "=");
        int addVarIndex = OtherUtils.findStringPosInString(query, "+=");
        if (addVarIndex == (setVarIndex - 1) && addVarIndex != -1) { //Add to a variable value
            String configName = query.substring(0,addVarIndex);
            String configValueAdd = query.substring(addVarIndex+2);
            //TODO
        }
        else {//Set a variable
            String configName = query.substring(0,setVarIndex);
            String configValue = query.substring(setVarIndex+1);
            if (configName.equalsIgnoreCase("run_type")) Main.currentRun.run_type = configValue;
        }
        return 1;
    }
    public static int database_runTracking_SaveRun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        if (Main.currentRun.run_length == -1 || Main.currentRun.runners.isEmpty() || Main.currentRun.run_number == -1) {
            Main.resetRunInfo();
            return -1;
        }
        try {
            DatabaseManager.addRun(Main.currentRun);
            DatabaseManager.addRunDetailed(Main.currentRun);
            DatabaseManager.addRunSpeedrun(Main.currentRun);
            System.out.println("Run Saved to Database.");

        }catch(Exception e) {
            System.out.println(e);
        }
        Main.resetRunInfo();
        return 1;
    }
    public static int database_runTracking_RunNumber(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        Main.currentRun.run_number = RunInfoParser.getRunNum(server);
        return 1;
    }
    public static int database_runTracking_RunDiff(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        Main.currentRun.difficulty = RunInfoParser.getRunDifficulty(server);
        return 1;
    }
    public static int database_runTracking_ItemDeck(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        ItemStack deck = RunInfoParser.getDeck(server);
        if (deck != null) Main.currentRun.deck_item = deck;
        return 1;
    }
    public static int database_runTracking_ItemInventory(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;
        List<PlayerEntity> playersList = RunInfoParser.getCurrentAliveRunners(server);
        if (playersList.isEmpty()) {
            Main.currentRun.inventory_save = null;
            return -1;
        }
        List<ItemStack> allRunnersItems = new ArrayList<>();
        for (PlayerEntity player : playersList) {
            allRunnersItems.addAll(ItemManager.getPlayerInventory(player));
        }
        Main.currentRun.inventory_save = allRunnersItems;
        return 1;
    }
    public static int database_runTracking_Timestamp(ServerCommandSource source, String varName) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        if (varName.contains("run_length")) Main.currentRun.run_length = RunInfoParser.getRunLength(server);
        if (varName.contains("artifact")) Main.currentRun.timestamp_artifact = RunInfoParser.getRunLength(server);
        if (varName.contains("lvl2_entry")) Main.currentRun.timestamp_lvl2_entry = RunInfoParser.getRunLength(server);
        if (varName.contains("lvl3_entry")) Main.currentRun.timestamp_lvl3_entry = RunInfoParser.getRunLength(server);
        if (varName.contains("lvl4_entry")) Main.currentRun.timestamp_lvl4_entry = RunInfoParser.getRunLength(server);
        if (varName.contains("lvl4_exit")) Main.currentRun.timestamp_lvl4_exit = RunInfoParser.getRunLength(server);
        if (varName.contains("lvl3_exit")) Main.currentRun.timestamp_lvl3_exit = RunInfoParser.getRunLength(server);
        if (varName.contains("lvl2_exit")) Main.currentRun.timestamp_lvl2_exit = RunInfoParser.getRunLength(server);
        if (varName.contains("lvl1_exit")) Main.currentRun.timestamp_lvl1_exit = RunInfoParser.getRunLength(server);
        return 1;
    }

    public static int database_runTracking_Items(ServerCommandSource source, String funName, Collection<? extends Entity> items) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;
        for (Entity item : items) {
            if (item instanceof ItemEntity) {
                ItemEntity itemEntity = (ItemEntity) item;
                ItemStack itemStack = itemEntity.getStack();
                if (funName.contains("card_plays")) Main.currentRun.card_plays.add(itemStack.copy());
                if (funName.contains("items_bought")) {
                    Main.currentRun.items_bought.add(itemStack.copy());
                    //Add to barrel
                    ItemManager.insertItemIntoBarrel(server.getOverworld(), new BlockPos(-549, 114, 1976), itemStack);
                }
            }
        }
        return 1;
    }
    public static int database_runTracking_Players(ServerCommandSource source, String varName) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        if (!varName.equalsIgnoreCase("runners") && !varName.equalsIgnoreCase("finishers")) return -1;
        String players = "";
        List<PlayerEntity> playersList = varName.equalsIgnoreCase("runners")?RunInfoParser.getCurrentRunners(server):RunInfoParser.getCurrentAliveRunners(server);
        if (playersList.isEmpty()) {
            if (varName.equalsIgnoreCase("runners")) Main.currentRun.runners = null;
            if (varName.equalsIgnoreCase("finishers")) {
                //Run End
                Main.currentRun.finishers = null;
                database_runTracking_SaveRun(source);
            }
            return -1;
        }
        for (PlayerEntity player : playersList) {
            if (varName.equalsIgnoreCase("runners")) Main.currentRun.runners.add(player.getUuidAsString());
            if (varName.equalsIgnoreCase("finishers")) Main.currentRun.finishers.add(player.getUuidAsString());
        }

        return 1;
    }
    public static int database_runTracking_PrepareForRun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        Main.resetRunInfo();
        return 1;
    }
}
