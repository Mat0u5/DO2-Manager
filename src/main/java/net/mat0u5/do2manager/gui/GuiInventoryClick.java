package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.FakeSign;
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
        OtherUtils.playGuiClickSound(player);

        NbtCompound nbt = clickedItem.getNbt();
        String tag = nbt.getString("GUI_ITEM");
        if (tag.equalsIgnoreCase("next_page")) {
            if (button == 0) Main.openGuis.get(player).guiDatabase.current_page += 1;
            else if (button == 1) Main.openGuis.get(player).guiDatabase.current_page = (int) Math.ceil(Main.openGuis.get(player).guiDatabase.runsSearch.size()/21)+1;
            Main.openGuis.get(player).guiDatabase.populateRunInventory();
        } else if (tag.equalsIgnoreCase("previous_page")) {
            if (Main.openGuis.get(player).guiDatabase.current_page <= 1) return;
            if (button == 0) Main.openGuis.get(player).guiDatabase.current_page -= 1;
            else if (button == 1) Main.openGuis.get(player).guiDatabase.current_page = 1;
            Main.openGuis.get(player).guiDatabase.populateRunInventory();
        } else if (tag.equalsIgnoreCase("next_page_custom_list")) {
            Main.openGuis.get(player).guiDatabase.current_page_custom_list += 1;
            Main.openGuis.get(player).guiDatabase.customItemListInventory(nbt.getString("custom_list_inv"), nbt.getInt("run_number"));
        }else if (tag.equalsIgnoreCase("previous_page_custom_list")) {
            if (Main.openGuis.get(player).guiDatabase.current_page_custom_list <= 1) return;
            Main.openGuis.get(player).guiDatabase.current_page_custom_list -= 1;
            Main.openGuis.get(player).guiDatabase.customItemListInventory(nbt.getString("custom_list_inv"), nbt.getInt("run_number"));
        }
        else if (tag.equalsIgnoreCase("filter_success")) {
            Main.openGuis.get(player).guiDatabase.filter_success++;
            if (Main.openGuis.get(player).guiDatabase.filter_success > 2) Main.openGuis.get(player).guiDatabase.filter_success =0;
            Main.openGuis.get(player).guiDatabase.updateSearch();
            Main.openGuis.get(player).guiDatabase.populateRunInventory();
        } else if (tag.equalsIgnoreCase("filter_difficulty")) {
            Main.openGuis.get(player).guiDatabase.filter_difficulty++;
            if (Main.openGuis.get(player).guiDatabase.filter_difficulty > 5) Main.openGuis.get(player).guiDatabase.filter_difficulty =0;
            Main.openGuis.get(player).guiDatabase.updateSearch();
            Main.openGuis.get(player).guiDatabase.populateRunInventory();
        } else if (tag.equalsIgnoreCase("filter_run_type")) {
            Main.openGuis.get(player).guiDatabase.filter_run_type++;
            if (Main.openGuis.get(player).guiDatabase.filter_run_type > 2) Main.openGuis.get(player).guiDatabase.filter_run_type =0;
            Main.openGuis.get(player).guiDatabase.updateSearch();
            Main.openGuis.get(player).guiDatabase.populateRunInventory();
        } else if (tag.equalsIgnoreCase("filter_player")) {
            FakeSign.openFakeSign((ServerPlayerEntity) player);
        } else if (tag.equalsIgnoreCase("toggle_heads")) {
            Main.openGuis.get(player).guiDatabase.showRunsAsHeads = !Main.openGuis.get(player).guiDatabase.showRunsAsHeads;
            Main.openGuis.get(player).guiDatabase.populateRunInventory();
        } else if (tag.equalsIgnoreCase("run") || tag.equalsIgnoreCase("back_to_run")) {
            Main.openGuis.get(player).guiDatabase.detailedRunInventory(nbt.getInt("run_number"));
        } else if (tag.equalsIgnoreCase("back_to_main")) {
            Main.openGuis.get(player).guiDatabase.populateRunInventory();
        } else if (tag.equalsIgnoreCase("card_plays")) {
            Main.openGuis.get(player).guiDatabase.current_page_custom_list = 1;
            Main.openGuis.get(player).guiDatabase.customItemListInventory(nbt.getString("custom_list_inv"), nbt.getInt("run_number"));
        } else if (tag.equalsIgnoreCase("inventory_save")) {
            Main.openGuis.get(player).guiDatabase.current_page_custom_list = 1;
            Main.openGuis.get(player).guiDatabase.customItemListInventory(nbt.getString("custom_list_inv"), nbt.getInt("run_number"));
        } else if (tag.equalsIgnoreCase("items_bought")) {
            Main.openGuis.get(player).guiDatabase.current_page_custom_list = 1;
            Main.openGuis.get(player).guiDatabase.customItemListInventory(nbt.getString("custom_list_inv"), nbt.getInt("run_number"));
        }
    }
}