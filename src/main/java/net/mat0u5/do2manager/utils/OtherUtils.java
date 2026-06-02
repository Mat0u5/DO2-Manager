package net.mat0u5.do2manager.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.ChestType;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static net.mat0u5.do2manager.Main.server;

public class OtherUtils {

    public static double roundToNPlaces(double value, int n) {
        double pow = Math.pow(10,n);
        return (double)Math.round(value * pow) / pow;
    }
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
    public static String convertSecondsToLongReadableTime(long totalSeconds) {
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder readableTime = new StringBuilder();

        if (days > 0) {
            readableTime.append(days).append("d");
        }

        if (hours > 0) {
            readableTime.append(hours).append("h");
        }

        if (minutes > 0) {
            readableTime.append(minutes).append("m");
        }

        if (seconds > 0) {
            readableTime.append(seconds).append("s");
        }

        return readableTime.toString();
    }
    public static String convertTicksToClockTime(long ticks) {
        return convertTicksToClockTime(ticks, false);
    }
    public static String convertTicksToClockTime(long ticks, boolean forceMilis) {

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

        if (totalMinutes == 0 || forceMilis) {
            String milis = String.valueOf(milliseconds);
            if (milliseconds < 100) {
                milis = "0"+milis;
            }
            while (milis.length() >= 3 && milis.endsWith("0")) {
                milis = milis.substring(0,milis.length()-1);
            }
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
        Commands manager = server.getCommands();
        CommandSourceStack commandSource = server.createCommandSourceStack().withSuppressedOutput();
        manager.performPrefixedCommand(commandSource,command);
    }
    public static void executeCommand(String command) {
        if (server == null) return;
        Commands manager = server.getCommands();
        CommandSourceStack commandSource = server.createCommandSourceStack().withSuppressedOutput();
        manager.performPrefixedCommand(commandSource,command);
    }
    public static void broadcastMessage(MinecraftServer server, Component message) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.displayClientMessage(message, false);
        }
    }
    public static void broadcastMessage(Component message) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.displayClientMessage(message, false);
        }
    }
    public static boolean isPlayerOnline(MinecraftServer server, String username) {
        PlayerList playerManager = server.getPlayerList();
        ServerPlayer player = playerManager.getPlayerByName(username);
        return player != null;
    }
    public static boolean isPlayerOnline(String username) {
        return isPlayerOnline(server,username);
    }
    public static String getPlayerNameFromUUID(String uuid) {
        if (Main.allPlayers.containsKey(uuid)) return Main.allPlayers.get(uuid);
        return "";
    }
    public static String getPlayerUUIDFromName(String name) {
        for (String uuid : Main.allPlayers.keySet()) {
            if (Main.allPlayers.get(uuid).equalsIgnoreCase(name)) return uuid;
        }
        return "";
    }
    public static void restartServer(MinecraftServer server) {
        System.out.println("A queued restart has triggered...");
        executeCommand(server,"stop");
    }
    public static boolean isServerEmptyOrOnlyTangoCam(MinecraftServer server) {
        int playerCount = server.getPlayerList().getPlayers().size();
        if (playerCount == 0) {
            return true;
        } else if (playerCount == 1) {
            ServerPlayer player = server.getPlayerList().getPlayers().get(0);
            return "TangoCam".equals(player.getGameProfile().getName());
        }
        return false;
    }
    public static void playGuiClickSound(Player player) {
        if (player != null && player.level() != null) {
            player.playNotifySound(SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS, 0.5F, 1.0F);
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
    public static boolean isHoldingAdminKey(Player player) {
        try {
            // Get the item stacks for main hand and offhand
            ItemStack mainHandItem = player.getMainHandItem();
            ItemStack offHandItem = player.getOffhandItem();

            // Get the item names
            String mainHandItemName = mainHandItem.isEmpty() ? "Empty" : mainHandItem.getHoverName().getString();
            String offHandItemName = offHandItem.isEmpty() ? "Empty" : offHandItem.getHoverName().getString();

            // Create and return the result text
            return Main.config.getProperty("block_password").equalsIgnoreCase(mainHandItemName) || Main.config.getProperty("block_password").equalsIgnoreCase(offHandItemName);

        }catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void unlockContainerForTick(ServerLevel world, MinecraftServer server, BaseContainerBlockEntity container, BlockPos pos) {
        HolderLookup.Provider registryLookup = Main.server.registryAccess();
        CompoundTag nbt = container.saveWithoutMetadata(registryLookup);
        String originalLock = nbt.getString("Lock");
        nbt.remove("Lock");
        container.loadWithComponents(nbt, registryLookup);
        server.execute(() -> {
            try {
                // Re-lock the original container
                CompoundTag newNbt = container.saveWithoutMetadata(registryLookup);
                newNbt.putString("Lock", originalLock);
                container.loadWithComponents(newNbt, registryLookup);
            } catch (Exception e) {
                System.out.println("Failed to re-add lock at " + pos.toString());
            }
        });

        // Unlock the other half if it's a double chest
        if (container instanceof ChestBlockEntity) {
            ChestBlockEntity chest = (ChestBlockEntity) container;
            ChestBlockEntity otherHalf = getOtherHalf(world, chest, pos);

            if (otherHalf != null) {
                CompoundTag otherNbt = otherHalf.saveWithoutMetadata(registryLookup);
                String otherOriginalLock = otherNbt.getString("Lock");
                otherNbt.remove("Lock");
                otherHalf.loadWithComponents(otherNbt, registryLookup);

                server.execute(() -> {
                    try {
                        if (otherHalf != null) {
                            CompoundTag newOtherNbt = otherHalf.saveWithoutMetadata(registryLookup);
                            newOtherNbt.putString("Lock", otherOriginalLock);
                            otherHalf.loadWithComponents(newOtherNbt, registryLookup);
                        }
                    } catch (Exception e) {
                        System.out.println("Failed to re-add lock at " + pos.toString());
                    }
                });
            }
        }
    }

    private static ChestBlockEntity getOtherHalf(ServerLevel world, ChestBlockEntity chest, BlockPos pos) {
        BlockState state = chest.getBlockState();
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        ChestType type = state.getValue(BlockStateProperties.CHEST_TYPE);

        BlockPos otherHalfPos = null;

        if (type == ChestType.LEFT) {
            otherHalfPos = pos.relative(facing.getClockWise());
        } else if (type == ChestType.RIGHT) {
            otherHalfPos = pos.relative(facing.getCounterClockWise());
        }

        if (otherHalfPos != null) {
            BlockEntity adjacentBlockEntity = world.getBlockEntity(otherHalfPos);
            if (adjacentBlockEntity instanceof ChestBlockEntity) {
                ChestBlockEntity adjacentChest = (ChestBlockEntity) adjacentBlockEntity;
                if (adjacentChest.getBlockState().getBlock() == Blocks.CHEST) {
                    return adjacentChest;
                }
            }
        }
        return null;
    }
    public static String getLock(BaseContainerBlockEntity container) {
        HolderLookup.Provider registryLookup = server.registryAccess();
        CompoundTag nbt = container.saveWithoutMetadata(registryLookup);
        if (nbt == null) return null;
        if (!nbt.contains("Lock")) return null;
        String lockKey = nbt.getString("Lock");
        if (lockKey.isEmpty()) return null;
        return lockKey;
    }
    public static void removeItemsFromPlayerInventory(Player player, String match) {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            Component customName = stack.getHoverName();
            if (customName.getString().toLowerCase().equalsIgnoreCase(match.toLowerCase())) {
                player.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }
    }
}
