package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.world.FakeSign;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(SignBlockEntity.class)
public class SignBlockEntityMixin {

    @Inject(method = "setFrontText", at = @At("HEAD"))
    private void onSetFrontText(SignText frontText, CallbackInfoReturnable<Void> cir) {
        this.onSetText(frontText, cir);
    }

    @Inject(method = "setBackText", at = @At("HEAD"))
    private void onSetBackText(SignText backText, CallbackInfoReturnable<Void> cir) {
        this.onSetText(backText, cir);
    }

    private void onSetText(SignText signText, CallbackInfoReturnable<Void> cir) {
        SignBlockEntity sign = (SignBlockEntity) (Object) this;
        FakeSign.onSignUpdate(signText, cir, sign);
    }
}