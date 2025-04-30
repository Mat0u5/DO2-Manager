package net.mat0u5.do2manager.tcg;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TCG_Items {
    private static Random rnd = new Random();
    private static List<ItemStack> allCards = new ArrayList<>();
    private static List<String> hermits = new ArrayList<>();
    private static List<String> hermits_ultra_rare = new ArrayList<>();
    private static List<String> hermits_rare = new ArrayList<>();
    private static List<String> hermits_common = new ArrayList<>();
    private static List<String> hermits_alter_ego = new ArrayList<>();
    private static List<String> hermits_alter_ego_ultra_rare = new ArrayList<>();
    private static List<String> hermits_alter_ego_rare = new ArrayList<>();
    private static List<String> hermits_alter_ego_common = new ArrayList<>();

    private static List<String> type_miner = new ArrayList<>();
    private static List<String> type_speedrunner = new ArrayList<>();
    private static List<String> type_balanced = new ArrayList<>();
    private static List<String> type_builder = new ArrayList<>();
    private static List<String> type_redstoner = new ArrayList<>();
    private static List<String> type_farm = new ArrayList<>();
    private static List<String> type_prankster = new ArrayList<>();
    private static List<String> type_terraform = new ArrayList<>();
    private static List<String> type_pvp = new ArrayList<>();
    private static List<String> type_explorer = new ArrayList<>();

    private static List<String> effects = new ArrayList<>();
    private static List<String> effects_ultra_rare = new ArrayList<>();
    private static List<String> effects_rare = new ArrayList<>();
    private static List<String> effects_common = new ArrayList<>();

    private static List<String> items = new ArrayList<>();
    private static List<String> items_ultra_rare = new ArrayList<>();
    private static List<String> items_rare = new ArrayList<>();
    private static List<String> items_common = new ArrayList<>();

    public static void reload() {
        List<ItemStack> allCards = DatabaseManager.getAllTCGItems();
        /*
        List<String> ids = new ArrayList<>();
        for (ItemStack item : allCards) {
            if (item == null) continue;
            ids.add(String.valueOf(item.getName().getString()));
        }
        //System.out.println("\""+String.join("\", \"",ids)+"\"");
        */
        TCG_Items.setAllCards(allCards);
    }
    public static void setAllCards(List<ItemStack> newCards) {
        allCards.clear();
        for (ItemStack item : newCards) {
            item.setCount(1);
            allCards.add(item);
        }
        reloadLists();
    }

    public static final String NBT_TCG = "tcg";
    public static final String NBT_HERMITS = "hermits";
    public static final String NBT_ALTEREGO = "alter_ego";
    public static final String NBT_EFFECTS = "effect_card";
    public static final String NBT_ITEMS = "item_card";
    public static final String NBT_TYPE = "type";

    public static void reloadLists() {
        for (ItemStack item : allCards) {
            if (item == null) continue;
            String itemName = item.getName().getString();
            if (item.get(DataComponentTypes.CUSTOM_NAME) != null) itemName = item.get(DataComponentTypes.CUSTOM_NAME).getString();
            List<String> errors = new ArrayList<>();
            int added = 0;
            int checkLayer1 = 0;
            int checkLayer2 = 0;
            int checkLayer3 = 0;

            Integer rarity = null;
            if (ItemManager.hasCustomComponentEntry(item, "common")) rarity = 1;
            if (ItemManager.hasCustomComponentEntry(item, "rare")) rarity = 2;
            if (ItemManager.hasCustomComponentEntry(item, "ultra_rare")) rarity = 3;

            if (ItemManager.hasCustomComponentEntry(item, NBT_TCG) && rarity != null) {
                checkLayer1++;
                if (ItemManager.hasCustomComponentEntry(item, NBT_HERMITS)) {
                    if (rarity == 1) hermits_common.add(itemName);
                    if (rarity == 2) hermits_rare.add(itemName);
                    if (rarity == 3) hermits_ultra_rare.add(itemName);
                    checkLayer2++;
                    added++;
                }
                if (ItemManager.hasCustomComponentEntry(item, NBT_ALTEREGO)) {
                    if (rarity == 1) hermits_alter_ego_common.add(itemName);
                    if (rarity == 2) hermits_alter_ego_rare.add(itemName);
                    if (rarity == 3) hermits_alter_ego_ultra_rare.add(itemName);
                    checkLayer2++;
                    added++;
                }
                if (ItemManager.hasCustomComponentEntry(item, NBT_EFFECTS)) {
                    if (rarity == 1) effects_common.add(itemName);
                    if (rarity == 2) effects_rare.add(itemName);
                    if (rarity == 3) effects_ultra_rare.add(itemName);
                    checkLayer2++;
                    added++;
                    checkLayer3 = 1;
                }
                if (ItemManager.hasCustomComponentEntry(item, NBT_ITEMS)) {
                    if (rarity == 1) items_common.add(itemName);
                    if (rarity == 2) items_rare.add(itemName);
                    if (rarity == 3) items_ultra_rare.add(itemName);
                    checkLayer2++;
                    added++;
                }

                if (ItemManager.hasCustomComponentEntry(item, NBT_TYPE)) {
                    String type = ItemManager.getCustomComponentString(item, NBT_TYPE);
                    checkLayer3 = 1;
                    added++;
                    if (type != null) {
                        if (type.equalsIgnoreCase("miner")) type_miner.add(itemName);
                        else if (type.equalsIgnoreCase("speedrunner")) type_speedrunner.add(itemName);
                        else if (type.equalsIgnoreCase("balanced")) type_balanced.add(itemName);
                        else if (type.equalsIgnoreCase("builder")) type_builder.add(itemName);
                        else if (type.equalsIgnoreCase("redstoner")) type_redstoner.add(itemName);
                        else if (type.equalsIgnoreCase("farm")) type_farm.add(itemName);
                        else if (type.equalsIgnoreCase("prankster")) type_prankster.add(itemName);
                        else if (type.equalsIgnoreCase("terraformer")) type_terraform.add(itemName);
                        else if (type.equalsIgnoreCase("pvp")) type_pvp.add(itemName);
                        else if (type.equalsIgnoreCase("explorer")) type_explorer.add(itemName);
                        else {
                             checkLayer3 = 0;
                             added--;
                         }
                    }
                    else {
                        checkLayer3 = 0;
                        added--;
                    }
                }
            }
            if (checkLayer1 == 0) errors.add("Item NBT does not contain " + NBT_TCG);

            if (rarity == null) {
                errors.add("Item NBT does not contain item rarity.");
            }
            else if (rarity <= 0 || rarity >= 4) {
                errors.add("Item NBT rarity must be set to 1/2/3");
            }

            if (checkLayer2 == 0) errors.add("Item NBT does not contain the Item Rarity");
            if (checkLayer2 > 1) errors.add("Item NBT contains more than one Item Rarity");

            if (checkLayer3 == 0) errors.add("Item NBT does not contain the Item Type");

            if (added == 0) errors.add("Item was not added to any list.");
            if (!errors.isEmpty()) {
                Main.LOGGER.warn("[TCG ITEMS DATABASE] -------");
                Main.LOGGER.warn("[TCG ITEMS DATABASE] NBT errors in item: " + item.toString());
                for (String s : errors) {
                    Main.LOGGER.warn("[TCG ITEMS DATABASE] Error in item: " + itemName + " - " + s);
                }
                Main.LOGGER.warn("[TCG ITEMS DATABASE] -------");
            }
        }
        items = new ArrayList<>();
        effects = new ArrayList<>();
        hermits = new ArrayList<>();
        hermits_alter_ego = new ArrayList<>();
        items.addAll(items_common);
        items.addAll(items_rare);
        items.addAll(items_ultra_rare);

        effects.addAll(effects_ultra_rare);
        effects.addAll(effects_rare);
        effects.addAll(effects_common);

        hermits_alter_ego.addAll(hermits_alter_ego_common);
        hermits_alter_ego.addAll(hermits_alter_ego_rare);
        hermits_alter_ego.addAll(hermits_alter_ego_ultra_rare);

        hermits.addAll(hermits_ultra_rare);
        hermits.addAll(hermits_rare);
        hermits.addAll(hermits_common);
        hermits.addAll(hermits_alter_ego);

    }
    private static ItemStack randomElement(List<ItemStack> list) {
        if (list == null) return Items.DIRT.getDefaultStack();
        if (list.isEmpty()) return Items.DIRT.getDefaultStack();
        return list.get(rnd.nextInt(list.size()));
    }
    private static List<ItemStack> getMatchingCard(List<String> validNames) {
        return getMatchingCard(allCards, validNames);
    }
    private static List<ItemStack> getMatchingCard(List<ItemStack> cards, List<String> validNames) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack card : cards) {
            String name = card.getName().getString();
            if (validNames.contains(name)) {
                result.add(card.copy());
            }
        }
        return result;
    }




    public static List<ItemStack> getAllCards() {
        return allCards;
    }
    public static ItemStack getRandomCard() {
        return randomElement(getAllCards());
    }
    ///
    public static List<String> getTypeFilter(String type) {
        if (type.equalsIgnoreCase("miner")) return type_miner;
        if (type.equalsIgnoreCase("speedrunner")) return type_speedrunner;
        if (type.equalsIgnoreCase("balanced")) return type_balanced;
        if (type.equalsIgnoreCase("builder")) return type_builder;
        if (type.equalsIgnoreCase("redstoner")) return type_redstoner;
        if (type.equalsIgnoreCase("farm")) return type_farm;
        if (type.equalsIgnoreCase("prankster")) return type_prankster;
        if (type.equalsIgnoreCase("terraform")) return type_terraform;
        if (type.equalsIgnoreCase("pvp")) return type_pvp;
        if (type.equalsIgnoreCase("explorer")) return type_explorer;
        return null;
    }
    public static String getRandomType(List<String> exclude) {
        List<String> list = new ArrayList<>(List.of(
                "miner", "speedrunner", "balanced", "builder", "redstoner",
                "farm", "prankster", "terraform", "pvp", "explorer"
        ));
        for (String s : exclude) {
            list.remove(s);
        }
        return list.get(rnd.nextInt(list.size()));
    }
    public static String getRandomType() {
        return getRandomType(List.of());
    }

    ///

    public static ItemStack getRandomTypeHermit(String type) {
        return randomElement(getMatchingCard(getAllHermits(),getTypeFilter(type)));
    }
    public static ItemStack getRandomTypeUltraRareHermit(String type, boolean includeAlterEgo) {
        List<ItemStack> hermits = getUltraRareHermits();
        if (includeAlterEgo) hermits.addAll(getMatchingCard(hermits_alter_ego_ultra_rare));
        return randomElement(getMatchingCard(hermits,getTypeFilter(type)));
    }
    public static ItemStack getRandomTypeRareHermit(String type, boolean includeAlterEgo) {
        List<ItemStack> hermits = getRareHermits();
        if (includeAlterEgo) hermits.addAll(getMatchingCard(hermits_alter_ego_rare));
        return randomElement(getMatchingCard(hermits,getTypeFilter(type)));
    }
    public static ItemStack getRandomTypeCommonHermit(String type, boolean includeAlterEgo) {
        List<ItemStack> hermits = getCommonHermits();
        if (includeAlterEgo) hermits.addAll(getMatchingCard(hermits_alter_ego_common));
        return randomElement(getMatchingCard(hermits,getTypeFilter(type)));
    }
    public static ItemStack getRandomTypeItem(String type) {
        return randomElement(getMatchingCard(getAllItems(),getTypeFilter(type)));
    }
    public static ItemStack getRandomTypeUltraRareItem(String type) {
        return randomElement(getMatchingCard(getUltraRareItems(),getTypeFilter(type)));
    }
    public static ItemStack getRandomTypeRareItem(String type) {
        return randomElement(getMatchingCard(getRareItems(),getTypeFilter(type)));
    }
    public static ItemStack getRandomTypeCommonItem(String type) {
        return randomElement(getMatchingCard(getCommonItems(),getTypeFilter(type)));
    }

    ///
    public static List<ItemStack> getAllHermits() {
        return getMatchingCard(hermits);
    }
    public static List<ItemStack> getUltraRareHermits() {
        return getMatchingCard(hermits_ultra_rare);
    }
    public static List<ItemStack> getRareHermits() {
        return getMatchingCard(hermits_rare);
    }
    public static List<ItemStack> getCommonHermits() {
        return getMatchingCard(hermits_common);
    }
    public static List<ItemStack> getAlterEgoHermits() {
        return getMatchingCard(hermits_alter_ego);
    }
    public static ItemStack getRandomHermit() {
        return randomElement(getAllHermits());
    }
    public static ItemStack getRandomUltraRareHermit() {
        return randomElement(getUltraRareHermits());
    }
    public static ItemStack getRandomRareHermit() {
        return randomElement(getRareHermits());
    }
    public static ItemStack getRandomCommonHermit() {
        return randomElement(getCommonHermits());
    }
    public static ItemStack getRandomAlterEgoHermit() {
        return randomElement(getAlterEgoHermits());
    }

    ///
    public static List<ItemStack> getAllEffects() {
        return getMatchingCard(effects);
    }
    public static List<ItemStack> getUltraRareEffects() {
        return getMatchingCard(effects_ultra_rare);
    }
    public static List<ItemStack> getRareEffects() {
        return getMatchingCard(effects_rare);
    }
    public static List<ItemStack> getCommonEffects() {
        return getMatchingCard(effects_common);
    }
    public static ItemStack getRandomEffect() {
        return randomElement(getAllEffects());
    }
    public static ItemStack getRandomUltraRareEffect() {
        return randomElement(getUltraRareEffects());
    }
    public static ItemStack getRandomRareEffect() {
        return randomElement(getRareEffects());
    }
    public static ItemStack getRandomCommonEffect() {
        return randomElement(getCommonEffects());
    }
    ///
    public static List<ItemStack> getAllItems() {
        return getMatchingCard(items);
    }
    public static List<ItemStack> getUltraRareItems() {
        return getMatchingCard(items_ultra_rare);
    }
    public static List<ItemStack> getRareItems() {
        return getMatchingCard(items_rare);
    }
    public static List<ItemStack> getCommonItems() {
        return getMatchingCard(items_common);
    }
    public static ItemStack getRandomItem() {
        return randomElement(getAllItems());
    }
    public static ItemStack getRandomUltraRareItem() {
        return randomElement(getUltraRareItems());
    }
    public static ItemStack getRandomRareItem() {
        return randomElement(getRareItems());
    }
    public static ItemStack getRandomCommonItem() {
        return randomElement(getCommonItems());
    }
}
