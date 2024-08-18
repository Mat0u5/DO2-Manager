package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.Main;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.List;

public class ItemConvertor extends PlayerInventoryScanner {
    public static List<Integer> expandedModelDatas = List.of(136,137,138,139,140);//plus <142;197>
    public static final int INV_UPDATE = 3;

    public static void onPlayerJoin(ServerPlayerEntity player) {
        while (getPlayerUpdateNum(player) < INV_UPDATE) {
            int num = getPlayerUpdateNum(player);
            if (num == 0) convertPhaseItems(player,1);
            if (num == 1) convertCustomItems(player,2);
            if (num == 2) convertPhaseItems(player,3);
        }
    }
    public static void convertPhaseItems(ServerPlayerEntity player, int updateToNum) {
        String playerUUID = player.getUuidAsString();
        System.out.println("Converting "+player.getEntityName()+"'s Items from phase to casual");

        List<ItemStack> items = PlayerInventoryScanner.getALLPlayerItems(player);
        for (ItemStack item : items) {
            convertPhaseItem(item);
        }

        if (updateToNum != -1) Main.lastInvUpdate.setProperty(playerUUID, String.valueOf(updateToNum));
        System.out.println("Conversion complete.");
    }
    public static void convertCustomItems(ServerPlayerEntity player, int updateToNum) {
        String playerUUID = player.getUuidAsString();
        System.out.println("Tagging "+player.getEntityName()+"'s Custom Cards");

        List<ItemStack> items = PlayerInventoryScanner.getALLPlayerItems(player);
        for (ItemStack item : items) {
            tagExtendedItems(item);
        }

        if (updateToNum != -1) Main.lastInvUpdate.setProperty(playerUUID, String.valueOf(updateToNum));
        System.out.println("Tagging complete.");
    }
    public static int getPlayerUpdateNum(ServerPlayerEntity player) {
        String playerUUID = player.getUuidAsString();
        if (Main.lastInvUpdate.getProperty(playerUUID) == null) return 0;
        return Integer.parseInt(Main.lastInvUpdate.getProperty(playerUUID));
    }


    public static void convertPhaseItem(ItemStack itemStack) {
        if (!itemStack.hasNbt()) return;
        NbtCompound nbt = itemStack.getNbt();
        if (nbt == null) return;
        if (!nbt.contains("CustomRoleplayData")) return;
        byte roleplayData = nbt.getByte("CustomRoleplayData");
        if (roleplayData != 2) return;
        nbt.putByte("CustomRoleplayData", (byte) 1);
        itemStack.setNbt(nbt);
        ItemManager.clearItemPhaseLore(itemStack);
        System.out.println("Converted Phase Item: " + itemStack.getName().getString());
    }
    public static void tagExtendedItems(ItemStack itemStack) {
        if (!itemStack.hasNbt()) return;
        NbtCompound nbt = itemStack.getNbt();
        if (nbt == null) return;
        if (!nbt.contains("CustomModelData")) return;
        int modelData = nbt.getInt("CustomModelData");
        if (!expandedModelDatas.contains(modelData) && !(modelData >= 142 && modelData <= 197)) return;
        if (nbt.contains("ExpandedCard")) return;
        nbt.putByte("ExpandedCard", (byte) 1);
        itemStack.setNbt(nbt);
        System.out.println("Tagged Expanded: " + itemStack.getName().getString());
    }
}
