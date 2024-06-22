package net.mat0u5.do2manager.events;



import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.DO2_GSON;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

public class Events {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerJoin(server, handler.getPlayer()));

        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, amount) -> {
            if (entity instanceof ServerPlayerEntity) {
                onPlayerDeath((ServerPlayerEntity) entity, damageSource);
            }
            return true;
        });

    }

    private static void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player) {
    }
    private static void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        MinecraftServer server = player.getServer();

        List<PlayerEntity> runners = RunInfoParser.getCurrentRunners(server);
        if (runners.contains(player) && runners.size() == 1) {
            if (Main.currentRun.run_number != -1 && Main.currentRun.inventory_save.isEmpty()) {
                Main.currentRun.inventory_save = ItemManager.getPlayerInventory(player);
            }
            Main.currentRun.death_pos = player.getPos().toString();
            Main.currentRun.death_message = source.getDeathMessage(player).getString();
        }
    }
    public static void onPlayerDropItem(ServerPlayerEntity player, ItemStack itemStack) {
        invPickupOrDropItem(player,itemStack);

    }
    public static void onPlayerPickupItem(PlayerEntity player, ItemEntity itemEntity) {
        if (itemEntity.getItemAge() < 40) return;
        invPickupOrDropItem(player,itemEntity.getStack());
    }




    public static void invPickupOrDropItem(PlayerEntity player, ItemStack itemStack) {
        if (!RunInfoParser.getCurrentRunners(player.getServer()).contains(player)) return;
        if (RunInfoParser.isDungeonCompass(itemStack) && Main.currentRun.compass_item == null) {
            Main.currentRun.compass_item = itemStack;
        }
        if (RunInfoParser.isDungeonArtifact(itemStack) && Main.currentRun.artifact_item == null) {
            Main.currentRun.artifact_item = itemStack;
        }
    }
}
