package net.mat0u5.do2manager.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.gui.GuiInventory_Database;
import net.mat0u5.do2manager.gui.GuiInventory_ChestFramework;
import net.mat0u5.do2manager.queue.QueueCommand;
import net.mat0u5.do2manager.tcg.TCG_Commands;
import net.mat0u5.do2manager.utils.PermissionManager;
import net.mat0u5.do2manager.utils.ScoreboardUtils;
import net.mat0u5.do2manager.world.FunctionPreview;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.CommandFunctionArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.FunctionCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.function.CommandFunctionManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static net.mat0u5.do2manager.Main.dungeonQueue;
import static net.mat0u5.do2manager.utils.PermissionManager.*;
import static net.minecraft.command.argument.EntityArgumentType.getPlayer;
import static net.minecraft.command.argument.EntityArgumentType.player;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;


public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("decked-out")
                .then(literal("console-only")
                    .requires(source -> ((isModOwner(source.getPlayer()) || (source.getEntity() == null))))
                    .executes(context -> ConsoleCommand.execute(
                        context.getSource())
                    )
                    .then(literal("database")
                        .then(literal("runTracking")
                            .then(literal("saveRun")
                                .executes(context -> OtherCommand.saveRunInfo(
                                    context.getSource())
                                )
                            )
                            .then(literal("prepareForRun")
                                .executes(context -> ConsoleCommand.database_runTracking_PrepareForRun(
                                    context.getSource())
                                )
                            )
                            .then(literal("var_modify")
                                .then(argument("query", StringArgumentType.string())
                                    .executes(context -> ConsoleCommand.database_runTracking_modifyVar(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "query"))
                                    )
                                )
                            )
                            .then(literal("var_modify_premade")
                                .then(literal("items")
                                    .then(argument("functionName", StringArgumentType.string())
                                        .then(argument("targets", EntityArgumentType.entities())
                                            .executes(context -> ConsoleCommand.database_runTracking_Items(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "functionName"),
                                                EntityArgumentType.getEntities(context, "targets"))
                                            )
                                        )
                                    )
                                )
                                .then(literal("timestamp")
                                    .then(argument("timestampName", StringArgumentType.string())
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
                                .then(literal("crowns_counted")
                                    .executes(context -> ConsoleCommand.database_runTracking_Crowns(
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
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .then(literal("scanner")
                        .requires(source -> ((isModOwner(source.getPlayer()) || (source.getEntity() == null))))
                        .then(literal("crowns")
                            .executes(context -> DatabaseCommand.updateCrowns())
                        )
                        .then(literal("totalCrowns")
                                .executes(context -> DatabaseCommand.updateTotalCrowns())
                        )
                        .then(literal("totalEmbers")
                                .executes(context -> DatabaseCommand.updateTotalEmbers())
                        )
                        .then(literal("lackeys")
                                .executes(context -> DatabaseCommand.updateLackeys())
                        )
                        .then(literal("boughtItems")
                                .executes(context -> DatabaseCommand.updateBoughtItems())
                        )
                    )
                    .then(literal("getRaw")
                        .then(argument("runId", IntegerArgumentType.integer())
                            .then(argument("var_name", StringArgumentType.string())
                                .executes(context -> DatabaseCommand.executeGetFromDB(
                                    context.getSource(),
                                    IntegerArgumentType.getInteger(context, "runId"),
                                    StringArgumentType.getString(context, "var_name"))
                                )
                            )
                        )
                    )

                    .then(literal("commandBlockStartScan")
                        .executes(context -> DatabaseCommand.executeCommandBlockUpdateDatabase(
                                context.getSource(), -672, 165, 1727,-337, -64, 2291)
                        )
                        .then(argument("fromPos", BlockPosArgumentType.blockPos()) // Suggests the block you're looking at
                            .then(argument("toPos", BlockPosArgumentType.blockPos()) // Suggests the block you're looking at
                                .executes(context -> {
                                    BlockPos fromPos = BlockPosArgumentType.getBlockPos(context, "fromPos");
                                    BlockPos toPos = BlockPosArgumentType.getBlockPos(context, "toPos");
                                    return DatabaseCommand.executeCommandBlockUpdateDatabase(
                                        context.getSource(),
                                        fromPos.getX(),
                                        fromPos.getY(),
                                        fromPos.getZ(),
                                        toPos.getX(),
                                        toPos.getY(),
                                        toPos.getZ()
                                    );
                                })
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
                    .requires(source -> (isAdmin(source.getPlayer())))
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
                        .then(argument("inv_size", IntegerArgumentType.integer())
                            .then(argument("inv_name", StringArgumentType.string())
                                .then(argument("chest_pos", StringArgumentType.string())
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
                    .requires(source -> ((isModOwner(source.getPlayer()) || (source.getEntity() == null))))
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
                        .then(argument("args", StringArgumentType.string())
                            .executes(context -> TestingCommand.executeCmd(
                                StringArgumentType.getString(context, "args"))
                            )
                        )
                    )
                    .then(literal("updateGameProfiles")
                        .executes(context -> TestingCommand.updateGameProfiles(
                            context.getSource())
                        )
                    )
                )
                .then(literal("simulator")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .then(literal("card_played")
                        .requires(source -> ((isModOwner(source.getPlayer()) || (source.getEntity() == null))))
                        .executes(context -> Main.simulator.cardPlayed(
                            context.getSource())
                        )
                    )
                    .then(literal("save_permanents")
                        .requires(source -> ((isModOwner(source.getPlayer()) || (source.getEntity() == null))))
                        .executes(context -> Main.simulator.saveHand(
                            context.getSource())
                        )
                    )
                    .then(literal("hand_deck")
                        .executes(context -> Main.simulator.runHand(
                            context.getSource(),5000,false)
                        )
                        .then(argument("simulate_runs_num", IntegerArgumentType.integer(1))
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
                        .requires(source -> ((isModOwner(source.getPlayer()) || (source.getEntity() == null))))
                        .executes(context -> Main.simulator.enOrDis(
                            context.getSource(),"false")
                        )
                    )
                    .then(literal("enable")
                        .requires(source -> ((isModOwner(source.getPlayer()) || (source.getEntity() == null))))
                        .executes(context -> Main.simulator.enOrDis(
                            context.getSource(),"true")
                        )
                    )
                )
                .then(literal("commandBlockSearch")
                    .requires(source -> (isAdmin(source.getPlayer())))
                    .then(literal("containsString")
                        .then(argument("string", StringArgumentType.string())
                            .executes(context -> DatabaseCommand.executeCommandBlockSearch(
                                context.getSource(),
                                StringArgumentType.getString(context, "string"),
                                "contains")
                            )
                        )
                    )
                    .then(literal("startsWithString")
                        .then(argument("string", StringArgumentType.string())
                            .executes(context -> DatabaseCommand.executeCommandBlockSearch(
                                context.getSource(),
                                StringArgumentType.getString(context, "string"),
                                "startsWith")
                            )
                        )
                    )
                    .then(literal("endsWithString")
                        .then(argument("string", StringArgumentType.string())
                            .executes(context -> DatabaseCommand.executeCommandBlockSearch(
                                context.getSource(),
                                StringArgumentType.getString(context, "string"),
                                "endsWith")
                            )
                        )
                    )
                )
                .then(literal("mapGuiScale")
                    .then(argument("guiScale", IntegerArgumentType.integer())
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
                        .requires(source -> (isModOwner(source.getPlayer())))
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
                        .requires(source -> ((isModOwner(source.getPlayer()) || (source.getEntity() == null))))
                        .then(argument("targets", EntityArgumentType.players())
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
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .executes(context -> RestartCommand.queueRestart(
                        context.getSource(),true)
                    )
                    .then(literal("stop")
                        .executes(context -> RestartCommand.queueRestart(
                            context.getSource(),false)
                        )
                    )
                )
                .then(literal("reload")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .executes(context -> OtherCommand.reload()
                    )
                    .then(literal("database")
                        .executes(context -> OtherCommand.reloadDatabase(context.getSource())
                        )
                    )
                )
        );
        dispatcher.register(
            literal("blocklock")
                .requires(source -> (isAdmin(source.getPlayer())))
                .then(argument("lock_or_unlock", StringArgumentType.string())
                    .suggests((context, builder) -> CommandSource.suggestMatching(List.of("lock", "unlock"), builder))
                    .then(argument("fromPos", BlockPosArgumentType.blockPos()) // Suggests the block you're looking at
                        .then(argument("toPos", BlockPosArgumentType.blockPos()) // Suggests the block you're looking at
                            .executes(context -> {
                                BlockPos fromPos = BlockPosArgumentType.getBlockPos(context, "fromPos");
                                BlockPos toPos = BlockPosArgumentType.getBlockPos(context, "toPos");
                                return OtherCommand.executeLock(
                                    context.getSource(),
                                    fromPos.getX(),
                                    fromPos.getY(),
                                    fromPos.getZ(),
                                    toPos.getX(),
                                    toPos.getY(),
                                    toPos.getZ(),
                                    StringArgumentType.getString(context, "lock_or_unlock")
                                );
                            })
                        )
                        .executes(context -> {
                            BlockPos fromPos = BlockPosArgumentType.getBlockPos(context, "fromPos");
                            return OtherCommand.executeLock(
                                context.getSource(),
                                fromPos.getX(),
                                fromPos.getY(),
                                fromPos.getZ(),
                                fromPos.getX(),
                                fromPos.getY(),
                                fromPos.getZ(),
                                StringArgumentType.getString(context, "lock_or_unlock")
                            );
                        })
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
        dispatcher.register(
            literal("runs")
                .requires(source -> (isAdmin(source.getPlayer())))
                .executes(context -> new GuiInventory_Database().openRunInventory(
                        context.getSource().getPlayer())
                )
        );
        dispatcher.register(
            literal("items")
                .requires(source -> (isAdmin(source.getPlayer())))
                .executes(context -> new GuiInventory_ChestFramework().openChestInventory(
                        context.getSource().getPlayer(),
                        54,
                        "Decked Out 2 Items",
                        "_-629,11,1966;0;1",false)
                )
        );
        dispatcher.register(
            literal("run")
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
                    .then(argument("skipTurns", IntegerArgumentType.integer(1))
                        .executes(context -> QueueCommand.skipTurn(
                                context.getSource(),IntegerArgumentType.getInteger(context,"skipTurns")
                            )
                        )
                    )
                    .then(argument("player", player())
                        .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                        .executes(context -> QueueCommand.skipTurnOther(
                            context.getSource(),getPlayer(context,"player"),1
                        ))
                        .then(argument("skipTurns", IntegerArgumentType.integer(1))
                            .executes(context -> QueueCommand.skipTurnOther(
                                            context.getSource(),getPlayer(context,"player"),IntegerArgumentType.getInteger(context,"skipTurns")
                                    )
                            )
                        )
                    )
                )
                .then(literal("finishRun")
                    .requires(source -> ((isModOwner(source.getPlayer()) || (source.getEntity() == null))))
                    .then(argument("targets", EntityArgumentType.players())
                        .executes(context -> QueueCommand.runFinish(
                                context.getSource(),
                                EntityArgumentType.getPlayers(context, "targets"))
                        )
                    )
                )
                .then(literal("add")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .then(argument("player", player())
                        .executes(context -> QueueCommand.addPlayerToQueue(
                            context.getSource(),getPlayer(context,"player")
                        ))
                    ))
                .then(literal("remove")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .then(argument("target", StringArgumentType.string())
                        .suggests(QueueCommand.getQueuePlayersSuggestionProvider())
                        .executes(context -> QueueCommand.removePlayerFromQueue(
                            context.getSource(),StringArgumentType.getString(context,"target")
                        ))
                    ))
                .then(literal("move")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .executes(QueueCommand::moveQueue))
        );

        dispatcher.register(
            literal("tcg")
                .requires(source -> ((isTCGGameMaster(source.getPlayer()) || (source.getEntity() == null))))
                .then(literal("giveBundle")
                    .then(argument("target", EntityArgumentType.player())
                        .then(literal("hermit")
                            .executes(context -> TCG_Commands.generateDeck(
                                    context.getSource(), "hermit",1,
                                    EntityArgumentType.getPlayer(context, "target")
                                )
                            )
                            .then(argument("amount", IntegerArgumentType.integer(1,27))
                                .executes(context -> TCG_Commands.generateDeck(
                                        context.getSource(), "hermit",IntegerArgumentType.getInteger(context,"amount"),
                                        EntityArgumentType.getPlayer(context, "target")
                                    )
                                )
                            )
                        )
                        .then(literal("booster")
                            .executes(context -> TCG_Commands.generateDeck(
                                    context.getSource(), "booster",1,
                                    EntityArgumentType.getPlayer(context, "target")
                                )
                            )
                            .then(argument("amount", IntegerArgumentType.integer(1,27))
                                .executes(context -> TCG_Commands.generateDeck(
                                        context.getSource(), "booster",IntegerArgumentType.getInteger(context,"amount"),
                                        EntityArgumentType.getPlayer(context, "target")
                                    )
                                )
                            )
                        )
                        .then(literal("starter")
                            .executes(context -> TCG_Commands.generateDeck(
                                    context.getSource(), "starter",1,
                                    EntityArgumentType.getPlayer(context, "target")
                                )
                            )
                            .then(argument("amount", IntegerArgumentType.integer(1,27))
                                .executes(context -> TCG_Commands.generateDeck(
                                        context.getSource(), "starter",IntegerArgumentType.getInteger(context,"amount"),
                                        EntityArgumentType.getPlayer(context, "target")
                                    )
                                )
                            )
                        )
                        .then(literal("alterEgo")
                            .executes(context -> TCG_Commands.generateDeck(
                                    context.getSource(), "alterEgo",1,
                                    EntityArgumentType.getPlayer(context, "target")
                                )
                            )
                            .then(argument("amount", IntegerArgumentType.integer(1,27))
                                .executes(context -> TCG_Commands.generateDeck(
                                        context.getSource(), "alterEgo",IntegerArgumentType.getInteger(context,"amount"),
                                        EntityArgumentType.getPlayer(context, "target")
                                    )
                                )
                            )
                        )
                        .then(literal("effect")
                            .executes(context -> TCG_Commands.generateDeck(
                                    context.getSource(), "effect",1,
                                    EntityArgumentType.getPlayer(context, "target")
                                )
                            )
                            .then(argument("amount", IntegerArgumentType.integer(1,27))
                                .executes(context -> TCG_Commands.generateDeck(
                                        context.getSource(), "effect",IntegerArgumentType.getInteger(context,"amount"),
                                        EntityArgumentType.getPlayer(context, "target")
                                    )
                                )
                            )
                        )
                        .then(literal("item")
                            .executes(context -> TCG_Commands.generateDeck(
                                    context.getSource(), "item",1,
                                    EntityArgumentType.getPlayer(context, "target")
                                )
                            )
                            .then(argument("amount", IntegerArgumentType.integer(1,27))
                                .executes(context -> TCG_Commands.generateDeck(
                                        context.getSource(), "item",IntegerArgumentType.getInteger(context,"amount"),
                                        EntityArgumentType.getPlayer(context, "target")
                                    )
                                )
                            )
                        )
                    )
                )

                .then(literal("_database_update_items")
                    .requires(source -> (isModOwner(source.getPlayer())))
                    .executes(context -> TCG_Commands.databaseUpdate(
                            context.getSource()
                        )
                    )
                )
                .then(literal("reload")
                        .requires(source -> ((isModOwner(source.getPlayer()) || (source.getEntity() == null))))
                        .executes(context -> TCG_Commands.reload(
                            context.getSource()
                        )
                    )
                )
        );

        dispatcher.register(
            literal("makephase")
                .requires(source -> (isAdmin(source.getPlayer())))
                .executes(context -> OtherCommand.makePhase(
                        context.getSource())
                )
        );
        dispatcher.register(
            literal("cmd")
                .requires(source -> (isAdmin(source.getPlayer())))
                .then(literal("get")
                    .executes(context -> OtherCommand.customModelData(
                        context.getSource(),false, -1)
                    )
                )
                .then(literal("set")
                    .then(argument("cmd", IntegerArgumentType.integer())
                        .executes(context -> OtherCommand.customModelData(
                            context.getSource(),true, IntegerArgumentType.getInteger(context,"cmd"))
                        )
                    )
                )
        );
        dispatcher.register(
            literal("patch")
                .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                .then(argument("change", StringArgumentType.string())
                    .executes(context -> OtherCommand.pushChange(
                        context.getSource(),StringArgumentType.getString(context,"change"),"",""
                    ))
                    .then(argument("reason", StringArgumentType.string())
                        .executes(context -> OtherCommand.pushChange(
                            context.getSource(),StringArgumentType.getString(context,"change"),
                            StringArgumentType.getString(context,"reason"),""
                        ))
                        .then(argument("affected", StringArgumentType.string())
                            .executes(context -> OtherCommand.pushChange(
                                context.getSource(),StringArgumentType.getString(context,"change"),
                                StringArgumentType.getString(context,"reason"), StringArgumentType.getString(context,"affected")
                            ))
                        )
                    )
                )
        );
        dispatcher.register(literal("scoreboard")
            .then(literal("objectives")
                .then(literal("rename")
                    .requires(source -> ((isAdmin(source.getPlayer()) || (source.getEntity() == null))))
                    .then(argument("old_objective", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            // Suggest existing objectives for the old_objective argument
                            Scoreboard scoreboard = context.getSource().getServer().getScoreboard();
                            return CommandSource.suggestMatching(scoreboard.getObjectiveNames(), builder);
                        })
                        .then(argument("new_objective", StringArgumentType.word())
                            .executes(context -> {
                                // Execute the renaming logic
                                String oldObjectiveName = StringArgumentType.getString(context, "old_objective");
                                String newObjectiveName = StringArgumentType.getString(context, "new_objective");
                                return ScoreboardUtils.renameScoreboardObjective(context.getSource(), oldObjectiveName, newObjectiveName);
                            })
                        )
                    )
                )
            )
        );

        dispatcher.register(
            literal("previewFunction")
                .requires(source -> (isAdmin(source.getPlayer())))
                .then(argument("name", CommandFunctionArgumentType.commandFunction())
                    .suggests(FUNCTION_COMMAND_SUGGESTION)
                    .executes(context -> FunctionPreview.previewFunction(
                        context.getSource(),CommandFunctionArgumentType.getFunctions(context, "name")
                    ))
                )
                .then(literal("stop")
                    .executes(context -> FunctionPreview.stopPreviewFunction(
                            context.getSource()
                        )
                    )
                )
        );


    }
    public static final SuggestionProvider<ServerCommandSource> FUNCTION_COMMAND_SUGGESTION = (context, builder) -> {
        CommandFunctionManager commandFunctionManager = ((ServerCommandSource)context.getSource()).getServer().getCommandFunctionManager();
        CommandSource.suggestIdentifiers(commandFunctionManager.getFunctionTags(), builder, "#");
        return CommandSource.suggestIdentifiers(commandFunctionManager.getAllFunctions(), builder);
    };
}
