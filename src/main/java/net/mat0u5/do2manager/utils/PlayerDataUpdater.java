package net.mat0u5.do2manager.utils;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelResource;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class PlayerDataUpdater {
    private final MinecraftServer server;
    private final DataFixer dataFixer;
    private final int currentDataVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();

    public PlayerDataUpdater(MinecraftServer server) {
        this.server = server;
        this.dataFixer = server.getFixerUpper();
    }

    public void updateAllPlayerData() {
        File playerDataFolder = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
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
                CompoundTag playerData = NbtIo.readCompressed(playerDataFile.toPath(), NbtAccounter.unlimitedHeap());
                if (playerData == null) {
                    System.out.println("Failed to read player data for " + playerDataFile.getName());
                    continue;
                }

                int oldVersion = playerData.getInt("DataVersion");
                if (oldVersion < currentDataVersion) {
                    CompoundTag newPlayerData = updatePlayerData(playerData, oldVersion);
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
        File playerDataFolder = server.getWorldPath(LevelResource.PLAYER_DATA_DIR).toFile();
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
                CompoundTag playerData = NbtIo.readCompressed(playerDataFile.toPath(), NbtAccounter.unlimitedHeap());
                if (playerData == null) {
                    System.out.println("Failed to read player data for " + playerDataFile.getName());
                    continue;
                }

                CompoundTag newPlayerData = validatePlayerData(playerData);
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

    private CompoundTag updatePlayerData(CompoundTag playerData, int oldVersion) {
        Dynamic<?> dynamic = new Dynamic<>(NbtOps.INSTANCE, playerData);
        Dynamic<?> updatedDynamic = dataFixer.update(References.PLAYER, dynamic, oldVersion, currentDataVersion);
        return (CompoundTag) updatedDynamic.getValue();
    }
    private CompoundTag validatePlayerData(CompoundTag playerData) {
        CompoundTag newPlayerData = playerData.copy();
        if (playerData.contains("Inventory")) {
            ListTag inventory = playerData.getList("Inventory", 10); // 10 is the ID for compounds in NBT
            ListTag newInv = new ListTag();

            for (int i = 0; i < inventory.size(); i++) {
                CompoundTag item = inventory.getCompound(i);
                ItemStack itemStack = ItemStack.parseOptional(server.registryAccess(),item);

                // Correct the item stack NBT data
                CompoundTag updatedNbt = (CompoundTag) itemStack.save(server.registryAccess());
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
