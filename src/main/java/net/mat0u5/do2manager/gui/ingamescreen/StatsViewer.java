package net.mat0u5.do2manager.gui.ingamescreen;

import com.mojang.serialization.JsonOps;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.mat0u5.do2manager.Main.server;
import static net.mat0u5.do2manager.utils.OtherUtils.roundToNPlaces;

public class StatsViewer {
    /* Tags list
    stats_filters_run_all
    stats_filters_run_casual
    stats_filters_run_phase
    stats_filters_run_hardcore

    stats_filters_difficulty_easy
    stats_filters_difficulty_normal
    stats_filters_difficulty_hard
    stats_filters_difficulty_deadly
    stats_filters_difficulty_deepfrost

    stats_filters_success_all
    stats_filters_success_successful
    stats_filters_success_failed

    stats_graph_winpercent
    stats_graph_runs
    stats_graph_embers
    stats_graph_crowns
    stats_graph_totalembers
    stats_graph_totalcrowns

    stats_info_runs
    stats_info_runs_successful
    stats_info_runs_failed
    stats_info_runs_winpercent
    stats_info_runs_winstreak
    stats_info_runs_lossstreak
    stats_info_runs_playtime
    stats_info_runs_totalembers
    stats_info_runs_totalcrowns
    stats_info_runs_avglength
    stats_info_runs_avgembers
    stats_info_runs_avgcrowns
     */
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    public static ServerPlayer currentPlayer;
    public static List<DO2RunAbridged> abridgedRuns = new ArrayList<>();
    public static List<DO2RunAbridged> filteredRuns = new ArrayList<>();
    public static final AABB box = new AABB(-535, 119, 1945, -507, 102, 1968);
    public static final AABB interactableBox = new AABB(-533, 118, 1945.5, -508, 104, 1946.1);
    public static Map<Double, String> selectMap = new HashMap<>() {{
        put(115.0918, "stats_filters_run_all");
        put(114.5918, "stats_filters_run_casual");
        put(114.0918, "stats_filters_run_phase");
        put(113.5918, "stats_filters_run_hardcore");

        put(112.8418, "stats_filters_difficulty_all");
        put(112.4043, "stats_filters_difficulty_easy");
        put(111.9043, "stats_filters_difficulty_normal");
        put(111.4043, "stats_filters_difficulty_hard");
        put(110.9043, "stats_filters_difficulty_deadly");
        put(110.4043, "stats_filters_difficulty_deepfrost");


        put(109.6543, "stats_filters_success_all");
        put(109.1543, "stats_filters_success_successful");
        put(108.6543, "stats_filters_success_failed");


        put(106.8410, "stats_graph_winpercent");
        put(106.4043, "stats_graph_runs");
        put(105.9668, "stats_graph_embers");
        put(105.5293, "stats_graph_crowns");
        put(105.0918, "stats_graph_totalembers");
        put(104.6543, "stats_graph_totalcrowns");

    }};

    public static String FILTER_RUNTYPE = "all";
    public static String FILTER_DIFFICULTY = "all";
    public static String FILTER_SUCCESS = "all";
    public static String GRAPH = "winpercent";

    public static int clickCooldown = 5;

    public static boolean currentlyReloading = false;
    public static void onTick(MinecraftServer server) {
        if (Main.statsViewerDisabled) return;
        try {
            playerChecker(server);
            if (currentPlayer != null) {
                updateCursor();
                playerCursorSet();
            }
            if (clickCooldown > 0) clickCooldown--;
        }catch(Exception e) {
            Main.LOGGER.error(e.getMessage());
        }
    }

    public static void playerCursorSet() {
        if (Main.statsViewerDisabled) return;
        if (currentPlayer == null) return;
        ItemStack cursor = new ItemStack(Items.IRON_NUGGET, 1);
        ItemManager.setModelData(cursor, 521);
        cursor.set(DataComponents.CUSTOM_NAME, Component.nullToEmpty("§r§lCursor"));
        ItemManager.addLoreToItemStack(cursor, List.of(Component.nullToEmpty("§5§oRight click to use.")));
        if (!box.intersects(currentPlayer.getBoundingBox())) {
            currentPlayer.getInventory().removeItem(cursor);
            return;
        }
        if (ItemManager.getModelData(currentPlayer.getMainHandItem()) == 521) return;
        removeCursorFromPlayer(currentPlayer);
        if (currentPlayer.getMainHandItem().isEmpty()) {
            currentPlayer.getInventory().setItem(currentPlayer.getInventory().getSelectedSlot(), cursor);
        }
    }

