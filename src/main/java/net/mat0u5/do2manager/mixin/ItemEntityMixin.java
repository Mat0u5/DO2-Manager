package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.events.Events;
import net.mat0u5.do2manager.events.PlayerEvents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	@Inject(method = "playerTouch", at = @At("HEAD"))
	private void onPlayerCollision(Player player, CallbackInfo ci) {
		PlayerEvents.onPlayerPickupItem(player, (ItemEntity)(Object)this);
	}
}