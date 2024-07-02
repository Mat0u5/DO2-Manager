package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.world.DO2Run;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

import static net.mat0u5.do2manager.Main.allRuns;

public class GuiInventory_Database extends GuiPlayerSpecific {
    private final int INVENTORY_SIZE = 54;



    public int openRunInventory(ServerPlayerEntity player) {
        inventory = new SimpleInventory(INVENTORY_SIZE);
        invId = "runs";
        // Populate the inventory with run data
        runsSearch = List.copyOf(allRuns);
        populateRunInventory();

        openRunInventoryNoUpdate(player);
        guiDatabase = this;
        current_page = 1;
        Main.openGuis.put(player,this);
        return 1;
    }public int openRunInventoryNoUpdate(ServerPlayerEntity player) {
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> {
            return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, inv, inventory, INVENTORY_SIZE / 9);
        }, Text.of("Run History")));
        return 1;
    }

    public void populateRunInventory() {
        for (int i = 0; i < 54; i++) {
            setIsNotMatching(i, GuiItems_Database.filler());
        }

        // Set navigation buttons
        addRunItems();
        addFiltersNStuff();
    }
    public void addFiltersNStuff() {
        int totalPages = (int) Math.ceil(runsSearch.size()/21)+1;
        if (current_page != 1) setOrReplaceNbt(46, GuiItems_Database.page(false,current_page,totalPages)); // Previous page
        setOrReplaceNbt(47, GuiItems_Database.filterPlayer(filter_player));
        setOrReplaceNbt(48, GuiItems_Database.filterDifficulty(filter_difficulty));
        setOrReplaceNbt(50, GuiItems_Database.filterSuccess(filter_success));
        setOrReplaceNbt(51, GuiItems_Database.filterRunType(filter_run_type));
        if (current_page  < totalPages) setOrReplaceNbt(52, GuiItems_Database.page(true,current_page,totalPages)); // Next page
    }
    public void setIsNotMatching(int slot, ItemStack itemStack) {
        if (!inventory.getStack(slot).getItem().equals(itemStack.getItem())) {
            inventory.setStack(slot,itemStack);
        }
    }
    public void setOrReplaceNbt(int slot, ItemStack itemStack) {
        if (!inventory.getStack(slot).getItem().equals(itemStack.getItem())) {
            inventory.setStack(slot,itemStack);
        }
        else inventory.getStack(slot).setNbt(itemStack.getNbt());
    }
    public void addRunItems() {
        int runIndex = (current_page-1)*21;
        for (int y = 0; y < 6; y++) {
        for (int x = 0; x < 9; x++) {
            int pos = x+y*9;
            if (!(x > 0 && x < 8 && y > 0 && y < 4)) continue;
            if (runsSearch.size() <= runIndex) {
                inventory.setStack(pos, GuiItems_Database.fillerLight());
                continue;
            }
            inventory.setStack(pos, GuiItems_Database.run(runsSearch.get(runIndex)));
            runIndex++;
        }
        }
    }

    public void updateSearch() {
        runsSearch = new ArrayList<>();
        for (DO2Run run : allRuns) {
            if (filter_success == 1 && String.join("",run.finishers).isEmpty()) continue;
            if (filter_success == 2 && !String.join("",run.finishers).isEmpty()) continue;
            if (filter_difficulty == 1 && run.difficulty != 1) continue;
            if (filter_difficulty == 2 && run.difficulty != 2) continue;
            if (filter_difficulty == 3 && run.difficulty != 3) continue;
            if (filter_difficulty == 4 && run.difficulty != 4) continue;
            if (filter_difficulty == 5 && run.difficulty != 5) continue;
            if (filter_run_type == 1 && !run.run_type.equalsIgnoreCase("casual")) continue;
            if (filter_run_type == 2 && !run.run_type.equalsIgnoreCase("phase")) continue;
            if (!filter_player_uuid.isEmpty()) {
                List<String> remainingFilters = new ArrayList<>(List.copyOf(filter_player_uuid));
                remainingFilters.removeAll(run.runners);
                if (!String.join("",remainingFilters).isEmpty()) continue;
            }


            runsSearch.add(run);
        }
    }
}