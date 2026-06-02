package net.mat0u5.do2manager.utils;

import net.mat0u5.do2manager.Main;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.ReadOnlyScoreInfo;
import net.minecraft.world.scores.ScoreAccess;
import net.minecraft.world.scores.ScoreHolder;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ScoreboardUtils {
    public static java.lang.Integer getPlayerScore(MinecraftServer server, ServerPlayer player, String objectiveName) {
        return getPlayerScore(server, player.getScoreboardName(),objectiveName);
    }
    public static java.lang.Integer getPlayerScore(MinecraftServer server, String playerName, String objectiveName) {
        if (server == null) return null;
        Scoreboard scoreboard = server.getScoreboard();
        Objective objective = scoreboard.getObjective(objectiveName);
        if (objective != null) {
            ReadOnlyScoreInfo score = scoreboard.getPlayerScoreInfo(ScoreHolder.forNameOnly(playerName), objective);
            if (score == null) return null;
            return score.value();
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
    public static int renameScoreboardObjective(CommandSourceStack source, String oldObjectiveName, String newObjectiveName) {
        MinecraftServer server = source.getServer();
        Scoreboard scoreboard = server.getScoreboard();
        Objective oldObjective = scoreboard.getObjective(oldObjectiveName);
        final ServerPlayer self = source.getPlayer();

        // Check if the old objective exists
        if (oldObjective == null) {
            source.sendFailure(Component.literal("Objective with name '" + oldObjectiveName + "' does not exist."));
            return -1;
        }
        if (scoreboard.getObjective(newObjectiveName) != null) {
            source.sendFailure(Component.literal("Objective '" + newObjectiveName + "' already exists."));
            return -1;
        }

        // Create the new objective with the new name
        Objective newObjective = scoreboard.addObjective(
                newObjectiveName, oldObjective.getCriteria(),
                oldObjective.getDisplayName(), oldObjective.getRenderType(),
                oldObjective.displayAutoUpdate(), oldObjective.numberFormat()
        );

        // Get all players who have a score in the old objective
        Collection<PlayerScoreEntry> playerScores = scoreboard.listPlayerScores(oldObjective);

        // Copy all player scores from the old objective to the new objective
        for (PlayerScoreEntry playerScore : playerScores) {
            String playerName = playerScore.owner();
            int score = playerScore.value();
            ScoreAccess newScore = scoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(playerName), newObjective);
            newScore.set(score);
        }

        // Remove the old objective
        scoreboard.removeObjective(oldObjective);
        System.out.println("Renamed objective '" + oldObjectiveName + "' to '" + newObjectiveName + "'.");
        System.out.println("Don't forget to rename all the usages of '"+oldObjectiveName+"' in command blocks and functions :)");
        if (self != null) {
            self.sendSystemMessage(Component.nullToEmpty("Renamed objective '" + oldObjectiveName + "' to '" + newObjectiveName + "'."));
            self.sendSystemMessage(Component.nullToEmpty("Don't forget to rename all the usages of '"+oldObjectiveName+"' in command blocks and functions :)"));
        }
        return 1;
    }


    public static int copyObjectiveFromFile(MinecraftServer server, String newObjectiveName, String oldObjective, File scoreboardFile) {
        Scoreboard mainScoreboard = server.getScoreboard();
        if (mainScoreboard.getObjective(newObjectiveName) != null) {
            System.out.println("Objective '" + newObjectiveName + "' already exists in the main scoreboard.");
            return -1; // Objective already exists in the main scoreboard
        }
        try (FileInputStream fileInputStream = new FileInputStream(scoreboardFile)) {
            // Read the NBT data from the specified scoreboard file
            CompoundTag nbtData = NbtIo.readCompressed(fileInputStream, NbtAccounter.unlimitedHeap());
            if (nbtData == null) {
                System.out.println("Failed to read NBT data from the file.");
                return -1;
            }

            // Navigate to the "data" compound tag first
            CompoundTag dataTag = nbtData.getCompound("data").orElse(null);

            if (dataTag == null || !dataTag.contains("Objectives")) { // TODO test
                System.out.println("The 'Objectives' list was not found in the file.");
                return -1;
            }

            // Get the list of objectives from the NBT data
            ListTag objectivesList = dataTag.getList("Objectives").orElse(null); // TODO test
            if (objectivesList == null) {
                return -1;
            }
            CompoundTag desiredObjectiveData = null;

            // Find the desired objective by name
            for (int i = 0; i < objectivesList.size(); i++) {
                Optional<CompoundTag> objectiveData = objectivesList.getCompound(i);
                if (objectiveData.isPresent() && objectiveData.get().getString("Name").equals(oldObjective)) {
                    desiredObjectiveData = objectiveData.get();
                    break;
                }
            }

            if (desiredObjectiveData == null) {
                System.out.println("Objective '" + oldObjective + "' not found in the file.");
                return -1; // Objective not found in the file
            }
            String criterion = desiredObjectiveData.getString("CriteriaName").orElse(null);
            boolean autoUpdate = desiredObjectiveData.getByte("display_auto_update").orElse((byte)1) == (byte) 1;
            String displayName = desiredObjectiveData.getString("DisplayName").orElse(null);
            String name = desiredObjectiveData.getString("Name").orElse(null);
            String renderType = desiredObjectiveData.getString("RenderType").orElse(null);
            if (criterion == null || displayName == null || name == null || renderType == null) {
                return -1;
            }

            Objective newObjective = mainScoreboard.addObjective(
                    newObjectiveName, ObjectiveCriteria.registerCustom(criterion),
                    Component.nullToEmpty(newObjectiveName), ObjectiveCriteria.RenderType.byId(renderType),
                    autoUpdate, null
            );


            // Get the list of player scores associated with the objective
            ListTag playerScoresList = dataTag.getList("PlayerScores").orElse(null); // TODO test

            if (playerScoresList == null) {
                return -1;
            }

            // Copy all player scores for the desired objective
            for (int i = 0; i < playerScoresList.size(); i++) {
                Optional<CompoundTag> scoreData = playerScoresList.getCompound(i);
                if (scoreData.isPresent() && scoreData.get().getString("Objective").equals(oldObjective)) {
                    String playerName = scoreData.get().getString("Name").orElse(null);
                    int scoreValue = scoreData.get().getInt("Score").orElse(0);
                    if (playerName == null) continue;
                    ScoreAccess newScore = mainScoreboard.getOrCreatePlayerScore(ScoreHolder.forNameOnly(playerName), newObjective);
                    newScore.set(scoreValue);
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
