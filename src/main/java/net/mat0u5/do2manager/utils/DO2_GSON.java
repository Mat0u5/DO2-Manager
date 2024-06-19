package net.mat0u5.do2manager.utils;

import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
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

    public static String serializeNbt(NbtCompound nbt) {
        if (nbt == null) return null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            NbtIo.writeCompressed(nbt, outputStream);
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static NbtCompound deserializeNbt(String nbtString) {
        if (nbtString == null) return null;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(nbtString))) {
            return NbtIo.readCompressed(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static SerializedItemStack serializeItemStackCustom(ItemStack itemStack) {
        NbtCompound nbtCompound = new NbtCompound();
        itemStack.writeNbt(nbtCompound);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            NbtIo.writeCompressed(nbtCompound, outputStream);
            String nbtBase64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            return new SerializedItemStack(
                    Registries.ITEM.getId(itemStack.getItem()).toString(),
                    itemStack.getCount(),
                    nbtBase64
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Deserialize SerializedItemStack to ItemStack
    private static ItemStack deserializeItemStack(SerializedItemStack serializedItemStack) {
        ItemStack itemStack = new ItemStack(
                Registries.ITEM.get(new Identifier(serializedItemStack.getItemName())),
                serializedItemStack.getQuantity()
        );

        if (serializedItemStack.getNbtData() != null) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(serializedItemStack.getNbtData()))) {
                NbtCompound nbtCompound = NbtIo.readCompressed(inputStream);
                itemStack.setNbt(nbtCompound);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return itemStack;
    }

    // Serialize ItemStack to JSON string
    public static String serializeItemStack(ItemStack itemStack) {
        NbtCompound nbtCompound = new NbtCompound();
        itemStack.writeNbt(nbtCompound);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            NbtIo.writeCompressed(nbtCompound, outputStream);
            String nbtData = serializeNbt(itemStack.getNbt());

            SerializedItemStack serializedItemStack = new SerializedItemStack(
                    Registries.ITEM.getId(itemStack.getItem()).toString(),
                    itemStack.getCount(),
                    nbtData
            );

            return GSON.toJson(serializedItemStack);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    // Deserialize JSON string to ItemStack
    public static ItemStack deserializeItemStack(String json) {
        SerializedItemStack serializedItemStack = GSON.fromJson(json, SerializedItemStack.class);

        ItemStack itemStack = new ItemStack(
                Registries.ITEM.get(new Identifier(serializedItemStack.getItemName())),
                serializedItemStack.getQuantity()
        );

        if (serializedItemStack.getNbtData() != null) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(serializedItemStack.getNbtData()))) {
                NbtCompound nbtCompound = NbtIo.readCompressed(inputStream);
                itemStack.setNbt(nbtCompound);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return itemStack;
    }

    // Serialize player's inventory to a JSON string
    public static String serializePlayerInventory(PlayerEntity player) {
        List<SerializedItemStack> serializedItemStacks = new ArrayList<>();

        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                serializedItemStacks.add(serializeItemStackCustom(itemStack));
            }
        }

        return GSON.toJson(serializedItemStacks);
    }

    // Deserialize JSON string to player's inventory
    public static List<ItemStack> deserializePlayerInventory(PlayerEntity player, String json) {
        List<ItemStack> inv = new ArrayList<ItemStack>();

        SerializedItemStack[] serializedItemStacks = GSON.fromJson(json, SerializedItemStack[].class);

        Inventory inventory = player.getInventory();
        inventory.clear();

        for (SerializedItemStack serializedItemStack : serializedItemStacks) {
            ItemStack itemStack = deserializeItemStack(serializedItemStack);
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
