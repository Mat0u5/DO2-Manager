package net.mat0u5.do2manager.world;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import com.google.gson.Gson;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class ItemManager {
    public static void giveItemStack(PlayerEntity player, ItemStack itemStack) {
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;


            // Attempt to add the ItemStack to the player's inventory
            if (!serverPlayer.giveItemStack(itemStack)) {
                // If the inventory is full, drop the ItemStack at the player's feet
                player.dropItem(itemStack, false);
            }
        }
    }
    public static ItemStack getItemStackFromString(String itemName, int quantity) {
        return getItemStackFromString(itemName, quantity,null);
    }
    public static ItemStack getItemStackFromString(String itemName, int quantity, String nbtData) {
        Item item = Registries.ITEM.get( new Identifier(itemName));
        if (item == null) {
            throw new IllegalArgumentException("Invalid item ID: " + itemName);
        }
        ItemStack itemStack = new ItemStack(item, quantity);

        // Deserialize NBT data from JSON
        if (nbtData != null) {
            itemStack.setNbt(deserializeNbt(nbtData));
        }
        return itemStack;
    }

    public static Identifier getItemId(ItemStack itemStack) {
        return Registries.ITEM.getId(itemStack.getItem());
    }
    public static String serializeNbt(NbtCompound nbt) {
        if (nbt == null) return null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            NbtIo.writeCompressed(nbt, outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static NbtCompound deserializeNbt(String nbtString) {
        if (nbtString == null) return null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(nbtString))) {
            return NbtIo.readCompressed(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
