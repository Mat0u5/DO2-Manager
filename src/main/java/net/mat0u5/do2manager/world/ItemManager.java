package net.mat0u5.do2manager.world;

import com.mojang.authlib.properties.PropertyMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ChestBoat;
import net.minecraft.world.entity.vehicle.MinecartHopper;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.phys.AABB;
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

    public static void giveItemStack(Player player, ItemStack itemStack) {
        if (!player.addItem(itemStack)) {
            player.drop(itemStack, false);
        }
    }
    public static void giveItemStack(Player player, List<ItemStack> itemStacks) {
        for (ItemStack itemStack : itemStacks) {
            giveItemStack(player,itemStack);
        }
    }
    public static void removeAllComponents(ItemStack itemStack) {itemStack.set(DataComponents.CUSTOM_DATA, itemStack.getPrototype().get(DataComponents.CUSTOM_DATA));
        itemStack.set(DataComponents.MAX_STACK_SIZE, itemStack.getPrototype().get(DataComponents.MAX_STACK_SIZE));
        itemStack.set(DataComponents.MAX_DAMAGE, itemStack.getPrototype().get(DataComponents.MAX_DAMAGE));
        itemStack.set(DataComponents.DAMAGE, itemStack.getPrototype().get(DataComponents.DAMAGE));
        itemStack.set(DataComponents.UNBREAKABLE, itemStack.getPrototype().get(DataComponents.UNBREAKABLE));
        itemStack.set(DataComponents.CUSTOM_NAME, itemStack.getPrototype().get(DataComponents.CUSTOM_NAME));
        itemStack.set(DataComponents.ITEM_NAME, itemStack.getPrototype().get(DataComponents.ITEM_NAME));
        itemStack.set(DataComponents.LORE, itemStack.getPrototype().get(DataComponents.LORE));
        itemStack.set(DataComponents.RARITY, itemStack.getPrototype().get(DataComponents.RARITY));
        itemStack.set(DataComponents.ENCHANTMENTS, itemStack.getPrototype().get(DataComponents.ENCHANTMENTS));
        itemStack.set(DataComponents.CAN_PLACE_ON, itemStack.getPrototype().get(DataComponents.CAN_PLACE_ON));
        itemStack.set(DataComponents.CAN_BREAK, itemStack.getPrototype().get(DataComponents.CAN_BREAK));
        itemStack.set(DataComponents.ATTRIBUTE_MODIFIERS, itemStack.getPrototype().get(DataComponents.ATTRIBUTE_MODIFIERS));
        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, itemStack.getPrototype().get(DataComponents.CUSTOM_MODEL_DATA));
        itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, itemStack.getPrototype().get(DataComponents.HIDE_ADDITIONAL_TOOLTIP));
        itemStack.set(DataComponents.HIDE_TOOLTIP, itemStack.getPrototype().get(DataComponents.HIDE_TOOLTIP));
        itemStack.set(DataComponents.REPAIR_COST, itemStack.getPrototype().get(DataComponents.REPAIR_COST));
        itemStack.set(DataComponents.CREATIVE_SLOT_LOCK, itemStack.getPrototype().get(DataComponents.CREATIVE_SLOT_LOCK));
        itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, itemStack.getPrototype().get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE));
        itemStack.set(DataComponents.INTANGIBLE_PROJECTILE, itemStack.getPrototype().get(DataComponents.INTANGIBLE_PROJECTILE));
        itemStack.set(DataComponents.FOOD, itemStack.getPrototype().get(DataComponents.FOOD));
        itemStack.set(DataComponents.FIRE_RESISTANT, itemStack.getPrototype().get(DataComponents.FIRE_RESISTANT));
        itemStack.set(DataComponents.TOOL, itemStack.getPrototype().get(DataComponents.TOOL));
        itemStack.set(DataComponents.STORED_ENCHANTMENTS, itemStack.getPrototype().get(DataComponents.STORED_ENCHANTMENTS));
        itemStack.set(DataComponents.DYED_COLOR, itemStack.getPrototype().get(DataComponents.DYED_COLOR));
        itemStack.set(DataComponents.MAP_COLOR, itemStack.getPrototype().get(DataComponents.MAP_COLOR));
        itemStack.set(DataComponents.MAP_ID, itemStack.getPrototype().get(DataComponents.MAP_ID));
        itemStack.set(DataComponents.MAP_DECORATIONS, itemStack.getPrototype().get(DataComponents.MAP_DECORATIONS));
        itemStack.set(DataComponents.MAP_POST_PROCESSING, itemStack.getPrototype().get(DataComponents.MAP_POST_PROCESSING));
        itemStack.set(DataComponents.CHARGED_PROJECTILES, itemStack.getPrototype().get(DataComponents.CHARGED_PROJECTILES));
        itemStack.set(DataComponents.BUNDLE_CONTENTS, itemStack.getPrototype().get(DataComponents.BUNDLE_CONTENTS));
        itemStack.set(DataComponents.POTION_CONTENTS, itemStack.getPrototype().get(DataComponents.POTION_CONTENTS));
        itemStack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, itemStack.getPrototype().get(DataComponents.SUSPICIOUS_STEW_EFFECTS));
        itemStack.set(DataComponents.WRITABLE_BOOK_CONTENT, itemStack.getPrototype().get(DataComponents.WRITABLE_BOOK_CONTENT));
        itemStack.set(DataComponents.WRITTEN_BOOK_CONTENT, itemStack.getPrototype().get(DataComponents.WRITTEN_BOOK_CONTENT));
        itemStack.set(DataComponents.TRIM, itemStack.getPrototype().get(DataComponents.TRIM));
        itemStack.set(DataComponents.DEBUG_STICK_STATE, itemStack.getPrototype().get(DataComponents.DEBUG_STICK_STATE));
        itemStack.set(DataComponents.ENTITY_DATA, itemStack.getPrototype().get(DataComponents.ENTITY_DATA));
        itemStack.set(DataComponents.BUCKET_ENTITY_DATA, itemStack.getPrototype().get(DataComponents.BUCKET_ENTITY_DATA));
        itemStack.set(DataComponents.BLOCK_ENTITY_DATA, itemStack.getPrototype().get(DataComponents.BLOCK_ENTITY_DATA));
        itemStack.set(DataComponents.OMINOUS_BOTTLE_AMPLIFIER, itemStack.getPrototype().get(DataComponents.OMINOUS_BOTTLE_AMPLIFIER));
        itemStack.set(DataComponents.JUKEBOX_PLAYABLE, itemStack.getPrototype().get(DataComponents.JUKEBOX_PLAYABLE));
        itemStack.set(DataComponents.LODESTONE_TRACKER, itemStack.getPrototype().get(DataComponents.LODESTONE_TRACKER));
        itemStack.set(DataComponents.FIREWORK_EXPLOSION, itemStack.getPrototype().get(DataComponents.FIREWORK_EXPLOSION));
        itemStack.set(DataComponents.FIREWORKS, itemStack.getPrototype().get(DataComponents.FIREWORKS));
        itemStack.set(DataComponents.PROFILE, itemStack.getPrototype().get(DataComponents.PROFILE));
        itemStack.set(DataComponents.NOTE_BLOCK_SOUND, itemStack.getPrototype().get(DataComponents.NOTE_BLOCK_SOUND));
        itemStack.set(DataComponents.BANNER_PATTERNS, itemStack.getPrototype().get(DataComponents.BANNER_PATTERNS));
        itemStack.set(DataComponents.BASE_COLOR, itemStack.getPrototype().get(DataComponents.BASE_COLOR));
        itemStack.set(DataComponents.POT_DECORATIONS, itemStack.getPrototype().get(DataComponents.POT_DECORATIONS));
        itemStack.set(DataComponents.CONTAINER, itemStack.getPrototype().get(DataComponents.CONTAINER));
        itemStack.set(DataComponents.BLOCK_STATE, itemStack.getPrototype().get(DataComponents.BLOCK_STATE));
        itemStack.set(DataComponents.LOCK, itemStack.getPrototype().get(DataComponents.LOCK));
        itemStack.set(DataComponents.CONTAINER_LOOT, itemStack.getPrototype().get(DataComponents.CONTAINER_LOOT));
    }

    public static String getItemId(ItemStack itemStack) {
        return BuiltInRegistries.ITEM.getKey(itemStack.getItem()).toString();
    }
    public static List<ItemStack> getHopperItems(ServerLevel world, BlockPos hopperPos) {
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
        for (int i = 0; i < hopper.getContainerSize(); i++) {
            ItemStack stack = hopper.getItem(i);
            if (!stack.isEmpty()) {
                contents.add(stack.copy());
            }
        }
        return contents;
    }
    public static List<ItemStack> getBarrelItems(ServerLevel world, BlockPos hopperPos) {
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
        for (int i = 0; i < barrel.getContainerSize(); i++) {
            ItemStack stack = barrel.getItem(i);
            if (!stack.isEmpty()) {
                contents.add(stack.copy());
            }
        }
        return contents;
    }
    public static List<ItemStack> getContentsOfEntitiesAtPosition(Level world, BlockPos pos, int range) {
        List<ItemStack> allContents = new ArrayList<>();

        List<Entity> entities = world.getEntitiesOfClass(
                Entity.class,
                new AABB(pos.offset(-range, -range, -range).getCenter(),
                pos.offset(range, range, range).getCenter()),
                entity -> entity instanceof MinecartHopper || entity instanceof ChestBoat
        );

        for (Entity entity : entities) {
            if (entity instanceof MinecartHopper) {
                MinecartHopper hopperMinecart = (MinecartHopper) entity;
                for (int i = 0; i < hopperMinecart.getContainerSize(); i++) {
                    ItemStack stack = hopperMinecart.getItem(i);
                    if (!stack.isEmpty()) {
                        allContents.add(stack.copy());
                    }
                }
            } else if (entity instanceof ChestBoat) {
                ChestBoat chestBoat = (ChestBoat) entity;
                for (int i = 0; i < chestBoat.getContainerSize(); i++) {
                    ItemStack stack = chestBoat.getItem(i);
                    if (!stack.isEmpty()) {
                        allContents.add(stack.copy());
                    }
                }
            }
        }

        return allContents;
    }

    public static List<ItemStack> getPlayerInventory(Player player) {
        List<ItemStack> list = new ArrayList<>();
        Container inventory = player.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (!itemStack.isEmpty()) {
                list.add(itemStack.copy());
            }
        }
        return list;
    }
    public static boolean insertItemIntoBarrel(Level world, BlockPos pos, ItemStack stack) {
        if (!(world.getBlockEntity(pos) instanceof BarrelBlockEntity)) {
            return false;
        }

        BarrelBlockEntity barrel = (BarrelBlockEntity) world.getBlockEntity(pos);
        if (barrel == null) {
            return false;
        }

        Container inventory = barrel;
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack slotStack = inventory.getItem(i);
            if (slotStack.isEmpty()) {
                inventory.setItem(i, stack.copy());
                stack.setCount(0);
                barrel.setChanged();
                return true;
            } else if (ItemStack.isSameItemSameComponents(slotStack, stack)) {
                int transferAmount = Math.min(stack.getMaxStackSize() - slotStack.getCount(), stack.getCount());
                slotStack.grow(transferAmount);
                stack.shrink(transferAmount);
                if (stack.isEmpty()) {
                    barrel.setChanged();
                    return true;
                }
            }
        }

        barrel.setChanged();
        return false;
    }
    public static List<ItemStack> getDropperItems(ServerLevel world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        List<ItemStack> items = new ArrayList<>();

        if (blockEntity instanceof DispenserBlockEntity) {
            DispenserBlockEntity dropper = (DispenserBlockEntity) blockEntity;
            for (int i = 0; i < dropper.getContainerSize(); i++) {
                ItemStack stack = dropper.getItem(i);
                if (!stack.isEmpty()) {
                    items.add(stack.copy());
                }
            }
        }

        return items;
    }

    public static void clearItemPhaseOrHardcoreLore(ItemStack itemStack) {
        List<Component> currentLore = getLore(itemStack);
        if (currentLore == null || currentLore.isEmpty()) return;
        List<Component> newLore = new ArrayList<>();
        for (Component loreLine : currentLore) {
            if (!loreLine.getString().contains("-= Phase") && !loreLine.getString().contains("-= Hardcore")) {
                newLore.add(loreLine);
            }
        }
        ItemLore lore = new ItemLore(newLore);
        itemStack.set(DataComponents.LORE,lore);
    }
    public static void clearItemLore(ItemStack itemStack) {
        itemStack.remove(DataComponents.LORE);
    }
    public static void addLoreToItemStack(ItemStack itemStack, List<Component> lines) {
        List<Component> loreLines = getLore(itemStack);
        if (lines != null && !lines.isEmpty()) loreLines.addAll(lines);
        ItemLore lore = new ItemLore(loreLines);
        itemStack.set(DataComponents.LORE, lore);
    }
    public static List<Component> getLore(ItemStack itemStack) {
        ItemLore lore = itemStack.get(DataComponents.LORE);
        List<Component> lines = lore.lines();
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
        ItemContainerContents contents = container.get(DataComponents.CONTAINER);
        if (contents == null) return new ArrayList<>();
        List<ItemStack> list = new ArrayList<>();
        contents.nonEmptyItems().forEach(list::add);
        return list;
    }
    public static List<ItemStack> getBundleItemContents(ItemStack bundle) {
        BundleContents contents = bundle.get(DataComponents.BUNDLE_CONTENTS);
        if (contents == null) return new ArrayList<>();
        List<ItemStack> list = new ArrayList<>();
        contents.items().forEach(list::add);
        return list;
    }
    public static int getHopperItemsCount(ServerLevel world, BlockPos pos) {
        List<ItemStack> items = getHopperItems(world,pos);
        return countItems(items);
    }
    public static int getDropperItemsCount(ServerLevel world, BlockPos pos) {
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
                if (ItemStack.isSameItemSameComponents(inputStack, combinedStack)) {
                    int combinedAmount = Math.min(combinedStack.getMaxStackSize() - combinedStack.getCount(), inputStack.getCount());
                    combinedStack.grow(combinedAmount);
                    inputStack.shrink(combinedAmount);

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
    public static ItemStack getHoldingItem(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem != null) {
            if (!mainHandItem.isEmpty()) return mainHandItem;
        }
        ItemStack offHandItem = player.getOffhandItem();
        return offHandItem;
    }


    public static void setCustomComponentInt(ItemStack itemStack, String componentKey, int value) {
        if (itemStack == null) return;
        CustomData currentNbt = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbtComp = currentNbt == null ? new CompoundTag() : currentNbt.copyTag();
        nbtComp.putInt(componentKey,value);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtComp));
    }
    public static void setCustomComponentByte(ItemStack itemStack, String componentKey, byte value) {
        if (itemStack == null) return;
        CustomData currentNbt = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbtComp = currentNbt == null ? new CompoundTag() : currentNbt.copyTag();
        nbtComp.putByte(componentKey,value);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtComp));
    }
    public static int setCustomComponentString(ItemStack itemStack, String componentKey, String value) {
        if (itemStack == null) return 0;
        CustomData currentNbt = itemStack.get(DataComponents.CUSTOM_DATA);
        CompoundTag nbtComp = currentNbt == null ? new CompoundTag() : currentNbt.copyTag();
        nbtComp.putString(componentKey,value);
        itemStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbtComp));
        return 1;
    }
    public static String getCustomComponentString(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        CompoundTag nbtComp = nbtComponent.copyTag();
        if (!nbtComp.contains(componentKey)) return null;
        return nbtComp.getString(componentKey);
    }
    public static Integer getCustomComponentInt(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        CompoundTag nbtComp = nbtComponent.copyTag();
        if (!nbtComp.contains(componentKey)) return null;
        return nbtComp.getInt(componentKey);
    }
    public static Byte getCustomComponentByte(ItemStack itemStack, String componentKey) {
        if (itemStack == null) return null;
        CustomData nbtComponent = itemStack.get(DataComponents.CUSTOM_DATA);
        if (nbtComponent == null) return null;
        CompoundTag nbtComp = nbtComponent.copyTag();
        if (!nbtComp.contains(componentKey)) return null;
        return nbtComp.getByte(componentKey);
    }
    public static boolean hasCustomComponentEntry(ItemStack itemStack, String componentEntry) {
        CustomData nbt = itemStack.getComponents().get(DataComponents.CUSTOM_DATA);
        if (nbt == null) return false;
        return nbt.contains(componentEntry);
    }
    public static void setModelData(ItemStack itemStack, int modelData) {
        itemStack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(modelData));
    }
    public static int getModelData(ItemStack itemStack) {
        CustomModelData cmdComp = itemStack.get(DataComponents.CUSTOM_MODEL_DATA);
        if (cmdComp == null) return -1;
        return cmdComp.value();
    }
    public static int getMapId(ItemStack itemStack) {
        MapId mapIdComp = itemStack.get(DataComponents.MAP_ID);
        if (mapIdComp == null) return -1;
        return mapIdComp.id();
    }
    public static void setRoleplayData(ItemStack itemStack, byte roleplayData) {
        setCustomComponentByte(itemStack,"CustomRoleplayData",roleplayData);
    }

    public static boolean isDungeonCompass(ItemStack itemStack) {
        if (!getItemId(itemStack).equalsIgnoreCase("minecraft:compass")) return false;
        if (!itemStack.has(DataComponents.LODESTONE_TRACKER)) return false;
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
        ResolvableProfile profileComponent = new ResolvableProfile(Optional.of(playerName), Optional.empty(), new PropertyMap());
        playerHead.set(DataComponents.PROFILE, profileComponent);
        return playerHead;
    }
}
