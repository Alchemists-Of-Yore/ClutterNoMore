package dev.tazer.clutternomore.common.mixin.recipe;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class ShapedRecipeMixin {
    //? if >1.21.1 {

    @Shadow
    @Final
    ItemStack result;

    @Inject(method = "matches(Lnet/minecraft/world/item/crafting/CraftingInput;Lnet/minecraft/world/level/Level;)Z", at=@At(value = "RETURN"), cancellable = true)
    private void no(CraftingInput input, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (ShapeMap.isShape(result.getItem())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "matches(Lnet/minecraft/world/item/crafting/RecipeInput;Lnet/minecraft/world/level/Level;)Z", at=@At(value = "RETURN"), cancellable = true)
    private void no(RecipeInput input, Level level, CallbackInfoReturnable<Boolean> cir) {
        if (ShapeMap.isShape(result.getItem())) {
            cir.setReturnValue(false);
        }
    }
    //?}
}
