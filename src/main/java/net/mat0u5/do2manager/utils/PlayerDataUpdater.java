package net.mat0u5.do2manager.utils;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;

import java.io.File;
import java.io.IOException;

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

    private NbtCompound updatePlayerData(NbtCompound playerData, int oldVersion) {
        Dynamic<?> dynamic = new Dynamic<>(NbtOps.INSTANCE, playerData);
        Dynamic<?> updatedDynamic = dataFixer.update(TypeReferences.PLAYER, dynamic, oldVersion, currentDataVersion);
        return (NbtCompound) updatedDynamic.getValue();
    }
}
