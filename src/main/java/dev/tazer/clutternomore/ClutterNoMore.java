package dev.tazer.clutternomore;

//import dev.tazer.clutternomore.common.data.DynamicServerResources;
import dev.tazer.clutternomore.client.assets.AssetGenerator;
import dev.tazer.clutternomore.client.assets.StepGenerator;
import dev.tazer.clutternomore.client.assets.VerticalSlabGenerator;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import dev.tazer.clutternomore.common.registry.CBlocks;
import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
//? if >1.21
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
//? if forge
/*import net.minecraftforge.registries.RegistryManager;*/
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ClutterNoMore {
    public static final String MODID = "clutternomore";
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");
    public static final CNMConfig.StartupConfig STARTUP_CONFIG = CNMConfig.StartupConfig.createToml(Platform.INSTANCE.configPath(), "", MODID+"-startup", CNMConfig.StartupConfig.class);

    public static void init() {
        LOGGER.info("Initializing {} on {}", MODID, Platform.INSTANCE.loader());
        BlockSetRegistry.init();
//        DynamicServerResources.register();
    }

    public static ResourceLocation location(String path) {
        return location(MODID, path);
    }

    public static ResourceLocation location(String namespace, String path) {
        //? if >1.21
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
        //? if <1.21
        /*return new ResourceLocation(namespace, path);*/
    }


    public static ResourceLocation parse(String id) {
        //? if >1.21
        return ResourceLocation.parse(id);
        //? if <1.21
        /*return new ResourceLocation(id);*/
    }

    public static void load(
            //? if >1.21 {
            HolderLookup.Provider
            //?} else {
            /*RegistryAccess
            *///?}
                    registries, RecipeManager recipeManager) {
        //FIXME 1.21.8
        //? if <1.21.2 {
        /*boolean changed = false;
        var originalRecipes = recipeManager.getRecipes();
        ArrayList<
        //? if >1.21 {
        RecipeHolder<?>
         //?} else {
        /^Recipe<?>
        ^///?}
        > newRecipes = new ArrayList<>();

        for (
                //? if >1.21 {
                RecipeHolder<?> recipeHolder
                //?} else {
                /^Recipe<?> recipe
                ^///?}
                        : originalRecipes) {
            //? if >1.21
            Recipe<?> recipe = recipeHolder.value();

            Item result = recipe.getResultItem(registries).getItem();

            if (ShapeMap.isShape(result)) continue;

            NonNullList<Ingredient> ingredients = recipe.getIngredients();
            for (Ingredient ingredient : new ArrayList<>(ingredients)) {
                ArrayList<ItemStack> stacks = new ArrayList<>();
                for (ItemStack stack : ingredient.getItems()) {
                    Item item = stack.getItem();
                    if (ShapeMap.isShape(item)) {
                        ItemStack originalStack = ShapeMap.getParent(item).getDefaultInstance();
                        originalStack.setCount(stack.getCount());
                        stacks.add(originalStack);
                        changed = true;
                    } else stacks.add(stack);
                }

                Stream<ItemStack> newStacks = stacks.stream();
                if (changed) {
                    try {
                        int index = ingredients.indexOf(ingredient);
                        ingredients.set(index, Ingredient.of(newStacks));
                    } catch (Exception ignored) {}
                }
            }


            //? if >1.21 {
            RecipeHolder<?> newHolder = new RecipeHolder<>(recipeHolder.id(), recipe);
            newRecipes.add(newHolder);
            //?} else {
            /^newRecipes.add(recipe);
            ^///?}
        }

        if (changed) {
            recipeManager.replaceRecipes(newRecipes);
        }
        *///?}
    }

    public static void registerVariants() {
        if (STARTUP_CONFIG.VERTICAL_SLABS.value() || STARTUP_CONFIG.STEPS.value()) {
            //? if neoforge {
            /*((RegistryAccess) BuiltInRegistries.BLOCK).clutternomore$unfreeze();
            ((RegistryAccess) BuiltInRegistries.ITEM).clutternomore$unfreeze();
            *///?} else if forge {
            /*RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.BLOCK.key()).unfreeze();
            RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.ITEM.key()).unfreeze();
            *///?}
            LinkedHashMap<String, Supplier<? extends Block>> toRegister = new LinkedHashMap<>();
            ArrayList<ResourceLocation> slabs = new ArrayList<>();
            ArrayList<ResourceLocation> stairs = new ArrayList<>();
            for (Map.Entry<ResourceKey<Item>, Item> resourceKeyItemEntry : BuiltInRegistries.ITEM.entrySet()) {
                if (resourceKeyItemEntry.getValue().asItem() instanceof BlockItem blockItem) {
                    if (blockItem.getBlock() instanceof SlabBlock slabBlock && STARTUP_CONFIG.VERTICAL_SLABS.value()) {
                        var path = "vertical_" + resourceKeyItemEntry.getKey().location().getPath();
                        toRegister.put(path, ()->new VerticalSlabBlock(copy(slabBlock)
                                //? if >1.21.2
                                .setId(CBlocks.registryKey(path))
                        ));
                        slabs.add(resourceKeyItemEntry.getKey().location());
                    }
                    if (blockItem.getBlock() instanceof StairBlock stairBlock && STARTUP_CONFIG.STEPS.value()) {
                        var path = resourceKeyItemEntry.getKey().location().getPath().replace("stairs", "step");
                        toRegister.put(path, ()->new StepBlock(copy(stairBlock)
                                //? if >1.21.2
                                .setId(CBlocks.registryKey(path))
                        ));
                        stairs.add(resourceKeyItemEntry.getKey().location());
                    }
                }
            }
            toRegister.forEach(CBlocks::register);
            VerticalSlabGenerator.SLABS = slabs;
            StepGenerator.STAIRS = stairs;
            AssetGenerator.keys = toRegister.keySet();
            //? if neoforge {
            /*BuiltInRegistries.BLOCK.freeze();
            BuiltInRegistries.ITEM.freeze();
            *///?} else if forge {
            /*RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.BLOCK.key()).freeze();
            RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.ITEM.key()).freeze();
            *///?}
        }
    }

    private static BlockBehaviour.Properties copy(Block block) {
        //? if >1.21
        return BlockBehaviour.Properties.ofFullCopy(block);
        //? if <1.21
        /*return BlockBehaviour.Properties.copy(block);*/
    }
}