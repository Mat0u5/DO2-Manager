package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DO2RunIterator;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.BlockScanner;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.FunctionScanner;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DatabaseCommand {
    public static int executeGetFromDB(ServerCommandSource source, int runId, String query) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        DO2Run run = DatabaseManager.getRunByRunId(runId);
        if (run == null) {
            self.sendMessage(Text.translatable("No run found!"));
            return -1;
        }
        try {
            if (query.equalsIgnoreCase("inventory_save")) ItemManager.giveItemStack(self,run.inventory_save);
            if (query.equalsIgnoreCase("items_bought")) ItemManager.giveItemStack(self,run.items_bought);
            if (query.equalsIgnoreCase("card_plays")) ItemManager.giveItemStack(self,run.card_plays);
            if (query.equalsIgnoreCase("artifact_item")) ItemManager.giveItemStack(self,run.artifact_item);
            if (query.equalsIgnoreCase("compass_item")) ItemManager.giveItemStack(self,run.compass_item);
            if (query.equalsIgnoreCase("deck_item")) ItemManager.giveItemStack(self,run.deck_item);
            if (query.equalsIgnoreCase("death_message")) self.sendMessage(Text.translatable(run.death_message));
            if (query.equalsIgnoreCase("death_pos")) self.sendMessage(Text.translatable(run.death_pos));
            if (query.equalsIgnoreCase("run_type")) self.sendMessage(Text.translatable(run.run_type));
            if (query.equalsIgnoreCase("runners")) self.sendMessage(Text.translatable(String.join(", ", run.runners)));
            if (query.equalsIgnoreCase("finishers")) self.sendMessage(Text.translatable(String.join(", ", run.finishers)));
            if (query.equalsIgnoreCase("difficulty")) self.sendMessage(Text.translatable(String.valueOf(run.difficulty)));
            if (query.equalsIgnoreCase("run_number")) self.sendMessage(Text.translatable(String.valueOf(run.run_number)));
            if (query.equalsIgnoreCase("run_length")) self.sendMessage(Text.translatable(String.valueOf(run.run_length)));
            if (query.equalsIgnoreCase("timestamp_artifact")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_artifact)));
            if (query.equalsIgnoreCase("timestamp_lvl2_entry")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl2_entry)));
            if (query.equalsIgnoreCase("timestamp_lvl3_entry")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl3_entry)));
            if (query.equalsIgnoreCase("timestamp_lvl4_entry")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl4_entry)));
            if (query.equalsIgnoreCase("timestamp_lvl4_exit")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl4_exit)));
            if (query.equalsIgnoreCase("timestamp_lvl3_exit")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl3_exit)));
            if (query.equalsIgnoreCase("timestamp_lvl2_exit")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl2_exit)));
            if (query.equalsIgnoreCase("timestamp_lvl1_exit")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl1_exit)));
        }catch(Exception e) {}

        return 1;
    }
    public static int executeCommandBlockSearch(ServerCommandSource source, String query, String searchType) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        query = OtherUtils.removeQuotes(query);
        if (query.startsWith("/"))query = query.substring(1);

        String origQuery = query + "";

        String sqlQuery;
        switch (searchType.toLowerCase()) {
            case "startswith":
                sqlQuery = "SELECT * FROM command_blocks WHERE command LIKE ?";
                query = query + "%";
                break;
            case "endswith":
                sqlQuery = "SELECT * FROM command_blocks WHERE command LIKE ?";
                query = "%" + query;
                break;
            case "contains":
            default:
                sqlQuery = "SELECT * FROM command_blocks WHERE command LIKE ?";
                query = "%" + query + "%";
                break;
        }

        try (Connection connection = DriverManager.getConnection(DatabaseManager.URL);
             PreparedStatement statement = connection.prepareStatement(sqlQuery)) {

            statement.setString(1, query);
            ResultSet resultSet = statement.executeQuery();

            List<Text> results = new ArrayList<>();
            boolean containsAtLeastOne = false;
            while (resultSet.next()) {
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                int z = resultSet.getInt("z");
                String type = resultSet.getString("type");
                String command = resultSet.getString("command");
                boolean conditional = resultSet.getBoolean("conditional");
                boolean auto = resultSet.getBoolean("auto");
                BlockPos pos = new BlockPos(x, y, z);
                Text positionText = Text.translatable(String.format("§6(%d, %d, %d)", x, y, z))
                        .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        String.format("/tp @s %d %d %d", x, y, z)))
                                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                                        Text.of("Teleport to this position"))));
                Text finalText = Text.translatable("§a- Pos: ").append(positionText).append(Text.translatable(" §aType: "+type+", Conditional: "+(conditional ? "Yes" : "No")+", Auto: "+(auto ? "Always Active" : "Needs Redstone")+", §bCommand: "+command+"\n"));
                results.add(finalText);
                containsAtLeastOne = true;
            }
            if (!containsAtLeastOne) {
                self.sendMessage(Text.of("§c No Command Blocks Found!"), false);
            }
            else {
                self.sendMessage(Text.of("Command Blocks matching the query:"), false);
                for (Text text : results) {
                    self.sendMessage(text);
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<String> functions = FunctionScanner.findFunctionsContaining(origQuery, searchType);
        if (!functions.isEmpty()) {
            self.sendMessage(Text.of("Functions matching the query:"), false);
            for (String text : functions) {
                self.sendMessage(Text.of("§a -" + text));
            }
        }
        return 1;
    }
    public static int executeCommandBlockUpdateDatabase(ServerCommandSource source, int fromX, int fromY, int fromZ, int toX, int toY, int toZ) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        self.sendMessage(Text.of("Deleting all stored command block data..."));
        DatabaseManager.deleteAllCommandBlocks();
        self.sendMessage(Text.of("Started Command Block Search..."));
        new BlockScanner().scanArea("command_block",server.getOverworld(),new BlockPos(fromX, fromY, fromZ),new BlockPos(toX, toY, toZ), source.getPlayer());
        return 1;
    }
    public static int executeFunctionUpdateDatabase(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        self.sendMessage(Text.of("Deleting all stored function data..."));
        DatabaseManager.deleteAllFunctions();
        self.sendMessage(Text.of("Started Function Search..."));
        FunctionScanner.scanFunctions();
        return 1;
    }
    public static int updateCrowns() {
        DO2RunIterator iterator = new DO2RunIterator() {
            @Override
            public void processRun(DO2Run run) {
                boolean modified = false;
                if (run.getSuccess()) {
                    run.crowns_counted = run.getCrownsFromInv();
                    modified = true;
                }
                else {
                    run.crowns_counted = 0;
                    modified = true;
                }
                if (modified) DatabaseManager.updateRun(run);
            }
        };
        iterator.start();
        return 1;
    }
    public static int updateLackeys() {
        DO2RunIterator iterator = new DO2RunIterator() {
            @Override
            public void processRun(DO2Run run) {
                boolean modified = false;
                if (run.isLackey() && !run.getSuccess()) {
                    if (run.embers_counted != 0) {
                        if (run.runners.contains("41682eb6-2b32-4f52-abc9-c15a9d53c83e")) {
                            run.finishers = List.of("41682eb6-2b32-4f52-abc9-c15a9d53c83e");
                        }
                        else if (run.run_number == 10182) {
                            run.finishers = List.of("0f8865f3-a9fd-406c-8c42-354887ad3891");
                        }
                        else if (run.run_number == 10239) {
                            run.finishers = List.of("3a491cb4-ba7d-4a4c-96df-d78a397c0bff");
                        }
                        else if (run.run_number == 10548) {
                            run.finishers = List.of("0f8865f3-a9fd-406c-8c42-354887ad3891");
                        }
                        else if (run.run_number == 10707) {
                            run.finishers = List.of("3a491cb4-ba7d-4a4c-96df-d78a397c0bff");
                        }
                        else if (run.run_number == 10734) {
                            run.finishers = List.of("3a491cb4-ba7d-4a4c-96df-d78a397c0bff");
                        }
                        else if (run.run_number == 10782) {
                            run.finishers = List.of("983895d7-82b6-4bcb-a8ad-17d93245e0a4");
                        }
                        else if (run.run_number == 10798) {
                            run.finishers = List.of("8b77df27-95ea-4c62-a12e-d5b9e19fc50b");
                        }
                        else if (run.run_number == 11075) {
                            run.finishers = List.of("3a491cb4-ba7d-4a4c-96df-d78a397c0bff");
                        }
                        else if (run.run_number == 11241) {
                            run.finishers = List.of("8b77df27-95ea-4c62-a12e-d5b9e19fc50b");
                        }
                        else if (run.run_number == 11471) {
                            run.finishers = List.of("3a491cb4-ba7d-4a4c-96df-d78a397c0bff");
                        }
                        else if (run.run_number == 11503) {
                            run.finishers = List.of("3a491cb4-ba7d-4a4c-96df-d78a397c0bff", "d2317734-f9d0-4819-b503-4e3fb1fd1f2c", "722b3035-7c50-4f31-bdcd-f8c82c8f38cb");
                        }
                        else if (run.run_number == 11594) {
                            run.finishers = List.of("2c4ace91-7b4b-4215-8dc8-2b79335be2e3");
                        }
                        else if (run.run_number == 12236) {
                            run.finishers = List.of("3a491cb4-ba7d-4a4c-96df-d78a397c0bff");
                        }
                        else if (run.run_number == 12240) {
                            run.finishers = List.of("3a491cb4-ba7d-4a4c-96df-d78a397c0bff");
                        }
                        else if (run.run_number == 12474) {
                            run.finishers = List.of("5de9d3bc-8fa8-48eb-a6a4-d78094973ce2");
                        }
                        else if (run.run_number == 12519) {
                            run.finishers = List.of("5de9d3bc-8fa8-48eb-a6a4-d78094973ce2");
                        }
                        else {
                            System.out.println(run.runners);
                            System.out.println(run.getRunnersName());
                        }
                        modified = true;
                    }
                }
                if (modified) DatabaseManager.updateRun(run);
            }
        };
        iterator.start();
        return 1;
    }
    public static int updateTotalEmbers() {
        OtherUtils.executeCommand(Main.server,"scoreboard objectives remove OverallEmbers");
        OtherUtils.executeCommand(Main.server,"scoreboard objectives add OverallEmbers dummy");
        HashMap<String,Integer> totalEmbers = new HashMap<String, Integer>();

        DO2RunIterator iterator = new DO2RunIterator() {
            @Override
            public void processRun(DO2Run run) {
                if (!run.getSuccess()) return;
                if (run.embers_counted == 0) return;
                String finishersStr = run.getFinishersName();
                int embers = (!finishersStr.contains(", ")?run.embers_counted:(run.embers_counted/finishersStr.split(", ").length));

                for (String finisher : finishersStr.split(", ")) {
                    if (!totalEmbers.containsKey(finisher)) {
                        totalEmbers.put(finisher, embers);
                    }
                    else {
                        totalEmbers.put(finisher, totalEmbers.get(finisher)+embers);
                    }
                }
            }
            @Override
            public void finishedProcessing() {
                System.out.println("Updating scoreboards...");
                for (String playerName : totalEmbers.keySet()) {
                    int embers = totalEmbers.get(playerName);
                    OtherUtils.executeCommand(Main.server,"scoreboard players set "+playerName+" OverallEmbers "+embers);
                }
                System.out.println("Scoreboards Updated.");
            }
        };
        iterator.start();
        return 1;
    }
    public static int updateTotalCrowns() {
        OtherUtils.executeCommand(Main.server,"scoreboard objectives remove OverallCrowns");
        OtherUtils.executeCommand(Main.server,"scoreboard objectives add OverallCrowns dummy");
        HashMap<String,Integer> totalCrowns = new HashMap<String, Integer>();
        DO2RunIterator iterator = new DO2RunIterator() {
            @Override
            public void processRun(DO2Run run) {
                if (!run.getSuccess()) return;
                if (run.crowns_counted == 0) return;
                String finishersStr = run.getFinishersName();
                int crowns = (!finishersStr.contains(", ")?run.crowns_counted:(run.crowns_counted/finishersStr.split(", ").length));

                for (String finisher : finishersStr.split(", ")) {
                    if (!totalCrowns.containsKey(finisher)) {
                        totalCrowns.put(finisher, crowns);
                    }
                    else {
                        totalCrowns.put(finisher, totalCrowns.get(finisher)+crowns);
                    }
                }
            }
            @Override
            public void finishedProcessing() {
                System.out.println("Updating scoreboards...");
                for (String playerName : totalCrowns.keySet()) {
                    int crowns = totalCrowns.get(playerName);
                    OtherUtils.executeCommand(Main.server,"scoreboard players set "+playerName+" OverallCrowns "+crowns);
                }
                System.out.println("Scoreboards Updated.");
            }
        };
        iterator.start();
        return 1;
    }
    public static int updateBoughtItems() {
        DO2RunIterator iterator = new DO2RunIterator() {
            @Override
            public void processRun(DO2Run run) {
                boolean modified = false;
                if (!run.items_bought.isEmpty()) {
                    List<ItemStack> newItems = ItemManager.combineItemStacks(run.items_bought);
                    run.items_bought = newItems;
                    modified = true;
                }
                if (modified) DatabaseManager.updateRun(run);
            }
        };
        iterator.start();
        return 1;
    }
}
