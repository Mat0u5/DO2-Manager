package net.mat0u5.do2manager.blockblocker;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class BlockBlocker {
    public static final List<String> defaultValues = List.of("minecraft:bedrock", "minecraft:air");
    public static BlockBlockerConfig config;
    public static void onInitialize() {
        AutoConfig.register(BlockBlockerConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(BlockBlockerConfig.class).get();
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            if ((config.general.opBypass && player.hasPermissionLevel(2)) || (config.general.creativeBypass && player.getAbilities().creativeMode)) {
                return ActionResult.PASS;
            }
            BlockPos target = hitResult.getBlockPos();

            if (player.getBlockPos() == target || world.isClient()) {
                return ActionResult.PASS;
            }

            BlockState state = world.getBlockState(hitResult.getBlockPos());
            String id = Registries.BLOCK.getId(state.getBlock()).toString();


            if(config.general.noInteract.contains(id)) {
                return ActionResult.FAIL;
            }
            //If the stack is a block, check if it can be placed
            if (player.getStackInHand(hand).getItem() instanceof BlockItem blockItem) {
                String idHand = Registries.BLOCK.getId(blockItem.getBlock()).toString();
                if(config.general.noPlace.contains(idHand)) {
                    return ActionResult.FAIL;
                }
            }
            return ActionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, dir) -> {
            if ((config.general.opBypass && player.hasPermissionLevel(2)) || (config.general.creativeBypass && player.getAbilities().creativeMode)) {
                return true;
            }
            if (world.isClient()) {
                return true;
            }

            String id = Registries.BLOCK.getId(state.getBlock()).toString();
            return !config.general.noHarvest.contains(id);
        });
    }
}
