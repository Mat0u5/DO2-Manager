package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.blockblocker.BlockBlocker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockPlaceMixin {
    @Inject(method = "placeBlock(Lnet/minecraft/world/item/context/BlockPlaceContext;Lnet/minecraft/world/level/block/state/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void place(BlockPlaceContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        if(!context.getLevel().isClientSide() && BlockBlocker.config.general.noPlace.contains(id)) {
            cir.setReturnValue(false);
        }
    }
}
