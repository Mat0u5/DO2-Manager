package net.mat0u5.do2manager.events;



import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.DO2_GSON;
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
        // Add player to the database
        String uuid = player.getUuidAsString();
        String name = player.getName().getString();
        DatabaseManager.addPlayer(uuid, name);

        // Example: Create a Shulker Box item with custom contents and metadata
        ItemStack shulkerBox = new ItemStack(Items.SHULKER_BOX);
        NbtCompound nbt = new NbtCompound();
        nbt.putString("CustomName", "My Shulker Box");
        shulkerBox.setNbt(nbt);

        // Add the Shulker Box to the player
        DatabaseManager.addItem(uuid, shulkerBox);

        // Send a welcome message to the player
        player.sendMessage(Text.of("Welcome to the server, " + name + "!"), false);
    }
    private static void onPlayerDeath(ServerPlayerEntity player, DamageSource source) {
        MinecraftServer server = player.getServer();
        player.sendMessage(source.getDeathMessage(player));
        List<PlayerEntity> runners = RunInfoParser.getCurrentRunners(server);
        if (runners.contains(player) && runners.size() == 1) {
            ItemStack artifact = RunInfoParser.getRunnersArtifact(server);
            ItemStack compass = RunInfoParser.getRunnersCompass(server);


            Main.config.setProperty("inventory_save", DO2_GSON.serializePlayerInventory(player));
            if (artifact != null) Main.config.setProperty("artifact_item", DO2_GSON.serializeItemStack(artifact));
            if (compass != null)Main.config.setProperty("compass_item", DO2_GSON.serializeItemStack(compass));
            Main.config.setProperty("death_pos", player.getPos().toString());
            Main.config.setProperty("death_message", source.getDeathMessage(player).toString());
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
        System.out.println("Player Item check");
        if (RunInfoParser.isDungeonCompass(itemStack) && (Main.config.getProperty("compass_item") == "null" || Main.config.getProperty("compass_item") == null || Main.config.getProperty("compass_item").isEmpty())) {
            Main.config.setProperty("compass_item", DO2_GSON.serializeItemStack(itemStack));
            System.out.println("Player compass");
        }
        if (RunInfoParser.isDungeonArtifact(itemStack) && (Main.config.getProperty("artifact_item") == "null" || Main.config.getProperty("artifact_item") == null || Main.config.getProperty("artifact_item").isEmpty())) {
            Main.config.setProperty("artifact_item", DO2_GSON.serializeItemStack(itemStack));
            System.out.println("Player artifact");
        }
    }
}
