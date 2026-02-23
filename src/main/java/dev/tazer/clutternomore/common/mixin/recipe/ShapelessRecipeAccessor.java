package dev.tazer.clutternomore.common.mixin.recipe;

import net.minecraft.world.item.ItemStack;
//? if >26
/*import net.minecraft.world.item.ItemStackTemplate;*/
import net.minecraft.world.item.crafting.ShapelessRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ShapelessRecipe.class)
public interface ShapelessRecipeAccessor {
    //? if >26 {
    /*@Accessor
    ItemStackTemplate getResult();
    *///?} else {
    @Accessor
    ItemStack getResult();
    //?}
}
