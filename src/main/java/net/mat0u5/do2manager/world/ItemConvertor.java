package net.mat0u5.do2manager.world;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.serialization.Dynamic;
import net.mat0u5.do2manager.Main;
import net.minecraft.SharedConstants;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.level.storage.LevelStorageException;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

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
        ItemManager.clearItemPhaseLore(itemStack);
        System.out.println("Converted Phase Item: " + itemStack.getName().getString());
    }
    public static void tagExtendedItems(ItemStack itemStack) {
        if (itemStack == null) return;
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
