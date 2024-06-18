package net.mat0u5.do2manager.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import java.util.List;

import net.minecraft.util.Hand;
import org.spongepowered.include.com.google.common.collect.ImmutableList;


public class Command {

    public static void register(CommandDispatcher<ServerCommandSource> serverCommandSourceCommandDispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        serverCommandSourceCommandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("decked-out")
                .executes(context -> Command.execute((ServerCommandSource)context.getSource())))));

        serverCommandSourceCommandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("decked-out")
                .then(CommandManager.literal("getItem").executes(context -> Command.executeGetItem((ServerCommandSource)context.getSource()))))));

        serverCommandSourceCommandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal("decked-out")
                .then(CommandManager.literal("setItem").executes(context -> Command.executeSetItem((ServerCommandSource)context.getSource()))))));

        //printAllPlayers()
    }
    public static int execute(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        DatabaseManager.printAllPlayers();
        self.sendMessage(Text.translatable("§6Command Worked.."));
        return -1;
    }
    public static int executeGetItem(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        List<ItemStack> playerItems = DatabaseManager.getItemsByPlayerUUID(self.getUuidAsString());
        for (ItemStack item : playerItems) {
            ItemManager.giveItemStack(self,item);
        }

        return -1;
    }
    public static int executeSetItem(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        self.sendMessage(Text.translatable("TEST: " + self.getStackInHand(Hand.MAIN_HAND).getItem().getTranslationKey()));
        self.sendMessage(Text.translatable("TES2: " + Registries.ITEM.getId(self.getStackInHand(Hand.MAIN_HAND).getItem())));
        DatabaseManager.addItem(self.getUuidAsString(),self.getStackInHand(Hand.MAIN_HAND));

        return -1;
    }
}
