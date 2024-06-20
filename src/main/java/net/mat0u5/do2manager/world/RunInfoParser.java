package net.mat0u5.do2manager.world;

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
import org.spongepowered.include.com.google.common.base.Predicate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RunInfoParser {
    public static final List<Integer> artiModelDataList = Arrays.asList(1,2);

    public static java.lang.Integer getRunNum(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"TangoCam","TestObj");
    }
    public static java.lang.Integer getRunLength(MinecraftServer server) {
        return ScoreboardUtils.getPlayerScore(server,"#Tick","DOM_Timer");
    }
    public static String getFormattedRunLength(MinecraftServer server) {
        java.lang.Integer ticks = getRunLength(server);
        if (ticks == null) return "null";
        return OtherUtils.convertSecondsToReadableTime(ticks / 20);
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
    public static ItemStack getRunnersCompass(MinecraftServer server) {
        for (PlayerEntity player : getCurrentRunners(server)) {
            for (ItemStack itemStack : player.getInventory().main) {
                if (ItemManager.getItemId(itemStack) == "minecraft:compass") {
                    return itemStack;
                }
            }
        }
        return null;
    }
    public static ItemStack getRunnersArtifact(MinecraftServer server) {
        for (PlayerEntity player : getCurrentRunners(server)) {
            for (ItemStack itemStack : player.getInventory().main) {
                if (ItemManager.getItemId(itemStack) == "minecraft:iron_nugget" && artiModelDataList.contains(getModelData(itemStack))) {
                    return itemStack;
                }
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
    public static int getModelData(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt != null && nbt.contains("CustomModelData")) return nbt.getInt("CustomModelData");
        return -1;
    }
}
