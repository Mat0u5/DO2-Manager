package net.mat0u5.do2manager.events;



import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.command.RestartCommand;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.gui.GuiInventoryClick;
import net.mat0u5.do2manager.gui.GuiInventory_Database;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.FakeSign;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Events {

    public static long clickEventCooldown = 00;
    private static long lastPlayerLogoutTime = -1;
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTING.register((server) -> onServerStart(server));
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerJoin(server, handler.getPlayer()));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            scheduler.schedule(() -> onPlayerDisconnect(server, handler.getPlayer()), 1, TimeUnit.SECONDS);
        });
        ServerTickEvents.END_SERVER_TICK.register(Events::onServerTickEnd);

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, amount) -> {
            if (entity instanceof ServerPlayerEntity) {
                onPlayerDeath((ServerPlayerEntity) entity, damageSource);
            }
            return true;
        });
    }

    private static void onServerTickEnd(MinecraftServer server) {
        if (lastPlayerLogoutTime != -1 && RestartCommand.isRestartQueued() && OtherUtils.isServerEmptyOrOnlyTangoCam(server)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPlayerLogoutTime >= TimeUnit.MINUTES.toMillis(3)) {
                OtherUtils.restartServer(server);
            }
        }
        if (clickEventCooldown > 0) clickEventCooldown--;
    }
    private static void onPlayerDisconnect(MinecraftServer server, ServerPlayerEntity player) {
        if (OtherUtils.isServerEmptyOrOnlyTangoCam(server)) {//Last player disconnects
            lastPlayerLogoutTime = System.currentTimeMillis();
        }
        else {
            lastPlayerLogoutTime = -1;
        }
    }
    private static void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player) {
        if (player.isCreative() && !player.hasPermissionLevel(2)) {
            player.changeGameMode(GameMode.SURVIVAL);
            System.out.println(player.getEntityName()+"'s gamemode was automatically reset to survival, because they were in creative.");
        }

        //Add the player to the database
        DatabaseManager.addPlayer(player.getUuidAsString(),player.getEntityName());
        DatabaseManager.fetchAllPlayers();
        lastPlayerLogoutTime = -1;

        //Remove all phase items when necessary
        String playerUUID = player.getUuidAsString();
        if (Main.lastPhaseUpdate.getProperty(playerUUID) == null) {
            System.out.println("Converting "+player.getEntityName()+"'s Items from phase to casual");
            ItemManager.phaseToCasualPlayer(player);
            Main.lastPhaseUpdate.setProperty(playerUUID, String.valueOf(Main.PHASE_UPDATE));
            System.out.println("Conversion complete.");
            return;
        }
        if (Integer.parseInt(Main.lastPhaseUpdate.getProperty(playerUUID)) < Main.PHASE_UPDATE) {
            System.out.println("Converting "+player.getEntityName()+"'s Items from phase to casual");
            ItemManager.phaseToCasualPlayer(player);
            Main.lastPhaseUpdate.setProperty(playerUUID, String.valueOf(Main.PHASE_UPDATE));
            System.out.println("Conversion complete.");
        }
    }
    private static void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
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
        if (!RunInfoParser.getCurrentRunners(player.getServer()).contains(player)) return;
        if (RunInfoParser.isDungeonCompass(itemStack) && Main.currentRun.compass_item == null) {
            boolean isSpeedrun = Main.config.getProperty("current_run_is_speedrun").equalsIgnoreCase("true");
            Main.currentRun.compass_item = itemStack;

            List<PlayerEntity> runners = RunInfoParser.getCurrentRunners(player.getServer());
            if (!runners.isEmpty()) {
                if (runners.size() == 1) Main.speedrun = RunInfoParser.getFastestPlayerRunMatchingCurrent(RunInfoParser.getCurrentRunners(player.getServer()).get(0));
            }
            if (isSpeedrun) {
                OtherUtils.broadcastMessage(player.getServer(), Text.translatable("§6This speedrun will be compared with " + player.getEntityName() + "'s fastest level " + Main.currentRun.getCompassLevel()+" run."));
            }
        }
        if (RunInfoParser.isDungeonArtifact(itemStack) && Main.currentRun.artifact_item == null) {
            if (ItemManager.getModelData(itemStack) == 36) {
                OtherUtils.executeCommand(player.getServer(),"function dom:mat0u5/gui/other/mug_maniac_activate");
            }
            Main.currentRun.artifact_item = itemStack;
        }
    }
    public static void onSlotClick(int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci, ScreenHandler handler) {
        try {
            if (!Main.openGuis.containsKey(player)) return;
            if (!Main.openGuis.get(player).invOpen) return;

            if (!handler.isValid(slotId)) return;
            ItemStack clickedItem = handler.getSlot(slotId).getStack();
            if (clickedItem == null) return;
            NbtCompound nbt = clickedItem.getNbt();
            if (nbt == null) return;
            if (!nbt.contains("GUI")) return;
            if (!nbt.contains("GUI_DontCancelClick")) ci.cancel();
            String tag = nbt.getString("GUI");
            if ((tag.equalsIgnoreCase("DatabaseGUI")||tag.equalsIgnoreCase("custom")) && clickEventCooldown <= 0) {
                clickEventCooldown = 4;
                GuiInventoryClick.onClickDatabaseGUI(tag,slotId,button,actionType,player,ci,handler);
            }
        }catch(Exception e) {}
    }
    private static void onServerStart(MinecraftServer server) {
        Main.server = server;
        System.out.println("MinecraftServer instance captured.");
    }
}
