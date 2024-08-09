package net.mat0u5.do2manager.world;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventoryScanner {
    public static List<ItemStack> getALLPlayerItems(PlayerEntity player) {
        List<ItemStack> result = new ArrayList<>();
        List<ItemStack> itemsInv = checkInventory(player.getInventory());
        List<ItemStack> itemsEnderChest = checkInventory(player.getEnderChestInventory());
        result.addAll(itemsInv);
        result.addAll(itemsEnderChest);
        return result;
    }
    private static List<ItemStack> checkInventory(Inventory inventory) {
        List<ItemStack> result = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack == null) continue;
            if (itemStack.isEmpty()) continue;
            if (ItemManager.isShulkerBox(itemStack)) {
                result.addAll(ItemManager.getShulkerItemContents(itemStack));
            }
            if (ItemManager.isBundle(itemStack)) {
                result.addAll(ItemManager.getBundleItemContents(itemStack));
            }
            result.add(itemStack);
        }
        return result;
    }
}
