package net.mat0u5.do2manager.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import java.util.List;

import net.minecraft.util.Hand;

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
                        )
                    )
                )

                ////////
                .then(literal("getItem")
                    .executes(context -> TestingCommand.executeGetItem(
                        context.getSource())
                    )
                )
                .then(literal("setItem")
                    .executes(context -> TestingCommand.executeSetItem(
                        context.getSource())
                    )
                )
                .then(literal("addRun")
                    .executes(context -> TestingCommand.executeAddRun(
                        context.getSource())
                    )
                )
                .then(literal("getInv")
                    .then(CommandManager.argument("runNum", IntegerArgumentType.integer())
                        .executes(context -> TestingCommand.executeGetInv(
                            context.getSource(),
                            IntegerArgumentType.getInteger(context, "runNum"))
                        )
                    )
                )
        );
    }
}
