package dev.tazer.clutternomore;

//import dev.tazer.clutternomore.common.data.DynamicServerResources;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.tazer.clutternomore.client.assets.vanilla.AssetGenerator;
import dev.tazer.clutternomore.client.assets.vanilla.StepGenerator;
import dev.tazer.clutternomore.client.assets.vanilla.VerticalSlabGenerator;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import dev.tazer.clutternomore.common.data.vanilla.CNMPackResources;
import dev.tazer.clutternomore.common.registry.CBlocks;
//? if neoforge {
/*import dev.tazer.clutternomore.common.data.DynamicServerResources;
*///?}
//? if neoforge {
/*import dev.tazer.clutternomore.common.registry.moonlight.BlockSetRegistry;
*///?} else {
import dev.tazer.clutternomore.common.registry.vanilla.BlockSetRegistry;
//?}

//? if <1.21 {
/*import net.minecraft.core.RegistryAccess;
*///?} else {
import net.minecraft.core.HolderLookup;
//?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
//? if >1.21
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

public class ClutterNoMore {
    public static final String MODID = "clutternomore";
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");
    public static final CNMConfig.StartupConfig STARTUP_CONFIG = CNMConfig.StartupConfig.createToml(Platform.INSTANCE.configPath(), "", MODID+"-startup", CNMConfig.StartupConfig.class);
    private static final PackLocationInfo PACK_INFO = new PackLocationInfo(ClutterNoMore.MODID+"-runtime", Component.literal("ClutterNoMore"), PackSource.BUILT_IN, Optional.empty());
    private static final CNMPackResources RESOURCES = new CNMPackResources(PACK_INFO);

    public static void init() {
        LOGGER.info("Initializing {} on {}", MODID, Platform.INSTANCE.loader());
        BlockSetRegistry.init();
        //? neoforge {
        /*DynamicServerResources.register();
        *///?}

        JsonArray array = new JsonArray();
        array.add("minecraft:iron_block");
        JsonObject tag = new JsonObject();
        tag.add("values", array);
        RESOURCES.addJson(PackType.SERVER_DATA, ClutterNoMore.location("tags/block/stupid.json"), tag);
    }

    public static Pack createPack(PackType type) {
        return Pack.readMetaAndCreate(
                ClutterNoMore.PACK_INFO,
                new Pack.ResourcesSupplier() {
                    @Override
                    public PackResources openPrimary(PackLocationInfo location) {
                        return RESOURCES;
                    }

                    @Override
                    public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
                        return RESOURCES;
                    }
                },
                type,
                new PackSelectionConfig(
                        true,
                        Pack.Position.TOP,
                        false
                )
        );
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

    //? if forge || fabric {
    public static void registerVariants() {
        if (STARTUP_CONFIG.VERTICAL_SLABS.value() || STARTUP_CONFIG.STEPS.value()) {
            //? if neoforge {
            /*((dev.tazer.clutternomore.common.access.RegistryAccess) BuiltInRegistries.BLOCK).clutternomore$unfreeze();
            ((dev.tazer.clutternomore.common.access.RegistryAccess) BuiltInRegistries.ITEM).clutternomore$unfreeze();
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
    //?}

    public static BlockBehaviour.Properties copy(Block block) {
        //? if >1.21
        return BlockBehaviour.Properties.ofFullCopy(block);
        //? if <1.21
        /*return BlockBehaviour.Properties.copy(block);*/
    }
}