    public static void removeCursorFromPlayer(ServerPlayer player) {
        for (int pos = 0; pos < player.getInventory().getContainerSize(); pos++) {
            ItemStack itemAtPos = player.getInventory().getItem(pos);
            if (ItemManager.getModelData(itemAtPos) == 521) {
                player.getInventory().removeItemNoUpdate(pos);
            }
        }

    }

    public static void onPlayerUse(ServerPlayer player) {
        if (Main.statsViewerDisabled) return;
        try {
            if (player == null) return;
            if (currentPlayer == null) return;
            if (player.getUUID() != player.getUUID()) return;
            Map.Entry<Double, String> pointingAt = getPointingBox();
            if (pointingAt == null) return;
            if (clickCooldown > 0) return;
            clickCooldown = 5;
            String clicked = pointingAt.getValue();
            if (clicked.startsWith("stats_filters_run_")) {
                String newClicked = clicked.replaceFirst("stats_filters_run_", "");
                FILTER_RUNTYPE = newClicked;
            }
            else if (clicked.startsWith("stats_filters_difficulty_")) {
                String newClicked = clicked.replaceFirst("stats_filters_difficulty_", "");
                FILTER_DIFFICULTY = newClicked;
            }
            else if (clicked.startsWith("stats_filters_success_")) {
                String newClicked = clicked.replaceFirst("stats_filters_success_", "");
                FILTER_SUCCESS = newClicked;
            }
            else if (clicked.startsWith("stats_graph_")) {
                String newClicked = clicked.replaceFirst("stats_graph_", "");
                GRAPH = newClicked;
            }
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundSource.PLAYERS, 0.8F, 1.0F);
            updateFilters();
        }catch(Exception e) {
            Main.LOGGER.error(e.getMessage());
        }
    }

    public static void playerChecker(MinecraftServer server) {
        if (Main.statsViewerDisabled) return;
        List<ServerPlayer> playersInBox = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (box.intersects(player.getBoundingBox())) playersInBox.add(player);
        }
        if (playersInBox.isEmpty()) {
            if (currentPlayer != null) {
                onLastPlayerLeave();
            }
            return;
        }
        boolean containsOldPlayer = false;
        if (currentPlayer != null) {
            for (ServerPlayer player : playersInBox) {
                if (currentPlayer.getUUID().equals(player.getUUID())) {
                    containsOldPlayer = true;
                    break;
                }
            }
        }

        if (containsOldPlayer) {
            // Player is still in the bounding box
            return;
        }
        for (ServerPlayer player : playersInBox) {
            if (!player.isSpectator()) {
                setCurrentPlayer(player);
                return;
            }
        }

        //All players are spectators
        if (currentPlayer != null) {
            onLastPlayerLeave();
        }
    }

    public static void setCurrentPlayer(ServerPlayer player) {
        if (currentlyReloading) {
            if (!Main.reloadedRuns) return;
        }
        if (!Main.reloadedRuns) {
            currentlyReloading = true;
            Main.reloadAllAbridgedRunsAsync().thenRun(() -> {
                currentlyReloading = false;
                actualSetCurrentPlayer(player);
            });
        }
        else {
            actualSetCurrentPlayer(player);
        }
    }

    private static void actualSetCurrentPlayer(ServerPlayer player) {
        if (currentPlayer != null) {
            removeCursorFromPlayer(currentPlayer);
        }
        FILTER_RUNTYPE = "all";
        FILTER_DIFFICULTY = "all";
        FILTER_SUCCESS = "all";
        GRAPH = "winpercent";

        setDisplayEntityText("stats_name", player.getFeedbackDisplayName());
        currentPlayer = player;
        List<DO2RunAbridged> newAbridgedRuns = new ArrayList<>();
        String uuid = player.getUUID().toString();
        for (DO2RunAbridged abridgedRun : Main.allAbridgedRuns) {
            if (abridgedRun.runners.contains(uuid)) {
                newAbridgedRuns.add(abridgedRun);
            }
        }
        abridgedRuns = newAbridgedRuns;
        updateFilters();
    }

    public static void onLastPlayerLeave() {
        try {
            OtherUtils.executeCommand("kill @e[tag=graph_var]");
            if (currentPlayer != null) {
                removeCursorFromPlayer(currentPlayer);
            }
            currentPlayer = null;
            abridgedRuns = new ArrayList<>();
            filteredRuns = new ArrayList<>();
        }catch(Exception e) {}
    }

    public static void updateFilters() {
        try {
            if (currentPlayer == null) return;
            filteredRuns = new ArrayList<>();
            for (DO2RunAbridged run : abridgedRuns) {
                if (FILTER_SUCCESS.equalsIgnoreCase("successful") && !run.getSuccessFor(currentPlayer.getUUID().toString())) continue;
                if (FILTER_SUCCESS.equalsIgnoreCase("failed") && run.getSuccessFor(currentPlayer.getUUID().toString())) continue;
                if (FILTER_DIFFICULTY.equalsIgnoreCase("easy") && run.difficulty != 1) continue;
                if (FILTER_DIFFICULTY.equalsIgnoreCase("normal") && run.difficulty != 2) continue;
                if (FILTER_DIFFICULTY.equalsIgnoreCase("hard") && run.difficulty != 3) continue;
                if (FILTER_DIFFICULTY.equalsIgnoreCase("deadly") && run.difficulty != 4) continue;
                if (FILTER_DIFFICULTY.equalsIgnoreCase("deepfrost") && run.difficulty != 5) continue;
                if (FILTER_RUNTYPE.equalsIgnoreCase("casual") && !run.run_type.equalsIgnoreCase("casual")) continue;
                if (FILTER_RUNTYPE.equalsIgnoreCase("phase") && !run.run_type.equalsIgnoreCase("phase")) continue;
                if (FILTER_RUNTYPE.equalsIgnoreCase("hardcore") && !run.run_type.equalsIgnoreCase("hardcore")) continue;
                filteredRuns.add(run);
            }
            updateAllDisplays();
            boolean noFilters = FILTER_SUCCESS.equalsIgnoreCase("all") && FILTER_DIFFICULTY.equalsIgnoreCase("all") && FILTER_RUNTYPE.equalsIgnoreCase("all");
            GraphGenerator.generateGraph(server.overworld(), new ArrayList<>(filteredRuns), GRAPH, noFilters, currentPlayer);
        }catch(Exception e) {}
    }

    public static void updateAllDisplays() {
        updateSelectedButton();
        updateDisplayFilters();
        updateDisplayRunsInfo();
    }

    public static void updateSelectedButton() {
        if (server == null) return;
        ItemStack emptyCheckbox = new ItemStack(Items.IRON_NUGGET, 1);
        ItemManager.setModelData(emptyCheckbox, 520);
        Tag emptyCheckboxNbt = ItemManager.save(emptyCheckbox, server.registryAccess());

        ItemStack fullCheckbox = new ItemStack(Items.IRON_NUGGET, 1);
        ItemManager.setModelData(fullCheckbox, 519);
        Tag fullCheckboxNbt = ItemManager.save(fullCheckbox, server.registryAccess());

        setDisplayEntityItem("stats_button_filters_run_all", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_filters_run_casual", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_filters_run_phase", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_filters_run_hardcore", emptyCheckboxNbt);

        setDisplayEntityItem("stats_button_filters_difficulty_all", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_filters_difficulty_easy", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_filters_difficulty_normal", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_filters_difficulty_hard", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_filters_difficulty_deadly", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_filters_difficulty_deepfrost", emptyCheckboxNbt);

        setDisplayEntityItem("stats_button_filters_success_all", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_filters_success_successful", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_filters_success_failed", emptyCheckboxNbt);

        setDisplayEntityItem("stats_button_graph_winpercentage", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_graph_runs", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_graph_embers", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_graph_crowns", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_graph_totalembers", emptyCheckboxNbt);
        setDisplayEntityItem("stats_button_graph_totalcrowns", emptyCheckboxNbt);

        if (FILTER_RUNTYPE.equalsIgnoreCase("all")) setDisplayEntityItem("stats_button_filters_run_all", fullCheckboxNbt);
        else if (FILTER_RUNTYPE.equalsIgnoreCase("casual")) setDisplayEntityItem("stats_button_filters_run_casual", fullCheckboxNbt);
        else if (FILTER_RUNTYPE.equalsIgnoreCase("phase")) setDisplayEntityItem("stats_button_filters_run_phase", fullCheckboxNbt);
        else if (FILTER_RUNTYPE.equalsIgnoreCase("hardcore")) setDisplayEntityItem("stats_button_filters_run_hardcore", fullCheckboxNbt);

        if (FILTER_DIFFICULTY.equalsIgnoreCase("all")) setDisplayEntityItem("stats_button_filters_difficulty_all", fullCheckboxNbt);
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("easy")) setDisplayEntityItem("stats_button_filters_difficulty_easy", fullCheckboxNbt);
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("normal")) setDisplayEntityItem("stats_button_filters_difficulty_normal", fullCheckboxNbt);
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("hard")) setDisplayEntityItem("stats_button_filters_difficulty_hard", fullCheckboxNbt);
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("deadly")) setDisplayEntityItem("stats_button_filters_difficulty_deadly", fullCheckboxNbt);
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("deepfrost")) setDisplayEntityItem("stats_button_filters_difficulty_deepfrost", fullCheckboxNbt);

        if (FILTER_SUCCESS.equalsIgnoreCase("all")) setDisplayEntityItem("stats_button_filters_success_all", fullCheckboxNbt);
        else if (FILTER_SUCCESS.equalsIgnoreCase("successful")) setDisplayEntityItem("stats_button_filters_success_successful", fullCheckboxNbt);
        else if (FILTER_SUCCESS.equalsIgnoreCase("failed")) setDisplayEntityItem("stats_button_filters_success_failed", fullCheckboxNbt);

        if (GRAPH.equalsIgnoreCase("winpercent")) setDisplayEntityItem("stats_button_graph_winpercentage", fullCheckboxNbt);
        else if (GRAPH.equalsIgnoreCase("runs")) setDisplayEntityItem("stats_button_graph_runs", fullCheckboxNbt);
        else if (GRAPH.equalsIgnoreCase("embers")) setDisplayEntityItem("stats_button_graph_embers", fullCheckboxNbt);
        else if (GRAPH.equalsIgnoreCase("crowns")) setDisplayEntityItem("stats_button_graph_crowns", fullCheckboxNbt);
        else if (GRAPH.equalsIgnoreCase("totalembers")) setDisplayEntityItem("stats_button_graph_totalembers", fullCheckboxNbt);
        else if (GRAPH.equalsIgnoreCase("totalcrowns")) setDisplayEntityItem("stats_button_graph_totalcrowns", fullCheckboxNbt);
    }

    public static void updateCursor() {
        if (currentPlayer == null) return;

        // Perform the raycast
        HitResult hitResult = currentPlayer.pick(25, 0.0F, false);
        // Check if the raycast hit something
        if (hitResult.getType() == HitResult.Type.MISS) {
            return; // Player isn't looking at anything within range
        }

        Vec3 targetPos = hitResult.getLocation(); // Position the player is looking at

        if (!interactableBox.contains(targetPos)) return;

        // Find the ItemEntity with the tag "cursor"
        Display.ItemDisplay itemEntity = currentPlayer.level().getEntitiesOfClass(
                Display.ItemDisplay.class,
                interactableBox,
                entity -> entity.getTags().contains("cursor")
        ).stream().findFirst().orElse(null);

        if (itemEntity == null) {
            return; // No ItemEntity with the tag "cursor" found
        }
        // Move the ItemEntity to the target position
        itemEntity.setPosRaw(targetPos.x, targetPos.y, targetPos.z);
        //itemEntity.velocityModified = true;

        Map.Entry<Double, String> pointingAt = getPointingBox();
        if (pointingAt == null) {
            ItemStack cursor = new ItemStack(Items.IRON_NUGGET, 1);
            ItemManager.setModelData(cursor, 521);
            setDisplayEntityItem("cursor", cursor);
        }
        else {
            ItemStack cursor = new ItemStack(Items.IRON_NUGGET, 1);
            ItemManager.setModelData(cursor, 522);
            setDisplayEntityItem("cursor", cursor);
        }


        Display.ItemDisplay highlightEntity = currentPlayer.level().getEntitiesOfClass(
                Display.ItemDisplay.class,
                interactableBox,
                entity -> entity.getTags().contains("highlight")
        ).stream().findFirst().orElse(null);
        if (highlightEntity == null) return;

        if (pointingAt == null) {
            highlightEntity.setPosRaw(highlightEntity.getX(), highlightEntity.getY(), 1945.7);
            return;
        }
        highlightEntity.setPosRaw(highlightEntity.getX(), pointingAt.getKey(), 1946.0);
    }

    public static Map.Entry<Double, String> getPointingBox() {
        if (currentPlayer == null) return null;
        HitResult hitResult = currentPlayer.pick(25, 0.0F, false);
        if (hitResult.getType() == HitResult.Type.MISS) return null;
        Vec3 targetPos = hitResult.getLocation();
        if (!interactableBox.contains(targetPos)) return null;

        if (targetPos.x > -529.7) return null;
        if (targetPos.x < -533) return null;
        if (targetPos.y > 115.5) return null;
        if (targetPos.y < 104) return null;

        for (Map.Entry<Double, String> entry : selectMap.entrySet()) {
            Double posY = entry.getKey();
            if (Math.abs(targetPos.y-posY) < 0.43) return entry;
        }

        return null;
    }

    public static void updateDisplayFilters() {
        setDisplayEntityText("stats_filters_run_all", Component.literal("All"));
        setDisplayEntityText("stats_filters_run_casual", Component.literal("Casual"));
        setDisplayEntityText("stats_filters_run_phase", Component.literal("Phase"));
        setDisplayEntityText("stats_filters_run_hardcore", Component.literal("Hardcore"));

        setDisplayEntityText("stats_filters_difficulty_all", Component.literal("All"));
        setDisplayEntityText("stats_filters_difficulty_easy", Component.literal("Easy"));
        setDisplayEntityText("stats_filters_difficulty_normal", Component.literal("Normal"));
        setDisplayEntityText("stats_filters_difficulty_hard", Component.literal("Hard"));
        setDisplayEntityText("stats_filters_difficulty_deadly", Component.literal("Deadly"));
        setDisplayEntityText("stats_filters_difficulty_deepfrost", Component.literal("Deepfrost"));

        setDisplayEntityText("stats_filters_success_all", Component.literal("All"));
        setDisplayEntityText("stats_filters_success_successful", Component.literal("Successful"));
        setDisplayEntityText("stats_filters_success_failed", Component.literal("Failed"));

        setDisplayEntityText("stats_graph_winpercent", Component.literal("Win Percent"));
        setDisplayEntityText("stats_graph_runs", Component.literal("Runs"));
        setDisplayEntityText("stats_graph_embers", Component.literal("Run Embers"));
        setDisplayEntityText("stats_graph_crowns", Component.literal("Run Crowns"));
        setDisplayEntityText("stats_graph_totalembers", Component.literal("Total Embers"));
        setDisplayEntityText("stats_graph_totalcrowns", Component.literal("Total Crowns"));

        if (FILTER_RUNTYPE.equalsIgnoreCase("all")) setDisplayEntityText("stats_filters_run_all", Component.literal("§aAll"));
        else if (FILTER_RUNTYPE.equalsIgnoreCase("casual")) setDisplayEntityText("stats_filters_run_casual", Component.literal("§eCasual"));
        else if (FILTER_RUNTYPE.equalsIgnoreCase("phase")) setDisplayEntityText("stats_filters_run_phase", Component.literal("§3Phase"));
        else if (FILTER_RUNTYPE.equalsIgnoreCase("hardcore")) setDisplayEntityText("stats_filters_run_hardcore", Component.literal("§4Hardcore"));

        if (FILTER_DIFFICULTY.equalsIgnoreCase("all")) setDisplayEntityText("stats_filters_difficulty_all", Component.literal("§aAll"));
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("easy")) setDisplayEntityText("stats_filters_difficulty_easy", Component.literal("§aEasy"));
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("normal")) setDisplayEntityText("stats_filters_difficulty_normal", Component.literal("§eNormal"));
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("hard")) setDisplayEntityText("stats_filters_difficulty_hard", Component.literal("§cHard"));
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("deadly")) setDisplayEntityText("stats_filters_difficulty_deadly", Component.literal("§4Deadly"));
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("deepfrost")) setDisplayEntityText("stats_filters_difficulty_deepfrost", Component.literal("§3Deepfrost"));

        if (FILTER_SUCCESS.equalsIgnoreCase("all")) setDisplayEntityText("stats_filters_success_all", Component.literal("§aAll"));
        else if (FILTER_SUCCESS.equalsIgnoreCase("successful")) setDisplayEntityText("stats_filters_success_successful", Component.literal("§aSuccessful"));
        else if (FILTER_SUCCESS.equalsIgnoreCase("failed")) setDisplayEntityText("stats_filters_success_failed", Component.literal("§cFailed"));

        if (GRAPH.equalsIgnoreCase("winpercent")) setDisplayEntityText("stats_graph_winpercent", Component.literal("§aWin Percent"));
        else if (GRAPH.equalsIgnoreCase("runs")) setDisplayEntityText("stats_graph_runs", Component.literal("§6Runs"));
        else if (GRAPH.equalsIgnoreCase("embers")) setDisplayEntityText("stats_graph_embers", Component.literal("§3Run Embers"));
        else if (GRAPH.equalsIgnoreCase("crowns")) setDisplayEntityText("stats_graph_crowns", Component.literal("§6Run Crowns"));
        else if (GRAPH.equalsIgnoreCase("totalembers")) setDisplayEntityText("stats_graph_totalembers", Component.literal("§3Total Embers"));
        else if (GRAPH.equalsIgnoreCase("totalcrowns")) setDisplayEntityText("stats_graph_totalcrowns", Component.literal("§6Total Crowns"));
    }

    public static void updateDisplayRunsInfo() {
        int runsNum = filteredRuns.size();
        int successfulRuns = 0;
        int unsuccessfulRuns = 0;
        int totalEmbers = 0;
        int totalCrowns = 0;
        int totalPlayTime = 0;
        int biggestWinStreak = 0;
        int biggestLossStreak = 0;
        int biggestWinStreakPos = 0;
        int biggestLossStreakPos = 0;

        int currentWinStreak = 0;
        int currentLossStreak = 0;
        String uuid = currentPlayer.getUUID().toString();
        for (DO2RunAbridged run : filteredRuns) {
            if (run.getSuccessFor(uuid)) {
                successfulRuns++;
                totalEmbers += run.embers_counted;
                totalCrowns += run.crowns_counted;
                currentLossStreak = 0;
                currentWinStreak++;
                if (currentWinStreak > biggestWinStreak) {
                    biggestWinStreak = currentWinStreak;
                    biggestWinStreakPos = run.run_number;
                }
            }
            else {
                unsuccessfulRuns++;
                currentWinStreak = 0;
                currentLossStreak++;
                if (currentLossStreak > biggestLossStreak) {
                    biggestLossStreak = currentLossStreak;
                    biggestLossStreakPos = run.run_number;
                }
            }
            totalPlayTime += run.run_length;
        }

        double winPercentage = roundToNPlaces(((double)successfulRuns*100d)/runsNum,3);
        double averageEmbers = roundToNPlaces((double)totalEmbers/successfulRuns,1);
        double averageCrowns = roundToNPlaces((double)totalCrowns/successfulRuns,1);
        double averageRunLength = roundToNPlaces((double)totalPlayTime/runsNum,0);

        setDisplayEntityText("stats_info_runs", Component.literal("Runs: §b" + runsNum));
        setDisplayEntityText("stats_info_runs_successful", Component.literal("Successful Runs: §a" + successfulRuns));
        setDisplayEntityText("stats_info_runs_failed", Component.literal("Failed Runs: §c" + unsuccessfulRuns));
        setDisplayEntityText("stats_info_runs_winpercent", Component.literal("Win Percentage:§6" + winPercentage+"%"));
        setDisplayEntityText("stats_info_runs_winstreak", Component.literal("Biggest Win Streak: §a" + biggestWinStreak));
        setDisplayEntityText("stats_info_runs_lossstreak", Component.literal("Biggest Loss Streak: §c" + biggestLossStreak));

        setDisplayEntityText("stats_info_runs_playtime", Component.literal("Play Time: §6" + OtherUtils.convertTicksToClockTime(totalPlayTime,false)));
        setDisplayEntityText("stats_info_runs_totalembers", Component.literal("Total Embers: §3" + totalEmbers));
        setDisplayEntityText("stats_info_runs_totalcrowns", Component.literal("Total Crowns: §e" + totalCrowns));
        setDisplayEntityText("stats_info_runs_avglength", Component.literal("Avg. Run Length: §6" + OtherUtils.convertTicksToClockTime((long) averageRunLength,false)));
        setDisplayEntityText("stats_info_runs_avgembers", Component.literal("Avg. Run Embers: §3" + averageEmbers));
        setDisplayEntityText("stats_info_runs_avgcrowns", Component.literal("Avg. Run Crowns: §e" + averageCrowns));
    }

    public static void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    public static void setDisplayEntityText(String tag, Component text) {
        if (server == null) return;
        if (tag == null || tag.isEmpty() || text == null) {
            System.err.println("Invalid tag or text provided.");
            return;
        }

        ServerLevel world = server.overworld();
        if (world == null) {
            System.err.println("World not found.");
            return;
        }

        // Search for entities within the bounding box
        List<Entity> entities = world.getEntitiesOfClass(Entity.class, box, entity ->
                entity instanceof Display.TextDisplay && entity.getTags().contains(tag)
        );

        // Update the text of the first matching TextDisplay entity
        if (!entities.isEmpty()) {
            Display.TextDisplay textDisplay = (Display.TextDisplay) entities.get(0);

            // Get the NBT data
            TagValueOutput writeView = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            textDisplay.saveWithoutId(writeView); // Write the current NBT of the entity to a compound
            CompoundTag nbt = writeView.buildResult();

            // Modify the "text" field in the NBT
            nbt.putString("text", ComponentSerialization.CODEC.encodeStart(JsonOps.INSTANCE, text).getOrThrow().getAsString());
             // Serialize the Text component to JSON and set it

            // Write the modified NBT back to the entity
            textDisplay.load(TagValueInput.create(ProblemReporter.DISCARDING, server.registryAccess(), nbt)); // Apply the modified NBT back to the entity
        } else {
            System.err.println("No TextDisplay entity found with tag: " + tag + " in the specified area.");
        }
    }

    public static void setDisplayEntityItem(String tag, ItemStack item) {
        if (server == null) return;
        setDisplayEntityItem(tag, ItemManager.save(item, server.registryAccess()));
    }

    public static void setDisplayEntityItem(String tag, Tag itemNbt) {
        if (server == null) return;
        if (tag == null || tag.isEmpty()) {
            System.err.println("Invalid tag or text provided.");
            return;
        }

        ServerLevel world = server.overworld();
        if (world == null) {
            System.err.println("World not found.");
            return;
        }

        // Search for entities within the bounding box
        List<Entity> entities = world.getEntitiesOfClass(Entity.class, box, entity ->
                entity instanceof Display.ItemDisplay && entity.getTags().contains(tag)
        );

        // Update the text of the first matching TextDisplay entity
        if (!entities.isEmpty()) {
            Display.ItemDisplay itemDisplay = (Display.ItemDisplay) entities.get(0);
            TagValueOutput writeView = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
            itemDisplay.saveWithoutId(writeView); // Write the current NBT of the entity to a compound
            CompoundTag nbt = writeView.buildResult();
            nbt.put("item", itemNbt);
            itemDisplay.load(TagValueInput.create(ProblemReporter.DISCARDING, server.registryAccess(), nbt));
        } else {
            System.err.println("No ItemDisplay entity found with tag: " + tag + " in the specified area.");
        }
    }
}
