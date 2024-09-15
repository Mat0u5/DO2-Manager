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

import java.util.*;

public class ItemManager {
    public static final List<Integer> artiModelDataList = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58);
    public static final HashMap<Integer, Integer> artifactWorth = new HashMap<Integer, Integer>() {{
        put(53, 66);
        put(48, 64);
        put(54, 62);
        put(37, 60);
        put(46, 57);
        put(36, 54);
        put(38, 52);
        put(14, 50);
        put(44, 48);
        put(11, 46);
        put(16, 40);
        put(58, 44);
        put(52, 44);
        put(39, 38);
        put(50, 37);
        put(10, 36);
        put(19, 34);
        put(49, 33);
        put(15, 32);
        put(31, 30);
        put(56, 29);
        put(47, 27);
        put(51, 26);
        put(57, 25);
        put(20, 24);
        put(41, 23);
        put(35, 22);
        put(18, 21);
        put(40, 20);
        put(12, 19);
        put(13, 18);
        put(32, 14);
        put(34, 13);
        put(29, 12);
        put(28, 11);
        put(30, 10);
        put(33, 9);
        put(17, 8);
        put(43, 7);
        put(42, 6);
        put(55, 5);
    }};
    public static final HashMap<Integer, String> artifactNames = new HashMap<Integer, String>() {{
        put(37, "key");
        put(36, "mug");
        put(38, "skadoodler");
        put(14, "slab");
        put(44, "staff");
        put(11, "rocket");
        put(16, "gem");
        put(39, "pickaxe");
        put(10, "watch");
        put(19, "golden_eye");
        put(15, "goggles");
        put(31, "stache");
        put(20, "bionic_eye");
        put(41, "helm");
        put(35, "wand");
        put(18, "bandana");
        put(40, "apron");
        put(12, "chisel");
        put(13, "goat");
        put(32, "pearl");
        put(34, "loop");
        put(29, "tome");
        put(28, "jar");
        put(30, "slippers");
        put(33, "shades");
        put(17, "waffle");
        put(43, "axe");
        put(42, "hood");

        put(55, "coin");
        put(57, "payday");
        put(51, "chip");
        put(47, "notes");
        put(56, "fist");
        put(49, "tie");
        put(50, "trigger");
        put(52, "spanner");
        put(58, "stopwatch");
        put(46, "orb");
        put(54, "laptop");
        put(48, "cloak");
        put(53, "mat");
    }};
    public static final LinkedHashMap<Integer, String> artifactNamesByValue = new LinkedHashMap<Integer, String>() {{
        put(66,"mat");
        put(64,"cloak");
        put(62,"laptop");
        put(60, "key");
        put(57,"orb");
        put(54, "mug");
        put(52, "skadoodler");
        put(50, "slab");
        put(48, "staff");
        put(46, "rocket");
        put(44,"stopwatch");
        //    put(44,"spanner");
        put(40, "gem");
        put(38, "pickaxe");
        put(37,"trigger");
        put(36, "watch");
        put(34, "golden_eye");
        put(33,"tie");
        put(32, "goggles");
        put(30, "stache");
        put(29,"fist");
        put(27,"notes");
        put(26,"chip");
        put(25,"payday");
        put(24, "bionic_eye");
        put(23, "helm");
        put(22, "wand");
        put(21, "bandana");
        put(20, "apron");
        put(19, "chisel");
        put(18, "goat");
        put(14, "pearl");
        put(13, "loop");
        put(12, "tome");
        put(11, "jar");
        put(10, "slippers");
        put(9, "shades");
        put(8, "waffle");
        put(7, "axe");
        put(6, "hood");
        put(5,"coin");
    }};

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
    public static void setModelData(ItemStack itemStack, int modelData) {
        setNbtInt(itemStack, "CustomModelData", modelData);
    }
    public static void setRoleplayData(ItemStack itemStack, byte roleplayData) {
        setNbtByte(itemStack, "CustomRoleplayData", roleplayData);
    }
    public static void setNbtInt(ItemStack itemStack, String subNbt, int setTo) {
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt(subNbt, setTo);
        itemStack.setNbt(nbt);
    }
    public static void setNbtByte(ItemStack itemStack, String subNbt, byte setTo) {
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putByte(subNbt, setTo);
        itemStack.setNbt(nbt);
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
    public static void addJsonLoreToItemStack(ItemStack itemStack, List<String> lore) {
        NbtCompound displayTag = itemStack.getOrCreateSubNbt("display");
        NbtList loreList = displayTag.getList("Lore", 8); // 8 means it's a string tag type

        // Convert each lore Text to string and add to NBT list
        for (String jsonLine : lore) {
            loreList.add(NbtString.of(jsonLine));
        }

        // Update the display tag with the new lore list
        displayTag.put("Lore", loreList);
    }
    public static List<Text> getLore(ItemStack itemStack) {
        List<Text> loreList = new ArrayList<>();

        if (itemStack.hasNbt() && itemStack.getNbt().contains("display")) {
            NbtList loreNbt = itemStack.getNbt().getCompound("display").getList("Lore", 8); // 8 is the NBT type for string
            for (int i = 0; i < loreNbt.size(); i++) {
                Text loreText = Text.Serializer.fromJson(loreNbt.getString(i));
                loreList.add(loreText);
            }
        }

        return loreList;
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

    public static List<ItemStack> combineItemStacks(List<ItemStack> inputStacks) {
        if (inputStacks == null) return new ArrayList<>();
        if (inputStacks.isEmpty()) return new ArrayList<>();

        List<ItemStack> combinedStacks = new ArrayList<>();
        for (ItemStack inputStack : List.copyOf(inputStacks)) {
            if (inputStack.isEmpty()) {
                continue;
            }

            boolean merged = false;

            // Try to merge with existing stacks in the combined list
            for (ItemStack combinedStack : combinedStacks) {
                if (ItemStack.canCombine(inputStack, combinedStack)) {
                    int combinedAmount = Math.min(combinedStack.getMaxCount() - combinedStack.getCount(), inputStack.getCount());
                    combinedStack.increment(combinedAmount);
                    inputStack.decrement(combinedAmount);

                    if (inputStack.isEmpty()) {
                        merged = true;
                        break;
                    }
                }
            }

            // If the inputStack couldn't be merged, add it to the combined list
            if (!merged) {
                combinedStacks.add(inputStack.copy());
            }
        }

        return combinedStacks;
    }
    public static ItemStack getHoldingItem(PlayerEntity player) {
        ItemStack mainHandItem = player.getMainHandStack();
        if (mainHandItem != null) {
            if (!mainHandItem.isEmpty()) return mainHandItem;
        }
        ItemStack offHandItem = player.getOffHandStack();
        return offHandItem;
    }

    public static boolean isDungeonCompass(ItemStack itemStack) {
        if (!getItemId(itemStack).equalsIgnoreCase("minecraft:compass")) return false;
        if (!hasNbtEntry(itemStack, "LodestoneTracked")) return false;
        return true;
    }
    public static boolean isDungeonArtifact(ItemStack itemStack) {
        if (!getItemId(itemStack).equalsIgnoreCase("minecraft:iron_nugget")) return false;
        return artiModelDataList.contains(getModelData(itemStack));
    }
    public static boolean isEmber(ItemStack itemStack) {
        if (!getItemId(itemStack).equalsIgnoreCase("minecraft:iron_nugget")) return false;
        return getModelData(itemStack) == 3;
    }
    public static boolean isCrown(ItemStack itemStack) {
        if (!getItemId(itemStack).equalsIgnoreCase("minecraft:iron_nugget")) return false;
        return getModelData(itemStack) == 2;
    }
    public static boolean isCoin(ItemStack itemStack) {
        if (!getItemId(itemStack).equalsIgnoreCase("minecraft:iron_nugget")) return false;
        return getModelData(itemStack) == 1;
    }
    public static boolean isDungeonCard(ItemStack itemStack) {
        if (!getItemId(itemStack).equalsIgnoreCase("minecraft:iron_nugget")) return false;
        int modelData = getModelData(itemStack);
        return modelData >= 101 && modelData <= 180;
    }
    public static int getArtifactWorth(ItemStack itemStack) {
        if (!isDungeonArtifact(itemStack)) return 0;
        if (artifactWorth.containsKey(getModelData(itemStack))) {
            return artifactWorth.get(getModelData(itemStack));
        }
        return 0;
    }
    public static String getArtifactName(ItemStack itemStack) {
        if (!isDungeonArtifact(itemStack)) return "";
        int modelData = getModelData(itemStack);
        if (modelData == -1) return "";
        if (artifactNames.containsKey(modelData)) {
            return artifactNames.get(modelData);
        }
        return "";
    }
}
