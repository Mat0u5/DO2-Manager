package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.ScoreboardUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunInfoParser {
    public static java.lang.Integer getRunNum(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"TangoCam","RunCount");
    }
    public static java.lang.Integer getRunDifficulty(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"Difficulty","DeckedOutGame");
    }
    public static java.lang.Integer getRunLength(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"#Tick","DOM_Timer");
    }
    public static java.lang.Integer getPlayerEmbers(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"TempPlayerEmbers","OverallEmbers");
    }
    public static java.lang.Integer getPlayerCrowns(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"TempPlayerCrowns","OverallCrowns");
    }
    public static String getFormattedRunLength(MinecraftServer server) {
        java.lang.Integer ticks = getRunLength(server);
        if (ticks == null) return "null";
        return OtherUtils.convertSecondsToReadableTime(ticks / 20);
    }
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public static void getFastestPlayerRunMatchingCurrent(PlayerEntity player) {
        boolean isSpeedrun = Main.config.getProperty("current_run_is_speedrun").equalsIgnoreCase("true");
        executor.submit(()  -> {
            List<DO2RunAbridged> allRunsAbridged = DatabaseManager.getAbridgedRunsByCriteria(List.of("runners = \"" + player.getUuidAsString()+"\""));
            DO2RunAbridged fastestRun = null;
            for (DO2RunAbridged run : allRunsAbridged) {
                if (!run.run_type.equalsIgnoreCase("testing") && run.getSuccess() && run.difficulty==Main.currentRun.difficulty&& run.compass_level == Main.currentRun.getCompassLevel() && Main.currentRun.getCompassLevel() != -1) {
                    if (fastestRun == null) {
                        fastestRun = run;
                        continue;
                    }
                    if (run.run_length <= fastestRun.run_length) fastestRun = run;
                }
            }
            if (fastestRun != null) {
                List<DO2Run> finalSpeedrun = DatabaseManager.getRunsByAbridgedRuns(List.of(fastestRun));
                if (finalSpeedrun.size() == 1) {
                    Main.speedrun = finalSpeedrun.get(0);
                    if (isSpeedrun) {
                        OtherUtils.broadcastMessage(player.getServer(), Text.translatable("§6This speedrun will be compared with " + player.getEntityName() + "'s fastest "+Main.currentRun.getFormattedDifficulty()+" level " + Main.currentRun.getCompassLevel()+"§6 run."));
                    }
                }
            }
            return fastestRun;
        });
    }
    public static List<PlayerEntity> getCurrentRunners(MinecraftServer server) {
        List<PlayerEntity> runners = new ArrayList<>();
        Collection<ServerPlayerEntity> players = server.getPlayerManager().getPlayerList();
        for (ServerPlayerEntity player : players) {
            java.lang.Integer isCurrentRunner = ScoreboardUtils.getPlayerScore(server,player,"CurrentRunner");
            if (isCurrentRunner == null) continue;
            if (isCurrentRunner == 1) {
                runners.add(player);
            }
        }
        return runners;
    }
    public static List<PlayerEntity> getCurrentAliveRunners(MinecraftServer server) {
        List<PlayerEntity> runners = getCurrentRunners(server);
        List<PlayerEntity> aliveRunners = new ArrayList<>();
        if (runners.isEmpty()) return aliveRunners;
        for (PlayerEntity runner : runners) {
            if (isValidRunner(runner)) {
                aliveRunners.add(runner);
            }
        }
        return aliveRunners;
    }
    public static boolean isValidRunner(PlayerEntity runner) {
        if (runner.isSpectator() || runner.isCreative() || !isInCitadelRegion(runner) || runner.isDead()) return false;
        return true;
    }

    public static ItemStack getDeck(MinecraftServer server) {
        BlockPos startGameHopper = new BlockPos(-565, 113, 1980);
        BlockPos startDropper = new BlockPos(-565, 111, 1980);
        BlockPos stashDeckHopper = new BlockPos(-551, 122, 1971);
        List<ItemStack> items = ItemManager.getHopperItems(server.getOverworld(), startGameHopper);
        if (items == null || items.isEmpty()) {
            items = ItemManager.getDropperItems(server.getOverworld(),startDropper);
            if (items == null || items.isEmpty()) {
                items = ItemManager.getHopperItems(server.getOverworld(), stashDeckHopper);
                if (items == null || items.isEmpty()) return null;
            }
        }


        for (ItemStack item : items) {
            if (ItemManager.getItemId(item).contains("shulker_box")) {
                return item;
            }
        }
        return null;
    }
    public static boolean isInCitadelRegion(PlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        int minX = -670;
        int maxX = -445;
        int minY = -64;
        int maxY = 67;
        int minZ = 1830;
        int maxZ = 2060;

        return playerPos.getX() >= minX && playerPos.getX() <= maxX &&
                playerPos.getY() >= minY && playerPos.getY() <= maxY &&
                playerPos.getZ() >= minZ && playerPos.getZ() <= maxZ;
    }

}
