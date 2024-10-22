package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.MSPTUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.server.world.ServerChunkManager;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BlockScanner extends MSPTUtils {
    List<Integer> percentCompleted = new ArrayList<>();
    String scanType = "";
    String blockPassword = "";
    int lockOrUnlock=0;
    int listPos=0;

    Integer positionsToCheckInt;
    ServerWorld world = null;
    PlayerEntity player = null;
    ServerChunkManager chunkManager;
    final Set<Block> lockableBlocks = Set.of(
            Blocks.CHEST, Blocks.HOPPER, Blocks.TRAPPED_CHEST,
            Blocks.DISPENSER, Blocks.DROPPER, Blocks.FURNACE,
            Blocks.BARREL, Blocks.SMOKER, Blocks.BLAST_FURNACE
    );
    final Set<Block> commandBlocks = Set.of(
            Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK
    );
    Integer minX;
    Integer minY;
    Integer minZ;
    Integer maxX;
    Integer maxY;
    Integer maxZ;
    List<CommandBlockData> commandBlocksList = new ArrayList<>();


    public void scanArea(String scanFor, ServerWorld world, BlockPos startPos, BlockPos endPos, PlayerEntity player) {
        this.world = world;
        this.player = player;
        listPos = 0;
        commandBlocksList.clear();
        percentCompleted = new ArrayList<>();
        scanType = scanFor;
        lockOrUnlock = 0;
        if (scanFor.equalsIgnoreCase("lock")) blockPassword = Main.config.getProperty("block_password");

        minX = Math.min(startPos.getX(), endPos.getX());
        maxX = Math.max(startPos.getX(), endPos.getX());
        minY = Math.min(startPos.getY(), endPos.getY());
        maxY = Math.max(startPos.getY(), endPos.getY());
        minZ = Math.min(startPos.getZ(), endPos.getZ());
        maxZ = Math.max(startPos.getZ(), endPos.getZ());

        positionsToCheckInt = (maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1);
        chunkManager = world.getChunkManager();

        startBoosted(Main.server);
    }

    @Override
    protected void complexFunction() {
        if (!running) {
            return;
        }
        if (listPos >= positionsToCheckInt) {
            System.out.println("Stopping...");
            stop();
        }
        else {
            int batchSize = 10_000;
            int batchEndPos = Math.min(listPos + batchSize, positionsToCheckInt);
            for (int i = listPos; i < batchEndPos; i++) {
                processPosition(i);
            }

            listPos = batchEndPos;

            if ((listPos % (positionsToCheckInt / 100.0))%25 == 0 && listPos != 0) {
                int percent = listPos / (positionsToCheckInt / 100);
                if (!percentCompleted.contains(percent) ) {
                    percentCompleted.add(percent);
                    player.sendMessage(Text.of("[Block Database Searcher] Processed " + percent + "% of positions."));
                    if (scanType.contains("lock")) player.sendMessage(Text.of("-Modified " + lockOrUnlock + " blocks."));
                    System.out.println("[Block Database Searcher] Processed " + percent + "% of positions.");
                }
            }

            if (!commandBlocksList.isEmpty()) {
                DatabaseManager.addCommandBlocks(commandBlocksList);
                commandBlocksList.clear();
            }
        }
    }
    private void processPosition(int posIndex) {
        int x = minX + (posIndex % (maxX - minX + 1));
        int y = minY + ((posIndex / (maxX - minX + 1)) % (maxY - minY + 1));
        int z = minZ + (posIndex / ((maxX - minX + 1) * (maxY - minY + 1)));
        BlockPos pos = new BlockPos(x, y, z);

        ChunkPos chunkPos = new ChunkPos(pos);
        if (!chunkManager.isChunkLoaded(chunkPos.x, chunkPos.z)) {
            System.out.println("Loading Chunk");
            chunkManager.addTicket(ChunkTicketType.FORCED, chunkPos, 1, chunkPos);
            world.getChunk(chunkPos.x, chunkPos.z);
        }

        Block block = world.getBlockState(pos).getBlock();

        if (scanType.equalsIgnoreCase("command_block")) {
            processCommandBlockPos(block,pos);
        } else if (scanType.contains("lock")) {
            processContainerBlockPos(block,pos);
        }
    }
    private void processCommandBlockPos(Block block, BlockPos pos) {
        if (!commandBlocks.contains(block)) return;
        CommandBlockBlockEntity commandBlockEntity = (CommandBlockBlockEntity) world.getBlockEntity(pos);
        if (commandBlockEntity == null) return;
        CommandBlockExecutor executor = commandBlockEntity.getCommandExecutor();
        String command = executor.getCommand();
        if (command.startsWith("/")) command = command.substring(1);
        String type = block == Blocks.COMMAND_BLOCK ? "Impulse" : block == Blocks.CHAIN_COMMAND_BLOCK ? "Chain" : "Repeating";
        boolean conditional = commandBlockEntity.isConditionalCommandBlock();
        boolean auto = commandBlockEntity.isAuto();
        commandBlocksList.add(new CommandBlockData(pos.getX(), pos.getY(), pos.getZ(), type, conditional, auto, command));
    }
    private void processContainerBlockPos(Block block, BlockPos pos) {
        if (!lockableBlocks.contains(block) && !block.asItem().toString().contains("shulker_box")) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) return;
        RegistryWrapper.WrapperLookup registryLookup = world.getServer().getRegistryManager();
        NbtCompound nbt = blockEntity.createNbt(registryLookup);
        if (scanType.equalsIgnoreCase("unlock")) {
            if (nbt.contains("Lock")) {
                nbt.remove("Lock");
                blockEntity.read(nbt,registryLookup);
                blockEntity.markDirty();
                world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                lockOrUnlock++;
            }
        }
        else {
            if (!nbt.contains("Lock") || !nbt.getString("Lock").equalsIgnoreCase(blockPassword)) {
                nbt.putString("Lock", blockPassword);
                blockEntity.read(nbt,registryLookup);
                blockEntity.markDirty();
                world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                lockOrUnlock++;
            }
        }
    }

    @Override
    protected void stoppedFunction() {
        player.sendMessage(Text.of("§aBlock scan complete."));
        System.out.println("Block scan complete.");
        if (scanType.equalsIgnoreCase("command_block")) {
            addCommandBlockData();
        }
        else if (scanType.contains("lock")) {
            player.sendMessage(Text.of("-Modified " + lockOrUnlock + " blocks."));
        }
    }
    private void addCommandBlockData() {
        player.sendMessage(Text.of("§aSaving Data to database."));
        if (!commandBlocksList.isEmpty()) DatabaseManager.addCommandBlocks(commandBlocksList);
        player.sendMessage(Text.of("§aData saved."));
    }
}
