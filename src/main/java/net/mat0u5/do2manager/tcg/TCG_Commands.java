package net.mat0u5.do2manager.tcg;

import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.PermissionManager;
import net.mat0u5.do2manager.world.ItemManager;
import net.mat0u5.do2manager.world.PlayerInventoryScanner;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static net.mat0u5.do2manager.tcg.TCG_Items.*;

public class TCG_Commands {
    public static int generateDeck(CommandSourceStack source, String type, int amount, ServerPlayer target) {
        if (target == null) return -1;
        for (int i = 0; i < amount; i++) {
            if (type.equalsIgnoreCase("hermit")) target.addItem(TCG_DeckCreator.getHermitPack());
            if (type.equalsIgnoreCase("booster")) target.addItem(TCG_DeckCreator.getBoosterPack());
            if (type.equalsIgnoreCase("starter")) target.addItem(TCG_DeckCreator.getStarterDeck());
            if (type.equalsIgnoreCase("alterEgo")) target.addItem(TCG_DeckCreator.getAlterEgoPack());
            if (type.equalsIgnoreCase("effect")) target.addItem(TCG_DeckCreator.getEffectPack());
            if (type.equalsIgnoreCase("item")) target.addItem(TCG_DeckCreator.getItemPack());
        }

        return 1;
    }
    public static int databaseUpdate(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();

        List<ItemStack> itemsInv = PlayerInventoryScanner.getALLItemsFromInv(self.getInventory(),false);

        DatabaseManager.deleteTCGItems();
        for (ItemStack item : itemsInv) {
            DatabaseManager.addTCGItem(item);
        }

        source.sendSystemMessage(Component.nullToEmpty("Saved " + itemsInv.size() + " items to the database!"));

        return 1;
    }
    public static int test(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();

        List<ItemStack> itemsInv = PlayerInventoryScanner.getALLItemsFromInv(self.getInventory(),false);

        for (ItemStack item : itemsInv) {
            if (ItemManager.hasCustomComponentEntry(item, NBT_TCG)) {

                String type = ItemManager.getCustomComponentString(item, NBT_TYPE);
                if (type == null && item.get(DataComponents.CUSTOM_NAME) != null) {
                    String itemName = item.get(DataComponents.CUSTOM_NAME).getString();
                    int test = 0;
                    if (ItemManager.hasCustomComponentEntry(item, NBT_HERMITS) || ItemManager.hasCustomComponentEntry(item, NBT_ALTEREGO)) {
                        if (itemName.contains("Miner Type")) test += ItemManager.setCustomComponentString(item, "type", "miner");
                        if (itemName.contains("Speedrunner Type")) test += ItemManager.setCustomComponentString(item, "type", "speedrunner");
                        if (itemName.contains("Balanced Type")) test += ItemManager.setCustomComponentString(item, "type", "balanced");
                        if (itemName.contains("Builder Type")) test += ItemManager.setCustomComponentString(item, "type", "builder");
                        if (itemName.contains("Redstone Type")) test += ItemManager.setCustomComponentString(item, "type", "redstoner");
                        if (itemName.contains("Farm Type")) test += ItemManager.setCustomComponentString(item, "type", "farm");
                        if (itemName.contains("Prankster Type")) test += ItemManager.setCustomComponentString(item, "type", "prankster");
                        if (itemName.contains("Terraform Type")) test += ItemManager.setCustomComponentString(item, "type", "terraformer");
                        if (itemName.contains("PVP Type")) test += ItemManager.setCustomComponentString(item, "type", "pvp");
                        if (itemName.contains("Explorer Type")) test += ItemManager.setCustomComponentString(item, "type", "explorer");
                    }
                    else {
                        if (itemName.contains("Miner")) test += ItemManager.setCustomComponentString(item, "type", "miner");
                        if (itemName.contains("Speedrunner")) test += ItemManager.setCustomComponentString(item, "type", "speedrunner");
                        if (itemName.contains("Balanced")) test += ItemManager.setCustomComponentString(item, "type", "balanced");
                        if (itemName.contains("Builder")) test += ItemManager.setCustomComponentString(item, "type", "builder");
                        if (itemName.contains("Redstone")) test += ItemManager.setCustomComponentString(item, "type", "redstoner");
                        if (itemName.contains("Farm")) test += ItemManager.setCustomComponentString(item, "type", "farm");
                        if (itemName.contains("Prankster")) test += ItemManager.setCustomComponentString(item, "type", "prankster");
                        if (itemName.contains("Terraform")) test += ItemManager.setCustomComponentString(item, "type", "terraformer");
                        if (itemName.contains("PVP")) test += ItemManager.setCustomComponentString(item, "type", "pvp");
                        if (itemName.contains("Explorer")) test += ItemManager.setCustomComponentString(item, "type", "explorer");
                    }
                    if (test == 0) System.out.println("Item name: " + itemName + "_ was not given a type");
                    if (test > 1) System.out.println("Item name: " + itemName + "_ was given more than one type");
                }
            }
        }

        source.sendSystemMessage(Component.nullToEmpty("updated"));

        return 1;
    }
    public static int reload(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final ServerPlayer self = source.getPlayer();

        TCG_Items.reload();
        source.sendSystemMessage(Component.nullToEmpty("Reloaded TCG Items from the database!"));

        return 1;
    }
}
