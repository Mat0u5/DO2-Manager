package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

public class GuiMapCommand {
    public static int executeGuiScale(CommandSourceStack source, int guiScale) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();

        if (guiScale < 0 || guiScale > 4) {
            self.sendSystemMessage(Component.nullToEmpty("§cInvalid gui scale!"));
            return -1;
        }
        String command = "execute as "+self.getStringUUID()+" run function dom:world/dungeon_functions/utilities/do2.map/scale/"+((guiScale==0)?"disable_map":"scale_"+guiScale);

        OtherUtils.executeCommand(server,command);

        return 1;
    }
}
