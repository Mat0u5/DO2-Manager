package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.Main;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

import static net.mat0u5.do2manager.Main.allRuns;

public class GuiInventory_Items extends GuiPlayerSpecific {
    private final int INVENTORY_SIZE = 54;

    public int openItemsInventory(ServerPlayerEntity player) {
        inventory = new SimpleInventory(INVENTORY_SIZE);
        invId = "items";

        populateInventory(Main.server.getOverworld(),"main_menu");

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> {
            return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, inv, inventory, INVENTORY_SIZE / 9);
        }, Text.of("Decked Out 2 Items")));
        invOpen = true;
        guiItems=this;
        Main.openGuis.put(player,this);
        return 1;
    }

    public void populateInventory(World world, String type) {

        if (type.equalsIgnoreCase("cards_menu"))getInventoryFromDoubleChest(world,new BlockPos(-624, 10, 1954));

        if (type.equalsIgnoreCase("main_menu"))getInventoryFromDoubleChest(world,new BlockPos(-623, 7, 1954));
        if (type.equalsIgnoreCase("common_cards"))getInventoryFromDoubleChest(world,new BlockPos(-623, 8, 1954));
        if (type.equalsIgnoreCase("uncommon_cards"))getInventoryFromDoubleChest(world,new BlockPos(-623, 9, 1954));
        if (type.equalsIgnoreCase("rare_cards"))getInventoryFromDoubleChest(world,new BlockPos(-623, 10, 1954));
        if (type.equalsIgnoreCase("artifakes"))getInventoryFromDoubleChest(world,new BlockPos(-623, 11, 1954));
        if (type.equalsIgnoreCase("artifacts"))getInventoryFromDoubleChest(world,new BlockPos(-623, 12, 1954));

        if (type.equalsIgnoreCase("shadow_main"))getInventoryFromDoubleChest(world,new BlockPos(-622, 7, 1954));
        if (type.equalsIgnoreCase("shadow_items"))getInventoryFromDoubleChest(world,new BlockPos(-622, 8, 1954));
        if (type.equalsIgnoreCase("shadow_cards"))getInventoryFromDoubleChest(world,new BlockPos(-622, 9, 1954));
        if (type.equalsIgnoreCase("player_items"))getInventoryFromDoubleChest(world,new BlockPos(-622, 10, 1954));
        if (type.equalsIgnoreCase("mod_items"))getInventoryFromDoubleChest(world,new BlockPos(-622, 11, 1954));
        if (type.equalsIgnoreCase("legendaries"))getInventoryFromDoubleChest(world,new BlockPos(-622, 12, 1954));

        if (type.equalsIgnoreCase("cmd_main"))getInventoryFromDoubleChest(world,new BlockPos(-621, 7, 1954));
        if (type.equalsIgnoreCase("cmd_1"))getInventoryFromDoubleChest(world,new BlockPos(-621, 8, 1954));
        if (type.equalsIgnoreCase("cmd_2"))getInventoryFromDoubleChest(world,new BlockPos(-621, 9, 1954));
        if (type.equalsIgnoreCase("cmd_3"))getInventoryFromDoubleChest(world,new BlockPos(-621, 10, 1954));
        if (type.equalsIgnoreCase("cmd_4"))getInventoryFromDoubleChest(world,new BlockPos(-621, 11, 1954));
        if (type.equalsIgnoreCase("cmd_5"))getInventoryFromDoubleChest(world,new BlockPos(-621, 12, 1954));
    }

    public void getInventoryFromDoubleChest(World world, BlockPos pos) {

        BlockState state = world.getBlockState(pos);
        if (state.getBlock() != Blocks.CHEST) return;

        ChestBlockEntity chestEntity1 = (ChestBlockEntity) world.getBlockEntity(pos);

        // Determine the second chest position
        BlockPos secondChestPos = pos.offset(Direction.Axis.Z,-1);
        BlockState secondState = world.getBlockState(secondChestPos);

        // Ensure the second block is also a chest
        if (secondState.getBlock() != Blocks.CHEST) return;

        // Get the second chest block entity
        ChestBlockEntity chestEntity2 = (ChestBlockEntity) world.getBlockEntity(secondChestPos);

        if (chestEntity1 != null) {
            for (int i = 0; i < chestEntity1.size(); i++) {
                inventory.setStack(i, chestEntity1.getStack(i).copy());
            }
        }
        if (chestEntity2 != null) {
            for (int i = 0; i < chestEntity2.size(); i++) {
                inventory.setStack(27 + i, chestEntity2.getStack(i).copy());
            }
        }
    }
}