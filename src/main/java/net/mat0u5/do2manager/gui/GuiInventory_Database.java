package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static net.mat0u5.do2manager.Main.allAbridgedRuns;

public class GuiInventory_Database extends GuiPlayerSpecific {
    private final int INVENTORY_SIZE = 54;
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    private AtomicReference<CompletableFuture<Void>> currentFuture = new AtomicReference<>();
    private AtomicReference<PreparedStatement> currentStatement = new AtomicReference<>();


    public int openRunInventory(ServerPlayer player) {
        if (Main.openGuis.containsKey(player)) {
            GuiPlayerSpecific openGui = Main.openGuis.get(player);
            if (openGui.invId.equalsIgnoreCase("runs")) {
                openGui.guiDatabase.openRunInventoryNoUpdate(player);
                return 1;
            }
        }
        inventory = new SimpleContainer(INVENTORY_SIZE);
        invId = "runs";
        // Populate the inventory with run data
        if (!Main.reloadedRuns) {
            Main.reloadAllAbridgedRunsAsync().thenRun(() -> {
                openRunInventoryAfterLoad(player);
            });
        } else {
            openRunInventoryAfterLoad(player);
        }
        return 1;
    }

    public void openRunInventoryAfterLoad(ServerPlayer player) {
        runsSearchAbridged = List.copyOf(allAbridgedRuns);
        populateRunInventory();

        openRunInventoryNoUpdate(player);
        guiDatabase = this;
        current_page = 1;
        current_page_custom_list = 1;
        Main.openGuis.put(player, this);
    }

    public void openRunInventoryNoUpdate(ServerPlayer player) {
        player.openMenu(new SimpleMenuProvider((syncId, inv, p) -> {
            return new ChestMenu(MenuType.GENERIC_9x6, syncId, inv, inventory, INVENTORY_SIZE / 9);
        }, Component.nullToEmpty("Run History")));
        invOpen = true;
    }

    public void populateRunInventory() {
        fillWithFillerItems(GuiItems_Database.filler(), List.of(4, 45, 46, 47, 48, 50, 51, 52));
        addRunItems();
        addFiltersNStuff();
    }

    public void fillWithFillerItems(ItemStack itemStack, List<Integer> skipSlots) {
        for (int i = 0; i < 54; i++) {
            if (skipSlots.contains(i)) continue;
            setIsNotMatching(i, itemStack);
        }
    }

    public void addFiltersNStuff() {
        int totalPages = (int) Math.ceil((double) runsSearchAbridged.size() / 21);

        setOrReplaceNbt(4, GuiItems_Database.itemInfo(runsSearchAbridged, filter_player_uuid));
        setOrReplaceNbt(45, GuiItems_Database.toggleHeads(showRunsAsHeads));
        if (current_page != 1)
            setOrReplaceNbt(46, GuiItems_Database.page(false, current_page, totalPages)); // Previous page
        else setIsNotMatching(46, GuiItems_Database.filler());
        setOrReplaceNbt(47, GuiItems_Database.filterPlayer(filter_player));
        setOrReplaceNbt(48, GuiItems_Database.filterDifficulty(filter_difficulty, filter_level));
        setOrReplaceNbt(49, GuiItems_Database.resetAll());
        setOrReplaceNbt(50, GuiItems_Database.filterSuccess(filter_success));
        setOrReplaceNbt(51, GuiItems_Database.filterRunType(filter_run_type));
        if (current_page < totalPages)
            setOrReplaceNbt(52, GuiItems_Database.page(true, current_page, totalPages)); // Next page
        else setIsNotMatching(52, GuiItems_Database.filler());
        setOrReplaceNbt(53, GuiItems_Database.sort_by(sort_by, sort_by_descending));
    }

    public void setIsNotMatching(int slot, ItemStack itemStack) {
        if (!inventory.getItem(slot).getItem().equals(itemStack.getItem())) {
            inventory.setItem(slot, itemStack);
        }
    }

    public void setOrReplaceNbt(int slot, ItemStack itemStack) {
        if (!ItemManager.getItemId(inventory.getItem(slot)).equalsIgnoreCase(ItemManager.getItemId(itemStack))) {
            inventory.setItem(slot, itemStack);
        } else {
            inventory.getItem(slot).applyComponents(itemStack.getComponents());
        }
    }

