package net.mat0u5.do2manager.gui;

import net.mat0u5.do2manager.world.DO2Run;
import net.minecraft.inventory.SimpleInventory;

import java.util.List;

public abstract class GuiPlayerSpecific {
    public int current_page = 1;
    public List<String> lastCriteria;
    public List<DO2Run> runsSearch;
    public SimpleInventory inventory;
    public String invId = "";
    public GuiInventory_Database guiDatabase;
    public int filter_success = 0;
    public int filter_difficulty = 0;
    public int filter_run_type = 0;
}
