package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.functions.CommandFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.scores.PlayerTeam;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FunctionPreview {

    static HashMap<BlockPos,String> blockLines = new HashMap<>();

    public static int previewFunction(CommandSourceStack source, Collection<CommandFunction<CommandSourceStack>> functions) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        blockLines.clear();
        if (self == null) return 0;
        killAllGlowingBlocks((ServerLevel) self.level());
        List<List<BlockPos>> blockPositions = new ArrayList<>();
        for (CommandFunction<CommandSourceStack> function : functions) {
            int lineNum = 0;
            self.sendSystemMessage(Component.nullToEmpty("Previewing "+function.id()));

            Path datapacksDirectory = server.getWorldPath(LevelResource.DATAPACK_DIR);
            Identifier functionId = function.id();
            String namespace = functionId.getNamespace();
            String functionPath = functionId.getPath();
            Path functionFilePath = datapacksDirectory
                    .resolve(namespace)
                    .resolve("data")
                    .resolve(namespace)
                    .resolve("function")
                    .resolve(functionPath + ".mcfunction");
            for (String line : getFunctionLines(functionFilePath.toString())) {
                lineNum++;
                if (line.trim().isEmpty() || line.trim().startsWith("#")) continue;
                for (BlockPos pos : parsePositions(line)) {
                    if (blockLines.containsKey(pos)) {
                        blockLines.put(pos, blockLines.get(pos)+", "+lineNum);
                    }
                    else {
                        blockLines.put(pos, String.valueOf(lineNum));
                    }
                }
                blockPositions.add(parsePositions(line));
            }
        }
        displayGlowingBlocks((ServerLevel) self.level(), blockPositions);
        return 1;
    }
    public static List<String> getFunctionLines(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath));  // Read all lines from the function file
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    public static int stopPreviewFunction(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        if (self == null) return 0;

        self.sendSystemMessage(Component.nullToEmpty("Stopping Preview"));
        killAllGlowingBlocks((ServerLevel) self.level());

        return 1;
    }
    public static List<BlockPos> parsePositions(String command) {
        String[] tokens = command.split(" ");
        List<BlockPos> positions = new ArrayList<>();

        List<Integer> coords = new ArrayList<>();

        // Loop through the command tokens and find numbers
        for (String token : tokens) {
            try {
                coords.add(Integer.parseInt(token));
                // Every 3 numbers make up one BlockPos
                if (coords.size() == 3) {
                    positions.add(new BlockPos(coords.get(0), coords.get(1), coords.get(2)));
                    coords.clear();
                }
            } catch (NumberFormatException ignored) {}
        }

        return positions;
    }

    public static void displayGlowingBlocks(ServerLevel world, List<List<BlockPos>> blockPosLists) {
        ChatFormatting[] colors = {
                ChatFormatting.BLACK, ChatFormatting.DARK_BLUE, ChatFormatting.DARK_GREEN, ChatFormatting.DARK_AQUA,
                ChatFormatting.DARK_RED, ChatFormatting.DARK_PURPLE, ChatFormatting.GOLD, ChatFormatting.GRAY,
                ChatFormatting.DARK_GRAY, ChatFormatting.BLUE, ChatFormatting.GREEN, ChatFormatting.AQUA,
                ChatFormatting.RED, ChatFormatting.LIGHT_PURPLE, ChatFormatting.YELLOW, ChatFormatting.WHITE
        };

        // Iterate through the outer list (List<List<BlockPos>>)
        for (int i = 0; i < blockPosLists.size(); i++) {
            List<BlockPos> blockPosList = blockPosLists.get(i);
            ChatFormatting color = colors[i % colors.length]; // Cycle through colors

            // For each block position in the inner list
            for (BlockPos pos : blockPosList) {
                spawnGlowingBlockDisplay(world, pos, color);
            }
        }
    }

    // Helper function to spawn a glowing BlockDisplayEntity at a BlockPos
    private static void spawnGlowingBlockDisplay(ServerLevel world, BlockPos pos, ChatFormatting color) {
        String blockName = "minecraft:glass";

        // Summon the block display entity
        String blockDisplayCommand = String.format(
                "summon block_display "+((double)pos.getX())+" "+((double)pos.getY())+" "+((double)pos.getZ())+" {block_state:{Name:\"%s\"},Tags:[\"function_preview\",\"team_assign_wait\"],Glowing:true}",
                blockName
        );
        OtherUtils.executeCommand(world.getServer(), blockDisplayCommand);

        // Assign color using teams
        String teamName = "glowing_" + color.getName();
        ServerScoreboard scoreboard = world.getScoreboard();
        if (!scoreboard.getTeamNames().contains(teamName)) {
            PlayerTeam team = scoreboard.addPlayerTeam(teamName);
            team.setColor(color);
        }
        String teamJoinCommand = "team join "+teamName+" @e[type=block_display,tag=function_preview,tag=team_assign_wait]";
        OtherUtils.executeCommand(world.getServer(), teamJoinCommand);
        String removeTag = "tag @e[tag=team_assign_wait] remove team_assign_wait";
        OtherUtils.executeCommand(world.getServer(), removeTag);

        // Summon the text display entity above the block display (1 block above)
        String posText = String.format("%d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
        if (blockLines.containsKey(pos)) posText+="\nLine: "+blockLines.get(pos);
        String textDisplayCommand = String.format(
                "summon text_display "+pos.getX()+" "+((double)pos.getY()+1.5)+" "+pos.getZ()+" {billboard:\"center\",see_through:1b,Tags:[\"function_preview\"],text:'\"%s\"'}",
                posText
        );
        OtherUtils.executeCommand(world.getServer(), textDisplayCommand);
    }
    public static void killAllGlowingBlocks(ServerLevel world) {
        String killCommand = "kill @e[tag=function_preview]";
        OtherUtils.executeCommand(world.getServer(), killCommand);
    }

}
