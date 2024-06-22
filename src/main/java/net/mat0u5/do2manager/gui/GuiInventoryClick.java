package net.mat0u5.do2manager.gui;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class GuiInventoryClick {
    public static void onClickDatabaseGUI(int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, ScreenHandler handler) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        ItemStack clickedItem = handler.getSlot(slotId).getStack();
        if (clickedItem.getItem() == Items.ARROW) {
            if (slotId == 45) {
                GuiInventory_Database.openRunInventory(serverPlayer, 1);
            } else if (slotId == 47) {
                GuiInventory_Database.openRunInventory(serverPlayer,2);
            }
        }
        else if (clickedItem.getItem() == Items.OAK_SIGN) {
        }
        else if (clickedItem.getItem() == Items.RED_WOOL || clickedItem.getItem() == Items.GREEN_WOOL) {
        }
    }
}