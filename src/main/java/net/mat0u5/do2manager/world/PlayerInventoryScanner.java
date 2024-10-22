package net.mat0u5.do2manager.world;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventoryScanner {
    public static List<ItemStack> getALLPlayerItems(PlayerEntity player) {
        List<ItemStack> result = new ArrayList<>();
        List<ItemStack> itemsInv = getALLItemsFromInv(player.getInventory(),true);
        List<ItemStack> itemsEnderChest = getALLItemsFromInv(player.getEnderChestInventory(),true);
        result.addAll(itemsInv);
        result.addAll(itemsEnderChest);
        return result;
    }
    public static List<ItemStack> getALLItemsFromInv(Inventory inventory, boolean includeContainersThemselves) {
        List<ItemStack> result = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (itemStack == null) continue;
            if (itemStack.isEmpty()) continue;
            result.add(itemStack);
        }
        int iterations = 0;
        List<ItemStack> shulkersAndBundles = new ArrayList<>();
        while((containsShulkerBox(result) || containsBundle(result)) && iterations < 10) {
            iterations++;
            List<ItemStack> toRemove = new ArrayList<>();
            List<ItemStack> toAdd = new ArrayList<>();
            for (ItemStack itemStack : result) {
                if (itemStack == null) continue;
                if (itemStack.isEmpty()) continue;
                if (ItemManager.isShulkerBox(itemStack)) {
                    shulkersAndBundles.add(itemStack);
                    toAdd.addAll(ItemManager.getContainerItemContents(itemStack));
                    toRemove.add(itemStack);
                }
                if (ItemManager.isBundle(itemStack)) {
                    shulkersAndBundles.add(itemStack);
                    toAdd.addAll(ItemManager.getBundleItemContents(itemStack));
                    toRemove.add(itemStack);
                }
            }
            result.removeAll(toRemove);
            result.addAll(toAdd);
        }
        if (includeContainersThemselves) {
            result.addAll(shulkersAndBundles);
        }

        return result;
    }
    public static boolean containsShulkerBox(List<ItemStack> list) {
        for (ItemStack itemStack : list) {
            if (ItemManager.isShulkerBox(itemStack)) return true;
        }
        return false;
    }
    public static boolean containsBundle(List<ItemStack> list) {
        for (ItemStack itemStack : list) {
            if (ItemManager.isBundle(itemStack)) return true;
        }
        return false;
    }
}
