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
            if (ItemManager.isShulkerBox(itemStack)) {
                result.addAll(ItemManager.getShulkerItemContents(itemStack));
                if (!includeContainersThemselves) continue;
            }
            if (ItemManager.isBundle(itemStack)) {
                result.addAll(ItemManager.getBundleItemContents(itemStack));
                if (!includeContainersThemselves) continue;
            }
            result.add(itemStack);
        }
        return result;
    }
}
