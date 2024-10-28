package net.mat0u5.do2manager.utils;

import net.mat0u5.do2manager.Main;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.scoreboard.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
            ReadableScoreboardScore score = scoreboard.getScore(ScoreHolder.fromName(playerName), objective);
            if (score == null) return null;
            return score.getScore();
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


    public static int copyObjectiveFromFile(MinecraftServer server, String newObjectiveName, String oldObjective, File scoreboardFile) {
        Scoreboard mainScoreboard = server.getScoreboard();
        if (mainScoreboard.getNullableObjective(newObjectiveName) != null) {
            System.out.println("Objective '" + newObjectiveName + "' already exists in the main scoreboard.");
            return -1; // Objective already exists in the main scoreboard
        }
        try (FileInputStream fileInputStream = new FileInputStream(scoreboardFile)) {
            // Read the NBT data from the specified scoreboard file
            NbtCompound nbtData = NbtIo.readCompressed(fileInputStream, NbtSizeTracker.ofUnlimitedBytes());
            if (nbtData == null) {
                System.out.println("Failed to read NBT data from the file.");
                return -1;
            }

            // Navigate to the "data" compound tag first
            NbtCompound dataTag = nbtData.getCompound("data");
            if (dataTag == null || !dataTag.contains("Objectives", 9)) { // 9 is the type ID for NbtList
                System.out.println("The 'Objectives' list was not found in the file.");
                return -1;
            }

            // Get the list of objectives from the NBT data
            NbtList objectivesList = dataTag.getList("Objectives", 10); // 10 is for compound tags
            NbtCompound desiredObjectiveData = null;

            // Find the desired objective by name
            for (int i = 0; i < objectivesList.size(); i++) {
                NbtCompound objectiveData = objectivesList.getCompound(i);
                if (objectiveData.getString("Name").equals(oldObjective)) {
                    desiredObjectiveData = objectiveData;
                    break;
                }
            }

            if (desiredObjectiveData == null) {
                System.out.println("Objective '" + oldObjective + "' not found in the file.");
                return -1; // Objective not found in the file
            }
            String criterion = desiredObjectiveData.getString("CriteriaName");
            boolean autoUpdate = desiredObjectiveData.getByte("display_auto_update") == (byte) 1;
            String displayName = desiredObjectiveData.getString("DisplayName");
            String name = desiredObjectiveData.getString("Name");
            String renderType = desiredObjectiveData.getString("RenderType");

            ScoreboardObjective newObjective = mainScoreboard.addObjective(
                    newObjectiveName, ScoreboardCriterion.create(criterion),
                    Text.of(newObjectiveName), ScoreboardCriterion.RenderType.getType(renderType),
                    autoUpdate, null
            );


            // Get the list of player scores associated with the objective
            NbtList playerScoresList = dataTag.getList("PlayerScores", 10); // 10 is for compound tags

            // Copy all player scores for the desired objective
            for (int i = 0; i < playerScoresList.size(); i++) {
                NbtCompound scoreData = playerScoresList.getCompound(i);
                if (scoreData.getString("Objective").equals(oldObjective)) {
                    String playerName = scoreData.getString("Name");
                    int scoreValue = scoreData.getInt("Score");
                    ScoreAccess newScore = mainScoreboard.getOrCreateScore(ScoreHolder.fromName(playerName), newObjective);
                    newScore.setScore(scoreValue);
                }
            }

            System.out.println("Objective '" + oldObjective + "' successfully copied from the file.");
            return 1; // Success
        } catch (Exception e) {
            System.out.println("An error occurred while reading the scoreboard file: " + e.getMessage());
            return -1; // Error reading the file
        }
    }
}
