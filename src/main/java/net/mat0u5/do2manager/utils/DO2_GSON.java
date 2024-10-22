package net.mat0u5.do2manager.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.DataFixerUpper;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.mat0u5.do2manager.world.ItemConvertor;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public class DO2_GSON {

    private static final Gson GSON = new Gson();
    private static final List<String> oldNbtVersions = List.of(
            "v.1.0.0",
            "v.1.0.1",
            "v.1.0.2",
            "v.1.0.3",
            "v.1.0.4",
            "v.1.0.5",
            "v.1.0.6",
            "v.1.0.7");

    public static NbtCompound deserializeNbt(String nbtString) {
        if (nbtString == null) return null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(nbtString))) {
            return NbtIo.readCompressed(inputStream,NbtSizeTracker.ofUnlimitedBytes());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static SerializedItemStack serializeItemStackCustom(ItemStack itemStack) {
        if (itemStack == null) return null;

        String nbtData = GSON.toJson(itemStack.getComponents(), ComponentMap.class);

        return new SerializedItemStack(
                ItemManager.getItemId(itemStack),
                itemStack.getCount(),
                nbtData
        );
    }
    // Deserialize SerializedItemStack to ItemStack
    private static ItemStack deserializeItemStack(SerializedItemStack serializedItemStack, String dbVersion) {
        if (serializedItemStack == null) return null;
        ItemStack itemStack = new ItemStack(
                Registries.ITEM.get(Identifier.of(serializedItemStack.getItemName())),
                serializedItemStack.getQuantity()
        );
        if (serializedItemStack.getNbtData() != null) {
            if (!dbVersion.equalsIgnoreCase("") && oldNbtVersions.contains(dbVersion)) {
                //Old NBT ItemStack
                NbtCompound nbtCompound = deserializeNbt(serializedItemStack.getNbtData());
                NbtCompound nbt = new NbtCompound();
                nbt.putString("id",serializedItemStack.getItemName());
                nbt.putByte("Count", (byte) serializedItemStack.getQuantity());
                nbt.put("tag", nbtCompound);
                itemStack = ItemConvertor.convertOldNbtToItemStack(nbt, 3465);
            }
            else {
                //Component ItemStack
                ComponentMap componentMap = GSON.fromJson(serializedItemStack.getNbtData(), ComponentMap.class);
                itemStack.applyComponentsFrom(componentMap);
            }
        }

        return itemStack;
    }
    // Deserialize JSON string to ItemStack
    public static ItemStack deserializeItemStack(String json, String dbVersion) {
        SerializedItemStack serializedItemStack = GSON.fromJson(json, SerializedItemStack.class);
        return deserializeItemStack(serializedItemStack,dbVersion);
    }
    // Serialize ItemStack to JSON string
    public static String serializeItemStack(ItemStack itemStack) {
        if (itemStack == null) return "";
        return GSON.toJson(serializeItemStackCustom(itemStack));
    }

    // Serialize List<ItemStack> to a JSON string
    public static String serializeListItemStack(List<ItemStack> list) {
        if (list == null || list.isEmpty()) return "[]";
        List<SerializedItemStack> serializedItemStacks = new ArrayList<>();

        for (ItemStack itemStack : list) {
            if (!itemStack.isEmpty()) {
                serializedItemStacks.add(serializeItemStackCustom(itemStack));
            }
        }

        return GSON.toJson(serializedItemStacks);
    }


    // Deserialize JSON string to List<ItemStack>
    public static List<ItemStack> deserializeListItemStack(String json, String dbVersion) {
        List<ItemStack> inv = new ArrayList<>();

        SerializedItemStack[] serializedItemStacks = GSON.fromJson(json, SerializedItemStack[].class);

        for (SerializedItemStack serializedItemStack : serializedItemStacks) {
            ItemStack itemStack = deserializeItemStack(serializedItemStack, dbVersion);
            inv.add(itemStack);
        }
        return inv;
    }




    // Inner class to represent the serialized form of an ItemStack
    private static class SerializedItemStack {
        private final String itemName;
        private final int quantity;
        private final String nbtData;

        public SerializedItemStack(String itemName, int quantity, String nbtData) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.nbtData = nbtData;
        }

        public String getItemName() {
            return itemName;
        }

        public int getQuantity() {
            return quantity;
        }

        public String getNbtData() {
            return nbtData;
        }
    }
}
