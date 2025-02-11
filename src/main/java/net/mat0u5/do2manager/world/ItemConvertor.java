package net.mat0u5.do2manager.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.mat0u5.do2manager.Main;
import net.minecraft.SharedConstants;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class ItemConvertor extends PlayerInventoryScanner {
    public static List<Integer> expandedModelDatas = List.of(136,137,138,139,140);//plus <142;197>
    public static final int INV_UPDATE = 4;

    public static void onPlayerJoin(ServerPlayerEntity player) {
        while (getPlayerUpdateNum(player) < INV_UPDATE) {
            int num = getPlayerUpdateNum(player);
            if (num == 0) convertPhaseItems(player,1);
            if (num == 1) convertCustomItems(player,2);
            if (num == 2) convertPhaseItems(player,3);
            if (num == 3) convertPhaseItems(player,4);
        }
    }
    public static void convertPhaseItems(ServerPlayerEntity player, int updateToNum) {
        String playerUUID = player.getUuidAsString();
        System.out.println("Converting "+player.getNameForScoreboard()+"'s Items from phase to casual");

        List<ItemStack> items = PlayerInventoryScanner.getALLPlayerItems(player);
        for (ItemStack item : items) {
            convertPhaseItem(item);
        }

        if (updateToNum != -1) Main.lastInvUpdate.setProperty(playerUUID, String.valueOf(updateToNum));
        System.out.println("Conversion complete.");
    }
    public static void deleteHardcoreItems(ServerPlayerEntity player, int updateToNum) {
        String playerUUID = player.getUuidAsString();
        System.out.println("Deleting "+player.getNameForScoreboard()+"'s Hardcore Items");

        List<ItemStack> items = PlayerInventoryScanner.getALLPlayerItems(player);
        for (ItemStack item : items) {
            deleteHardoreItem(item);
        }

        if (updateToNum != -1) Main.lastInvUpdate.setProperty(playerUUID, String.valueOf(updateToNum));
        System.out.println("Deletion complete.");
        player.getInventory().markDirty();
        player.getEnderChestInventory().markDirty();
    }
    public static void deleteCRDItems(ServerPlayerEntity player, int crd) {
        String playerUUID = player.getUuidAsString();
        System.out.println("Deleting "+player.getNameForScoreboard()+"'s Items with CRD:"+crd);

        List<ItemStack> items = PlayerInventoryScanner.getALLPlayerItems(player);
        for (ItemStack item : items) {
            deleteCRDItem(item,crd);
        }

        System.out.println("Deletion complete.");
        player.getInventory().markDirty();
        player.getEnderChestInventory().markDirty();
    }
    public static void convertCustomItems(ServerPlayerEntity player, int updateToNum) {
        String playerUUID = player.getUuidAsString();
        System.out.println("Tagging "+player.getNameForScoreboard()+"'s Custom Cards");

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
        if (itemStack == null) return;

        if (!ItemManager.hasCustomComponentEntry(itemStack, "CustomRoleplayData")) return;

        byte roleplayData = ItemManager.getCustomComponentByte(itemStack, "CustomRoleplayData");
        if (roleplayData != 2) return;
        ItemManager.setCustomComponentByte(itemStack,"CustomRoleplayData", (byte) 1);
        ItemManager.clearItemPhaseOrHardcoreLore(itemStack);
        System.out.println("Converted Phase Item: " + itemStack.getName().getString());
    }
    public static void deleteHardoreItem(ItemStack itemStack) {
        if (itemStack == null) return;

        if (!ItemManager.hasCustomComponentEntry(itemStack, "CustomRoleplayData")) return;

        byte roleplayData = ItemManager.getCustomComponentByte(itemStack, "CustomRoleplayData");
        if (roleplayData != 3) return;
        ItemManager.removeAllComponents(itemStack);
        ItemManager.setModelData(itemStack, 1111);
        itemStack.set(DataComponentTypes.CUSTOM_NAME, Text.of("§7The remains of a Hardcore item..."));
    }
    public static void deleteCRDItem(ItemStack itemStack, int crd) {
        if (itemStack == null) return;

        if (!ItemManager.hasCustomComponentEntry(itemStack, "CustomRoleplayData")) return;

        byte roleplayData = ItemManager.getCustomComponentByte(itemStack, "CustomRoleplayData");
        if (roleplayData != crd) return;
        ItemManager.removeAllComponents(itemStack);
        ItemManager.setModelData(itemStack, 1111);
        itemStack.set(DataComponentTypes.CUSTOM_NAME, Text.of("§7The remains of an item..."));
    }
    public static void tagExtendedItems(ItemStack itemStack) {
        if (itemStack == null) return;
        if (!itemStack.getItem().equals(Items.IRON_NUGGET)) return;
        int modelData = ItemManager.getModelData(itemStack);
        if (modelData == -1) return;
        if (!expandedModelDatas.contains(modelData) && !(modelData >= 142 && modelData <= 197)) return;
        if (ItemManager.hasCustomComponentEntry(itemStack,"ExpandedCard")) return;
        ItemManager.setCustomComponentByte(itemStack,"ExpandedCard", (byte) 1);
        System.out.println("Tagged Expanded: " + itemStack.getName().getString());
    }
    public static ItemStack convertOldNbtToItemStack(NbtCompound oldNbt, int oldVersion) {
        DataFixer dataFixer = Main.server.getDataFixer();
        RegistryWrapper.WrapperLookup registries = Main.server.getRegistryManager();
        int currentDataVersion = SharedConstants.getGameVersion().getSaveVersion().getId();
        Dynamic<?> dynamic = new Dynamic<>(NbtOps.INSTANCE, oldNbt);
        Dynamic<?> updatedDynamic = dataFixer.update(TypeReferences.ITEM_STACK, dynamic, oldVersion, currentDataVersion);
        NbtCompound updatedNbt = (NbtCompound) updatedDynamic.getValue();
        ItemStack item = ItemStack.fromNbtOrEmpty(registries, updatedNbt);
        return item;
    }
}
