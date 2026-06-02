package net.mat0u5.do2manager.mixin;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.gui.ingamescreen.StatsViewer;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.TextUtils;
import net.mat0u5.do2manager.world.CommandBlockData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.protocol.game.ServerboundSetCommandBlockPacket;
import net.minecraft.network.protocol.game.ServerboundUseItemPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerPlayNetworkHandlerMixin {

    @Inject(method = "broadcastChatMessage(Lnet/minecraft/network/chat/PlayerChatMessage;)V",
            at = @At("HEAD"), cancellable = true)
    private void onHandleDecoratedMessage(PlayerChatMessage message, CallbackInfo ci) {
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = handler.player;
        Component originalText = message.decoratedContent();
        String originalContent = originalText.getString();
        if (!originalContent.contains(":")) return;
        String formattedContent = TextUtils.replaceEmotes(originalContent);

        if (!originalContent.equals(formattedContent)) {
            Component playerNameWithFormatting = player.getDisplayName();
            Component formattedContentText = Component.literal(formattedContent).setStyle(originalText.getStyle());
            Component finalMessage = Component.empty().append("<").append(playerNameWithFormatting).append("> ").append(formattedContentText);

            OtherUtils.broadcastMessage(player.getServer(), finalMessage);
            DiscordUtils.sendMessageToDiscord(TextUtils.formatEmotesForDiscord(originalContent),"[Server] "+player.getScoreboardName(),"https://mc-heads.net/avatar/"+player.getStringUUID());
            ci.cancel();
        }
    }

    @Inject(method = "handleSetCommandBlock", at = @At("HEAD"))
    private void onUpdateCommandBlock(ServerboundSetCommandBlockPacket packet, CallbackInfo ci) {
        ServerLevel world = ((ServerGamePacketListenerImpl) (Object) this).getPlayer().serverLevel();
        BlockPos pos = packet.getPos();
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof CommandBlockEntity) {
            String type = packet.getMode().toString();
            type = type.replaceAll("REDSTONE","Impulse").replaceAll("AUTO","Repeating").replaceAll("SEQUENCE","Chain");
            CommandBlockData data = new CommandBlockData(pos.getX(),pos.getY(),pos.getZ(),type,packet.isConditional(),packet.isAutomatic(),packet.getCommand());
            // Update the database with the new command block data
            DatabaseManager.updateCommandBlock(data);
        }
    }
    @Inject(method = "handleUseItem", at = @At("HEAD"))
    private void onPlayerAction(ServerboundUseItemPacket packet, CallbackInfo ci) {
        // Get the packet's player
        ServerGamePacketListenerImpl handler = (ServerGamePacketListenerImpl) (Object) this;
        ServerPlayer player = handler.player;
        StatsViewer.onPlayerUse(player);
    }
}
