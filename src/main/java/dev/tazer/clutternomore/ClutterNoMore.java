package dev.tazer.clutternomore;

//import dev.tazer.clutternomore.common.data.DynamicServerResources;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.tazer.clutternomore.client.assets.AssetGenerator;
import dev.tazer.clutternomore.client.assets.StepGenerator;
import dev.tazer.clutternomore.client.assets.VerticalSlabGenerator;
import dev.tazer.clutternomore.common.access.RegistryAccess;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import dev.tazer.clutternomore.common.data.CNMPackResources;
//? if <1.21 {
/*import net.minecraft.core.RegistryAccess;
*///?} else {
import dev.tazer.clutternomore.common.registry.BlockSetRegistry;
import dev.tazer.clutternomore.common.registry.CBlocks;
import net.minecraft.core.HolderLookup;
//?}

//? if <1.21.4 {
/*import net.minecraft.core.NonNullList;
import net.minecraft.world.item.crafting.Ingredient;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import java.util.stream.Stream;
*///?}

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
//? if >1.21 {
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
//?}
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
//? if forge {
/*import net.minecraftforge.registries.RegistryManager;
*///?}
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Supplier;

public class ClutterNoMore {
    public static final String MODID = "clutternomore";
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");
    public static final CNMConfig.StartupConfig STARTUP_CONFIG = CNMConfig.StartupConfig.createToml(Platform.INSTANCE.configPath(), MODID, "startup", CNMConfig.StartupConfig.class);
    private static final PackLocationInfo PACK_INFO = new PackLocationInfo(ClutterNoMore.MODID+"-runtime", Component.literal("ClutterNoMore"), PackSource.BUILT_IN, Optional.empty());
    public static final CNMPackResources RESOURCES = new CNMPackResources(PACK_INFO);

