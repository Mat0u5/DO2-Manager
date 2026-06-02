package net.mat0u5.do2manager.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.NoteBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerInteractionManagerMixin {
    private static final List<BlockPos> allowedPositions = List.of(
            new BlockPos(-564, 116, 1980),
            new BlockPos(-561, 116, 1985),
            new BlockPos(-557, 116, 1985),
            new BlockPos(-553, 114, 1983),
            new BlockPos(-489, 112, 1971),
            new BlockPos(-489, 112, 1975)
    );

    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void onInteractBlock(ServerPlayer player, Level world, ItemStack stack, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.level().getServer().getPlayerList().isOp(player.nameAndId())) return;
        if (player.isCreative()) return;

        BlockState state = world.getBlockState(hitResult.getBlockPos());
        Block block = state.getBlock();
        if (block instanceof RepeaterBlock || block instanceof ComparatorBlock || block instanceof RedStoneWireBlock || block instanceof NoteBlock) {
            if (allowedPositions.contains(hitResult.getBlockPos())) return;
            cir.setReturnValue(InteractionResult.FAIL);
        }
    }
}