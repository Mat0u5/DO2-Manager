package net.mat0u5.do2manager.blockblocker;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;

public class BlockBlocker {
    public static final List<String> defaultValues = List.of("minecraft:bedrock", "minecraft:air");
    public static BlockBlockerConfig config;
    public static void onInitialize() {
        AutoConfig.register(BlockBlockerConfig.class, GsonConfigSerializer::new);
        config = AutoConfig.getConfigHolder(BlockBlockerConfig.class).get();
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {

            if ((config.general.opBypass && player.hasPermissions(2)) || (config.general.creativeBypass && player.getAbilities().instabuild)) {
                return InteractionResult.PASS;
            }
            BlockPos target = hitResult.getBlockPos();

            if (player.blockPosition() == target || world.isClientSide()) {
                return InteractionResult.PASS;
            }

            BlockState state = world.getBlockState(hitResult.getBlockPos());
            String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();


            if(config.general.noInteract.contains(id)) {
                return InteractionResult.FAIL;
            }
            //If the stack is a block, check if it can be placed
            if (player.getItemInHand(hand).getItem() instanceof BlockItem blockItem) {
                String idHand = BuiltInRegistries.BLOCK.getKey(blockItem.getBlock()).toString();
                if(config.general.noPlace.contains(idHand)) {
                    return InteractionResult.FAIL;
                }
            }
            return InteractionResult.PASS;
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, dir) -> {
            if ((config.general.opBypass && player.hasPermissions(2)) || (config.general.creativeBypass && player.getAbilities().instabuild)) {
                return true;
            }
            if (world.isClientSide()) {
                return true;
            }

            String id = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
            return !config.general.noHarvest.contains(id);
        });
    }
}
