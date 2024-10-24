package net.mat0u5.do2manager.utils;

import net.mat0u5.do2manager.Main;
import net.minecraft.scoreboard.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ScoreboardUtils {
    public static java.lang.Integer getPlayerScore(MinecraftServer server, ServerPlayerEntity player, String objectiveName) {
        return getPlayerScore(server, player.getNameForScoreboard(),objectiveName);
    }
    public static java.lang.Integer getPlayerScore(MinecraftServer server, String playerName, String objectiveName) {
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective objective = scoreboard.getNullableObjective(objectiveName);
        if (objective != null) {
            return scoreboard.getScore(ScoreHolder.fromName(playerName), objective).getScore();
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
    public static int renameScoreboardObjective(ServerCommandSource source, String oldObjectiveName, String newObjectiveName) {
        MinecraftServer server = source.getServer();
        Scoreboard scoreboard = server.getScoreboard();
        ScoreboardObjective oldObjective = scoreboard.getNullableObjective(oldObjectiveName);
        final ServerPlayerEntity self = source.getPlayer();

        // Check if the old objective exists
        if (oldObjective == null) {
            source.sendError(Text.literal("Objective with name '" + oldObjectiveName + "' does not exist."));
            return -1;
        }
        if (scoreboard.getNullableObjective(newObjectiveName) != null) {
            source.sendError(Text.literal("Objective '" + newObjectiveName + "' already exists."));
            return -1;
        }

        // Create the new objective with the new name
        ScoreboardObjective newObjective = scoreboard.addObjective(
                newObjectiveName, oldObjective.getCriterion(),
                oldObjective.getDisplayName(), oldObjective.getRenderType(),
                oldObjective.shouldDisplayAutoUpdate(), oldObjective.getNumberFormat()
        );

        // Get all players who have a score in the old objective
        Collection<ScoreboardEntry> playerScores = scoreboard.getScoreboardEntries(oldObjective);

        // Copy all player scores from the old objective to the new objective
        for (ScoreboardEntry playerScore : playerScores) {
            String playerName = playerScore.owner();
            int score = playerScore.value();
            ScoreAccess newScore = scoreboard.getOrCreateScore(ScoreHolder.fromName(playerName), newObjective);
            newScore.setScore(score);
        }

        // Remove the old objective
        scoreboard.removeObjective(oldObjective);
        System.out.println("Renamed objective '" + oldObjectiveName + "' to '" + newObjectiveName + "'.");
        System.out.println("Don't forget to rename all the usages of '"+oldObjectiveName+"' in command blocks and functions :)");
        if (self != null) {
            self.sendMessage(Text.of("Renamed objective '" + oldObjectiveName + "' to '" + newObjectiveName + "'."));
            self.sendMessage(Text.of("Don't forget to rename all the usages of '"+oldObjectiveName+"' in command blocks and functions :)"));
        }
        return 1;
    }
}
