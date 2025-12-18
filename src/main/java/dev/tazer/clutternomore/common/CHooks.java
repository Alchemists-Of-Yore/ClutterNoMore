package dev.tazer.clutternomore.common;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

    public static List<ItemStack> getDrops(List<ItemStack> old, BlockState state, ServerLevel level, BlockPos pos, BlockEntity blockEntity, @Nullable Entity entity, ItemStack tool) {
        if (ShapeMap.isShape(state.getBlock().asItem())) {
            LootParams.Builder lootparams$builder = (new LootParams.Builder(level)).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos)).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.THIS_ENTITY, entity).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity);
            BlockState newState = Block.byItem(ShapeMap.getParent(state.getBlock().asItem())).defaultBlockState();
            return newState.getDrops(lootparams$builder);
        }

        return old;
    }
}
