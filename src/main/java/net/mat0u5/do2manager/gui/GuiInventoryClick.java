package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.Main;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class GuiInventoryClick {
    public static void onClickDatabaseGUI(int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, ScreenHandler handler) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        ItemStack clickedItem = handler.getSlot(slotId).getStack();
        NbtCompound nbt = clickedItem.getNbt();
        String tag = nbt.getString("GUI_ITEM");
        System.out.println("TTTESTTT>>> " + tag);
        if (tag.equalsIgnoreCase("next_page")) {
            System.out.println("TTTESTTT>><<> " + Main.openGuis.get(player).guiDatabase);
            System.out.println("TTTESTTT> " + Main.openGuis.get(player).guiDatabase.current_page);
            Main.openGuis.get(player).guiDatabase.current_page += 1;
            System.out.println("TTTESTTT> " + Main.openGuis.get(player).guiDatabase.current_page);
            Main.openGuis.get(player).guiDatabase.addRunItems();
        } else if (tag.equalsIgnoreCase("previous_page")) {
            Main.openGuis.get(player).guiDatabase.current_page -= 1;
            Main.openGuis.get(player).guiDatabase.addRunItems();
        }
    }
}