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
    public static int database_runTracking_modifyVar(ServerCommandSource source, String query) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        query = OtherUtils.removeQuotes(query);
        int setVarIndex = OtherUtils.findStringPosInString(query, "=");
        String configName = query.substring(0,setVarIndex);
        String configValue = query.substring(setVarIndex+1);
        if (configName.equalsIgnoreCase("run_type")) Main.currentRun.run_type = configValue;
        if (configName.equalsIgnoreCase("special_event")) {
            if (!Main.currentRun.special_events.contains(configValue)) Main.currentRun.special_events.add(configValue);
        }
        return 1;
    }
    public static int database_runTracking_SaveRun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        DatabaseManager.saveRun(server);
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

        if (Main.currentRun.deck_item != null) return -1;
        ItemStack deck = RunInfoParser.getDeck(server);
        if (deck != null) Main.currentRun.deck_item = deck;
        return 1;
    }
    public static int database_runTracking_Embers(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        Main.currentRun.embers_counted = RunInfoParser.getPlayerEmbers(server);
        return 1;
    }
    public static int database_runTracking_Crowns(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        Main.currentRun.crowns_counted = RunInfoParser.getPlayerCrowns(server);
        return 1;
    }
    public static int database_runTracking_ItemInventory(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        if (!Main.currentRun.inventory_save.isEmpty()) return -1;

        List<PlayerEntity> playersList = RunInfoParser.getCurrentAliveRunners(server);
        if (playersList.isEmpty())  return -1;
        List<ItemStack> allRunnersItems = new ArrayList<>();
        for (PlayerEntity player : playersList) {
            allRunnersItems.addAll(ItemManager.getPlayerInventory(player));
        }
        Main.currentRun.inventory_save = allRunnersItems;
        return 1;
    }
    public static int lastSplit = 0;
    public static int database_runTracking_Timestamp(ServerCommandSource source, String varName) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;
        int run_length = RunInfoParser.getRunLength(server);
        if (varName.contains("run_length")) {
            Main.currentRun.run_length = run_length;
            if (Main.currentRun.timestamp_lvl1_exit!=-1) {
                sendSpeedrunMessage(server, "End Time", Main.currentRun.run_length,Main.speedrun.run_length,true);
            }
        }
        if (varName.contains("artifact")) {
            Main.currentRun.timestamp_artifact = run_length;
            sendSpeedrunMessage(server, "Artifact obtained", Main.currentRun.timestamp_artifact,Main.speedrun.timestamp_artifact);
        }
        if (varName.contains("lvl2_entry")) {
            Main.currentRun.timestamp_lvl2_entry = run_length;
            sendSpeedrunMessage(server, "Lvl2 entry", Main.currentRun.timestamp_lvl2_entry,Main.speedrun.timestamp_lvl2_entry);
        }
        if (varName.contains("lvl3_entry")) {
            Main.currentRun.timestamp_lvl3_entry = run_length;
            sendSpeedrunMessage(server, "Lvl3 entry", Main.currentRun.timestamp_lvl3_entry,Main.speedrun.timestamp_lvl3_entry);
        }
        if (varName.contains("lvl4_entry")) {
            Main.currentRun.timestamp_lvl4_entry = run_length;
            sendSpeedrunMessage(server, "Lvl4 entry", Main.currentRun.timestamp_lvl4_entry,Main.speedrun.timestamp_lvl4_entry);
        }
        if (varName.contains("lvl4_exit")) {
            Main.currentRun.timestamp_lvl4_exit = run_length;
            sendSpeedrunMessage(server, "Lvl4 exit", Main.currentRun.timestamp_lvl4_exit,Main.speedrun.timestamp_lvl4_exit);
        }
        if (varName.contains("lvl3_exit")) {
            Main.currentRun.timestamp_lvl3_exit = run_length;
            sendSpeedrunMessage(server, "Lvl3 exit", Main.currentRun.timestamp_lvl3_exit,Main.speedrun.timestamp_lvl3_exit);
        }
        if (varName.contains("lvl2_exit")) {
            Main.currentRun.timestamp_lvl2_exit = run_length;
            sendSpeedrunMessage(server, "Lvl2 exit", Main.currentRun.timestamp_lvl2_exit,Main.speedrun.timestamp_lvl2_exit);
        }
        if (varName.contains("lvl1_exit")) {
            Main.currentRun.timestamp_lvl1_exit = run_length;
        }
        int currentSplit = run_length-lastSplit;
        sendTimestampMessage(server,"_CurrentSplit_",currentSplit);
        lastSplit = run_length;

        return 1;
    }
    public static void sendSpeedrunMessage(MinecraftServer server, String name, int currentRun, int bestRun) {
        sendSpeedrunMessage(server,name,currentRun,bestRun,false);
    }
    public static void sendSpeedrunMessage(MinecraftServer server, String name, int currentRun, int bestRun, boolean showMilis) {
        boolean isSpeedrun = Main.config.getProperty("current_run_is_speedrun").equalsIgnoreCase("true") || Main.config.getProperty("current_run_is_speedrun").equalsIgnoreCase("detailed");
        if (!isSpeedrun) return;
        if (currentRun == -1 || bestRun == -1) return;
        int diff = currentRun - bestRun;
        OtherUtils.broadcastMessage(server, Text.translatable(
                "§6 - "+name+": " + OtherUtils.convertTicksToClockTime(currentRun,showMilis) +
                        " [" + (diff < 0 ? "§a" : "§c")+((diff > 0)? "+":"")+OtherUtils.convertTicksToClockTime(diff,showMilis) + "§6]"
        ));
    }
    public static void sendTimestampMessage(MinecraftServer server, String name, int currentRun) {
        boolean isSpeedrunAdvanced = Main.config.getProperty("current_run_is_speedrun").equalsIgnoreCase("detailed");
        if (!isSpeedrunAdvanced) return;
        if (currentRun == -1) return;
        OtherUtils.broadcastMessage(server, Text.translatable(
                "§6 - "+name+": " + OtherUtils.convertTicksToClockTime(currentRun,true)
        ));
    }

    public static int database_runTracking_Items(ServerCommandSource source, String funName, Collection<? extends Entity> items) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;
        for (Entity item : items) {
            if (item instanceof ItemEntity) {
                ItemEntity itemEntity = (ItemEntity) item;
                ItemStack itemStack = itemEntity.getStack();
                if (funName.contains("card_plays")) {
                    Main.currentRun.card_plays.add(itemStack.copy());
                }
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
        List<PlayerEntity> playersList = varName.equalsIgnoreCase("runners")?RunInfoParser.getCurrentRunners(server):RunInfoParser.getCurrentAliveRunners(server);
        if (playersList.isEmpty()) {
            if (varName.equalsIgnoreCase("runners")) Main.currentRun.runners = new ArrayList<>();
            if (varName.equalsIgnoreCase("finishers")) {
                //Run End
                Main.currentRun.finishers = new ArrayList<>();
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
