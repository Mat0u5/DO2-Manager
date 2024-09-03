package net.mat0u5.do2manager.utils;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class PermissionManager {
    public static boolean isModOwner(ServerPlayerEntity player) {
        if (player == null) return false;
        return player.getUuidAsString().equalsIgnoreCase("41682eb6-2b32-4f52-abc9-c15a9d53c83e");
    }
    public static boolean isAdmin(ServerPlayerEntity player) {
        if (player == null) return false;
        if (isModOwner(player)) return true;
        if (!player.hasPermissionLevel(2)) return false;
        return true;
    }

    public static boolean isTCGGameMaster(ServerPlayerEntity player) {
        if (player == null) return false;
        if (isAdmin(player)) return true;
        return player.getCommandTags().contains("TCGGameMaster");
    }
    public static boolean isModOwner(PlayerEntity player) {
        return isModOwner((ServerPlayerEntity) player);
    }
    public static boolean isAdmin(PlayerEntity player) {
        return isAdmin((ServerPlayerEntity) player);
    }
    public static boolean isTCGGameMaster(PlayerEntity player) {
        return isTCGGameMaster((ServerPlayerEntity) player);
    }
}
