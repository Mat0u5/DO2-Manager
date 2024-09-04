package net.mat0u5.do2manager.tcg;

import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.mat0u5.do2manager.world.ItemManager.createItemStackEntry;

public class TCG_DeckCreator {
    static Random rnd = new Random();
    public static ItemStack getBasePack(String addToName) {
        ItemStack itemStack = Items.BUNDLE.getDefaultStack();
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("CustomModelData", 1);
        Text customName = Text.literal("Hermitcraft ").styled(style -> style.withColor(Formatting.DARK_GRAY).withBold(true).withItalic(false))
                .append(Text.literal("TC").styled(style -> style.withColor(Formatting.DARK_AQUA).withBold(true).withItalic(false)))
                .append(Text.literal("G").styled(style -> style.withColor(0x80C71F).withBold(true).withItalic(false)))
                .append(addToName).styled(style -> style.withBold(true).withItalic(false));
        itemStack.setCustomName(customName);
        return itemStack;
    }
    public static ItemStack getHermitPack() {
        ItemStack itemStack = getBasePack(" §6");
        Text customName = Text.literal("Hermitcraft ").styled(style -> style.withColor(Formatting.DARK_GRAY).withBold(true).withItalic(false))
                .append(Text.literal("TC").styled(style -> style.withColor(Formatting.DARK_AQUA).withBold(true).withItalic(false)))
                .append(Text.literal("G ").styled(style -> style.withColor(0x80C71F).withBold(true).withItalic(false)))
                .append(Text.literal("Hermit Pack").styled(style -> style.withColor(0xFED83D).withBold(true).withItalic(false)));
        itemStack.setCustomName(customName);

        NbtCompound nbt = itemStack.getOrCreateNbt();
        NbtList itemsList = new NbtList();
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonHermit()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonHermit()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonHermit()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonHermit()));
        boolean isUltraRare = rnd.nextInt(10)==0;
        if (isUltraRare) itemsList.add(createItemStackEntry(TCG_Items.getRandomUltraRareHermit()));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomRareHermit()));

        nbt.put("Items", itemsList);
        itemStack.setNbt(nbt);

        return itemStack;
    }
    public static ItemStack getBoosterPack() {
        ItemStack itemStack = getBasePack(" §l§fBooster Pack");


        NbtCompound nbt = itemStack.getOrCreateNbt();
        NbtList itemsList = new NbtList();
        int rareRollOne = rnd.nextInt(6);
        int rareRollTwo = rnd.nextInt(6);
        while (rareRollTwo == rareRollOne) rareRollTwo = rnd.nextInt(6);

        if (rareRollOne == 0 || rareRollTwo == 0) {
            if (rnd.nextInt(10)==0) itemsList.add(createItemStackEntry(TCG_Items.getRandomUltraRareHermit()));
            else itemsList.add(createItemStackEntry(TCG_Items.getRandomRareHermit()));
        }
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonHermit()));
        if (rareRollOne == 1 || rareRollTwo == 1) {
            if (rnd.nextInt(10)==0) itemsList.add(createItemStackEntry(TCG_Items.getRandomUltraRareHermit()));
            else itemsList.add(createItemStackEntry(TCG_Items.getRandomRareHermit()));
        }
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonHermit()));

        if (rareRollOne == 2 || rareRollTwo == 2) {
            if (rnd.nextInt(2)==0) itemsList.add(createItemStackEntry(TCG_Items.getRandomUltraRareEffect()));
            else itemsList.add(createItemStackEntry(TCG_Items.getRandomRareEffect()));
        }
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonEffect()));
        if (rareRollOne == 3 || rareRollTwo == 3) {
            if (rnd.nextInt(2)==0) itemsList.add(createItemStackEntry(TCG_Items.getRandomUltraRareEffect()));
            else itemsList.add(createItemStackEntry(TCG_Items.getRandomRareEffect()));
        }
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonEffect()));

        if (rareRollOne == 4 || rareRollTwo == 4) itemsList.add(createItemStackEntry(TCG_Items.getRandomRareItem()));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonItem()));
        if (rareRollOne == 5 || rareRollTwo == 5) itemsList.add(createItemStackEntry(TCG_Items.getRandomRareItem()));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonItem()));

        nbt.put("Items", itemsList);
        itemStack.setNbt(nbt);

        return itemStack;
    }
    public static ItemStack getAlterEgoPack() {
        ItemStack itemStack = getBasePack(" §aAlter Ego Pack");


        NbtCompound nbt = itemStack.getOrCreateNbt();
        NbtList itemsList = new NbtList();
        itemsList.add(createItemStackEntry(TCG_Items.getRandomAlterEgoHermit()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomAlterEgoHermit()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonItem()));
        if (rnd.nextInt(2)==0) itemsList.add(createItemStackEntry(TCG_Items.getRandomRareEffect()));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonEffect()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonEffect()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonEffect()));

        nbt.put("Items", itemsList);
        itemStack.setNbt(nbt);

        return itemStack;
    }
    public static ItemStack getEffectPack() {
        ItemStack itemStack = getBasePack("");
        Text customName = Text.literal("Hermitcraft ").styled(style -> style.withColor(Formatting.DARK_GRAY).withBold(true).withItalic(false))
                .append(Text.literal("TC").styled(style -> style.withColor(Formatting.DARK_AQUA).withBold(true).withItalic(false)))
                .append(Text.literal("G ").styled(style -> style.withColor(0x80C71F).withBold(true).withItalic(false)))
                .append(Text.literal("§lEffect Pack").styled(style -> style.withColor(0xF38BAA).withBold(true).withItalic(false)));
        itemStack.setCustomName(customName);

        NbtCompound nbt = itemStack.getOrCreateNbt();
        NbtList itemsList = new NbtList();
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonEffect()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonEffect()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonEffect()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonEffect()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomRareEffect()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomRareEffect()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomUltraRareEffect()));
        nbt.put("Items", itemsList);
        itemStack.setNbt(nbt);

        return itemStack;
    }
    public static ItemStack getItemPack() {
        ItemStack itemStack = getBasePack(" §l§6Item Pack");

        NbtCompound nbt = itemStack.getOrCreateNbt();
        NbtList itemsList = new NbtList();
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonItem()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonItem()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonItem()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonItem()));
        itemsList.add(createItemStackEntry(TCG_Items.getRandomRareItem()));
        nbt.put("Items", itemsList);
        itemStack.setNbt(nbt);

        return itemStack;
    }
    public static ItemStack getStarterDeck() {
        ItemStack itemStack = getBasePack(" §6");
        Text customName = Text.literal("Hermitcraft ").styled(style -> style.withColor(Formatting.DARK_GRAY).withBold(true).withItalic(false))
                .append(Text.literal("TC").styled(style -> style.withColor(Formatting.DARK_AQUA).withBold(true).withItalic(false)))
                .append(Text.literal("G ").styled(style -> style.withColor(0x80C71F).withBold(true).withItalic(false)))
                .append(Text.literal("§lStarter Deck").styled(style -> style.withColor(0xB02E26).withBold(true).withItalic(false)));
        itemStack.setCustomName(customName);

        NbtCompound nbt = itemStack.getOrCreateNbt();
        NbtList itemsList = new NbtList();
        String type1 = TCG_Items.getRandomType(List.of("prankster","speedrunner","terraform"));
        String type2 = TCG_Items.getRandomType(List.of(type1));
        String type3 = TCG_Items.getRandomType(List.of(type1, type2));
        if (type2.equalsIgnoreCase(type1) || type3.equalsIgnoreCase(type2) || type3.equalsIgnoreCase(type1)) {
            System.out.println("ERROR_02");
            return null;
        }
        int randomRares = rnd.nextInt(8);
        int randomRares2 = rnd.nextInt(8);
        while (randomRares2 == randomRares) randomRares2 = rnd.nextInt(8);

        if (randomRares==0 || randomRares2==0) itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeRareHermit(type1, true)));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeCommonHermit(type1, true)));
        if (randomRares==1 || randomRares2==1) itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeRareHermit(type1, true)));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeCommonHermit(type1, true)));
        if (randomRares==2 || randomRares2==2) itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeRareHermit(type1, true)));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeCommonHermit(type1, true)));
        if (randomRares==3 || randomRares2==3) itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeRareHermit(type1, true)));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeCommonHermit(type1, true)));

        if (randomRares==4 || randomRares2==4) itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeRareHermit(type2, true)));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeCommonHermit(type2, true)));
        if (randomRares==5 || randomRares2==5) itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeRareHermit(type2, true)));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeCommonHermit(type2, true)));

        if (randomRares==6 || randomRares2==6) itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeRareHermit(type3, true)));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeCommonHermit(type3, true)));
        if (randomRares==7 || randomRares2==7) itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeRareHermit(type3, true)));
        else itemsList.add(createItemStackEntry(TCG_Items.getRandomTypeCommonHermit(type3, true)));


        ItemStack item1 = TCG_Items.getRandomTypeCommonItem(type1);
        ItemStack item2 = TCG_Items.getRandomTypeCommonItem(type2);
        ItemStack item3 = TCG_Items.getRandomTypeCommonItem(type3);
        item1.setCount(13);
        item2.setCount(7);
        item3.setCount(7);
        itemsList.add(createItemStackEntry(item1));
        itemsList.add(createItemStackEntry(item2));
        itemsList.add(createItemStackEntry(item3));
        for (int i = 0; i < 6; i++) {
            itemsList.add(createItemStackEntry(TCG_Items.getRandomCommonEffect()));
        }
        itemsList.add(createItemStackEntry(TCG_Items.getRandomRareEffect()));
        
        

        nbt.put("Items", itemsList);
        itemStack.setNbt(nbt);

        return itemStack;
    }
}
