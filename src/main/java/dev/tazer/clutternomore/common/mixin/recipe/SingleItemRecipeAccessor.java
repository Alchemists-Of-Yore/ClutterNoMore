package dev.tazer.clutternomore.common.mixin.recipe;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SingleItemRecipe.class)
public interface SingleItemRecipeAccessor {
    //? if >26 {
    /*@Accessor
    ItemStackTemplate getResult();
    *///?} else {
    @Accessor
    ItemStack getResult();
    //?}
}
