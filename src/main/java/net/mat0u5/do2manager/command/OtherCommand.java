package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.config.ConfigManager;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.DiscordUtils;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.utils.PermissionManager;
import net.mat0u5.do2manager.utils.TextUtils;
import net.mat0u5.do2manager.world.BlockScanner;
import net.mat0u5.do2manager.world.ItemConvertor;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;

public class OtherCommand {
    public static int remainingTime(CommandSourceStack source, long timestamp) {
        long timestampMillis = 0;
        if (timestamp >= 1000000000L && timestamp < 1000000000000L) {
            //Timestamp is in seconds.
            timestampMillis = timestamp * 1000;
        }
        else if (timestamp >= 1000000000000L && timestamp < 1000000000000000L) {
            //Timestamp is in millis.
            timestampMillis = timestamp;
        }
        else if (timestamp >= 1000000000000000L && timestamp < 1000000000000000000L) {
            //Timestamp is in microseconds.
            timestampMillis = timestamp / 1000;
        }
        else if (timestamp >= 1000000000000000000L) {
            //Timestamp is in nanos.
            timestampMillis = timestamp / 1000000;
        }
        long remainingMillis = timestampMillis - System.currentTimeMillis();
        long remainingSeconds = (int) (remainingMillis / 1000);
        source.sendSystemMessage(Component.nullToEmpty("There are "+remainingSeconds+" seconds remaining. ("+OtherUtils.convertSecondsToLongReadableTime(remainingSeconds)+")"));
        return (int) remainingSeconds;
    }

