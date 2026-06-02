package net.mat0u5.do2manager.mixin;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.context.CommandContext;
import net.mat0u5.do2manager.command.validator.CommandAnalyzer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Commands.class)
public class CommandManagerMixin {
    @Inject(method = "performCommand", at = @At("HEAD"), cancellable = true)
    private void onCommandExecute(ParseResults<CommandSourceStack> parseResults, String command, CallbackInfo ci) {
        CommandSourceStack source = parseResults.getContext().getSource();
        if (source.getEntity() instanceof ServerPlayer player) {
            CommandContext<CommandSourceStack> context = parseResults.getContext().build(command);
            if (CommandAnalyzer.shouldConfirm(command, context)) {
                CommandAnalyzer.sendConfirmationMessage(player, command, context);
                player.playNotifySound(SoundEvents.NOTE_BLOCK_DIDGERIDOO.value(), SoundSource.BLOCKS, 1, 1);
                ci.cancel();
            }
        }
    }
}
