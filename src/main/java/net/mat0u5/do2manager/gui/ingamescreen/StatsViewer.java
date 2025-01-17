package net.mat0u5.do2manager.gui.ingamescreen;

import com.mojang.serialization.JsonOps;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

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
    public static ServerPlayerEntity currentPlayer;
    public static List<DO2RunAbridged> abridgedRuns = new ArrayList<>();
    public static List<DO2RunAbridged> filteredRuns = new ArrayList<>();
    public static final Box box = new Box(-535, 121, 1945, -507, 104, 1968);
    public static final Box interactableBox = new Box(-533, 120, 1945.5, -508, 106, 1946.1);
    public static HashMap<Double, String> selectMap = new HashMap<Double, String>() {{
        put(117.0918, "stats_filters_run_all");
        put(116.5918, "stats_filters_run_casual");
        put(116.0918, "stats_filters_run_phase");
        put(115.5918, "stats_filters_run_hardcore");

        put(114.8418, "stats_filters_difficulty_all");
        put(114.4043, "stats_filters_difficulty_easy");
        put(113.9043, "stats_filters_difficulty_normal");
        put(113.4043, "stats_filters_difficulty_hard");
        put(112.9043, "stats_filters_difficulty_deadly");
        put(112.4043, "stats_filters_difficulty_deepfrost");


        put(111.6543, "stats_filters_success_all");
        put(111.1543, "stats_filters_success_successful");
        put(110.6543, "stats_filters_success_failed");


        put(108.8410, "stats_graph_winpercent");
        put(108.4043, "stats_graph_runs");
        put(107.9668, "stats_graph_embers");
        put(107.5293, "stats_graph_crowns");
        put(107.0918, "stats_graph_totalembers");
        put(106.6543, "stats_graph_totalcrowns");

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
        cursor.set(DataComponentTypes.CUSTOM_NAME, Text.of("Cursor"));
        if (!box.intersects(currentPlayer.getBoundingBox())) {
            currentPlayer.getInventory().removeOne(cursor);
            return;
        }
        if (ItemManager.getModelData(currentPlayer.getMainHandStack()) == 521) return;
        removeCursorFromPlayer(currentPlayer);
        if (currentPlayer.getMainHandStack().isEmpty()) {
            currentPlayer.getInventory().setStack(currentPlayer.getInventory().selectedSlot, cursor);
        }
    }

    public static void removeCursorFromPlayer(ServerPlayerEntity player) {
        ItemStack cursor = new ItemStack(Items.IRON_NUGGET, 1);
        ItemManager.setModelData(cursor, 521);
        cursor.set(DataComponentTypes.CUSTOM_NAME, Text.of("Cursor"));
        for (int pos = 0; pos < player.getInventory().size(); pos++) {
            ItemStack itemAtPos = player.getInventory().getStack(pos);
            if (ItemManager.getModelData(itemAtPos) == 521) {
                player.getInventory().removeStack(pos);
            }
        }

    }

    public static void onPlayerUse(ServerPlayerEntity player) {
        if (Main.statsViewerDisabled) return;
        try {
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
            player.getServerWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 0.8F, 1.0F);
            updateFilters();
        }catch(Exception e) {
            Main.LOGGER.error(e.getMessage());
        }
    }

    public static void playerChecker(MinecraftServer server) {
        if (Main.statsViewerDisabled) return;
        List<ServerPlayerEntity> playersInBox = new ArrayList<>();
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
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
            for (ServerPlayerEntity player : playersInBox) {
                if (currentPlayer.getUuid().equals(player.getUuid())) {
                    containsOldPlayer = true;
                    break;
                }
            }
        }

        if (containsOldPlayer) {
            // Player is still in the bounding box
            return;
        }
        for (ServerPlayerEntity player : playersInBox) {
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

    public static void setCurrentPlayer(ServerPlayerEntity player) {
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

    private static void actualSetCurrentPlayer(ServerPlayerEntity player) {
        if (currentPlayer != null) {
            removeCursorFromPlayer(currentPlayer);
        }
        FILTER_RUNTYPE = "all";
        FILTER_DIFFICULTY = "all";
        FILTER_SUCCESS = "all";
        GRAPH = "winpercent";

        setDisplayEntityText("stats_name", player.getStyledDisplayName());
        currentPlayer = player;
        List<DO2RunAbridged> newAbridgedRuns = new ArrayList<>();
        String uuid = player.getUuid().toString();
        for (DO2RunAbridged abridgedRun : Main.allAbridgedRuns) {
            if (abridgedRun.runners.contains(uuid)) {
                newAbridgedRuns.add(abridgedRun);
            }
        }
        abridgedRuns = newAbridgedRuns;
        updateFilters();
    }

    public static void onLastPlayerLeave() {
        if (currentPlayer != null) {
            removeCursorFromPlayer(currentPlayer);
        }
        currentPlayer = null;
        abridgedRuns = new ArrayList<>();
        filteredRuns = new ArrayList<>();
    }

    public static void updateFilters() {
        if (currentPlayer == null) return;
        filteredRuns = new ArrayList<>();
        for (DO2RunAbridged run : abridgedRuns) {
            if (FILTER_SUCCESS.equalsIgnoreCase("successful") && !run.getSuccessFor(currentPlayer.getUuid().toString())) continue;
            if (FILTER_SUCCESS.equalsIgnoreCase("failed") && run.getSuccessFor(currentPlayer.getUuid().toString())) continue;
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
        GraphGenerator.generateGraph(server.getOverworld(), new ArrayList<>(filteredRuns), GRAPH, noFilters, currentPlayer);
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
        NbtElement emptyCheckboxNbt = emptyCheckbox.encode(server.getRegistryManager());

        ItemStack fullCheckbox = new ItemStack(Items.IRON_NUGGET, 1);
        ItemManager.setModelData(fullCheckbox, 519);
        NbtElement fullCheckboxNbt = fullCheckbox.encode(server.getRegistryManager());

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
        HitResult hitResult = currentPlayer.raycast(25, 0.0F, false);
        // Check if the raycast hit something
        if (hitResult.getType() == HitResult.Type.MISS) {
            return; // Player isn't looking at anything within range
        }

        Vec3d targetPos = hitResult.getPos(); // Position the player is looking at

        if (!interactableBox.contains(targetPos)) return;

        // Find the ItemEntity with the tag "cursor"
        DisplayEntity.ItemDisplayEntity itemEntity = currentPlayer.getServerWorld().getEntitiesByClass(
                DisplayEntity.ItemDisplayEntity.class,
                interactableBox,
                entity -> entity.getCommandTags().contains("cursor")
        ).stream().findFirst().orElse(null);

        if (itemEntity == null) {
            return; // No ItemEntity with the tag "cursor" found
        }
        // Move the ItemEntity to the target position
        itemEntity.setPos(targetPos.x, targetPos.y, targetPos.z);
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


        DisplayEntity.ItemDisplayEntity highlightEntity = currentPlayer.getServerWorld().getEntitiesByClass(
                DisplayEntity.ItemDisplayEntity.class,
                interactableBox,
                entity -> entity.getCommandTags().contains("highlight")
        ).stream().findFirst().orElse(null);
        if (highlightEntity == null) return;

        if (pointingAt == null) {
            highlightEntity.setPos(highlightEntity.getX(), highlightEntity.getY(), 1945.7);
            return;
        }
        highlightEntity.setPos(highlightEntity.getX(), pointingAt.getKey(), 1946.0);
    }

    public static Map.Entry<Double, String> getPointingBox() {
        if (currentPlayer == null) return null;
        HitResult hitResult = currentPlayer.raycast(25, 0.0F, false);
        if (hitResult.getType() == HitResult.Type.MISS) return null;
        Vec3d targetPos = hitResult.getPos();
        if (!interactableBox.contains(targetPos)) return null;

        if (targetPos.x > -529.7) return null;
        if (targetPos.x < -533) return null;
        if (targetPos.y > 117.5) return null;
        if (targetPos.y < 106) return null;

        for (Map.Entry<Double, String> entry : selectMap.entrySet()) {
            Double posY = entry.getKey();
            if (Math.abs(targetPos.y-posY) < 0.43) return entry;
        }

        return null;
    }

    public static void updateDisplayFilters() {
        setDisplayEntityText("stats_filters_run_all", Text.literal("All"));
        setDisplayEntityText("stats_filters_run_casual", Text.literal("Casual"));
        setDisplayEntityText("stats_filters_run_phase", Text.literal("Phase"));
        setDisplayEntityText("stats_filters_run_hardcore", Text.literal("Hardcore"));

        setDisplayEntityText("stats_filters_difficulty_all", Text.literal("All"));
        setDisplayEntityText("stats_filters_difficulty_easy", Text.literal("Easy"));
        setDisplayEntityText("stats_filters_difficulty_normal", Text.literal("Normal"));
        setDisplayEntityText("stats_filters_difficulty_hard", Text.literal("Hard"));
        setDisplayEntityText("stats_filters_difficulty_deadly", Text.literal("Deadly"));
        setDisplayEntityText("stats_filters_difficulty_deepfrost", Text.literal("Deepfrost"));

        setDisplayEntityText("stats_filters_success_all", Text.literal("All"));
        setDisplayEntityText("stats_filters_success_successful", Text.literal("Successful"));
        setDisplayEntityText("stats_filters_success_failed", Text.literal("Failed"));

        setDisplayEntityText("stats_graph_winpercent", Text.literal("Win Percent"));
        setDisplayEntityText("stats_graph_runs", Text.literal("Runs"));
        setDisplayEntityText("stats_graph_embers", Text.literal("Run Embers"));
        setDisplayEntityText("stats_graph_crowns", Text.literal("Run Crowns"));
        setDisplayEntityText("stats_graph_totalembers", Text.literal("Total Embers"));
        setDisplayEntityText("stats_graph_totalcrowns", Text.literal("Total Crowns"));

        if (FILTER_RUNTYPE.equalsIgnoreCase("all")) setDisplayEntityText("stats_filters_run_all", Text.literal("§aAll"));
        else if (FILTER_RUNTYPE.equalsIgnoreCase("casual")) setDisplayEntityText("stats_filters_run_casual", Text.literal("§eCasual"));
        else if (FILTER_RUNTYPE.equalsIgnoreCase("phase")) setDisplayEntityText("stats_filters_run_phase", Text.literal("§3Phase"));
        else if (FILTER_RUNTYPE.equalsIgnoreCase("hardcore")) setDisplayEntityText("stats_filters_run_hardcore", Text.literal("§4Hardcore"));

        if (FILTER_DIFFICULTY.equalsIgnoreCase("all")) setDisplayEntityText("stats_filters_difficulty_all", Text.literal("§aAll"));
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("easy")) setDisplayEntityText("stats_filters_difficulty_easy", Text.literal("§aEasy"));
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("normal")) setDisplayEntityText("stats_filters_difficulty_normal", Text.literal("§eNormal"));
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("hard")) setDisplayEntityText("stats_filters_difficulty_hard", Text.literal("§cHard"));
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("deadly")) setDisplayEntityText("stats_filters_difficulty_deadly", Text.literal("§4Deadly"));
        else if (FILTER_DIFFICULTY.equalsIgnoreCase("deepfrost")) setDisplayEntityText("stats_filters_difficulty_deepfrost", Text.literal("§3Deepfrost"));

        if (FILTER_SUCCESS.equalsIgnoreCase("all")) setDisplayEntityText("stats_filters_success_all", Text.literal("§aAll"));
        else if (FILTER_SUCCESS.equalsIgnoreCase("successful")) setDisplayEntityText("stats_filters_success_successful", Text.literal("§aSuccessful"));
        else if (FILTER_SUCCESS.equalsIgnoreCase("failed")) setDisplayEntityText("stats_filters_success_failed", Text.literal("§cFailed"));

        if (GRAPH.equalsIgnoreCase("winpercent")) setDisplayEntityText("stats_graph_winpercent", Text.literal("§aWin Percent"));
        else if (GRAPH.equalsIgnoreCase("runs")) setDisplayEntityText("stats_graph_runs", Text.literal("§6Runs"));
        else if (GRAPH.equalsIgnoreCase("embers")) setDisplayEntityText("stats_graph_embers", Text.literal("§3Run Embers"));
        else if (GRAPH.equalsIgnoreCase("crowns")) setDisplayEntityText("stats_graph_crowns", Text.literal("§6Run Crowns"));
        else if (GRAPH.equalsIgnoreCase("totalembers")) setDisplayEntityText("stats_graph_totalembers", Text.literal("§3Total Embers"));
        else if (GRAPH.equalsIgnoreCase("totalcrowns")) setDisplayEntityText("stats_graph_totalcrowns", Text.literal("§6Total Crowns"));
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
        String uuid = currentPlayer.getUuid().toString();
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

        setDisplayEntityText("stats_info_runs", Text.literal("Runs: §b" + runsNum));
        setDisplayEntityText("stats_info_runs_successful", Text.literal("Successful Runs: §a" + successfulRuns));
        setDisplayEntityText("stats_info_runs_failed", Text.literal("Failed Runs: §c" + unsuccessfulRuns));
        setDisplayEntityText("stats_info_runs_winpercent", Text.literal("Win Percentage:§6" + winPercentage+"%"));
        setDisplayEntityText("stats_info_runs_winstreak", Text.literal("Biggest Win Streak: §a" + biggestWinStreak));
        setDisplayEntityText("stats_info_runs_lossstreak", Text.literal("Biggest Loss Streak: §c" + biggestLossStreak));

        setDisplayEntityText("stats_info_runs_playtime", Text.literal("Play Time: §6" + OtherUtils.convertTicksToClockTime(totalPlayTime,false)));
        setDisplayEntityText("stats_info_runs_totalembers", Text.literal("Total Embers: §3" + totalEmbers));
        setDisplayEntityText("stats_info_runs_totalcrowns", Text.literal("Total Crowns: §e" + totalCrowns));
        setDisplayEntityText("stats_info_runs_avglength", Text.literal("Avg. Run Length: §6" + OtherUtils.convertTicksToClockTime((long) averageRunLength,false)));
        setDisplayEntityText("stats_info_runs_avgembers", Text.literal("Avg. Run Embers: §3" + averageEmbers));
        setDisplayEntityText("stats_info_runs_avgcrowns", Text.literal("Avg. Run Crowns: §e" + averageCrowns));
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

    public static void setDisplayEntityText(String tag, Text text) {
        if (server == null) return;
        if (tag == null || tag.isEmpty() || text == null) {
            System.err.println("Invalid tag or text provided.");
            return;
        }

        // Define the area to search for the TextDisplay entities
        final Box box = new Box(-535, 121, 1945, -507, 104, 1968);

        ServerWorld world = server.getOverworld();
        if (world == null) {
            System.err.println("World not found.");
            return;
        }

        // Search for entities within the bounding box
        List<Entity> entities = world.getEntitiesByClass(Entity.class, box, entity ->
                entity instanceof DisplayEntity.TextDisplayEntity && entity.getCommandTags().contains(tag)
        );

        // Update the text of the first matching TextDisplay entity
        if (!entities.isEmpty()) {
            DisplayEntity.TextDisplayEntity textDisplay = (DisplayEntity.TextDisplayEntity) entities.get(0);

            // Get the NBT data
            NbtCompound nbt = new NbtCompound();
            textDisplay.writeNbt(nbt); // Write the current NBT of the entity to a compound

            // Modify the "text" field in the NBT
            nbt.putString("text", TextCodecs.STRINGIFIED_CODEC.encodeStart(JsonOps.INSTANCE, text).getOrThrow().getAsString());
             // Serialize the Text component to JSON and set it

            // Write the modified NBT back to the entity
            textDisplay.readNbt(nbt); // Apply the modified NBT back to the entity
        } else {
            System.err.println("No TextDisplay entity found with tag: " + tag + " in the specified area.");
        }
    }

    public static void setDisplayEntityItem(String tag, ItemStack item) {
        if (server == null) return;
        setDisplayEntityItem(tag, item.encode(server.getRegistryManager()));
    }

    public static void setDisplayEntityItem(String tag, NbtElement itemNbt) {
        if (server == null) return;
        if (tag == null || tag.isEmpty()) {
            System.err.println("Invalid tag or text provided.");
            return;
        }

        // Define the area to search for the TextDisplay entities
        final Box box = new Box(-535, 121, 1945, -507, 104, 1968);

        ServerWorld world = server.getOverworld();
        if (world == null) {
            System.err.println("World not found.");
            return;
        }

        // Search for entities within the bounding box
        List<Entity> entities = world.getEntitiesByClass(Entity.class, box, entity ->
                entity instanceof DisplayEntity.ItemDisplayEntity && entity.getCommandTags().contains(tag)
        );

        // Update the text of the first matching TextDisplay entity
        if (!entities.isEmpty()) {
            DisplayEntity.ItemDisplayEntity itemDisplay = (DisplayEntity.ItemDisplayEntity) entities.get(0);
            NbtCompound nbt = new NbtCompound();
            itemDisplay.writeNbt(nbt);
            nbt.put("item", itemNbt);
            itemDisplay.readNbt(nbt);
        } else {
            System.err.println("No ItemDisplay entity found with tag: " + tag + " in the specified area.");
        }
    }
}
