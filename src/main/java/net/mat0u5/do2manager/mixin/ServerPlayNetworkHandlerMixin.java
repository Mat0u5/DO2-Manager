package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.TextUtils;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Inject(method = "handleDecoratedMessage(Lnet/minecraft/network/message/SentMessage;Lnet/minecraft/network/message/MessageType$Parameters;)V",
            at = @At("HEAD"), cancellable = true)
    private void onHandleDecoratedMessage(SignedMessage message, CallbackInfo ci) {
        ServerPlayNetworkHandler handler = (ServerPlayNetworkHandler) (Object) this;
        ServerPlayerEntity player = handler.player;
        Text originalText = message.getContent();
        String originalContent = originalText.getString();

        if (!originalContent.contains(":")) return;
        String formattedContent = TextUtils.replaceEmotes(originalContent);

        if (!originalContent.equals(formattedContent)) {
            Text playerNameWithFormatting = player.getDisplayName();
            Text formattedContentText = Text.literal(formattedContent).setStyle(originalText.getStyle());
            Text finalMessage = Text.empty().append("<").append(playerNameWithFormatting).append("> ").append(formattedContentText);

            OtherUtils.broadcastMessage(player.getServer(), finalMessage);
            ci.cancel();
        }
    }
}
