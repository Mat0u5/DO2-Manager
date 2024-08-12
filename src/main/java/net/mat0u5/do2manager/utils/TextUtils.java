package net.mat0u5.do2manager.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtils {
    public static HashMap<List<String>, List<String>> emotes = new HashMap<List<String>, List<String>>();
    public static void setEmotes() {
        emotes.put(List.of("coin"), List.of("\uE0A1", "1259207562687942816"));
        emotes.put(List.of("crown"), List.of("\uE0A2", "1259207563988307988"));
        emotes.put(List.of("ember"), List.of("\uE0A3", "1259207565330612345"));
        emotes.put(List.of("victory_tome"), List.of("\uE0A4", "1259208114411143198"));
        emotes.put(List.of("berries"), List.of("\uE0A5", "1259207556744740905"));

        emotes.put(List.of("treasure"), List.of("\uE0CA", "1259208112934621265"));
        emotes.put(List.of("hazard"), List.of("\uE0CB", "1259208002875949116"));
        emotes.put(List.of("clank"), List.of("\uE0C3", "1259207561434103838"));

        emotes.put(List.of("lvl2_key","key2"), List.of("\uE0C4", "1259208049181069384"));
        emotes.put(List.of("lvl3_key","key3"), List.of("\uE0C5", "1259208051160907919"));
        emotes.put(List.of("lvl4_key","key4"), List.of("\uE0C6", "1259208072841134201"));
        emotes.put(List.of("kit","rusty","rusty_kit"), List.of("\uE0C7", "1259208046207307826"));
        emotes.put(List.of("bomb"), List.of("\uE0C8", "1259207559672496159"));

        emotes.put(List.of("slab","the_slab"), List.of("\uE0C9", "1259208084463681699"));
        emotes.put(List.of("mug","mug_of_the_dungeon_master"), List.of("\uE0A6", "1259208074120532149"));
        emotes.put(List.of("pickaxe","old_friends_pickaxe"), List.of("\uE0A7", "1259208077933023376"));
        emotes.put(List.of("axe","axe_of_the_screamin_void"), List.of("\uE0A8", "1259207999944130701"));
        emotes.put(List.of("apron","butchers_apron"), List.of("\uE0A9", "1259207578987270205"));
        emotes.put(List.of("chisel","chisel_of_the_undead_sculptress"), List.of("\uE0AA", "1259208041266417787"));
        emotes.put(List.of("staff","worm","staff_of_the_pink_shepherd"), List.of("\uE0AB", "1259208107725426831"));
        emotes.put(List.of("loop","death_loop"), List.of("\uE0AC", "1259208047679639614"));
        //emotes.put(List.of("", "\uE0AD");
        emotes.put(List.of("shades","shades_of_the_dog"), List.of("\uE0AE", "1259208081359769620"));
        emotes.put(List.of("gem","gem_of_greatness"), List.of("\uE0AF", "1259207566156759101"));
        emotes.put(List.of("goat","horn_of_the_goat"), List.of("\uE0B0", "1259207567834611916"));
        emotes.put(List.of("goggles","goggles_of_symmetry"), List.of("\uE0B1", "1259208001458405557"));
        emotes.put(List.of("eye","golden_eye"), List.of("\uE0B2", "1259207571454165154"));
        emotes.put(List.of("stache","the_hidden_stache"), List.of("\uE0B3", "1259208109411270780"));
        emotes.put(List.of("hood","hood_of_aw_yah"), List.of("\uE0B4", "1259208039920177294"));
        emotes.put(List.of("bandana","hypnotic_bandana"), List.of("\uE0B5", "1259207582367748167"));
        emotes.put(List.of("jar","jar_of_speedy_slime"), List.of("\uE0B6", "1259208043166568569"));
        //emotes.put(List.of("", "\uE0B7");
        emotes.put(List.of("helm","knights_helm"), List.of("\uE0B8", "1259207575883485247"));
        emotes.put(List.of("tome","tome_of_the_hills"), List.of("\uE0B9", "1259208110808109239"));
        emotes.put(List.of("pearl","pearl_of_cleansing"), List.of("\uE0BA", "1259208076007833760"));
        emotes.put(List.of("waffle","multi_grain_waffle"), List.of("\uE0BB", "1259208115878887434"));
        emotes.put(List.of("bionic_eye","bionic_eye_of_doom"), List.of("\uE0BC", "1259207558233849857"));
        emotes.put(List.of("slippers","papas_slippers"), List.of("\uE0BD", "1259208106588770516"));
        emotes.put(List.of("watch","pocket_watch_of_shreeping"), List.of("\uE0BE", "1259208136062140571"));
        emotes.put(List.of("rocket","cf_135"), List.of("\uE0BF", "1259208079644299406"));
        emotes.put(List.of("skadoodler"), List.of("\uE0C0", "1259208083071041618"));
        emotes.put(List.of("key","masters_key","the_masters_key"), List.of("\uE0C1", "1259208044684771398"));
        emotes.put(List.of("wand","wand_of_gorgeousness"), List.of("\uE0C2", "1259208117598814298"));

        emotes.put(List.of("skull"),List.of("☠"));
        emotes.put(List.of("smile"),List.of("☺"));
        emotes.put(List.of("frown"),List.of("☹"));
        emotes.put(List.of("heart"),List.of("❤"));
        emotes.put(List.of("copyright"),List.of("©"));
        emotes.put(List.of("trademark","tm"),List.of("™"));

        emotes.put(List.of("mat","Mat0u5"), List.of("\uE0CC"));
        emotes.put(List.of("gari","Garibaldi","Garibaldi_"), List.of("\uE0CD"));
        emotes.put(List.of("onti","OntiMoose"), List.of("\uE0CE"));
        emotes.put(List.of("simple","ItsSimpleAsThat"), List.of("\uE0CF"));

        emotes.put(List.of("glitched","glitched_coin"), List.of("\uE0D0"));
        emotes.put(List.of("payday","willie","payday_payout"), List.of("\uE0D1"));
        emotes.put(List.of("chip","tungsten_chip"), List.of("\uE0D2", "1269729688092282910"));
        emotes.put(List.of("notes","notepad","almighty_notepad"), List.of("\uE0D3", "1269729689677729853"));
        emotes.put(List.of("fist","iron_fist"), List.of("\uE0D4"));
        emotes.put(List.of("tie","fancy_tie"), List.of("\uE0D5", "1269729694119628881"));
        emotes.put(List.of("trigger","haunter","haunters","haunters_trigger"), List.of("\uE0D6", "1269729686552973433"));
        emotes.put(List.of("spanner","super_spanner"), List.of("\uE0D7", "1269729692798292019"));
        emotes.put(List.of("orb","ball","ontiball","onti_ball","oversized_onti_ball"), List.of("\uE0D8", "1269729690944405615"));
        emotes.put(List.of("laptop","citadel_laptop"), List.of("\uE0D9", "1269729681687445504"));
        emotes.put(List.of("cloak","cloak_of_the_dungeon_master","cloak_of_the_new_dungeon_master"), List.of("\uE0DA", "1269729683650646017"));
        emotes.put(List.of("doormat","1027","auto_temp_doormat","door_mat"), List.of("\uE0DB", "1269729685110132929"));
        emotes.put(List.of("stopwatch","attuned_stopwatch","speedrun","speedrunner"), List.of("\uE0DC", "1272583799997333556"));
    }
    public static String replaceEmotes(String input) {
        for (Map.Entry<List<String>, List<String>> entry : emotes.entrySet()) {
            if (entry.getValue().size()==0) continue;
            String emoteValue = entry.getValue().get(0);
            for (String emote : entry.getKey()) {
                String emoteCode = ":" + emote + ":";
                input = replaceCaseInsensitive(input, emoteCode, emoteValue);
            }
            if (!input.contains(":")) return input;
        }
        return input;
    }
    public static String replaceEmotesDiscord(String input) {
        for (Map.Entry<List<String>, List<String>> entry : emotes.entrySet()) {
            if (entry.getValue().size() <=1) continue;
            String emoteValue = entry.getValue().get(0);
            String emoteID = entry.getValue().get(1);
            for (String emote : entry.getKey()) {
                String emoteCode = "<:" + emote + ":"+emoteID+">";
                input = replaceCaseInsensitive(input, emoteCode, emoteValue);
            }
            if (!input.contains(":")) return input;
        }
        return input;
    }
    public static String formatEmotesForDiscord(String input) {
        for (Map.Entry<List<String>, List<String>> entry : emotes.entrySet()) {
            if (entry.getValue().size() <=1) continue;
            String emoteValue = entry.getValue().get(1);
            for (String emote : entry.getKey()) {
                String emoteCode = ":" + emote + ":";
                input = replaceCaseInsensitive(input, emoteCode, "<"+emoteCode+emoteValue+">");
            }
            if (!input.contains(":")) return input;
        }
        return input;
    }
    public static String replaceCaseInsensitive(String input, String replaceWhat, String replaceWith) {
        Pattern pattern = Pattern.compile(replaceWhat, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(input);
        String result = matcher.replaceAll(replaceWith);
        return result;
    }
}
