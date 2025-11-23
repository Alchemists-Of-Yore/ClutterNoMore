package dev.tazer.clutternomore;

import dev.tazer.clutternomore.client.assets.AssetGenerator;
import dev.tazer.clutternomore.client.assets.StepGenerator;
import dev.tazer.clutternomore.client.assets.VerticalSlabGenerator;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import dev.tazer.clutternomore.common.blocks.WeatheringStepBlock;
import dev.tazer.clutternomore.common.blocks.WeatheringVerticalSlabBlock;
import dev.tazer.clutternomore.common.data.CNMPackResources;
import dev.tazer.clutternomore.common.data.DataGenerator;
import dev.tazer.clutternomore.common.registry.CBlocks;
import dev.tazer.clutternomore.common.mixin.access.BlockBehaviorAccessor;
//? if <1.21 {
/*import net.minecraft.core.RegistryAccess;
*///?} else {
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
//? if =1.21.1 {
/*import net.minecraft.world.item.crafting.RecipeHolder;
*///?}
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
//? if forge {
/*import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;
*///?} else if fabric {
import dev.tazer.clutternomore.fabric.FabricEntrypoint;
//?}
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

import static dev.tazer.clutternomore.common.data.DataGenerator.*;

public class ClutterNoMore {
    public static final String MODID = "clutternomore";
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");
    public static final CNMConfig.StartupConfig STARTUP_CONFIG = CNMConfig.StartupConfig.createToml(Platform.INSTANCE.configPath(), MODID, "startup", CNMConfig.StartupConfig.class);
    //? if >1.21 {
    private static final PackLocationInfo PACK_INFO = new PackLocationInfo(ClutterNoMore.MODID+"-runtime", Component.literal("ClutterNoMore"), PackSource.BUILT_IN, Optional.empty());
    //?} else {
    /*private static final String PACK_INFO = ClutterNoMore.MODID+"-runtime";
    *///?}
    public static final CNMPackResources RESOURCES = new CNMPackResources(PACK_INFO);
    public static LinkedHashMap<ResourceLocation, ResourceLocation> COPPER_BLOCKS = new LinkedHashMap<>();
    public static ArrayList<ResourceLocation> WAXED_COPPER_BLOCKS = new ArrayList<>();

    public static void init() {
        LOGGER.info("Initializing {} on {}", MODID, Platform.INSTANCE.loader());
    }

