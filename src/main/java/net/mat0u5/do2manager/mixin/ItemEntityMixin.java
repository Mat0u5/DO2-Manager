package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.events.Events;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	@Inject(method = "onPlayerCollision", at = @At("HEAD"))
	private void onPlayerCollision(PlayerEntity player, CallbackInfo ci) {
		Events.onPlayerPickupItem(player, (ItemEntity)(Object)this);
	}
}