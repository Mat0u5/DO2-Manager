package net.mat0u5.do2manager.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class PermissionManager {
    public static boolean isModOwner(ServerPlayer player) {
        if (player == null) return false;
        return player.getTags().contains("Admins");
    }
    public static boolean isAdmin(ServerPlayer player) {
        if (player == null) return false;
        if (isModOwner(player)) return true;
        return player.level().getServer().getPlayerList().isOp(player.nameAndId());
    }

    public static boolean isTCGGameMaster(ServerPlayer player) {
        if (player == null) return false;
        if (isAdmin(player)) return true;
        return player.getTags().contains("TCGGameMaster");
    }
    public static boolean isModOwner(Player player) {
        return isModOwner((ServerPlayer) player);
    }
    public static boolean isAdmin(Player player) {
        return isAdmin((ServerPlayer) player);
    }
    public static boolean isTCGGameMaster(Player player) {
        return isTCGGameMaster((ServerPlayer) player);
    }
    public static boolean isMapGhost(ServerPlayer player) {
        if (player == null) return false;
        return player.getTags().contains("MapGhost");
    }
}
