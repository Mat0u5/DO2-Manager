package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.events.CrossbowEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void onCrossbowUse(Level level, Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        CrossbowEvents.onCrossbowUse(level, player, interactionHand, cir);
    }
    @Inject(method = "tryLoadProjectiles", at = @At("RETURN"))
    private static void onLoadFinish(LivingEntity user, ItemStack projectile, CallbackInfoReturnable<Boolean> cir) {
        CrossbowEvents.onLoadFinish(user, projectile, cir);
    }
    @Inject(method = "shootProjectile", at = @At("HEAD"), cancellable = true)
    private static void onShoot(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target, CallbackInfo ci) {
        CrossbowEvents.onShoot(shooter.level(), shooter);

    }
}