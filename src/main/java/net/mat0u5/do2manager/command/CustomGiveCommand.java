package net.mat0u5.do2manager.command;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mat0u5.do2manager.database.DatabaseManager;
import net.mat0u5.do2manager.utils.OtherUtils;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;

import java.util.*;

public class CustomGiveCommand {

    public static int execute(ServerCommandSource source, String item, Collection<ServerPlayerEntity> targets, int count) throws CommandSyntaxException {
        ItemStack itemStack = getItemFromString(item, 1).copy();
        int maxCount = itemStack.getMaxCount();
        int j = maxCount * 100;
        if (count > j) {
            source.sendError(Text.translatable("commands.give.failed.toomanyitems", new Object[]{j, itemStack.toHoverableText()}));
            return 0;
        }

        Iterator targetsIterator = targets.iterator();

        label44:
        while(targetsIterator.hasNext()) {
            ServerPlayerEntity player = (ServerPlayerEntity)targetsIterator.next();
            int newCount = count;

            while(true) {
                while(true) {
                    if (newCount <= 0) {
                        continue label44;
                    }

                    int giveCount = Math.min(maxCount, newCount);
                    newCount -= giveCount;
                    ItemStack itemStack2 = getItemFromString(item, giveCount).copy();
                    boolean bl = player.getInventory().insertStack(itemStack2);
                    ItemEntity itemEntity;
                    if (bl && itemStack2.isEmpty()) {
                        itemEntity = player.dropItem(itemStack, false);
                        if (itemEntity != null) {
                            itemEntity.setDespawnImmediately();
                        }

                        player.getWorld().playSound((PlayerEntity)null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                        player.currentScreenHandler.sendContentUpdates();
                    } else {
                        itemEntity = player.dropItem(itemStack2, false);
                        if (itemEntity != null) {
                            itemEntity.resetPickupDelay();
                            itemEntity.setOwner(player.getUuid());
                        }
                    }
                }
            }
        }

        if (targets.size() == 1) {
            source.sendFeedback(() -> {
                return Text.translatable("commands.give.success.single", new Object[]{count, itemStack.toHoverableText(), ((ServerPlayerEntity)targets.iterator().next()).getDisplayName()});
            }, true);
        } else {
            source.sendFeedback(() -> {
                return Text.translatable("commands.give.success.single", new Object[]{count, itemStack.toHoverableText(), targets.size()});
            }, true);
        }

        return targets.size();
    }

    public static int addMainHandItem(ServerCommandSource source, String name) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        if (self == null) return -1;

        ItemStack item = self.getMainHandStack();
        if (item == null || item.isEmpty()) {
            item = self.getOffHandStack();;
        }
        if (item == null || item.isEmpty()) {
            source.sendError(Text.of("You're not holding an item."));
            return -1;
        }
        ItemStack setItem = item.copy();
        setItem.setCount(1);
        if (customItems.containsKey(name)) {
            source.sendMessage(Text.literal("Updated db entry for: ").append(setItem.getName()));
            customItems.put(name, setItem);
            DatabaseManager.deleteCustomItem(name);
            DatabaseManager.addCustomItems(name, setItem);
        }
        else {
            source.sendMessage(Text.literal("Added db entry for: ").append(setItem.getName()));
            DatabaseManager.addCustomItems(name, setItem);
        }
        customItems.put(name, setItem);
        source.sendMessage(Text.literal("You can now use /give to get it."));
        return 1;
    }

    public static int removeItem(ServerCommandSource source, String name) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        if (self == null) return -1;


        if (!customItems.containsKey(name)) {
            source.sendError(Text.literal("The db does not contain an item with that name."));
        }
        else {
            source.sendMessage(Text.literal("Removed db entry: " + name));
            DatabaseManager.deleteCustomItem(name);
            customItems.remove(name);
        }
        return 1;
    }

    public static int removeAllItems(ServerCommandSource source) {
        MinecraftServer server = source.getServer();
        final PlayerEntity self = source.getPlayer();
        if (self == null) return -1;

        DatabaseManager.deleteCustomItems();
        source.sendError(Text.literal("Deleted all saved items."));
        customItems = new HashMap<>();
        return 1;
    }
    public static int reloadItems(ServerCommandSource source) {
        source.sendError(Text.literal("Reloaded all saved items."));
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
