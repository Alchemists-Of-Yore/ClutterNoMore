package dev.tazer.clutternomore.common.mixin.recipe;

//? if >=1.21 {
import net.minecraft.core.HolderLookup;
//?}
//? if >26 {
import net.minecraft.world.item.crafting.RecipeMap;
//?}
import net.minecraft.world.item.crafting.RecipeManager;
import org.spongepowered.asm.mixin.Mixin;
//? if >=1.21 {
import org.spongepowered.asm.mixin.gen.Accessor;
//?}

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    //? if >26 {
    @Accessor("recipes")
    RecipeMap cnm$getRecipeMap();

    @Accessor("recipes")
    void cnm$setRecipeMap(RecipeMap recipeMap);
    //?}

    //? if >=1.21 {
    @Accessor("registries")
    HolderLookup.Provider cnm$getRegistries();
    //?}
}
