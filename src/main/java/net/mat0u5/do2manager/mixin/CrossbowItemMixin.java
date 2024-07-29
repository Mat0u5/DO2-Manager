package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.events.CrossbowEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void onCrossbowUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        CrossbowEvents.onCrossbowUse(world, user, hand, cir);
    }
    @Inject(method = "loadProjectiles", at = @At("RETURN"))
    private static void onLoadFinish(LivingEntity user, ItemStack projectile, CallbackInfoReturnable<Boolean> cir) {
        CrossbowEvents.onLoadFinish(user, projectile, cir);
    }
    @Inject(method = "shoot", at = @At("HEAD"), cancellable = true)
    private static void onShoot(World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed, float divergence, float simulated, CallbackInfo ci) {
        CrossbowEvents.onShoot(world, shooter, hand, crossbow, ci);
    }
}