    public void addRunItems() {
        if (runsSearch.isEmpty() && !runsSearchAbridged.isEmpty()) {
            updateSearch();
        }
        loadFullRuns().thenRun(() -> {
            int runIndex = 0;
            for (int y = 0; y < 6; y++) {
                for (int x = 0; x < 9; x++) {
                    int pos = x + y * 9;
                    if (!(x > 0 && x < 8 && y > 0 && y < 4)) continue;
                    if (runsSearch.size() <= runIndex) {
                        inventory.setItem(pos, GuiItems_Database.fillerLight());
                        continue;
                    }
                    inventory.setItem(pos, GuiItems_Database.run(runsSearch.get(runIndex), showRunsAsHeads, filter_player_uuid));
                    runIndex++;
                }
            }

        });
    }

    public void updateSearch() {
        runsSearchAbridged = new ArrayList<>();
        runsSearch = new ArrayList<>();
        for (DO2RunAbridged run : allAbridgedRuns) {
            if (filter_success == 1 && !run.getSuccessAdvanced(filter_player_uuid)) continue;
            if (filter_success == 2 && run.getSuccessAdvanced(filter_player_uuid)) continue;
            if (filter_difficulty == 1 && run.difficulty != 1) continue;
            if (filter_difficulty == 2 && run.difficulty != 2) continue;
            if (filter_difficulty == 3 && run.difficulty != 3) continue;
            if (filter_difficulty == 4 && run.difficulty != 4) continue;
            if (filter_difficulty == 5 && run.difficulty != 5) continue;
            if (filter_level == 1 && run.compass_level != 1) continue;
            if (filter_level == 2 && run.compass_level != 2) continue;
            if (filter_level == 3 && run.compass_level != 3) continue;
            if (filter_level == 4 && run.compass_level != 4) continue;
            if (filter_run_type == 1 && !run.run_type.equalsIgnoreCase("casual")) continue;
            if (filter_run_type == 2 && !run.run_type.equalsIgnoreCase("phase")) continue;
            if (filter_run_type == 3 && !run.run_type.equalsIgnoreCase("hardcore")) continue;
            if (!filter_player_uuid.isEmpty()) {
                List<String> remainingFilters = new ArrayList<>(List.copyOf(filter_player_uuid));
                remainingFilters.removeAll(run.runners);
                if (!String.join("", remainingFilters).isEmpty()) continue;
            }
            long timestampDate = run.timestampDate();
            if (filter_date_after != -1 && timestampDate != -1 && timestampDate < filter_date_after) continue;
            if (filter_date_before != -1 && timestampDate != -1 && timestampDate > filter_date_before) continue;

            runsSearchAbridged.add(run);
        }
        sortRuns();
        int totalPages = (int) Math.ceil((double) runsSearchAbridged.size() / 21);
        if (current_page > totalPages) {
            current_page = totalPages;
        }
    }

