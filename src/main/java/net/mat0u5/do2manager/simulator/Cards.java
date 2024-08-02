package net.mat0u5.do2manager.simulator;

public class Cards {
    public final Card Stumble = new Card("stumble","2C",false,true,0);

    public final Card PayToWin = new Card("pay_to_win","10E",false,true,3);
    public final Card PorkchopPower = new Card("pork_chop_power","custom",true,true,1);
    public final Card TacticalApproach = new Card("tactical_approach","5CB,5T",true,true,1);

    public final Card Sneak = new Card("sneak","2CB",false,false,5);
    public final Card Stability = new Card("stability","2HB",false,false,5);
    public final Card TreasureHunter = new Card("treasure_hunter","4T",false,false,5);
    public final Card EmberSeeker = new Card("ember_seeker","2E",false,false,5);
    public final Card MoC = new Card("moment_of_clarity","2CB,2HB,4T,2E",false,true,5);

    public final Card Evasion = new Card("evasion","4CB",false,false,3);
    public final Card TreadLightly = new Card("tread_lightly","4HB",false,false,3);
    public final Card FrostFocus = new Card("frost_focus","4E",false,false,3);
    public final Card LootAndScoot = new Card("loot_and_scoot","7T,15S",false,false,3);
    public final Card SecondWind = new Card("second_wind","30RG,30S",false,false,3);
    public final Card BeastSense = new Card("beast_sense","1C,Glow",false,false,3);//
    public final Card BoundingStrides = new Card("bounding_strides","2HB,120J",false,false,3);
    public final Card RecklessCharge = new Card("reckless_charge","2H,10E*",false,false,3);//
    public final Card Sprint = new Card("sprint","60S",false,false,3);
    public final Card NimbleLooting = new Card("nimble_looting","1C,2T,custom",false,false,3);//
    public final Card SmashAndGrab = new Card("smash_and_grab","13T,2C",false,false,3);
    public final Card Quickstep = new Card("quickstep","2CB,15S,1R",false,false,3);
    public final Card SuitUp = new Card("suit_up","custom",true,false,1);//
    public final Card AdrenalineRush = new Card("adrenaline_rush","1H,33T",false,false,3);//
    public final Card FrozenTears = new Card("frozen_tears","20E",false,false,3);//
    public final Card RavenousInclination = new Card("ravenous_inclination","6E",false,false,3);//
    public final Card OneForTheRoad = new Card("one_for_the_road","custom",true,false,1);

    public final Card EerieSilence = new Card("eerie_silence","2HB,8CB,1Skip",false,false,3);
    public final Card DungeonRepairs = new Card("dungeon_repairs","1C,7HB",false,false,3);
    public final Card Swagger = new Card("swagger","10T,10E,2Stumble",false,false,3);
    public final Card ChillStep = new Card("chill_step","custom",false,false,3);//done
    public final Card SpeedRunner = new Card("speed_runner","8E*",true,false,1);
    public final Card EyesOnThePrize = new Card("eyes_on_the_prize","custom",false,false,3);//
    public final Card PiratesBooty = new Card("pirate's_booty","custom",false,false,1);//
    public final Card ColdSnap = new Card("cold_snap","2H,custom",false,false,3);//done
    public final Card SilentRunner = new Card("silent_runner","custom",true,false,1);//done
    public final Card FuzzyBunnySlippers = new Card("fuzzy_bunny_slippers","custom",true,false,1);//
    public final Card Deepfrost = new Card("deepfrost","18E*",false,false,3);
    public final Card Brilliance = new Card("brilliance","2R",false,false,3);
    public final Card SneakyPants = new Card("sneaky_pants","custom",true,false,1);

    public final Card GloriousMoment = new Card("glorious_moment","custom",true,false,1);//1
    public final Card BeastMaster = new Card("beast_master","custom",true,false,1);//
    public final Card CashCow = new Card("cash_cow","custom",false,false,1);//done
    public final Card Avalanche = new Card("avalanche","custom",false,false,1);//done
    public final Card BootsOfSwiftness = new Card("boots_of_swiftness","custom",true,false,1);//done
    public final Card Eureka = new Card("eureka","12CB,12HB,16T,12E",false,true,1);
    public final Card Enlightenment = new Card("enlightenment","custom",false,false,1);
    public final Card Revelation = new Card("revelation","3CB,3HB,6T,3E",false,true,3);
    public final Card Ambrosia = new Card("ambrosia","custom",true,false,1);//done
    public final Card Zoltraak = new Card("zoltraak","custom",true,false,1);
    public final Card StrideWithPride = new Card("stride_with_pride","2H,4T,custom",false,false,3);
    public final Card DelvingGrace = new Card("delving_grace","3C,3H,12T",true,true,1);

    public final Card GamblersParadise = new Card("gambler's_paradise","custom",true,false,1);//done
    public final Card RiskyReserves = new Card("risky_reserves","custom",true,false,1);//done

    public Card[] allCards = {
            Stumble,Sneak,Stability,TreasureHunter,EmberSeeker,MoC,Evasion,TreadLightly,FrostFocus,LootAndScoot,SecondWind
            ,BeastSense,BoundingStrides,RecklessCharge,Sprint,NimbleLooting, SmashAndGrab, Quickstep,SuitUp,AdrenalineRush
            ,EerieSilence,DungeonRepairs,Swagger,ChillStep,SpeedRunner,PiratesBooty,ColdSnap,SilentRunner,FuzzyBunnySlippers,EyesOnThePrize
            ,Deepfrost,Brilliance,GloriousMoment,BeastMaster,CashCow,Avalanche,BootsOfSwiftness,Eureka,Enlightenment,Revelation,Ambrosia
            ,PayToWin,PorkchopPower,TacticalApproach,GamblersParadise,RiskyReserves,FrozenTears,RavenousInclination,OneForTheRoad,SneakyPants
            ,Zoltraak,StrideWithPride,DelvingGrace
    };
    public Card getCardFromName(String name) {
        for (Card card : allCards) {
            if (card.cardName.equalsIgnoreCase(name)) return card;
        }
        return null;
    }
}
