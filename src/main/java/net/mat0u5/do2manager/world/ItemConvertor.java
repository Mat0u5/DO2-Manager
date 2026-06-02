package net.mat0u5.do2manager.world;

import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.SharedConstants;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.List;

public class ItemConvertor extends PlayerInventoryScanner {
    public static List<Integer> expandedModelDatas = List.of(136,137,138,139,140);//plus <142;197>

    public static Integer getInvUpdate() {
        String invUpdate = Main.config.getProperty("inv_update");
        try {
            return Integer.parseInt(invUpdate);
        }catch(Exception e) {}
        return null;
    }

    public static void setInvUpdate(int i) {
        Main.config.setProperty("inv_update", String.valueOf(i));
    }

    public static void onPlayerJoin(ServerPlayer player) {
        Integer invUpdate = getInvUpdate();
        if (invUpdate == null) return;
        while (getPlayerUpdateNum(player) < invUpdate) {
            int num = getPlayerUpdateNum(player);
            if (num == 0) convertPhaseItems(player,1);
            if (num == 1) convertCustomItems(player,2);
            if (num >= 2) convertPhaseItems(player, invUpdate);
        }
    }
    public static void convertPhaseItems(ServerPlayer player, int updateToNum) {
        String playerUUID = player.getStringUUID();
        System.out.println("Converting "+player.getScoreboardName()+"'s Items from phase to casual");

        List<ItemStack> items = PlayerInventoryScanner.getALLPlayerItems(player);
        for (ItemStack item : items) {
            convertPhaseItem(item);
        }

        if (updateToNum != -1) Main.lastInvUpdate.setProperty(playerUUID, String.valueOf(updateToNum));
        System.out.println("Conversion complete.");
    }
    public static void deleteHardcoreItems(ServerPlayer player, int updateToNum) {
        String playerUUID = player.getStringUUID();
        System.out.println("Deleting "+player.getScoreboardName()+"'s Hardcore Items");

        List<ItemStack> items = PlayerInventoryScanner.getALLPlayerItems(player);
        for (ItemStack item : items) {
            deleteHardoreItem(item);
        }

        if (updateToNum != -1) Main.lastInvUpdate.setProperty(playerUUID, String.valueOf(updateToNum));
        System.out.println("Deletion complete.");
        player.getInventory().setChanged();
        player.getEnderChestInventory().setChanged();
    }
    public static void deleteCRDItems(ServerPlayer player, int crd) {
        String playerUUID = player.getStringUUID();
        System.out.println("Deleting "+player.getScoreboardName()+"'s Items with CRD:"+crd);

        List<ItemStack> items = PlayerInventoryScanner.getALLPlayerItems(player);
        for (ItemStack item : items) {
            deleteCRDItem(item,crd);
        }

        System.out.println("Deletion complete.");
        player.getInventory().setChanged();
        player.getEnderChestInventory().setChanged();
    }
    public static void convertCustomItems(ServerPlayer player, int updateToNum) {
        String playerUUID = player.getStringUUID();
        System.out.println("Tagging "+player.getScoreboardName()+"'s Custom Cards");

        List<ItemStack> items = PlayerInventoryScanner.getALLPlayerItems(player);
        for (ItemStack item : items) {
            tagExtendedItems(item);
        }

        if (updateToNum != -1) Main.lastInvUpdate.setProperty(playerUUID, String.valueOf(updateToNum));
        System.out.println("Tagging complete.");
    }
    public static int getPlayerUpdateNum(ServerPlayer player) {
        String playerUUID = player.getStringUUID();
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
        System.out.println("Converted Phase Item: " + itemStack.getHoverName().getString());
    }
    public static void deleteHardoreItem(ItemStack itemStack) {
        if (itemStack == null) return;

        if (!ItemManager.hasCustomComponentEntry(itemStack, "CustomRoleplayData")) return;

        byte roleplayData = ItemManager.getCustomComponentByte(itemStack, "CustomRoleplayData");
        if (roleplayData != 3) return;
        ItemManager.removeAllComponents(itemStack);
        ItemManager.setModelData(itemStack, 1111);
        itemStack.set(DataComponents.CUSTOM_NAME, Component.nullToEmpty("§7The remains of a Hardcore item..."));
    }
    public static void deleteCRDItem(ItemStack itemStack, int crd) {
        if (itemStack == null) return;

        if (!ItemManager.hasCustomComponentEntry(itemStack, "CustomRoleplayData")) return;

        byte roleplayData = ItemManager.getCustomComponentByte(itemStack, "CustomRoleplayData");
        if (roleplayData != crd) return;
        ItemManager.removeAllComponents(itemStack);
        ItemManager.setModelData(itemStack, 1111);
        itemStack.set(DataComponents.CUSTOM_NAME, Component.nullToEmpty("§7The remains of an item..."));
    }
    public static void tagExtendedItems(ItemStack itemStack) {
        if (itemStack == null) return;
        if (!itemStack.getItem().equals(Items.IRON_NUGGET)) return;
        int modelData = ItemManager.getModelData(itemStack);
        if (modelData == -1) return;
        if (!expandedModelDatas.contains(modelData) && !(modelData >= 142 && modelData <= 197)) return;
        if (ItemManager.hasCustomComponentEntry(itemStack,"ExpandedCard")) return;
        ItemManager.setCustomComponentByte(itemStack,"ExpandedCard", (byte) 1);
        System.out.println("Tagged Expanded: " + itemStack.getHoverName().getString());
    }
    public static ItemStack convertOldNbtToItemStack(CompoundTag oldNbt, int oldVersion) {
        DataFixer dataFixer = Main.server.getFixerUpper();
        HolderLookup.Provider registries = Main.server.registryAccess();
        int currentDataVersion = SharedConstants.getCurrentVersion().getDataVersion().getVersion();
        Dynamic<?> dynamic = new Dynamic<>(NbtOps.INSTANCE, oldNbt);
        Dynamic<?> updatedDynamic = dataFixer.update(References.ITEM_STACK, dynamic, oldVersion, currentDataVersion);
        CompoundTag updatedNbt = (CompoundTag) updatedDynamic.getValue();
        ItemStack item = ItemStack.parseOptional(registries, updatedNbt);
        return item;
    }
}
