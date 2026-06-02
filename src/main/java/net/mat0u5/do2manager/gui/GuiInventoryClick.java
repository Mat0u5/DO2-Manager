package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.FakeSign;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Optional;

public class GuiInventoryClick {
    public static void onClickDatabaseGUI(String guiName, int slotId, int button, ClickType actionType, Player player, CallbackInfo ci, AbstractContainerMenu handler) {
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ItemStack clickedItem = handler.getSlot(slotId).getItem();
        OtherUtils.playGuiClickSound(player);

        CompoundTag nbt = clickedItem.get(DataComponents.CUSTOM_DATA).copyTag();
        Optional<String> tagOpt = nbt.getString("GUI_ITEM");
        if (tagOpt.isEmpty()) return;
        String tag = tagOpt.get();
        GuiPlayerSpecific gui = Main.openGuis.get(player);
        GuiInventory_Database guiDatabase = gui.guiDatabase;
        if (guiName.equalsIgnoreCase("DatabaseGUI")) {
            if (tag.equalsIgnoreCase("next_page")) {
                if (button == 0) guiDatabase.current_page += 1;
                else if (button == 1) guiDatabase.current_page = (int) Math.ceil((double) guiDatabase.runsSearchAbridged.size() /21);
                guiDatabase.populateRunInventory();
            } else if (tag.equalsIgnoreCase("previous_page")) {
                if (guiDatabase.current_page <= 1) return;
                if (button == 0) guiDatabase.current_page -= 1;
                else if (button == 1) guiDatabase.current_page = 1;
                guiDatabase.populateRunInventory();
            } else if (tag.equalsIgnoreCase("next_page_custom_list")) {
                guiDatabase.current_page_custom_list += 1;
                Optional<String> opt1 = nbt.getString("custom_list_inv");
                Optional<Integer> opt2 = nbt.getInt("run_number");
                if (opt1.isPresent() && opt2.isPresent()) guiDatabase.customItemListInventory(opt1.get(), opt2.get());
            }else if (tag.equalsIgnoreCase("previous_page_custom_list")) {
                if (guiDatabase.current_page_custom_list <= 1) return;
                guiDatabase.current_page_custom_list -= 1;
                Optional<String> opt1 = nbt.getString("custom_list_inv");
                Optional<Integer> opt2 = nbt.getInt("run_number");
                if (opt1.isPresent() && opt2.isPresent()) guiDatabase.customItemListInventory(opt1.get(), opt2.get());
            }
            else if (tag.equalsIgnoreCase("filter_success")) {
                guiDatabase.filter_success++;
                if (guiDatabase.filter_success > 2) guiDatabase.filter_success =0;
                guiDatabase.updateSearch();
                guiDatabase.populateRunInventory();
            } else if (tag.equalsIgnoreCase("filter_difficulty")) {
                if (button == 0) {
                    guiDatabase.filter_difficulty++;
                    if (guiDatabase.filter_difficulty > 5) guiDatabase.filter_difficulty =0;
                }
                else if (button == 1) {
                    guiDatabase.filter_level++;
                    if (guiDatabase.filter_level > 4) guiDatabase.filter_level =0;
                }
                guiDatabase.updateSearch();
                guiDatabase.populateRunInventory();
            } else if (tag.equalsIgnoreCase("filter_run_type")) {
                guiDatabase.filter_run_type++;
                if (guiDatabase.filter_run_type > 3) guiDatabase.filter_run_type =0;
                guiDatabase.updateSearch();
                guiDatabase.populateRunInventory();
            } else if (tag.equalsIgnoreCase("filter_player")) {
                if (button == 0) FakeSign.openFakeSign((ServerPlayer) player);
                else if (button == 1) {
                    guiDatabase.filter_player = new ArrayList<>();
                    guiDatabase.filter_player_uuid = new ArrayList<>();
                    guiDatabase.updateSearch();
                    guiDatabase.populateRunInventory();
                }
            } else if (tag.equalsIgnoreCase("sort_by")) {
                if (button == 0) {
                    String current_sort = guiDatabase.sort_by;
                    if (current_sort.equalsIgnoreCase("run_number")) {
                        guiDatabase.sort_by = "run_length";
                    }
                    else if (current_sort.equalsIgnoreCase("run_length")) {
                        guiDatabase.sort_by = "difficulty";
                    }
                    else if (current_sort.equalsIgnoreCase("difficulty")) {
                        guiDatabase.sort_by = "embers";
                    }
                    else if (current_sort.equalsIgnoreCase("embers")) {
                        guiDatabase.sort_by = "crowns";
                    }
                    else if (current_sort.equalsIgnoreCase("crowns")) {
                        guiDatabase.sort_by = "run_number";
                    }
                    else {//Just to be safe
                        guiDatabase.sort_by = "run_number";
                    }
                }
                else if (button == 1) {
                    guiDatabase.sort_by_descending = !guiDatabase.sort_by_descending;
                }
                guiDatabase.updateSearch();
                guiDatabase.populateRunInventory();
            } else if (tag.equalsIgnoreCase("toggle_heads")) {
                guiDatabase.showRunsAsHeads = !guiDatabase.showRunsAsHeads;
                guiDatabase.populateRunInventory();
            } else if (tag.equalsIgnoreCase("run") || tag.equalsIgnoreCase("back_to_run")) {
                Optional<Integer> opt2 = nbt.getInt("run_number");
                if (opt2.isPresent()) guiDatabase.detailedRunInventory(opt2.get());
            } else if (tag.equalsIgnoreCase("back_to_main")) {
                guiDatabase.populateRunInventory();
            } else if (tag.equalsIgnoreCase("card_plays")) {
                guiDatabase.current_page_custom_list = 1;
                Optional<String> opt1 = nbt.getString("custom_list_inv");
                Optional<Integer> opt2 = nbt.getInt("run_number");
                if (opt1.isPresent() && opt2.isPresent()) guiDatabase.customItemListInventory(opt1.get(), opt2.get());
            } else if (tag.equalsIgnoreCase("inventory_save")) {
                guiDatabase.current_page_custom_list = 1;
                Optional<String> opt1 = nbt.getString("custom_list_inv");
                Optional<Integer> opt2 = nbt.getInt("run_number");
                if (opt1.isPresent() && opt2.isPresent()) guiDatabase.customItemListInventory(opt1.get(), opt2.get());
            } else if (tag.equalsIgnoreCase("items_bought")) {
                guiDatabase.current_page_custom_list = 1;
                Optional<String> opt1 = nbt.getString("custom_list_inv");
                Optional<Integer> opt2 = nbt.getInt("run_number");
                if (opt1.isPresent() && opt2.isPresent()) guiDatabase.customItemListInventory(opt1.get(), opt2.get());
            } else if (tag.equalsIgnoreCase("reset_all")) {
                serverPlayer.closeContainer();
                gui.invId="";
                new GuiInventory_Database().openRunInventory(serverPlayer);
            } else if (tag.equalsIgnoreCase("player_choice")) {
                ResolvableProfile profile = clickedItem.get(DataComponents.PROFILE);
                if (profile == null) return;
                Optional<String> optName = profile.name();
                if (optName.isEmpty()) return;
                String playerName = optName.get();
                guiDatabase.filter_player.add(playerName);
                guiDatabase.filter_player_uuid.add(OtherUtils.getPlayerUUIDFromName(playerName));
                guiDatabase.updateSearch();
                guiDatabase.populateRunInventory();
            }
        }
        else if (guiName.equalsIgnoreCase("custom")) {
            boolean openNewInv;
            if (!Main.openGuis.containsKey(player)) openNewInv = true;
            else if (!gui.invOpen) openNewInv = true;
			else {
				openNewInv = false;
			}

			if (nbt.contains("GUI_ChangeToItem")) {
                nbt.getString("GUI_ChangeToItem").ifPresent(leadsToChest -> {
                    leadsToChest = "_" + leadsToChest;
                    if (leadsToChest.contains(";")) {
                        String[] split = leadsToChest.split(";");
                        int invSize = 27;
                        if (split.length == 3) invSize = 54;

                        if (!openNewInv) {
                            int oldInvsize = gui.inventory.getContainerSize();
                            if (oldInvsize > 27) oldInvsize = 54;
                            else oldInvsize = 27;
                            if (oldInvsize == invSize)
                                gui.guiItems.populateInventory(player, Main.server.overworld(), leadsToChest, false);
                            else
                                new GuiInventory_ChestFramework().openChestInventory((ServerPlayer) player, invSize, "", leadsToChest, false);
                        } else
                            new GuiInventory_ChestFramework().openChestInventory((ServerPlayer) player, invSize, "", leadsToChest, false);
                    }
                });
            }
            if (nbt.contains("GUI_ChangeTo")) {
                nbt.getString("GUI_ChangeTo").ifPresent(leadsToChest -> {
                    int invSize = leadsToChest.contains(";")?54:27;
                    if (!openNewInv) {
                        int oldInvsize = gui.inventory.getContainerSize();
                        if (oldInvsize > 27) oldInvsize = 54;
                        else oldInvsize = 27;
                        if (oldInvsize == invSize) gui.guiItems.populateInventory(player, Main.server.overworld(), leadsToChest, false);
                        else new GuiInventory_ChestFramework().openChestInventory((ServerPlayer) player,invSize,"",leadsToChest,false);
                    }
                    else new GuiInventory_ChestFramework().openChestInventory((ServerPlayer) player,invSize,"",leadsToChest,false);
                });
            }
            if (nbt.contains("GUI_ChangeTo_OpenContainer")) {
                nbt.getString("GUI_ChangeTo_OpenContainer").ifPresent(leadsToChest -> {
                    int invSize = leadsToChest.contains(";")?54:27;
                    if (!openNewInv) {
                        int oldInvsize = gui.inventory.getContainerSize();
                        if (oldInvsize > 27) oldInvsize = 54;
                        else oldInvsize = 27;
                        if (oldInvsize == invSize) gui.guiItems.populateInventory(player, Main.server.overworld(), leadsToChest, true);
                        else new GuiInventory_ChestFramework().openChestInventory((ServerPlayer) player,invSize,"",leadsToChest,true);
                    }
                    else new GuiInventory_ChestFramework().openChestInventory((ServerPlayer) player,invSize,"",leadsToChest,true);
                });
            }
            if (nbt.contains("GUI_ExecuteCommand")) {
                nbt.getString("GUI_ExecuteCommand").ifPresent(command -> {
                    OtherUtils.executeCommand(player.level().getServer(),command);
                });
            }
        }
    }
}