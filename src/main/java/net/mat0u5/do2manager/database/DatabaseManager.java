package net.mat0u5.do2manager.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.utils.DO2_GSON;

public class DatabaseManager {
    public static final String DB_VERSION = "v.0.1.0";

    private static final String FOLDER_PATH = "./config/"+ Main.MOD_ID;
    private static final String FILE_PATH = FOLDER_PATH+"/"+Main.MOD_ID+".db";
    private static final String URL = "jdbc:sqlite:"+FILE_PATH;

    public static void initialize() {
        createFolderIfNotExists();
        try (Connection connection = DriverManager.getConnection(URL)) {
            if (connection != null) {
                createRunsTable(connection);
                createRunsDetailedTable(connection);
                createRunsSpeedrunsTable(connection);
                System.out.println("Database initialized.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void checkForDBUpdates() {
        String lastRecordedDBVersion = Main.config.getProperty("db_version");
        if (DB_VERSION.equalsIgnoreCase(lastRecordedDBVersion)) return;
        if (lastRecordedDBVersion == null || lastRecordedDBVersion.isEmpty()) {
            Main.config.setProperty("db_version",DB_VERSION);
            return;
        }
    }
    private static void createFolderIfNotExists() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
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
    public static void addRun(DO2Run run) {
        String sql = "INSERT INTO runs(db_version, run_number, date, run_type, runners, finishers, run_length) VALUES(?, ?, datetime('now'), ?, ?, ?, ?)";
        try (Connection connection = DriverManager.getConnection(URL);
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, DB_VERSION);
            statement.setInt(2, run.run_number);
            statement.setString(3, run.run_type);
            statement.setString(4, String.join(",", run.runners));
            statement.setString(5, String.join(",", run.finishers));
            statement.setInt(6, run.run_length);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addRunDetailed(DO2Run run) {
        String sql = "INSERT INTO runsDetailed(db_version, run_number, card_plays, difficulty, compass_item, artifact_item, deck_item, inventory_save, items_bought, death_pos, death_message) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
            statement.setString(9, DO2_GSON.serializeListItemStack(run.items_bought));
            statement.setString(10, run.death_pos);
            statement.setString(11, run.death_message);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addRunSpeedrun(DO2Run run) {
        String sql = "INSERT INTO runsSpeedruns(db_version, run_number, timestamp_lvl2_entry, timestamp_lvl3_entry, timestamp_lvl4_entry, timestamp_lvl4_exit, timestamp_lvl3_exit, timestamp_lvl2_exit, timestamp_lvl1_exit, timestamp_artifact) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DO2Run getRunByRunNumber(int runNumber) {
        DO2Run run = null;
        String runSql = "SELECT * FROM runs WHERE run_number = ?";
        String runDetailedSql = "SELECT * FROM runsDetailed WHERE run_number = ?";
        String runSpeedrunsSql = "SELECT * FROM runsSpeedruns WHERE run_number = ?";

        try (Connection connection = DriverManager.getConnection(URL)) {
            PreparedStatement runStatement = connection.prepareStatement(runSql);
            runStatement.setInt(1, runNumber);
            ResultSet runResultSet = runStatement.executeQuery();

            if (runResultSet.next()) {
                run = new DO2Run();
                run.run_number = runResultSet.getInt("run_number");
                run.run_type = runResultSet.getString("run_type");
                run.runners = List.of(runResultSet.getString("runners").split(","));
                run.finishers = List.of(runResultSet.getString("finishers").split(","));
                run.run_length = runResultSet.getInt("run_length");
            }

            if (run != null) {
                PreparedStatement runDetailedStatement = connection.prepareStatement(runDetailedSql);
                runDetailedStatement.setInt(1, runNumber);
                ResultSet runDetailedResultSet = runDetailedStatement.executeQuery();

                if (runDetailedResultSet.next()) {
                    run.card_plays = DO2_GSON.deserializeListItemStack(runDetailedResultSet.getString("card_plays"));
                    run.difficulty = runDetailedResultSet.getInt("difficulty");
                    run.compass_item = DO2_GSON.deserializeItemStack(runDetailedResultSet.getString("compass_item"));
                    run.artifact_item = DO2_GSON.deserializeItemStack(runDetailedResultSet.getString("artifact_item"));
                    run.deck_item = DO2_GSON.deserializeItemStack(runDetailedResultSet.getString("deck_item"));
                    run.inventory_save = DO2_GSON.deserializeListItemStack(runDetailedResultSet.getString("inventory_save"));
                    run.items_bought = DO2_GSON.deserializeListItemStack(runDetailedResultSet.getString("items_bought"));
                    run.death_pos = runDetailedResultSet.getString("death_pos");
                    run.death_message = runDetailedResultSet.getString("death_message");
                }

                PreparedStatement runSpeedrunsStatement = connection.prepareStatement(runSpeedrunsSql);
                runSpeedrunsStatement.setInt(1, runNumber);
                ResultSet runSpeedrunsResultSet = runSpeedrunsStatement.executeQuery();

                if (runSpeedrunsResultSet.next()) {
                    run.timestamp_lvl2_entry = runSpeedrunsResultSet.getInt("timestamp_lvl2_entry");
                    run.timestamp_lvl3_entry = runSpeedrunsResultSet.getInt("timestamp_lvl3_entry");
                    run.timestamp_lvl4_entry = runSpeedrunsResultSet.getInt("timestamp_lvl4_entry");
                    run.timestamp_lvl4_exit = runSpeedrunsResultSet.getInt("timestamp_lvl4_exit");
                    run.timestamp_lvl3_exit = runSpeedrunsResultSet.getInt("timestamp_lvl3_exit");
                    run.timestamp_lvl2_exit = runSpeedrunsResultSet.getInt("timestamp_lvl2_exit");
                    run.timestamp_lvl1_exit = runSpeedrunsResultSet.getInt("timestamp_lvl1_exit");
                    run.timestamp_artifact = runSpeedrunsResultSet.getInt("timestamp_artifact");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return run;
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
}
