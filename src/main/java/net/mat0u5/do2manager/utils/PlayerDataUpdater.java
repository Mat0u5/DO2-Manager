package net.mat0u5.do2manager.utils;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class PlayerDataUpdater {
    private final MinecraftServer server;
    private final DataFixer dataFixer;
    private final int currentDataVersion = SharedConstants.getGameVersion().getSaveVersion().getId();

    public PlayerDataUpdater(MinecraftServer server) {
        this.server = server;
        this.dataFixer = server.getDataFixer();
    }

    public void updateAllPlayerData() {
        File playerDataFolder = server.getSavePath(WorldSavePath.PLAYERDATA).toFile();
        if (!playerDataFolder.exists() || !playerDataFolder.isDirectory()) {
            System.out.println("Player data folder not found.");
            return;
        }

        File[] playerDataFiles = playerDataFolder.listFiles((dir, name) -> name.endsWith(".dat"));
        if (playerDataFiles == null || playerDataFiles.length == 0) {
            System.out.println("No player data files found.");
            return;
        }

        for (File playerDataFile : playerDataFiles) {
            try {
                NbtCompound playerData = NbtIo.readCompressed(playerDataFile.toPath(), NbtSizeTracker.ofUnlimitedBytes());
                if (playerData == null) {
                    System.out.println("Failed to read player data for " + playerDataFile.getName());
                    continue;
                }

                int oldVersion = playerData.getInt("DataVersion");
                if (oldVersion < currentDataVersion) {
                    NbtCompound newPlayerData = updatePlayerData(playerData, oldVersion);
                    NbtIo.writeCompressed(newPlayerData, playerDataFile.toPath());
                    System.out.println("Updated player data for " + playerDataFile.getName() + " was ("+oldVersion+")");
                } else {
                    System.out.println("Player data already up-to-date for " + playerDataFile.getName());
                }
            } catch (IOException e) {
                System.err.println("Error updating player data (v.) for " + playerDataFile.getName() + ": " + e.getMessage());
            }
        }
    }
    public void validateAllPlayerData() {
        File playerDataFolder = server.getSavePath(WorldSavePath.PLAYERDATA).toFile();
        if (!playerDataFolder.exists() || !playerDataFolder.isDirectory()) {
            System.out.println("Player data folder not found.");
            return;
        }

        File[] playerDataFiles = playerDataFolder.listFiles((dir, name) -> name.endsWith(".dat"));
        if (playerDataFiles == null || playerDataFiles.length == 0) {
            System.out.println("No player data files found.");
            return;
        }

        for (File playerDataFile : playerDataFiles) {
            try {
                NbtCompound playerData = NbtIo.readCompressed(playerDataFile.toPath(), NbtSizeTracker.ofUnlimitedBytes());
                if (playerData == null) {
                    System.out.println("Failed to read player data for " + playerDataFile.getName());
                    continue;
                }

                NbtCompound newPlayerData = validatePlayerData(playerData);
                if (newPlayerData.toString().equalsIgnoreCase(playerData.toString())) {
                    System.out.println("Player data for " + playerDataFile.getName()+" does not need updating");
                }
                else {
                    NbtIo.writeCompressed(newPlayerData, playerDataFile.toPath());
                    System.out.println("Updated player data for " + playerDataFile.getName());
                }
            } catch (IOException e) {
                System.err.println("Error validating for " + playerDataFile.getName() + ": " + e.getMessage());
            }
        }
    }

    private NbtCompound updatePlayerData(NbtCompound playerData, int oldVersion) {
        Dynamic<?> dynamic = new Dynamic<>(NbtOps.INSTANCE, playerData);
        Dynamic<?> updatedDynamic = dataFixer.update(TypeReferences.PLAYER, dynamic, oldVersion, currentDataVersion);
        return (NbtCompound) updatedDynamic.getValue();
    }
    private NbtCompound validatePlayerData(NbtCompound playerData) {
        NbtCompound newPlayerData = playerData.copy();
        if (playerData.contains("Inventory")) {
            NbtList inventory = playerData.getList("Inventory", 10); // 10 is the ID for compounds in NBT
            NbtList newInv = new NbtList();

            for (int i = 0; i < inventory.size(); i++) {
                NbtCompound item = inventory.getCompound(i);
                ItemStack itemStack = ItemStack.fromNbtOrEmpty(server.getRegistryManager(),item);

                // Correct the item stack NBT data
                NbtCompound updatedNbt = (NbtCompound) itemStack.encode(server.getRegistryManager());
                updatedNbt.putByte("Slot",item.getByte("Slot"));
                if (!item.toString().equalsIgnoreCase(updatedNbt.toString())) {
                    System.out.println("UpdatedNBT_1_"+item.toString());
                    System.out.println("UpdatedNBT_2_"+updatedNbt.toString());
                }
                // Serialize back to Nbt and update inventory
                newInv.add(i, updatedNbt);
            }
            newPlayerData.put("Inventory",newInv);
        }
        return newPlayerData; // Return the modified NbtCompound with corrected data
    }

}
