package net.mat0u5.do2manager.simulator;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class Simulator {
    public static Simulation currentSimulation = null;
    public static PlayerEntity simulationPlayer = null;
    public static Deck startingDeck = null;

    public Deck getDeckFromHand(PlayerEntity player) {
        ItemStack mainHand = player.getMainHandStack();
        List<ItemStack> shulkerBoxItems = ItemManager.getShulkerItemContents(mainHand);
        Deck deck = getDeckFromItems(shulkerBoxItems);

        for (Card card : deck.deck.keySet()) {
            player.sendMessage(Text.of(card.cardName+"_"+deck.deck.get(card)));
        }
        return deck;
    }
    public Deck getDeckFromProcessor(World world) {
        List<ItemStack> entityItems = ItemManager.getContentsOfEntitiesAtPosition(world,new BlockPos(-565, 40, 1913),2);
        List<ItemStack> hopper1 = ItemManager.getHopperItems((ServerWorld) world,new BlockPos(-565, 39, 1914));
        List<ItemStack> hopper2 = ItemManager.getHopperItems((ServerWorld) world,new BlockPos(-565, 39, 1913));
        List<ItemStack> dropper1 = ItemManager.getDropperItems((ServerWorld) world,new BlockPos(-564, 39, 1913));
        List<ItemStack> dropper2 = ItemManager.getDropperItems((ServerWorld) world,new BlockPos(-564, 40, 1913));
        entityItems.addAll(hopper1);
        entityItems.addAll(hopper2);
        entityItems.addAll(dropper1);
        entityItems.addAll(dropper2);

        Deck deck = getDeckFromItems(entityItems);

        /*for (Card card : deck.deck.keySet()) {
            System.out.println(card.cardName+"_"+deck.deck.get(card));
        }*/
        return deck;
    }
    public Deck getDeckFromItems(List<ItemStack> items) {
        Cards Cards = new Cards();
        Deck deck = new Deck();
        for (ItemStack cardItem : items) {
            if (cardItem == null) continue;
            String cardName = cardItem.getName().getString().replaceAll("✧", "").replaceAll("✲", "").replaceAll("≡", "").trim().toLowerCase().replaceAll(" ","_");
            Card addCard = Cards.getCardFromName(cardName);
            if (addCard == null) {
                System.out.println("Card Not Found: " + cardItem.getName().getString());
                continue;
            }
            deck.addCard(addCard, cardItem.getCount());
        }
        return deck;
    }
    public int cardPlayed(ServerCommandSource source) {
        if (simulationPlayer == null) return -1;
        stopCurrentSim();
        Deck currentGameDeck = getDeckFromProcessor(source.getServer().getOverworld());
        currentGameDeck.permanents = startingDeck.copy().permanents;
        currentSimulation = new Simulation(currentGameDeck, simulationPlayer);
        currentSimulation.runSimulation();
        return 1;
    }
    public int startSimulator(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        simulationPlayer = self;
        startingDeck = getDeckFromHand(self);
        self.sendMessage(Text.of("§5Simulator initialized. The permanents in the deck in your main hand hand have been saved."));
        return 1;
    }
    public void runFinished() {
        stopCurrentSim();
        Main.simulator = new Simulator();
    }
    public void stopCurrentSim() {
        if (currentSimulation != null) {
            currentSimulation.stopSimulation();
        }
    }
}
