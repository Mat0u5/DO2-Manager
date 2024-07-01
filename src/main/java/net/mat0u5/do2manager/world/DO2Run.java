package net.mat0u5.do2manager.world;

import com.google.gson.Gson;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.DO2_GSON;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.ArrayList;
import java.util.List;

public class DO2Run {
    public String run_type = null;
    public List<String> runners = new ArrayList<>();
    public List<String> finishers = new ArrayList<>();
    public List<ItemStack> card_plays = new ArrayList<>();
    public ItemStack compass_item = null;
    public ItemStack artifact_item = null;
    public ItemStack deck_item = null;
    public List<ItemStack> inventory_save = new ArrayList<>();
    public List<ItemStack> items_bought = new ArrayList<>();
    public String death_pos = null;
    public String death_message = null;

    public int difficulty = -1;
    public int run_number = -1;
    public int run_length = -1;
    public int embers_counted = 0;
    public int timestamp_lvl2_entry = -1;
    public int timestamp_lvl3_entry = -1;
    public int timestamp_lvl4_entry = -1;
    public int timestamp_lvl4_exit = -1;
    public int timestamp_lvl3_exit = -1;
    public int timestamp_lvl2_exit = -1;
    public int timestamp_lvl1_exit = -1;
    public int timestamp_artifact = -1;

    private static final Gson GSON = new Gson();

    public int getRunNum() {
        return run_number;
    }
    public String getRunnersName() {
        if (runners.isEmpty()) return "";
        List<String> runnersIGN = new ArrayList<>();
        for (String uuid : runners) {
            String player = Main.allPlayers.get(uuid);
            if (player != null) runnersIGN.add(player);
        }
        return String.join(", ",runnersIGN);
    }
    public boolean getSuccess() {
        return !String.join("",finishers).isEmpty();
    }
    public int getCompassLevel() {
        if (compass_item == null) return -1;
        NbtCompound nbt = compass_item.getNbt();
        if (ItemManager.hasNbtEntry(compass_item, "Level")) return nbt.getInt("Level");
        return -1;
    }
    // Serialize DO2Run object to JSON string
    public String serialize() {
        SerializedDO2Run serializedDO2Run = new SerializedDO2Run(
                run_type,
                runners,
                finishers,
                DO2_GSON.serializeListItemStack(card_plays),
                DO2_GSON.serializeItemStack(compass_item),
                DO2_GSON.serializeItemStack(artifact_item),
                DO2_GSON.serializeItemStack(deck_item),
                DO2_GSON.serializeListItemStack(inventory_save),
                DO2_GSON.serializeListItemStack(items_bought),
                death_pos,
                death_message,
                difficulty,
                run_number,
                run_length,
                embers_counted,
                timestamp_lvl2_entry,
                timestamp_lvl3_entry,
                timestamp_lvl4_entry,
                timestamp_lvl4_exit,
                timestamp_lvl3_exit,
                timestamp_lvl2_exit,
                timestamp_lvl1_exit,
                timestamp_artifact
        );
        return GSON.toJson(serializedDO2Run);
    }

