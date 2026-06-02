package net.mat0u5.do2manager.events;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

public class CrossbowEvents {

    public static final ResourceLocation GUNSHOT_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "do2.weapon.gunshot");
    public static final SoundEvent GUNSHOT = SoundEvent.createVariableRangeEvent(GUNSHOT_ID);
    public static final ResourceLocation GUNLOAD_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "do2.weapon.gun_loading");
    public static final SoundEvent GUNLOAD = SoundEvent.createVariableRangeEvent(GUNLOAD_ID);
    public static final ResourceLocation GUNLOAD_FINISH_ID = ResourceLocation.fromNamespaceAndPath("minecraft", "do2.weapon.gun_load_fin");
    public static final SoundEvent GUNLOAD_FINISH = SoundEvent.createVariableRangeEvent(GUNLOAD_FINISH_ID);

    public static void onCrossbowUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        ItemStack crossbow = user.getItemInHand(hand);
        int modelData = ItemManager.getModelData(crossbow);
        if (modelData == 1) {
            if (!world.isClientSide) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), GUNLOAD, SoundSource.PLAYERS, 0.8F, 1.0F);
            }
        }
    }
    public static void onLoadFinish(LivingEntity user, ItemStack projectile, CallbackInfoReturnable<Boolean> cir) {
        ItemStack crossbow = user.getUseItem();
        int modelData = ItemManager.getModelData(crossbow);
        if (modelData == 1) {
            if (!user.level().isClientSide) {
                user.level().playSound(null, user.getX(), user.getY(), user.getZ(), GUNLOAD_FINISH, SoundSource.PLAYERS, 0.8F, 1.0F);
            }
        }
    }
    public static void onShoot(Level world, LivingEntity user) {
        ItemStack crossbow = user.getMainHandItem();
        ItemStack crossbow2 = user.getOffhandItem();
        int modelData = ItemManager.getModelData(crossbow);
        int modelData2 = ItemManager.getModelData(crossbow2);
        if (modelData == 1 || modelData2 == 1) {
            if (!world.isClientSide) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), GUNSHOT, SoundSource.PLAYERS, 0.8F, 1.0F);
            }
        }
    }
}
