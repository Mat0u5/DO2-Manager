package net.mat0u5.do2manager.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.GameMode;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class OtherUtils {
    public static String convertSecondsToReadableTime(int totalSeconds) {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;

        StringBuilder readableTime = new StringBuilder();

        if (hours > 0) {
            readableTime.append(hours).append(" Hour");
            if (hours > 1) {
                readableTime.append("s");
            }
        }

        if (minutes > 0) {
            if (readableTime.length() > 0) {
                readableTime.append(", ");
            }
            readableTime.append(minutes).append(" Minute");
            if (minutes > 1) {
                readableTime.append("s");
            }
        }

        if (seconds > 0) {
            if (readableTime.length() > 0) {
                readableTime.append(" and ");
            }
            readableTime.append(seconds).append(" Second");
            if (seconds > 1) {
                readableTime.append("s");
            }
        }

        return readableTime.toString();
    }
    public static String convertTicksToClockTime(long ticks) {

        long totalMilliseconds = (ticks * 50);
        long totalSeconds = totalMilliseconds / 1000;
        long milliseconds = totalMilliseconds % 1000;
        long seconds = totalSeconds % 60;
        long totalMinutes = totalSeconds / 60;
        long minutes = totalMinutes % 60;
        long hours = totalMinutes / 60;

        StringBuilder timeString = new StringBuilder();

        if (hours > 0) {
            timeString.append(hours).append(":");
        }

        if (minutes > 0 || hours > 0) { // show minutes if there are hours or if minutes are non-zero
            if (hours > 0 && minutes < 10) {
                timeString.append("0");
            }
            timeString.append(minutes).append(":");
        }

        if (seconds < 10 && (minutes > 0 || hours > 0)) {
            timeString.append("0");
        }
        timeString.append(seconds);

        if (minutes == 0) {
            String milis = String.valueOf(milliseconds);
            while (milis.endsWith("0")) {
                milis = milis.substring(0,milis.length()-1);
            }
            if (milis.isEmpty() && milliseconds==0) milis = "0";
            timeString.append(".").append(milis);
        }
        String result = timeString.toString();
        if (result.contains("-")) result = "-" + result.replaceAll("-","");
        return result;
    }
    public static String removeQuotes(String str) {
        while (str.startsWith("\"") && str.endsWith("\"")) str = str.substring(1,str.length()-1);
        return str;
    }
    public static int findStringPosInString(String str, String find) {
        int deletedChars = 0;
        while(!str.startsWith(find) && str.length() != 0) {
            str = str.substring(1);
            deletedChars++;
        }
        if (str.startsWith(find)) return deletedChars;
        return -1;
    }
    public static int stringToInt(String str) {
        try {
            int i = Integer.parseInt(str);
            return i;
        }catch (Exception e) {
            return -1;
        }
    }
    public static void executeCommand(MinecraftServer server, String command) {
        CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
        ServerCommandSource commandSource = server.getCommandSource();
        try {
            dispatcher.execute(command, commandSource);
        } catch (CommandSyntaxException e) {
            server.sendMessage(Text.literal("Failed to execute command: " + e.getMessage()).formatted(Formatting.RED));
        }
    }
    public static void broadcastMessage(MinecraftServer server, Text message) {
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            player.sendMessage(message, false);
        }
    }
    public static PlayerEntity getPlayerFromUUIDString(MinecraftServer server, String uuidString) {
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID string: " + uuidString);
            return null;
        }

        PlayerManager playerManager = server.getPlayerManager();
        ServerPlayerEntity playerEntity = playerManager.getPlayer(uuid);

        if (playerEntity != null) {
            return playerEntity; // Player is online
        } else {
            GameProfile profile = server.getUserCache().getByUuid(uuid).orElse(null);
            if (profile != null) {
                // Load player data from disk
                File playerDataFile = server.getSavePath(WorldSavePath.PLAYERDATA).resolve(uuid.toString() + ".dat").toFile();
                if (playerDataFile.exists()) {
                    try {
                        NbtCompound playerData = NbtIo.readCompressed(playerDataFile);
                        ServerWorld world = server.getOverworld();
                        ServerPlayerEntity offlinePlayerEntity = new ServerPlayerEntity(server, world, profile);
                        offlinePlayerEntity.readNbt(playerData);
                        return offlinePlayerEntity;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Player not found: " + uuidString);
            return null;
        }
    }
    public static PlayerEntity getPlayerFromName(MinecraftServer server, String playerName) {
        PlayerManager playerManager = server.getPlayerManager();
        PlayerEntity playerEntity = playerManager.getPlayer(playerName);

        if (playerEntity != null) {
            return playerEntity; // Player is online
        } else {
            GameProfile profile = server.getUserCache().findByName(playerName).orElse(null);
            if (profile != null) {
                UUID uuid = profile.getId();
                File playerDataFile = server.getSavePath(WorldSavePath.PLAYERDATA).resolve(uuid.toString() + ".dat").toFile();
                if (playerDataFile.exists()) {
                    try {
                        NbtCompound playerData = NbtIo.readCompressed(playerDataFile);
                        ServerWorld world = server.getOverworld();
                        PlayerEntity offlinePlayerEntity = new ServerPlayerEntity(server, world, profile);
                        offlinePlayerEntity.readNbt(playerData);
                        return offlinePlayerEntity;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("Player not found: " + playerName);
            return null;
        }
    }
    public static void restartServer(MinecraftServer server) {
        System.out.println("A queued restart has triggered...");
        executeCommand(server,"stop");
    }
    public static boolean isServerEmptyOrOnlyTangoCam(MinecraftServer server) {
        int playerCount = server.getPlayerManager().getPlayerList().size();
        if (playerCount == 0) {
            return true;
        } else if (playerCount == 1) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayerList().get(0);
            return "TangoCam".equals(player.getGameProfile().getName());
        }
        return false;
    }
}
