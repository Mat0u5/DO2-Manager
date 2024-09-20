package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.do2manager.utils.OtherUtils.roundToNPlaces;

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
        return createGuiItem(itemStack, (isNextPage?"next":"previous")+"_page", "§a"+(isNextPage?"Next":"Previous")+" Page", List.of(Text.of("§7(" + currentPage + "/" + totalPages + ")"), Text.of(""), Text.of("§8Right-Click to skip!"), Text.of("§eClick to turn page!")));
    }
    public static ItemStack pageCustom(boolean isNextPage, int currentPage, int totalPages, String custom_inv, int run_number) {
        ItemStack itemStack = new ItemStack(Items.ARROW, 1);
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("run_number", run_number);
        nbt.putString("custom_list_inv", custom_inv);

        return createGuiItem(itemStack, (isNextPage?"next":"previous")+"_page_custom_list", "§a"+(isNextPage?"Next":"Previous")+" Page", List.of(Text.of("§7(" + currentPage + "/" + totalPages + ")"), Text.of(""), Text.of("§eClick to turn page!")));
    }
    public static ItemStack searchItem() {
        return createGuiItem(new ItemStack(Items.OAK_SIGN, 1), "search_item", "Search");
    }
    public static ItemStack run(DO2Run run, boolean showRunsAsHeads, List<String> filter_player_uuid) {
        if (run == null) return createGuiItem(new ItemStack(Items.BARRIER, 1), "run", "§4Run Is Null.");
        ItemStack itemStack;
        List<Text> lore = new ArrayList<>();
        lore.add(Text.of(""));
        boolean successfulRun = run.getSuccessAdvanced(filter_player_uuid);
        if (successfulRun) {
            if (run.run_type.equalsIgnoreCase("phase")) itemStack = new ItemStack(Items.GREEN_STAINED_GLASS);
            else itemStack = new ItemStack(Items.GREEN_WOOL);
        }
        else {
            if (run.run_type.equalsIgnoreCase("phase")) itemStack = new ItemStack(Items.RED_STAINED_GLASS);
            else itemStack = new ItemStack(Items.RED_WOOL);
        }
        if (showRunsAsHeads) {
            itemStack = run.getRunnerSkull();
            if (successfulRun) itemStack.setCount(2);
        }

        lore.add(Text.of("§7Runners: §3" + run.getRunnersName()));
        lore.add(Text.of("§7Difficulty: " +run.getFormattedDifficulty()));
        lore.add(Text.of("§7Level: " +run.getFormattedLevel()));
        lore.add(Text.of("§7Run Type: "+((run.run_type.equalsIgnoreCase("phase")?"§b":(run.run_type.equalsIgnoreCase("casual")?"§e":"§d")) +run.getRunType())));
        lore.add(Text.of(""));
        lore.add(Text.of("§7Run Length: §6" + OtherUtils.convertTicksToClockTime(run.run_length,true)));
        if (run.date!=null) lore.add(Text.of("§7Date & Time: §f"+run.getFormattedDate()));
        if (run.embers_counted > 0) lore.add(Text.of("§7Embers Counted: §3" + run.embers_counted));
        if (run.crowns_counted > 0) lore.add(Text.of("§7Crowns Counted: §6" + run.crowns_counted));

        if (run.run_type.equalsIgnoreCase("testing")) itemStack = new ItemStack(Items.BEDROCK);

        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("run_number", run.run_number);
        return createGuiItem(itemStack, "run", (successfulRun?"§a":"§c")+"Run #" + run.run_number,lore);
    }
    public static ItemStack sort_by(String sort_by, boolean sort_by_descending) {
        ItemStack itemStack = new ItemStack(Items.HOPPER, 1);
        return createGuiItem(itemStack, "sort_by", "§aSort By §7("+(sort_by_descending?"§c↓":"§a↑")+"§7)",
            List.of(Text.of(""),
                    Text.of((sort_by.equalsIgnoreCase("run_number")?"§e▶ ":"  §7")+"Run Number"),
                    Text.of((sort_by.equalsIgnoreCase("run_length")?"§9▶ ":"  §7")+"Run Length"),
                    Text.of((sort_by.equalsIgnoreCase("difficulty")?"§c▶ ":"  §7")+"Difficulty"),
                    Text.of((sort_by.equalsIgnoreCase("embers")?"§3▶ ":"  §7")+"Embers Counted"),
                    Text.of((sort_by.equalsIgnoreCase("crowns")?"§6▶ ":"  §7")+"Crowns Counted"),
                    Text.of(""),
                    Text.of("§8Right-Click to reverse results!"),
                    Text.of("§eClick cycle through!")
            ));
    }
    public static ItemStack filler(ItemStack itemStack) {
        return createGuiItem(itemStack, "filler", "");
    }
    public static ItemStack filler() {
        return filler(new ItemStack(Items.BLACK_STAINED_GLASS_PANE, 1));
    }
    public static ItemStack fillerLight() {
        return filler(new ItemStack(Items.LIGHT_GRAY_STAINED_GLASS_PANE, 1));
    }
    public static ItemStack fillerGreen() {
        return filler(new ItemStack(Items.LIME_STAINED_GLASS_PANE, 1));
    }
    public static ItemStack fillerRed() {
        return filler(new ItemStack(Items.RED_STAINED_GLASS_PANE, 1));
    }
    public static ItemStack filterSuccess(int filter_success) {
        return createGuiItem(new ItemStack((filter_success==0?Items.WHITE_WOOL:(filter_success==1)?Items.GREEN_WOOL:Items.RED_WOOL), 1), "filter_success", "§aSuccess Filter", List.of(Text.of(""), Text.of((filter_success==0?"§8▶ ":"  ")+"§8No filter"), Text.of((filter_success==1?"§2▶ ":"  §7")+"Successful"), Text.of((filter_success==2?"§c▶ ":"  §7")+"Unsuccessful"), Text.of(""), Text.of("§eClick cycle through!")));
    }
    public static ItemStack filterDifficulty(int filter_difficulty, int filter_level) {
        return createGuiItem(new ItemStack(Items.COMPASS, 1), "filter_difficulty", "§aDifficulty Filter",
                List.of(Text.of(""),
                Text.literal("§fDifficulty:").append(Text.of("§f        Level:")),
                Text.literal(filter_difficulty==0?"§8▶ No filter ":"  §8No filter  ").append(Text.of("    "+(filter_level==0?"§8▶ ":"  §8")+"§8No filter")),
                Text.literal(filter_difficulty==1?"§a▶ Easy ":"  §7Easy  ").append(Text.of("         "+(filter_level==1?"§a▶ ":"  §7")+"Level 1")),
                Text.literal(filter_difficulty==2?"§e▶ Medium ":"  §7Medium  ").append(Text.of("       "+(filter_level==2?"§6▶ ":"  §7")+"Level 2")),
                Text.literal(filter_difficulty==3?"§6▶ Hard ":"  §7Hard  ").append(Text.of("         "+(filter_level==3?"§4▶ ":"  §7")+"Level 3")),
                Text.literal(filter_difficulty==4?"§4▶ Deadly ":"  §7Deadly  ").append(Text.of("       "+(filter_level==4?"§3▶ ":"  §7")+"Level 4")),
                Text.literal(filter_difficulty==5?"§3▶ Deepfrost ":"  §7Deepfrost  "),
                Text.of(""),
                Text.of("§8Right-Click cycle through levels!"),
                Text.of("§eclick cycle through difficulties!")));
        // is longer
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

        return createGuiItem(itemStack, "filter_player", "§aPlayer Filter", List.of(Text.of(""), Text.of((filter_player.isEmpty()?"§8▶ No filter":"§f▶ "+playerList)), Text.of(""),Text.of("§8Right-Click to clear!"),Text.of("§eClick to filter players!")));
    }
    public static ItemStack toggleHeads(boolean showRunsAsHeads) {
        ItemStack itemStack = new ItemStack(Items.SKELETON_SKULL, 1);
        if (!showRunsAsHeads) itemStack = new ItemStack(Items.LIME_DYE, 1);

        return createGuiItem(itemStack, "toggle_heads", "§aRun Item Display", List.of(Text.of(""), Text.of("§7Switches between previewing runs with player heads"), Text.of("§7or with wool (colored based on run outcome)"), Text.of(""), Text.of("§eClick to switch!")));
    }
    public static ItemStack backToMain() {
        return createGuiItem(new ItemStack(Items.ARROW, 1), "back_to_main", "§aGo Back", List.of(Text.of(""), Text.of("§eClick to return!")));
    }
    public static ItemStack backToRunNum(int run_number) {
        ItemStack itemStack = new ItemStack(Items.ARROW, 1);
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("run_number", run_number);

        return createGuiItem(itemStack, "back_to_run", "§aGo Back", List.of(Text.of(""), Text.of("§eClick to return!")));
    }
    public static ItemStack resetAll() {
        ItemStack itemStack = new ItemStack(Items.BARRIER, 1);

        return createGuiItem(itemStack, "reset_all", "§aReset All Filters", List.of(Text.of(""), Text.of("§eClick to reset!")));
    }
    public static ItemStack runHeads(DO2Run run) {
        ItemStack itemStack = run.getRunnerSkull();

        List<Text> lore = new ArrayList<>();
        lore.add(Text.of(""));

        lore.add(Text.of("§7Runners: §3" + run.getRunnersName()));
        lore.add(Text.of("§7Finishers: §3" + run.getFinishersName()));
        lore.add(Text.of(""));
        lore.add(Text.of("§7→ Run Successful: "+(run.getSuccess()?"§aYes":"§cNo")));
        lore.add(Text.of(""));
        lore.add(Text.of("§7Difficulty: " +run.getFormattedDifficulty()));
        lore.add(Text.of("§7Level: " +run.getFormattedLevel()));
        lore.add(Text.of("§7Run Type: "+((run.run_type.equalsIgnoreCase("phase")?"§b":(run.run_type.equalsIgnoreCase("casual")?"§e":"§d")) +run.getRunType())));
        if (run.date!=null) lore.add(Text.of("§7Date & Time: §f"+run.getFormattedDate()));
        return createGuiItem(itemStack, "runners", (run.getSuccess()?"§a":"§c")+"Run #" + run.run_number, lore);
    }
    public static ItemStack runClock(DO2Run run) {
        ItemStack itemStack = new ItemStack(Items.CLOCK, 1);

        List<Text> lore = new ArrayList<>();
        lore.add(Text.of(""));

        lore.add(Text.of("§7Run Length: §6" + OtherUtils.convertTicksToClockTime(run.run_length,true)));
        lore.add(Text.of(""));
        if (run.timestamp_lvl2_entry > 0) lore.add(Text.of("§7Lvl2 Entry: §6" + OtherUtils.convertTicksToClockTime(run.timestamp_lvl2_entry,true)));
        if (run.timestamp_lvl3_entry > 0) lore.add(Text.of("§7Lvl3 Entry: §6" + OtherUtils.convertTicksToClockTime(run.timestamp_lvl3_entry,true)));
        if (run.timestamp_lvl4_entry > 0) lore.add(Text.of("§7Lvl4 Entry: §6" + OtherUtils.convertTicksToClockTime(run.timestamp_lvl4_entry,true)));
        if (run.timestamp_artifact > 0) lore.add(Text.of("§3Artifact obtained: §6" + OtherUtils.convertTicksToClockTime(run.timestamp_artifact,true)));
        if (run.timestamp_lvl4_exit > 0) lore.add(Text.of("§7Lvl4 Exit: §6" + OtherUtils.convertTicksToClockTime(run.timestamp_lvl4_exit,true)));
        if (run.timestamp_lvl3_exit > 0) lore.add(Text.of("§7Lvl3 Exit: §6" + OtherUtils.convertTicksToClockTime(run.timestamp_lvl3_exit,true)));
        if (run.timestamp_lvl2_exit > 0) lore.add(Text.of("§7Lvl2 Exit: §6" + OtherUtils.convertTicksToClockTime(run.timestamp_lvl2_exit,true)));
        return createGuiItem(itemStack, "timestamps", "§aTimestamps", lore);
    }
    public static ItemStack runDeath(DO2Run run) {
        ItemStack itemStack = new ItemStack(Items.CARVED_PUMPKIN, 1);
        List<Text> lore = new ArrayList<>();
        String itemName = "§aDeath Info";
        if (!run.getSuccess()) {
            NbtCompound nbt = itemStack.getOrCreateNbt();
            nbt.putInt("CustomModelData", 96);

            lore.add(Text.of(""));

            lore.add(Text.of("§7Death Message: §c" + run.death_message));
            lore.add(Text.of("§7Death Pos: §c" + run.death_pos));
            return createGuiItem(itemStack, "death_info", itemName, lore);
        }
        //Run is successful

        return getEmbers(run);
    }
    public static ItemStack getEmbers(DO2Run run) {
        ItemStack itemStack = new ItemStack(Items.IRON_NUGGET, 1);
        List<Text> lore = new ArrayList<>();
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("CustomModelData", 3);
        itemStack.setCount(Math.max(1, Math.min(64, run.embers_counted)));
        String itemName = "§aFrost Embers";
        lore.add(Text.of(""));

        lore.add(Text.of("§7Embers Counted: §3" + run.embers_counted));
        return createGuiItem(itemStack, "death_info", itemName, lore);
    }
    public static ItemStack getCrowns(DO2Run run) {
        if (!run.getSuccess() || run.crowns_counted == 0) {
            return run.getSuccess()?GuiItems_Database.fillerGreen():GuiItems_Database.fillerRed();
        }
        ItemStack itemStack = new ItemStack(Items.IRON_NUGGET, 1);
        List<Text> lore = new ArrayList<>();
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("CustomModelData", 2);
        itemStack.setCount(Math.max(1, Math.min(64, run.crowns_counted)));
        String itemName = "§aCrowns";
        lore.add(Text.of(""));

        lore.add(Text.of("§7Crowns Counted: §6" + run.crowns_counted));
        return createGuiItem(itemStack, "crowns_info", itemName, lore);
    }
    public static ItemStack runCardPlays(DO2Run run) {
        ItemStack itemStack = new ItemStack(Items.IRON_NUGGET, 1);
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("CustomModelData", 102);
        nbt.putInt("run_number", run.run_number);
        nbt.putString("custom_list_inv", "card_plays");
        List<Text> lore = new ArrayList<>();
        itemStack.setCount(Math.max(1, Math.min(64,run.card_plays.size())));

        lore.add(Text.of(""));
        lore.add(Text.of("§eClick view the cards that played in this run!"));

        return createGuiItem(itemStack, "card_plays", "§7Cards Played", lore);
    }
    public static ItemStack runInventory(DO2Run run) {
        ItemStack itemStack = new ItemStack(Items.CHEST, 1);
        List<Text> lore = new ArrayList<>();
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("run_number", run.run_number);
        nbt.putString("custom_list_inv", "inventory_save");

        lore.add(Text.of(""));
        lore.add(Text.of("§eClick view the last inventory save in this run!"));

        return createGuiItem(itemStack, "inventory_save", "§7Inventory Save", lore);
    }
    public static ItemStack runItemsBought(DO2Run run) {
        ItemStack itemStack = new ItemStack(Items.GOLD_INGOT, 1);
        List<Text> lore = new ArrayList<>();
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("run_number", run.run_number);
        nbt.putString("custom_list_inv", "items_bought");


        lore.add(Text.of(""));
        lore.add(Text.of("§eClick view the bought items in this run!"));

        return createGuiItem(itemStack, "items_bought", "§7Bought Items", lore);
    }
    public static ItemStack itemInfo(List<DO2RunAbridged> runsSearchAbridged, List<String> filter_player_uuid) {

        int runsNum = runsSearchAbridged.size();
        int successfulRuns = 0;
        int unsuccessfulRuns = 0;
        int totalEmbers = 0;
        int totalCrowns = 0;
        int totalPlayTime = 0;
        int biggestWinStreak = 0;
        int biggestLossStreak = 0;
        int biggestWinStreakPos = 0;
        int biggestLossStreakPos = 0;

        int currentWinStreak = 0;
        int currentLossStreak = 0;
        for (DO2RunAbridged run : runsSearchAbridged) {
            if (run.getSuccessAdvanced(filter_player_uuid)) {
                successfulRuns++;
                totalEmbers += run.embers_counted;
                totalCrowns += run.crowns_counted;
                currentLossStreak = 0;
                currentWinStreak++;
                if (currentWinStreak > biggestWinStreak) {
                    biggestWinStreak = currentWinStreak;
                    biggestWinStreakPos = run.run_number;
                }
            }
            else {
                unsuccessfulRuns++;
                currentWinStreak = 0;
                currentLossStreak++;
                if (currentLossStreak > biggestLossStreak) {
                    biggestLossStreak = currentLossStreak;
                    biggestLossStreakPos = run.run_number;
                }
            }
            totalPlayTime += run.run_length;
        }

        double winPercentage = roundToNPlaces(((double)successfulRuns*100d)/runsNum,3);
        double averageEmbers = roundToNPlaces((double)totalEmbers/successfulRuns,2);
        double averageCrowns = roundToNPlaces((double)totalCrowns/successfulRuns,2);
        double averageRunLength = roundToNPlaces((double)totalPlayTime/runsNum,0);

        ItemStack itemStack = new ItemStack(Items.IRON_NUGGET, 1);
        List<Text> lore = new ArrayList<>();
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putInt("CustomModelData", 47);
        String itemName = "§aRuns Info";
        lore.add(Text.of(""));
        lore.add(Text.of("§7Runs: §b" + runsNum));
        lore.add(Text.of("§7Successful Runs: §a" + successfulRuns));
        lore.add(Text.of("§7Unsuccessful Runs: §c" + unsuccessfulRuns));
        lore.add(Text.of("§7 -> Win Percentage: " + winPercentage+"%"));
        lore.add(Text.of("§7Biggest Win Streak: §a" + biggestWinStreak  + " §8[@ #"+biggestWinStreakPos+"]"));
        lore.add(Text.of("§7Biggest Loss Streak: §c" + biggestLossStreak + " §8[@ #"+biggestLossStreakPos+"]"));
        lore.add(Text.of(""));
        lore.add(Text.of("§7Total Play Time: §6" + OtherUtils.convertTicksToClockTime(totalPlayTime,false)));
        lore.add(Text.of("§7Total Embers: §3" + totalEmbers));
        lore.add(Text.of("§7Total Crowns: §6" + totalCrowns));
        lore.add(Text.of("§7Average Run Time: §6" + OtherUtils.convertTicksToClockTime((long) averageRunLength,false)));
        lore.add(Text.of("§7Average Embers: §3" + averageEmbers));
        lore.add(Text.of("§7Average Crowns: §6" + averageCrowns));

        return createGuiItem(itemStack, "runs_info", itemName, lore);
    }
    public static ItemStack playerHeadChoice(String name) {
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD, 1);
        NbtCompound nbt = itemStack.getOrCreateNbt();
        nbt.putString("SkullOwner", name);
        return createGuiItem(itemStack, "player_choice", "§7"+name, List.of(Text.of(""),Text.of("§eClick to choose this player!")));
    }
}
