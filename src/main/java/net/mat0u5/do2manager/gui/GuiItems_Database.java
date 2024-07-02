package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class GuiItems_Database {
    private static ItemStack createGuiItem(ItemStack itemStack, String tag, String displayName) {
        return createGuiItem(itemStack, tag, displayName, null);
    }
    private static ItemStack createGuiItem(ItemStack itemStack, String tag, String displayName, List<Text> lore) {
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putString("GUI", "DatabaseGUI");
        nbt.putString("GUI_ITEM", tag);
        itemStack.setCustomName(Text.of(displayName));

        if (lore != null) ItemManager.addLoreToItemStack(itemStack, lore);
        return itemStack;
    }

    public static ItemStack page(boolean isNextPage, int currentPage, int totalPages) {
        ItemStack itemStack = new ItemStack(Items.ARROW, 1);
        return createGuiItem(itemStack, (isNextPage?"next":"previous")+"_page", "§a"+(isNextPage?"Next":"Previous")+" Page", List.of(Text.of("§7(" + currentPage + "/" + totalPages + ")"), Text.of(""), Text.of("§eClick to turn page!")));
    }

    public static ItemStack searchItem() {
        return createGuiItem(new ItemStack(Items.OAK_SIGN, 1), "search_item", "Search");
    }

    public static ItemStack run(DO2Run run) {
        if (run == null) return createGuiItem(new ItemStack(Items.BARRIER, 1), "run", "§4Run Is Null.");
        ItemStack itemStack;
        List<Text> lore = new ArrayList<>();
        lore.add(Text.of(""));
        if (run.getSuccess()) {
            if (run.run_type.equalsIgnoreCase("phase")) itemStack = new ItemStack(Items.GREEN_STAINED_GLASS);
            else itemStack = new ItemStack(Items.GREEN_WOOL);
        }
        else {
            if (run.run_type.equalsIgnoreCase("phase")) itemStack = new ItemStack(Items.RED_STAINED_GLASS);
            else itemStack = new ItemStack(Items.RED_WOOL);
        }
        lore.add(Text.of("§7Runners: §3" + run.getRunnersName()));
        lore.add(Text.of("§7Difficulty: " +(run.difficulty==5?"§3Deepfrost":run.difficulty==4?"§4Deadly":run.difficulty==3?"§6Hard":run.difficulty==2?"§eMedium":run.difficulty==1?"§aEasy":"§dnull")));
        lore.add(Text.of("§7Run Type: "+((run.run_type.equalsIgnoreCase("phase")?"§b":(run.run_type.equalsIgnoreCase("casual")?"§e":"§d")) +run.run_type)));

        lore.add(Text.of("§7Run Length: §6" + OtherUtils.convertTicksToClockTime(run.run_length)));
        if (run.embers_counted > 0) lore.add(Text.of("§7Embers Counted: §3" + run.embers_counted));

        if (run.run_type.equalsIgnoreCase("testing")) itemStack = new ItemStack(Items.BEDROCK);
        return createGuiItem(itemStack, "run", (run.getSuccess()?"§a":"§c")+"Run #" + run.run_number,lore);
    }
    public static ItemStack filler() {
        return createGuiItem(new ItemStack(Items.BLACK_STAINED_GLASS_PANE, 1), "filler", "");
    }
    public static ItemStack fillerLight() {
        return createGuiItem(new ItemStack(Items.LIGHT_GRAY_STAINED_GLASS_PANE, 1), "filler", "");
    }
    public static ItemStack filterSuccess(int filter_success) {
        return createGuiItem(new ItemStack((filter_success==0?Items.WHITE_WOOL:(filter_success==1)?Items.GREEN_WOOL:Items.RED_WOOL), 1), "filter_success", "§aSuccess Filter", List.of(Text.of(""), Text.of((filter_success==0?"§8▶ ":"  ")+"§8No filter"), Text.of((filter_success==1?"§2▶ ":"  §7")+"Successful"), Text.of((filter_success==2?"§c▶ ":"  §7")+"Unsuccessful"), Text.of(""), Text.of("§eClick cycle through!")));
    }
    public static ItemStack filterDifficulty(int filter_difficulty) {
        return createGuiItem(new ItemStack(Items.COMPASS, 1), "filter_difficulty", "§aDifficulty Filter", List.of(Text.of(""), Text.of((filter_difficulty==0?"§8▶ ":"  ")+"§8No filter"), Text.of((filter_difficulty==1?"§a▶ ":"  §7")+"Easy"), Text.of((filter_difficulty==2?"§e▶ ":"  §7")+"Normal"), Text.of((filter_difficulty==3?"§6▶ ":"  §7")+"Hard"), Text.of((filter_difficulty==4?"§4▶ ":"  §7")+"Deadly"), Text.of((filter_difficulty==5?"§3▶ ":"  §7")+"Deepfrost"), Text.of(""), Text.of("§eClick cycle through!")));
    }
    public static ItemStack filterRunType(int filter_run_type) {
        ItemStack itemStack = new ItemStack(Items.IRON_NUGGET, 1);

        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("CustomModelData", 6); // Set the custom model data value
        return createGuiItem(itemStack, "filter_run_type", "§aRun Type Filter", List.of(Text.of(""), Text.of((filter_run_type==0?"§8▶ ":"  ")+"§8No filter"), Text.of((filter_run_type==1?"§e▶ ":"  §7")+"Casual"), Text.of((filter_run_type==2?"§b▶ ":"  §7")+"Phase"), Text.of(""), Text.of("§eClick cycle through!")));
    }
    public static ItemStack filterPlayer(List<String> filter_player) {
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD, 1);
        String playerList = String.join(", ",filter_player);
        if (!playerList.isEmpty()) {
            if (!playerList.contains(", ")) {
                String playerName = filter_player.get(0);
                NbtCompound nbt = itemStack.getOrCreateNbt();
                nbt.putString("SkullOwner", playerName);
            }
            else {
                itemStack = new ItemStack(Items.CARVED_PUMPKIN, 1);
                NbtCompound nbt = itemStack.getOrCreateNbt();
                nbt.putInt("CustomModelData", 46);
            }
        }

        return createGuiItem(itemStack, "filter_player", "§aPlayer Filter", List.of(Text.of(""), Text.of((filter_player.isEmpty()?"§8▶ No filter":"§f▶ "+playerList)), Text.of("§eClick to filter players!")));
    }
}
