package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.TextUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(PlayerList.class)
public class PlayerManagerMixin {

    @Inject(method = "broadcastSystemMessage", at = @At("HEAD"), cancellable = true)
    private void onBroadcast(Component message, boolean actionBar, CallbackInfo ci) {
        if (!message.getString().matches("<§9\\[Discord§9\\] .+> §r.+")) return;
        List<Component> siblings = message.getSiblings();
        String originalMessage = siblings.get(2).getString();
        if (siblings.size() != 3) return;
        String modifiedMessage = TextUtils.replaceEmotesDiscord(originalMessage);
        if (!originalMessage.equals(modifiedMessage)) {
            OtherUtils.broadcastMessage(Main.server, Component.translatable("<§9[Discord§9] "+siblings.get(0).getString()).append(siblings.get(1)).append(Component.translatable(modifiedMessage)));
            ci.cancel();
        }
    }
}
