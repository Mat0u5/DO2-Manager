package net.mat0u5.do2manager.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.mat0u5.do2manager.gui.GuiInventory_Database;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.literal;


public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("decked-out")
                .then(literal("console-only")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> ConsoleCommand.execute(
                        context.getSource())
                    )
                    .then(literal("database")
                        .then(literal("runTracking")
                            .then(literal("getInfo")
                                .executes(context -> ConsoleCommand.database_runTracking_GetInfo(
                                    context.getSource())
                                )
                            )
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
                )
                .then(literal("testing")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> TestingCommand.execute(
                        context.getSource())
                    )
                    .then(literal("test")
                        .executes(context -> TestingCommand.executeTest(
                            context.getSource())
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
                )
        );
    }
}
