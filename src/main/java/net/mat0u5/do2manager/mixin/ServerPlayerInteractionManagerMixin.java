package net.mat0u5.do2manager.mixin;

import net.minecraft.block.*;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    private static final List<BlockPos> allowedPositions = List.of(
            new BlockPos(-564, 116, 1980),
            new BlockPos(-561, 116, 1985),
            new BlockPos(-557, 116, 1985),
            new BlockPos(-553, 114, 1983),
            new BlockPos(-489, 112, 1971),
            new BlockPos(-489, 112, 1975)
    );

    @Inject(method = "interactBlock", at = @At("HEAD"), cancellable = true)
    private void onInteractBlock(ServerPlayerEntity player, World world, ItemStack stack, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
        if (player.hasPermissionLevel(2)) return;
        if (player.isCreative()) return;

        BlockState state = world.getBlockState(hitResult.getBlockPos());
        Block block = state.getBlock();
        if (block instanceof RepeaterBlock || block instanceof ComparatorBlock || block instanceof RedstoneWireBlock || block instanceof NoteBlock) {
            if (allowedPositions.contains(hitResult.getBlockPos())) return;
            cir.setReturnValue(ActionResult.FAIL);
        }
    }
}