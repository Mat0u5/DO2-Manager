package net.mat0u5.do2manager.world;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.DO2_GSON;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.TextUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    public String date = null;
    public List<String> loot_drops = new ArrayList<>();
    public List<String> special_events = new ArrayList<>();

    public int id = -1;
    public int difficulty = -1;
    public int run_number = -1;
    public int run_length = -1;
    public int embers_counted = 0;
    public int crowns_counted = 0;
    public int timestamp_lvl2_entry = -1;
    public int timestamp_lvl3_entry = -1;
    public int timestamp_lvl4_entry = -1;
    public int timestamp_lvl4_exit = -1;
    public int timestamp_lvl3_exit = -1;
    public int timestamp_lvl2_exit = -1;
    public int timestamp_lvl1_exit = -1;
    public int timestamp_artifact = -1;

    private static final Gson GSON = new Gson();
    public DO2RunAbridged getAbridgedRun() {
        DO2RunAbridged run = new DO2RunAbridged();
        run.run_number = run_number;
        run.date = date;
        run.run_type = run_type;
        run.runners = runners;
        run.finishers = finishers;
        run.run_length = run_length;
        run.embers_counted = embers_counted;
        run.crowns_counted = crowns_counted;
        run.difficulty = difficulty;
        run.compass_level = getCompassLevel();
        run.id = id;

        return run;
    }
    public boolean isLackey() {
        if (runners == null) return false;
        if (runners.isEmpty()) return false;
        if (runners.size() == 1) return false;
        return true;
    }
    public int getEmbersFromInv() {
        int result = 0;
        for (ItemStack item : inventory_save) {
            if (item == null) continue;
            if (item.isEmpty()) continue;
            item = item.copy();
            if (ItemManager.isEmber(item)) {
                result += item.getCount();
            }
            else if (ItemManager.isDungeonArtifact(item)) {
                result += ItemManager.getArtifactWorth(item);
            }
        }
        return result;
    }
    public int getCrownsFromInv() {
        int crowns = 0;
        int coins = 0;
        for (ItemStack item : inventory_save) {
            if (item == null) continue;
            if (item.isEmpty()) continue;
            item = item.copy();
            if (ItemManager.isCrown(item)) {
                crowns += item.getCount();
            }
            else if (ItemManager.isCoin(item)) {
                coins += item.getCount();
            }
        }
        int total = crowns+Math.floorDiv(coins,4);
        return total;
    }
    public String getArtifactEmote() {
        return ":"+ ItemManager.getArtifactName(artifact_item)+":";
    }
    public void sendInfoToDiscord() {
        boolean run_success = getSuccess();
        String specialEvents = getFormattedEvents();
        String run_time = OtherUtils.convertTicksToClockTime(run_length,true);
        String formatted_arti = (artifact_item == null || artifact_item.isEmpty())?"":TextUtils.formatEmotesForDiscord(getArtifactEmote());
        if (run_time.contains(".")) {
            run_time = "**"+run_time.split("\\.")[0]+"**.*"+run_time.split("\\.")[1]+"*";
        }
        else {
            run_time = "**"+run_time+"**";
        }

        JsonObject json = DiscordUtils.getDefaultJSON();
        JsonObject embed = new JsonObject();
        embed.addProperty("description", "__**Run Info:**__   __*(Run #"+run_number+")*__"+
                "\n\nRunners: **"+getRunnersName()+"**"+
                "\nRun Successful: **"+(run_success?"Yes":"No")+"**"+
                "\n\nRun Type: **"+getRunType()+"**"+
                "\nRun Difficulty: **"+getUnFormattedDifficulty()+"**"+
                "\nCompass Level: "+getUnFormattedLevel()+
                "\nRun Length: "+run_time+"\n"+
                (artifact_item == null || artifact_item.isEmpty()?"":"\nArtifact:  " + formatted_arti)+
                "\nEmbers Counted: "+(run_success?embers_counted:getEmbersFromInv())+" <:ember:1259207565330612345> "+(!formatted_arti.isEmpty()?"(including  "+formatted_arti+")":"")+
                "\nCrowns Counted: "+(run_success?crowns_counted:getCrownsFromInv())+" <:crown:1259207563988307988>"+
                (!specialEvents.isEmpty()?"\nSpecial Events: "+specialEvents:"")
        );
        embed.addProperty("color", (run_type.equalsIgnoreCase("testing")?11223753:run_success?65289:16711680));
        JsonArray embeds = new JsonArray();
        embeds.add(embed);
        json.add("embeds", embeds);
        DiscordUtils.sendMessageToDiscord(json);
    }
    public String getRunType() {
        return TextUtils.capitalize(run_type);
    }
    public boolean containsSpecialEvent(String event) {
        if (special_events == null) return false;
        if (special_events.isEmpty()) return false;
        return special_events.contains(event);
    }
    public String getFormattedEvents() {
        String text = String.join(" ",special_events).replaceAll("bomb",":bomb:").replaceAll("rusty",":kit:")
                .replaceAll("dive",":diving_mask:").replaceAll("witch_hut",":jack_o_lantern:");
        return TextUtils.formatEmotesForDiscord(text);
    }
    public String getFormattedDifficulty() {
        if (difficulty==1) return "§aEasy";
        if (difficulty==2) return "§eMedium";
        if (difficulty==3) return "§6Hard";
        if (difficulty==4) return "§4Deadly";
        if (difficulty==5) return "§3Deepfrost";
        return "§dnull";
    }
    public String getUnFormattedDifficulty() {
        if (difficulty==1) return "Easy";
        if (difficulty==2) return "Medium";
        if (difficulty==3) return "Hard";
        if (difficulty==4) return "Deadly";
        if (difficulty==5) return "Deepfrost";
        return "null";
    }
    public String getUnFormattedLevel() {
        int level = getCompassLevel();
        if (level==1) return "Level 1";
        if (level==2) return "Level 2";
        if (level==3) return "Level 3";
        if (level==4) return "Level 4";
        return "null";
    }
    public String getFormattedLevel() {
        int level = getCompassLevel();
        if (level==1) return "§aLevel 1";
        if (level==2) return "§6Level 2";
        if (level==3) return "§4Level 3";
        if (level==4) return "§3Level 4";
        return "§dnull";
    }
    public String getFormattedDate() {
        if (date==null || date.isEmpty()) return "";
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(date, inputFormatter);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy  HH:mm", Locale.ENGLISH);
        return dateTime.format(outputFormatter);
    }
    public int timestampDate() {
        if (date==null || date.isEmpty()) return -1;
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(date, inputFormatter);
        return dateTime.getSecond();
    }
    public int getRunNum() {
        return run_number;
    }
    public String getRunnersName() {
        return getListName(runners);
    }
    public String getFinishersName() {
        return getListName(finishers);
    }
    private String getListName(List<String> players) {
        if (players.isEmpty()) return "";
        List<String> runnersIGN = new ArrayList<>();
        for (String uuid : players) {
            String player = Main.allPlayers.get(uuid);
            if (player != null) runnersIGN.add(player);
        }
        return String.join(", ",runnersIGN);
    }
    public ItemStack getRunnerSkull() {
        ItemStack itemStack = new ItemStack(Items.PLAYER_HEAD, 1);
        String playerList = String.join(",",runners);
        if (!playerList.isEmpty()) {
            if (!playerList.contains(",")) {
                String playerName = Main.allPlayers.get(runners.get(0));
                NbtCompound nbt = itemStack.getOrCreateNbt();
                nbt.putString("SkullOwner", playerName);
            }
            else {
                itemStack = new ItemStack(Items.CARVED_PUMPKIN, 1);
                NbtCompound nbt = itemStack.getOrCreateNbt();
                nbt.putInt("CustomModelData", 46);
            }
        }
        return itemStack;
    }
    public boolean getSuccess() {
        return !String.join("",finishers).isEmpty();
    }
    public boolean getSuccessFor(String uuid) {
        if (!getSuccess()) return false;
        return finishers.contains(uuid);
    }
    public boolean getSuccessAdvanced(List<String> filter_player_uuid) {
        boolean currentSuccess = getSuccess();
        if (!currentSuccess) return false;
        if (filter_player_uuid.isEmpty()) return currentSuccess;
        if (!isLackey()) return currentSuccess;

        for (String uuid : filter_player_uuid) {
            if (getSuccessFor(uuid)) return true;
        }

        return false;
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
                loot_drops,
                special_events,
                id,
                difficulty,
                run_number,
                run_length,
                embers_counted,
                crowns_counted,
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
        do2Run.loot_drops = serializedDO2Run.loot_drops;
        do2Run.special_events = serializedDO2Run.special_events;
        do2Run.id = serializedDO2Run.id;
        do2Run.difficulty = serializedDO2Run.difficulty;
        do2Run.run_number = serializedDO2Run.run_number;
        do2Run.run_length = serializedDO2Run.run_length;
        do2Run.embers_counted = serializedDO2Run.embers_counted;
        do2Run.crowns_counted = serializedDO2Run.crowns_counted;
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
        private final List<String> loot_drops;
        private final List<String> special_events;

        private final int id;
        private final int difficulty;
        private final int run_number;
        private final int run_length;
        private final int embers_counted;
        private final int crowns_counted;
        private final int timestamp_lvl2_entry;
        private final int timestamp_lvl3_entry;
        private final int timestamp_lvl4_entry;
        private final int timestamp_lvl4_exit;
        private final int timestamp_lvl3_exit;
        private final int timestamp_lvl2_exit;
        private final int timestamp_lvl1_exit;
        private final int timestamp_artifact;

        public SerializedDO2Run(String run_type, List<String> runners, List<String> finishers, String card_plays, String compass_item, String artifact_item, String deck_item, String inventory_save, String items_bought, String death_pos, String death_message,List<String> loot_drops, List<String> special_events, int id, int difficulty, int run_number, int run_length, int embers_counted, int crowns_counted, int timestamp_lvl2_entry, int timestamp_lvl3_entry, int timestamp_lvl4_entry, int timestamp_lvl4_exit, int timestamp_lvl3_exit, int timestamp_lvl2_exit, int timestamp_lvl1_exit, int timestamp_artifact) {
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
            this.loot_drops = loot_drops;
            this.special_events = special_events;

            this.id = id;
            this.difficulty = difficulty;
            this.run_number = run_number;
            this.run_length = run_length;
            this.embers_counted = embers_counted;
            this.crowns_counted = crowns_counted;
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


