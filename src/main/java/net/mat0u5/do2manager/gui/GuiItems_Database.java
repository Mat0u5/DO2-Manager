package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.world.DO2Run;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

public class GuiItems_Database {
    private static ItemStack createGuiItem(ItemStack itemStack, String tag, String displayName) {
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putString("GUI", "DatabaseGUI");
        nbt.putString("GUI_ITEM", tag);
        itemStack.setCustomName(Text.literal(displayName));
        return itemStack;
    }

    public static ItemStack page(boolean isNextPage) {
        ItemStack itemStack = new ItemStack(Items.ARROW, 1);
        return createGuiItem(itemStack, (isNextPage?"next":"previous")+"_page", (isNextPage?"Next":"Previous")+" Page");
    }

    public static ItemStack searchItem() {
        return createGuiItem(new ItemStack(Items.OAK_SIGN, 1), "search_item", "Search");
    }

    public static ItemStack run(DO2Run run) {
        if (run == null) return createGuiItem(new ItemStack(Items.BARRIER, 1), "run", "Run Is Null.");
        ItemStack itemStack;
        if (String.join("",run.finishers).isEmpty()) itemStack = new ItemStack(Items.RED_WOOL);
        else itemStack = new ItemStack(Items.GREEN_WOOL);
        if (run.run_type.equalsIgnoreCase("testing")) itemStack = new ItemStack(Items.BEDROCK);

        return createGuiItem(itemStack, "run", "Run #" + run.run_number);
    }
    public static ItemStack filler() {
        return createGuiItem(new ItemStack(Items.BLACK_STAINED_GLASS_PANE, 1), "filler", "");
    }
    public static ItemStack fillerLight() {
        return createGuiItem(new ItemStack(Items.LIGHT_GRAY_STAINED_GLASS_PANE, 1), "filler", "");
    }
    public static ItemStack filterSuccess() {
        return createGuiItem(new ItemStack(Items.BLACK_STAINED_GLASS_PANE, 1), "filler", "");
    }
}
