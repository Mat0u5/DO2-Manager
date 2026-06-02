package net.mat0u5.do2manager.tcg;

import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TCG_DeckCreator {
    static Random rnd = new Random();
    public static ItemStack getBasePack(String addToName) {
        ItemStack itemStack = Items.BUNDLE.getDefaultInstance();
        ItemManager.setModelData(itemStack,1);
        Component customName = Component.literal("Hermitcraft ").withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY).withBold(true).withItalic(false))
                .append(Component.literal("TC").withStyle(style -> style.withColor(ChatFormatting.DARK_AQUA).withBold(true).withItalic(false)))
                .append(Component.literal("G").withStyle(style -> style.withColor(0x80C71F).withBold(true).withItalic(false)))
                .append(addToName).withStyle(style -> style.withBold(true).withItalic(false));
        itemStack.set(DataComponents.CUSTOM_NAME, Component.translationArg(customName));
        return itemStack;
    }
    public static ItemStack getHermitPack() {
        ItemStack itemStack = getBasePack(" §6");
        Component customName = Component.literal("Hermitcraft ").withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY).withBold(true).withItalic(false))
                .append(Component.literal("TC").withStyle(style -> style.withColor(ChatFormatting.DARK_AQUA).withBold(true).withItalic(false)))
                .append(Component.literal("G ").withStyle(style -> style.withColor(0x80C71F).withBold(true).withItalic(false)))
                .append(Component.literal("Hermit Pack").withStyle(style -> style.withColor(0xFED83D).withBold(true).withItalic(false)));
        itemStack.set(DataComponents.CUSTOM_NAME, Component.translationArg(customName));

        List<ItemStack> itemsList = new ArrayList<>();
        itemsList.add(TCG_Items.getRandomCommonHermit());
        itemsList.add(TCG_Items.getRandomCommonHermit());
        itemsList.add(TCG_Items.getRandomCommonHermit());
        itemsList.add(TCG_Items.getRandomCommonHermit());
        boolean isUltraRare = rnd.nextInt(10)==0;
        if (isUltraRare) itemsList.add(TCG_Items.getRandomUltraRareHermit());
        else itemsList.add(TCG_Items.getRandomRareHermit());

        BundleContents bundleContentsComponent = new BundleContents(itemsList);
        itemStack.set(DataComponents.BUNDLE_CONTENTS, bundleContentsComponent);
        return itemStack;
    }
    public static ItemStack getBoosterPack() {
        ItemStack itemStack = getBasePack(" §l§fBooster Pack");


        List<ItemStack> itemsList = new ArrayList<>();
        int rareRollOne = rnd.nextInt(6);
        int rareRollTwo = rnd.nextInt(6);
        while (rareRollTwo == rareRollOne) rareRollTwo = rnd.nextInt(6);

        if (rareRollOne == 0 || rareRollTwo == 0) {
            if (rnd.nextInt(10)==0) itemsList.add(TCG_Items.getRandomUltraRareHermit());
            else itemsList.add(TCG_Items.getRandomRareHermit());
        }
        else itemsList.add(TCG_Items.getRandomCommonHermit());
        if (rareRollOne == 1 || rareRollTwo == 1) {
            if (rnd.nextInt(10)==0) itemsList.add(TCG_Items.getRandomUltraRareHermit());
            else itemsList.add(TCG_Items.getRandomRareHermit());
        }
        else itemsList.add(TCG_Items.getRandomCommonHermit());

        if (rareRollOne == 2 || rareRollTwo == 2) {
            if (rnd.nextInt(2)==0) itemsList.add(TCG_Items.getRandomUltraRareEffect());
            else itemsList.add(TCG_Items.getRandomRareEffect());
        }
        else itemsList.add(TCG_Items.getRandomCommonEffect());
        if (rareRollOne == 3 || rareRollTwo == 3) {
            if (rnd.nextInt(2)==0) itemsList.add(TCG_Items.getRandomUltraRareEffect());
            else itemsList.add(TCG_Items.getRandomRareEffect());
        }
        else itemsList.add(TCG_Items.getRandomCommonEffect());

        if (rareRollOne == 4 || rareRollTwo == 4) itemsList.add(TCG_Items.getRandomRareItem());
        else itemsList.add(TCG_Items.getRandomCommonItem());
        if (rareRollOne == 5 || rareRollTwo == 5) itemsList.add(TCG_Items.getRandomRareItem());
        else itemsList.add(TCG_Items.getRandomCommonItem());

        BundleContents bundleContentsComponent = new BundleContents(itemsList);
        itemStack.set(DataComponents.BUNDLE_CONTENTS, bundleContentsComponent);

        return itemStack;
    }
    public static ItemStack getAlterEgoPack() {
        ItemStack itemStack = getBasePack(" §aAlter Ego Pack");


        List<ItemStack> itemsList = new ArrayList<>();
        itemsList.add(TCG_Items.getRandomAlterEgoHermit());
        itemsList.add(TCG_Items.getRandomAlterEgoHermit());
        itemsList.add(TCG_Items.getRandomCommonItem());
        if (rnd.nextInt(2)==0) itemsList.add(TCG_Items.getRandomRareEffect());
        else itemsList.add(TCG_Items.getRandomCommonEffect());
        itemsList.add(TCG_Items.getRandomCommonEffect());
        itemsList.add(TCG_Items.getRandomCommonEffect());

        BundleContents bundleContentsComponent = new BundleContents(itemsList);
        itemStack.set(DataComponents.BUNDLE_CONTENTS, bundleContentsComponent);

        return itemStack;
    }
    public static ItemStack getEffectPack() {
        ItemStack itemStack = getBasePack("");
        Component customName = Component.literal("Hermitcraft ").withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY).withBold(true).withItalic(false))
                .append(Component.literal("TC").withStyle(style -> style.withColor(ChatFormatting.DARK_AQUA).withBold(true).withItalic(false)))
                .append(Component.literal("G ").withStyle(style -> style.withColor(0x80C71F).withBold(true).withItalic(false)))
                .append(Component.literal("§lEffect Pack").withStyle(style -> style.withColor(0xF38BAA).withBold(true).withItalic(false)));
        itemStack.set(DataComponents.CUSTOM_NAME, Component.translationArg(customName));

        List<ItemStack> itemsList = new ArrayList<>();
        itemsList.add(TCG_Items.getRandomCommonEffect());
        itemsList.add(TCG_Items.getRandomCommonEffect());
        itemsList.add(TCG_Items.getRandomCommonEffect());
        itemsList.add(TCG_Items.getRandomCommonEffect());
        itemsList.add(TCG_Items.getRandomRareEffect());
        itemsList.add(TCG_Items.getRandomRareEffect());
        itemsList.add(TCG_Items.getRandomUltraRareEffect());
        BundleContents bundleContentsComponent = new BundleContents(itemsList);
        itemStack.set(DataComponents.BUNDLE_CONTENTS, bundleContentsComponent);

        return itemStack;
    }
    public static ItemStack getItemPack() {
        ItemStack itemStack = getBasePack(" §l§6Item Pack");

        List<ItemStack> itemsList = new ArrayList<>();
        itemsList.add(TCG_Items.getRandomCommonItem());
        itemsList.add(TCG_Items.getRandomCommonItem());
        itemsList.add(TCG_Items.getRandomCommonItem());
        itemsList.add(TCG_Items.getRandomCommonItem());
        itemsList.add(TCG_Items.getRandomRareItem());
        BundleContents bundleContentsComponent = new BundleContents(itemsList);
        itemStack.set(DataComponents.BUNDLE_CONTENTS, bundleContentsComponent);

        return itemStack;
    }
    public static ItemStack getStarterDeck() {
        ItemStack itemStack = getBasePack(" §6");
        Component customName = Component.literal("Hermitcraft ").withStyle(style -> style.withColor(ChatFormatting.DARK_GRAY).withBold(true).withItalic(false))
                .append(Component.literal("TC").withStyle(style -> style.withColor(ChatFormatting.DARK_AQUA).withBold(true).withItalic(false)))
                .append(Component.literal("G ").withStyle(style -> style.withColor(0x80C71F).withBold(true).withItalic(false)))
                .append(Component.literal("§lStarter Deck").withStyle(style -> style.withColor(0xB02E26).withBold(true).withItalic(false)));
        itemStack.set(DataComponents.CUSTOM_NAME, Component.translationArg(customName));

        List<ItemStack> itemsList = new ArrayList<>();
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

        if (randomRares==0 || randomRares2==0) itemsList.add(TCG_Items.getRandomTypeRareHermit(type1, true));
        else itemsList.add(TCG_Items.getRandomTypeCommonHermit(type1, true));
        if (randomRares==1 || randomRares2==1) itemsList.add(TCG_Items.getRandomTypeRareHermit(type1, true));
        else itemsList.add(TCG_Items.getRandomTypeCommonHermit(type1, true));
        if (randomRares==2 || randomRares2==2) itemsList.add(TCG_Items.getRandomTypeRareHermit(type1, true));
        else itemsList.add(TCG_Items.getRandomTypeCommonHermit(type1, true));
        if (randomRares==3 || randomRares2==3) itemsList.add(TCG_Items.getRandomTypeRareHermit(type1, true));
        else itemsList.add(TCG_Items.getRandomTypeCommonHermit(type1, true));

        if (randomRares==4 || randomRares2==4) itemsList.add(TCG_Items.getRandomTypeRareHermit(type2, true));
        else itemsList.add(TCG_Items.getRandomTypeCommonHermit(type2, true));
        if (randomRares==5 || randomRares2==5) itemsList.add(TCG_Items.getRandomTypeRareHermit(type2, true));
        else itemsList.add(TCG_Items.getRandomTypeCommonHermit(type2, true));

        if (randomRares==6 || randomRares2==6) itemsList.add(TCG_Items.getRandomTypeRareHermit(type3, true));
        else itemsList.add(TCG_Items.getRandomTypeCommonHermit(type3, true));
        if (randomRares==7 || randomRares2==7) itemsList.add(TCG_Items.getRandomTypeRareHermit(type3, true));
        else itemsList.add(TCG_Items.getRandomTypeCommonHermit(type3, true));


        ItemStack item1 = TCG_Items.getRandomTypeCommonItem(type1);
        ItemStack item2 = TCG_Items.getRandomTypeCommonItem(type2);
        ItemStack item3 = TCG_Items.getRandomTypeCommonItem(type3);
        item1.setCount(13);
        item2.setCount(7);
        item3.setCount(7);
        itemsList.add(item1);
        itemsList.add(item2);
        itemsList.add(item3);
        for (int i = 0; i < 6; i++) {
            itemsList.add(TCG_Items.getRandomCommonEffect());
        }
        itemsList.add(TCG_Items.getRandomRareEffect());



        BundleContents bundleContentsComponent = new BundleContents(itemsList);
        itemStack.set(DataComponents.BUNDLE_CONTENTS, bundleContentsComponent);

        return itemStack;
    }
}
