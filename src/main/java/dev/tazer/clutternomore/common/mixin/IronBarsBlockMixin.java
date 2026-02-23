package dev.tazer.clutternomore.common.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(IronBarsBlock.class)
public class IronBarsBlockMixin {
    @WrapMethod(method = "attachsTo")
    private boolean attachToVerticalSlabs(BlockState state, boolean solidSide, Operation<Boolean> original) {
        if (state.getBlock() instanceof VerticalSlabBlock) return true;
        return original.call(state, solidSide);
    }
}
