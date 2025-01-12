package net.mat0u5.do2manager.gui;

import com.mojang.serialization.JsonOps;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.math.Box;

import java.util.ArrayList;
import java.util.List;
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

    Area:   to
     */
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    public static ServerPlayerEntity currentPlayer;
    public static List<DO2RunAbridged> abridgedRuns = new ArrayList<>();
    public static List<DO2RunAbridged> filteredRuns = new ArrayList<>();
    public static final Box box = new Box(-535, 121, 1945, -507, 104, 1968);

    public static String FILTER_RUNTYPE = "all";
    public static String FILTER_DIFFICULTY = "all";
    public static String FILTER_SUCCESS = "all";
    public static String GRAPH = "winpercent";

    public static void onTick(MinecraftServer server) {
        playerChecker(server);
    }

    public static void playerChecker(MinecraftServer server) {
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
        System.out.println("Test1_"+System.nanoTime());
        setDisplayEntityText("stats_name", player.getStyledDisplayName());
        currentPlayer = player;
        abridgedRuns = new ArrayList<>();
        filteredRuns = new ArrayList<>();
        String uuid = player.getUuid().toString();
        for (DO2RunAbridged abridgedRun : Main.allAbridgedRuns) {
            if (abridgedRun.runners.contains(uuid)) {
                abridgedRuns.add(abridgedRun);
            }
        }
        updateFilters();
        System.out.println("Test1_"+System.nanoTime());
    }

    public static void onLastPlayerLeave() {
        currentPlayer = null;
        abridgedRuns = new ArrayList<>();
        filteredRuns = new ArrayList<>();
    }

    public static void updateFilters() {
        if (currentPlayer == null) return;
        filteredRuns = new ArrayList<>();
        for (DO2RunAbridged run : abridgedRuns) {
            if (FILTER_SUCCESS.equalsIgnoreCase("successful") && !run.getSuccessAdvanced(List.of(currentPlayer.getUuid().toString()))) continue;
            if (FILTER_SUCCESS.equalsIgnoreCase("failed") && run.getSuccessAdvanced(List.of(currentPlayer.getUuid().toString()))) continue;
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
    }

    public static void updateAllDisplays() {

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
        setDisplayEntityText("stats_graph_embers", Text.literal("Embers"));
        setDisplayEntityText("stats_graph_crowns", Text.literal("Crowns"));

        if (FILTER_SUCCESS.equalsIgnoreCase("all")) setDisplayEntityText("stats_filters_run_all", Text.literal("§aAll"));
        if (FILTER_SUCCESS.equalsIgnoreCase("casual")) setDisplayEntityText("stats_filters_run_casual", Text.literal("§eCasual"));
        if (FILTER_SUCCESS.equalsIgnoreCase("phase")) setDisplayEntityText("stats_filters_run_phase", Text.literal("§3Phase"));
        if (FILTER_SUCCESS.equalsIgnoreCase("hardcore")) setDisplayEntityText("stats_filters_run_hardcore", Text.literal("§4Hardcore"));

        if (FILTER_DIFFICULTY.equalsIgnoreCase("all")) setDisplayEntityText("stats_filters_difficulty_all", Text.literal("§aAll"));
        if (FILTER_DIFFICULTY.equalsIgnoreCase("easy")) setDisplayEntityText("stats_filters_difficulty_easy", Text.literal("§aEasy"));
        if (FILTER_DIFFICULTY.equalsIgnoreCase("normal")) setDisplayEntityText("stats_filters_difficulty_normal", Text.literal("§eNormal"));
        if (FILTER_DIFFICULTY.equalsIgnoreCase("hard")) setDisplayEntityText("stats_filters_difficulty_hard", Text.literal("§cHard"));
        if (FILTER_DIFFICULTY.equalsIgnoreCase("deadly")) setDisplayEntityText("stats_filters_difficulty_deadly", Text.literal("§4Deadly"));
        if (FILTER_DIFFICULTY.equalsIgnoreCase("deepfrost")) setDisplayEntityText("stats_filters_difficulty_deepfrost", Text.literal("§3Deepfrost"));

        if (FILTER_RUNTYPE.equalsIgnoreCase("all")) setDisplayEntityText("stats_filters_success_all", Text.literal("§aAll"));
        if (FILTER_RUNTYPE.equalsIgnoreCase("successful")) setDisplayEntityText("stats_filters_success_successful", Text.literal("§aSuccessful"));
        if (FILTER_RUNTYPE.equalsIgnoreCase("failed")) setDisplayEntityText("stats_filters_success_failed", Text.literal("§cFailed"));

        if (GRAPH.equalsIgnoreCase("winpercent")) setDisplayEntityText("stats_graph_winpercent", Text.literal("§aWin Percent"));
        if (GRAPH.equalsIgnoreCase("runs")) setDisplayEntityText("stats_graph_runs", Text.literal("§6Runs"));
        if (GRAPH.equalsIgnoreCase("embers")) setDisplayEntityText("stats_graph_embers", Text.literal("§3Embers"));
        if (GRAPH.equalsIgnoreCase("crowns")) setDisplayEntityText("stats_graph_crowns", Text.literal("§6Crowns"));



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
        List<String> uuid = List.of(currentPlayer.getUuid().toString());
        for (DO2RunAbridged run : filteredRuns) {
            if (run.getSuccessAdvanced(uuid)) {
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
        setDisplayEntityText("stats_info_runs_winpercent", Text.literal("Win Percentage: §6" + winPercentage+"%"));
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
        if (tag == null || tag.isEmpty() || text == null) {
            System.err.println("Invalid tag or text provided.");
            return;
        }

        // Define the area to search for the TextDisplay entities
        final Box box = new Box(-535, 121, 1945, -507, 104, 1968);

        // Get the world (replace `yourServerWorld` with the actual ServerWorld instance)
        ServerWorld world = server.getOverworld(); // Replace with the correct dimension if necessary
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
}
