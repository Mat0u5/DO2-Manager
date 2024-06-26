package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.CommandBlockScanner;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.FunctionScanner;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseCommand {
    public static int executeGetFromDB(ServerCommandSource source, int runNum, String query) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        DO2Run run = DatabaseManager.getRunByRunNumber(runNum);
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
        CommandBlockScanner.scanArea(server.getOverworld(),new BlockPos(fromX, fromY, fromZ),new BlockPos(toX, toY, toZ), source.getPlayer());
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

}
