package dev.tazer.clutternomore.common.mixin.recipe;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.Container;
//? > 1.20.1 {
import net.minecraft.world.item.crafting.SingleRecipeInput;
//?}
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StonecutterRecipe.class)
public class StonecutterRecipeMixin extends SingleItemRecipeMixin {
    //? if =1.21.1 {
    /*@Inject(method = "matches(Lnet/minecraft/world/item/crafting/SingleRecipeInput;Lnet/minecraft/world/level/Level;)Z", at = @At("RETURN"), cancellable = true)
    private void noMatches(SingleRecipeInput input, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (ShapeMap.isShape(((SingleItemRecipeAccessor) (this)).getResult().getItem())) {
            cir.setReturnValue(false);
        }
    }
    *///?}

    //? if =1.20.1 {
    /*@Inject(method = "matches", at = @At("RETURN"), cancellable = true)
    private void noMatches(Container inv, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (ShapeMap.isShape(((SingleItemRecipeAccessor) (this)).getResult().getItem())) {
            cir.setReturnValue(false);
        }
    }
    *///?}
}