package net.mat0u5.do2manager.events;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.world.CommandBlockData;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.CommandBlockExecutor;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class CommandBlockEvents {
    public static void onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        if (!world.isClient) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.getItem() == Blocks.COMMAND_BLOCK.asItem() ||
                itemStack.getItem() == Blocks.CHAIN_COMMAND_BLOCK.asItem() ||
                itemStack.getItem() == Blocks.REPEATING_COMMAND_BLOCK.asItem()) {
                BlockPos posPlace = hitResult.getBlockPos().offset(hitResult.getSide());
                onCommandBlockPlaced(player, posPlace, Block.getBlockFromItem(itemStack.getItem()));
            }
        }
    }
    public static void onCommandBlockPlaced(PlayerEntity player, BlockPos pos, Block block) {
        player.getServer().execute(() -> {
            try {
                if (block instanceof CommandBlock) {
                    CommandBlockBlockEntity blockEntity = (CommandBlockBlockEntity) player.getWorld().getBlockEntity(pos);
                    if (blockEntity != null) {
                        CommandBlockExecutor executor = blockEntity.getCommandExecutor();
                        String command = executor.getCommand();
                        if (command.startsWith("/")) command = command.substring(1);
                        String type = block == Blocks.COMMAND_BLOCK ? "Impulse" : block == Blocks.CHAIN_COMMAND_BLOCK ? "Chain" : "Repeating";
                        boolean conditional = blockEntity.isConditionalCommandBlock();
                        boolean auto = blockEntity.isAuto();
                        CommandBlockData data = new CommandBlockData(pos.getX(), pos.getY(), pos.getZ(), type, conditional, auto, command);
                        DatabaseManager.addCommandBlock(data); // Insert the new command block data into the database
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to add command block at "+pos.toString());
            }
        });
    }
    public static void onCommandBlockBroken(PlayerEntity player, BlockPos pos, Block block) {
        if (block instanceof CommandBlock) {
            DatabaseManager.removeCommandBlock(pos); // Remove the command block data from the database
        }
    }
    public static CommandBlockData getCommandBlockData(BlockPos pos, CommandBlockBlockEntity blockEntity) {
        String command = blockEntity.getCommandExecutor().getCommand();
        String type = blockEntity.getCachedState().getBlock() == Blocks.COMMAND_BLOCK ? "Impulse"
                : blockEntity.getCachedState().getBlock() == Blocks.CHAIN_COMMAND_BLOCK ? "Chain" : "Repeating";
        boolean conditional = blockEntity.isConditionalCommandBlock();
        boolean auto = blockEntity.isAuto();

        return new CommandBlockData(pos.getX(), pos.getY(), pos.getZ(), type, conditional, auto, command);
    }
}
