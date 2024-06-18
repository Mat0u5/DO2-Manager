package net.mat0u5.do2manager.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.item.Item;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:./mods/mymod.db";
    private static final Gson GSON = new Gson();

    public static void initialize() {
        try (Connection connection = DriverManager.getConnection(URL)) {
            if (connection != null) {
                createPlayersTable(connection);
                createItemsTable(connection);
                System.out.println("Database initialized.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                "player_uuid TEXT NOT NULL," +
                "item_name TEXT NOT NULL," +
                "quantity INTEGER NOT NULL," +
                "nbt_data TEXT," + // Column for storing NBT data as JSON
                "FOREIGN KEY(player_uuid) REFERENCES players(uuid)" +
                ");";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }

    public static void addPlayer(String uuid, String name) {
        String sql = "INSERT INTO players(uuid, name, joined_at) VALUES(?, ?, datetime('now'))";
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
        String sql = "INSERT INTO items(player_uuid, item_name, quantity, nbt_data) VALUES(?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUuid);
            statement.setString(2, ItemManager.getItemId(itemStack).toString());
            statement.setInt(3, itemStack.getCount());

            // Serialize NBT data to JSON
            String nbtJson = ItemManager.serializeNbt(itemStack.getNbt());
            statement.setString(4, nbtJson);

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
                String itemName = resultSet.getString("item_name");
                int quantity = resultSet.getInt("quantity");
                String nbtData = resultSet.getString("nbt_data");


                System.out.println("Found: " + itemName+"_"+quantity);

                ItemStack itemStack = ItemManager.getItemStackFromString(itemName,quantity,nbtData);
                System.out.println("FoundItemStack: " + itemStack);

                items.add(itemStack);
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
