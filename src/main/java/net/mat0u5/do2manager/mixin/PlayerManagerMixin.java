package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.TextUtils;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "broadcast", at = @At("HEAD"), cancellable = true)
    private void onBroadcast(Text message, boolean actionBar, CallbackInfo ci) {
        if (!message.getString().matches("§3\\[.+ on Discord§3\\] §r.+")) return;
        List<Text> siblings = message.getSiblings();
        String originalMessage = siblings.get(2).getString();
        if (siblings.size() != 3) return;
        String modifiedMessage = TextUtils.replaceEmotesDiscord(originalMessage);
        if (!originalMessage.equals(modifiedMessage)) {
            OtherUtils.broadcastMessage(Main.server, Text.translatable("§3[§r"+siblings.get(0).getString()).append(siblings.get(1)).append(Text.translatable(modifiedMessage)));
            ci.cancel();
        }
    }
}
