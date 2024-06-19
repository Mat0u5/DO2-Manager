package net.mat0u5.do2manager.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.DO2_GSON;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class DatabaseManager {
    private static final String DB_VERSION = "v.0.0.1";

    private static final String FOLDER_PATH = "./config/"+ Main.MOD_ID;
    private static final String FILE_PATH = FOLDER_PATH+"/mymod.db";
    private static final String URL = "jdbc:sqlite:"+FILE_PATH;

    public static void initialize() {
        createFolderIfNotExists();
        try (Connection connection = DriverManager.getConnection(URL)) {
            if (connection != null) {
                createPlayersTable(connection);
                createItemsTable(connection);
                createRunsTable(connection);
                createRunsDetailedTable(connection);
                createRunsSpeedrunsTable(connection);
                System.out.println("Database initialized.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }

    private static void createItemsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS items (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "db_version TEXT NOT NULL," +
                "player_uuid TEXT NOT NULL," +
                "item TEXT," +
                "FOREIGN KEY(player_uuid) REFERENCES players(uuid)" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
    private static void createRunsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS runs (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "db_version TEXT NOT NULL," +
                "run_number INTEGER NOT NULL UNIQUE," +
                "date TEXT NOT NULL," +
                "run_type TEXT," +
                "runners TEXT," +
                "finishers TEXT," +
                "run_length INTEGER" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
    private static void createRunsDetailedTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS runsDetailed (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "db_version TEXT NOT NULL," +
                "run_number INTEGER NOT NULL UNIQUE," +
                "card_plays TEXT," +
                "compass_item TEXT," +
                "artifact_item TEXT," +
                "deck_item TEXT," +
                "inventory_save TEXT," +
                "death_pos TEXT," +
                "death_cause TEXT," +
                "FOREIGN KEY(run_number) REFERENCES runs(run_number)" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
    private static void createRunsSpeedrunsTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS runsSpeedruns (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "db_version TEXT NOT NULL," +
                "run_number INTEGER NOT NULL UNIQUE," +
                "timestamp_lvl2_entry INTEGER," +
                "timestamp_lvl3_entry INTEGER," +
                "timestamp_lvl4_entry INTEGER," +
                "timestamp_lvl4_exit INTEGER," +
                "timestamp_lvl3_exit INTEGER," +
                "timestamp_lvl2_exit INTEGER," +
                "timestamp_lvl1_exit INTEGER," +
                "timestamp_artifact INTEGER," +
                "FOREIGN KEY(run_number) REFERENCES runs(run_number)" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }
    public static void updateTable() throws SQLException {
        /*
        try (Connection connection = DriverManager.getConnection(URL)) {
            if (connection != null) {
                String sql = "ALTER TABLE items ADD db_version TEXT;";
                PreparedStatement statement = connection.prepareStatement(sql);
                statement.executeUpdate();
                System.out.println("Database updated.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    public static void addPlayer(String uuid, String name) {
        String sql = "INSERT OR IGNORE INTO players(uuid, name, joined_at) VALUES(?, ?, datetime('now'))";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addItem(String playerUuid, ItemStack itemStack) {
        String sql = "INSERT INTO items(db_version, player_uuid, item) VALUES(?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DB_VERSION);
            statement.setString(2, playerUuid);
            statement.setString(3, DO2_GSON.serializeItemStack(itemStack));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addRun(int runNumber, String runType, String runners, String finishers, int runLength) {
        String sql = "INSERT INTO runs(db_version, run_number, date, run_type, runners, finishers, run_length) VALUES(?, ?, datetime('now'), ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DB_VERSION);
            statement.setInt(2, runNumber);
            statement.setString(3, runType);
            statement.setString(4, runners);
            statement.setString(5, finishers);
            statement.setInt(6, runLength);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addRunDetailed(int runNumber, String cardPlays, ItemStack compass, ItemStack artifact, ItemStack deck, PlayerEntity player, String deathPos, String deathCause) {
        String sql = "INSERT INTO runsDetailed(db_version, run_number, card_plays, compass_item, artifact_item, deck_item, inventory_save, death_pos, death_cause) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DB_VERSION);
            statement.setInt(2, runNumber);
            statement.setString(3, cardPlays);
            statement.setString(4, DO2_GSON.serializeItemStack(compass));
            statement.setString(5, DO2_GSON.serializeItemStack(artifact));
            statement.setString(6, DO2_GSON.serializeItemStack(deck));
            statement.setString(7, DO2_GSON.serializePlayerInventory(player));
            statement.setString(8, deathPos);
            statement.setString(9, deathCause);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void addRunSpeedrun(int runNumber, int lvl2_entry, int lvl3_entry, int lvl4_entry, int lvl4_exit, int lvl3_exit, int lvl2_exit, int lvl1_exit, int artiPickup) {
        String sql = "INSERT INTO runsSpeedruns(db_version, run_number, timestamp_lvl2_entry, timestamp_lvl3_entry, timestamp_lvl4_entry, timestamp_lvl4_exit, timestamp_lvl3_exit, timestamp_lvl2_exit, timestamp_lvl1_exit, timestamp_artifact) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DB_VERSION);
            statement.setInt(2, runNumber);
            statement.setInt(3, lvl2_entry);
            statement.setInt(4, lvl3_entry);
            statement.setInt(5, lvl4_entry);
            statement.setInt(6, lvl4_exit);
            statement.setInt(7, lvl3_exit);
            statement.setInt(8, lvl2_exit);
            statement.setInt(9, lvl1_exit);
            statement.setInt(10, artiPickup);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<ItemStack> getItemsByPlayerUUID(String playerUuid) {
        List<ItemStack> items = new ArrayList<>();
        String sql = "SELECT * FROM items WHERE player_uuid = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUuid);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String db_version = resultSet.getString("db_version");
                String item = resultSet.getString("item");

                ItemStack itemStack = DO2_GSON.deserializeItemStack(item);

                items.add(itemStack);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }
    public static List<ItemStack> getInvByRunNumber(PlayerEntity player, int runNumber) {
        List<ItemStack> items = new ArrayList<>();
        String sql = "SELECT * FROM runsDetailed WHERE run_number = ?";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, runNumber);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String db_version = resultSet.getString("db_version");
                String inv = resultSet.getString("inventory_save");
                return DO2_GSON.deserializePlayerInventory(player, inv);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }


    public static void printAllPlayers() {
        String sql = "SELECT * FROM players";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                System.out.println("Player " + resultSet.getString("name") + " joined at " + resultSet.getString("joined_at"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void printAllItems() {
        String sql = "SELECT * FROM items";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    System.out.println("Player UUID " + resultSet.getString("player_uuid") +
                            " has " + resultSet.getInt("quantity") +
                            " of item " + resultSet.getString("item_name"));
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static class DatabaseItem {
        private final String playerUuid;
        private final String itemName;
        private final int quantity;
        private final String nbtData;

        public DatabaseItem(String playerUuid, String itemName, int quantity, String nbtData) {
            this.playerUuid = playerUuid;
            this.itemName = itemName;
            this.quantity = quantity;
            this.nbtData = nbtData;
        }

        public String getPlayerUuid() {
            return playerUuid;
        }

        public String getItemName() {
            return itemName;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getNbtData() {
            return nbtData;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "playerUuid='" + playerUuid + '\'' +
                    ", itemName='" + itemName + '\'' +
                    ", quantity=" + quantity +
                    ", nbtData='" + nbtData + '\'' +
                    '}';
        }
    }
}
