package net.mat0u5.do2manager.command.validator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import static net.mat0u5.do2manager.Main.server;
import static net.mat0u5.do2manager.utils.PermissionManager.isAdmin;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class ConfirmationCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher,
                                CommandBuildContext commandRegistryAccess,
                                Commands.CommandSelection registrationEnvironment) {
        dispatcher.register(
                literal("confirmcmd")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("action", StringArgumentType.string())
                        .executes(ConfirmationCommand::execute)
                )
        );
    }

    private static int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayerOrException();
        String action = StringArgumentType.getString(context, "action");

        CommandValidator.PendingCommand pending = CommandValidator.getPendingCommand(player.getUUID());

        if (pending == null) {
            player.displayClientMessage(Component.literal("No pending command to confirm.").withStyle(ChatFormatting.RED), false);
            return 0;
        }

        if ("cancel".equals(action)) {
            CommandValidator.removePendingCommand(player.getUUID());
            player.displayClientMessage(Component.literal("Command cancelled.").withStyle(ChatFormatting.GREEN), false);
            return 1;
        }

        if (pending.confirmId.equals(action)) {
            CommandValidator.removePendingCommand(player.getUUID());

            try {
                CommandDispatcher<CommandSourceStack> dispatcher = server.getCommands().getDispatcher();
                dispatcher.execute(pending.command, source);
                player.displayClientMessage(Component.literal("Command executed.").withStyle(ChatFormatting.GREEN), false);
                return 1;
            } catch (Exception e) {
                player.displayClientMessage(Component.literal("Failed to execute command: " + e.getMessage()).withStyle(ChatFormatting.RED), false);
                return 0;
            }
        } else {
            player.displayClientMessage(Component.literal("Invalid confirmation code.").withStyle(ChatFormatting.RED), false);
            return 0;
        }
    }
}