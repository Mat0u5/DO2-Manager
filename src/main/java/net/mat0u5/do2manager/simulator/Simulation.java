package net.mat0u5.do2manager.simulator;

import net.minecraft.entity.player.PlayerEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Simulation {
    PlayerEntity messagePlayer;
    int skipFirstCards = 0;
    int cardsUntilArtifact = 10;
    int currentRecycles = 0;
    int currentRunLength = 0;
    int currentCardsPlayed = 0;
    int currentColdSnap = 0;
    int currentStrideWithPride = 0;
    boolean currentCashCow = false;
    boolean artifactPicked = false;
    boolean speedRunnerCollected = false;
    boolean riskyReservesTriggered = false;

    boolean stopSimulation = false;
    Deck deck = new Deck();
    public Simulation() {
    }
    public Simulation(Deck deck, PlayerEntity player) {
        this.deck = deck;
        this.messagePlayer = player;
    }
    public void stopSimulation() {
        stopSimulation = true;
    }
    public double runSimulation() {
        Random rnd = new Random();
        double[] avgC = {0,0};//NumGames,TotalValueGames
        double[] avgH = {0,0};
        double[] avgCB = {0,0};
        double[] avgHB = {0,0};
        double[] avgTQ = {0,0};
        double[] avgEQ = {0,0};
        double[] avgR = {0,0};
        double[] length = {0,0};
        double[][] avgEFF = {{0,0,0,0},{0,0,0,0}};
        long gamesPlayed = 0;

        while(gamesPlayed < 1 && !stopSimulation) {
            gamesPlayed++;
            int clankGenerated = 0;
            int hazardGenerated = 0;
            int clankBlock = 0;
            int hazardBlock = 0;
            int treasureQ = 0;
            int emberQ = 0;
            int recycleTotal = 0;
            int recycle = currentRecycles;
            int[] gameEffects = {0,0,0,0};
            int skipCards = skipFirstCards;
            int cardsPlayed = currentCardsPlayed;
            int runLength = currentRunLength;
            //Permanents/Customs
            int lastSpeedQueued = 0;
            int lastStumbleGivenTime = 0;
            boolean artiPicked = artifactPicked;
            boolean riskyReserved = riskyReservesTriggered;

            String cardsPlayedStr = "";
            HashMap<String, Integer> currentCardEffects = new HashMap<String, Integer>();
            //
            Deck deckCopy = deck.copy();

            while (deckCopy.hasCards()) {
                Card randomCard = null;
                if (deckCopy.permanents.size() > 0) {
                    randomCard = (Card) deckCopy.permanents.keySet().toArray()[0];
                    deckCopy.removeCard(randomCard, 1);
                    cardsPlayedStr+="\nPermanent Played: " + randomCard.cardName;
                    System.out.println("Permanented: " + randomCard.cardName);
                }
                else {
                    if (currentCardEffects.containsKey("fuzzy_bunny_slippers")) {
                        runLength += 15;
                    }
                    else {
                        runLength += 30;
                    }
                    cardsPlayed++;
                    if ((runLength-lastStumbleGivenTime)/120>1) {
                        deckCopy.addCard(new Cards().Stumble, 1);
                        lastStumbleGivenTime = runLength;
                    }
                    lastSpeedQueued = gameEffects[0];
                    if (cardsPlayed== cardsUntilArtifact && currentCardEffects.containsKey("glorious_moment") && !artiPicked) {
                        cardsPlayedStr+="\n_Artifact Picked";
                        System.out.println("_ARTI_PICK");
                        artiPicked = true;
                        clankBlock+=3;
                        hazardBlock += 3;
                        recycle += 3;
                        recycleTotal += 3;
                    }
                    randomCard = deckCopy.getRandomCard();
                    if (currentCardEffects.containsKey("cold_snap") && rnd.nextInt(4) == 0) {
                        cardsPlayedStr+="\n_Cold Snap End";
                        int prevCount = currentCardEffects.get("cold_snap");
                        if (prevCount <= 1)currentCardEffects.remove("cold_snap");
                        else currentCardEffects.put("cold_snap", prevCount-1);
                        if (!currentCardEffects.containsKey("cold_snap")) {
                            System.out.println("_Cold Snap End");
                        }
                    }
                    if (currentCardEffects.containsKey("boots_of_swiftness") && rnd.nextInt(4) == 0) {
                        if (!(currentCardEffects.containsKey("fuzzy_bunny_slippers") && artiPicked)) gameEffects[0] += 30;
                        gameEffects[1] += 30;
                    }
                    if (currentCardEffects.containsKey("ambrosia") && rnd.nextInt(3) == 0) {
                        gameEffects[2] += 30;
                    }
                    if (currentCardEffects.containsKey("gambler's_paradise")) {
                        int rndNum = rnd.nextInt(100)+1;
                        if (rndNum == 100) {
                            gameEffects[0] += 60;
                            gameEffects[1] += 60;
                            gameEffects[2] += 60;
                            gameEffects[3] += 60;
                            treasureQ+=16;
                            emberQ+=16;
                            clankBlock+=8;
                            hazardBlock+=8;
                        }
                        if (rndNum >=58 && rndNum <=63) {
                            deckCopy.addCard(new Cards().Stumble, 1);
                        }
                        if (rndNum >=70 && rndNum <=75) {
                            clankBlock+=4;
                            hazardBlock+=4;
                        }
							/*if (rndNum >=76 && rndNum <=81) {
								skipCards++;
							}*/
                        if (rndNum >=82 && rndNum <=87) {
                            treasureQ+=6;
                            emberQ+=6;
                        }
                        if (rndNum >=88 && rndNum <=93) {
                            hazardGenerated+=4;
                        }
                        if (rndNum >=94 && rndNum <=99) {
                            clankGenerated+=4;
                        }
                    }
                    if (recycle >= 1 && !randomCard.isEthereal && !randomCard.cardName.equalsIgnoreCase("quickstep") && !randomCard.cardName.equalsIgnoreCase("brilliance")) {
                        recycle--;
                        cardsPlayedStr+="\nRecycled Card: " + randomCard.cardName;
                        System.out.println("Played+Recycled Card: " + randomCard.cardName);
                    }
                    else {
                        deckCopy.removeCard(randomCard, 1);
                        System.out.println("Played: " + randomCard.cardName);
                    }
                    if (skipCards > 0) {
                        skipCards--;
                        cardsPlayedStr+="\nSkipped Card: " + randomCard.cardName;
                        System.out.println("Skipped Card: " + randomCard.cardName);
                        continue;
                    }
                    cardsPlayedStr+="\nPlayed: " + randomCard.cardName;
                }
                if (currentCardEffects.containsKey("chill_step") && randomCard.cardName.equalsIgnoreCase("sneak")) {
                    emberQ+=(2*currentCardEffects.get("chill_step")*(currentCardEffects.containsKey("cold_snap")?2:1));
                }
                if (currentCardEffects.containsKey("cash_cow")) {
                    treasureQ+=currentCardEffects.get("cash_cow");
                }

                String cardEffect = randomCard.cardEffect;
                List<String> effects = Arrays.stream(cardEffect.split(",")).collect(Collectors.toList());
                for (String effect : effects) {
                    if (effect.matches("[0-9]+CB")) clankBlock+=Integer.parseInt(effect.split("CB")[0]);
                    if (effect.matches("[0-9]+HB")) hazardBlock+=Integer.parseInt(effect.split("HB")[0]);
                    if (effect.matches("[0-9]+C")) clankGenerated+=Integer.parseInt(effect.split("C")[0]);
                    if (effect.matches("[0-9]+H")) hazardGenerated+=Integer.parseInt(effect.split("H")[0]);
                    if (effect.matches("[0-9]+T")) treasureQ+=Integer.parseInt(effect.split("T")[0]);
                    if (effect.matches("[0-9]+E")) emberQ+=(Integer.parseInt(effect.split("E")[0])*(currentCardEffects.containsKey("cold_snap")?2:1));
                    if (effect.matches("[0-9]+R")) {
                        recycleTotal+=Integer.parseInt(effect.split("R")[0]);
                        recycle+=Integer.parseInt(effect.split("R")[0]);
                    }
                    if (effect.matches("[0-9]+S") && !(currentCardEffects.containsKey("fuzzy_bunny_slippers") && artiPicked)) gameEffects[0]+=Integer.parseInt(effect.split("S")[0]);
                    if (effect.matches("[0-9]+J")) gameEffects[1]+=Integer.parseInt(effect.split("J")[0]);
                    if (effect.matches("[0-9]+RG")) gameEffects[2]+=Integer.parseInt(effect.split("RG")[0]);
                    if (effect.matches("[0-9]+RS")) gameEffects[3]+=Integer.parseInt(effect.split("RS")[0]);
                    if (effect.matches("[0-9]+Skip")) skipCards+=Integer.parseInt(effect.split("Skip")[0]);
                    if (effect.matches("[0-9]+Stumble")) {
                        int stumbleCount = Integer.parseInt(effect.split("Stumble")[0]);
                        deckCopy.addCard(new Cards().Stumble, stumbleCount);
                    }

                    if (effect.matches("[0-9]+E\\*")) emberQ+=(Integer.parseInt(effect.split("E")[0])*(currentCardEffects.containsKey("cold_snap")?2:1));//TODO

                    if (effect.equalsIgnoreCase("custom")) {
                        if (randomCard.cardName.equalsIgnoreCase("cold_snap") || randomCard.cardName.equalsIgnoreCase("chill_step") || randomCard.cardName.equalsIgnoreCase("cash_cow")) {
                            if (!currentCardEffects.containsKey(randomCard.cardName)) {
                                currentCardEffects.put(randomCard.cardName, 1);
                            }
                            else {
                                int prevCount = currentCardEffects.get(randomCard.cardName);
                                currentCardEffects.put(randomCard.cardName, prevCount+1);
                            }
                        }
                        if (randomCard.cardName.equalsIgnoreCase("silent_runner") || randomCard.cardName.equalsIgnoreCase("boots_of_swiftness") || randomCard.cardName.equalsIgnoreCase("fuzzy_bunny_slippers") || randomCard.cardName.equalsIgnoreCase("ambrosia") || randomCard.cardName.equalsIgnoreCase("glorious_moment") || randomCard.cardName.equalsIgnoreCase("gambler's_paradise") || randomCard.cardName.equalsIgnoreCase("risky_reserves")) {
                            currentCardEffects.put(randomCard.cardName, 1);
                        }
                        if (randomCard.cardName.equalsIgnoreCase("avalanche")) {
                            emberQ+=(cardsPlayed*(currentCardEffects.containsKey("cold_snap")?2:1));
                        }
                    }
                }
                if (currentCardEffects.containsKey("risky_reserves") && !deckCopy.hasCards() && !riskyReserved) {
                    emberQ+=14*(currentCardEffects.containsKey("cold_snap")?2:1);
                    treasureQ+=14;
                    riskyReserved = true;
                    System.out.println("_Risky Reserves Played");
                }
                if (currentCardEffects.containsKey("silent_runner")) {
                    int count = (gameEffects[0] - lastSpeedQueued)/15;
                    for (int i = 0; i < count; i++) {
                        if (rnd.nextInt(2)==0) {
                            clankBlock+=1;
                        }
                    }
                }
            }
            avgC[0]++;
            avgC[1] += clankGenerated;
            avgH[0]++;
            avgH[1] += hazardGenerated;
            avgCB[0]++;
            avgCB[1] += clankBlock;
            avgHB[0]++;
            avgHB[1] += hazardBlock;
            avgTQ[0]++;
            avgTQ[1] += treasureQ;
            avgEQ[0]++;
            avgEQ[1] += emberQ;
            avgR[0]++;
            avgR[1] += recycleTotal;
            avgEFF[0][0]++;
            avgEFF[0][1]++;
            avgEFF[0][2]++;
            avgEFF[0][3]++;
            avgEFF[1][0]+= gameEffects[0];
            avgEFF[1][1]+= gameEffects[1];
            avgEFF[1][2]+= gameEffects[2];
            avgEFF[1][3]+= gameEffects[3];
            length[0]++;
            length[1] += runLength;
        }
        if (gamesPlayed%1==0) {
            System.out.println("Games Played: " + gamesPlayed);
            if (length[0] > 0 && length[1] > 0) System.out.println("RunLength: " + Math.round(length[1]/length[0]*100)/100.0);
            if (avgC[0] > 0 && avgC[1] > 0) System.out.println("clankGenerated: " + Math.round(avgC[1]/avgC[0]*100)/100.0);
            if (avgH[0] > 0 && avgH[1] > 0) System.out.println("hazardGenerated: " + Math.round(avgH[1]/avgH[0]*100)/100.0);
            if (avgCB[0] > 0 && avgCB[1] > 0) System.out.println("clankBlock: " + Math.round(avgCB[1]/avgCB[0]*100)/100.0);
            if (avgHB[0] > 0 && avgHB[1] > 0) System.out.println("hazardBlock: " + Math.round(avgHB[1]/avgHB[0]*100)/100.0);
            if (avgTQ[0] > 0 && avgTQ[1] > 0) System.out.println("treasureQ: " + Math.round(avgTQ[1]/avgTQ[0]*100)/100.0);
            if (avgEQ[0] > 0 && avgEQ[1] > 0) System.out.println("emberQ: " + Math.round(avgEQ[1]/avgEQ[0]*100)/100.0);
            if (avgR[0] > 0 && avgR[1] > 0) System.out.println("recyclesNum: " + Math.round(avgR[1]/avgR[0]*100)/100.0);
            if (avgEFF[0][0] > 0 && avgEFF[1][0] > 0)System.out.println("Speed: " + Math.round(avgEFF[1][0]/avgEFF[0][0]*100)/100.0);
            if (avgEFF[0][1] > 0 && avgEFF[1][1] > 0)System.out.println("Jump: " + Math.round(avgEFF[1][1]/avgEFF[0][1]*100)/100.0);
            if (avgEFF[0][2] > 0 && avgEFF[1][2] > 0)System.out.println("Regen: " + Math.round(avgEFF[1][2]/avgEFF[0][2]*100)/100.0);
            if (avgEFF[0][3] > 0 && avgEFF[1][3] > 0)System.out.println("Resistance: " + Math.round(avgEFF[1][3]/avgEFF[0][3]*100)/100.0);
            System.out.println("AllEffects: "+(Math.round(avgEFF[1][0]/avgEFF[0][0]*100)/100.0+Math.round(avgEFF[1][1]/avgEFF[0][1]*100)/100.0+Math.round(avgEFF[1][2]/avgEFF[0][2]*100)/100.0+Math.round(avgEFF[1][3]/avgEFF[0][3]*100)/100.0));
            System.out.println("\n");
            //For The Deck Analyzer:
            /*
            System.out.println(String.valueOf(Math.round(avgTQ[1]/avgTQ[0]*100)/100.0).replaceAll("\\.",","));
            System.out.println(String.valueOf(Math.round(avgEQ[1]/avgEQ[0]*100)/100.0).replaceAll("\\.",","));
            System.out.println(String.valueOf(Math.round(avgHB[1]/avgHB[0]*100)/100.0).replaceAll("\\.",","));
            System.out.println(String.valueOf(Math.round(avgCB[1]/avgCB[0]*100)/100.0).replaceAll("\\.",","));
            System.out.println(String.valueOf(Math.round(length[1]/length[0]*100)/100.0).replaceAll("\\.",","));
            System.out.println(String.valueOf(Math.round(avgEFF[1][0]/avgEFF[0][0]*100)/100.0+Math.round(avgEFF[1][1]/avgEFF[0][1]*100)/100.0+Math.round(avgEFF[1][2]/avgEFF[0][2]*100)/100.0+Math.round(avgEFF[1][3]/avgEFF[0][3]*100)/100.0).replaceAll("\\.",","));
*/
				/*System.out.println("\n");
				System.out.println("Lowest Embers: " + lowestEmbers + "_" + lowestEmbersCardPlays);
				System.out.println("\n");
				System.out.println("\n");
				System.out.println("Highest Embers: " + highestEmbers + "_" + highestEmbersCardPlays);*/
            //if (avgEQ[0] > 0 && avgEQ[1] > 0) return  Math.round(avgEQ[1]/avgEQ[0]*100)/100.0;
            //if (avgCB[0] > 0 && avgCB[1] > 0) return Math.round(avgCB[1]/avgCB[0]*100)/100.0;
            //if (avgHB[0] > 0 && avgHB[1] > 0) return Math.round(avgHB[1]/avgHB[0]*100)/100.0;
            //if (avgTQ[0] > 0 && avgTQ[1] > 0) return Math.round(avgTQ[1]/avgTQ[0]*100)/100.0;
            //return Math.round(avgEFF[1][0]/avgEFF[0][0]*100)/100.0+Math.round(avgEFF[1][1]/avgEFF[0][1]*100)/100.0+Math.round(avgEFF[1][2]/avgEFF[0][2]*100)/100.0+Math.round(avgEFF[1][3]/avgEFF[0][3]*100)/100.0;
            if (length[0] > 0 && length[1] > 0) return Math.round(length[1]/length[0]*100)/100.0;
        }
        return -1;
    }
}
