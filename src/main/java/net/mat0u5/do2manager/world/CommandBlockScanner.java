package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.server.world.ServerChunkManager;

import java.util.ArrayList;
import java.util.List;

public class CommandBlockScanner {
    static List<Integer> percentCompleted = new ArrayList<>();

    public static void scanArea(ServerWorld world, BlockPos startPos, BlockPos endPos, PlayerEntity player) {
        percentCompleted = new ArrayList<>();
        List<BlockPos> positionsToCheck = new ArrayList<>();

        int minX = Math.min(startPos.getX(), endPos.getX());
        int maxX = Math.max(startPos.getX(), endPos.getX());
        int minY = Math.min(startPos.getY(), endPos.getY());
        int maxY = Math.max(startPos.getY(), endPos.getY());
        int minZ = Math.min(startPos.getZ(), endPos.getZ());
        int maxZ = Math.max(startPos.getZ(), endPos.getZ());

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    positionsToCheck.add(new BlockPos(x, y, z));
                }
            }
        }

        processPositions(world, positionsToCheck, 0, player);
    }

    private static void processPositions(ServerWorld world, List<BlockPos> positions, int index, PlayerEntity player) {
        int batchSize = 100; // Number of positions to process per tick
        MinecraftServer server = world.getServer();
        ServerChunkManager chunkManager = world.getChunkManager();

        if (index >= positions.size()) {
            // Processing complete
            player.sendMessage(Text.of("§aCommand block scan complete."));
            System.out.println("Command block scan complete.");
            return;
        }

        // Process a batch of positions
        int endIndex = Math.min(index + batchSize, positions.size());
        for (int i = index; i < endIndex; i++) {
            BlockPos pos = positions.get(i);
            ChunkPos chunkPos = new ChunkPos(pos);

            if (!chunkManager.isChunkLoaded(chunkPos.x, chunkPos.z)) {
                chunkManager.addTicket(ChunkTicketType.FORCED, chunkPos, 1, chunkPos);
                world.getChunk(chunkPos.x, chunkPos.z);
            }

            Block block = world.getBlockState(pos).getBlock();
            if (block == Blocks.COMMAND_BLOCK || block == Blocks.CHAIN_COMMAND_BLOCK || block == Blocks.REPEATING_COMMAND_BLOCK) {
                CommandBlockBlockEntity commandBlockEntity = (CommandBlockBlockEntity) world.getBlockEntity(pos);
                if (commandBlockEntity != null) {
                    CommandBlockExecutor executor = commandBlockEntity.getCommandExecutor();
                    String command = executor.getCommand();
                    if (command.startsWith("/")) command = command.substring(1);
                    String type = block == Blocks.COMMAND_BLOCK ? "Impulse" : block == Blocks.CHAIN_COMMAND_BLOCK ? "Chain" : "Repeating";
                    boolean conditional = commandBlockEntity.isConditionalCommandBlock();
                    boolean auto = commandBlockEntity.isAuto();
                    DatabaseManager.addCommandBlock(pos.getX(), pos.getY(), pos.getZ(), type, conditional, auto, command);
                }
            }
        }
        int percent = (int) Math.floor((endIndex * 10d) / (double) positions.size())*10;
        if (!percentCompleted.contains(percent) && percent != 0) {
            percentCompleted.add(percent);
            player.sendMessage(Text.of("[Command Block Database Searcher] Processed "+percent+"% of positions."));
            System.out.println("[Command Block Database Searcher] Processed "+percent+"% of positions.");
        }

        // Schedule the next batch
        server.execute(() -> processPositions(world, positions, endIndex, player));
    }

    //if (command.startsWith("/")) command = command.substring(1);
}
