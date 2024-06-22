package net.mat0u5.do2manager.command;

import net.mat0u5.do2manager.Main;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.world.DO2Run;
import net.mat0u5.do2manager.world.ItemManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class DatabaseCommand {
    public static int executeGetFromDB(ServerCommandSource source, int runNum, String query) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();

        DO2Run run = DatabaseManager.getRunByRunNumber(runNum);
        if (run == null) {
            self.sendMessage(Text.translatable("No run found!"));
            return -1;
        }
        try {
            if (query.equalsIgnoreCase("inventory_save")) ItemManager.giveItemStack(self,run.inventory_save);
            if (query.equalsIgnoreCase("items_bought")) ItemManager.giveItemStack(self,run.items_bought);
            if (query.equalsIgnoreCase("card_plays")) ItemManager.giveItemStack(self,run.card_plays);
            if (query.equalsIgnoreCase("artifact_item")) ItemManager.giveItemStack(self,run.artifact_item);
            if (query.equalsIgnoreCase("compass_item")) ItemManager.giveItemStack(self,run.compass_item);
            if (query.equalsIgnoreCase("deck_item")) ItemManager.giveItemStack(self,run.deck_item);
            if (query.equalsIgnoreCase("death_message")) self.sendMessage(Text.translatable(run.death_message));
            if (query.equalsIgnoreCase("death_pos")) self.sendMessage(Text.translatable(run.death_pos));
            if (query.equalsIgnoreCase("run_type")) self.sendMessage(Text.translatable(run.run_type));
            if (query.equalsIgnoreCase("runners")) self.sendMessage(Text.translatable(String.join(", ", run.runners)));
            if (query.equalsIgnoreCase("finishers")) self.sendMessage(Text.translatable(String.join(", ", run.finishers)));
            if (query.equalsIgnoreCase("difficulty")) self.sendMessage(Text.translatable(String.valueOf(run.difficulty)));
            if (query.equalsIgnoreCase("run_number")) self.sendMessage(Text.translatable(String.valueOf(run.run_number)));
            if (query.equalsIgnoreCase("run_length")) self.sendMessage(Text.translatable(String.valueOf(run.run_length)));
            if (query.equalsIgnoreCase("timestamp_artifact")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_artifact)));
            if (query.equalsIgnoreCase("timestamp_lvl2_entry")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl2_entry)));
            if (query.equalsIgnoreCase("timestamp_lvl3_entry")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl3_entry)));
            if (query.equalsIgnoreCase("timestamp_lvl4_entry")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl4_entry)));
            if (query.equalsIgnoreCase("timestamp_lvl4_exit")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl4_exit)));
            if (query.equalsIgnoreCase("timestamp_lvl3_exit")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl3_exit)));
            if (query.equalsIgnoreCase("timestamp_lvl2_exit")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl2_exit)));
            if (query.equalsIgnoreCase("timestamp_lvl1_exit")) self.sendMessage(Text.translatable(String.valueOf(run.timestamp_lvl1_exit)));
        }catch(Exception e) {}

        return 1;
    }
}
