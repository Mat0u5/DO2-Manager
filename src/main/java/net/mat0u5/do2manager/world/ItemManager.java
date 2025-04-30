package net.mat0u5.do2manager.world;

import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.block.entity.*;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.ChestBoatEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

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
    public static void removeAllComponents(ItemStack itemStack) {itemStack.set(DataComponentTypes.CUSTOM_DATA, itemStack.getDefaultComponents().get(DataComponentTypes.CUSTOM_DATA));
        itemStack.set(DataComponentTypes.MAX_STACK_SIZE, itemStack.getDefaultComponents().get(DataComponentTypes.MAX_STACK_SIZE));
        itemStack.set(DataComponentTypes.MAX_DAMAGE, itemStack.getDefaultComponents().get(DataComponentTypes.MAX_DAMAGE));
        itemStack.set(DataComponentTypes.DAMAGE, itemStack.getDefaultComponents().get(DataComponentTypes.DAMAGE));
        itemStack.set(DataComponentTypes.UNBREAKABLE, itemStack.getDefaultComponents().get(DataComponentTypes.UNBREAKABLE));
        itemStack.set(DataComponentTypes.CUSTOM_NAME, itemStack.getDefaultComponents().get(DataComponentTypes.CUSTOM_NAME));
        itemStack.set(DataComponentTypes.ITEM_NAME, itemStack.getDefaultComponents().get(DataComponentTypes.ITEM_NAME));
        itemStack.set(DataComponentTypes.LORE, itemStack.getDefaultComponents().get(DataComponentTypes.LORE));
        itemStack.set(DataComponentTypes.RARITY, itemStack.getDefaultComponents().get(DataComponentTypes.RARITY));
        itemStack.set(DataComponentTypes.ENCHANTMENTS, itemStack.getDefaultComponents().get(DataComponentTypes.ENCHANTMENTS));
        itemStack.set(DataComponentTypes.CAN_PLACE_ON, itemStack.getDefaultComponents().get(DataComponentTypes.CAN_PLACE_ON));
        itemStack.set(DataComponentTypes.CAN_BREAK, itemStack.getDefaultComponents().get(DataComponentTypes.CAN_BREAK));
        itemStack.set(DataComponentTypes.ATTRIBUTE_MODIFIERS, itemStack.getDefaultComponents().get(DataComponentTypes.ATTRIBUTE_MODIFIERS));
        itemStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, itemStack.getDefaultComponents().get(DataComponentTypes.CUSTOM_MODEL_DATA));
        itemStack.set(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP, itemStack.getDefaultComponents().get(DataComponentTypes.HIDE_ADDITIONAL_TOOLTIP));
        itemStack.set(DataComponentTypes.HIDE_TOOLTIP, itemStack.getDefaultComponents().get(DataComponentTypes.HIDE_TOOLTIP));
        itemStack.set(DataComponentTypes.REPAIR_COST, itemStack.getDefaultComponents().get(DataComponentTypes.REPAIR_COST));
        itemStack.set(DataComponentTypes.CREATIVE_SLOT_LOCK, itemStack.getDefaultComponents().get(DataComponentTypes.CREATIVE_SLOT_LOCK));
        itemStack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, itemStack.getDefaultComponents().get(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE));
        itemStack.set(DataComponentTypes.INTANGIBLE_PROJECTILE, itemStack.getDefaultComponents().get(DataComponentTypes.INTANGIBLE_PROJECTILE));
        itemStack.set(DataComponentTypes.FOOD, itemStack.getDefaultComponents().get(DataComponentTypes.FOOD));
        itemStack.set(DataComponentTypes.FIRE_RESISTANT, itemStack.getDefaultComponents().get(DataComponentTypes.FIRE_RESISTANT));
        itemStack.set(DataComponentTypes.TOOL, itemStack.getDefaultComponents().get(DataComponentTypes.TOOL));
        itemStack.set(DataComponentTypes.STORED_ENCHANTMENTS, itemStack.getDefaultComponents().get(DataComponentTypes.STORED_ENCHANTMENTS));
        itemStack.set(DataComponentTypes.DYED_COLOR, itemStack.getDefaultComponents().get(DataComponentTypes.DYED_COLOR));
        itemStack.set(DataComponentTypes.MAP_COLOR, itemStack.getDefaultComponents().get(DataComponentTypes.MAP_COLOR));
        itemStack.set(DataComponentTypes.MAP_ID, itemStack.getDefaultComponents().get(DataComponentTypes.MAP_ID));
        itemStack.set(DataComponentTypes.MAP_DECORATIONS, itemStack.getDefaultComponents().get(DataComponentTypes.MAP_DECORATIONS));
        itemStack.set(DataComponentTypes.MAP_POST_PROCESSING, itemStack.getDefaultComponents().get(DataComponentTypes.MAP_POST_PROCESSING));
        itemStack.set(DataComponentTypes.CHARGED_PROJECTILES, itemStack.getDefaultComponents().get(DataComponentTypes.CHARGED_PROJECTILES));
        itemStack.set(DataComponentTypes.BUNDLE_CONTENTS, itemStack.getDefaultComponents().get(DataComponentTypes.BUNDLE_CONTENTS));
        itemStack.set(DataComponentTypes.POTION_CONTENTS, itemStack.getDefaultComponents().get(DataComponentTypes.POTION_CONTENTS));
        itemStack.set(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, itemStack.getDefaultComponents().get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS));
        itemStack.set(DataComponentTypes.WRITABLE_BOOK_CONTENT, itemStack.getDefaultComponents().get(DataComponentTypes.WRITABLE_BOOK_CONTENT));
        itemStack.set(DataComponentTypes.WRITTEN_BOOK_CONTENT, itemStack.getDefaultComponents().get(DataComponentTypes.WRITTEN_BOOK_CONTENT));
        itemStack.set(DataComponentTypes.TRIM, itemStack.getDefaultComponents().get(DataComponentTypes.TRIM));
        itemStack.set(DataComponentTypes.DEBUG_STICK_STATE, itemStack.getDefaultComponents().get(DataComponentTypes.DEBUG_STICK_STATE));
        itemStack.set(DataComponentTypes.ENTITY_DATA, itemStack.getDefaultComponents().get(DataComponentTypes.ENTITY_DATA));
        itemStack.set(DataComponentTypes.BUCKET_ENTITY_DATA, itemStack.getDefaultComponents().get(DataComponentTypes.BUCKET_ENTITY_DATA));
        itemStack.set(DataComponentTypes.BLOCK_ENTITY_DATA, itemStack.getDefaultComponents().get(DataComponentTypes.BLOCK_ENTITY_DATA));
        itemStack.set(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, itemStack.getDefaultComponents().get(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER));
        itemStack.set(DataComponentTypes.JUKEBOX_PLAYABLE, itemStack.getDefaultComponents().get(DataComponentTypes.JUKEBOX_PLAYABLE));
        itemStack.set(DataComponentTypes.LODESTONE_TRACKER, itemStack.getDefaultComponents().get(DataComponentTypes.LODESTONE_TRACKER));
        itemStack.set(DataComponentTypes.FIREWORK_EXPLOSION, itemStack.getDefaultComponents().get(DataComponentTypes.FIREWORK_EXPLOSION));
        itemStack.set(DataComponentTypes.FIREWORKS, itemStack.getDefaultComponents().get(DataComponentTypes.FIREWORKS));
        itemStack.set(DataComponentTypes.PROFILE, itemStack.getDefaultComponents().get(DataComponentTypes.PROFILE));
        itemStack.set(DataComponentTypes.NOTE_BLOCK_SOUND, itemStack.getDefaultComponents().get(DataComponentTypes.NOTE_BLOCK_SOUND));
        itemStack.set(DataComponentTypes.BANNER_PATTERNS, itemStack.getDefaultComponents().get(DataComponentTypes.BANNER_PATTERNS));
        itemStack.set(DataComponentTypes.BASE_COLOR, itemStack.getDefaultComponents().get(DataComponentTypes.BASE_COLOR));
        itemStack.set(DataComponentTypes.POT_DECORATIONS, itemStack.getDefaultComponents().get(DataComponentTypes.POT_DECORATIONS));
        itemStack.set(DataComponentTypes.CONTAINER, itemStack.getDefaultComponents().get(DataComponentTypes.CONTAINER));
        itemStack.set(DataComponentTypes.BLOCK_STATE, itemStack.getDefaultComponents().get(DataComponentTypes.BLOCK_STATE));
        itemStack.set(DataComponentTypes.LOCK, itemStack.getDefaultComponents().get(DataComponentTypes.LOCK));
        itemStack.set(DataComponentTypes.CONTAINER_LOOT, itemStack.getDefaultComponents().get(DataComponentTypes.CONTAINER_LOOT));
    }

    public static String getItemId(ItemStack itemStack) {
        return Registries.ITEM.getId(itemStack.getItem()).toString();
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

        List<Entity> entities = world.getEntitiesByClass(
                Entity.class,
                new Box(pos.add(-range, -range, -range).toCenterPos(),
                pos.add(range, range, range).toCenterPos()),
                entity -> entity instanceof HopperMinecartEntity || entity instanceof ChestBoatEntity
        );

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
            } else if (ItemStack.areItemsAndComponentsEqual(slotStack, stack)) {
                int transferAmount = Math.min(stack.getMaxCount() - slotStack.getCount(), stack.getCount());
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

    public static void clearItemPhaseOrHardcoreLore(ItemStack itemStack) {
        List<Text> currentLore = getLore(itemStack);
        if (currentLore == null || currentLore.isEmpty()) return;
        List<Text> newLore = new ArrayList<>();
        for (Text loreLine : currentLore) {
            if (!loreLine.getString().contains("-= Phase") && !loreLine.getString().contains("-= Hardcore")) {
                newLore.add(loreLine);
            }
        }
        LoreComponent lore = new LoreComponent(newLore);
        itemStack.set(DataComponentTypes.LORE,lore);
    }
    public static void clearItemLore(ItemStack itemStack) {
        itemStack.remove(DataComponentTypes.LORE);
    }
    public static void addLoreToItemStack(ItemStack itemStack, List<Text> lines) {
        List<Text> loreLines = getLore(itemStack);
        if (lines != null && !lines.isEmpty()) loreLines.addAll(lines);
        LoreComponent lore = new LoreComponent(loreLines);
        itemStack.set(DataComponentTypes.LORE, lore);
    }
    public static List<Text> getLore(ItemStack itemStack) {
        LoreComponent lore = itemStack.get(DataComponentTypes.LORE);
        List<Text> lines = lore.lines();
        if (lines == null) return new ArrayList<>();
        if (lines.isEmpty()) return new ArrayList<>();
        return lines;
    }
    public static boolean isShulkerBox(ItemStack itemStack) {
        return getItemId(itemStack).endsWith("shulker_box");
    }
    public static boolean isBundle(ItemStack itemStack) {
        return (itemStack.getItem() instanceof BundleItem);
    }

    public static List<ItemStack> getContainerItemContents(ItemStack container) {
        ContainerComponent contents = container.get(DataComponentTypes.CONTAINER);
        if (contents == null) return new ArrayList<>();
        List<ItemStack> list = new ArrayList<>();
        contents.iterateNonEmpty().forEach(list::add);
        return list;
    }
    public static List<ItemStack> getBundleItemContents(ItemStack bundle) {
        BundleContentsComponent contents = bundle.get(DataComponentTypes.BUNDLE_CONTENTS);
        if (contents == null) return new ArrayList<>();
        List<ItemStack> list = new ArrayList<>();
        contents.iterate().forEach(list::add);
        return list;
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
                if (ItemStack.areItemsAndComponentsEqual(inputStack, combinedStack)) {
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


    public static void setCustomComponentInt(ItemStack itemStack, String componentKey, int value) {
        if (itemStack == null) return;
        NbtComponent currentNbt = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbtComp = currentNbt == null ? new NbtCompound() : currentNbt.copyNbt();
        nbtComp.putInt(componentKey,value);
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtComp));
    }
    public static void setCustomComponentByte(ItemStack itemStack, String componentKey, byte value) {
        if (itemStack == null) return;
        NbtComponent currentNbt = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbtComp = currentNbt == null ? new NbtCompound() : currentNbt.copyNbt();
        nbtComp.putByte(componentKey,value);
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtComp));
    }
    public static int setCustomComponentString(ItemStack itemStack, String componentKey, String value) {
        if (itemStack == null) return 0;
        NbtComponent currentNbt = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        NbtCompound nbtComp = currentNbt == null ? new NbtCompound() : currentNbt.copyNbt();
        nbtComp.putString(componentKey,value);
        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbtComp));
        return 1;
    }
    public static String getCustomComponentString(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        NbtComponent nbtComponent = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        NbtCompound nbtComp = nbtComponent.copyNbt();
        if (!nbtComp.contains(componentKey)) return null;
        return nbtComp.getString(componentKey);
    }
    public static Integer getCustomComponentInt(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        NbtComponent nbtComponent = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        NbtCompound nbtComp = nbtComponent.copyNbt();
        if (!nbtComp.contains(componentKey)) return null;
        return nbtComp.getInt(componentKey);
    }
    public static Byte getCustomComponentByte(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        NbtComponent nbtComponent = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        NbtCompound nbtComp = nbtComponent.copyNbt();
        if (!nbtComp.contains(componentKey)) return null;
        return nbtComp.getByte(componentKey);
    }
    public static boolean hasCustomComponentEntry(ItemStack itemStack, String componentEntry) {
        NbtComponent nbt = itemStack.getComponents().get(DataComponentTypes.CUSTOM_DATA);
        if (nbt == null) return false;
        return nbt.contains(componentEntry);
    }
    public static void setModelData(ItemStack itemStack, int modelData) {
        itemStack.set(DataComponentTypes.CUSTOM_MODEL_DATA, new CustomModelDataComponent(modelData));
    }
    public static int getModelData(ItemStack itemStack) {
        CustomModelDataComponent cmdComp = itemStack.get(DataComponentTypes.CUSTOM_MODEL_DATA);
        if (cmdComp == null) return -1;
        return cmdComp.value();
    }
    public static int getMapId(ItemStack itemStack) {
        MapIdComponent mapIdComp = itemStack.get(DataComponentTypes.MAP_ID);
        if (mapIdComp == null) return -1;
        return mapIdComp.id();
    }
    public static void setRoleplayData(ItemStack itemStack, byte roleplayData) {
        setCustomComponentByte(itemStack,"CustomRoleplayData",roleplayData);
    }

    public static boolean isDungeonCompass(ItemStack itemStack) {
        if (!getItemId(itemStack).equalsIgnoreCase("minecraft:compass")) return false;
        if (!itemStack.contains(DataComponentTypes.LODESTONE_TRACKER)) return false;
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
    public static ItemStack getPlayerSkull(String playerName) {
        ItemStack playerHead = new ItemStack(Items.PLAYER_HEAD, 1);
        ProfileComponent profileComponent = new ProfileComponent(Optional.of(playerName), Optional.empty(), new PropertyMap());
        playerHead.set(DataComponentTypes.PROFILE, profileComponent);
        return playerHead;
    }
}
