package net.mat0u5.do2manager.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.SignEditScreen;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
        ServerCommandSource commandSource = server.getCommandSource().withSilent();
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
        return getPlayerFromName(server, DatabaseManager.getPlayerNameFromUUID(uuidString));
    }
    public static String fetchPlayerNameFromMojangAPI(UUID uuid) {
        String urlString = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "");
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == 200) {
                InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                reader.close();
                return json.get("name").getAsString();
            } else {
                System.out.println("Failed to fetch player name, response code: " + conn.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static UUID getUUIDFromString(String uuidString) {
        UUID uuid;
        try {
            uuid = UUID.fromString(uuidString.trim());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid UUID string: " + uuidString);
            return null;
        }
        return uuid;
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
    public static void playGuiClickSound(PlayerEntity player) {
        if (player != null && player.getWorld() != null) {
            player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.PLAYERS, 0.5F, 1.0F);
        }
    }
    public static List<BlockPos> getPositionsFromString(String str) {
        List<BlockPos> posList = new ArrayList<>();
        str = str.replaceAll(" ","");
        if (str.contains(";")) {
            for (String pos : str.split(";")) {
                try {
                    int x = Integer.parseInt(pos.split(",")[0]);
                    int y = Integer.parseInt(pos.split(",")[1]);
                    int z = Integer.parseInt(pos.split(",")[2]);
                    posList.add(new BlockPos(x,y,z));
                }catch(Exception e) {}
            }
        }
        else {
            try {
                int x = Integer.parseInt(str.split(",")[0]);
                int y = Integer.parseInt(str.split(",")[1]);
                int z = Integer.parseInt(str.split(",")[2]);
                posList.add(new BlockPos(x,y,z));
            }catch(Exception e) {}
        }
        return posList;
    }
    public static boolean isHoldingAdminKey(PlayerEntity player) {
        // Get the item stacks for main hand and offhand
        ItemStack mainHandItem = player.getMainHandStack();
        ItemStack offHandItem = player.getOffHandStack();

        // Get the item names
        String mainHandItemName = mainHandItem.isEmpty() ? "Empty" : mainHandItem.getName().getString();
        String offHandItemName = offHandItem.isEmpty() ? "Empty" : offHandItem.getName().getString();

        // Create and return the result text
        return Main.config.getProperty("block_password").equalsIgnoreCase(mainHandItemName) || Main.config.getProperty("block_password").equalsIgnoreCase(offHandItemName);
    }
}