    public static void init() {
        LOGGER.info("Initializing {} on {}", MODID, Platform.INSTANCE.loader());
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

            JsonArray verticalSlabsArray = new JsonArray();
            JsonArray stepsArray = new JsonArray();
            JsonArray woodenVerticalSlabsArray = new JsonArray();
            JsonArray woodenStepsArray = new JsonArray();
            JsonArray pickaxeMineableArray = new JsonArray();
            JsonArray shovelMineableArray = new JsonArray();

            List<SoundType> woodenSoundTypes = List.of(
                    SoundType.WOOD,
                    SoundType.BAMBOO_WOOD,
                    SoundType.CHERRY_WOOD,
                    SoundType.NETHER_WOOD
            );
            List<SoundType> shovelSoundTypes = List.of(
                    SoundType.GRAVEL,
                    SoundType.GRASS
            );

            for (Map.Entry<ResourceKey<Item>, Item> resourceKeyItemEntry : BuiltInRegistries.ITEM.entrySet()) {
                if (resourceKeyItemEntry.getValue().asItem() instanceof BlockItem blockItem) {
                    var blockId = resourceKeyItemEntry.getKey().location();
                    var blockNamespace = blockId.getNamespace() + "/";
                    if (blockId.getNamespace().equals("minecraft")) {
                        blockNamespace = "";
                    }
                    if (blockItem.getBlock() instanceof SlabBlock slabBlock && STARTUP_CONFIG.VERTICAL_SLABS.value()) {
                        String shortPath = "vertical_" + blockId.getPath();
                        String path = blockNamespace + shortPath;
                        //? if =1.21.1 {
                        /*if (!blockNamespace.isEmpty()) {
                            BuiltInRegistries.BLOCK.addAlias(ClutterNoMore.location(shortPath), ClutterNoMore.location(path));
                            BuiltInRegistries.ITEM.addAlias(ClutterNoMore.location(shortPath), ClutterNoMore.location(path));
                        }
                        *///?}
                        toRegister.put(path, ()->new VerticalSlabBlock(copy(slabBlock)
                                //? if >1.21.2
                                .setId(CBlocks.registryKey(path))
                        ));

                        slabs.add(blockId);

                        if (woodenSoundTypes.contains(slabBlock.properties().soundType)) {
                            woodenVerticalSlabsArray.add(ClutterNoMore.location(path).toString());
                        } else {
                            verticalSlabsArray.add(ClutterNoMore.location(path).toString());
                            if (shovelSoundTypes.contains(slabBlock.properties().soundType)) shovelMineableArray.add(ClutterNoMore.location(path).toString());
                            else pickaxeMineableArray.add(ClutterNoMore.location(path).toString());
                        }
                    }
                    if (blockItem.getBlock() instanceof StairBlock stairBlock && STARTUP_CONFIG.STEPS.value()) {
                        String shortPath = blockId.getPath().replace("stairs", "step");
                        String path = blockNamespace + shortPath;
                        //? if =1.21.1 {
                        /*if (!blockNamespace.isEmpty()) {
                            BuiltInRegistries.BLOCK.addAlias(ClutterNoMore.location(shortPath), ClutterNoMore.location(path));
                            BuiltInRegistries.ITEM.addAlias(ClutterNoMore.location(shortPath), ClutterNoMore.location(path));
                        }
                        *///?}
                        toRegister.put(path, ()->new StepBlock(copy(stairBlock)
                                //? if >1.21.2
                                .setId(CBlocks.registryKey(path))
                        ));

                        stairs.add(blockId);

                        if (woodenSoundTypes.contains(stairBlock.properties().soundType)) {
                            woodenStepsArray.add(ClutterNoMore.location(path).toString());
                        } else {
                            stepsArray.add(ClutterNoMore.location(path).toString());
                            if (shovelSoundTypes.contains(stairBlock.properties().soundType)) shovelMineableArray.add(ClutterNoMore.location(path).toString());
                            else pickaxeMineableArray.add(ClutterNoMore.location(path).toString());
                        }
                    }
                }
            }
            toRegister.forEach(CBlocks::register);
            VerticalSlabGenerator.SLABS = slabs;
            StepGenerator.STAIRS = stairs;
            AssetGenerator.keys = toRegister.keySet();


            JsonObject verticalSlabTag = new JsonObject();
            verticalSlabTag.add("values", verticalSlabsArray);
            RESOURCES.addJson(PackType.SERVER_DATA, ClutterNoMore.location("tags/block/vertical_slabs.json"), verticalSlabTag);

            JsonObject woodenVerticalSlabTag = new JsonObject();
            woodenVerticalSlabTag.add("values", woodenVerticalSlabsArray);
            RESOURCES.addJson(PackType.SERVER_DATA, ClutterNoMore.location("tags/block/wooden_vertical_slabs.json"), woodenVerticalSlabTag);

            JsonObject stepTag = new JsonObject();
            stepTag.add("values", stepsArray);
            RESOURCES.addJson(PackType.SERVER_DATA, ClutterNoMore.location("tags/block/steps.json"), stepTag);

            JsonObject woodenStepTag = new JsonObject();
            woodenStepTag.add("values", woodenStepsArray);
            RESOURCES.addJson(PackType.SERVER_DATA, ClutterNoMore.location("tags/block/wooden_steps.json"), woodenStepTag);


            JsonObject pickaxeMineableTag = new JsonObject();
            pickaxeMineableTag.add("values", pickaxeMineableArray);
            RESOURCES.addJson(PackType.SERVER_DATA, ClutterNoMore.location("minecraft", "tags/block/mineable/pickaxe.json"), pickaxeMineableTag);

            JsonObject shovelMineableTag = new JsonObject();
            shovelMineableTag.add("values", shovelMineableArray);
            RESOURCES.addJson(PackType.SERVER_DATA, ClutterNoMore.location("minecraft", "tags/block/mineable/shovel.json"), shovelMineableTag);

            //? if neoforge {
            /*BuiltInRegistries.BLOCK.freeze();
            BuiltInRegistries.ITEM.freeze();
            *///?} else if forge {
            /*RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.BLOCK.key()).freeze();
            RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.ITEM.key()).freeze();
            *///?}
        }
    }

    public static BlockBehaviour.Properties copy(Block block) {
        //? if >1.21
        return BlockBehaviour.Properties.ofFullCopy(block);
        //? if <1.21
        /*return BlockBehaviour.Properties.copy(block);*/
    }
}