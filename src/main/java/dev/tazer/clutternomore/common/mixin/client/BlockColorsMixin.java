package dev.tazer.clutternomore.common.mixin.client;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if <26.1 {
/*import net.minecraft.world.level.BlockAndTintGetter;
 *///?} else {
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
//?}

@Mixin(BlockColors.class)
public class BlockColorsMixin {

    //? if <26.1 {
    /*@Inject(method = "getColor(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/core/BlockPos;I)I", at = @At("HEAD"), cancellable = true)
    private void cnm$getShapeBlockColor(BlockState blockState, BlockAndTintGetter level, BlockPos pos, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        Item item = blockState.getBlock().asItem();
        if (item != null && ShapeMap.isShape(item)) {
            Item parent = ShapeMap.getParent(item);
            if (parent instanceof BlockItem blockItem) {
                BlockState parentState = blockItem.getBlock().defaultBlockState();
                int color = ((BlockColors) (Object) this).getColor(parentState, level, pos, tintIndex);
                if (color != -1) {
                    cir.setReturnValue(color);
                }
            }
        }
    }
    *///?} else {
    @Inject(method = "getTintSource", at = @At("HEAD"), cancellable = true)
    private void cnm$getShapeTintSource(BlockState state, int layer, CallbackInfoReturnable<BlockTintSource> cir) {
        Item item = state.getBlock().asItem();
        if (ShapeMap.isShape(item)) {
            Item parent = ShapeMap.getParent(item);
            if (parent instanceof BlockItem blockItem) {
                BlockState parentState = blockItem.getBlock().defaultBlockState();
                BlockTintSource source = ((BlockColors) (Object) this).getTintSource(parentState, layer);
                if (source != null) {
                    cir.setReturnValue(source);
                }
            }
        }
    }
    //?}
}