package dev.tazer.clutternomore.common.mixin.recipe;

//? if <1.21 {
/*import net.minecraft.world.item.crafting.Ingredient;
*///?}
import net.minecraft.world.item.crafting.SingleItemRecipe;
import org.spongepowered.asm.mixin.Mixin;
//? if <1.21 {
/*import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
*///?}

@Mixin(SingleItemRecipe.class)
public interface SingleItemRecipeAccessor {
    //? if <1.21 {
    /*@Accessor("ingredient")
    Ingredient getInput();

    @Accessor("ingredient")
    @Mutable
    void setInput(Ingredient ingredient);
    *///?}
}
