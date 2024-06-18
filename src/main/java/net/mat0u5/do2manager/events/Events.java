package net.mat0u5.do2manager.events;



import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.List;

public class Events {

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> onPlayerJoin(server, handler.getPlayer()));
    }

    private static void onPlayerJoin(MinecraftServer server, ServerPlayerEntity player) { // Add player to the database
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

        // Retrieve and print the items for the player
        List<ItemStack> items = DatabaseManager.getItemsByPlayerUUID(uuid);
        for (ItemStack item : items) {
            player.sendMessage(Text.of("You have " + item.getCount() + " of " + item.getName().getString()), false);
        }

        // Send a welcome message to the player
        player.sendMessage(Text.of("Welcome to the server, " + name + "!"), false);
    }
}
