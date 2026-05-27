package dev.tazer.clutternomore.common.mixin.recipe;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if <26
//import java.util.Arrays;

@Mixin(Ingredient.class)
public class IngredientMixin {
    //? if >=26 {
    @Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    private void cnm$test(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() || stack == null || stack.isEmpty()) return;
        Item myItem = stack.getItem();
        Ingredient self = (Ingredient) (Object) this;
        if (self.items().anyMatch(h -> ShapeMap.inSameShapeSet(h.value(), myItem))) {
            cir.setReturnValue(true);
        }
    }
    //?} else {
    /*@Inject(method = "test(Lnet/minecraft/world/item/ItemStack;)Z", at = @At("RETURN"), cancellable = true)
    private void cnm$test(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue() || stack == null || stack.isEmpty()) return;
        Item myItem = stack.getItem();
        Ingredient self = (Ingredient) (Object) this;
        if (Arrays.stream(self.getItems()).anyMatch(h -> ShapeMap.inSameShapeSet(h.getItem(), myItem))) {
            cir.setReturnValue(true);
        }
    }
    *///?}
}
