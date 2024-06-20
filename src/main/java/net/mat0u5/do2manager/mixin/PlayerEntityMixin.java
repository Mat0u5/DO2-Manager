package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.events.Events;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class PlayerEntityMixin {
	@Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
	private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<ItemStack> cir) {
		PlayerInventory inventory = (PlayerInventory) (Object) this;
		PlayerEntity player = inventory.player;
		if (player instanceof ServerPlayerEntity) {
			ItemStack droppedStack = player.getInventory().getMainHandStack().copy();
			Events.onPlayerDropItem((ServerPlayerEntity) player, droppedStack);
		}
	}
}
