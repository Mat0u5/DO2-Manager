package net.mat0u5.do2manager.events;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.world.ItemManager;
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

    public static final Identifier GUNSHOT_ID = Identifier.of("minecraft", "do2.weapon.gunshot");
    public static final SoundEvent GUNSHOT = SoundEvent.of(GUNSHOT_ID);
    public static final Identifier GUNLOAD_ID = Identifier.of("minecraft", "do2.weapon.gun_loading");
    public static final SoundEvent GUNLOAD = SoundEvent.of(GUNLOAD_ID);
    public static final Identifier GUNLOAD_FINISH_ID = Identifier.of("minecraft", "do2.weapon.gun_load_fin");
    public static final SoundEvent GUNLOAD_FINISH = SoundEvent.of(GUNLOAD_FINISH_ID);

    public static void onCrossbowUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<TypedActionResult<ItemStack>> cir) {
        ItemStack crossbow = user.getStackInHand(hand);
        int modelData = ItemManager.getModelData(crossbow);
        if (modelData == 1) {
            if (!world.isClient) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), GUNLOAD, SoundCategory.PLAYERS, 0.8F, 1.0F);
            }
        }
    }
    public static void onLoadFinish(LivingEntity user, ItemStack projectile, CallbackInfoReturnable<Boolean> cir) {
        ItemStack crossbow = user.getActiveItem();
        int modelData = ItemManager.getModelData(crossbow);
        if (modelData == 1) {
            if (!user.getWorld().isClient) {
                user.getWorld().playSound(null, user.getX(), user.getY(), user.getZ(), GUNLOAD_FINISH, SoundCategory.PLAYERS, 0.8F, 1.0F);
            }
        }
    }
    public static void onShoot(World world, LivingEntity user) {
        ItemStack crossbow = user.getMainHandStack();
        ItemStack crossbow2 = user.getOffHandStack();
        int modelData = ItemManager.getModelData(crossbow);
        int modelData2 = ItemManager.getModelData(crossbow2);
        if (modelData == 1 || modelData2 == 1) {
            if (!world.isClient) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), GUNSHOT, SoundCategory.PLAYERS, 0.8F, 1.0F);
            }
        }
    }
}
