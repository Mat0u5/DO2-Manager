package net.mat0u5.do2manager.command.validator;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.mat0u5.do2manager.Main.server;
import static net.mat0u5.do2manager.utils.PermissionManager.isAdmin;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ConfirmationCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(
                literal("confirmcmd")
                .requires(source -> (isAdmin(source.getPlayer()) || (source.getEntity() == null)))
                .then(argument("action", StringArgumentType.string())
                        .executes(ConfirmationCommand::execute)
                )
        );
    }

    private static int execute(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayerOrThrow();
        String action = StringArgumentType.getString(context, "action");

        CommandValidator.PendingCommand pending = CommandValidator.getPendingCommand(player.getUuid());

        if (pending == null) {
            player.sendMessage(Text.literal("No pending command to confirm.").formatted(Formatting.RED), false);
            return 0;
        }

        if ("cancel".equals(action)) {
            CommandValidator.removePendingCommand(player.getUuid());
            player.sendMessage(Text.literal("Command cancelled.").formatted(Formatting.GREEN), false);
            return 1;
        }

        if (pending.confirmId.equals(action)) {
            CommandValidator.removePendingCommand(player.getUuid());

            try {
                CommandDispatcher<ServerCommandSource> dispatcher = server.getCommandManager().getDispatcher();
                dispatcher.execute(pending.command, source);
                player.sendMessage(Text.literal("Command executed.").formatted(Formatting.GREEN), false);
                return 1;
            } catch (Exception e) {
                player.sendMessage(Text.literal("Failed to execute command: " + e.getMessage()).formatted(Formatting.RED), false);
                return 0;
            }
        } else {
            player.sendMessage(Text.literal("Invalid confirmation code.").formatted(Formatting.RED), false);
            return 0;
        }
    }
}