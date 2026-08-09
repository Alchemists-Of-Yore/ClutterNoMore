package dev.tazer.clutternomore.common;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
//? if >26
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CHooks {
    public static boolean denyItem(Item item) {
        return ShapeMap.isShape(item);
    }

    public static List<ItemStack> getDrops(List<ItemStack> old, BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, @Nullable Entity entity
            //? if >26
            , ItemInstance tool
            //? if <26
            //, ItemStack tool
    ) {
        Item item = state.getBlock().asItem();
        if (ShapeMap.isShape(item) && ClutterNoMore.STARTUP_CONFIG.INHERIT_LOOT_TABLES.value()) {
            if (isNonLowerMultiBlockPart(state)) return old;

            LootParams.Builder lootparams$builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.THIS_ENTITY, entity).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
            BlockState newState = Block.byItem(ShapeMap.getParent(item)).defaultBlockState();
            for (Property<?> property : state.getProperties()) {
                newState = copyProperty(state, newState, property);
            }
            return newState.getDrops(lootparams$builder);
        }

        return old;
    }

    private static boolean isNonLowerMultiBlockPart(BlockState state) {
        for (Property<?> property : state.getProperties()) {
            if (MultiBlockLootPart.isNonLower(property.getName(), propertyValueName(state, property))) {
                return true;
            }
        }
        return false;
    }

    private static <T extends Comparable<T>> String propertyValueName(BlockState state, Property<T> property) {
        return property.getName(state.getValue(property));
    }

    private static <T extends Comparable<T>> BlockState copyProperty(BlockState source, BlockState target, Property<T> property) {
        return target.hasProperty(property) ? target.setValue(property, source.getValue(property)) : target;
    }
}
