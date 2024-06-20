package net.mat0u5.do2manager.config;

import net.mat0u5.do2manager.Main;

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
                    // Add default properties or leave it empty
                    resetRunInfo();
                    properties.store(output, null);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    public static void resetRunInfo() {
        Main.config.setProperty("run_type","");
        Main.config.setProperty("runners","");
        Main.config.setProperty("finishers","");
        Main.config.setProperty("card_plays","");
        Main.config.setProperty("compass_item","");
        Main.config.setProperty("artifact_item","");
        Main.config.setProperty("deck_item","");
        Main.config.setProperty("inventory_save","");
        Main.config.setProperty("death_pos","");
        Main.config.setProperty("death_message","");
        Main.config.setProperty("run_number","");
        Main.config.setProperty("run_length","");
        Main.config.setProperty("timestamp_lvl2_entry","");
        Main.config.setProperty("timestamp_lvl3_entry","");
        Main.config.setProperty("timestamp_lvl4_entry","");
        Main.config.setProperty("timestamp_lvl4_exit","");
        Main.config.setProperty("timestamp_lvl3_exit","");
        Main.config.setProperty("timestamp_lvl2_exit","");
        Main.config.setProperty("timestamp_lvl1_exit","");
        Main.config.setProperty("timestamp_artifact","");
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
