package net.mat0u5.do2manager.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.gui.GuiInventoryClick;
import net.mat0u5.do2manager.queue.QueueEvents;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.PermissionManager;
import net.mat0u5.do2manager.world.ItemConvertor;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.do2manager.events.Events.clickEventCooldown;
import static net.mat0u5.do2manager.events.Events.lastPlayerLogoutTime;

public class PlayerEvents {
    static void onPlayerDisconnect(MinecraftServer server, ServerPlayer player) {
        try {
            QueueEvents.onPlayerLeave(player);
            if (OtherUtils.isServerEmptyOrOnlyTangoCam(server)) {//Last player disconnects
                lastPlayerLogoutTime = System.currentTimeMillis();
            }
            else {
                lastPlayerLogoutTime = -1;
            }
        }catch (Exception e) {}
    }
    static void onPlayerJoin(MinecraftServer server, ServerPlayer player) {
        try {
            QueueEvents.onPlayerJoin(player);
            if (player.isCreative() && !PermissionManager.isAdmin(player)) {
                player.setGameMode(GameType.SPECTATOR);
                System.out.println(player.getScoreboardName()+"'s gamemode was automatically reset to spectator, because they were in creative.");
            }

            //Add the player to the database
            DatabaseManager.addPlayer(player.getStringUUID(),player.getScoreboardName(), player.getGameProfile());
            if (Main.allPlayers.isEmpty()) {
                DatabaseManager.fetchAllPlayers();
            }
            else {
                Main.allPlayers.put(player.getStringUUID(),player.getScoreboardName());
            }
            lastPlayerLogoutTime = -1;

            //Item Conversions
            ItemConvertor.onPlayerJoin(player);
        }catch (Exception e) {}
    }
    static void onPlayerDeath(ServerPlayer player, DamageSource source) {
        MinecraftServer server = player.level().getServer();
        if (player.getMainHandItem().getItem() == Items.TOTEM_OF_UNDYING || player.getOffhandItem().getItem() == Items.TOTEM_OF_UNDYING) {
            return;
        }

        List<Player> runners = RunInfoParser.getCurrentRunners(server);
        if (runners.contains(player) && runners.size() == 1) {
            boolean diedFromPathOfCoward = player.position().distanceTo(new Vec3(-643, -18, 1977))<2;
            if (Main.currentRun.run_number != -1 && Main.currentRun.inventory_save.isEmpty() && !diedFromPathOfCoward) {
                Main.currentRun.inventory_save = ItemManager.getPlayerInventory(player);
            }
            Main.currentRun.death_pos = player.position().toString();
            Main.currentRun.death_message = source.getLocalizedDeathMessage(player).getString();
            if (diedFromPathOfCoward) {
                System.out.println(player.getScoreboardName() + " took the path of the coward. LLLL");
                Main.currentRun.finishers = new ArrayList<>();
                DatabaseManager.saveRun(server);
            }
        }
    }
    public static void onPlayerDropItem(ServerPlayer player, ItemStack itemStack) {
        invPickupOrDropItem(player,itemStack);
    }
    public static void onPlayerPickupItem(Player player, ItemEntity itemEntity) {
        if (itemEntity.hasPickUpDelay()) return;
        invPickupOrDropItem(player,itemEntity.getItem());
    }


