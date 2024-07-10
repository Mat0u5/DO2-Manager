package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.events.Events;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public abstract class ScreenHandlerMixin {
    @Inject(method = "onSlotClick", at = @At("HEAD"), cancellable = true)
    public void onSlotClick(int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        Events.onSlotClick(slotId,button,actionType,player,ci,((ScreenHandler) (Object) this));
    }

    @Inject(method = "onClosed(Lnet/minecraft/entity/player/PlayerEntity;)V", at = @At("HEAD"))
    public void onClose(PlayerEntity player, CallbackInfo ci) {
        if (!Main.openGuis.containsKey(player)) return;
        Main.openGuis.get(player).invOpen = false;
    }
}