package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.config.ConfigManager;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.ScoreboardUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.TagCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.include.com.google.common.base.Predicate;

import java.util.*;

public class RunInfoParser {
    public static final List<Integer> artiModelDataList = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57);
    public static final HashMap<Integer, Integer> artifactWorth = new HashMap<Integer, Integer>() {{
        put(37, 60);
        put(36, 54);
        put(38, 52);
        put(14, 50);
        put(44, 48);
        put(11, 46);
        put(16, 40);
        put(39, 38);
        put(10, 36);
        put(19, 34);
        put(15, 32);
        put(31, 30);
        put(20, 24);
        put(41, 23);
        put(35, 22);
        put(18, 21);
        put(40, 20);
        put(12, 19);
        put(13, 18);
        put(32, 14);
        put(34, 13);
        put(29, 12);
        put(28, 11);
        put(30, 10);
        put(33, 9);
        put(17, 8);
        put(43, 7);
        put(42, 6);
    }};

    public static java.lang.Integer getRunNum(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"TangoCam","TestObj");
    }
    public static java.lang.Integer getRunDifficulty(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"Difficulty","DOM_Difficulty");
    }
    public static java.lang.Integer getRunLength(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"#Tick","DOM_Timer");
    }
    public static java.lang.Integer getPlayerEmbers(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"TempPlayerEmbers","OverallEmbers");
    }
    public static String getFormattedRunLength(MinecraftServer server) {
        java.lang.Integer ticks = getRunLength(server);
        if (ticks == null) return "null";
        return OtherUtils.convertSecondsToReadableTime(ticks / 20);
    }
    public static DO2Run getFastestPlayerRunMatchingCurrent(PlayerEntity player) {
        List<DO2Run> allRuns = DatabaseManager.getRunsByCriteria(List.of("runners = \"" + player.getUuidAsString()+"\""));
        DO2Run fastestRun = null;
        for (DO2Run run : allRuns) {
            if (!run.run_type.equalsIgnoreCase("testing") && run.getSuccess() && run.difficulty==Main.currentRun.difficulty&& run.getCompassLevel() == Main.currentRun.getCompassLevel() && Main.currentRun.getCompassLevel() != -1) {
                if (fastestRun == null) {
                    fastestRun = run;
                    continue;
                }
                if (run.run_length <= fastestRun.run_length) fastestRun = run;
            }
        }
        return fastestRun;
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
        if (runners.isEmpty()) return runners;
        for (PlayerEntity runner : runners) {
            if (!isValidRunner(runner)) {
                runners.remove(runner);
                if (runners.isEmpty()) return runners;
            }
        }
        return runners;
    }
    public static boolean isValidRunner(PlayerEntity runner) {
        if (runner.isSpectator() || runner.isCreative() || !isInCitadelRegion(runner) || runner.isDead()) return false;
        return true;
    }

    public static ItemStack getRunnersCompass(MinecraftServer server) {
        for (PlayerEntity player : getCurrentRunners(server)) {
            for (ItemStack itemStack : player.getInventory().main) {
                if (isDungeonCompass(itemStack)) return itemStack;
            }
        }
        return null;
    }
    public static ItemStack getRunnersArtifact(MinecraftServer server) {
        for (PlayerEntity player : getCurrentRunners(server)) {
            for (ItemStack itemStack : player.getInventory().main) {
                if (isDungeonArtifact(itemStack)) return itemStack;
            }
        }
        return null;
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
    public static boolean isDungeonCompass(ItemStack itemStack) {
        if (!ItemManager.getItemId(itemStack).equalsIgnoreCase("minecraft:compass")) return false;
        if (!ItemManager.hasNbtEntry(itemStack, "LodestoneTracked")) return false;
        return true;
    }
    public static boolean isDungeonArtifact(ItemStack itemStack) {
        if (!ItemManager.getItemId(itemStack).equalsIgnoreCase("minecraft:iron_nugget")) return false;
        if (!artiModelDataList.contains(ItemManager.getModelData(itemStack))) return false;
        return true;
    }
    public static boolean isEmber(ItemStack itemStack) {
        if (!ItemManager.getItemId(itemStack).equalsIgnoreCase("minecraft:iron_nugget")) return false;
        return ItemManager.getModelData(itemStack) == 3;
    }
    public static int getArtifactWorth(ItemStack itemStack) {
        if (!isDungeonArtifact(itemStack)) return 0;
        if (artifactWorth.containsKey(ItemManager.getModelData(itemStack))) return artifactWorth.get(ItemManager.getModelData(itemStack));
        return 0;
    }
}
