package dev.tazer.clutternomore.common.mixin.recipe;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
//? >1.20.1 {
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeInput;
//?}
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapelessRecipe.class)
public class ShapelessRecipeMixin {
    //? >1.20.1 {
    @Inject(method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z", at=@At(value = "RETURN"), cancellable = true)
    private void noMatches(CraftingInput input, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (ShapeMap.isShape(((ShapelessRecipeAccessor) (this)).getResult().getItem())) {
            cir.setReturnValue(false);
        }
    }


    @Inject(method = "matches(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Z", at=@At(value = "RETURN"), cancellable = true)
    private void noMatches2(RecipeInput input, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (ShapeMap.isShape(((ShapelessRecipeAccessor) (this)).getResult().getItem())) {
            cir.setReturnValue(false);
        }
    }
    //?} else {
    /*@Inject(method = "matches(Lnet/minecraft/world/inventory/CraftingContainer;Lnet/minecraft/world/level/Level;)Z", at = @At("RETURN"), cancellable = true)
    private void noMatches(CraftingContainer inv, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (ShapeMap.isShape(((ShapelessRecipeAccessor) (this)).getResult().getItem())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "matches(Lnet/minecraft/world/Container;Lnet/minecraft/world/level/Level;)Z", at = @At("RETURN"), cancellable = true)
    private void noMatches2(Container par1, Level par2, CallbackInfoReturnable<Boolean> cir) {
        if (ShapeMap.isShape(((ShapelessRecipeAccessor) (this)).getResult().getItem())) {
            cir.setReturnValue(false);
        }
    }
    *///?}
}
