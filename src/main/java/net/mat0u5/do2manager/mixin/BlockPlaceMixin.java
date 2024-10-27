package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.blockblocker.BlockBlocker;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.Registries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public class BlockPlaceMixin {
    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;Lnet/minecraft/block/BlockState;)Z", at = @At("HEAD"), cancellable = true)
    private void place(ItemPlacementContext context, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        String id = Registries.BLOCK.getId(state.getBlock()).toString();
        if(!context.getWorld().isClient() && BlockBlocker.config.general.noPlace.contains(id)) {
            cir.setReturnValue(false);
        }
    }
}
