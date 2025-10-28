package dev.tazer.clutternomore.common.mixin.recipe;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SingleItemRecipe.class)
public class SingleItemRecipeMixin {
    //? if >1.21.1 {
    @Shadow
    @Final
    private ItemStack result;

    @Inject(method = "matches(Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/world/level/Level;)Z", at = @At(value = "RETURN"), cancellable = true)
    private void noMatches(CallbackInfoReturnable<ItemStack> cir) {
        if (ShapeMap.isShape(result.getItem())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }

    @Inject(method = "matches(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Z", at = @At(value = "RETURN"), cancellable = true)
    private void noMatches2(CallbackInfoReturnable<ItemStack> cir) {
        if (ShapeMap.isShape(result.getItem())) {
            cir.setReturnValue(ItemStack.EMPTY);
        }
    }
    //?}

}
