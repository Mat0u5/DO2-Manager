package net.mat0u5.do2manager.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import java.util.*;

public class CustomGiveCommand {

    public static int execute(CommandSourceStack source, String item, Collection<ServerPlayer> targets, int count) throws CommandSyntaxException {
        ItemStack itemStack = getItemFromString(item, 1).copy();
        int maxCount = itemStack.getMaxStackSize();
        int j = maxCount * 100;
        if (count > j) {
            source.sendFailure(Component.translatable("commands.give.failed.toomanyitems", new Object[]{j, itemStack.getDisplayName()}));
            return 0;
        }

        Iterator targetsIterator = targets.iterator();

        label44:
        while(targetsIterator.hasNext()) {
            ServerPlayer player = (ServerPlayer)targetsIterator.next();
            int newCount = count;

            while(true) {
                while(true) {
                    if (newCount <= 0) {
                        continue label44;
                    }

                    int giveCount = Math.min(maxCount, newCount);
                    newCount -= giveCount;
                    ItemStack itemStack2 = getItemFromString(item, giveCount).copy();
                    boolean bl = player.getInventory().add(itemStack2);
                    ItemEntity itemEntity;
                    if (bl && itemStack2.isEmpty()) {
                        itemEntity = player.drop(itemStack, false);
                        if (itemEntity != null) {
                            itemEntity.makeFakeItem();
                        }

                        player.level().playSound((Player)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        player.containerMenu.broadcastChanges();
                    } else {
                        itemEntity = player.drop(itemStack2, false);
                        if (itemEntity != null) {
                            itemEntity.setNoPickUpDelay();
                            itemEntity.setTarget(player.getUUID());
                        }
                    }
                }
            }
        }

        if (targets.size() == 1) {
            source.sendSuccess(() -> {
                return Component.translatable("commands.give.success.single", new Object[]{count, itemStack.getDisplayName(), ((ServerPlayer)targets.iterator().next()).getDisplayName()});
            }, true);
        } else {
            source.sendSuccess(() -> {
                return Component.translatable("commands.give.success.single", new Object[]{count, itemStack.getDisplayName(), targets.size()});
            }, true);
        }

        return targets.size();
    }

    public static int addMainHandItem(CommandSourceStack source, String name) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        if (self == null) return -1;

        ItemStack item = self.getMainHandItem();
        if (item == null || item.isEmpty()) {
            item = self.getOffhandItem();;
        }
        if (item == null || item.isEmpty()) {
            source.sendFailure(Component.nullToEmpty("You're not holding an item."));
            return -1;
        }
        ItemStack setItem = item.copy();
        setItem.setCount(1);
        if (customItems.containsKey(name)) {
            source.sendSystemMessage(Component.literal("Updated db entry for: ").append(setItem.getHoverName()));
            customItems.put(name, setItem);
            DatabaseManager.deleteCustomItem(name);
            DatabaseManager.addCustomItems(name, setItem);
        }
        else {
            source.sendSystemMessage(Component.literal("Added db entry for: ").append(setItem.getHoverName()));
            DatabaseManager.addCustomItems(name, setItem);
        }
        customItems.put(name, setItem);
        source.sendSystemMessage(Component.literal("You can now use /give to get it."));
        return 1;
    }

    public static int removeItem(CommandSourceStack source, String name) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        if (self == null) return -1;


        if (!customItems.containsKey(name)) {
            source.sendFailure(Component.literal("The db does not contain an item with that name."));
        }
        else {
            source.sendSystemMessage(Component.literal("Removed db entry: " + name));
            DatabaseManager.deleteCustomItem(name);
            customItems.remove(name);
        }
        return 1;
    }

    public static int removeAllItems(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        final Player self = source.getPlayer();
        if (self == null) return -1;

        DatabaseManager.deleteCustomItems();
        source.sendFailure(Component.literal("Deleted all saved items."));
        customItems = new HashMap<>();
        return 1;
    }
    public static int reloadItems(CommandSourceStack source) {
        source.sendFailure(Component.literal("Reloaded all saved items."));
        loadItemStacks();
        return 1;
    }

    public static HashMap<String, ItemStack> customItems = new HashMap<>();

    public static void loadItemStacks() {
        customItems = DatabaseManager.getAllCustomItems();
    }

    public static ItemStack getItemFromString(String name, int count) {
        if (!customItems.containsKey(name)) return ItemStack.EMPTY;
        ItemStack item = customItems.get(name);
        item.setCount(count);
        return item;
    }
    public static List<String> getAllItems() {
        List<String> result = new ArrayList<>();
        for (String name : customItems.keySet()) {
            result.add(name);
        }
        return result;
    }
}
