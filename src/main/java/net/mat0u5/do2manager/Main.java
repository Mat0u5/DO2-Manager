package net.mat0u5.do2manager;

import net.fabricmc.api.ModInitializer;

import net.mat0u5.do2manager.config.ConfigManager;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.gui.GuiPlayerSpecific;
import net.mat0u5.do2manager.simulator.Simulator;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.utils.ModRegistries;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;


public class Main implements ModInitializer {
	public static final int PHASE_UPDATE = 1;
	public static final String MOD_ID = "do2manager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ConfigManager config;
	public static ConfigManager lastPhaseUpdate;
	public static DO2Run currentRun = new DO2Run();
	public static DO2Run speedrun = new DO2Run();
	public static List<DO2Run> allRuns = new ArrayList<>();
	public static HashMap<PlayerEntity, GuiPlayerSpecific> openGuis = new HashMap<>();
	public static HashMap<String, String> allPlayers = new HashMap<>();
	public static MinecraftServer server;
	public static boolean reloadedRuns = false;
	public static Simulator simulator;

	@Override
	public void onInitialize() {

		config = new ConfigManager("./config/"+MOD_ID+"/"+MOD_ID+".properties");
		lastPhaseUpdate = new ConfigManager("./config/"+MOD_ID+"/"+MOD_ID+"_phase_inv_update.properties");

		if (config.getProperty("current_run") != null && !config.getProperty("current_run").isEmpty()) loadRunInfoFromConfig();
		DatabaseManager.checkForDBUpdates();
		ModRegistries.registerModStuff();
		LOGGER.info("Initializing DO2-manager...");
		simulator = new Simulator();

		DatabaseManager.fetchAllPlayers();
		Runtime.getRuntime().addShutdownHook(new Thread(Main::saveRunInfoToConfig));
	}

	public static void resetRunInfo() {
		currentRun = new DO2Run();
		config.setProperty("current_run","");
		config.setProperty("current_run_is_speedrun","false");
		speedrun = new DO2Run();
	}
	public static void saveRunInfoToConfig() {
		config.setProperty("current_run",currentRun.serialize());
	}
	public static void loadRunInfoFromConfig() {
		currentRun = new DO2Run().deserialize(config.getProperty("current_run"));
	}

	public static CompletableFuture<Void> reloadAllRunsAsync() {
		return CompletableFuture.runAsync(() -> {
			allRuns = DatabaseManager.getRunsByCriteria(new ArrayList<>());
			Collections.sort(allRuns, new Comparator<DO2Run>() {
				@Override
				public int compare(DO2Run run1, DO2Run run2) {
					return Integer.compare(run2.getRunNum(), run1.getRunNum());
				}
			});
			System.out.println("Runs Reloaded.");
			reloadedRuns = true;
		});
	}
	public static void addRun(DO2Run run) {
		if (run.date==null||run.date.isEmpty()) {
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			run.date = now.format(formatter);
		}
		allRuns.add(run);
		Collections.sort(allRuns, new Comparator<DO2Run>() {
			@Override
			public int compare(DO2Run run1, DO2Run run2) {
				return Integer.compare(run2.getRunNum(), run1.getRunNum());
			}
		});

	}
}