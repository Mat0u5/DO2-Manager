package net.mat0u5.do2manager.utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntityUtils {
    public static boolean isItemInRange(ServerLevel world, BlockPos position, double range) {
        Vec3 center = new Vec3(position.getX(), position.getY(), position.getZ());
        AABB box = new AABB(center, center).inflate(range);
        for (ItemEntity itemEntity : world.getEntitiesOfClass(ItemEntity.class, box, entity -> true)) {
            if (itemEntity.distanceToSqr(center) <= range * range) {
                return true;
            }
        }
        return false;
    }
    public static List<ItemStack> getItemStacksInBox(ServerLevel world, BlockPos pos1, BlockPos pos2) {
        AABB box = new AABB(pos1.getCenter(), pos2.getCenter());

        List<ItemStack> itemStacks = new ArrayList<>();
        for (ItemEntity itemEntity : world.getEntitiesOfClass(ItemEntity.class, box, entity -> true)) {
            itemStacks.add(itemEntity.getItem());
        }
        return itemStacks;
    }
}
