package net.mat0u5.do2manager.tcg;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.PermissionManager;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.PlayerInventoryScanner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

public class TCG_Commands {
    public static int generateDeck(ServerCommandSource source, String type) {
        MinecraftServer server = source.getServer();
        final ServerPlayerEntity self = source.getPlayer();
        if (self == null) return -1;
        if (type.equalsIgnoreCase("hermit")) self.giveItemStack(TCG_DeckCreator.getHermitPack());
        if (type.equalsIgnoreCase("booster")) self.giveItemStack(TCG_DeckCreator.getBoosterPack());
        if (type.equalsIgnoreCase("starter")) self.giveItemStack(TCG_DeckCreator.getStarterDeck());
        if (type.equalsIgnoreCase("alterEgo")) self.giveItemStack(TCG_DeckCreator.getAlterEgoPack());
        if (type.equalsIgnoreCase("effect")) self.giveItemStack(TCG_DeckCreator.getEffectPack());
        if (type.equalsIgnoreCase("item")) self.giveItemStack(TCG_DeckCreator.getItemPack());

        return 1;
    }
    public static int databaseUpdate(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final ServerPlayerEntity self = source.getPlayer();

        List<ItemStack> itemsInv = PlayerInventoryScanner.getALLItemsFromInv(self.getInventory(),false);

        DatabaseManager.deleteTCGItems();
        for (ItemStack item : itemsInv) {
            DatabaseManager.addTCGItem(item);
        }

        return 1;
    }
    public static int reload(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final ServerPlayerEntity self = source.getPlayer();

        TCG_Items.reload();

        return 1;
    }
}
