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