    public CompletableFuture<Void> loadFullRuns() {
        // Cancel the previous future if it exists
        CompletableFuture<Void> previousFuture = currentFuture.getAndSet(null);
        if (previousFuture != null && !previousFuture.isDone()) {
            previousFuture.cancel(true);
        }
        // Cancel the previous statement if it exists
        PreparedStatement previousStatement = currentStatement.getAndSet(null);
        if (previousStatement != null) {
            try {
                previousStatement.cancel();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        // Create and run a new future
        CompletableFuture<Void> newFuture = CompletableFuture.runAsync(() -> {
            synchronized (GuiInventory_Database.class) {
                int showFrom = (current_page - 1) * 21;
                int showTo = showFrom + 21;
                List<DO2RunAbridged> getRuns = new ArrayList<>();
                for (int i = showFrom; i <= showTo; i++) {
                    if (runsSearchAbridged.size() <= i) continue;
                    getRuns.add(runsSearchAbridged.get(i));
                }

                // Fetch runs with optional cancellation support
                runsSearch = DatabaseManager.getRunsByAbridgedRuns(getRuns, currentStatement, false);
            }
        }, executor);

        // Update the reference with the new future
        currentFuture.set(newFuture);

        return newFuture;
    }
    public static void shutdownExecutor() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    public void sortRuns() {
        if (runsSearchAbridged == null) return;
        if (runsSearchAbridged.size() <= 1) return;

        runsSearchAbridged.sort(new Comparator<DO2RunAbridged>() {
            @Override
            public int compare(DO2RunAbridged run1, DO2RunAbridged run2) {
                if (sort_by.equalsIgnoreCase("run_number")) {
                    return Integer.compare(run2.getRunNum(), run1.getRunNum());
                } else if (sort_by.equalsIgnoreCase("run_length")) {
                    return Integer.compare(run2.run_length, run1.run_length);
                } else if (sort_by.equalsIgnoreCase("difficulty")) {
                    return Integer.compare(run2.difficulty, run1.difficulty);
                } else if (sort_by.equalsIgnoreCase("embers")) {
                    return Integer.compare(run2.embers_counted, run1.embers_counted);
                } else if (sort_by.equalsIgnoreCase("crowns")) {
                    return Integer.compare(run2.crowns_counted, run1.crowns_counted);
                }
                return Integer.compare(run2.getRunNum(), run1.getRunNum());
            }
        });
        if (!sort_by_descending) {
            Collections.reverse(runsSearchAbridged);
        }
    }

    public DO2Run getRunFromNum(int run_number) {
        for (DO2Run run : runsSearch) {
            if (run.run_number == run_number) return run;
        }
        return null;
    }

    public void detailedRunInventory(int run_number) {
        DO2Run run = getRunFromNum(run_number);
        if (run == null) return;

        fillWithFillerItems((run.getSuccess() ? GuiItems_Database.fillerGreen() : GuiItems_Database.fillerRed()), new ArrayList<>());
        inventory.setItem(49, GuiItems_Database.backToMain());
        setOrReplaceNbt(4, GuiItems_Database.runHeads(run));
        setOrReplaceNbt(12, GuiItems_Database.runClock(run));
        setOrReplaceNbt(13, GuiItems_Database.getCrowns(run));
        setOrReplaceNbt(14, GuiItems_Database.runDeath(run));
        setOrReplaceNbt(20, GuiItems_Database.runCardPlays(run));
        setOrReplaceNbt(22, GuiItems_Database.runInventory(run));
        setOrReplaceNbt(24, GuiItems_Database.runItemsBought(run));
        if (run.compass_item != null) setOrReplaceNbt(30, run.compass_item.copy());
        if (run.deck_item != null) setOrReplaceNbt(31, run.deck_item.copy());
        if (run.artifact_item != null) setOrReplaceNbt(32, run.artifact_item.copy());
    }

    public void customItemListInventory(String fillType, int run_number) {
        DO2Run run = getRunFromNum(run_number);
        if (run == null) return;


        fillWithFillerItems(GuiItems_Database.filler(), new ArrayList<>());
        inventory.setItem(49, GuiItems_Database.backToRunNum(run.run_number));
        List<ItemStack> items = new ArrayList<>();
        if (fillType.equalsIgnoreCase("card_plays")) items = List.copyOf(run.card_plays);
        if (fillType.equalsIgnoreCase("inventory_save")) items = List.copyOf(run.inventory_save);
        if (fillType.equalsIgnoreCase("items_bought")) items = List.copyOf(run.items_bought);

        int totalPages = (int) Math.ceil((double) items.size() / 21);

        if (current_page_custom_list != 1)
            setOrReplaceNbt(46, GuiItems_Database.pageCustom(false, current_page_custom_list, totalPages, fillType, run_number)); // Previous page
        if (current_page_custom_list < totalPages)
            setOrReplaceNbt(52, GuiItems_Database.pageCustom(true, current_page_custom_list, totalPages, fillType, run_number)); // Next page


        int runIndex = (current_page_custom_list - 1) * 21;
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 9; x++) {
                int pos = x + y * 9;
                if (!(x > 0 && x < 8 && y > 0 && y < 4)) continue;
                if (items.size() <= runIndex) {
                    inventory.setItem(pos, GuiItems_Database.fillerLight());
                    continue;
                }
                inventory.setItem(pos, items.get(runIndex).copy());
                runIndex++;
            }
        }
    }

    public void playerChoiceInventory(HashMap<String, Integer> options) {
        ArrayList<Integer> listValues = new ArrayList<>();
        ArrayList<String> sortedListKeys = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : options.entrySet()) {
            if (listValues.contains(entry.getValue())) continue;
            listValues.add(entry.getValue());
        }
        Collections.sort(listValues);
        for (int num : listValues.reversed()) {
            for (Entry<String, Integer> entry : options.entrySet()) {
                if (entry.getValue().equals(num)) {
                    sortedListKeys.add(entry.getKey());
                }
            }
        }


        fillWithFillerItems(GuiItems_Database.filler(), List.of());
        int runIndex = 0;
        for (int y = 0; y < 6; y++) {
            for (int x = 0; x < 9; x++) {
                int pos = x + y * 9;
                if (!(x > 0 && x < 8 && y > 0 && y < 5)) continue;
                if (sortedListKeys.size() <= runIndex) {
                    inventory.setItem(pos, GuiItems_Database.fillerLight());
                    continue;
                }
                String playerName = sortedListKeys.get(runIndex);
                int playerRuns = options.get(playerName);
                inventory.setItem(pos, GuiItems_Database.playerHeadChoice(playerName, playerRuns));
                runIndex++;
            }
        }
    }
}