    public static void invPickupOrDropItem(Player player, ItemStack itemStack) {
        try {
            if (!RunInfoParser.getCurrentRunners(player.level().getServer()).contains(player)) return;
            if (ItemManager.isDungeonCompass(itemStack) && Main.currentRun.compass_item == null) {
                Main.currentRun.compass_item = itemStack;

                List<Player> runners = RunInfoParser.getCurrentRunners(player.level().getServer());
                if (!runners.isEmpty()) {
                    boolean isSpeedrun = Main.config.getProperty("current_run_is_speedrun").equalsIgnoreCase("true");
                    if (runners.size() == 1 && isSpeedrun) RunInfoParser.getFastestPlayerRunMatchingCurrent(RunInfoParser.getCurrentRunners(player.level().getServer()).get(0));
                }
            }
            if (ItemManager.isDungeonArtifact(itemStack) && Main.currentRun.artifact_item == null) {
                if (ItemManager.getModelData(itemStack) == 36) {
                    OtherUtils.executeCommand(player.level().getServer(),"function dom:world/dungeon_functions/utilities/do2.map/other/mug_maniac_activate");
                }
                Main.currentRun.artifact_item = itemStack;
            }
        }catch(Exception e) {}
    }
    public static void onSlotClick(int slotId, int button, ClickType actionType, Player player, CallbackInfo ci, AbstractContainerMenu handler) {
        try {
            if (!handler.isValidSlotIndex(slotId)) return;
            if (slotId < 0 ) return;
            ItemStack clickedItem = handler.getSlot(slotId).getItem();
            if (clickedItem == null) return;
            if (OtherUtils.isHoldingAdminKey(player)) return;
            if (!ItemManager.hasCustomComponentEntry(clickedItem,"GUI")) return;
            if (!ItemManager.hasCustomComponentEntry(clickedItem,"GUI_DontCancelClick")) ci.cancel();
            String tag = ItemManager.getCustomComponentString(clickedItem, "GUI");
            if ((tag.equalsIgnoreCase("DatabaseGUI")||tag.equalsIgnoreCase("custom")) && clickEventCooldown <= 0) {
                clickEventCooldown = 4;
                GuiInventoryClick.onClickDatabaseGUI(tag,slotId,button,actionType,player,ci,handler);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static InteractionResult onBlockUse(Player player, Level world, InteractionHand hand, BlockHitResult hitResult) {
        CommandBlockEvents.onBlockUse(player,world,hand,hitResult);
        BlockPos pos = hitResult.getBlockPos();
        Block block = world.getBlockState(pos).getBlock();
        if (block == null) return InteractionResult.PASS;
        if (!(world.getBlockEntity(pos) instanceof BaseContainerBlockEntity)) return InteractionResult.PASS;
        BaseContainerBlockEntity container = (BaseContainerBlockEntity) world.getBlockEntity(pos);
        if (container == null) return InteractionResult.PASS;

        String lock = OtherUtils.getLock(container);
        if (lock == null || lock.isEmpty()) return InteractionResult.PASS;
        if (PermissionManager.isAdmin(player)
                || player.getStringUUID().equalsIgnoreCase("24268497-6a56-4132-8699-8d956dfd062d") // GGGregian special perms
        ) {
            if (player instanceof ServerPlayer serverPlayer) {
                OtherUtils.unlockContainerForTick((ServerLevel) world, serverPlayer.level().getServer(), container,pos);

                serverPlayer.connection
                        .send(
                                new ClientboundSoundPacket(
                                        BuiltInRegistries.SOUND_EVENT.wrapAsHolder(SoundEvents.AMETHYST_BLOCK_STEP), SoundSource.PLAYERS, player.getX(), player.getY(), player.getZ(), 0.7f, 1.0f, player.getRandom().nextLong()
                                )
                        );
            }
            return InteractionResult.PASS;
        }

        ItemStack handItem = player.getItemInHand(hand);
        if (handItem.getHoverName().toString().isEmpty()) return InteractionResult.PASS;
        if (!lock.contains(handItem.getHoverName().getString())) return InteractionResult.PASS;
        else if (PermissionManager.isTCGGameMaster(player)) return InteractionResult.PASS;

        // Player does not have permission to open the chest

        try {
            OtherUtils.removeItemsFromPlayerInventory(player, lock);
            ((ServerPlayer)player).closeContainer();

            JsonObject json = DiscordUtils.getDefaultJSON();

            JsonObject embed = new JsonObject();
            embed.addProperty("description", "__**[DO2-Manager]**__" +
                    "\n\n**"+player.getScoreboardName()+"** opened a locked container!" +
                    "\n Lock: "+lock+
                    "\n Location: " + pos.toString()+
                    "\n\n All items with the given password have been removed from the players inventory."
            );
            embed.addProperty("color", 16711680);
            JsonArray embeds = new JsonArray();
            embeds.add(embed);
            json.add("embeds", embeds);

            DiscordUtils.sendMessageToDiscord(json, DiscordUtils.getWebhookStaffURL());
        }catch (Exception e) {
            e.printStackTrace();
        }

        return InteractionResult.FAIL;
    }
}
