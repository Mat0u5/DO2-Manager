package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.events.CommandBlockEvents;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.TextUtils;
import net.mat0u5.do2manager.world.CommandBlockData;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.network.packet.c2s.play.UpdateCommandBlockC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Inject(method = "handleDecoratedMessage(Lnet/minecraft/network/message/SignedMessage;)V",
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
            DiscordUtils.sendMessageToDiscord(TextUtils.formatEmotesForDiscord(originalContent),"[Server] "+player.getNameForScoreboard(),"https://mc-heads.net/avatar/"+player.getUuidAsString());
            ci.cancel();
        }
    }

    @Inject(method = "onUpdateCommandBlock", at = @At("HEAD"))
    private void onUpdateCommandBlock(UpdateCommandBlockC2SPacket packet, CallbackInfo ci) {
        ServerWorld world = ((ServerPlayNetworkHandler) (Object) this).getPlayer().getServerWorld();
        BlockPos pos = packet.getPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof CommandBlockBlockEntity) {
            String type = packet.getType().toString();
            type = type.replaceAll("REDSTONE","Impulse").replaceAll("AUTO","Repeating").replaceAll("SEQUENCE","Chain");
            CommandBlockData data = new CommandBlockData(pos.getX(),pos.getY(),pos.getZ(),type,packet.isConditional(),packet.isAlwaysActive(),packet.getCommand());
            // Update the database with the new command block data
            DatabaseManager.updateCommandBlock(data);
        }
    }
}
