package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class GuiMapCommand {
    public static int executeGuiScale(ServerCommandSource source, int guiScale) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        if (guiScale < 0 || guiScale > 4) {
            self.sendMessage(Text.of("§cInvalid gui scale!"));
            return -1;
        }
        if (guiScale == 0) OtherUtils.executeCommand(server,"function dom:mat0u5/gui/scale/disable_map");
        else OtherUtils.executeCommand(server,"function dom:mat0u5/gui/scale/scale_"+guiScale);

        return 1;
    }
}
