package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.config.ConfigManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.mat0u5.do2manager.world.BlockScanner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class OtherCommand {
    public static int executeSpeedrun(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        Main.config.setProperty("current_run_is_speedrun","true");
        OtherUtils.broadcastMessage(server, Text.of("§6This run has been marked as a speedrun."));
        return 1;
    }
    public static int executeLock(ServerCommandSource source, int fromX, int fromY, int fromZ, int toX, int toY, int toZ, String type) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        self.sendMessage(Text.of("Started Block Lock Search..."));
        BlockScanner.scanArea(type,server.getOverworld(),new BlockPos(fromX, fromY, fromZ),new BlockPos(toX, toY, toZ), source.getPlayer());
        return 1;
    }
    public static int reload() {
        Main.config= new ConfigManager("./config/"+Main.MOD_ID+"/"+Main.MOD_ID+".properties");
        Main.lastPhaseUpdate = new ConfigManager("./config/"+Main.MOD_ID+"/"+Main.MOD_ID+"_phase_inv_update.properties");
        return 1;
    }
    public static int playerList(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        int playerCount = 0;
        MutableText message = Text.translatable("There are "+server.getPlayerManager().getPlayerList().size()+" players online: ");
        for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
            playerCount++;
            message = message.append(player.getDisplayName());
            if (playerCount != server.getPlayerManager().getPlayerList().size()) {
                message = message.append(", ");
            }
        }
        if (self != null) {
            self.sendMessage(message);
        }
        else {
            System.out.println(message.getString());
        }
        return 1;
    }
}
