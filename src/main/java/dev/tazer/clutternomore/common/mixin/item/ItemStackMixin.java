package dev.tazer.clutternomore.common.mixin.item;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    //~ if >1.20.1 'isSameItemSameTags' -> 'isSameItemSameComponents' {
    @Inject(method = "isSameItemSameComponents", at = @At("RETURN"), cancellable = true)
    private static void cnm$isSameItemSameComponents(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && ShapeMap.inSameShapeSet(stack.getItem(), other.getItem())) {
            cir.setReturnValue(ItemStack.isSameItemSameComponents(ShapeMap.transferStack(stack, 0), ShapeMap.transferStack(other, 0)));
        }
    }
    //~}
}
