package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.TextUtils;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {

    @Inject(method = "sendChatMessage(Lnet/minecraft/network/message/SentMessage;ZLnet/minecraft/network/message/MessageType$Parameters;)V",
            at = @At("HEAD"), cancellable = true)
    public void onSendChatMessage(SentMessage message, boolean filterMaskEnabled, MessageType.Parameters params, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        PlayerManager playerManager = player.server.getPlayerManager();

        Text originalText = message.getContent();
        String originalContent = originalText.getString();
        if (!originalContent.contains(":")) return;
        String formattedContent = TextUtils.replaceEmotes(originalContent);

        if (!originalContent.equals(formattedContent)) {
            Text playerNameWithFormatting = player.getDisplayName();
            Text censoredMessageText = Text.literal(formattedContent).setStyle(originalText.getStyle());
            Text finalMessage = Text.empty().append("<").append(playerNameWithFormatting).append("> ").append(censoredMessageText);
            OtherUtils.broadcastMessage(player.getServer(),finalMessage);
            ci.cancel();
        }
    }
}