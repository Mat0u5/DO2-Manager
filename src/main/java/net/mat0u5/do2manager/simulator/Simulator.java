package net.mat0u5.do2manager.simulator;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.EntityUtils;
import net.mat0u5.do2manager.utils.ScoreboardUtils;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.RunInfoParser;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.List;

public class Simulator {
    public static Simulation currentSimulation = null;
    public static Player simulationPlayer = null;
    public static Deck startingDeck = null;
    public int lastTotalEmbers = 0;

    public Deck getDeckFromHand(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        List<ItemStack> shulkerBoxItems = ItemManager.getContainerItemContents(mainHand);
        Deck deck = getDeckFromItems(shulkerBoxItems);
        return deck;
    }
    public List<ItemStack> getDeckItemsFromProcessor(Level world) {
        List<ItemStack> entityItems = ItemManager.getContentsOfEntitiesAtPosition(world,new BlockPos(-565, 40, 1913),2);
        List<ItemStack> hopper1 = ItemManager.getHopperItems((ServerLevel) world,new BlockPos(-565, 39, 1914));
        List<ItemStack> hopper2 = ItemManager.getHopperItems((ServerLevel) world,new BlockPos(-565, 39, 1913));
        List<ItemStack> dropper1 = ItemManager.getDropperItems((ServerLevel) world,new BlockPos(-564, 39, 1913));
        List<ItemStack> dropper2 = ItemManager.getDropperItems((ServerLevel) world,new BlockPos(-564, 40, 1913));
        entityItems.addAll(hopper1);
        entityItems.addAll(hopper2);
        entityItems.addAll(dropper1);
        entityItems.addAll(dropper2);
        return entityItems;
    }
    public Deck getDeckFromProcessor(Level world) {
        List<ItemStack> deckItems = getDeckItemsFromProcessor(world);
        Deck deck = getDeckFromItems(deckItems);
        return deck;
    }
    public void getPermanentsFromStoredDeck(Level world) {
        List<ItemStack> hopper = ItemManager.getHopperItems((ServerLevel) world,new BlockPos(-551,122,1971));
        List<ItemStack> deckItems = new ArrayList<>();
        for (ItemStack shulkerPotential : hopper) {
            List<ItemStack> potentialDeckItems = ItemManager.getContainerItemContents(shulkerPotential);
            if (potentialDeckItems == null) continue;
            if (potentialDeckItems.isEmpty()) continue;
            deckItems.addAll(potentialDeckItems);
        }
        Deck newDeck = getDeckFromItems(deckItems);
        startingDeck = newDeck.copy();
    }
    public Deck getDeckFromItems(List<ItemStack> items) {
        Cards Cards = new Cards();
        Deck deck = new Deck();
        for (ItemStack cardItem : items) {
            if (cardItem == null) continue;
            String cardName = cardItem.getHoverName().getString().replaceAll("✧", "").replaceAll("✲", "").replaceAll("≡", "").trim().toLowerCase().replaceAll(" ","_");
            Card addCard = Cards.getCardFromName(cardName);
            if (addCard == null) {
                System.out.println("Card Not Found: " + cardItem.getHoverName().getString());
                continue;
            }
            deck.addCard(addCard, cardItem.getCount());
        }
        return deck;
    }
    public Card getFirstCardPlay(Level world) {
        Cards Cards = new Cards();
        List<ItemStack> hopper = ItemManager.getDropperItems((ServerLevel) world,new BlockPos(-565, 37, 1914));
        for (ItemStack cardPotential : hopper) {
            if (cardPotential == null) continue;
            if (cardPotential.isEmpty()) continue;
            String cardName = cardPotential.getHoverName().getString().replaceAll("✧", "").replaceAll("✲", "").replaceAll("≡", "").trim().toLowerCase().replaceAll(" ","_");
            Card card = Cards.getCardFromName(cardName);
            if (card == null) continue;
            return card;
        }
        return null;
    }
    public int cardPlayed(CommandSourceStack source) {
        if (Main.config.getProperty("simulator_enabled") == null) return -1;
        if (!Main.config.getProperty("simulator_enabled").equalsIgnoreCase("true")) return -1;
        stopCurrentSim();
        MinecraftServer server = source.getServer();
        ServerLevel overworld = server.overworld();
        if (simulationPlayer == null) {
            ServerPlayer newPlayer = server.getPlayerList().getPlayerByName("Mat0u5");
            if (newPlayer == null) return -1;
            simulationPlayer = newPlayer;
            if (simulationPlayer == null) return -1;
        }
        Deck currentGameDeck = getDeckFromProcessor(overworld);
        if (startingDeck == null) {
            getPermanentsFromStoredDeck(overworld);
        }
        currentGameDeck.permanents = startingDeck.copy().permanents;
        Card firstCardPlay = getFirstCardPlay(overworld);
        currentSimulation = new Simulation(currentGameDeck, simulationPlayer,false,false, List.of("emberMAXX"),getIntInfo(server),getBoolInfo(server),firstCardPlay);
        currentSimulation.runSimulation();
        return 1;
    }
    public List<Integer> getIntInfo(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        int recycles = ItemManager.getHopperItemsCount(overworld, new BlockPos(-625, 34, 1920));
        int skipCardsActual = ItemManager.getDropperItemsCount(overworld, new BlockPos(-568, 36, 1921));
        int run_length = RunInfoParser.getRunLength(server)/20;
        Integer cards_played_score = ScoreboardUtils.getPlayerScore(server,"CardsPlayed","DeckedOutGame");
        int cards_played = 0;
        if (cards_played_score != null) cards_played = cards_played_score;
        int coldSnaps = ItemManager.getHopperItemsCount(overworld, new BlockPos(-617, 38, 1931));
        int strideWithPride = ItemManager.getDropperItemsCount(overworld, new BlockPos(-581, 27, 1898));
        int bombEmbers = ItemManager.getHopperItemsCount(overworld, new BlockPos(-578, -37, 1837));
        int bombCrowns = ItemManager.getHopperItemsCount(overworld, new BlockPos(-580, -37, 1837));
        int playerEmbers = getPlayerEmbers(server);
        int artiEmbers = getPlayerArtiEmbers(server);
        int floorEmbers = getFloorEmbers(overworld);
        int cashCow = ItemManager.getDropperItemsCount(overworld, new BlockPos(-605, 40, 1932));
        int embersQueuedRN = 0;
        Integer embersQ = ScoreboardUtils.getPlayerScore(server,"EmbersQueued","DeckedOutGame");
        if (embersQ != null) embersQueuedRN = embersQ;
        //Calculated
        int skipFirstCards = 0;

        Integer deepestLevel = ScoreboardUtils.getPlayerScore(server,"DeepestLevelReached","DeckedOutGame");
        if (deepestLevel == null) skipFirstCards=4;
        else {
            if (deepestLevel == 1) skipFirstCards=4;
            if (deepestLevel == 2) skipFirstCards=3;
            if (deepestLevel == 3) skipFirstCards=1;
            if (deepestLevel == 4) skipFirstCards=0;
        }
        int cardsUntilArti = skipFirstCards+2;
        if (skipFirstCards == 0)  {
            cardsUntilArti = 1;
        }
        int diveEmbers = 0;
        List<ItemStack> diveItems = ItemManager.getBarrelItems(overworld,new BlockPos(-580,15,1969));
        if (diveItems != null && !diveItems.isEmpty()) {
            for (ItemStack item : diveItems) {
                if (item == null) continue;
                if (item.isEmpty()) continue;
                if (ItemManager.isEmber(item)) {
                    diveEmbers+=item.getCount();
                }
            }
        }


        skipFirstCards+= skipCardsActual;

        List<Integer> result = List.of(
                skipFirstCards,cardsUntilArti,recycles,
                run_length,cards_played,coldSnaps,strideWithPride,
                cashCow,playerEmbers,artiEmbers,
                floorEmbers,bombEmbers,bombCrowns,diveEmbers,embersQueuedRN);
        return result;
    }
    public List<Boolean> getBoolInfo(MinecraftServer server) {
        ServerLevel overworld = server.overworld();

        boolean artiAcquired = false;
        Integer artiAcquiredScore = ScoreboardUtils.getPlayerScore(server,"- Artifact Acquired","SpectatorMapDisplay");
        if (artiAcquiredScore != null) {
            if (artiAcquiredScore > 0) artiAcquired=true;
        }
        boolean riskyPlayed = ItemManager.getHopperItemsCount(overworld, new BlockPos(-625, 34, 1920))>0;
        boolean bombTriggered = Main.currentRun.containsSpecialEvent("bomb");
        boolean rustyCollected = Main.currentRun.containsSpecialEvent("rusty");
        boolean diveCollected = Main.currentRun.containsSpecialEvent("dive");
        boolean hideoutCompleted = ItemManager.getHopperItemsCount(overworld, new BlockPos(-608, -62, 1882))==0;
        boolean speedrunnerCollected = !EntityUtils.isItemInRange(overworld,new BlockPos(-584, 4, 1932),3);

        List<Boolean> result = List.of(
                artiAcquired,speedrunnerCollected,riskyPlayed
                ,bombTriggered,rustyCollected,
                diveCollected,hideoutCompleted);
        return result;
    }
    public int getPlayerEmbers(MinecraftServer server) {
        List<Player> players = RunInfoParser.getCurrentAliveRunners(server);
        if (players == null) return 0;
        if (players.isEmpty()) return 0;
        int count = 0;
        for (Player player : players) {
            List<ItemStack> inv = ItemManager.getPlayerInventory(player);
            if (inv == null) continue;
            if (inv.isEmpty()) continue;
            for (ItemStack item : inv) {
                if (item == null) continue;
                if (item.isEmpty()) continue;
                if (ItemManager.isEmber(item)) {
                    count += item.getCount();
                }
            }
        }
        return count;
    }
    public int getPlayerArtiEmbers(MinecraftServer server) {
        List<Player> players = RunInfoParser.getCurrentAliveRunners(server);
        if (players == null) return 0;
        if (players.isEmpty()) return 0;
        int count = 0;
        for (Player player : players) {
            List<ItemStack> inv = ItemManager.getPlayerInventory(player);
            if (inv == null) continue;
            if (inv.isEmpty()) continue;
            for (ItemStack item : inv) {
                if (item == null) continue;
                if (item.isEmpty()) continue;
                if (ItemManager.isDungeonArtifact(item)) {
                    count += ItemManager.getArtifactWorth(item);
                }
            }
        }
        return count;
    }
    public int getFloorEmbers(ServerLevel world) {
        List<ItemStack> lvl4Items = EntityUtils.getItemStacksInBox(world,new BlockPos(-565, -30, 1836),new BlockPos(-661, -60, 1920));
        if (lvl4Items == null) return 0;
        if (lvl4Items.isEmpty()) return 0;
        int count = 0;
        for (ItemStack item : lvl4Items) {
            if (item == null) continue;
            if (item.isEmpty()) continue;
            if (ItemManager.isEmber(item)) {
                count += item.getCount();
            }
        }
        return count;
    }
    public int saveHand(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        simulationPlayer = self;
        startingDeck = getDeckFromHand(self);
        self.displayClientMessage(Component.nullToEmpty("§5Simulator initialized. The permanents in the deck in your main hand hand have been saved."), false);
        return 1;
    }
    public int enOrDis(CommandSourceStack source, String setTo) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        Main.config.setProperty("simulator_enabled",setTo);
        return 1;
    }
    public int runHand(CommandSourceStack source, int simulateRuns, boolean dontSkipCards) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        finishedRun();
        simulationPlayer = self;
        startingDeck = getDeckFromHand(self);
        self.displayClientMessage(Component.nullToEmpty("§5Simulation started. ["+simulateRuns+" runs]"), false);
        currentSimulation = new Simulation(startingDeck, simulationPlayer,true,dontSkipCards, List.of("all"));
        currentSimulation.runSimulation(simulateRuns);
        return 1;
    }
    public int stopSimCommand(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        if (self != null) {
            self.displayClientMessage(Component.nullToEmpty("§5The simulation has been stopped."), false);
        }
        finishedRun();
        return 1;
    }
    public void finishedRun() {
        stopCurrentSim();
        deleteInfo();
    }
    public void deleteInfo() {
        Main.simulator = new Simulator();
        currentSimulation = null;
        simulationPlayer = null;
        startingDeck = null;
        lastTotalEmbers = 0;
    }
    public void stopCurrentSim() {
        if (currentSimulation != null) {
            currentSimulation.stopSimulation();
        }
    }
}
