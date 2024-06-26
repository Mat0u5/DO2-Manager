package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.database.DatabaseManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FunctionScanner {

    private static final String FUNCTION_FOLDER_PATH = "./world/datapacks/dom/data/dom/functions/"; // Adjust the folder path as needed

    public static void scanFunctions() {
        try (Connection connection = DriverManager.getConnection(DatabaseManager.URL)) {
            if (connection != null) {
                scanDirectory(new File(FUNCTION_FOLDER_PATH), connection);
                System.out.println("Function scanning completed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void scanDirectory(File directory, Connection connection) throws SQLException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(file, connection); // Recursively scan subdirectories
                } else if (file.getName().endsWith(".mcfunction")) {
                    String filePath = file.getPath();
                    String functionName = file.getName().replace(".mcfunction", "");
                    String functionContent = readFunctionContent(file.toPath());

                    if (functionContent != null) {
                        addFunctionToDatabase(connection, filePath, functionName, functionContent);
                    }
                }
            }
        }
    }

    private static String readFunctionContent(Path filePath) {
        try {
            byte[] bytes = Files.readAllBytes(filePath);
            return new String(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void addFunctionToDatabase(Connection connection, String filePath, String functionName, String functionContent) throws SQLException {
        String sql = "INSERT INTO functions(file_path, function_name, function_content) VALUES(?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, filePath);
            statement.setString(2, functionName);
            statement.setString(3, functionContent);
            statement.executeUpdate();
        }
    }
    public static List<String> findFunctionsContaining(String searchString, String searchMode) {
        List<String> foundFunctions = new ArrayList<>();
        String sql = "SELECT * FROM functions";
        try (Connection connection = DriverManager.getConnection(DatabaseManager.URL);
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String functionPath = resultSet.getString("file_path");
                String functionName = resultSet.getString("function_name");
                String functionContent = resultSet.getString("function_content");
                if (matchesSearchCriteria(functionContent, searchString, searchMode)) {
                    String fun = functionPath.replaceAll("\\\\","/").replaceAll(".mcfunction","").replaceAll("./world/datapacks/dom/data/dom/functions/","dom:");
                    foundFunctions.add(fun);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return foundFunctions;
    }

    private static boolean matchesSearchCriteria(String functionContent, String searchString, String searchMode) {
        String[] lines = functionContent.split("\\r?\\n");
        for (String line : lines) {
            if (!line.trim().startsWith("#")) { // Ignore comment lines
                switch (searchMode) {
                    case "contains":
                        if (line.contains(searchString)) {
                            return true;
                        }
                        break;
                    case "startsWith":
                        if (line.startsWith(searchString)) {
                            return true;
                        }
                        break;
                    case "endsWith":
                        if (line.endsWith(searchString)) {
                            return true;
                        }
                        break;
                    default:
                        return false;
                }
            }
        }
        return false;
    }
}
