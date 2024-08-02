package net.mat0u5.do2manager.utils;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class EntityUtils {
    public static boolean isItemInRange(ServerWorld world, BlockPos position, double range) {
        Vec3d center = new Vec3d(position.getX(), position.getY(), position.getZ());
        Box box = new Box(center, center).expand(range);
        for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, box, entity -> true)) {
            if (itemEntity.squaredDistanceTo(center) <= range * range) {
                return true;
            }
        }
        return false;
    }
    public static List<ItemStack> getItemStacksInBox(ServerWorld world, BlockPos pos1, BlockPos pos2) {
        Box box = new Box(pos1, pos2);

        List<ItemStack> itemStacks = new ArrayList<>();
        for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, box, entity -> true)) {
            itemStacks.add(itemEntity.getStack());
        }
        return itemStacks;
    }
}
