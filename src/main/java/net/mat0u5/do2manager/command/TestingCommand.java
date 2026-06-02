package net.mat0u5.do2manager.command;

import com.google.gson.Gson;
import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.*;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.DO2RunAbridged;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.io.File;
import java.nio.file.Path;
import java.util.List;


public class TestingCommand {
    private static final Gson GSON = new Gson();
    public static int execute(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();

        ItemStack item = self.getItemInHand(InteractionHand.MAIN_HAND);
        ItemManager.setCustomComponentByte(item,"CustomRoleplayData",(byte) 1);

        return 1;
    }
    public static int executeCmd(String args) {
        OtherUtils.executeCommand(Main.server,args);
        return 1;
    }
    public static int executeAddRun(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();


        self.sendSystemMessage(Component.translatable("§6Command Worked.."));
        return 1;
    }
    public static int executeGetInv(CommandSourceStack source, int runNum) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();


        if (!Main.reloadedRuns) {
            Main.reloadAllAbridgedRunsAsync().thenRun(() -> {
                testRun(runNum);
            });
        }
        else {
            testRun(runNum);
        }


        self.sendSystemMessage(Component.translatable("§6Command Worked.."));
        return 1;
    }
    public static void testRun(int num) {
        for (DO2RunAbridged abridgedRun : Main.allAbridgedRuns) {
            if (abridgedRun.run_number == num) {
                List<DO2Run> actualRun = DatabaseManager.getRunsByAbridgedRuns(List.of(abridgedRun),true);
                for (DO2Run run : actualRun) {
                    DatabaseManager.updateRun(run);
                }
            }
        }
    }
    public static int executeTest(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();

        return 1;
    }
    public static int executeCopyScoreboard(CommandSourceStack source, String newObj, String oldObj, String pathName) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();


        ScoreboardUtils.copyObjectiveFromFile(server, newObj, oldObj, new File(pathName));

        return 1;
    }
    public static int updatePlayerData(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        PlayerDataUpdater updater = new PlayerDataUpdater(server);
        updater.updateAllPlayerData();
        return 1;
    }
    public static int validatePlayerData(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        PlayerDataUpdater updater = new PlayerDataUpdater(server);
        updater.validateAllPlayerData();
        return 1;
    }
}
