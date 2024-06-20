package net.mat0u5.do2manager.utils;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

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
}
