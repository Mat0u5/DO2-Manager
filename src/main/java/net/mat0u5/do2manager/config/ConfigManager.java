package net.mat0u5.do2manager.config;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import static net.mat0u5.do2manager.utils.OtherUtils.stringToInt;

public class ConfigManager {

    private Properties properties = new Properties();
    private String filePath;
    public static final String VALUE_SEPARATOR = "_~~_";

    public ConfigManager(String filePath) {
        this.filePath = filePath;
        createFileIfNotExists();
        loadProperties();
    }

    private void createFileIfNotExists() {
        File configFile = new File(filePath);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
                try (OutputStream output = new FileOutputStream(configFile)) {
                    properties.setProperty("current_run","");
                    properties.setProperty("db_version",DatabaseManager.DB_VERSION);
                    properties.setProperty("testing","false");
                    properties.setProperty("current_run_is_speedrun","false");
                    properties.setProperty("webhook_url","");
                    properties.setProperty("webhook_url_staff","");
                    properties.setProperty("webhook_token","");
                    properties.setProperty("block_password","");
                    properties.setProperty("simulator_enabled","false");
                    properties.setProperty("server_chat_channel_id","");


                    properties.store(output, null);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void loadProperties() {
        try (InputStream input = new FileInputStream(filePath)) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(filePath)) {
            properties.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
