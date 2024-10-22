package net.mat0u5.do2manager.simulator;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.utils.MSPTUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

import java.util.*;
import java.util.stream.Collectors;

public class Simulation extends MSPTUtils {
    PlayerEntity messagePlayer;
    int skipFirstCards = 4;
    int cardsUntilArtifact = 6;
    int currentRecycles = 0;
    int currentRunLength = 0;
    int currentCardsPlayed = 0;
    int currentColdSnap = 0;
    int currentStrideWithPride = 0;
    int currentCashCow = 0;
    int currentPlayerEmbers = 0;
    int currentPlayerArtiValue = 0;
    int currentFloorEmbers = 0;
    int currentBombEmbers = 0;
    int currentBombCrowns = 0;
    int currentDiveEmbers = 0;
    int currentEmbersQueued = 0;

    boolean artifactPicked = false;
    boolean speedRunnerCollected = false;
    boolean riskyReservesTriggered = false;
    boolean bombCollected = false;
    boolean rustyCollected = false;
    boolean diveCollected = false;
    boolean hideoutCompleted = false;
    Card firstCardPlay;


    final double emberQueueMultiplier = 135.0/98.0;
    final double treasureQueueMultiplier = 1.55;
    boolean stopSimulation = false;
    boolean deleteAfter = false;
    int maxRuns = 10000;
    List<String> printOut = new ArrayList<>();
    Deck deck = new Deck();
    public Simulation() {
    }
    public Simulation(Deck deck, PlayerEntity player) {
        this.deck = deck;
        this.messagePlayer = player;
    }
    public Simulation(Deck deck, PlayerEntity player, boolean deleteAfter, boolean dontSkipCards, List<String> printOut) {
        this.deck = deck;
        this.messagePlayer = player;
        this.printOut = printOut;
        this.deleteAfter = deleteAfter;
        if (dontSkipCards) {
            skipFirstCards = 0;
        }
    }
    public Simulation(Deck deck, PlayerEntity player, boolean deleteAfter, boolean dontSkipCards, List<String> printOut, List<Integer> argsInt, List<Boolean> argsBool, Card firstCardPlay) {
        this.deck = deck;
        this.messagePlayer = player;
        this.printOut = printOut;
        this.firstCardPlay = firstCardPlay;
        this.deleteAfter = deleteAfter;

        skipFirstCards = argsInt.get(0);
        cardsUntilArtifact = argsInt.get(1);
        currentRecycles = argsInt.get(2);
        currentRunLength = argsInt.get(3);
        currentCardsPlayed = argsInt.get(4);
        currentColdSnap = argsInt.get(5);
        currentStrideWithPride = argsInt.get(6);
        currentCashCow = argsInt.get(7);
        currentPlayerEmbers = argsInt.get(8);
        currentPlayerArtiValue = argsInt.get(9);
        currentFloorEmbers = argsInt.get(10);
        currentBombEmbers = argsInt.get(11);
        currentBombCrowns = argsInt.get(12);
        currentDiveEmbers = argsInt.get(13);
        currentEmbersQueued = argsInt.get(14);


        artifactPicked = argsBool.get(0);
        speedRunnerCollected = argsBool.get(1);
        riskyReservesTriggered = argsBool.get(2);
        bombCollected = argsBool.get(3);
        rustyCollected = argsBool.get(4);
        diveCollected = argsBool.get(5);
        hideoutCompleted = argsBool.get(6);
        if (dontSkipCards) {
            skipFirstCards = 0;
        }
        /*System.out.println("__________");
        System.out.println("__________");
        System.out.println("skipFirstCards"+skipFirstCards);
        System.out.println("cardsUntilArtifact"+cardsUntilArtifact);
        System.out.println("currentRecycles"+currentRecycles);
        System.out.println("currentRunLength"+currentRunLength);
        System.out.println("currentCardsPlayed"+currentCardsPlayed);
        System.out.println("currentColdSnap"+currentColdSnap);
        System.out.println("currentStrideWithPride"+currentStrideWithPride);
        System.out.println("currentCashCow"+currentCashCow);
        System.out.println("currentPlayerEmbers"+currentPlayerEmbers);
        System.out.println("currentPlayerArtiValue"+currentPlayerArtiValue);
        System.out.println("currentFloorEmbers"+currentFloorEmbers);
        System.out.println("currentBombEmbers"+currentBombEmbers);
        System.out.println("currentBombCrowns"+currentBombCrowns);
        System.out.println("currentDiveEmbers"+currentDiveEmbers);
        System.out.println("_currentEmbersQueued"+currentEmbersQueued);
        System.out.println("__________");
        System.out.println("artifactPicked"+artifactPicked);
        System.out.println("speedRunnerCollected"+speedRunnerCollected);
        System.out.println("riskyReservesTriggered"+riskyReservesTriggered);
        System.out.println("bombCollected"+bombCollected);
        System.out.println("rustyCollected"+rustyCollected);
        System.out.println("diveCollected"+diveCollected);
        System.out.println("hideoutCompleted"+hideoutCompleted);
        System.out.println("__________");
        System.out.println("__________");*/
    }
    public void stopSimulation() {
        stopSimulation = true;
    }
    public void runSimulation() {
        runSimulation(10000);
    }
    public void runSimulation(int maxRuns) {
        this.maxRuns = maxRuns;
        start(Main.server);
    }
    public double round(double round, double decimals){
        double tens = Math.pow(10,decimals);
        return ((int)Math.round(round*tens)/tens);
    }
    public String getComparisonToLastRun(int currentTotal) {
        int diff = currentTotal-Main.simulator.lastTotalEmbers;
        return "§r ["+(diff<0?"§c":"§a+")+diff+"]";
    }
    public void printRunInfo() {
        String text = "\n§5[DO2-Manager] Simulated " + gamesPlayed+" runs.§r {CardsLeft: "+deck.getNonPermanentCardCount()+"}";
        if (printOut.contains("emberMAXX") || printOut.contains("all")) {
            String addToText = "\n  ";
            double embersQueued=round(currentEmbersQueued*emberQueueMultiplier,0);
            double embersToQueue=round(getMean(avgEQ)*emberQueueMultiplier,0);
            double embersTotal = embersQueued+embersToQueue+currentPlayerEmbers+currentPlayerArtiValue+currentFloorEmbers;
            addToText+="  CURRENT_INV="+currentPlayerEmbers;
            addToText+=",  CURRENT_ARTI="+currentPlayerArtiValue;
            addToText+=",  CURRENT_FLOOR="+currentFloorEmbers;
            addToText+="\n    QueuedRN="+embersQueued+" ["+currentEmbersQueued+"]";
            addToText+="\n    ToQueue="+embersToQueue;
            if (!artifactPicked) {
                addToText+=",  Artifact=55";
                embersTotal+=55;
            }
            if (!bombCollected) {
                addToText+=",  Bomb="+currentBombEmbers;
                embersTotal+=currentBombEmbers;
            }
            if (artifactPicked && currentPlayerArtiValue==60 && !hideoutCompleted) {
                addToText+=",  Hideout=24";
                embersTotal+=24;
            }
            if (!diveCollected) {
                addToText+=",  Dive="+currentDiveEmbers;
                embersTotal+=currentDiveEmbers;
            }
            if (!rustyCollected) {
                addToText+=",  Rusty=6";
                embersTotal+=6;
            }
            addToText+=",  StairEmbers=14";
            embersTotal+=14;
            Cards cards = new Cards();
            if (deck.hasCard(cards.SpeedRunner) && !speedRunnerCollected) {
                addToText+=",  Speedrunner=8";
                embersTotal+=8;
            }
            if (deck.hasCard(cards.OneForTheRoad)) {
                addToText+=",  OFTR=9";
                embersTotal+=9;
            }
            text += "\n  §3Embers: "+getMean(avgEQ)+" ["+round(getPercentageOverThousand(avgEQ, (int) (embersTotal-embersToQueue)),3)+"% chance over 1000]§r";
            text += "\n    §rEmber distribution: Total= §3"+embersTotal+getComparisonToLastRun((int) embersTotal)+"§r"+addToText+"\n";
            Main.simulator.lastTotalEmbers = (int) embersTotal;
        }
        if (printOut.contains("ember") || printOut.contains("all")) text += "\n  §rEmbers: "+getMean(avgEQ);
        if (printOut.contains("treasure") || printOut.contains("all")) text += "\n  §rTreasure: "+getMean(avgTQ);
        if (printOut.contains("clank") || printOut.contains("all")) text += "\n  §rClank: "+getMean(avgC);
        if (printOut.contains("hazard") || printOut.contains("all")) text += "\n  §rHazard: "+getMean(avgH);
        if (printOut.contains("clankBlock") || printOut.contains("all")) text += "\n  §rClankBlock: "+getMean(avgCB);
        if (printOut.contains("hazardBlocks") || printOut.contains("all")) text += "\n  §rHazardBlock: "+getMean(avgHB);
        if (printOut.contains("effects") || printOut.contains("all")) {
            double[] meanEffects = getMeanArrays(avgEFF);
            text += "\n  §rEffects: [S,J,RG,RS] = ["+meanEffects[0]+", "+meanEffects[1]+", "+meanEffects[2]+", "+meanEffects[3]+"]";
        }
        if (printOut.contains("runLength") || printOut.contains("all")) text += "\n  §rRunLength: "+getMean(avgLength);
        if (printOut.contains("recycle") || printOut.contains("all")) text += "\n  §rRecycles: "+getMean(avgR);

        messagePlayer.sendMessage(Text.of(text));
    }
    public double getAverage(List<Integer> list) {
        int total = 0;
        int sum = 0;
        for (int i : list) {
            total++;
            sum += i;
        }
        if (sum == 0 || total == 0) return 0;
        return (double) sum /total;
    }
    public double getMean(List<Integer> list) {
        Collections.sort(list);
        if (list.isEmpty()) return 0;
        int mid = list.size()/2;
        return list.size()%2==0?(list.get(mid)+list.get(mid-1))/2.0:list.get(mid);
    }
    public double[] getMeanArrays(List<Integer[]> listArrays) {
        double[] result = new double[4];
        List<List<Integer>> listLists = new ArrayList<>();
        listLists.add(new ArrayList<>());
        listLists.add(new ArrayList<>());
        listLists.add(new ArrayList<>());
        listLists.add(new ArrayList<>());
        for (Integer[] arr : listArrays) {
            listLists.get(0).add(arr[0]);
            listLists.get(1).add(arr[1]);
            listLists.get(2).add(arr[2]);
            listLists.get(3).add(arr[3]);
        }
        int pos = 0;
        for (List<Integer> list : listLists) {
            Collections.sort(list);
            if (list.isEmpty()) return new double[]{0,0,0,0};
            int mid = list.size()/2;
            double value =list.size()%2==0?(list.get(mid)+list.get(mid-1))/2.0:list.get(mid);
            result[pos] = value;
            pos++;
        }
        return result;
    }
    public double getPercentageOverThousand(List<Integer> list, int addOn) {
        Collections.sort(list);
        int result = 0;
        for (int i : list) {
            double embers = i*emberQueueMultiplier;
            if ((embers+addOn) >=1000) result++;
        }
        if (result == 0) return 0;
        return (double) result /list.size();
    }