    public static Pack createPack(PackType type) {
        //? if >1.21 {
        return Pack.readMetaAndCreate(
                PACK_INFO,
                new PackResourcesSupplier(),
                type,
                new PackSelectionConfig(
                        true,
                        Pack.Position.BOTTOM,
                        true
                )
        );
        //?} else {
        /*return Pack.readMetaAndCreate(
                PACK_INFO,
                Component.literal("ClutterNoMore"),
                true,
                new PackResourcesSupplier(),
                type,
                Pack.Position.BOTTOM,
                PackSource.BUILT_IN
        );
        *///?}

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

    public static void modifyRecipes(
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
        //? if forge {
        /*RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.BLOCK.key()).unfreeze();
        RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.ITEM.key()).unfreeze();
         *///?}
        if (STARTUP_CONFIG.VERTICAL_SLABS.value() || STARTUP_CONFIG.STEPS.value()) {
            LinkedHashMap<String, Supplier<? extends Block>> toRegister = new LinkedHashMap<>();
            ArrayList<ResourceLocation> slabs = new ArrayList<>();
            ArrayList<ResourceLocation> stairs = new ArrayList<>();

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
                    if (blockItem.getBlock() instanceof SlabBlock slabBlock && slabBlock.defaultBlockState().getValues().size() == 2 && STARTUP_CONFIG.VERTICAL_SLABS.value()) {
                        String shortPath = "vertical_" + blockId.getPath();
                        String path = blockNamespace + shortPath;
                        //? if =1.21.1 {
                        /*addAlias(blockNamespace, shortPath, path);
                        *///?}

                        if (slabBlock instanceof WeatheringCopperSlabBlock weatheringSlabBlock) {
                            Supplier<Block> block = ()->new WeatheringVerticalSlabBlock(copy(slabBlock)
                                    //? if >1.21.2
                                    .setId(CBlocks.registryKey(path))
                                    , weatheringSlabBlock.getAge()
                            );
                            toRegister.put(path, block);
                            matchCopperBlock(ClutterNoMore.location(path));
                        } else {
                            toRegister.put(path, ()->new VerticalSlabBlock(copy(slabBlock)
                                    //? if >1.21.2
                                    .setId(CBlocks.registryKey(path))
                            ));
                            if (path.contains("waxed")) {
                                WAXED_COPPER_BLOCKS.add(ClutterNoMore.location(path));
                            }
                        }

                        slabs.add(blockId);

                        ResourceLocation shapeId = ClutterNoMore.location(path);
                        DataGenerator.addLootTable(blockId, shapeId);

                        
                        var soundType = ((BlockBehaviorAccessor) slabBlock).getSoundType();

                        if (woodenSoundTypes.contains(soundType)) {
                            woodenVerticalSlabsArray.add(ClutterNoMore.location(path).toString());
                        } else {
                            DataGenerator.addToTag(path, verticalSlabsArray);
                            if (shovelSoundTypes.contains(soundType)) DataGenerator.addToTag(path, shovelMineableArray);
                            else DataGenerator.addToTag(path, pickaxeMineableArray);
                        }
                    }

                    if (blockItem.getBlock() instanceof StairBlock stairBlock && stairBlock.defaultBlockState().getValues().size() == 4 && STARTUP_CONFIG.STEPS.value()) {
                        String shortPath = blockId.getPath().replace("stairs", "step");
                        String path = blockNamespace + shortPath;
                        //? if =1.21.1 {
                        /*addAlias(blockNamespace, shortPath, path);
                        *///?}
                        if (stairBlock instanceof WeatheringCopperStairBlock weatheringCopperStairBlock) {
                            Supplier<Block> block = ()->new WeatheringStepBlock(copy(stairBlock)
                                    //? if >1.21.2
                                    .setId(CBlocks.registryKey(path))
                                    , weatheringCopperStairBlock.getAge()
                            );
                            toRegister.put(path, block);
                            matchCopperBlock(ClutterNoMore.location(path));
                        } else {
                            toRegister.put(path, ()->new StepBlock(copy(stairBlock)
                                    //? if >1.21.2
                                    .setId(CBlocks.registryKey(path))
                            ));
                            if (path.contains("waxed")) {
                                WAXED_COPPER_BLOCKS.add(ClutterNoMore.location(path));
                            }
                        }


                        stairs.add(blockId);

                        ResourceLocation shapeId = ClutterNoMore.location(path);
                        DataGenerator.addLootTable(blockId, shapeId);

                        var soundType = ((BlockBehaviorAccessor) stairBlock).getSoundType();

                        if (woodenSoundTypes.contains(soundType)) {
                            DataGenerator.addToTag(shapeId, woodenStepsArray);
                        } else {
                            DataGenerator.addToTag(shapeId, stepsArray);
                            if (shovelSoundTypes.contains(soundType)) DataGenerator.addToTag(path, shovelMineableArray);
                            else DataGenerator.addToTag(path, pickaxeMineableArray);
                        }
                    }
                }
            }
            toRegister.forEach(CBlocks::register);
            AssetGenerator.keys = toRegister.keySet();
            VerticalSlabGenerator.SLABS = slabs;
            StepGenerator.STAIRS = stairs;
            DataGenerator.generate();
            Platform.INSTANCE.finalizeCopperBlockRegistration();
        }
    }

    private static void matchCopperBlock(ResourceLocation id) {
        if (id.getPath().contains("oxidized")) {
            var weatheredPath = ClutterNoMore.location(id.getNamespace(), id.getPath().replace("oxidized", "weathered"));
            COPPER_BLOCKS.put(weatheredPath, id);
        }
        if (id.getPath().contains("weathered")) {
            var exposed = ClutterNoMore.location(id.getNamespace(), id.getPath().replace("weathered", "exposed"));
            COPPER_BLOCKS.put(exposed, id);
        }
        if (id.getPath().contains("exposed")) {
            var unaffected = ClutterNoMore.location(id.getNamespace(), id.getPath().replace("exposed_", ""));
            COPPER_BLOCKS.put(unaffected, id);
        }
    }

    //? if =1.21.1 {
    /*public static final ArrayList<ResourceLocation> ALIASES = new ArrayList<>();
    private static void addAlias(String blockNamespace, String shortPath, String path) {
        ResourceLocation shortNamespace = ClutterNoMore.location(shortPath);
        if (!blockNamespace.isEmpty() && !ALIASES.contains(shortNamespace)) {
            ResourceLocation id = ClutterNoMore.location(path);
            BuiltInRegistries.BLOCK.addAlias(shortNamespace, id);
            BuiltInRegistries.ITEM.addAlias(shortNamespace, id);
            ALIASES.add(shortNamespace);
        }
    }
    *///?}

    public static final Path pack = Platform.INSTANCE.getResourcePack().resolve("clutternomore");

    public static void writeFile(Path path, Path filePath, String contents) {
        try {
            path.toFile().mkdirs();
            FileWriter langWriter = new FileWriter(filePath.toFile());
            langWriter.write(contents);
            langWriter.close();  // must close manually
            ClutterNoMore.LOGGER.debug("Successfully wrote to {}", filePath);
        } catch (IOException e) {
            ClutterNoMore.LOGGER.error("Failed to write dynamic data. %s".formatted(e));
        }
    }

    public static BlockBehaviour.Properties copy(Block block) {
        //? if >1.21
        return BlockBehaviour.Properties.ofFullCopy(block);
        //? if <1.21
        /*return BlockBehaviour.Properties.copy(block);*/
    }

    private static class PackResourcesSupplier implements Pack.ResourcesSupplier {
        //? if >1.21 {
        @Override
        public PackResources openPrimary(PackLocationInfo location) {
            return RESOURCES;
        }

        @Override
        public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
            return RESOURCES;
        }
        //?} else {
        /*@Override
        public PackResources open(String s) {
            return RESOURCES;
        }
        *///?}
    }
}