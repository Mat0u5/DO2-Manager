package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FunctionPreview {

    static HashMap<BlockPos,String> blockLines = new HashMap<>();

    public static int previewFunction(ServerCommandSource source, Collection<CommandFunction<ServerCommandSource>> functions) {
        MinecraftServer server = source.getServer();
        final ServerPlayerEntity self = source.getPlayer();
        blockLines.clear();
        if (self == null) return 0;
        killAllGlowingBlocks((ServerWorld) self.getWorld());
        List<List<BlockPos>> blockPositions = new ArrayList<>();
        for (CommandFunction<ServerCommandSource> function : functions) {
            int lineNum = 0;
            self.sendMessage(Text.of("Previewing "+function));

            Path datapacksDirectory = server.getSavePath(WorldSavePath.DATAPACKS);
            Identifier functionId = function.id();
            String namespace = functionId.getNamespace();
            String functionPath = functionId.getPath();
            Path functionFilePath = datapacksDirectory
                    .resolve(namespace)
                    .resolve("data")
                    .resolve(namespace)
                    .resolve("functions")
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
        displayGlowingBlocks((ServerWorld) self.getWorld(), blockPositions);
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
    public static int stopPreviewFunction(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final ServerPlayerEntity self = source.getPlayer();
        if (self == null) return 0;

        self.sendMessage(Text.of("Stopping Preview"));
        killAllGlowingBlocks((ServerWorld) self.getWorld());

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

    public static void displayGlowingBlocks(ServerWorld world, List<List<BlockPos>> blockPosLists) {
        Formatting[] colors = {
                Formatting.BLACK, Formatting.DARK_BLUE, Formatting.DARK_GREEN, Formatting.DARK_AQUA,
                Formatting.DARK_RED, Formatting.DARK_PURPLE, Formatting.GOLD, Formatting.GRAY,
                Formatting.DARK_GRAY, Formatting.BLUE, Formatting.GREEN, Formatting.AQUA,
                Formatting.RED, Formatting.LIGHT_PURPLE, Formatting.YELLOW, Formatting.WHITE
        };

        // Iterate through the outer list (List<List<BlockPos>>)
        for (int i = 0; i < blockPosLists.size(); i++) {
            List<BlockPos> blockPosList = blockPosLists.get(i);
            Formatting color = colors[i % colors.length]; // Cycle through colors

            // For each block position in the inner list
            for (BlockPos pos : blockPosList) {
                spawnGlowingBlockDisplay(world, pos, color);
            }
        }
    }

    // Helper function to spawn a glowing BlockDisplayEntity at a BlockPos
    private static void spawnGlowingBlockDisplay(ServerWorld world, BlockPos pos, Formatting color) {
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
            Team team = scoreboard.addTeam(teamName);
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
    public static void killAllGlowingBlocks(ServerWorld world) {
        String killCommand = "kill @e[tag=function_preview]";
        OtherUtils.executeCommand(world.getServer(), killCommand);
    }

}
