package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.config.ConfigManager;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.DO2_GSON;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

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
            String configValueBefore = Main.config.getProperty(configName);
            if (configValueBefore == null || configValueBefore.isEmpty()) {
                Main.config.setProperty(configName, configValueAdd);
            }
            else {
                //This aint pretty, look away o_O
                Main.config.setProperty(configName, configValueBefore+ConfigManager.VALUE_SEPARATOR + configValueAdd);
            }
        }
        else {//Set a variable
            String configName = query.substring(0,setVarIndex);
            String configValue = query.substring(setVarIndex+1);
            Main.config.setProperty(configName, configValue);
        }
        return 1;
    }
    public static int database_runTracking_SaveRun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        String run_type = Main.config.getProperty("run_type");//Done with modifyVar
        String runners = Main.config.getProperty("runners"); //Done with Players
        String finishers = Main.config.getProperty("finishers"); //Done with Players
        String card_plays = Main.config.getProperty("card_plays");//Done with modifyVar
        String compass_item = Main.config.getProperty("compass_item");//Done
        String artifact_item = Main.config.getProperty("artifact_item");//Done
        String deck_item = Main.config.getProperty("deck_item");//Done
        String inventory_save = Main.config.getProperty("inventory_save");//Done
        String items_bought = Main.config.getProperty("items_bought");
        String death_pos = Main.config.getProperty("death_pos");//Done
        String death_cause = Main.config.getProperty("death_message");//Done
        int difficulty = stringToInt(Main.config.getProperty("difficulty"));//Done
        int run_number = stringToInt(Main.config.getProperty("run_number"));//Done + rest done
        int run_length = stringToInt(Main.config.getProperty("run_length"));
        int timestamp_lvl2_entry = stringToInt(Main.config.getProperty("timestamp_lvl2_entry"));
        int timestamp_lvl3_entry = stringToInt(Main.config.getProperty("timestamp_lvl3_entry"));
        int timestamp_lvl4_entry = stringToInt(Main.config.getProperty("timestamp_lvl4_entry"));
        int timestamp_lvl4_exit = stringToInt(Main.config.getProperty("timestamp_lvl4_exit"));
        int timestamp_lvl3_exit = stringToInt(Main.config.getProperty("timestamp_lvl3_exit"));
        int timestamp_lvl2_exit = stringToInt(Main.config.getProperty("timestamp_lvl2_exit"));
        int timestamp_lvl1_exit = stringToInt(Main.config.getProperty("timestamp_lvl1_exit"));
        int timestamp_artifact = stringToInt(Main.config.getProperty("timestamp_artifact"));

        DatabaseManager.addRun(run_number, run_type, runners,finishers,run_length);
        DatabaseManager.addRunDetailed(run_number, card_plays, difficulty, compass_item,artifact_item,deck_item,inventory_save, items_bought,death_pos,death_cause);
        DatabaseManager.addRunSpeedrun(run_number, timestamp_lvl2_entry,timestamp_lvl3_entry,timestamp_lvl4_entry,timestamp_lvl4_exit,timestamp_lvl3_exit,timestamp_lvl2_exit,timestamp_lvl1_exit,timestamp_artifact);
        return 1;
    }
    public static int database_runTracking_ItemCompassOrArti(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        ItemStack compass = RunInfoParser.getRunnersCompass(server);
        ItemStack artifact = RunInfoParser.getRunnersArtifact(server);
        if (compass == null && artifact == null) {
            System.out.println("ERROR_NO_COMPASS_OR_ARTIFACT_FOUND");
            return -1;
        }
        if (compass != null ) Main.config.setProperty("compass_item", DO2_GSON.serializeItemStack(compass));
        if (artifact != null ) Main.config.setProperty("artifact_item", DO2_GSON.serializeItemStack(artifact));
        return 1;
    }
    public static int database_runTracking_RunNumber(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        Main.config.setProperty("run_number", String.valueOf(RunInfoParser.getRunNum(server)));
        return 1;
    }
    public static int database_runTracking_RunDiff(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        Main.config.setProperty("difficulty", String.valueOf(RunInfoParser.getRunDifficulty(server)));
        return 1;
    }
    public static int database_runTracking_ItemDeck(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        ItemStack deck = RunInfoParser.getDeck(server);
        System.out.println(deck);
        if (deck != null) Main.config.setProperty("deck_item", DO2_GSON.serializeItemStack(deck));
        return 1;
    }
    public static int database_runTracking_ItemInventory(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;
        List<PlayerEntity> playersList = RunInfoParser.getCurrentAliveRunners(server);
        if (playersList.isEmpty()) {
            Main.config.setProperty("inventory_save", "");
            return -1;
        }
        PlayerEntity player = playersList.get(0);//TODO SUPPORT FOR MULTIPLE PLAYERS
        Main.config.setProperty("inventory_save", DO2_GSON.serializePlayerInventory(player));
        return 1;
    }
    public static int database_runTracking_Timestamp(ServerCommandSource source, String varName) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        Main.config.setProperty(varName, String.valueOf(RunInfoParser.getRunLength(server)));
        return 1;
    }
    public static int database_runTracking_Players(ServerCommandSource source, String varName) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        if (!varName.equalsIgnoreCase("runners") && !varName.equalsIgnoreCase("finishers")) return -1;
        String players = "";
        List<PlayerEntity> playersList = varName.equalsIgnoreCase("runners")?RunInfoParser.getCurrentRunners(server):RunInfoParser.getCurrentAliveRunners(server);
        if (playersList.isEmpty()) {
            Main.config.setProperty(varName, "");
            return -1;
        }
        for (PlayerEntity player : playersList) {
            players += ConfigManager.VALUE_SEPARATOR+ player.getUuidAsString();
        }
        if (players.startsWith(ConfigManager.VALUE_SEPARATOR)) players = players.replaceFirst(ConfigManager.VALUE_SEPARATOR,"");
        Main.config.setProperty(varName, players);

        return 1;
    }
    public static int database_runTracking_PrepareForRun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        ConfigManager.resetRunInfo();
        return 1;
    }
}
