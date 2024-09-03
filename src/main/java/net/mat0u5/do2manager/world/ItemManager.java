package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.utils.DO2_GSON;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    public static void giveItemStack(PlayerEntity player, ItemStack itemStack) {
        if (!player.giveItemStack(itemStack)) {
            player.dropItem(itemStack, false);
        }
    }
    public static void giveItemStack(PlayerEntity player, List<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            giveItemStack(player,itemStack);
        }
    }
    public static ItemStack getItemStackFromString(String itemName, int quantity) {
        return getItemStackFromString(itemName, quantity,null);
    }
    public static ItemStack getItemStackFromString(String itemName, int quantity, String nbtData) {
        Item item = Registries.ITEM.get( new Identifier(itemName));
        if (item == null) {
            throw new IllegalArgumentException("Invalid item ID: " + itemName);
        }
        ItemStack itemStack = new ItemStack(item, quantity);

        // Deserialize NBT data from JSON
        if (nbtData != null) {
            itemStack.setNbt(DO2_GSON.deserializeNbt(nbtData));
        }
        return itemStack;
    }

    public static String getItemId(ItemStack itemStack) {
        return Registries.ITEM.getId(itemStack.getItem()).toString();
    }
    public static boolean hasNbtEntry(ItemStack itemStack, String nbtEntry) {
        NbtCompound nbt = itemStack.getNbt();
        if (nbt == null) return false;
        return nbt.contains(nbtEntry);
    }
    public static int getModelData(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        if (hasNbtEntry(itemStack, "CustomModelData")) return nbt.getInt("CustomModelData");
        return -1;
    }
    public static int getMapId(ItemStack itemStack) {
        NbtCompound nbt = itemStack.getNbt();
        if (hasNbtEntry(itemStack, "map")) return nbt.getInt("map");
        return -1;
    }
    public static NbtCompound createItemEntry(Item item, int count) {
        ItemStack stack = new ItemStack(item, count);
        NbtCompound entry = new NbtCompound();
        stack.writeNbt(entry);
        entry.putByte("Count", (byte) count);  // Set the item count
        return entry;
    }
    public static NbtCompound createItemStackEntry(ItemStack stack) {
        NbtCompound entry = new NbtCompound();
        stack.writeNbt(entry);  // Write the ItemStack's NBT data to the entry
        entry.putByte("Count", (byte) stack.getCount());  // Set the item count
        return entry;
    }

    public static List<ItemStack> getHopperItems(ServerWorld world, BlockPos hopperPos) {
        BlockEntity blockEntity = world.getBlockEntity(hopperPos);

        if (blockEntity instanceof HopperBlockEntity) {
            HopperBlockEntity hopper = (HopperBlockEntity) blockEntity;
            return getHopperContents(hopper);

        } else {
            System.out.println("No hopper found at the specified position.");
        }
        return null;
    }
    private static List<ItemStack> getHopperContents(HopperBlockEntity hopper) {
        List<ItemStack> contents = new ArrayList<>();
        for (int i = 0; i < hopper.size(); i++) {
            ItemStack stack = hopper.getStack(i);
            if (!stack.isEmpty()) {
                contents.add(stack.copy());
            }
        }
        return contents;
    }
    public static List<ItemStack> getBarrelItems(ServerWorld world, BlockPos hopperPos) {
        BlockEntity blockEntity = world.getBlockEntity(hopperPos);

        if (blockEntity instanceof BarrelBlockEntity) {
            BarrelBlockEntity barrel = (BarrelBlockEntity) blockEntity;
            return getBarrelContents(barrel);

        } else {
            System.out.println("No barrel found at the specified position.");
        }
        return null;
    }
    private static List<ItemStack> getBarrelContents(BarrelBlockEntity barrel) {
        List<ItemStack> contents = new ArrayList<>();
        for (int i = 0; i < barrel.size(); i++) {
            ItemStack stack = barrel.getStack(i);
            if (!stack.isEmpty()) {
                contents.add(stack.copy());
            }
        }
        return contents;
    }
    public static List<ItemStack> getContentsOfEntitiesAtPosition(World world, BlockPos pos, int range) {
        List<ItemStack> allContents = new ArrayList<>();

        List<Entity> entities = world.getEntitiesByClass(Entity.class, new Box(pos.add(-range, -range, -range), pos.add(range, range, range)), entity -> entity instanceof HopperMinecartEntity || entity instanceof ChestBoatEntity);

        for (Entity entity : entities) {
            if (entity instanceof HopperMinecartEntity) {
                HopperMinecartEntity hopperMinecart = (HopperMinecartEntity) entity;
                for (int i = 0; i < hopperMinecart.size(); i++) {
                    ItemStack stack = hopperMinecart.getStack(i);
                    if (!stack.isEmpty()) {
                        allContents.add(stack.copy());
                    }
                }
            } else if (entity instanceof ChestBoatEntity) {
                ChestBoatEntity chestBoat = (ChestBoatEntity) entity;
                for (int i = 0; i < chestBoat.size(); i++) {
                    ItemStack stack = chestBoat.getStack(i);
                    if (!stack.isEmpty()) {
                        allContents.add(stack.copy());
                    }
                }
            }
        }

        return allContents;
    }

    public static List<ItemStack> getPlayerInventory(PlayerEntity player) {
        List<ItemStack> list = new ArrayList<>();
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack itemStack = inventory.getStack(i);
            if (!itemStack.isEmpty()) {
                list.add(itemStack.copy());
            }
        }
        return list;
    }
    public static boolean insertItemIntoBarrel(World world, BlockPos pos, ItemStack stack) {
        if (!(world.getBlockEntity(pos) instanceof BarrelBlockEntity)) {
            return false;
        }

        BarrelBlockEntity barrel = (BarrelBlockEntity) world.getBlockEntity(pos);
        if (barrel == null) {
            return false;
        }

        Inventory inventory = barrel;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack slotStack = inventory.getStack(i);
            if (slotStack.isEmpty()) {
                inventory.setStack(i, stack.copy());
                stack.setCount(0);
                barrel.markDirty();
                return true;
            } else if (ItemStack.canCombine(slotStack, stack)) {
                int transferAmount = Math.min(inventory.getMaxCountPerStack() - slotStack.getCount(), stack.getCount());
                slotStack.increment(transferAmount);
                stack.decrement(transferAmount);
                if (stack.isEmpty()) {
                    barrel.markDirty();
                    return true;
                }
            }
        }

        barrel.markDirty();
        return false;
    }
    public static List<ItemStack> getDropperItems(ServerWorld world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        List<ItemStack> items = new ArrayList<>();

        if (blockEntity instanceof DispenserBlockEntity) {
            DispenserBlockEntity dropper = (DispenserBlockEntity) blockEntity;
            for (int i = 0; i < dropper.size(); i++) {
                ItemStack stack = dropper.getStack(i);
                if (!stack.isEmpty()) {
                    items.add(stack.copy());
                }
            }
        }

        return items;
    }

    public static void clearItemPhaseLore(ItemStack itemStack) {
        NbtCompound display = itemStack.getSubNbt("display");
        if (display != null && display.contains("Lore", NbtElement.LIST_TYPE)) {
            NbtList loreList = display.getList("Lore", NbtElement.STRING_TYPE);
            int lastIndex = loreList.size() - 1;

            if (lastIndex >= 0) {
                String lastLoreLine = loreList.getString(lastIndex);
                if (lastLoreLine.contains("-= Phase")) {
                    loreList.remove(lastIndex);
                }
            }

            if (loreList.isEmpty()) {
                display.remove("Lore");
            } else {
                display.put("Lore", loreList);
            }

            if (display.isEmpty()) {
                itemStack.removeSubNbt("display");
            } else {
                itemStack.setSubNbt("display", display);
            }
        }
    }
    public static void clearItemLore(ItemStack itemStack) {
        NbtCompound display = itemStack.getSubNbt("display");
        if (display != null) {
            display.remove("Lore");
            if (display.isEmpty()) {
                itemStack.removeSubNbt("display");
            } else {
                itemStack.setSubNbt("display", display);
            }
        }
    }
    public static void addLoreToItemStack(ItemStack itemStack, List<Text> lore) {
        NbtCompound displayTag = itemStack.getOrCreateSubNbt("display");
        NbtList loreList = displayTag.getList("Lore", 8); // 8 means it's a string tag type

        // Convert each lore Text to string and add to NBT list
        for (Text loreLine : lore) {
            loreList.add(NbtString.of(Text.Serializer.toJson(loreLine)));
        }

        // Update the display tag with the new lore list
        displayTag.put("Lore", loreList);
    }
    public static boolean isShulkerBox(ItemStack itemStack) {
        return getItemId(itemStack).endsWith("shulker_box");
    }
    public static boolean isBundle(ItemStack itemStack) {
        return (itemStack.getItem() instanceof BundleItem);
    }

    public static List<ItemStack> getShulkerItemContents(ItemStack shulkerBox) {
        List<ItemStack> result = new ArrayList<>();

        NbtCompound nbt = shulkerBox.getOrCreateSubNbt("BlockEntityTag");
        NbtList items = nbt.getList("Items", 10);
        for (int i = 0; i < items.size(); i++) {
            NbtCompound itemTag = items.getCompound(i);
            ItemStack itemStack = ItemStack.fromNbt(itemTag);
            result.add(itemStack);
        }
        return result;
    }
    public static List<ItemStack> getBundleItemContents(ItemStack bundle) {
        List<ItemStack> items = new ArrayList<>();
        if (!isBundle(bundle)) return items;

        NbtCompound nbtCompound = bundle.getNbt();
        if (nbtCompound != null && nbtCompound.contains("Items", 9)) { // 9 is the NBT type ID for a list
            NbtList itemList = nbtCompound.getList("Items", 10); // 10 is the NBT type ID for a compound tag

            for (int i = 0; i < itemList.size(); i++) {
                NbtCompound itemCompound = itemList.getCompound(i);
                ItemStack itemStack = ItemStack.fromNbt(itemCompound);
                items.add(itemStack);
            }
        }
        return items;
    }
    public static int getHopperItemsCount(ServerWorld world, BlockPos pos) {
        List<ItemStack> items = getHopperItems(world,pos);
        return countItems(items);
    }
    public static int getDropperItemsCount(ServerWorld world, BlockPos pos) {
        List<ItemStack> items = getDropperItems(world,pos);
        return countItems(items);
    }
    public static int countItems(List<ItemStack> items) {
        if (items == null) return 0;
        if (items.isEmpty()) return 0;
        int count = 0;
        for (ItemStack item : items) {
            if (item == null) continue;
            if (item.isEmpty()) continue;
            count+= item.getCount();
        }
        return count;
    }
}
