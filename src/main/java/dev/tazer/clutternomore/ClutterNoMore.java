package dev.tazer.clutternomore;

//import dev.tazer.clutternomore.common.data.DynamicServerResources;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.tazer.clutternomore.client.assets.AssetGenerator;
import dev.tazer.clutternomore.client.assets.StepGenerator;
import dev.tazer.clutternomore.client.assets.VerticalSlabGenerator;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import dev.tazer.clutternomore.common.data.CNMPackResources;
import dev.tazer.clutternomore.common.registry.CBlocks;
import dev.tazer.clutternomore.common.mixin.access.BlockBehaviorAccessor;
//? if <1.21 {
/*import net.minecraft.core.RegistryAccess;
*///?} else {

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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
//? if >1.21 {
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackSelectionConfig;
//?}
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
//? if forge {
/*import net.minecraftforge.registries.RegistryManager;
*///?}
//? if fabric {
//?} else if neoforge {
//?}
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.function.Supplier;

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
    public static final ArrayList<ResourceLocation> ALIASES = new ArrayList<>();

    public static void init() {
        LOGGER.info("Initializing {} on {}", MODID, Platform.INSTANCE.loader());
    }

    public static Pack createPack(PackType type) {
        //? if >1.21 {
        return Pack.readMetaAndCreate(
                ClutterNoMore.PACK_INFO,
                new PackResourcesSupplier(),
                type,
                new PackSelectionConfig(
                        true,
                        Pack.Position.TOP,
                        false
                )
        );
        //?} else {
        /*return Pack.readMetaAndCreate(
                PACK_INFO,
                Component.literal("ClutterNoMore"),
                true,
                new PackResourcesSupplier(),
                type,
                Pack.Position.TOP,
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
        //? if forge {
        /*RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.BLOCK.key()).unfreeze();
        RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.ITEM.key()).unfreeze();
         *///?}
        if (STARTUP_CONFIG.VERTICAL_SLABS.value() || STARTUP_CONFIG.STEPS.value()) {
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
                        /*addAlias(blockNamespace, shortPath, path);
                        *///?}
                        toRegister.put(path, ()->new VerticalSlabBlock(copy(slabBlock)
                                //? if >1.21.2
                                .setId(CBlocks.registryKey(path))
                        ));

                        slabs.add(blockId);

                        ResourceLocation shapeId = ClutterNoMore.location(path);
                        addLootTable(blockId, shapeId);

                        
                        var soundType = ((BlockBehaviorAccessor) slabBlock).getSoundType();

                        if (woodenSoundTypes.contains(soundType)) {
                            woodenVerticalSlabsArray.add(ClutterNoMore.location(path).toString());
                        } else {
                            verticalSlabsArray.add(ClutterNoMore.location(path).toString());
                            if (shovelSoundTypes.contains(soundType)) shovelMineableArray.add(ClutterNoMore.location(path).toString());
                            else pickaxeMineableArray.add(ClutterNoMore.location(path).toString());
                        }


                    }
                    if (blockItem.getBlock() instanceof StairBlock stairBlock && STARTUP_CONFIG.STEPS.value()) {
                        String shortPath = blockId.getPath().replace("stairs", "step");
                        String path = blockNamespace + shortPath;
                        //? if =1.21.1 {
                        /*addAlias(blockNamespace, shortPath, path);
                        *///?}
                        toRegister.put(path, ()->new StepBlock(copy(stairBlock)
                                //? if >1.21.2
                                .setId(CBlocks.registryKey(path))
                        ));

                        stairs.add(blockId);

                        ResourceLocation shapeId = ClutterNoMore.location(path);
                        addLootTable(blockId, shapeId);

                        var soundType = ((BlockBehaviorAccessor) stairBlock).getSoundType();

                        if (woodenSoundTypes.contains(soundType)) {
                            woodenStepsArray.add(shapeId.toString());
                        } else {
                            stepsArray.add(shapeId.toString());
                            if (shovelSoundTypes.contains(soundType)) shovelMineableArray.add(shapeId.toString());
                            else pickaxeMineableArray.add(shapeId.toString());
                        }
                    }
                }
            }
            toRegister.forEach(CBlocks::register);
            AssetGenerator.keys = toRegister.keySet();
            VerticalSlabGenerator.SLABS = slabs;
            StepGenerator.STAIRS = stairs;

            JsonObject verticalSlabTag = new JsonObject();
            verticalSlabTag.add("values", verticalSlabsArray);
            ClutterNoMore.blockAndItemTag("vertical_slabs", verticalSlabTag);

            JsonObject woodenVerticalSlabTag = new JsonObject();
            woodenVerticalSlabTag.add("values", woodenVerticalSlabsArray);
            ClutterNoMore.blockAndItemTag("wooden_vertical_slabs", woodenVerticalSlabTag);

            JsonObject stepTag = new JsonObject();
            stepTag.add("values", stepsArray);
            ClutterNoMore.blockAndItemTag("steps", stepTag);

            JsonObject woodenStepTag = new JsonObject();
            woodenStepTag.add("values", woodenStepsArray);
            ClutterNoMore.blockAndItemTag("wooden_steps", woodenStepTag);


            JsonObject pickaxeMineableTag = new JsonObject();
            pickaxeMineableTag.add("values", pickaxeMineableArray);
            ClutterNoMore.blockAndItemTag("minecraft","mineable/pickaxe", pickaxeMineableTag);

            JsonObject shovelMineableTag = new JsonObject();
            shovelMineableTag.add("values", shovelMineableArray);
            ClutterNoMore.blockAndItemTag("minecraft","mineable/shovel", shovelMineableTag);
        }
    }

    private static void blockTag(String s, JsonElement verticalSlabTag) {
        blockTag(ClutterNoMore.MODID, s, verticalSlabTag);
    }

    private static void itemTag(String namespace, String s, JsonElement verticalSlabTag) {
        //? if >1.21 {
        var location = ClutterNoMore.location(namespace, "tags/item/" +s + ".json");
        //?} else {
        /*var location = ClutterNoMore.location(namespace, "tags/items/" +s + ".json");
        *///?}
        RESOURCES.addJson(PackType.SERVER_DATA, location, verticalSlabTag);
    }

    private static void blockTag(String namespace, String s, JsonElement verticalSlabTag) {
        //? if >1.21 {
        var location = ClutterNoMore.location(namespace, "tags/block/" +s + ".json");
         //?} else {
        /*var location = ClutterNoMore.location(namespace, "tags/blocks/" +s + ".json");
        *///?}
        RESOURCES.addJson(PackType.SERVER_DATA, location, verticalSlabTag);
    }

    private static void itemTag(String path, JsonElement verticalSlabTag) {
        itemTag(ClutterNoMore.MODID, path, verticalSlabTag);
    }

    private static void blockAndItemTag(String path, JsonElement verticalSlabTag) {
        blockTag(path, verticalSlabTag);
        itemTag(path, verticalSlabTag);
    }

    private static void blockAndItemTag(String namespace, String path, JsonElement verticalSlabTag) {
        blockTag(namespace, path, verticalSlabTag);
        itemTag(namespace, path, verticalSlabTag);
    }

    //? if =1.21.1 {
    /*private static void addAlias(String blockNamespace, String shortPath, String path) {
        ResourceLocation shortNamespace = ClutterNoMore.location(shortPath);
        if (!blockNamespace.isEmpty() && !ALIASES.contains(shortNamespace)) {
            ResourceLocation id = ClutterNoMore.location(path);
            BuiltInRegistries.BLOCK.addAlias(shortNamespace, id);
            BuiltInRegistries.ITEM.addAlias(shortNamespace, id);
            ALIASES.add(shortNamespace);
        }
    }
    *///?}

    public static void addLootTable(ResourceLocation block, ResourceLocation shape) {
        JsonObject lootTable = new JsonObject();
        lootTable.add("type", new JsonPrimitive("block"));
        JsonArray pools = new JsonArray();
        JsonObject pool = new JsonObject();
        pool.add("rolls", new JsonPrimitive(1));
        JsonArray entries = new JsonArray();
        JsonObject entry = new JsonObject();
        entry.add("type", new JsonPrimitive("loot_table"));
        entry.add("value", new JsonPrimitive(block.withPrefix("blocks/").toString()));
        entries.add(entry);
        pool.add("entries", entries);
        pools.add(pool);
        lootTable.add("pools", pools);
        //? if >1.21 {
        var path = "loot_table";
        //?} else {
        /*var path = "loot_tables";
        *///?}
        RESOURCES.addJson(PackType.SERVER_DATA, ClutterNoMore.location("%s/blocks/%s.json".formatted(path, shape.getPath())), lootTable);
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