package net.mat0u5.do2manager.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.PermissionManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LockCode.class)
public class LockCodeMixin {
	@Inject(method = "canUnlock", at = @At("RETURN"), cancellable = true)
	private void stopUnlock(Player player, CallbackInfoReturnable<Boolean> cir) {
		LockCode lockCode = (LockCode) (Object) this;
		if (lockCode == LockCode.NO_LOCK) return;
		if (player instanceof ServerPlayer serverPlayer) {
			if (PermissionManager.isAdmin(player) || player.getStringUUID().equalsIgnoreCase("24268497-6a56-4132-8699-8d956dfd062d") // GGGregian special perms
			) {
				cir.setReturnValue(true);
				serverPlayer.connection
						.send(
								new ClientboundSoundPacket(
										BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.AMETHYST_BLOCK_STEP), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 0.7f, 1.0f, player.getRandom().nextLong()
								)
						);
			}
			else if (cir.getReturnValue().equals(true) && !player.isSpectator()) {
				cir.setReturnValue(false);
				try {
					//OtherUtils.removeItemsFromPlayerInventory(player, lock);
					((ServerPlayer)player).setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
					((ServerPlayer)player).closeContainer();

					JsonObject json = DiscordUtils.getDefaultJSON();

					JsonObject embed = new JsonObject();
					embed.addProperty("description", "__**[DO2-Manager]**__" +
							"\n\n**"+player.getScoreboardName()+"** opened a locked container!" +
							"\n\n Lock Predicate: " + lockCode.predicate() +
							"\n\n Player Location: " + player.position()
					);
					embed.addProperty("color", 16711680);
					JsonArray embeds = new JsonArray();
					embeds.add(embed);
					json.add("embeds", embeds);

					DiscordUtils.sendMessageToDiscord(json, DiscordUtils.getWebhookStaffURL());
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
