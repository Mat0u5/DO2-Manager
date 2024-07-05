package net.mat0u5.do2manager.utils;

import java.util.HashMap;
import java.util.Map;

public class TextUtils {
    public static HashMap<String, String> emotes = new HashMap<String, String>();
    public static void setEmotes() {
        emotes.put("coin", "\uE0A1");
        emotes.put("coin_big", "\uE0CA");
        emotes.put("crown", "\uE0A2");
        emotes.put("ember", "\uE0A3");
        emotes.put("victory_tome", "\uE0A4");
        emotes.put("berries", "\uE0A5");

        emotes.put("hazard", "\uE0CB");
        emotes.put("clank", "\uE0C3");

        emotes.put("lvl2_key", "\uE0C4");
        emotes.put("key2", "\uE0C4");
        emotes.put("lvl3_key", "\uE0C5");
        emotes.put("key3", "\uE0C5");
        emotes.put("lvl4_key", "\uE0C6");
        emotes.put("key4", "\uE0C6");
        emotes.put("rusty", "\uE0C7");
        emotes.put("rusty_kit", "\uE0C7");
        emotes.put("bomb", "\uE0C8");

        emotes.put("slab", "\uE0C9");
        emotes.put("the_slab", "\uE0C9");
        emotes.put("mug", "\uE0A6");
        emotes.put("mug_of_the_dungeon_master", "\uE0A6");
        emotes.put("pickaxe", "\uE0A7");
        emotes.put("old_friends_pickaxe", "\uE0A7");
        emotes.put("axe", "\uE0A8");
        emotes.put("axe_of_the_screamin_void", "\uE0A8");
        emotes.put("apron", "\uE0A9");
        emotes.put("butchers_apron", "\uE0A9");
        emotes.put("chisel", "\uE0AA");
        emotes.put("chisel_of_the_undead_sculptress", "\uE0AA");
        emotes.put("staff", "\uE0AB");
        emotes.put("worm", "\uE0AB");
        emotes.put("staff_of_the_pink_shepherd", "\uE0AB");
        emotes.put("loop", "\uE0AC");
        emotes.put("death_loop", "\uE0AC");
        //emotes.put("", "\uE0AD");
        emotes.put("shades", "\uE0AE");
        emotes.put("shades_of_the_dog", "\uE0AE");
        emotes.put("gem", "\uE0AF");
        emotes.put("gem_of_greatness", "\uE0AF");
        emotes.put("goat", "\uE0B0");
        emotes.put("horn_of_the_goat", "\uE0B0");
        emotes.put("goggles", "\uE0B1");
        emotes.put("goggles_of_symmetry", "\uE0B1");
        emotes.put("eye", "\uE0B2");
        emotes.put("golden_eye", "\uE0B2");
        emotes.put("stache", "\uE0B3");
        emotes.put("the_hidden_stache", "\uE0B3");
        emotes.put("hood", "\uE0B4");
        emotes.put("hood_of_aw_yah", "\uE0B4");
        emotes.put("bandana", "\uE0B5");
        emotes.put("hypnotic_bandana", "\uE0B5");
        emotes.put("jar", "\uE0B6");
        emotes.put("jar_of_speedy_slime", "\uE0B6");
        //emotes.put("", "\uE0B7");
        emotes.put("helm", "\uE0B8");
        emotes.put("knights_helm", "\uE0B8");
        emotes.put("tome", "\uE0B9");
        emotes.put("tome_of_the_hills", "\uE0B9");
        emotes.put("pearl", "\uE0BA");
        emotes.put("pearl_of_cleansing", "\uE0BA");
        emotes.put("waffle", "\uE0BB");
        emotes.put("multi_grain_waffle", "\uE0BB");
        emotes.put("bionic_eye", "\uE0BC");
        emotes.put("bionic_eye_of_doom", "\uE0BC");
        emotes.put("slippers", "\uE0BD");
        emotes.put("papas_slippers", "\uE0BD");
        emotes.put("watch", "\uE0BE");
        emotes.put("pocket_watch_of_shreeping", "\uE0BE");
        emotes.put("rocket", "\uE0BF");
        emotes.put("cf_135", "\uE0BF");
        emotes.put("skadoodler", "\uE0C0");
        emotes.put("key", "\uE0C1");
        emotes.put("masters_key", "\uE0C1");
        emotes.put("the_masters_key", "\uE0C1");
        emotes.put("wand", "\uE0C2");
        emotes.put("wand_of_gorgeousness", "\uE0C2");

        emotes.put("skull","☠");
        emotes.put("smile","☺");
        emotes.put("frown","☹");
        emotes.put("heart","❤");
        emotes.put("copyright","©");
        emotes.put("trademark","™");
    }
    public static String replaceEmotes(String input) {
        for (Map.Entry<String, String> entry : emotes.entrySet()) {
            String emoteCode = ":" + entry.getKey() + ":";
            String emoteValue = entry.getValue();
            input = input.replaceAll(emoteCode, emoteValue);
            if (!input.contains(":")) return input;
        }
        return input;
    }
}
