package net.mat0u5.do2manager.simulator;

import java.util.HashMap;
import java.util.Random;

public class Deck {
    Random rnd = new Random();
    public HashMap<Card, Integer> permanents = new HashMap<Card, Integer>();
    public HashMap<Card, Integer> deck = new HashMap<Card, Integer>();
    public Deck() {

    }
    public Deck(HashMap<Card, Integer> deck, HashMap<Card, Integer> permanents) {
        this.deck = deck;
        this.permanents = permanents;
    }
    public int getCardCount() {
        int count = 0;
        for (Card card : deck.keySet()) {
            count += deck.get(card);

        }
        for (Card card : permanents.keySet()) {
            count += permanents.get(card);

        }
        return count;
    }
    public int getNonPermanentCardCount() {
        int count = 0;
        for (Card card : deck.keySet()) {
            count += deck.get(card);

        }
        return count;
    }
    public boolean hasCard(Card matchFor) {
        for (Card card : deck.keySet()) {
            if (card.cardName.equalsIgnoreCase(matchFor.cardName)) return true;
        }
        for (Card card : permanents.keySet()) {
            if (card.cardName.equalsIgnoreCase(matchFor.cardName)) return true;
        }
        return false;
    }
    public void removeCard(Card card, int count) {
        if (count <= 0) return;
        if (!card.isPermanent) {
            if (!deck.containsKey(card)) return;
            int cardNum = deck.get(card);
            if (cardNum <= count) {
                deck.remove(card);
            }
            else {
                deck.put(card, cardNum - count);
            }
        }
        else {
            if (!permanents.containsKey(card)) return;
            int cardNum = permanents.get(card);
            if (cardNum <= count) {
                permanents.remove(card);
            }
            else {
                permanents.put(card, cardNum - count);
            }
        }
    }
    public void addCard(Card card, int count) {
        if (count <= 0) return;
        if (!card.isPermanent) {
            if (!deck.containsKey(card)) {
                deck.put(card, count);
            }
            else {
                int cardNum = deck.get(card);
                deck.put(card, cardNum+count);
            }
        }
        else {
            if (!permanents.containsKey(card)) {
                permanents.put(card, count);
            }
            else {
                int cardNum = permanents.get(card);
                permanents.put(card, cardNum+count);
            }
        }
    }
    public boolean hasCards() {
        return !deck.isEmpty() || !permanents.isEmpty();
    }
    public Card getRandomCard() {
        if (!hasCards()) return null;
        int random = rnd.nextInt(deck.size());
        int pos = 0;
        for (Card card : deck.keySet()) {
            if (pos == random) return card;
            pos++;
        }
        return null;
    }
    public Card getRandomWeightedCard() {//This is the correct function.
        // Calculate the total weight
        int totalWeight = 0;
        for (int weight : deck.values()) {
            totalWeight += weight;
        }
        // Generate a random number between 0 and the total weight
        Random random = new Random();
        int randomWeight = random.nextInt(totalWeight);
        // Iterate through the entries of the map and subtract the weights from the random number
        for (Card key : deck.keySet()) {
            randomWeight -= deck.get(key);
            if (randomWeight < 0) {
                return key;
            }
        }

        // Should never reach here if the map is not empty and weights are positive
        return null;
    }
    public Deck copy() {
        HashMap<Card, Integer> newDeck = new HashMap<Card, Integer>();
        deck.forEach((card, count) -> {
            newDeck.put(card, count);
        });
        HashMap<Card, Integer> newPermanents = new HashMap<Card, Integer>();
        permanents.forEach((card, count) -> {
            newPermanents.put(card, count);
        });
        return new Deck(newDeck, newPermanents);
    }
}
