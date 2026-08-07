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
//? if >=1.21 {
import net.minecraft.server.packs.PackLocationInfo;
//?}

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
//? if forge {
/*import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryManager;
*///?} else if fabric {
import dev.tazer.clutternomore.fabric.FabricEntrypoint;
//?}
import net.minecraft.world.level.block.state.BlockState;
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
    public static LinkedHashMap<Identifier, Identifier> COPPER_BLOCKS = new LinkedHashMap<>();
    public static ArrayList<Identifier> WAXED_COPPER_BLOCKS = new ArrayList<>();

    public static void init() {
        LOGGER.info("Initializing {} on {}", MODID, Platform.INSTANCE.loader());
    }

    public static Identifier location(String path) {
        return location(MODID, path);
    }

    public static Identifier location(String namespace, String path) {
        //? if >1.21
        return Identifier.fromNamespaceAndPath(namespace, path);
        //? if <1.21
        //return new Identifier(namespace, path);
    }


    public static String shapeNamespace(Identifier parent) {
        return parent.getNamespace().equals("minecraft") ? "" : parent.getNamespace() + "/";
    }

    public static Identifier parse(String id) {
        //? if >1.21
        return Identifier.parse(id);
        //? if <1.21
        //return new Identifier(id);
    }

    public static void registerVariants() {
        //? if forge {
        /*RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.BLOCK.key()).unfreeze();
        RegistryManager.ACTIVE.getRegistry(BuiltInRegistries.ITEM.key()).unfreeze();
         *///?}
        if (STARTUP_CONFIG.VERTICAL_SLABS.value() || STARTUP_CONFIG.STEPS.value()) {
            LinkedHashMap<String, Supplier<? extends Block>> toRegister = new LinkedHashMap<>();
            ArrayList<Identifier> slabs = new ArrayList<>();
            ArrayList<Identifier> stairs = new ArrayList<>();

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
                    Identifier blockId = resourceKeyItemEntry.getKey().identifier();
                    String blockNamespace = shapeNamespace(blockId);
                    if (blockItem.getBlock() instanceof SlabBlock slabBlock && size(slabBlock.defaultBlockState()) == 2 && STARTUP_CONFIG.VERTICAL_SLABS.value()) {
                        String shortPath = "vertical_" + blockId.getPath();
                        String path = blockNamespace + shortPath;

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

                        SoundType soundType = ((BlockBehaviorAccessor) slabBlock).getSoundType();
                        if (woodenSoundTypes.contains(soundType)) {
                            DataGenerator.addToTag(path, woodenVerticalSlabsArray);
                        } else {
                            DataGenerator.addToTag(path, verticalSlabsArray);
                            if (shovelSoundTypes.contains(soundType)) DataGenerator.addToTag(path, shovelMineableArray);
                            else DataGenerator.addToTag(path, pickaxeMineableArray);
                        }
                    }

                    if (blockItem.getBlock() instanceof StairBlock stairBlock && size(stairBlock.defaultBlockState()) == 4 && STARTUP_CONFIG.STEPS.value()) {
                        String shortPath = blockId.getPath().replace("stairs", "step");
                        String path = blockNamespace + shortPath;
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

                        SoundType soundType = ((BlockBehaviorAccessor) stairBlock).getSoundType();
                        if (woodenSoundTypes.contains(soundType)) {
                            DataGenerator.addToTag(path, woodenStepsArray);
                        } else {
                            DataGenerator.addToTag(path, stepsArray);
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

	private static int size(BlockState blockState) {
		return blockState.getValues()
                //? if >26
                .toList()
                .size();
	}

    private static void matchCopperBlock(Identifier id) {
        if (id.getPath().contains("oxidized")) {
            Identifier weatheredPath = ClutterNoMore.location(id.getNamespace(), id.getPath().replace("oxidized", "weathered"));
            COPPER_BLOCKS.put(weatheredPath, id);
        }
        if (id.getPath().contains("weathered")) {
            Identifier exposed = ClutterNoMore.location(id.getNamespace(), id.getPath().replace("weathered", "exposed"));
            COPPER_BLOCKS.put(exposed, id);
        }
        if (id.getPath().contains("exposed")) {
            Identifier unaffected = ClutterNoMore.location(id.getNamespace(), id.getPath().replace("exposed_", ""));
            COPPER_BLOCKS.put(unaffected, id);
        }
    }

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
        //return BlockBehaviour.Properties.copy(block);
    }

}