package net.mat0u5.do2manager.events;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.world.CommandBlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import java.util.HashMap;
import java.util.Map;

public class CommandBlockEvents {
    public static void onBlockUse(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        if (!world.isClientSide()) {
            ItemStack itemStack = player.getItemInHand(hand);
            if (itemStack.getItem() == Blocks.COMMAND_BLOCK.asItem() ||
                itemStack.getItem() == Blocks.CHAIN_COMMAND_BLOCK.asItem() ||
                itemStack.getItem() == Blocks.REPEATING_COMMAND_BLOCK.asItem()) {
                BlockPos posPlace = hitResult.getBlockPos().relative(hitResult.getDirection());
                onCommandBlockPlaced(player, posPlace, Block.byItem(itemStack.getItem()));
            }
        }
    }
    public static void onCommandBlockPlaced(Player player, BlockPos pos, Block block) {
        player.level().getServer().execute(() -> {
            try {
                if (block instanceof CommandBlock) {
                    CommandBlockEntity blockEntity = (CommandBlockEntity) player.level().getBlockEntity(pos);
                    if (blockEntity != null) {
                        BaseCommandBlock executor = blockEntity.getCommandBlock();
                        String command = executor.getCommand();
                        if (command.startsWith("/")) command = command.substring(1);
                        String type = block == Blocks.COMMAND_BLOCK ? "Impulse" : block == Blocks.CHAIN_COMMAND_BLOCK ? "Chain" : "Repeating";
                        boolean conditional = blockEntity.isConditional();
                        boolean auto = blockEntity.isAutomatic();
                        CommandBlockData data = new CommandBlockData(pos.getX(), pos.getY(), pos.getZ(), type, conditional, auto, command);
                        DatabaseManager.addCommandBlock(data); // Insert the new command block data into the database
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to add command block at "+pos.toString());
            }
        });
    }
    public static void onCommandBlockBroken(Player player, BlockPos pos, Block block) {
        if (block instanceof CommandBlock) {
            DatabaseManager.removeCommandBlock(pos); // Remove the command block data from the database
        }
    }
    public static CommandBlockData getCommandBlockData(BlockPos pos, CommandBlockEntity blockEntity) {
        String command = blockEntity.getCommandBlock().getCommand();
        String type = blockEntity.getBlockState().getBlock() == Blocks.COMMAND_BLOCK ? "Impulse"
                : blockEntity.getBlockState().getBlock() == Blocks.CHAIN_COMMAND_BLOCK ? "Chain" : "Repeating";
        boolean conditional = blockEntity.isConditional();
        boolean auto = blockEntity.isAutomatic();

        return new CommandBlockData(pos.getX(), pos.getY(), pos.getZ(), type, conditional, auto, command);
    }
}
