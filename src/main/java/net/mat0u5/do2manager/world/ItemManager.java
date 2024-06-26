package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.utils.DO2_GSON;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

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
        for (int i = 0; i < hopper.size(); i++) {
            ItemStack stack = hopper.getStack(i);
            if (!stack.isEmpty()) {
                contents.add(stack.copy());
            }
        }

        return contents;
    }

    public static List<ItemStack> getPlayerInventory(PlayerEntity player) {
        List<ItemStack> list = new ArrayList<>();
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                list.add(itemStack.copy());
            }
        }
        return list;
    }
    public static boolean insertItemIntoBarrel(World world, BlockPos pos, ItemStack stack) {
        if (!(world.getBlockEntity(pos) instanceof BarrelBlockEntity)) {
            return false;
        }

        BarrelBlockEntity barrel = (BarrelBlockEntity) world.getBlockEntity(pos);
        if (barrel == null) {
            return false;
        }

        Inventory inventory = barrel;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slotStack = inventory.getStack(i);
            if (slotStack.isEmpty()) {
                inventory.setStack(i, stack.copy());
                stack.setCount(0);
                barrel.markDirty();
                return true;
            } else if (ItemStack.canCombine(slotStack, stack)) {
                int transferAmount = Math.min(inventory.getMaxCountPerStack() - slotStack.getCount(), stack.getCount());
                slotStack.increment(transferAmount);
                stack.decrement(transferAmount);
                if (stack.isEmpty()) {
                    barrel.markDirty();
                    return true;
                }
            }
        }

        barrel.markDirty();
        return false;
    }
    public static List<ItemStack> getDropperItems(ServerWorld world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        List<ItemStack> items = new ArrayList<>();

        if (blockEntity instanceof DispenserBlockEntity) {
            DispenserBlockEntity dropper = (DispenserBlockEntity) blockEntity;
            for (int i = 0; i < dropper.size(); i++) {
                ItemStack stack = dropper.getStack(i);
                if (!stack.isEmpty()) {
                    items.add(stack.copy());
                }
            }
        }

        return items;
    }
    public static void phaseToCasualPlayer(PlayerEntity player) {
        phaseToCasualInventory(player.getInventory());
        phaseToCasualInventory(player.getEnderChestInventory());
    }

    private static void phaseToCasualInventory(Inventory inventory) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                phaseToCasualItemStack(itemStack);
            }
        }
    }

    private static void phaseToCasualItemStack(ItemStack itemStack) {
        // Check if item has NBT data
        if (itemStack.hasNbt()) {
            NbtCompound nbt = itemStack.getNbt();
            if (nbt != null && nbt.contains("CustomRoleplayData")) {
                byte roleplayData = nbt.getByte("CustomRoleplayData");
                if (roleplayData == 2) {
                    nbt.putByte("CustomRoleplayData", (byte) 1);
                    itemStack.setNbt(nbt);
                    clearItemLore(itemStack);
                    System.out.println("Converted: " + itemStack.getName().getString());
                }
            }
            // Check if item is a Shulker Box and process its contents
            if (isShulkerBox(itemStack)) {
                phaseToCasualShulkerBox(itemStack);
            }
        }
    }

    private static void clearItemLore(ItemStack itemStack) {
        NbtCompound display = itemStack.getSubNbt("display");
        if (display != null) {
            display.remove("Lore");
            if (display.isEmpty()) {
                itemStack.removeSubNbt("display");
            } else {
                itemStack.setSubNbt("display", display);
            }
        }
    }

    public static boolean isShulkerBox(ItemStack itemStack) {
        return getItemId(itemStack).endsWith("shulker_box");
    }
    private static void phaseToCasualShulkerBox(ItemStack shulkerBox) {
        NbtCompound nbt = shulkerBox.getOrCreateSubNbt("BlockEntityTag");
        NbtList items = nbt.getList("Items", 10);
        boolean modified = false;

        for (int i = 0; i < items.size(); i++) {
            NbtCompound itemTag = items.getCompound(i);
            ItemStack itemStack = ItemStack.fromNbt(itemTag);
            phaseToCasualItemStack(itemStack);
            NbtCompound newItemTag = new NbtCompound();
            itemStack.writeNbt(newItemTag);
            newItemTag.putByte("Slot", itemTag.getByte("Slot"));
            items.set(i, newItemTag);

            if (newItemTag.contains("CustomRoleplayData", 2) && newItemTag.getByte("CustomRoleplayData") == 1) {
                modified = true;
            }
        }

        if (modified) {
            System.out.println("Converted Shulker Box: " + shulkerBox.getName().getString());
            nbt.put("Items", items);
            shulkerBox.setNbt(nbt);
        }
    }
}
