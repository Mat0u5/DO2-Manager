package net.mat0u5.do2manager.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;


public class Command {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
            literal("decked-out")
                .requires(source -> source.hasPermissionLevel(2))
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
                .then(literal("database-testing")
                    .requires(source -> source.hasPermissionLevel(2))
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
        );
    }
}
