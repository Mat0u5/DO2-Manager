package net.mat0u5.do2manager.blockblocker;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.List;

@Config(name = "blockblocker")
public class BlockBlockerConfig implements ConfigData {

    @ConfigEntry.Gui.CollapsibleObject
    public General general = new General();

    public static class General {
        @Comment("Bypass the blocking when a player is an OP.")
        public boolean opBypass = false;

        @Comment("Bypass the blocking when a player is in creative mode.")
        public boolean creativeBypass = false;

        @Comment("Prevent these BLOCKS from being interacted with entirely (Only works if configured on client as well as server). If dimensions are desired, syntax is \"modid:block$dimensions\"")
        public List<? extends String> noInteract = BlockBlocker.defaultValues;

        @Comment("BLOCK id's in this list will not be harvestable. If dimensions are desired, syntax is \"modid:block$dimension\"")
        public List<? extends String> noHarvest = BlockBlocker.defaultValues;

        @Comment("BLOCK id's in this list will not be placeable and\nwill not spawn in the world nor return to the player. If dimensions are desired, syntax is \"modid:block$dimensions\"")
        public List<? extends String> noPlace = BlockBlocker.defaultValues;
    }
}

