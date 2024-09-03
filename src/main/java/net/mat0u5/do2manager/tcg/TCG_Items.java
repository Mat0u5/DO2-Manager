package net.mat0u5.do2manager.tcg;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TCG_Items {
    private static Random rnd = new Random();
    private static List<ItemStack> allCards = new ArrayList<>();
    private static List<String> hermits = new ArrayList<>();
    private static List<String> hermits_ultra_rare = List.of("TFC ★ ultra rare ★ Miner Type", "Etho ★ ultra rare ★ PVP Type", "Beef ★ ultra rare ★ Explorer Type");
    private static List<String> hermits_rare = List.of("Hypno ★ rare ★ Miner Type", "Bdubs ★ rare ★ Balanced Type", "Cubfan ★ rare ★ Speedrunner Type", "DocM ★ rare ★ Farm Type", "Etho ★ rare ★ Redstone Type", "False ★ rare ★ Builder Type", "Gem ★ rare ★ Terraform Type", "Grian ★ rare ★ Prankster Type", "Iskall ★ rare ★ Farm Type", "Joe Hills ★ rare ★ Farm Type", "Keralis ★ rare ★ Terraform Type", "Pearl ★ rare ★ Terraform Type", "Rendog ★ rare ★ Builder Type", "Scar ★ rare ★ Builder Type", "Impulse ★ rare ★ Redstone Type", "Jevin ★ rare ★ Speedrunner Type", "TFC ★ rare ★ Miner Type", "Stress ★ rare ★ Prankster Type", "Tango ★ rare ★ Farm Type", "Beef ★ rare ★ Builder Type", "Wels ★ rare ★ PVP Type", "Mumbo ★ rare ★ Prankster Type", "xB ★ rare ★ Explorer Type", "Xisuma ★ rare ★ Redstone Type", "Zedaph ★ rare ★ Explorer Type", "Cleo ★ rare ★ PVP Type");
    private static List<String> hermits_common = List.of("Rendog ■ common ■ Balanced Type", "Bdubs ■ common ■ Builder Type", "DocM ■ common ■ Redstone Type", "Hypno ■ common ■ Balanced Type", "False ■ common ■ PVP Type", "Gem ■ common ■ Builder Type", "Grian ■ common ■ Builder Type", "Etho ■ common ■ Balanced Type", "Jevin ■ common ■ Explorer Type", "Joe Hills ■ common ■ Explorer Type", "Mumbo ■ common ■ Redstone Type", "Cubfan ■ common ■ Balanced Type", "Scar ■ common ■ Terraform Type", "Impulse ■ common ■ Farm Type", "Iskall ■ common ■ Balanced Type", "Stress ■ common ■ Builder Type", "Beef ■ common ■ Balanced Type", "Tango ■ common ■ Redstone Type", "TFC ■ common ■ Miner Type", "Wels ■ common ■ Builder Type", "Pearl ■ common ■ Builder Type", "Keralis ■ common ■ Builder Type", "xB ■ common ■ PVP Type", "Xisuma ■ common ■ Farm Type", "Zedaph ■ common ■ Redstone Type", "Cleo ■ common ■ Builder Type");
    private static List<String> hermits_alter_ego = List.of("Llamadad ★ rare ★ Balanced Type", "Evil Jevin ■ common ■ Miner Type", "Potato Boy ★ rare ★ Farm Type", "Poultry Man ■ common ■ Prankster Type", "Renbob ★ rare ★ Explorer Type", "Jingler ★ rare ★ Speedrunner Type", "Hotguy ★ rare ★ Explorer Type", "Beetlejhost ■ common ■ Speedrunner Type", "Goatfather ★ rare ★ Prankster Type", "Human Cleo ★ rare ★ PVP Type", "Evil X ★ rare ★ Balanced Type", "Helsknight ★ rare ★ PVP Type");

    private static List<String> effects = new ArrayList<>();
    private static List<String> effects_ultra_rare = List.of("Mending ★ ultra rare ★ Single Use", "Fortune ★ ultra rare ★ Single Use", "Clock ★ ultra rare ★ Single Use", "Totem Of Undying ★ ultra rare ★ Attach", "Fishing Rod ★ ultra rare ★ Single Use", "Netherite Sword ★ ultra rare ★ Single Use", "Bed ★ ultra rare ★ Attach", "Golden Apple ★ ultra rare ★ Single Use", "Netherite Armour ★ ultra rare ★ Attach", "Armour Stand ★ ultra rare ★ Attach", "Sweeping Edge ★ ultra rare ★ Single Use", "Thorns III ★ ultra rare ★ Attach", "Ladder ★ ultra rare ★ Single Use");
    private static List<String> effects_rare = List.of("Emerald ★ rare ★ Single Use", "Knockback ★ rare ★ Single Use", "Loyalty ★ rare ★ Attach", "Efficiency ★ rare ★ Single Use", "Looting ★ rare ★ Attach", "Invisibility ★ rare ★ Single Use", "Instant Health II ★ rare ★ Single Use", "Splash Potion Of Poison ★ rare ★ Single Use", "Lava Bucket ★ rare ★ Single Use", "Wolf ★ rare ★ Attach", "Diamond Armour ★ rare ★ Attach", "Golden Axe ★ rare ★ Single Use", "Chest ★ rare ★ Single Use", "Diamond Sword ★ rare ★ Single Use", "Spyglass ★ rare ★ Single Use", "Crossbow ★ rare ★ Single Use", "Thorns II ★ rare ★ Attach", "String ★ rare ★ Attach", "Egg ★ rare ★ Single Use", "Target Block ★ rare ★ Single Use", "Trident ★ rare ★ Single Use", "Splash Potion Of Healing II ★ rare ★ Single Use", "Bad Omen ★ rare ★ Single Use", "Command Block ★ rare ★ Attach", "Turtle Shell ★ rare ★ Attach", "Anvil ★ rare ★ Single Use");
    private static List<String> effects_common = List.of("Curse Of Binding ■ common ■ Single Use", "Curse Of Vanishing ■ common ■ Single Use", "Thorns ■ common ■ Attach", "Lead ■ common ■ Single Use", "Chorus Fruit ■ common ■ Single Use", "Iron Armour ■ common ■ Attach", "Gold Armour ■ common ■ Attach", "Shield ■ common ■ Attach", "Water Bucket ■ common ■ Single Use OR Attach", "Milk Bucket ■ common ■ Single Use OR Attach", "Instant Health ■ common ■ Single Use", "Bow ■ common ■ Single Use", "TNT ■ common ■ Single Use", "Iron Sword ■ common ■ Single Use", "Flint And Steel ■ common ■ Single Use", "Composter ■ common ■ Single Use", "Splash Potion Of Healing ■ common ■ Single Use", "Fire Charge ■ common ■ Single Use", "Chainmail Armour ■ common ■ Attach", "Ender Pearl ■ common ■ Single Use", "Lightning Rod ■ common ■ Attach", "Piston ■ common ■ Single Use", "Potion Of Slowness ■ common ■ Single Use", "Potion Of Weakness ■ common ■ Single Use");

    private static List<String> items = new ArrayList<>();
    private static List<String> items_rare = List.of("Builder ★ double ★", "Redstone ★ double ★", "PVP ★ double ★", "Explorer ★ double ★", "Speedrunner ★ double ★", "Balanced ★ double ★", "Prankster ★ double ★", "Terraform ★ double ★", "Miner ★ double ★", "Farm ★ double ★");
    private static List<String> items_common = List.of("Builder", "Redstone", "PVP", "Explorer", "Speedrunner", "Balanced", "Prankster", "Terraform", "Miner", "Farm");

    public static void reload() {
        List<ItemStack> allCards = DatabaseManager.getAllTCGItems();
        List<String> ids = new ArrayList<>();
        for (ItemStack item : allCards) {
            if (item == null) continue;
            /*
            int mapId = ItemManager.getMapId(item);
            if (mapId == -1) continue;
            */
            ids.add(String.valueOf(item.getName().getString()));
        }
        //System.out.println("\""+String.join("\", \"",ids)+"\"");
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
    public static void reloadLists() {
        items = new ArrayList<>();
        effects = new ArrayList<>();
        hermits = new ArrayList<>();
        items.addAll(items_common);
        items.addAll(items_rare);

        effects.addAll(effects_ultra_rare);
        effects.addAll(effects_rare);
        effects.addAll(effects_common);

        hermits.addAll(hermits_ultra_rare);
        hermits.addAll(hermits_rare);
        hermits.addAll(hermits_common);
        hermits.addAll(hermits_alter_ego);
    }
    private static ItemStack randomElement(List<ItemStack> list) {
        if (list == null) return null;
        if (list.isEmpty()) return null;
        return list.get(rnd.nextInt(list.size()));
    }
    private static List<ItemStack> getMatchingCard(List<String> validNames) {
        List<ItemStack> result = new ArrayList<>();
        for (ItemStack card : allCards) {
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
    public static List<ItemStack> getRareItems() {
        return getMatchingCard(items_rare);
    }
    public static List<ItemStack> getCommonItems() {
        return getMatchingCard(items_common);
    }
    public static ItemStack getRandomItem() {
        return randomElement(getAllItems());
    }
    public static ItemStack getRandomRareItem() {
        return randomElement(getRareItems());
    }
    public static ItemStack getRandomCommonItem() {
        return randomElement(getCommonItems());
    }
}
