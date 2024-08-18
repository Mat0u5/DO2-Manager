package net.mat0u5.do2manager.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.gui.GuiInventory_Database;
import net.mat0u5.do2manager.gui.GuiInventory_ChestFramework;
import net.mat0u5.do2manager.queue.QueueCommand;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.literal;


public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("decked-out")
                .then(literal("console-only")
                    .requires(source -> ((source.getEntity() instanceof ServerPlayerEntity &&"Mat0u5".equals(source.getName()) || (source.getEntity() == null))))
                    .executes(context -> ConsoleCommand.execute(
                        context.getSource())
                    )
                    .then(literal("database")
                        .then(literal("runTracking")
                            .then(literal("prepareForRun")
                                .executes(context -> ConsoleCommand.database_runTracking_PrepareForRun(
                                    context.getSource())
                                )
                            )
                            .then(literal("var_modify")
                                .then(CommandManager.argument("query", StringArgumentType.string())
                                    .executes(context -> ConsoleCommand.database_runTracking_modifyVar(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "query"))
                                    )
                                )
                            )
                            .then(literal("var_modify_premade")
                                .then(literal("items")
                                    .then(CommandManager.argument("functionName", StringArgumentType.string())
                                        .then(CommandManager.argument("targets", EntityArgumentType.entities())
                                            .executes(context -> ConsoleCommand.database_runTracking_Items(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "functionName"),
                                                EntityArgumentType.getEntities(context, "targets"))
                                            )
                                        )
                                    )
                                )
                                .then(literal("timestamp")
                                    .then(CommandManager.argument("timestampName", StringArgumentType.string())
                                        .executes(context -> ConsoleCommand.database_runTracking_Timestamp(
                                            context.getSource(),
                                            StringArgumentType.getString(context, "timestampName"))
                                        )
                                    )
                                )
                                .then(literal("run_number")
                                    .executes(context -> ConsoleCommand.database_runTracking_RunNumber(
                                        context.getSource())
                                    )
                                )
                                .then(literal("run_difficulty")
                                    .executes(context -> ConsoleCommand.database_runTracking_RunDiff(
                                        context.getSource())
                                    )
                                )
                                .then(literal("deck_item")
                                    .executes(context -> ConsoleCommand.database_runTracking_ItemDeck(
                                        context.getSource())
                                    )
                                )
                                .then(literal("save_inv")
                                    .executes(context -> ConsoleCommand.database_runTracking_ItemInventory(
                                        context.getSource())
                                    )
                                )
                                .then(literal("embers_counted")
                                    .executes(context -> ConsoleCommand.database_runTracking_Embers(
                                        context.getSource())
                                    )
                                )
                                .then(literal("players")
                                    .then(literal("runners")
                                        .executes(context -> ConsoleCommand.database_runTracking_Players(
                                            context.getSource(),
                                    "runners")
                                        )
                                    )
                                    .then(literal("finishers")
                                        .executes(context -> ConsoleCommand.database_runTracking_Players(
                                            context.getSource(),
                                    "finishers")
                                        )
                                    )
                                )
                            )
                            .then(literal("save_run_to_db")
                                .executes(context -> ConsoleCommand.database_runTracking_SaveRun(
                                    context.getSource())
                                )
                            )
                        )
                    )
                )

                ////////
                .then(literal("database")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(literal("getRaw")
                        .then(CommandManager.argument("runNum", IntegerArgumentType.integer())
                            .then(CommandManager.argument("var_name", StringArgumentType.string())
                                .executes(context -> DatabaseCommand.executeGetFromDB(
                                    context.getSource(),
                                    IntegerArgumentType.getInteger(context, "runNum"),
                                    StringArgumentType.getString(context, "var_name"))
                                )
                            )
                        )
                    )

                    .then(literal("commandBlockStartScan")
                        .executes(context -> DatabaseCommand.executeCommandBlockUpdateDatabase(
                                context.getSource(), -672, 165, 1727,-337, -64, 2291)
                        )
                        .then(CommandManager.argument("fromX", IntegerArgumentType.integer())
                        .then(CommandManager.argument("fromY", IntegerArgumentType.integer())
                        .then(CommandManager.argument("fromZ", IntegerArgumentType.integer())
                        .then(CommandManager.argument("toX", IntegerArgumentType.integer())
                        .then(CommandManager.argument("toY", IntegerArgumentType.integer())
                        .then(CommandManager.argument("toZ", IntegerArgumentType.integer())
                            .executes(context -> DatabaseCommand.executeCommandBlockUpdateDatabase(
                                context.getSource(),
                                IntegerArgumentType.getInteger(context, "fromX"),
                                IntegerArgumentType.getInteger(context, "fromY"),
                                IntegerArgumentType.getInteger(context, "fromZ"),
                                IntegerArgumentType.getInteger(context, "toX"),
                                IntegerArgumentType.getInteger(context, "toY"),
                                IntegerArgumentType.getInteger(context, "toZ"))
                            )
                        )
                        )
                        )
                        )
                        )
                        )
                    )
                    .then(literal("functionsStartScan")
                        .executes(context -> DatabaseCommand.executeFunctionUpdateDatabase(
                            context.getSource())
                        )
                    )
                )
                .then(literal("gui")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> new GuiInventory_Database().openRunInventory(
                        context.getSource().getPlayer())
                    )
                    .then(literal("allItems")
                        .executes(context -> new GuiInventory_ChestFramework().openChestInventory(
                            context.getSource().getPlayer(),
                        54,
                        "Decked Out 2 Items",
                        "_-629,11,1966;0;1",false)
                        )
                    )
                    .then(literal("runHistory")
                        .executes(context -> new GuiInventory_Database().openRunInventory(
                            context.getSource().getPlayer())
                        )
                    )
                    .then(literal("customGUI")
                        .then(CommandManager.argument("inv_size", IntegerArgumentType.integer())
                            .then(CommandManager.argument("inv_name", StringArgumentType.string())
                                .then(CommandManager.argument("chest_pos", StringArgumentType.string())
                                    .executes(context -> new GuiInventory_ChestFramework().openChestInventory(
                                        context.getSource().getPlayer(),
                                        IntegerArgumentType.getInteger(context, "inv_size"),
                                        StringArgumentType.getString(context, "inv_name"),
                                        StringArgumentType.getString(context, "chest_pos"),false)
                                    )
                                )
                            )
                        )
                    )
                )
                .then(literal("testing")
                    .requires(source -> source.getEntity() instanceof ServerPlayerEntity &&"Mat0u5".equals(source.getName()))
                    .executes(context -> TestingCommand.execute(
                        context.getSource())
                    )
                    .then(literal("test")
                        .executes(context -> TestingCommand.executeTest(
                            context.getSource())
                        )
                    )
                    .then(literal("testAddRun")
                        .executes(context -> TestingCommand.executeAddRun(
                            context.getSource())
                        )
                    )
                    .then(literal("execute")
                        .then(CommandManager.argument("args", StringArgumentType.string())
                            .executes(context -> TestingCommand.executeCmd(
                                StringArgumentType.getString(context, "args"))
                            )
                        )
                    )
                )
                .then(literal("simulator")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(literal("card_played")
                        .requires(source -> ((source.getEntity() instanceof ServerPlayerEntity &&"Mat0u5".equals(source.getName()) || (source.getEntity() == null))))
                        .executes(context -> Main.simulator.cardPlayed(
                            context.getSource())
                        )
                    )
                    .then(literal("save_permanents")
                        .requires(source -> ((source.getEntity() instanceof ServerPlayerEntity &&"Mat0u5".equals(source.getName()) || (source.getEntity() == null))))
                        .executes(context -> Main.simulator.saveHand(
                            context.getSource())
                        )
                    )
                    .then(literal("hand_deck")
                        .executes(context -> Main.simulator.runHand(
                            context.getSource(),5000,false)
                        )
                        .then(CommandManager.argument("simulate_runs_num", IntegerArgumentType.integer(1))
                            .executes(context -> Main.simulator.runHand(
                                context.getSource(),
                                IntegerArgumentType.getInteger(context, "simulate_runs_num"),false)
                            )
                            .then(literal("dont_skip_cards")
                                .executes(context -> Main.simulator.runHand(
                                    context.getSource(),IntegerArgumentType.getInteger(context, "simulate_runs_num"),true)
                                )
                            )
                        )
                    )
                    .then(literal("stop_sim")
                        .executes(context -> Main.simulator.stopSimCommand(
                            context.getSource())
                        )
                    )
                    .then(literal("disable")
                        .requires(source -> ((source.getEntity() instanceof ServerPlayerEntity &&"Mat0u5".equals(source.getName()) || (source.getEntity() == null))))
                        .executes(context -> Main.simulator.enOrDis(
                            context.getSource(),"false")
                        )
                    )
                    .then(literal("enable")
                        .requires(source -> ((source.getEntity() instanceof ServerPlayerEntity &&"Mat0u5".equals(source.getName()) || (source.getEntity() == null))))
                        .executes(context -> Main.simulator.enOrDis(
                            context.getSource(),"true")
                        )
                    )
                )
                .then(literal("commandBlockSearch")
                    .requires(source -> source.hasPermissionLevel(2))
                    .then(literal("containsString")
                        .then(CommandManager.argument("string", StringArgumentType.string())
                            .executes(context -> DatabaseCommand.executeCommandBlockSearch(
                                context.getSource(),
                                StringArgumentType.getString(context, "string"),
                                "contains")
                            )
                        )
                    )
                    .then(literal("startsWithString")
                        .then(CommandManager.argument("string", StringArgumentType.string())
                            .executes(context -> DatabaseCommand.executeCommandBlockSearch(
                                context.getSource(),
                                StringArgumentType.getString(context, "string"),
                                "startsWith")
                            )
                        )
                    )
                    .then(literal("endsWithString")
                        .then(CommandManager.argument("string", StringArgumentType.string())
                            .executes(context -> DatabaseCommand.executeCommandBlockSearch(
                                context.getSource(),
                                StringArgumentType.getString(context, "string"),
                                "endsWith")
                            )
                        )
                    )
                )
                .then(literal("mapGuiScale")
                    .then(CommandManager.argument("guiScale", IntegerArgumentType.integer())
                        .executes(context -> GuiMapCommand.executeGuiScale(
                            context.getSource(),
                            IntegerArgumentType.getInteger(context, "guiScale"))
                        )
                    )
                )
                .then(literal("speedrunStart")
                    .executes(context -> OtherCommand.executeSpeedrun(
                        context.getSource())
                    )
                    .then(literal("advanced")
                        .requires(source -> source.getEntity() instanceof ServerPlayerEntity &&"Mat0u5".equals(source.getName()))
                        .executes(context -> OtherCommand.executeSpeedrunAdvanced(
                            context.getSource())
                        )
                    )
                )
                .then(literal("currentRun")
                    .then(literal("getInfo")
                        .executes(context -> OtherCommand.getInfo(
                            context.getSource())
                        )
                    )
                    .then(literal("viewDeck")
                        .executes(context -> OtherCommand.viewDeck(
                            context.getSource())
                        )
                    )
                    .then(literal("viewRunnerInv")
                        .executes(context -> OtherCommand.viewInv(
                            context.getSource())
                        )
                    )
                )
                .then(literal("invScanner")
                    .requires(source -> ((source.getEntity() instanceof ServerPlayerEntity &&"Mat0u5".equals(source.getName()) || (source.getEntity() == null))))
                        .then(CommandManager.argument("targets", EntityArgumentType.players())
                            .then(literal("tagExpanded")
                                .executes(context -> OtherCommand.invScanner(
                                    context.getSource(),
                                    EntityArgumentType.getPlayers(context, "targets"),"tagExpanded")
                                )
                            )
                            .then(literal("removePhase")
                                .executes(context -> OtherCommand.invScanner(
                                    context.getSource(),
                                    EntityArgumentType.getPlayers(context, "targets"),"removePhase")
                                )
                            )
                        )
                )
                .then(literal("queueRestart")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> RestartCommand.queueRestart(
                        context.getSource(),true)
                    )
                    .then(literal("stop")
                        .executes(context -> RestartCommand.queueRestart(
                            context.getSource(),false)
                        )
                    )
                )
                .then(literal("blockLock")
                    .requires(source -> source.getEntity() instanceof ServerPlayerEntity &&"Mat0u5".equals(source.getName()))
                    .then(literal("startSearch")
                        .then(CommandManager.argument("fromX", IntegerArgumentType.integer())
                        .then(CommandManager.argument("fromY", IntegerArgumentType.integer())
                        .then(CommandManager.argument("fromZ", IntegerArgumentType.integer())
                        .then(CommandManager.argument("toX", IntegerArgumentType.integer())
                        .then(CommandManager.argument("toY", IntegerArgumentType.integer())
                        .then(CommandManager.argument("toZ", IntegerArgumentType.integer())
                            .executes(context -> OtherCommand.executeLock(
                                context.getSource(),
                                IntegerArgumentType.getInteger(context, "fromX"),
                                IntegerArgumentType.getInteger(context, "fromY"),
                                IntegerArgumentType.getInteger(context, "fromZ"),
                                IntegerArgumentType.getInteger(context, "toX"),
                                IntegerArgumentType.getInteger(context, "toY"),
                                IntegerArgumentType.getInteger(context, "toZ"),
                                "lock_block")
                            )
                            .then(literal("unlock")
                                .executes(context -> OtherCommand.executeLock(
                                    context.getSource(),
                                    IntegerArgumentType.getInteger(context, "fromX"),
                                    IntegerArgumentType.getInteger(context, "fromY"),
                                    IntegerArgumentType.getInteger(context, "fromZ"),
                                    IntegerArgumentType.getInteger(context, "toX"),
                                    IntegerArgumentType.getInteger(context, "toY"),
                                    IntegerArgumentType.getInteger(context, "toZ"),
                                    "unlock_block")
                                )
                            )
                        )
                        )
                        )
                        )
                        )
                        )
                    )
                )
                .then(literal("reload")
                    .executes(context -> OtherCommand.reload()
                    )
                )
        );


        dispatcher.register(
            literal("playerlist")
                .executes(context -> OtherCommand.playerList(
                    context.getSource())
                )
        );
        dispatcher.register(
            literal("stuck")
                .executes(context -> OtherCommand.stuck(
                    context.getSource())
                )
        );
        dispatcher.register(literal("queue")
                .then(literal("join")
                    .executes(context -> QueueCommand.joinQueue(
                            context.getSource()
                        )
                    )
                )
                .then(literal("leave")
                    .executes(context -> QueueCommand.leaveQueue(
                            context.getSource()
                        )
                    )
                )
                .then(literal("list")
                    .executes(context -> QueueCommand.listQueue(
                            context.getSource()
                        )
                    )
                )
                .then(literal("skipTurn")
                    .executes(context -> QueueCommand.skipTurn(
                            context.getSource(),1
                        )
                    )
                    .then(CommandManager.argument("skipTurns", IntegerArgumentType.integer(1))
                        .executes(context -> QueueCommand.skipTurn(
                                context.getSource(),IntegerArgumentType.getInteger(context,"skipTurns")
                            )
                        )
                    )
                )
                .then(literal("finishRun")
                    .requires(source -> ((source.getEntity() instanceof ServerPlayerEntity &&"Mat0u5".equals(source.getName()) || (source.getEntity() == null))))
                    .then(CommandManager.argument("targets", EntityArgumentType.players())
                        .executes(context -> QueueCommand.runFinish(
                                context.getSource(),
                                EntityArgumentType.getPlayers(context, "targets"))
                        )
                    )
                )
                .then(literal("add").requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("player", player())
                        .executes(context -> QueueCommand.addPlayerToQueue(
                            context.getSource(),getPlayer(context,"player")
                        ))
                    ))
                .then(literal("remove").requires(source -> source.hasPermissionLevel(2))
                    .then(CommandManager.argument("player", player())
                        .suggests(QueueCommand.getQueuePlayersSuggestionProvider())
                        .executes(context -> QueueCommand.removePlayerFromQueue(
                            context.getSource(),getPlayer(context,"player")
                        ))
                    ))
                .then(literal("move").requires(source -> source.hasPermissionLevel(2))
                    .executes(QueueCommand::moveQueue))
        );

    }
}
