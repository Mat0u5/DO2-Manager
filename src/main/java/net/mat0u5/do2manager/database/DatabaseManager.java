package net.mat0u5.do2manager.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.ScoreboardUtils;
import net.mat0u5.do2manager.world.CommandBlockData;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.utils.DO2_GSON;
import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.component.ComponentMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import org.sqlite.SQLiteErrorCode;

public class DatabaseManager {
    public static final String DB_VERSION = "v.1.1.0";

    private static final String FOLDER_PATH = "./config/"+ Main.MOD_ID;
    private static final String FILE_PATH = FOLDER_PATH+"/"+Main.MOD_ID+".db";
    public static final String URL = "jdbc:sqlite:"+FILE_PATH;
    private static final Gson GSON = new Gson();

    public static void initialize() {
        System.out.println("Initializing database");
        try {
            createFolderIfNotExists();
            try (Connection connection = DriverManager.getConnection(URL)) {
                if (connection != null) {
                    System.out.println("Connection established successfully.");
                    if (isFirstStartup()) {
                        createRunsTable(connection);
                        createRunsDetailedTable(connection);
                        createRunsSpeedrunsTable(connection);
                        createCommandBlocksTable(connection);
                        createFunctionsTable(connection);
                        createPlayersTable(connection);
                        createTCGTable(connection);
                    }
                    System.out.println("Database initialized.");
                } else {
                    System.err.println("Failed to establish connection.");
                }
            } catch (SQLException e) {
                System.err.println("SQLException encountered during initialization: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Unexpected error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
    public static boolean isFirstStartup() {
        String lastRecordedDBVersion = Main.config.getProperty("db_version");
        if (DB_VERSION.equalsIgnoreCase(lastRecordedDBVersion)) return false;
        if (lastRecordedDBVersion == null || lastRecordedDBVersion.isEmpty()) {
            return true;
        }
        return false;
    }
    public static void checkForDBUpdates() {
        String lastRecordedDBVersion = Main.config.getProperty("db_version");
        if (DB_VERSION.equalsIgnoreCase(lastRecordedDBVersion)) return;
        if (lastRecordedDBVersion == null || lastRecordedDBVersion.isEmpty()) {
            Main.config.setProperty("db_version",DB_VERSION);
            return;
        }
        try {
            updateTable();
        }catch(Exception e) {}
    }
    private static void createFolderIfNotExists() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }
    private static void createPlayersTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS players (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "uuid TEXT NOT NULL UNIQUE," +
                "name TEXT NOT NULL," +
                "joined_at TEXT NOT NULL" +
                "game_profile TEXT" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
    private static void createCommandBlocksTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS command_blocks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "x INTEGER NOT NULL," +
                "y INTEGER NOT NULL," +
                "z INTEGER NOT NULL," +
                "type TEXT NOT NULL," +
                "conditional BOOLEAN NOT NULL," +
                "auto BOOLEAN NOT NULL," +
                "command TEXT NOT NULL" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
    private static void createRunsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS runs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "db_version TEXT NOT NULL," +
                "run_number INTEGER NOT NULL," +
                "date TEXT NOT NULL," +
                "run_type TEXT," +
                "runners TEXT," +
                "finishers TEXT," +
                "run_length INTEGER" +
                "embers_counted INTEGER" +
                "crowns_counted INTEGER" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
    private static void createRunsDetailedTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS runsDetailed (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "db_version TEXT NOT NULL," +
                "run_number INTEGER NOT NULL," +
                "card_plays TEXT," +
                "difficulty INTEGER," +
                "compass_item TEXT," +
                "artifact_item TEXT," +
                "deck_item TEXT," +
                "inventory_save TEXT," +
                "items_bought TEXT," +
                "death_pos TEXT," +
                "death_message TEXT," +
                "loot_drops TEXT," +
                "special_events TEXT," +
                "FOREIGN KEY(run_number) REFERENCES runs(run_number)" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
    private static void createRunsSpeedrunsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS runsSpeedruns (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "db_version TEXT NOT NULL," +
                "run_number INTEGER NOT NULL," +
                "timestamp_lvl2_entry INTEGER," +
                "timestamp_lvl3_entry INTEGER," +
                "timestamp_lvl4_entry INTEGER," +
                "timestamp_lvl4_exit INTEGER," +
                "timestamp_lvl3_exit INTEGER," +
                "timestamp_lvl2_exit INTEGER," +
                "timestamp_lvl1_exit INTEGER," +
                "timestamp_artifact INTEGER," +
                "run_length INTEGER," +
                "FOREIGN KEY(run_number) REFERENCES runs(run_number)" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
    private static void createFunctionsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS functions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "file_path TEXT NOT NULL," +
                "function_name TEXT NOT NULL," +
                "function_content TEXT NOT NULL" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
    private static void createTCGTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE \"tcg_items\" (\"id\" INTEGER,\"db_version\" TEXT,\"item\" TEXT,PRIMARY KEY(\"id\" AUTOINCREMENT));";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
    public static void updateTable() throws SQLException {
        String old_db_ver = Main.config.getProperty("db_version");
        HashMap<List<String>,List<String>> versionUpdates = new HashMap<>();

        versionUpdates.put(List.of("v.1.0.0","v.1.0.1","v.1.0.2"),List.of("v.1.0.3","ALTER TABLE runs ADD embers_counted INTEGER;"));
        versionUpdates.put(List.of("v.1.0.3"),List.of("v.1.0.4",""));
        versionUpdates.put(List.of("v.1.0.4"),List.of("v.1.0.5","ALTER TABLE runsDetailed ADD loot_drops TEXT; ALTER TABLE runsDetailed ADD special_events TEXT;"));
        versionUpdates.put(List.of("v.1.0.5"),List.of("v.1.0.6","ALTER TABLE runs ADD crowns_counted INTEGER;"));
        versionUpdates.put(List.of("v.1.0.6"),List.of("v.1.0.7","CREATE TABLE \"tcg_items\" (\"id\" INTEGER,\"db_version\" TEXT,\"item\" TEXT,PRIMARY KEY(\"id\" AUTOINCREMENT));"));
        versionUpdates.put(List.of("v.1.0.7"),List.of("v.1.1.0","ALTER TABLE players ADD game_profile TEXT;"));
        //
        try (Connection connection = DriverManager.getConnection(URL)) {
            if (connection != null) {
                String sql = "";

                for (List<String> updateVersion : versionUpdates.keySet()) {
                    System.out.println(updateVersion);
                    if (updateVersion.contains(old_db_ver)) {
                        List<String> val = versionUpdates.get(updateVersion);
                        System.out.println("Updating database from " + old_db_ver + " to " + val.get(0) + " ("+val.get(1)+")");
                        old_db_ver = val.get(0);
                        sql = val.get(1);
                    }
                }
                if (!sql.isEmpty()) {
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.executeUpdate();
                }
                Main.config.setProperty("db_version",old_db_ver);
                System.out.println("Database updated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteAllCommandBlocks() {
        String sql = "DELETE FROM command_blocks";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteAllFunctions() {
        String sql = "DELETE FROM functions";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void deleteTCGItems() {
        String sql = "DELETE FROM tcg_items";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addRun(DO2Run run) {
        String sql = "INSERT INTO runs(db_version, run_number, date, run_type, runners, finishers, run_length, embers_counted, crowns_counted) VALUES(?, ?, datetime('now'), ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DB_VERSION);
            statement.setInt(2, run.run_number);
            statement.setString(3, run.run_type);
            statement.setString(4, String.join(",", run.runners));
            statement.setString(5, String.join(",", run.finishers));
            statement.setInt(6, run.run_length);
            statement.setInt(7, run.embers_counted);
            statement.setInt(8, run.crowns_counted);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addRunDetailed(DO2Run run) {
        String sql = "INSERT INTO runsDetailed(db_version, run_number, card_plays, difficulty, compass_item, artifact_item, deck_item, inventory_save, items_bought, death_pos, death_message, loot_drops, special_events) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DB_VERSION);
            statement.setInt(2, run.run_number);
            statement.setString(3, DO2_GSON.serializeListItemStack(run.card_plays));
            statement.setInt(4, run.difficulty);
            statement.setString(5, DO2_GSON.serializeItemStack(run.compass_item));
            statement.setString(6, DO2_GSON.serializeItemStack(run.artifact_item));
            statement.setString(7, DO2_GSON.serializeItemStack(run.deck_item));
            statement.setString(8, DO2_GSON.serializeListItemStack(run.inventory_save));
            statement.setString(9, DO2_GSON.serializeListItemStack(ItemManager.combineItemStacks(run.items_bought)));
            statement.setString(10, run.death_pos);
            statement.setString(11, run.death_message);
            statement.setString(12, String.join(",", run.loot_drops));
            statement.setString(13, String.join(",", run.special_events));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addRunSpeedrun(DO2Run run) {
        String sql = "INSERT INTO runsSpeedruns(db_version, run_number, timestamp_lvl2_entry, timestamp_lvl3_entry, timestamp_lvl4_entry, timestamp_lvl4_exit, timestamp_lvl3_exit, timestamp_lvl2_exit, timestamp_lvl1_exit, timestamp_artifact, run_length) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DB_VERSION);
            statement.setInt(2, run.run_number);
            statement.setInt(3, run.timestamp_lvl2_entry);
            statement.setInt(4, run.timestamp_lvl3_entry);
            statement.setInt(5, run.timestamp_lvl4_entry);
            statement.setInt(6, run.timestamp_lvl4_exit);
            statement.setInt(7, run.timestamp_lvl3_exit);
            statement.setInt(8, run.timestamp_lvl2_exit);
            statement.setInt(9, run.timestamp_lvl1_exit);
            statement.setInt(10, run.timestamp_artifact);
            statement.setInt(11, run.run_length);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updateRunRun(DO2Run run) {
        String sql = "UPDATE runs SET run_type = ?, runners = ?, finishers = ?, run_length = ?, embers_counted = ?, crowns_counted = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, run.run_type);
            statement.setString(2, String.join(",", run.runners));
            statement.setString(3, String.join(",", run.finishers));
            statement.setInt(4, run.run_length);
            statement.setInt(5, run.embers_counted);
            statement.setInt(6, run.crowns_counted);
            statement.setInt(7, run.id);  // Use id in WHERE clause

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updateRunDetailed(DO2Run run) {
        String sql = "UPDATE runsDetailed SET card_plays = ?, difficulty = ?, compass_item = ?, artifact_item = ?, deck_item = ?, inventory_save = ?, items_bought = ?, death_pos = ?, death_message = ?, loot_drops = ?, special_events = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DO2_GSON.serializeListItemStack(run.card_plays));
            statement.setInt(2, run.difficulty);
            statement.setString(3, DO2_GSON.serializeItemStack(run.compass_item));
            statement.setString(4, DO2_GSON.serializeItemStack(run.artifact_item));
            statement.setString(5, DO2_GSON.serializeItemStack(run.deck_item));
            statement.setString(6, DO2_GSON.serializeListItemStack(run.inventory_save));
            statement.setString(7, DO2_GSON.serializeListItemStack(run.items_bought));
            statement.setString(8, run.death_pos);
            statement.setString(9, run.death_message);
            statement.setString(10, String.join(",", run.loot_drops));
            statement.setString(11, String.join(",", run.special_events));
            statement.setInt(12, run.id);  // Use id in WHERE clause

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updateRunSpeedrun(DO2Run run) {
        String sql = "UPDATE runsSpeedruns SET timestamp_lvl2_entry = ?, timestamp_lvl3_entry = ?, timestamp_lvl4_entry = ?, timestamp_lvl4_exit = ?, timestamp_lvl3_exit = ?, timestamp_lvl2_exit = ?, timestamp_lvl1_exit = ?, timestamp_artifact = ?, run_length = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, run.timestamp_lvl2_entry);
            statement.setInt(2, run.timestamp_lvl3_entry);
            statement.setInt(3, run.timestamp_lvl4_entry);
            statement.setInt(4, run.timestamp_lvl4_exit);
            statement.setInt(5, run.timestamp_lvl3_exit);
            statement.setInt(6, run.timestamp_lvl2_exit);
            statement.setInt(7, run.timestamp_lvl1_exit);
            statement.setInt(8, run.timestamp_artifact);
            statement.setInt(9, run.run_length);
            statement.setInt(10, run.id);  // Use id in WHERE clause

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addTCGItem(ItemStack itemStack) {
        String sql = "INSERT INTO tcg_items(db_version, item) VALUES(?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DB_VERSION);
            statement.setString(2, DO2_GSON.serializeItemStack(itemStack));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static List<ItemStack> getAllTCGItems() {
        List<ItemStack> result = new ArrayList<>();
        String sql = "SELECT * from tcg_items";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String itemStr = resultSet.getString("item");
                String db_version = resultSet.getString("db_version");
                result.add(DO2_GSON.deserializeItemStack(itemStr,db_version));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static List<DO2RunAbridged> fetchAbridgedRuns(String sql, List<Object> parameters) {
        List<DO2RunAbridged> runsDictionary = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Set the parameters
            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                DO2RunAbridged run = new DO2RunAbridged();
                //String db_version = resultSet.getString("db_version");
                run.id = resultSet.getInt("id");
                run.run_number = resultSet.getInt("run_number");
                run.date = resultSet.getString("date");
                run.run_type = resultSet.getString("run_type");
                run.runners = List.of(resultSet.getString("runners").split(","));
                run.finishers = List.of(resultSet.getString("finishers").split(","));
                run.run_length = resultSet.getInt("run_length");
                run.embers_counted = resultSet.getInt("embers_counted");
                run.crowns_counted = resultSet.getInt("crowns_counted");
                run.difficulty = resultSet.getInt("difficulty");
/*
                ItemStack compass_item = DO2_GSON.deserializeItemStack(resultSet.getString("compass_item"),db_version);
                if (compass_item != null)  {
                    run.compass_level= ItemManager.getCustomComponentInt(compass_item,"Level");
                }*/

                runsDictionary.add(run);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return runsDictionary;
    }
    public static DO2Run getRunByRunId(int id) {
        String sql = "SELECT r.*, rd.*, rs.* FROM runs r " +
                "LEFT JOIN runsDetailed rd ON r.id = rd.id " +
                "LEFT JOIN runsSpeedruns rs ON r.id = rs.id " +
                "WHERE r.id = ?";

        List<DO2Run> runs = fetchRuns(sql, List.of(id));
        return runs.isEmpty() ? null : runs.get(0); // Return the first run, or null if none found
    }
    public static List<DO2Run> getRunsByAbridgedRuns(List<DO2RunAbridged> runNumbers, AtomicReference<PreparedStatement> currentStatementRef) {
        if (runNumbers == null || runNumbers.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder whereClause = new StringBuilder("WHERE ");
        for (int i = 0; i < runNumbers.size(); i++) {
            DO2RunAbridged abridged = runNumbers.get(i);
            whereClause.append("(r.id = ")
                    .append(abridged.id)
                    .append(")");
            if (i < runNumbers.size() - 1) {
                whereClause.append(" OR ");
            }
        }

        StringBuilder orderByClause = new StringBuilder("ORDER BY CASE");
        for (int i = 0; i < runNumbers.size(); i++) {
            orderByClause.append(" WHEN r.id = ").append(runNumbers.get(i).id)
                    .append(" THEN ").append(i);
        }
        orderByClause.append(" END");

        String sql = "SELECT r.*, rd.*, rs.* FROM runs r " +
                "LEFT JOIN runsDetailed rd ON r.id = rd.id " +
                "LEFT JOIN runsSpeedruns rs ON r.id = rs.id " +
                whereClause.toString() + " " +
                orderByClause.toString();

        return fetchRuns(sql, new ArrayList<>(), currentStatementRef);
    }

    // Original method without cancellation support
    public static List<DO2Run> getRunsByAbridgedRuns(List<DO2RunAbridged> runNumbers) {
        return getRunsByAbridgedRuns(runNumbers, null);
    }

    // Overloaded method with cancellation support
    private static List<DO2Run> fetchRuns(String sql, List<Object> parameters,
                                          AtomicReference<PreparedStatement> currentStatementRef) {
        List<DO2Run> runsDictionary = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            // Store the current statement if cancellation is supported
            if (currentStatementRef != null) {
                currentStatementRef.set(statement);
            }

            for (int i = 0; i < parameters.size(); i++) {
                statement.setObject(i + 1, parameters.get(i));
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                String db_version = resultSet.getString("db_version");

                DO2Run run = new DO2Run();
                run.id = resultSet.getInt("id");
                run.run_number = resultSet.getInt("run_number");
                run.date = resultSet.getString("date");
                run.run_type = resultSet.getString("run_type");
                run.runners = List.of(resultSet.getString("runners").split(","));
                run.finishers = List.of(resultSet.getString("finishers").split(","));
                run.run_length = resultSet.getInt("run_length");
                run.embers_counted = resultSet.getInt("embers_counted");
                run.crowns_counted = resultSet.getInt("crowns_counted");

                // Populate runsDetailed fields
                run.card_plays = DO2_GSON.deserializeListItemStack(resultSet.getString("card_plays"),db_version);
                run.difficulty = resultSet.getInt("difficulty");
                run.compass_item = DO2_GSON.deserializeItemStack(resultSet.getString("compass_item"),db_version);
                run.artifact_item = DO2_GSON.deserializeItemStack(resultSet.getString("artifact_item"),db_version);
                run.deck_item = DO2_GSON.deserializeItemStack(resultSet.getString("deck_item"),db_version);
                run.inventory_save = DO2_GSON.deserializeListItemStack(resultSet.getString("inventory_save"),db_version);
                run.items_bought = DO2_GSON.deserializeListItemStack(resultSet.getString("items_bought"),db_version);
                run.death_pos = resultSet.getString("death_pos");
                run.death_message = resultSet.getString("death_message");
                if (resultSet.getString("loot_drops") != null)
                    run.loot_drops = List.of(resultSet.getString("loot_drops").split(","));
                if (resultSet.getString("special_events") != null)
                    run.special_events = List.of(resultSet.getString("special_events").split(","));

                // Populate runsSpeedruns fields
                run.timestamp_lvl2_entry = resultSet.getInt("timestamp_lvl2_entry");
                run.timestamp_lvl3_entry = resultSet.getInt("timestamp_lvl3_entry");
                run.timestamp_lvl4_entry = resultSet.getInt("timestamp_lvl4_entry");
                run.timestamp_lvl4_exit = resultSet.getInt("timestamp_lvl4_exit");
                run.timestamp_lvl3_exit = resultSet.getInt("timestamp_lvl3_exit");
                run.timestamp_lvl2_exit = resultSet.getInt("timestamp_lvl2_exit");
                run.timestamp_lvl1_exit = resultSet.getInt("timestamp_lvl1_exit");
                run.timestamp_artifact = resultSet.getInt("timestamp_artifact");

                runsDictionary.add(run);
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_INTERRUPT.code) {
                System.out.println("Query was interrupted.");
            } else {
                e.printStackTrace(); // Handle other SQLExceptions as needed
            }
        } finally {
            // Clear the reference after execution if cancellation is supported
            if (currentStatementRef != null) {
                currentStatementRef.set(null);
            }
        }
        return runsDictionary;
    }

    // Original method without cancellation support
    private static List<DO2Run> fetchRuns(String sql, List<Object> parameters) {
        return fetchRuns(sql, parameters, null);
    }


    public static List<DO2Run> getRunsByCriteria(List<String> criteria) {
        String sql = "SELECT r.*, rd.*, rs.* FROM runs r " +
                "LEFT JOIN runsDetailed rd ON r.id = rd.id " +
                "LEFT JOIN runsSpeedruns rs ON r.id = rs.id " +
                (criteria.isEmpty() ? "" : "WHERE " + String.join(" AND ", criteria));

        return fetchRuns(sql, new ArrayList<>()); // No additional parameters needed
    }
    public static List<DO2RunAbridged> getAbridgedRunsByCriteria(List<String> criteria) {
        String sql = "SELECT r.*, rd.* FROM runs r " +
                "LEFT JOIN runsDetailed rd ON r.id = rd.id " +
                (criteria.isEmpty() ? "" : "WHERE " + String.join(" AND ", criteria));
        return fetchAbridgedRuns(sql, new ArrayList<>()); // No additional parameters needed
    }

    public static void addCommandBlocks(List<CommandBlockData> commandBlocks) {
        String sql = "INSERT INTO command_blocks(x, y, z, type, conditional, auto, command) VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {

            connection.setAutoCommit(false); // Disable auto-commit for batch processing

            for (CommandBlockData blockData : commandBlocks) {
                statement.setInt(1, blockData.getX());
                statement.setInt(2, blockData.getY());
                statement.setInt(3, blockData.getZ());
                statement.setString(4, blockData.getType());
                statement.setBoolean(5, blockData.isConditional());
                statement.setBoolean(6, blockData.isAuto());
                statement.setString(7, blockData.getCommand());
                statement.addBatch(); // Add to batch
            }

            statement.executeBatch(); // Execute all batched statements
            connection.commit(); // Commit the transaction

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void removeCommandBlock(BlockPos pos) {
        String sql = "DELETE FROM command_blocks WHERE x = ? AND y = ? AND z = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, pos.getX());
            statement.setInt(2, pos.getY());
            statement.setInt(3, pos.getZ());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updateCommandBlock(CommandBlockData blockData) {
        String sql = "UPDATE command_blocks SET type = ?, conditional = ?, auto = ?, command = ? WHERE x = ? AND y = ? AND z = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, blockData.getType());
            statement.setBoolean(2, blockData.isConditional());
            statement.setBoolean(3, blockData.isAuto());
            statement.setString(4, blockData.getCommand());
            statement.setInt(5, blockData.getX());
            statement.setInt(6, blockData.getY());
            statement.setInt(7, blockData.getZ());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addCommandBlock(CommandBlockData blockData) {
        String sql = "INSERT INTO command_blocks(x, y, z, type, conditional, auto, command) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, blockData.getX());
            statement.setInt(2, blockData.getY());
            statement.setInt(3, blockData.getZ());
            statement.setString(4, blockData.getType());
            statement.setBoolean(5, blockData.isConditional());
            statement.setBoolean(6, blockData.isAuto());
            statement.setString(7, blockData.getCommand());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static ResultSet runQuery(String sql) {
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
             return resultSet;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void saveRun(MinecraftServer server) {
        if (Main.currentRun.run_length == -1 || Main.currentRun.runners.isEmpty() || Main.currentRun.run_number == -1) {
            Main.resetRunInfo();
            return;
        }
        try {
            Main.currentRun.loot_drops = ScoreboardUtils.getLootEvents();
            Main.currentRun.sendInfoToDiscord();
            Main.addRun(Main.currentRun);
            Main.simulator.finishedRun();

            DatabaseManager.addRun(Main.currentRun);
            DatabaseManager.addRunDetailed(Main.currentRun);
            DatabaseManager.addRunSpeedrun(Main.currentRun);
            System.out.println("Run Saved to Database.");

        }catch(Exception e) {
            System.out.println(e);
        }
        Main.resetRunInfo();
    }
    public static void updateRun(DO2Run run) {
        try {
            DatabaseManager.updateRunRun(run);
            DatabaseManager.updateRunDetailed(run);
            DatabaseManager.updateRunSpeedrun(run);
            System.out.println("Run ID:"+run.run_number+" has been updated in the database.");
        }catch(Exception e) {
            System.out.println(e);
        }
        Main.resetRunInfo();
    }

    public static void addPlayer(String uuid, String name, GameProfile gameProfile) {
        String sql = "INSERT OR REPLACE INTO players(uuid, name, joined_at) VALUES(?, ?, datetime('now'))";//game_profile, ?
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, name);
            /*
            String game_profile = GSON.toJson(gameProfile.getProperties(), PropertyMap.class);
            statement.setString(3, game_profile);*/
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static String getPlayerNameFromUUID(String uuid) {
        String sql = "SELECT name FROM players WHERE uuid = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getPlayerNameCaseCorrected(String name) {
        String sql = "SELECT name FROM players WHERE LOWER(name) = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.toLowerCase());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("name");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String getPlayerUUIDFromName(String name) {
        String sql = "SELECT uuid FROM players WHERE LOWER(name) = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name.toLowerCase());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("uuid");
                } else {
                    return null;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void fetchAllPlayers() {
        Main.allPlayers.clear();
        String sql = "SELECT uuid, name FROM players";//game_profile
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                String name = resultSet.getString("name");
                /*
                String game_profile = resultSet.getString("game_profile");
                if (game_profile != null) {
                    if (!game_profile.isEmpty()) {
                        PropertyMap properties = GSON.fromJson(game_profile, PropertyMap.class);
                        Main.allPlayerProfiles.put(uuid, properties);
                    }
                }*/
                Main.allPlayers.put(uuid, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
