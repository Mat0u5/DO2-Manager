package net.mat0u5.do2manager.world;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.TicketType;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static net.mat0u5.do2manager.Main.server;

public class BlockScanner {
    public static List<Integer> percentCompleted = new ArrayList<>();
    public static String scanType = "";
    public static String blockPassword = "";
    public static int lockOrUnlock=0;
    public static int listPos = 0;

    public static Integer positionsToCheckInt;
    public static ServerLevel world = null;
    public static Player player = null;
    public static ServerChunkCache chunkManager;
    public static final Set<Block> lockableBlocks = Set.of(
            Blocks.CHEST, Blocks.HOPPER, Blocks.TRAPPED_CHEST,
            Blocks.DISPENSER, Blocks.DROPPER, Blocks.FURNACE,
            Blocks.BARREL, Blocks.SMOKER, Blocks.BLAST_FURNACE
    );
    public static final Set<Block> commandBlocks = Set.of(
            Blocks.COMMAND_BLOCK, Blocks.CHAIN_COMMAND_BLOCK, Blocks.REPEATING_COMMAND_BLOCK
    );
    public static Integer minX;
    public static Integer minY;
    public static Integer minZ;
    public static Integer maxX;
    public static Integer maxY;
    public static Integer maxZ;
    public static List<CommandBlockData> commandBlocksList = new ArrayList<>();
    public static boolean running = false;


    public static void scanArea(String scanFor, ServerLevel world, BlockPos startPos, BlockPos endPos, Player player) {
        BlockScanner.world = world;
        BlockScanner.player = player;
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
        chunkManager = world.getChunkSource();

        start();
    }
    private static void start() {
        running = true;
    }

    public static void onTickEnd() {
        if (server == null || !running) {
            return;
        }
        // Check the server's MSPT
        float currentMSPT = server.getCurrentSmoothedTickTime();

        // Adjust the workload based on current MSPT
        if (currentMSPT < 45) {
            complexFunction();
        }
    }

