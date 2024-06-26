package net.mat0u5.do2manager;

import net.fabricmc.api.ModInitializer;

import net.mat0u5.do2manager.config.ConfigManager;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.gui.GuiPlayerSpecific;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.utils.ModRegistries;
import net.minecraft.entity.player.PlayerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;


public class Main implements ModInitializer {
	public static final int PHASE_UPDATE = 1;
	public static final String MOD_ID = "do2manager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ConfigManager config;
	public static ConfigManager lastPhaseUpdate;
	public static DO2Run currentRun = new DO2Run();
	public static HashMap<PlayerEntity, GuiPlayerSpecific> openGuis = new HashMap<>();


	@Override
	public void onInitialize() {
		config = new ConfigManager("./config/"+MOD_ID+"/"+MOD_ID+".properties");
		lastPhaseUpdate = new ConfigManager("./config/"+MOD_ID+"/"+MOD_ID+"_phase_inv_update.properties");

		if (config.getProperty("current_run") != null && !config.getProperty("current_run").isEmpty()) loadRunInfoFromConfig();
		DatabaseManager.checkForDBUpdates();
		ModRegistries.registerModStuff();
		LOGGER.info("Initializing DO2-manager...");

		Runtime.getRuntime().addShutdownHook(new Thread(Main::saveRunInfoToConfig));
	}

	public static void resetRunInfo() {
		currentRun = new DO2Run();
		config.setProperty("current_run","");
	}
	public static void saveRunInfoToConfig() {
		config.setProperty("current_run",currentRun.serialize());
	}
	public static void loadRunInfoFromConfig() {
		currentRun = new DO2Run().deserialize(config.getProperty("current_run"));
	}
}