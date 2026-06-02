package net.mat0u5.do2manager.utils;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;


public class ItemStackCodec {
    public static String serializeItemStack(ItemStack itemStack) {
        if (itemStack == null) return "";
        if (itemStack.isEmpty()) return "";
        return ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, itemStack)
                .result()
                .map(JsonElement::toString)
                .orElse("");
    }
    public static ItemStack deserializeItemStack(String serialized) {
        if (serialized.isEmpty()) return ItemStack.EMPTY;
        return ItemStack.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(serialized))
                .result()
                .orElse(ItemStack.EMPTY);
    }
    public static String serializeListItemStack(List<ItemStack> itemList) {
        if (itemList == null) return "";
        if (itemList.isEmpty()) return "";
        return ItemStack.CODEC.listOf().encodeStart(JsonOps.INSTANCE, itemList)
                .result()
                .map(JsonElement::toString)
                .orElse("");
    }
    public static List<ItemStack> deserializeListItemStack(String serialized) {
        if (serialized.isEmpty()) return new ArrayList<>();
        return ItemStack.CODEC.listOf().parse(JsonOps.INSTANCE, JsonParser.parseString(serialized))
                .result()
                .orElse(new ArrayList<>());
    }
}
