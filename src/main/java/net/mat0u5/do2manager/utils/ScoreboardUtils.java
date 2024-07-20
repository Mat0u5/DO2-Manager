package net.mat0u5.do2manager.utils;

import net.mat0u5.do2manager.Main;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

public class ScoreboardUtils {
    public static java.lang.Integer getPlayerScore(MinecraftServer server, ServerPlayerEntity player, String objectiveName) {
        return getPlayerScore(server, player.getEntityName(),objectiveName);
    }
    public static java.lang.Integer getPlayerScore(MinecraftServer server, String playerName, String objectiveName) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);
        if (objective != null) {
            return scoreboard.getPlayerScore(playerName, objective).getScore();
        } else {
            return null; // Objective not found
        }
    }
    public static List<String> getLootEvents() {
        List<String> result = new ArrayList<>();
        MinecraftServer server = Main.server;
        String[] events = {"treasure","embers"};
        String[] levels = {"1","2","3","4"};
        for (String level : levels) {
            for (String event : events) {
                String playerName = "lvl_"+level+"_"+event;
                java.lang.Integer score = getPlayerScore(server,playerName,"LootEvents");
                String scoreStr = (score==null)?"0":String.valueOf(score);
                result.add(scoreStr);
            }
        }

        return result;
    }
}
