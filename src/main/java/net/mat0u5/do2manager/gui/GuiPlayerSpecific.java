package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.minecraft.inventory.SimpleInventory;

import java.util.ArrayList;
import java.util.List;

public abstract class GuiPlayerSpecific {
    public boolean invOpen = false;
    public int current_page = 1;
    public int current_page_custom_list = 1;
    public List<DO2RunAbridged> runsSearchAbridged = new ArrayList<>();
    public List<DO2Run> runsSearch = new ArrayList<>();
    public SimpleInventory inventory;
    public String invId = "";
    public GuiInventory_Database guiDatabase;
    public GuiInventory_ChestFramework guiItems;
    public int filter_success = 0;
    public int filter_difficulty = 0;
    public int filter_level = 0;
    public int filter_run_type = 0;
    public String sort_by = "run_number";
    public boolean sort_by_descending = true;
    public boolean showRunsAsHeads = true;
    public List<String> filter_player = new ArrayList<>();
    public List<String> filter_player_uuid = new ArrayList<>();
}
