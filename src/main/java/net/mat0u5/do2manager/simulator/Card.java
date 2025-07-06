package net.mat0u5.do2manager.simulator;

public class Card {
    String cardName;
    String cardEffect;
    boolean isPermanent;
    boolean isEthereal;
    int rarity;
    int maxCount = -1;
    public Card(String cardName, String cardEffect,boolean isPermanent,boolean isEthereal) {
        this.cardName = cardName;
        this.cardEffect = cardEffect;
        this.isPermanent = isPermanent;
        this.isEthereal = isEthereal;
    }
    public Card(String cardName, String cardEffect,boolean isPermanent,boolean isEthereal, int maxCount) {
        this.cardName = cardName;
        this.cardEffect = cardEffect;
        this.isPermanent = isPermanent;
        this.isEthereal = isEthereal;
        this.maxCount = maxCount;
    }
}
