//? if forge && =1.20.1 {
package dev.tazer.clutternomore.common.mixin.item;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.item.ItemStackLinkedSet$1")
public class ItemStackLinkedSetMixin {
    @Inject(method = "hashCode(Lnet/minecraft/world/item/ItemStack;)I", at = @At("HEAD"), cancellable = true)
    private void cnm$hashCode(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        if (stack != null && ShapeMap.isShape(stack)) {
            cir.setReturnValue(ItemStackLinkedSet.TYPE_AND_TAG.hashCode(ShapeMap.transferStack(stack, 0)));
        }
    }
}
//?} else {
/*package dev.tazer.clutternomore.common.mixin.item;

import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackLinkedSetMixin {
}
*///?}