    List<Integer> avgC = new ArrayList<>();
    List<Integer> avgH = new ArrayList<>();
    List<Integer> avgCB = new ArrayList<>();
    List<Integer> avgHB = new ArrayList<>();
    List<Integer> avgTQ = new ArrayList<>();
    List<Integer> avgEQ = new ArrayList<>();
    List<Integer> avgR = new ArrayList<>();
    List<Integer> avgLength = new ArrayList<>();
    List<Integer[]> avgEFF = new ArrayList<>();
    long gamesPlayed = 0;
    int highestEm = 0;
    String highestEmStr = "";
    @Override
    protected void stoppedFunction() {
        printRunInfo();
        if (deleteAfter) {
            Main.simulator.deleteInfo();
        }
    }
    @Override
    protected void complexFunction() {
        Random rnd = new Random();
        if (gamesPlayed >= maxRuns || stopSimulation) {
            stop();
        }
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
        int runLength = 0;
        //Permanents/Customs
        int lastSpeedQueued = 0;
        int lastStumbleGivenTime = 0;
        boolean artiPicked = artifactPicked;
        boolean riskyReserved = riskyReservesTriggered;
        boolean firstCardPlayed = false;

        String cardsPlayedStr = "";
        HashMap<String, Integer> currentCardEffects = new HashMap<String, Integer>();
        //
        Deck deckCopy = deck.copy();

        while (deckCopy.hasCards()) {
            Card randomCard = null;
            if (deckCopy.permanents.size() > 0) {
                //To Play permanents first
                randomCard = (Card) deckCopy.permanents.keySet().toArray()[0];
                deckCopy.removeCard(randomCard, 1);
                cardsPlayedStr+="\nPermanent Played: " + randomCard.cardName;
                //System.out.println("Permanented: " + randomCard.cardName);
            }
            else {
                // Card Played Effects
                if (currentCardEffects.containsKey("fuzzy_bunny_slippers") && artiPicked) {
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
                if (((cardsPlayed == cardsUntilArtifact && cardsUntilArtifact > 0) || (deckCopy.deck.size()<10 && cardsUntilArtifact==-1)) && currentCardEffects.containsKey("glorious_moment") && !artiPicked) {
                    cardsPlayedStr+="\n_Artifact Picked";
                    //System.out.println("_ARTI_PICK");
                    artiPicked = true;
                    clankBlock+=3;
                    hazardBlock += 3;
                    recycle += 3;
                    recycleTotal += 3;
                }
                if (!firstCardPlayed && firstCardPlay != null) {
                    randomCard = firstCardPlay;
                }
                else {
                    randomCard = deckCopy.getRandomWeightedCard();
                }
                firstCardPlayed=true;
                if (currentCardEffects.containsKey("cold_snap") && rnd.nextInt(4) == 0) {
                    cardsPlayedStr+="\n_Cold Snap End";
                    int prevCount = currentCardEffects.get("cold_snap");
                    if (prevCount <= 1)currentCardEffects.remove("cold_snap");
                    else currentCardEffects.put("cold_snap", prevCount-1);
                    if (!currentCardEffects.containsKey("cold_snap")) {
                        //System.out.println("_Cold Snap End");
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
                        emberQ+=16*(currentCardEffects.containsKey("cold_snap")?2:1);
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
                        emberQ+=6*(currentCardEffects.containsKey("cold_snap")?2:1);
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
                    //System.out.println("Played+Recycled Card: " + randomCard.cardName);
                }
                else {
                    deckCopy.removeCard(randomCard, 1);
                    //System.out.println("Played: " + randomCard.cardName);
                }
                if (skipCards > 0) {
                    skipCards--;
                    cardsPlayedStr+="\nSkipped Card: " + randomCard.cardName;
                    //System.out.println("Skipped Card: " + randomCard.cardName);
                    continue;
                }
                cardsPlayedStr+="\nPlayed: " + randomCard.cardName;
            }
            if (currentCardEffects.containsKey("chill_step") && randomCard.cardName.equalsIgnoreCase("sneak")) {
                emberQ+=(2*currentCardEffects.get("chill_step")*(currentCardEffects.containsKey("cold_snap")?2:1));
            }
            if (currentCardEffects.containsKey("cash_cow")) {
                treasureQ+=currentCardEffects.get("cash_cow");
                //treasureQ+=1;
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

                if (effect.matches("[0-9]+E\\*")) emberQ+=(Integer.parseInt(effect.split("E")[0])*(currentCardEffects.containsKey("cold_snap")?2:1));

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
                //System.out.println("_Risky Reserves Played");
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
        avgC.add(clankGenerated);
        avgH.add(hazardGenerated);
        avgCB.add(clankBlock);
        avgHB.add(hazardBlock);
        avgTQ.add(treasureQ);
        avgEQ.add(emberQ);
        avgR.add(recycleTotal);
        avgC.add(clankGenerated);
        avgLength.add(runLength);
        avgEFF.add(new Integer[]{gameEffects[0],gameEffects[1],gameEffects[2],gameEffects[3]});
        if (highestEm < emberQ) {
            highestEm = emberQ;
            highestEmStr = cardsPlayedStr;
        }
    }
}
