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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RunInfoParser {
    public static final List<Integer> artiModelDataList = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58);
    public static final HashMap<Integer, Integer> artifactWorth = new HashMap<Integer, Integer>() {{
            put(53, 66);
            put(48, 64);
            put(54, 62);
        put(37, 60);
            put(46, 57);
        put(36, 54);
        put(38, 52);
        put(14, 50);
        put(44, 48);
        put(11, 46);
        put(16, 40);
            put(58, 44);
            put(52, 44);
        put(39, 38);
            put(50, 37);
        put(10, 36);
        put(19, 34);
            put(49, 33);
        put(15, 32);
        put(31, 30);
            put(56, 29);
            put(47, 27);
            put(51, 26);
            put(57, 25);
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
            put(55, 5);
    }};
    public static final HashMap<Integer, String> artifactNames = new HashMap<Integer, String>() {{
        put(37, "key");
        put(36, "mug");
        put(38, "skadoodler");
        put(14, "slab");
        put(44, "staff");
        put(11, "rocket");
        put(16, "gem");
        put(39, "pickaxe");
        put(10, "watch");
        put(19, "golden_eye");
        put(15, "goggles");
        put(31, "stache");
        put(20, "bionic_eye");
        put(41, "helm");
        put(35, "wand");
        put(18, "bandana");
        put(40, "apron");
        put(12, "chisel");
        put(13, "goat");
        put(32, "pearl");
        put(34, "loop");
        put(29, "tome");
        put(28, "jar");
        put(30, "slippers");
        put(33, "shades");
        put(17, "waffle");
        put(43, "axe");
        put(42, "hood");

        put(55, "coin");
        put(57, "payday");
        put(51, "chip");
        put(47, "notes");
        put(56, "fist");
        put(49, "tie");
        put(50, "trigger");
        put(52, "spanner");
        put(58, "stopwatch");
        put(46, "orb");
        put(54, "laptop");
        put(48, "cloak");
        put(53, "mat");
    }};
    public static final LinkedHashMap<Integer, String> artifactNamesByValue = new LinkedHashMap<Integer, String>() {{
            put(66,"mat");
            put(64,"cloak");
            put(62,"laptop");
        put(60, "key");
            put(57,"orb");
        put(54, "mug");
        put(52, "skadoodler");
        put(50, "slab");
        put(48, "staff");
        put(46, "rocket");
            put(44,"stopwatch");
        //    put(44,"spanner");
        put(40, "gem");
        put(38, "pickaxe");
            put(37,"trigger");
        put(36, "watch");
        put(34, "golden_eye");
            put(33,"tie");
        put(32, "goggles");
        put(30, "stache");
            put(29,"fist");
            put(27,"notes");
            put(26,"chip");
            put(25,"payday");
        put(24, "bionic_eye");
        put(23, "helm");
        put(22, "wand");
        put(21, "bandana");
        put(20, "apron");
        put(19, "chisel");
        put(18, "goat");
        put(14, "pearl");
        put(13, "loop");
        put(12, "tome");
        put(11, "jar");
        put(10, "slippers");
        put(9, "shades");
        put(8, "waffle");
        put(7, "axe");
        put(6, "hood");
            put(5,"coin");
    }};
    public static java.lang.Integer getRunNum(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"TangoCam","TestObj");
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
            Main.speedrun = fastestRun;
            if (isSpeedrun) {
                OtherUtils.broadcastMessage(player.getServer(), Text.translatable("§6This speedrun will be compared with " + player.getEntityName() + "'s fastest "+Main.currentRun.getFormattedDifficulty()+" level " + Main.currentRun.getCompassLevel()+"§6 run."));
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
    public static boolean isCrown(ItemStack itemStack) {
        if (!ItemManager.getItemId(itemStack).equalsIgnoreCase("minecraft:iron_nugget")) return false;
        return ItemManager.getModelData(itemStack) == 2;
    }
    public static boolean isCoin(ItemStack itemStack) {
        if (!ItemManager.getItemId(itemStack).equalsIgnoreCase("minecraft:iron_nugget")) return false;
        return ItemManager.getModelData(itemStack) == 1;
    }
    public static int getArtifactWorth(ItemStack itemStack) {
        if (!isDungeonArtifact(itemStack)) return 0;
        if (artifactWorth.containsKey(ItemManager.getModelData(itemStack))) {
            return artifactWorth.get(ItemManager.getModelData(itemStack));
        }
        return 0;
    }
    public static String getArtifactName(ItemStack itemStack) {
        if (!isDungeonArtifact(itemStack)) return "";
        int modelData = ItemManager.getModelData(itemStack);
        if (modelData == -1) return "";
        if (artifactNames.containsKey(modelData)) {
            return artifactNames.get(modelData);
        }
        return "";
    }
}