    protected static void complexFunction() {
        if (!running || positionsToCheckInt == null) {
            return;
        }
        if (listPos >= positionsToCheckInt) {
            System.out.println("Stopping...");
            running = false;
            stoppedFunction();
        }
        else {
            int batchSize = 1_000_000;
            int batchEndPos = Math.min(listPos + batchSize, positionsToCheckInt);
            for (int i = listPos; i < batchEndPos; i++) {
                processPosition(i);
            }

            listPos = batchEndPos;

            if ((listPos % (positionsToCheckInt / 100.0))%25 == 0 && listPos != 0) {
                int percent = listPos / (positionsToCheckInt / 100);
                if (!percentCompleted.contains(percent) ) {
                    percentCompleted.add(percent);
                    player.displayClientMessage(Component.nullToEmpty("[Block Database Searcher] Processed " + percent + "% of positions."), false);
                    if (scanType.contains("lock")) player.displayClientMessage(Component.nullToEmpty("-Modified " + lockOrUnlock + " blocks."), false);
                    System.out.println("[Block Database Searcher] Processed " + percent + "% of positions.");
                }
            }

            if (!commandBlocksList.isEmpty()) {
                DatabaseManager.addCommandBlocks(commandBlocksList);
                commandBlocksList.clear();
            }
        }
    }
    private static void processPosition(int posIndex) {
        int x = minX + (posIndex % (maxX - minX + 1));
        int y = minY + ((posIndex / (maxX - minX + 1)) % (maxY - minY + 1));
        int z = minZ + (posIndex / ((maxX - minX + 1) * (maxY - minY + 1)));
        BlockPos pos = new BlockPos(x, y, z);

        ChunkPos chunkPos = new ChunkPos(pos);
        if (!chunkManager.hasChunk(chunkPos.x, chunkPos.z)) {
            System.out.println("Loading Chunk");
            chunkManager.addTicketWithRadius(TicketType.FORCED, chunkPos, 1);
            world.getChunk(chunkPos.x, chunkPos.z);
        }

        BlockState blockState = world.getBlockState(pos);
        if (blockState.isAir()) return;
        Block block = blockState.getBlock();

        if (scanType.equalsIgnoreCase("command_block")) {
            processCommandBlockPos(block,pos);
        } else if (scanType.contains("lock")) {
            processContainerBlockPos(block,pos);
        }
    }
    private static void processCommandBlockPos(Block block, BlockPos pos) {
        if (!commandBlocks.contains(block)) return;
        CommandBlockEntity commandBlockEntity = (CommandBlockEntity) world.getBlockEntity(pos);
        if (commandBlockEntity == null) return;
        BaseCommandBlock executor = commandBlockEntity.getCommandBlock();
        String command = executor.getCommand();
        if (command.startsWith("/")) command = command.substring(1);
        String type = block == Blocks.COMMAND_BLOCK ? "Impulse" : block == Blocks.CHAIN_COMMAND_BLOCK ? "Chain" : "Repeating";
        boolean conditional = commandBlockEntity.isConditional();
        boolean auto = commandBlockEntity.isAutomatic();
        commandBlocksList.add(new CommandBlockData(pos.getX(), pos.getY(), pos.getZ(), type, conditional, auto, command));
    }
    private static void processContainerBlockPos(Block block, BlockPos pos) {
        if (!lockableBlocks.contains(block) && !block.asItem().toString().contains("shulker_box")) return;
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity == null) return;
        HolderLookup.Provider registryLookup = world.getServer().registryAccess();
        CompoundTag nbt = blockEntity.saveWithoutMetadata(registryLookup);
        if (scanType.equalsIgnoreCase("unlock")) {
            if (nbt.contains("lock")) {
                nbt.remove("lock");
                blockEntity.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, registryLookup, nbt));
                blockEntity.setChanged();
                world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                lockOrUnlock++;
            }
        }
        else {
            boolean needsLockUpdate = true;

            if (nbt.contains("lock")) {
                Optional<CompoundTag> lockTag = nbt.getCompound("lock");
                if (lockTag.isPresent()) {
                    Optional<CompoundTag> componentsTag = lockTag.get().getCompound("components");
                    if (componentsTag.isPresent()) {
                        Optional<String> currentLockName = componentsTag.get().getString("minecraft:item_name");
                        if (currentLockName.isPresent() && currentLockName.get().equalsIgnoreCase(blockPassword)) {
                            needsLockUpdate = false;
                        }
                    }
                }
            }

            if (needsLockUpdate) {
                CompoundTag lockComponent = new CompoundTag();
                CompoundTag componentsSubTag = new CompoundTag();

                componentsSubTag.putString("minecraft:item_name", blockPassword);
                lockComponent.put("components", componentsSubTag);

                nbt.put("lock", lockComponent);

                blockEntity.loadWithComponents(net.minecraft.world.level.storage.TagValueInput.create(net.minecraft.util.ProblemReporter.DISCARDING, registryLookup, nbt));
                blockEntity.setChanged();
                world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
                lockOrUnlock++;
            }
        }
    }

    protected static void stoppedFunction() {
        player.displayClientMessage(Component.nullToEmpty("§aBlock scan complete."), false);
        System.out.println("Block scan complete.");
        if (scanType.equalsIgnoreCase("command_block")) {
            addCommandBlockData();
        }
        else if (scanType.contains("lock")) {
            player.displayClientMessage(Component.nullToEmpty("-Modified " + lockOrUnlock + " blocks."), false);
        }
    }
    private static void addCommandBlockData() {
        player.displayClientMessage(Component.nullToEmpty("§aSaving Data to database."), false);
        if (!commandBlocksList.isEmpty()) DatabaseManager.addCommandBlocks(commandBlocksList);
        player.displayClientMessage(Component.nullToEmpty("§aData saved."), false);
    }
}
