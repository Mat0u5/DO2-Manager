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
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

public class GuiInventoryClick {
    public static void onClickDatabaseGUI(String guiName, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, ScreenHandler handler) {
        ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
        ItemStack clickedItem = handler.getSlot(slotId).getStack();
        OtherUtils.playGuiClickSound(player);

        NbtCompound nbt = clickedItem.getNbt();
        String tag = nbt.getString("GUI_ITEM");
        if (guiName.equalsIgnoreCase("DatabaseGUI")) {
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
                if (button == 0) {
                    Main.openGuis.get(player).guiDatabase.filter_difficulty++;
                    if (Main.openGuis.get(player).guiDatabase.filter_difficulty > 5) Main.openGuis.get(player).guiDatabase.filter_difficulty =0;
                }
                else if (button == 1) {
                    Main.openGuis.get(player).guiDatabase.filter_level++;
                    if (Main.openGuis.get(player).guiDatabase.filter_level > 4) Main.openGuis.get(player).guiDatabase.filter_level =0;
                }
                Main.openGuis.get(player).guiDatabase.updateSearch();
                Main.openGuis.get(player).guiDatabase.populateRunInventory();
            } else if (tag.equalsIgnoreCase("filter_run_type")) {
                Main.openGuis.get(player).guiDatabase.filter_run_type++;
                if (Main.openGuis.get(player).guiDatabase.filter_run_type > 2) Main.openGuis.get(player).guiDatabase.filter_run_type =0;
                Main.openGuis.get(player).guiDatabase.updateSearch();
                Main.openGuis.get(player).guiDatabase.populateRunInventory();
            } else if (tag.equalsIgnoreCase("filter_player")) {
                if (button == 0) FakeSign.openFakeSign((ServerPlayerEntity) player);
                else if (button == 1) {
                    Main.openGuis.get(player).guiDatabase.filter_player = new ArrayList<>();
                    Main.openGuis.get(player).guiDatabase.filter_player_uuid = new ArrayList<>();
                    Main.openGuis.get(player).guiDatabase.updateSearch();
                    Main.openGuis.get(player).guiDatabase.populateRunInventory();
                }
            } else if (tag.equalsIgnoreCase("sort_by")) {
                if (button == 0) {
                    String current_sort = Main.openGuis.get(player).guiDatabase.sort_by;
                    if (current_sort.equalsIgnoreCase("run_number")) {
                        Main.openGuis.get(player).guiDatabase.sort_by = "run_length";
                    }
                    else if (current_sort.equalsIgnoreCase("run_length")) {
                        Main.openGuis.get(player).guiDatabase.sort_by = "difficulty";
                    }
                    else if (current_sort.equalsIgnoreCase("difficulty")) {
                        Main.openGuis.get(player).guiDatabase.sort_by = "embers";
                    }
                    else if (current_sort.equalsIgnoreCase("embers")) {
                        Main.openGuis.get(player).guiDatabase.sort_by = "crowns";
                    }
                    else if (current_sort.equalsIgnoreCase("crowns")) {
                        Main.openGuis.get(player).guiDatabase.sort_by = "run_number";
                    }
                    else {//Just to be safe
                        Main.openGuis.get(player).guiDatabase.sort_by = "run_number";
                    }
                }
                else if (button == 1) {
                    Main.openGuis.get(player).guiDatabase.sort_by_descending = !Main.openGuis.get(player).guiDatabase.sort_by_descending;
                }
                Main.openGuis.get(player).guiDatabase.updateSearch();
                Main.openGuis.get(player).guiDatabase.populateRunInventory();
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
            } else if (tag.equalsIgnoreCase("reset_all")) {
                serverPlayer.closeHandledScreen();
                Main.openGuis.get(player).invId="";
                new GuiInventory_Database().openRunInventory(serverPlayer);
            }
        }
        else if (guiName.equalsIgnoreCase("custom")) {
            boolean openNewInv = false;
            if (!Main.openGuis.containsKey(player)) openNewInv = true;
            else if (!Main.openGuis.get(player).invOpen) openNewInv = true;

            if (nbt.contains("GUI_ChangeToItem")) {
                String leadsToChest = nbt.getString("GUI_ChangeToItem");
                leadsToChest = "_"+leadsToChest;
                if (leadsToChest.contains(";")) {
                    String[] split = leadsToChest.split(";");
                    int invSize = 27;
                    if (split.length==3) invSize=54;

                    if (!openNewInv) {
                        int oldInvsize = Main.openGuis.get(player).inventory.size();
                        if (oldInvsize > 27) oldInvsize = 54;
                        else oldInvsize = 27;
                        if (oldInvsize == invSize) Main.openGuis.get(player).guiItems.populateInventory(player, Main.server.getOverworld(), leadsToChest, false);
                        else new GuiInventory_ChestFramework().openChestInventory((ServerPlayerEntity) player,invSize,"",leadsToChest,false);
                    }
                    else new GuiInventory_ChestFramework().openChestInventory((ServerPlayerEntity) player,invSize,"",leadsToChest,false);
                }
            }
            if (nbt.contains("GUI_ChangeTo")) {
                String leadsToChest = nbt.getString("GUI_ChangeTo");
                int invSize = leadsToChest.contains(";")?54:27;
                if (!openNewInv) {
                    int oldInvsize = Main.openGuis.get(player).inventory.size();
                    if (oldInvsize > 27) oldInvsize = 54;
                    else oldInvsize = 27;
                    if (oldInvsize == invSize) Main.openGuis.get(player).guiItems.populateInventory(player, Main.server.getOverworld(), leadsToChest, false);
                    else new GuiInventory_ChestFramework().openChestInventory((ServerPlayerEntity) player,invSize,"",leadsToChest,false);
                }
                else new GuiInventory_ChestFramework().openChestInventory((ServerPlayerEntity) player,invSize,"",leadsToChest,false);
            }
            if (nbt.contains("GUI_ChangeTo_OpenContainer")) {
                String leadsToChest = nbt.getString("GUI_ChangeTo_OpenContainer");
                int invSize = leadsToChest.contains(";")?54:27;
                if (!openNewInv) {
                    int oldInvsize = Main.openGuis.get(player).inventory.size();
                    if (oldInvsize > 27) oldInvsize = 54;
                    else oldInvsize = 27;
                    if (oldInvsize == invSize) Main.openGuis.get(player).guiItems.populateInventory(player, Main.server.getOverworld(), leadsToChest, true);
                    else new GuiInventory_ChestFramework().openChestInventory((ServerPlayerEntity) player,invSize,"",leadsToChest,true);
                }
                else new GuiInventory_ChestFramework().openChestInventory((ServerPlayerEntity) player,invSize,"",leadsToChest,true);
            }
            if (nbt.contains("GUI_ExecuteCommand")) {
                String command = nbt.getString("GUI_ExecuteCommand");
                OtherUtils.executeCommand(player.getServer(),command);
            }
        }
    }
}