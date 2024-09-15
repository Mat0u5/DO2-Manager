package net.mat0u5.do2manager;

import net.fabricmc.api.ModInitializer;

import net.mat0u5.do2manager.config.ConfigManager;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.gui.GuiPlayerSpecific;
import net.mat0u5.do2manager.queue.DungeonQueue;
import net.mat0u5.do2manager.simulator.Simulator;
import net.mat0u5.do2manager.tcg.TCG_Items;
import net.mat0u5.do2manager.utils.DiscordBot;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.utils.ModRegistries;
import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Main implements ModInitializer {
	public static final String MOD_ID = "do2manager";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static ConfigManager config;
	public static ConfigManager lastInvUpdate;
	public static DO2Run currentRun = new DO2Run();
	public static DO2Run speedrun = new DO2Run();
	public static List<DO2RunAbridged> allAbridgedRuns = new ArrayList<>();
	public static HashMap<PlayerEntity, GuiPlayerSpecific> openGuis = new HashMap<>();
	public static HashMap<String, String> allPlayers = new HashMap<>();
	public static MinecraftServer server;
	public static boolean reloadedRuns = false;
	public static Simulator simulator;
	public static DungeonQueue dungeonQueue = new DungeonQueue();

	@Override
	public void onInitialize() {

		config = new ConfigManager("./config/"+MOD_ID+"/"+MOD_ID+".properties");
		lastInvUpdate = new ConfigManager("./config/"+MOD_ID+"/"+MOD_ID+"_inv_update.properties");

		if (config.getProperty("current_run") != null && !config.getProperty("current_run").isEmpty()) loadRunInfoFromConfig();
		DatabaseManager.checkForDBUpdates();
		ModRegistries.registerModStuff();
		LOGGER.info("Initializing DO2-manager...");
		simulator = new Simulator();

		dungeonQueue.loadQueueFromConfig();
		TCG_Items.reload();
	}

	public static void resetRunInfo() {
		currentRun = new DO2Run();
		config.setProperty("current_run","");
		config.setProperty("current_run_is_speedrun","false");
		speedrun = new DO2Run();
	}
	public static void saveRunInfoToConfig() {
		config.setProperty("current_run",currentRun.serialize());
		config.setProperty("current_queue",dungeonQueue.getQueueAsString());
		System.out.println("Shutting Down DO2-Manager..");
	}
	public static void loadRunInfoFromConfig() {
		currentRun = new DO2Run().deserialize(config.getProperty("current_run"));
	}

	private static final ExecutorService executor = Executors.newSingleThreadExecutor();
	public static CompletableFuture<Void> reloadAllAbridgedRunsAsync() {
		return CompletableFuture.runAsync(() -> {
			synchronized (Main.class) { // Synchronize to handle concurrent access
				System.out.println("Loading All Abridged Runs...");
				allAbridgedRuns = DatabaseManager.getAbridgedRunsByCriteria(new ArrayList<>());
				Collections.sort(allAbridgedRuns, Comparator.comparingInt(DO2RunAbridged::getRunNum).reversed());

				List<DO2RunAbridged> testingRuns = new ArrayList<>();
				for (DO2RunAbridged run : allAbridgedRuns) {
					if (run.run_type.equalsIgnoreCase("testing")) {
						testingRuns.add(run);
					}
				}
				allAbridgedRuns.removeAll(testingRuns);

				System.out.println("Abridged Runs Reloaded.");
				reloadedRuns = true;
			}
		}, executor);
	}
	public static void addRun(DO2Run run) {
		if (run.date==null||run.date.isEmpty()) {
			LocalDateTime now = LocalDateTime.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			run.date = now.format(formatter);
		}
		allAbridgedRuns.add(run.getAbridgedRun());
		Collections.sort(allAbridgedRuns, new Comparator<DO2RunAbridged>() {
			@Override
			public int compare(DO2RunAbridged run1, DO2RunAbridged run2) {
				return Integer.compare(run2.getRunNum(), run1.getRunNum());
			}
		});

	}
}