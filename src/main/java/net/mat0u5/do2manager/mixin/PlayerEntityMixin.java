package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.events.PlayerEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Inventory.class)
public class PlayerEntityMixin {
	@Inject(method = "removeFromSelected", at = @At("HEAD"), cancellable = true)
	private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
		Inventory inventory = (Inventory) (Object) this;
		Player player = inventory.player;
		if (player instanceof ServerPlayer) {
			ItemStack droppedStack = player.getInventory().getSelectedItem().copy();
			PlayerEvents.onPlayerDropItem((ServerPlayer) player, droppedStack);
		}
	}
}