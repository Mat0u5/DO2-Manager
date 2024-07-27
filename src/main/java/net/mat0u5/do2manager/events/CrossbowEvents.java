package net.mat0u5.do2manager.events;

import net.mat0u5.do2manager.Main;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class CrossbowEvents {

    public static final Identifier GUNSHOT_ID = new Identifier("minecraft", "gunshot");
    public static final SoundEvent GUNSHOT = SoundEvent.of(GUNSHOT_ID);
    public static final Identifier GUNLOAD_ID = new Identifier("minecraft", "gunload");
    public static final SoundEvent GUNLOAD = SoundEvent.of(GUNLOAD_ID);
    public static final Identifier GUNLOAD_FINISH_ID = new Identifier("minecraft", "gunload_finish");
    public static final SoundEvent GUNLOAD_FINISH = SoundEvent.of(GUNLOAD_FINISH_ID);

    public static void onCrossbowUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack crossbow = user.getStackInHand(hand);
        if (crossbow.hasNbt() && crossbow.getNbt().contains("CustomModelData") && crossbow.getNbt().getInt("CustomModelData") == 1) {
            if (!world.isClient) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), GUNLOAD, SoundCategory.PLAYERS, 0.8F, 1.0F);
            }
        }
    }
    public static void onLoadFinish(LivingEntity user, ItemStack projectile, CallbackInfoReturnable<Boolean> cir) {
        ItemStack crossbow = user.getActiveItem();
        if (crossbow.hasNbt() && crossbow.getNbt().contains("CustomModelData") && crossbow.getNbt().getInt("CustomModelData") == 1) {
            if (!user.getWorld().isClient) {
                user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), GUNLOAD_FINISH, SoundCategory.PLAYERS, 0.8F, 1.0F);
            }
        }
    }
    public static void onShoot(World world, LivingEntity user, Hand hand, ItemStack crossbow, CallbackInfo cir) {
        if (crossbow.hasNbt() && crossbow.getNbt().contains("CustomModelData") && crossbow.getNbt().getInt("CustomModelData") == 1) {
            if (!world.isClient) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), GUNSHOT, SoundCategory.PLAYERS, 0.8F, 1.0F);
            }
        }
    }
}