    public static int statsViewer(CommandSourceStack source, boolean newValue) {
        Main.statsViewerDisabled = newValue;
        source.sendFailure(Component.nullToEmpty("StatsViewer is now " + (Main.statsViewerDisabled ? "disabled" : "enabled")));
        return 1;
    }
    public static int executeSpeedrun(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();

        Main.config.setProperty("current_run_is_speedrun","true");
        OtherUtils.broadcastMessage(server, Component.nullToEmpty("§6This run has been marked as a speedrun."));
        return 1;
    }
    public static int executeSpeedrunAdvanced(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();

        Main.config.setProperty("current_run_is_speedrun","detailed");
        OtherUtils.broadcastMessage(server, Component.nullToEmpty("§6This run has been marked as a §o§edetailed§r§6 speedrun."));
        return 1;
    }
    public static int executeLock(CommandSourceStack source, int fromX, int fromY, int fromZ, int toX, int toY, int toZ, String type) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        if (self == null) return -1;
        self.displayClientMessage(Component.nullToEmpty("Started Block Lock Search..."), false);
        BlockScanner.scanArea(type, (ServerLevel) self.level(),new BlockPos(fromX, fromY, fromZ),new BlockPos(toX, toY, toZ), source.getPlayer());
        return 1;
    }
    public static int reload() {
        Main.config= new ConfigManager("./config/"+Main.MOD_ID+"/"+Main.MOD_ID+".properties");
        Main.lastInvUpdate = new ConfigManager("./config/"+Main.MOD_ID+"/"+Main.MOD_ID+"_inv_update.properties");
        TextUtils.setEmotes();
        DatabaseManager.fetchAllPlayers();
        return 1;
    }
    public static int reloadDatabase(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        self.displayClientMessage(Component.nullToEmpty("Reloading database..."), false);
        Main.reloadAllAbridgedRunsAsync().thenRun(() -> {
            self.displayClientMessage(Component.nullToEmpty("Database Reloaded."), false);
        });
        return 1;
    }
    public static int playerList(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();

        int playerCount = 0;
        MutableComponent message = Component.translatable("There are "+server.getPlayerList().getPlayers().size()+" players online: ");
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            playerCount++;
            message = message.append(player.getDisplayName());
            if (playerCount != server.getPlayerList().getPlayers().size()) {
                message = message.append(", ");
            }
        }
        if (self != null) {
            self.displayClientMessage(message, false);
        }
        else {
            System.out.println(message.getString());
        }
        return 1;
    }
    public static int stuck(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (isRunner(server, self)) return -1;

        self.setGameMode(GameType.SPECTATOR);
        self.teleportTo(server.overworld(), -529.5, 113, 1980.5, EnumSet.noneOf(Relative.class), 90, 0, false);
        return 1;
    }
    public static int viewDeck(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (isRunner(server, self) && !PermissionManager.isAdmin(self)) return -1;

        List<ItemStack> currentCards = RunInfoParser.getDeckItemsFromProcessor(server.overworld());

        SimpleContainer inventory = new SimpleContainer(27);
        for (ItemStack item : currentCards) {
            ItemManager.setCustomComponentString(item,"GUI","view-deck");
            inventory.addItem(item);
        }

        self.openMenu(new SimpleMenuProvider((syncId, inv, p) -> {
            return new ChestMenu(MenuType.GENERIC_9x3, syncId, inv, inventory, 3);
        }, Component.nullToEmpty("Cards Remaining In Deck")));
        return 1;
    }
    public static int viewInv(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (isRunner(server, self) && !PermissionManager.isAdmin(self)) return -1;
        List<Player> runners = RunInfoParser.getCurrentAliveRunners(server);
        if (runners.isEmpty()) return -1;

        List<ItemStack> currentItems = new ArrayList<>();
        for (Player runner : runners) {
            currentItems.addAll(ItemManager.getPlayerInventory(runner));
        }

        SimpleContainer inventory = new SimpleContainer(54);

        for (ItemStack item : currentItems) {
            ItemManager.setCustomComponentString(item,"GUI","player_inv");
            inventory.addItem(item);
        }

        self.openMenu(new SimpleMenuProvider((syncId, inv, p) -> {
            return new ChestMenu(MenuType.GENERIC_9x6, syncId, inv, inventory, 6);
        }, Component.nullToEmpty((runners.size() == 1 ? runners.get(0).getScoreboardName(): "Coop")+"'s Items")));
        return 1;
    }
    public static int getInfo(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        if (isRunner(server, self)) return -1;

        self.sendSystemMessage(Component.nullToEmpty("This command is not done yet :P"));
        return 1;
    }
    public static boolean isRunner(MinecraftServer server, ServerPlayer self) {
        if (self == null) {
            return true;
        }
        List<Player> aliveRunners = RunInfoParser.getCurrentAliveRunners(server);
        if (aliveRunners.contains(self)) {
            self.sendSystemMessage(Component.nullToEmpty("§cRunners cannot use this command :)"));
            return true;
        }
        return false;
    }

    public static int invScannerIncrement(CommandSourceStack source) {
        Integer currentInvUpdate = ItemConvertor.getInvUpdate();
        if (currentInvUpdate == null) currentInvUpdate = 0;
        ItemConvertor.setInvUpdate(currentInvUpdate+1);
        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            ItemConvertor.onPlayerJoin(player);
        }
        source.sendSystemMessage(Component.nullToEmpty("Updated index from " + currentInvUpdate + " to " + (currentInvUpdate+1)));
        return 1;
    }

    public static int invScanner(CommandSourceStack source, Collection<? extends ServerPlayer> targets, String scanType) {
        for (ServerPlayer player : targets) {
            if (scanType.equalsIgnoreCase("tagExpanded")) {
                source.sendSystemMessage(Component.nullToEmpty("Tagging "+player.getScoreboardName()+"'s Custom Cards"));
                ItemConvertor.convertCustomItems(player,-1);
                source.sendSystemMessage(Component.nullToEmpty("Tagging complete."));
            }
            if (scanType.equalsIgnoreCase("removePhase")) {
                source.sendSystemMessage(Component.nullToEmpty("Converting "+player.getScoreboardName()+"'s Items from phase to casual"));
                ItemConvertor.convertPhaseItems(player,-1);
                source.sendSystemMessage(Component.nullToEmpty("Conversion complete."));
            }
            if (scanType.equalsIgnoreCase("deleteHardcore")) {
                source.sendSystemMessage(Component.nullToEmpty("Deleting "+player.getScoreboardName()+"'s Hardcore Items"));
                ItemConvertor.deleteHardcoreItems(player,-1);
                source.sendSystemMessage(Component.nullToEmpty("Deletion complete."));
            }
            if (scanType.startsWith("deleteCRD") && scanType.contains("_")) {
                String crdStr = scanType.split("_")[1];
                try {
                    int crd = Integer.parseInt(crdStr);
                    source.sendSystemMessage(Component.nullToEmpty("Deleting "+player.getScoreboardName()+"'s Items with CRD:"+crd));
                    ItemConvertor.deleteCRDItems(player,crd);
                    source.sendSystemMessage(Component.nullToEmpty("Deletion complete."));
                }catch(Exception ignore) {}
            }
        }
        return 1;
    }
    public static int saveRunInfo(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        Main.saveRunInfoToConfig();
        return 1;
    }
    public static int makePhase(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;

        ItemStack holdingItem = ItemManager.getHoldingItem(self);
        ItemManager.setRoleplayData(holdingItem,(byte) 2);
        ItemManager.clearItemPhaseOrHardcoreLore(holdingItem);

        Component phaseLore = Component.literal("-= Phase Item =-").withStyle(ChatFormatting.RED);
        if (ItemManager.isDungeonCard(holdingItem)) {
            phaseLore = Component.literal("-= Phase Card =-").withStyle(ChatFormatting.RED);
        }
        ItemManager.addLoreToItemStack(holdingItem,List.of(Component.translationArg(phaseLore)));

        return 1;
    }
    public static int makeHardcore(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;

        ItemStack holdingItem = ItemManager.getHoldingItem(self);
        ItemManager.setRoleplayData(holdingItem,(byte) 3);
        ItemManager.clearItemPhaseOrHardcoreLore(holdingItem);

        Component phaseLore = Component.literal("-= Hardcore Item =-").withStyle(ChatFormatting.RED);
        if (ItemManager.isDungeonCard(holdingItem)) {
            phaseLore = Component.literal("-= Hardcore Card =-").withStyle(ChatFormatting.RED);
        }
        ItemManager.addLoreToItemStack(holdingItem,List.of(Component.translationArg(phaseLore)));

        return 1;
    }
    public static int makeCasual(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;

        ItemStack holdingItem = ItemManager.getHoldingItem(self);
        ItemManager.setRoleplayData(holdingItem,(byte) 1);
        ItemManager.clearItemPhaseOrHardcoreLore(holdingItem);

        return 1;
    }
    public static int customModelData(CommandSourceStack source, boolean setNotGet, int setTo) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        if (self == null) return -1;
        ItemStack holdingItem = ItemManager.getHoldingItem(self);
        if (setNotGet) {
            ItemManager.setModelData(holdingItem, setTo);
            self.sendSystemMessage(Component.nullToEmpty("The CustomModelData has been set to: "+setTo));
        }
        else {
            self.sendSystemMessage(Component.nullToEmpty("The CustomModelData of the item in your hand is: "+ ItemManager.getModelData(holdingItem)));
        }

        return 1;
    }
    public static int pushChange(CommandSourceStack source, String change, String reason, String affected) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();
        String name = "null";
        if (self != null) {
            name = self.getScoreboardName();
            self.sendSystemMessage(Component.nullToEmpty("Discord message has been sent."));
        }
        DiscordUtils.sendChangeInfo(name,change,reason,affected);

        return 1;
    }
}
