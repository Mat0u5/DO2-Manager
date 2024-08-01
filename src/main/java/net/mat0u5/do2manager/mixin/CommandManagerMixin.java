package net.mat0u5.do2manager.mixin;

import com.mojang.brigadier.ParseResults;
import net.mat0u5.do2manager.command.GuiMapCommand;
import net.mat0u5.do2manager.command.OtherCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//@Mixin(CommandManager.class)
@Mixin(value = CommandManager.class, priority = 1001)
public abstract class CommandManagerMixin {

    @Inject(method = "execute", at = @At(value = "HEAD"), cancellable = true)
    public void execute(ParseResults<ServerCommandSource> parseResults, String command, CallbackInfoReturnable<Integer> cir) {
        ServerCommandSource cmdSource = parseResults.getContext().getSource();
        if (!cmdSource.isExecutedByPlayer()) return;
        ServerPlayerEntity player = cmdSource.getPlayer();
        if (player == null) return;
        if (player.hasPermissionLevel(2)) return;
        if (!command.equalsIgnoreCase("decked-out") && !command.matches("decked-out .*")) return;
        try {
            if (command.matches("decked-out mapGuiScale [0-9]+")) {
                GuiMapCommand.executeGuiScale(cmdSource, Integer.parseInt(command.split("decked-out mapGuiScale ")[1]));
                cir.setReturnValue(0);
            }
            else if (command.equalsIgnoreCase("stuck")) {
                OtherCommand.stuck(cmdSource);
                cir.setReturnValue(0);
            }
            else if (command.equalsIgnoreCase("decked-out currentRun viewDeck")) {
                OtherCommand.viewDeck(cmdSource);
                cir.setReturnValue(0);
            }
            else if (command.equalsIgnoreCase("decked-out currentRun getInfo")) {
                OtherCommand.getInfo(cmdSource);
                cir.setReturnValue(0);
            }
        }catch (Exception e) {}
    }
}