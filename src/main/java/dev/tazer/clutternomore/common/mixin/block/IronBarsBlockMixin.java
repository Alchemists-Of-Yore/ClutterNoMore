package dev.tazer.clutternomore.common.mixin.block;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(IronBarsBlock.class)
public class IronBarsBlockMixin {
    @WrapMethod(method = "attachsTo")
    private boolean attachToVerticalSlabs(BlockState state, boolean solidSide, Operation<Boolean> original) {
        if (state.getBlock() instanceof VerticalSlabBlock) return true;
        return original.call(state, solidSide);
    }
}
