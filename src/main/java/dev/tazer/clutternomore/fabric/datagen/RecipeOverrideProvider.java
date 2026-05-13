package dev.tazer.clutternomore.fabric.datagen;

//? fabric {

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.world.item.Items.*;

public class RecipeOverrideProvider extends FabricRecipeProvider {
    public RecipeOverrideProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output
                //? if >1.21 {
                , registriesFuture
                //?}
        );
    }


    //? if >1.21.6 {
    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput output) {
        return new RecipeProvider(registryLookup, output) {

            @Override
            public void buildRecipes() {

                //?} else if >1.21 {

        /*@Override
        public void buildRecipes(RecipeOutput output) {

        *///?} else {
        /*@Override
        public void buildRecipes(Consumer<net.minecraft.data.recipes.FinishedRecipe> output) {
        *///?}


                // start recipes

                shaped(RecipeCategory.DECORATIONS, BARREL)
                        .pattern("#T#")
                        .pattern("# #")
                        .pattern("###")
                        .define('#', ItemTags.PLANKS)
                        .define('T', ItemTags.WOODEN_TRAPDOORS)
                        .save(output);

                this.shaped(RecipeCategory.BUILDING_BLOCKS, CHISELED_BOOKSHELF)
                        .define('#', ItemTags.PLANKS)
                        .define('P', PAPER)
                        .pattern("###")
                        .pattern("PPP")
                        .pattern("###")
                        .save(output);

                chiseled(CHISELED_DEEPSLATE, COBBLED_DEEPSLATE, output);
                chiseled(CHISELED_NETHER_BRICKS, NETHER_BRICKS, output);
                chiseled(CHISELED_POLISHED_BLACKSTONE, POLISHED_BLACKSTONE, output);
                chiseled(CHISELED_QUARTZ_BLOCK, QUARTZ_BLOCK, output);
                chiseled(CHISELED_RED_SANDSTONE, RED_SANDSTONE, output);
                chiseled(CHISELED_SANDSTONE, SANDSTONE, output);
                chiseled(CHISELED_STONE_BRICKS, STONE_BRICKS, output);
                //? if >1.21 {
                chiseled(CHISELED_COPPER, CUT_COPPER, output);
                chiseled(EXPOSED_CHISELED_COPPER, EXPOSED_CUT_COPPER, output);
                chiseled(WEATHERED_CHISELED_COPPER, WEATHERED_CUT_COPPER, output);
                chiseled(OXIDIZED_CHISELED_COPPER, OXIDIZED_CUT_COPPER, output);

                chiseled(WAXED_CHISELED_COPPER, WAXED_CUT_COPPER, output);
                chiseled(WAXED_EXPOSED_CHISELED_COPPER, WAXED_EXPOSED_CUT_COPPER, output);
                chiseled(WAXED_WEATHERED_CHISELED_COPPER, WAXED_WEATHERED_CUT_COPPER, output);
                chiseled(WAXED_OXIDIZED_CHISELED_COPPER, WAXED_OXIDIZED_CUT_COPPER, output);

                chiseled(CHISELED_TUFF, TUFF, output);
                chiseled(CHISELED_TUFF_BRICKS, TUFF_BRICKS, output);
                //?}

                //? if >1.21.4
                chiseled(CHISELED_RESIN_BRICKS, RESIN_BRICK_SLAB, output);


            }

            private void chiseled(Item chiseledRedSandstone, Item redSandstone, RecipeOutput output) {
                this.shaped(RecipeCategory.BUILDING_BLOCKS, chiseledRedSandstone, 4)
                        .define('#', redSandstone)
                        .pattern(" # ")
                        .pattern("# #")
                        .pattern(" # ")
                        .save(output);
            }

            //? if <1.21.6 {
			/*private ShapedRecipeBuilder shaped(RecipeCategory recipeCategory, Item item) {
				return ShapedRecipeBuilder.shaped(recipeCategory, item);
			}
			private ShapedRecipeBuilder shaped(RecipeCategory recipeCategory, Item item, int i) {
				return ShapedRecipeBuilder.shaped(recipeCategory, item, i);
			}
			*///?} else {
            private void stonecutterResultFromBase(RecipeOutput output, RecipeCategory recipeCategory, Item item1, Item item) {
                stonecutterResultFromBase(recipeCategory, item1, item);
            }
            private void stonecutterResultFromBase(RecipeOutput output, RecipeCategory recipeCategory, Item item1, Item item, int i) {
                stonecutterResultFromBase(recipeCategory, item1, item, i);
            }
        };
    }


    //?}

    @Override
    public String getName() {
        return "Vanilla Overrides";
    }

    @Override
    protected Identifier getRecipeIdentifier(Identifier identifier) {
        return Identifier.fromNamespaceAndPath("minecraft", identifier.getPath());
    }
}
//?}