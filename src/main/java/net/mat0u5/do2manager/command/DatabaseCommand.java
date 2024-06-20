package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class DatabaseCommand {
    public static int execute(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        DatabaseManager.printAllPlayers();
        try {
            DatabaseManager.updateTable();
        }catch (Exception e){}
        self.sendMessage(Text.translatable("§6Test Database Command..."));
        return 1;
    }
}
