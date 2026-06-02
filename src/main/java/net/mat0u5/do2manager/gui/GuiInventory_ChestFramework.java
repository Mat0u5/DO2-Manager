package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BarrelBlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

public class GuiInventory_ChestFramework extends GuiPlayerSpecific {

    public int openChestInventory(ServerPlayer player, int INVENTORY_SIZE, String invName, String leadsToChest, boolean actuallyInteract) {
        player.closeContainer();
        inventory = new SimpleContainer(INVENTORY_SIZE);
        invId = "custom";
        MenuType screenHandler;
        if (INVENTORY_SIZE / 9==6) screenHandler = MenuType.GENERIC_9x6;
        else if (INVENTORY_SIZE / 9==3) screenHandler = MenuType.GENERIC_9x3;
        else if (INVENTORY_SIZE / 9==1) screenHandler = MenuType.GENERIC_9x1;
        else if (INVENTORY_SIZE / 9==2) screenHandler = MenuType.GENERIC_9x2;
        else if (INVENTORY_SIZE / 9==4) screenHandler = MenuType.GENERIC_9x4;
        else if (INVENTORY_SIZE / 9==5) screenHandler = MenuType.GENERIC_9x5;
        else {
            screenHandler = MenuType.GENERIC_9x6;
        }
        populateInventory(player, Main.server.overworld(),leadsToChest,actuallyInteract);

        player.openMenu(new SimpleMenuProvider((syncId, inv, p) -> {
            return new ChestMenu(screenHandler, syncId, inv, inventory, Math.min(54,INVENTORY_SIZE) / 9);
        }, Component.nullToEmpty(invName)));
        invOpen = true;
        guiItems=this;
        Main.openGuis.put(player,this);
        return 1;
    }

    public void populateInventory(Player player, Level world, String leadsToChest, boolean actuallyInteract) {
        int[] inception = {-1,-1};
        if (leadsToChest.startsWith("_")) {
            leadsToChest = leadsToChest.replaceFirst("_","");
            String[] split = leadsToChest.split(";");
            if (split.length>=2)inception[0]=Integer.parseInt(split[1]);
            if (split.length>=3)inception[1]=Integer.parseInt(split[2]);
        }
        List<BlockPos> posList = OtherUtils.getPositionsFromString(leadsToChest);
        getInventoryFromChest(world, posList,actuallyInteract,inception);
    }

    public void getInventoryFromChest(Level world, List<BlockPos> posList, boolean actuallyInteract, int[] inception) {
        try {
            int listPos = 0;
            for (BlockPos pos : posList) {
                BlockState state = world.getBlockState(pos);
                if (state.getBlock() == Blocks.CHEST) {
                    ChestBlockEntity chestEntity = (ChestBlockEntity) world.getBlockEntity(pos);
                    if (chestEntity != null) {
                        for (int i = 0; i < chestEntity.getContainerSize(); i++) {
                            if (inception[0] == -1) {
                                if (listPos*27+i >= 54) return;
                                if (!actuallyInteract) inventory.setItem(listPos*27+i, chestEntity.getItem(i).copy());
                                else inventory.setItem(listPos*27+i, chestEntity.getItem(i));
                            }
                            else if (i == inception[0] || i == inception[1]) {
                                setFromItemStack(chestEntity.getItem(i).copy(),listPos);
                                listPos++;
                            }
                        }
                    }
                }
                else if (state.getBlock() == Blocks.BARREL) {
                    BarrelBlockEntity barrelEntity = (BarrelBlockEntity) world.getBlockEntity(pos);
                    if (barrelEntity != null) {
                        for (int i = 0; i < barrelEntity.getContainerSize(); i++) {
                            if (inception[0] == -1) {
                                if (listPos*27+i >= 54) return;
                                if (!actuallyInteract) inventory.setItem(listPos*27+i, barrelEntity.getItem(i).copy());
                                else inventory.setItem(listPos*27+i, barrelEntity.getItem(i));
                            }
                            else if (i == inception[0] || i == inception[1]) {
                                setFromItemStack(barrelEntity.getItem(i).copy(),listPos);
                                listPos++;
                            }
                        }
                    }
                }
                else if (state.getBlock().asItem().toString().contains("shulker_box")) {
                    ShulkerBoxBlockEntity shulkerEntity = (ShulkerBoxBlockEntity) world.getBlockEntity(pos);
                    if (shulkerEntity != null) {
                        for (int i = 0; i < shulkerEntity.getContainerSize(); i++) {
                            if (inception[0] == -1) {
                                if (listPos*27+i >= 54) return;
                                if (!actuallyInteract) inventory.setItem(listPos*27+i, shulkerEntity.getItem(i).copy());
                                else inventory.setItem(listPos*27+i, shulkerEntity.getItem(i));
                            }
                            else if (i == inception[0] || i == inception[1]) {
                                setFromItemStack(shulkerEntity.getItem(i).copy(),listPos);
                                listPos++;
                            }
                        }
                    }
                }

                else continue;
                listPos++;
            }
        }catch(Exception e) {}
    }
    public void setFromItemStack(ItemStack shulkerBox, int listPos) {
        List<ItemStack> items = ItemManager.getContainerItemContents(shulkerBox);
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            inventory.setItem(listPos*27+i, itemStack.copy());
        }
    }
}