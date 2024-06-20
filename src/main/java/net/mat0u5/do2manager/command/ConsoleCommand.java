package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.config.ConfigManager;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.predicate.entity.LocationPredicate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
        String card_plays = Main.config.getProperty("card_plays");
        String compass_item = Main.config.getProperty("compass_item");
        String artifact_item = Main.config.getProperty("artifact_item");
        String deck_item = Main.config.getProperty("deck_item");
        String inventory_save = Main.config.getProperty("inventory_save");
        String death_pos = Main.config.getProperty("death_pos");
        String death_cause = Main.config.getProperty("death_cause");
        int run_number = stringToInt(Main.config.getProperty("run_number"));
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
        DatabaseManager.addRunDetailed(run_number, card_plays, compass_item,artifact_item,deck_item,inventory_save,death_pos,death_cause);
        DatabaseManager.addRunSpeedrun(run_number, timestamp_lvl2_entry,timestamp_lvl3_entry,timestamp_lvl4_entry,timestamp_lvl4_exit,timestamp_lvl3_exit,timestamp_lvl2_exit,timestamp_lvl1_exit,timestamp_artifact);
        return 1;
    }
    public static int database_runTracking_ItemCompass(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        return 1;
    }
    public static int database_runTracking_ItemDeck(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        return 1;
    }
    public static int database_runTracking_ItemInventory(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        return 1;
    }
    public static int database_runTracking_Timestamp(ServerCommandSource source, String varName) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        return 1;
    }
    public static int database_runTracking_Players(ServerCommandSource source, String varName) {
        MinecraftServer server = source.getServer();
        if (isRanByPlayer(source)) return -1;

        if (varName == "runners") {
            String runners = "";
            for (PlayerEntity runner : RunInfoParser.getCurrentRunners(server)) {
                runners += ConfigManager.VALUE_SEPARATOR+ runner.getUuidAsString();
            }
            if (runners.startsWith(ConfigManager.VALUE_SEPARATOR)) runners = runners.replaceFirst(ConfigManager.VALUE_SEPARATOR,"");
            Main.config.setProperty("runners", runners);
        }
        if (varName == "finishers") {
            String finishers = "";
            for (PlayerEntity runner : RunInfoParser.getCurrentRunners(server)) {
                if (!runner.isSpectator() && !runner.isCreative() && RunInfoParser.isInCitadelRegion(runner)) {
                    finishers += ConfigManager.VALUE_SEPARATOR+ runner.getUuidAsString();
                }
            }
            if (finishers.startsWith(ConfigManager.VALUE_SEPARATOR)) finishers = finishers.replaceFirst(ConfigManager.VALUE_SEPARATOR,"");
            Main.config.setProperty("finishers", finishers);
        }
        return 1;
    }
}
