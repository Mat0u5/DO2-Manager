package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.events.Events;
import net.mat0u5.do2manager.events.PlayerEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public abstract class ScreenHandlerMixin {
    @Inject(method = "clicked", at = @At("HEAD"), cancellable = true)
    public void onSlotClick(int slotId, int button, ClickType actionType, Player player, CallbackInfo ci) {
        PlayerEvents.onSlotClick(slotId,button,actionType,player,ci,((AbstractContainerMenu) (Object) this));
    }

    @Inject(method = "removed(Lnet/minecraft/world/entity/player/Player;)V", at = @At("HEAD"))
    public void onClose(Player player, CallbackInfo ci) {
        if (!Main.openGuis.containsKey(player)) return;
        Main.openGuis.get(player).invOpen = false;
    }
}