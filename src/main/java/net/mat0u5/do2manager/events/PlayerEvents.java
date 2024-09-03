package net.mat0u5.do2manager.events;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.management.OperatingSystemMXBean;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.gui.GuiInventoryClick;
import net.mat0u5.do2manager.queue.QueueEvents;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.PermissionManager;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.ItemConvertor;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.block.Block;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import static net.mat0u5.do2manager.events.Events.clickEventCooldown;
import static net.mat0u5.do2manager.events.Events.lastPlayerLogoutTime;

public class PlayerEvents {
    static void onPlayerDisconnect(MinecraftServer server, ServerPlayerEntity player) {
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
    static void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player) {
        try {
            QueueEvents.onPlayerJoin(player);
            if (player.isCreative() && !player.hasPermissionLevel(2)) {
                player.changeGameMode(GameMode.SPECTATOR);
                System.out.println(player.getEntityName()+"'s gamemode was automatically reset to spectator, because they were in creative.");
            }

            //Add the player to the database
            DatabaseManager.addPlayer(player.getUuidAsString(),player.getEntityName());
            DatabaseManager.fetchAllPlayers();
            lastPlayerLogoutTime = -1;

            //Item Conversions
            ItemConvertor.onPlayerJoin(player);
        }catch (Exception e) {}
    }
    static void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        MinecraftServer server = player.getServer();
        if (player.getMainHandStack().getItem() == Items.TOTEM_OF_UNDYING || player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING) {
            return;
        }

        List<PlayerEntity> runners = RunInfoParser.getCurrentRunners(server);
        if (runners.contains(player) && runners.size() == 1) {
            boolean diedFromPathOfCoward = player.getPos().distanceTo(new Vec3d(-643, -18, 1977))<2;
            if (Main.currentRun.run_number != -1 && Main.currentRun.inventory_save.isEmpty() && !diedFromPathOfCoward) {
                Main.currentRun.inventory_save = ItemManager.getPlayerInventory(player);
            }
            Main.currentRun.death_pos = player.getPos().toString();
            Main.currentRun.death_message = source.getDeathMessage(player).getString();
            if (diedFromPathOfCoward) {
                System.out.println(player.getEntityName() + " took the path of the coward. LLLL");
                Main.currentRun.finishers = new ArrayList<>();
                DatabaseManager.saveRun(server);
            }
        }
    }
    public static void onPlayerDropItem(ServerPlayerEntity player, ItemStack itemStack) {
        invPickupOrDropItem(player,itemStack);
    }
    public static void onPlayerPickupItem(PlayerEntity player, ItemEntity itemEntity) {
        if (itemEntity.cannotPickup()) return;
        invPickupOrDropItem(player,itemEntity.getStack());
    }


    public static void invPickupOrDropItem(PlayerEntity player, ItemStack itemStack) {
        try {
            if (!RunInfoParser.getCurrentRunners(player.getServer()).contains(player)) return;
            if (RunInfoParser.isDungeonCompass(itemStack) && Main.currentRun.compass_item == null) {
                Main.currentRun.compass_item = itemStack;

                List<PlayerEntity> runners = RunInfoParser.getCurrentRunners(player.getServer());
                if (!runners.isEmpty()) {
                    boolean isSpeedrun = Main.config.getProperty("current_run_is_speedrun").equalsIgnoreCase("true");
                    if (runners.size() == 1 && isSpeedrun) RunInfoParser.getFastestPlayerRunMatchingCurrent(RunInfoParser.getCurrentRunners(player.getServer()).get(0));
                }
            }
            if (RunInfoParser.isDungeonArtifact(itemStack) && Main.currentRun.artifact_item == null) {
                if (ItemManager.getModelData(itemStack) == 36) {
                    OtherUtils.executeCommand(player.getServer(),"function dom:mat0u5/gui/other/mug_maniac_activate");
                }
                Main.currentRun.artifact_item = itemStack;
            }
        }catch(Exception e) {}
    }
    public static void onSlotClick(int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, ScreenHandler handler) {
        try {
            if (!handler.isValid(slotId)) return;
            if (slotId < 0 ) return;
            ItemStack clickedItem = handler.getSlot(slotId).getStack();
            if (clickedItem == null) return;
            NbtCompound nbt = clickedItem.getNbt();
            if (nbt == null) return;
            if (OtherUtils.isHoldingAdminKey(player)) return;
            if (!nbt.contains("GUI")) return;
            if (!nbt.contains("GUI_DontCancelClick")) ci.cancel();
            String tag = nbt.getString("GUI");
            if ((tag.equalsIgnoreCase("DatabaseGUI")||tag.equalsIgnoreCase("custom")) && clickEventCooldown <= 0) {
                clickEventCooldown = 4;
                GuiInventoryClick.onClickDatabaseGUI(tag,slotId,button,actionType,player,ci,handler);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    public static ActionResult onBlockUse(PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) {
        CommandBlockEvents.onBlockUse(player,world,hand,hitResult);
        BlockPos pos = hitResult.getBlockPos();
        Block block = world.getBlockState(pos).getBlock();
        if (block == null) return ActionResult.PASS;
        if (!(world.getBlockEntity(pos) instanceof LockableContainerBlockEntity)) return ActionResult.PASS;
        LockableContainerBlockEntity container = (LockableContainerBlockEntity) world.getBlockEntity(pos);
        if (container == null) return ActionResult.PASS;

        String lock = OtherUtils.getLock(container);
        if (lock == null || lock.isEmpty()) return ActionResult.PASS;
        if (PermissionManager.isAdmin(player) || player.getUuidAsString().equalsIgnoreCase("24268497-6a56-4132-8699-8d956dfd062d")) {
            OtherUtils.unlockContainerForTick((ServerWorld) world, player.getServer(), container,pos);
            player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_STEP, SoundCategory.PLAYERS, 0.7f, 1.0f);
            return ActionResult.PASS;
        }

        ItemStack handItem = player.getStackInHand(hand);
        if (handItem.getName().toString().isEmpty()) return ActionResult.PASS;
        if (!lock.contains(handItem.getName().getString())) return ActionResult.PASS;
        else if (PermissionManager.isTCGGameMaster(player)) return ActionResult.PASS;

        // Player does not have permission to open the chest

        try {
            OtherUtils.removeItemsFromPlayerInventory(player, lock);
            ((ServerPlayerEntity)player).closeHandledScreen();

            JsonObject json = DiscordUtils.getDefaultJSON();

            JsonObject embed = new JsonObject();
            embed.addProperty("description", "__**[DO2-Manager]**__" +
                    "\n\n**"+player.getEntityName()+"** opened a locked container!" +
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

        return ActionResult.FAIL;
    }
}
