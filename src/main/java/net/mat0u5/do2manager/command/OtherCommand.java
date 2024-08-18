package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.config.ConfigManager;
import net.mat0u5.do2manager.simulator.Simulator;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.TextUtils;
import net.mat0u5.do2manager.world.BlockScanner;
import net.mat0u5.do2manager.world.ItemConvertor;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;

import java.util.Collection;
import java.util.List;

public class OtherCommand {
    public static int executeSpeedrun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        Main.config.setProperty("current_run_is_speedrun","true");
        OtherUtils.broadcastMessage(server, Text.of("§6This run has been marked as a speedrun."));
        return 1;
    }
    public static int executeSpeedrunAdvanced(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        Main.config.setProperty("current_run_is_speedrun","detailed");
        OtherUtils.broadcastMessage(server, Text.of("§6This run has been marked as a §o§edetailed§r§6 speedrun."));
        return 1;
    }
    public static int executeLock(ServerCommandSource source, int fromX, int fromY, int fromZ, int toX, int toY, int toZ, String type) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        self.sendMessage(Text.of("Started Block Lock Search..."));
        new BlockScanner().scanArea(type,server.getOverworld(),new BlockPos(fromX, fromY, fromZ),new BlockPos(toX, toY, toZ), source.getPlayer());
        return 1;
    }
    public static int reload() {
        Main.config= new ConfigManager("./config/"+Main.MOD_ID+"/"+Main.MOD_ID+".properties");
        Main.lastInvUpdate = new ConfigManager("./config/"+Main.MOD_ID+"/"+Main.MOD_ID+"_inv_update.properties");
        TextUtils.setEmotes();
        return 1;
    }
    public static int playerList(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        int playerCount = 0;
        MutableText message = Text.translatable("There are "+server.getPlayerManager().getPlayerList().size()+" players online: ");
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            playerCount++;
            message = message.append(player.getDisplayName());
            if (playerCount != server.getPlayerManager().getPlayerList().size()) {
                message = message.append(", ");
            }
        }
        if (self != null) {
            self.sendMessage(message);
        }
        else {
            System.out.println(message.getString());
        }
        return 1;
    }
    public static int stuck(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;
        if (isRunner(server, self)) return -1;

        self.changeGameMode(GameMode.SPECTATOR);
        self.teleport(-529.5, 113, 1980.5);
        return 1;
    }
    public static int viewDeck(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;
        if (isRunner(server, self) && !self.hasPermissionLevel(2)) return -1;

        List<ItemStack> currentCards = new Simulator().getDeckItemsFromProcessor(server.getOverworld());

        SimpleInventory inventory = new SimpleInventory(27);
        for (ItemStack item : currentCards) {
            NbtCompound nbt = item.getOrCreateNbt();
            nbt.putString("GUI","view-deck");
            item.setNbt(nbt);
            inventory.addStack(item);
        }

        self.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> {
            return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X3, syncId, inv, inventory, 3);
        }, Text.of("Cards Remaining In Deck")));
        return 1;
    }
    public static int viewInv(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;
        if (isRunner(server, self) && !self.hasPermissionLevel(2)) return -1;
        List<PlayerEntity> runners = RunInfoParser.getCurrentAliveRunners(server);
        if (runners.isEmpty()) return -1;
        if (runners.size() != 1) {
            self.sendMessage(Text.of("§cYou cannot use this command with more than one runner!"));
            return -1;
        }
        PlayerEntity runner = runners.get(0);

        List<ItemStack> currentCards = ItemManager.getPlayerInventory(runner);

        SimpleInventory inventory = new SimpleInventory(54);

        for (ItemStack item : currentCards) {
            NbtCompound nbt = item.getOrCreateNbt();
            nbt.putString("GUI","player_inv");
            item.setNbt(nbt);
            inventory.addStack(item);
        }

        self.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, inv, p) -> {
            return new GenericContainerScreenHandler(ScreenHandlerType.GENERIC_9X6, syncId, inv, inventory, 6);
        }, Text.of(runner.getEntityName()+"'s Items")));
        return 1;
    }
    public static int getInfo(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;
        if (isRunner(server, self)) return -1;

        self.sendMessage(Text.of("This command is not done yet :P"));
        return 1;
    }
    public static boolean isRunner(MinecraftServer server, ServerPlayerEntity self) {
        if (self == null) {
            return true;
        }
        List<PlayerEntity> aliveRunners = RunInfoParser.getCurrentAliveRunners(server);
        if (aliveRunners.contains(self)) {
            self.sendMessage(Text.of("§cRunners cannot use this command :)"));
            return true;
        }
        return false;
    }
    public static int invScanner(ServerCommandSource source, Collection<? extends ServerPlayerEntity> targets, String scanType) {
        MinecraftServer server = source.getServer();
        final ServerPlayerEntity self = source.getPlayer();

        for (ServerPlayerEntity player : targets) {
            if (scanType.equalsIgnoreCase("tagExpanded")) ItemConvertor.convertCustomItems(player,-1);
            if (scanType.equalsIgnoreCase("removePhase")) ItemConvertor.convertPhaseItems(player,-1);
        }
        return 1;
    }
}
