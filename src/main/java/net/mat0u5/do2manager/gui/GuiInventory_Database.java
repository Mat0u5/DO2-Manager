package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.world.DO2Run;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

public class GuiInventory_Database extends GuiPlayerSpecific {
    private final int INVENTORY_SIZE = 54;



    public int openRunInventory(ServerPlayerEntity player) {
        if (!player.getEntityName().equalsIgnoreCase("Mat0u5")) return -1;
        inventory = new SimpleInventory(INVENTORY_SIZE);
        invId = "runs";
        // Populate the inventory with run data
        populateRunInventory();

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> {
            return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, inv, inventory, INVENTORY_SIZE / 9);
        }, Text.of("Run History")));
        Main.openGuis.put(player,this);
        guiDatabase = this;
        return 1;
    }

    public void populateRunInventory() {
        updateSearch();
        for (int i = 0; i < 54; i++) {
            inventory.setStack(i, GuiItems_Database.filler());
        }

        // Set navigation buttons
        inventory.setStack(46, GuiItems_Database.page(false)); // Previous page
        inventory.setStack(50, GuiItems_Database.filterSuccess()); // Search
        inventory.setStack(52, GuiItems_Database.page(true)); // Next page
        addRunItems();
    }

    public void addRunItems() {
        System.out.println("Adding....." + current_page);
        int runIndex = (current_page-1)*27;
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
        List<String> currentCriteria = getCurrentCriteria();
        //if (lastCriteria.equals(currentCriteria) & !(lastCriteria.isEmpty() && runsSearch.isEmpty())) return;
        lastCriteria = currentCriteria;
        System.out.println("Searching database for runs matching criteria.");
        runsSearch = DatabaseManager.getRunsByCriteria(currentCriteria);

        Collections.sort(runsSearch, new Comparator<DO2Run>() {
            @Override
            public int compare(DO2Run run1, DO2Run run2) {
                return Integer.compare(run2.getRunNum(), run1.getRunNum());
            }
        });
    }
    public List<String> getCurrentCriteria() {
        return new ArrayList<>();
    }
}