package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class GuiInventory_ChestFramework extends GuiPlayerSpecific {

    public int openChestInventory(ServerPlayerEntity player, int INVENTORY_SIZE, String invName, String leadsToChest) {
        inventory = new SimpleInventory(INVENTORY_SIZE);
        invId = "custom";
        ScreenHandlerType screenHandler;
        if (INVENTORY_SIZE / 9==6) screenHandler = ScreenHandlerType.GENERIC_9X6;
        else if (INVENTORY_SIZE / 9==3) screenHandler = ScreenHandlerType.GENERIC_9X3;
        else if (INVENTORY_SIZE / 9==1) screenHandler = ScreenHandlerType.GENERIC_9X1;
        else if (INVENTORY_SIZE / 9==2) screenHandler = ScreenHandlerType.GENERIC_9X2;
        else if (INVENTORY_SIZE / 9==4) screenHandler = ScreenHandlerType.GENERIC_9X4;
        else if (INVENTORY_SIZE / 9==5) screenHandler = ScreenHandlerType.GENERIC_9X5;
        else {
            screenHandler = ScreenHandlerType.GENERIC_9X6;
        }
        populateInventory(Main.server.getOverworld(),leadsToChest);

        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> {
            return new GenericContainerScreenHandler(screenHandler, syncId, inv, inventory, Math.min(54,INVENTORY_SIZE) / 9);
        }, Text.of(invName)));
        invOpen = true;
        guiItems=this;
        Main.openGuis.put(player,this);
        return 1;
    }

    public void populateInventory(World world, String leadsToChest) {
        List<BlockPos> posList = OtherUtils.getPositionsFromString(leadsToChest);
        getInventoryFromChest(world, posList);
    }

    public void getInventoryFromChest(World world, List<BlockPos> posList) {
        try {
            int listPos = 0;
            for (BlockPos pos : posList) {
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() == Blocks.CHEST) {
                    ChestBlockEntity chestEntity = (ChestBlockEntity) world.getBlockEntity(pos);
                    if (chestEntity != null) {
                        for (int i = 0; i < chestEntity.size(); i++) {
                            if (listPos*27+i >= 54) return;
                            inventory.setStack(listPos*27+i, chestEntity.getStack(i).copy());
                        }
                    }
                }
                else if (state.getBlock() == Blocks.BARREL) {
                    BarrelBlockEntity barrelEntity = (BarrelBlockEntity) world.getBlockEntity(pos);
                    if (barrelEntity != null) {
                        for (int i = 0; i < barrelEntity.size(); i++) {
                            if (listPos*27+i >= 54) return;
                            inventory.setStack(listPos*27+i, barrelEntity.getStack(i).copy());
                        }
                    }
                }
                else continue;
                listPos++;
            }
        }catch(Exception e) {}
    }
}