    // Deserialize JSON string to DO2Run object
    public static DO2Run deserialize(String json) {
        SerializedDO2Run serializedDO2Run = GSON.fromJson(json, SerializedDO2Run.class);
        DO2Run do2Run = new DO2Run();

        do2Run.run_type = serializedDO2Run.run_type;
        do2Run.runners = serializedDO2Run.runners;
        do2Run.finishers = serializedDO2Run.finishers;
        do2Run.card_plays = DO2_GSON.deserializeListItemStack(serializedDO2Run.card_plays);
        do2Run.compass_item = DO2_GSON.deserializeItemStack(serializedDO2Run.compass_item);
        do2Run.artifact_item = DO2_GSON.deserializeItemStack(serializedDO2Run.artifact_item);
        do2Run.deck_item = DO2_GSON.deserializeItemStack(serializedDO2Run.deck_item);
        do2Run.inventory_save = DO2_GSON.deserializeListItemStack(serializedDO2Run.inventory_save);
        do2Run.items_bought = DO2_GSON.deserializeListItemStack(serializedDO2Run.items_bought);
        do2Run.death_pos = serializedDO2Run.death_pos;
        do2Run.death_message = serializedDO2Run.death_message;
        do2Run.difficulty = serializedDO2Run.difficulty;
        do2Run.run_number = serializedDO2Run.run_number;
        do2Run.run_length = serializedDO2Run.run_length;
        do2Run.embers_counted = serializedDO2Run.embers_counted;
        do2Run.timestamp_lvl2_entry = serializedDO2Run.timestamp_lvl2_entry;
        do2Run.timestamp_lvl3_entry = serializedDO2Run.timestamp_lvl3_entry;
        do2Run.timestamp_lvl4_entry = serializedDO2Run.timestamp_lvl4_entry;
        do2Run.timestamp_lvl4_exit = serializedDO2Run.timestamp_lvl4_exit;
        do2Run.timestamp_lvl3_exit = serializedDO2Run.timestamp_lvl3_exit;
        do2Run.timestamp_lvl2_exit = serializedDO2Run.timestamp_lvl2_exit;
        do2Run.timestamp_lvl1_exit = serializedDO2Run.timestamp_lvl1_exit;
        do2Run.timestamp_artifact = serializedDO2Run.timestamp_artifact;

        return do2Run;
    }

    // Inner class to represent the serialized form of a DO2Run
    private static class SerializedDO2Run {
        private final String run_type;
        private final List<String> runners;
        private final List<String> finishers;
        private final String card_plays;
        private final String compass_item;
        private final String artifact_item;
        private final String deck_item;
        private final String inventory_save;
        private final String items_bought;
        private final String death_pos;
        private final String death_message;

        private final int difficulty;
        private final int run_number;
        private final int run_length;
        private final int embers_counted;
        private final int timestamp_lvl2_entry;
        private final int timestamp_lvl3_entry;
        private final int timestamp_lvl4_entry;
        private final int timestamp_lvl4_exit;
        private final int timestamp_lvl3_exit;
        private final int timestamp_lvl2_exit;
        private final int timestamp_lvl1_exit;
        private final int timestamp_artifact;

        public SerializedDO2Run(String run_type, List<String> runners, List<String> finishers, String card_plays, String compass_item, String artifact_item, String deck_item, String inventory_save, String items_bought, String death_pos, String death_message, int difficulty, int run_number, int run_length, int embers_counted, int timestamp_lvl2_entry, int timestamp_lvl3_entry, int timestamp_lvl4_entry, int timestamp_lvl4_exit, int timestamp_lvl3_exit, int timestamp_lvl2_exit, int timestamp_lvl1_exit, int timestamp_artifact) {
            this.run_type = run_type;
            this.runners = runners;
            this.finishers = finishers;
            this.card_plays = card_plays;
            this.compass_item = compass_item;
            this.artifact_item = artifact_item;
            this.deck_item = deck_item;
            this.inventory_save = inventory_save;
            this.items_bought = items_bought;
            this.death_pos = death_pos;
            this.death_message = death_message;
            this.difficulty = difficulty;
            this.run_number = run_number;
            this.run_length = run_length;
            this.embers_counted = embers_counted;
            this.timestamp_lvl2_entry = timestamp_lvl2_entry;
            this.timestamp_lvl3_entry = timestamp_lvl3_entry;
            this.timestamp_lvl4_entry = timestamp_lvl4_entry;
            this.timestamp_lvl4_exit = timestamp_lvl4_exit;
            this.timestamp_lvl3_exit = timestamp_lvl3_exit;
            this.timestamp_lvl2_exit = timestamp_lvl2_exit;
            this.timestamp_lvl1_exit = timestamp_lvl1_exit;
            this.timestamp_artifact = timestamp_artifact;
        }
    }
}


