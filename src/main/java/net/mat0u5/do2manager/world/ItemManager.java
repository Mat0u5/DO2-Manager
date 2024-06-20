package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.utils.DO2_GSON;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;

import java.util.List;

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
    public static void giveItemStack(PlayerEntity player, List<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            giveItemStack(player,itemStack);
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
            itemStack.setNbt(DO2_GSON.deserializeNbt(nbtData));
        }
        return itemStack;
    }

    public static String getItemId(ItemStack itemStack) {
        return Registries.ITEM.getId(itemStack.getItem()).toString();
    }



}
