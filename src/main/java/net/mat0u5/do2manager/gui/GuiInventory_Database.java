package net.mat0u5.do2manager.gui;

import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class GuiInventory_Database {
    private static final int INVENTORY_SIZE = 54;

    public static void openRunInventory(ServerPlayerEntity player, int page) {
        SimpleInventory inventory = new SimpleInventory(INVENTORY_SIZE);

        // Populate the inventory with run data
        populateRunInventory(inventory, page);

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> {
            return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, inv, inventory, INVENTORY_SIZE / 9);
        }, Text.of("Run History")));
    }

    private static void populateRunInventory(SimpleInventory inventory, int page) {
        // Example of setting items in the inventory
        // Set success and failure runs
        for (int i = 0; i < 45; i++) {
            if (i % 2 == 0) {
                inventory.setStack(i, GuiItems_Database.run(null)); // Failure run
            } else {
                inventory.setStack(i, GuiItems_Database.run(null)); // Success run
            }
        }

        // Set navigation buttons
        inventory.setStack(45, GuiItems_Database.page(false)); // Previous page
        inventory.setStack(46, GuiItems_Database.searchItem()); // Search
        inventory.setStack(47, GuiItems_Database.page(true)); // Next page
    }
}