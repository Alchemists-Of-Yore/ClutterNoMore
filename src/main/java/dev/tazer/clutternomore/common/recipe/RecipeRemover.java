package dev.tazer.clutternomore.common.recipe;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.mixin.recipe.ShapedRecipeAccessor;
import dev.tazer.clutternomore.common.mixin.recipe.ShapelessRecipeAccessor;
import dev.tazer.clutternomore.common.mixin.recipe.SingleItemRecipeAccessor;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.crafting.Recipe;
//? if >1.21 {
import net.minecraft.world.item.crafting.RecipeHolder;
//?}
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SingleItemRecipe;

//? if <26 {
/*import java.util.ArrayList;
*///?}

public class RecipeRemover {

    private static boolean isShapeResult(Recipe<?> recipe) {
        if (recipe instanceof ShapedRecipe shaped)
            return ShapeMap.isShape(((ShapedRecipeAccessor) shaped).getResult());
        if (recipe instanceof ShapelessRecipe shapeless)
            return ShapeMap.isShape(((ShapelessRecipeAccessor) shapeless).getResult());
        if (recipe instanceof SingleItemRecipe single)
            return ShapeMap.isShape(((SingleItemRecipeAccessor) single).getResult());
        return false;
    }

    public static void removeShapeRecipes(RecipeManager manager) {
        //? if <26 {
        /*int removed = 0;

        //? if >1.21 {
        ArrayList<RecipeHolder<?>> filtered = new ArrayList<>();
        for (RecipeHolder<?> holder : manager.getRecipes()) {
            if (isShapeResult(holder.value())) {
                removed++;
                continue;
            }
            filtered.add(holder);
        }
        //?} else {
        /^ArrayList<Recipe<?>> filtered = new ArrayList<>();
        for (Recipe<?> recipe : manager.getRecipes()) {
            if (isShapeResult(recipe)) {
                removed++;
                continue;
            }
            filtered.add(recipe);
        }
        ^///?}

        if (removed > 0) {
            manager.replaceRecipes(filtered);
            ClutterNoMore.LOGGER.info("Removed {} shape recipes", removed);
        }
        *///?}
    }
}
