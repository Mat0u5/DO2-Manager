package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.utils.DO2_GSON;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    public static void giveItemStack(PlayerEntity player, ItemStack itemStack) {
        if (!player.giveItemStack(itemStack)) {
            player.dropItem(itemStack, false);
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
    public static boolean hasNbtEntry(ItemStack itemStack, String nbtEntry) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt == null) return false;
        return nbt.contains(nbtEntry);
    }
    public static int getModelData(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        if (hasNbtEntry(itemStack, "CustomModelData")) return nbt.getInt("CustomModelData");
        return -1;
    }

    public static List<ItemStack> getHopperItems(ServerWorld world, BlockPos hopperPos) {
        BlockEntity blockEntity = world.getBlockEntity(hopperPos);

        if (blockEntity instanceof HopperBlockEntity) {
            HopperBlockEntity hopper = (HopperBlockEntity) blockEntity;
            return getHopperContents(hopper);

        } else {
            System.out.println("No hopper found at the specified position.");
        }
        return null;
    }
    private static List<ItemStack> getHopperContents(HopperBlockEntity hopper) {
        List<ItemStack> contents = new ArrayList<>();
        System.out.println("HopperSize: " + hopper.size());
        for (int i = 0; i < hopper.size(); i++) {
            ItemStack stack = hopper.getStack(i);
            if (!stack.isEmpty()) {
                contents.add(stack.copy());
            }
        }

        return contents;
    }

}
