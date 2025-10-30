package dev.tazer.clutternomore.common.registry.moonlight;

//? forge || neoforge {
/*import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.blocks.StepBlock;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import net.mehvahdjukaar.moonlight.api.misc.Registrator;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.BlockType;
import net.mehvahdjukaar.moonlight.api.set.BlockTypeRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BlockSetRegistry {

    public static void init() {
        BlockSetAPI.registerBlockSetDefinition(new ShapeSetRegistry());
        BlockSetAPI.addDynamicBlockRegistration(BlockSetRegistry::registerShapeBlocks, ShapeSet.class);
        BlockSetAPI.addDynamicItemRegistration(BlockSetRegistry::registerShapeItems, ShapeSet.class);
    }

    public static ShapeSet get(Item item) {
        return BlockSetAPI.getBlockTypeOf(item, BlockSetRegistry.ShapeSet.class);
    }
    public static ShapeSet getBlockTypeOf(Item item, Class<ShapeSet> shapeSetClass) {
        return BlockSetAPI.getBlockTypeOf(item, BlockSetRegistry.ShapeSet.class);
    }

    public static class ShapeSetRegistry extends BlockTypeRegistry<ShapeSet> {
        protected ShapeSetRegistry() {
            super(ShapeSet.class, "shape_set");
        }

        @Override
        public Optional<ShapeSet> detectTypeFromBlock(Block block, ResourceLocation blockId) {
            if (block.asItem() != Items.AIR) {
                if (isParentBlock(blockId)) {
                    return Optional.of(new ShapeSet(blockId, block));
                }
            }

            return Optional.empty();
        }

        private static boolean has(ResourceLocation block) {
            return BuiltInRegistries.BLOCK.containsKey(block);
        }

        private static boolean isParentBlock(ResourceLocation block) {
            List<String> namespaces = List.of(block.getNamespace(), "minecraft");
            String path = block.getPath();

            List<String> parentSuffixes = List.of("log", "planks", "block");
            List<String> ignoredSuffixes = List.of("block");
            Map<String, String> replacements = Map.of("log", "wood");
            List<String> prefixes = List.of("spiked");
            List<String> ignoredPrefixes = List.of("stripped");
            List<String> suffixes = List.of("stairs", "slab", "wall");

            for (String namespace : namespaces) {
                ResourceLocation base = ClutterNoMore.location(namespace, path);

                String suffixBase = path;
                for (String ignored : ignoredSuffixes) {
                    suffixBase = stripSuffix(suffixBase, ignored);
                }

                for (String parent : parentSuffixes) {
                    String suffixed = suffixBase + "_" + parent;
                    if (!suffixed.equals(path)) {
                        ResourceLocation candidate = base.withPath(p -> suffixed);
                        if (has(candidate)) return false;
                    }
                }

                for (Map.Entry<String, String> replacement : replacements.entrySet()) {
                    String replaced = path.replace(replacement.getKey(), replacement.getValue());
                    if (!replaced.equals(path)) {
                        ResourceLocation candidate = base.withPath(p -> replaced);
                        if (has(candidate)) return true;
                    }
                }

                String prefixBase = path;
                for (String ignored : ignoredPrefixes) {
                    prefixBase = stripPrefix(prefixBase, ignored);
                }

                for (String prefix : prefixes) {
                    String prefixed = prefix + "_" + prefixBase;
                    if (!prefixed.equals(path)) {
                        ResourceLocation candidate = base.withPath(p -> prefixed);
                        if (has(candidate)) return true;
                    }
                }

                for (String parent : parentSuffixes) {
                    suffixBase = stripSuffix(suffixBase, parent);
                }

                for (String suffix : suffixes) {
                    if (suffixBase.endsWith("s")) {
                        String trimmed  = suffixBase.substring(0, suffixBase.length() - 1);
                        String suffixed = trimmed + "_" + suffix;
                        if (!suffixed.equals(path)) {
                            ResourceLocation candidate = base.withPath(p -> suffixed);
                            if (has(candidate)) return true;
                        }
                    }

                    String suffixed = suffixBase + "_" + suffix;
                    if (!suffixed.equals(path)) {
                        ResourceLocation candidate = base.withPath(p -> suffixed);
                        if (has(candidate)) return true;
                    }
                }
            }

            return false;
        }

        @Override
        public ShapeSet getDefaultType() {
            return STONE;
        }

        public static final ShapeSet STONE = new ShapeSet(ClutterNoMore.location("minecraft", "stone"), Blocks.STONE);
    }

    public static class ShapeSet extends BlockType {
        private final ResourceLocation id;
        private final Block block;

        ShapeSet(ResourceLocation id, Block block) {
            super(id);
            this.id = id;
            this.block = block;
        }

        @Override
        protected void initializeChildrenBlocks() {
            addChild("block", block);
        }

        public String getVariantId(String prefix, String postfix) {
            String name = prefix + (prefix.isEmpty() ? "" : "_") + getTypeName();

            List<String> suffixes = List.of("_block", "_planks", "s");

            for (String suffix : suffixes) {
                if (name.endsWith(suffix)) {
                    name = name.substring(0, name.length() - suffix.length());
                    break;
                }
            }

            return name + (postfix.isEmpty() ? "" : "_") + postfix;
        }


        protected @Nullable Item findRelatedEntry(String prefix, String postfix) {
            String basePath = id.getPath();

            List<String> parentSuffixes = List.of("block", "planks");
            for (String parent : parentSuffixes) {
                basePath = stripSuffix(basePath, parent);
            }

            List<String> ignoredPrefixes = List.of("stripped");
            String reapplyPrefix = "";
            for (String ignored : ignoredPrefixes) {
                if (id.getPath().startsWith(ignored + "_")) {
                    reapplyPrefix = ignored + "_";
                    basePath = stripPrefix(basePath, ignored);
                }
            }

            String prefixPart = prefix.isEmpty() ? "" : prefix + "_";
            String postfixPart = postfix.isEmpty() ? "" : "_" + postfix;

            List<String> candidates = new ArrayList<>();
            if (basePath.endsWith("s")) {
                candidates.add(basePath.substring(0, basePath.length() - 1));
            }
            candidates.add(basePath);

            for (String stem : candidates) {
                String candidatePath = reapplyPrefix + prefixPart + stem + postfixPart;
                ResourceLocation candidateId = id.withPath(p -> candidatePath);
                Optional<Item> found = BuiltInRegistries.ITEM.getOptional(candidateId);
                if (found.isPresent()) return found.get();
            }

            return null;
        }

        protected @Nullable Item findRelatedEntry(String postfix) {
            return findRelatedEntry("", postfix);
        }

        protected @Nullable Item getWood() {
            String path = id.getPath();
            if (path.endsWith("log")) {
                String stem = path.substring(0, path.length() - 3);
                ResourceLocation woodId = id.withPath(p -> stem + "wood");
                return BuiltInRegistries.ITEM.getOptional(woodId).orElse(null);
            }

            return null;
        }

        @Override
        protected void initializeChildrenItems() {
            List<String> postfixes = List.of("stairs", "slab", "wall");

            for (String postfix : postfixes) {
                Item found = findRelatedEntry(postfix);
                if (found != null) addChild(postfix, found);
            }

            Item spiked = findRelatedEntry("spiked", "");
            if (spiked != null) addChild("spiked", spiked);

            Item wood = getWood();
            if (wood != null) addChild("wood", wood);
        }


        @Override
        public String getTranslationKey() {
            return "shape_set." + this.getNamespace() + "." + this.getTypeName();
        }

        @Override
        public Block mainChild() {
            return block;
        }

        //? if <1.21 {
        /^public boolean hasChild(String id) {
            return getChild(id) != null;
        }
        ^///?}
    }

    private static void registerShapeBlocks(Registrator<Block> event, Collection<ShapeSet> shapeSets) {
        for (ShapeSet set : shapeSets) {
            if (ClutterNoMore.STARTUP_CONFIG.VERTICAL_SLABS.value() && set.hasChild("slab")) {
                ResourceLocation id = ClutterNoMore.location(set.getVariantId("vertical", "slab"));
                if (!BuiltInRegistries.BLOCK.containsKey(id)) {
                    Block slab = Block.byItem((Item) set.getChild("slab"));

                    if (slab.defaultBlockState().getValues().size() == 2) {
                        Block block = new VerticalSlabBlock(ClutterNoMore.copy(Block.byItem((Item) set.getChild("slab"))));
                        event.register(id, block);
                        set.addChild("vertical_slab_block", block);
                    }
                }
            }

            if (ClutterNoMore.STARTUP_CONFIG.STEPS.value() && set.hasChild("stairs")) {
                ResourceLocation id = ClutterNoMore.location(set.getVariantId("", "step"));
                if (!BuiltInRegistries.BLOCK.containsKey(id)) {
                    Block stairs = Block.byItem((Item) set.getChild("stairs"));

                    if (stairs.defaultBlockState().getValues().size() == 4) {
                        Block block = new StepBlock(ClutterNoMore.copy(stairs));
                        event.register(id, block);
                        set.addChild("step_block", block);
                    }
                }
            }
        }
    }

    private static void registerShapeItems(Registrator<Item> event, Collection<ShapeSet> shapeSets) {
        for (ShapeSet type : shapeSets) {
            Block block;

            block = (Block) type.getChild("vertical_slab_block");
            if (block != null) {
                ResourceLocation id = ClutterNoMore.location(type.getVariantId("vertical", "slab"));
                if (!BuiltInRegistries.ITEM.containsKey(id)) {
                    Item item = new BlockItem(block, new Item.Properties());
                    event.register(id, item);
                    type.addChild("vertical_slab", item);
                }
            }

            block = (Block) type.getChild("step_block");
            if (block != null) {
                ResourceLocation id = ClutterNoMore.location(type.getVariantId("", "step"));
                if (!BuiltInRegistries.ITEM.containsKey(id)) {
                    Item item = new BlockItem(block, new Item.Properties());
                    event.register(id, item);
                    type.addChild("step", item);
                }
            }
        }
    }

    private static String stripSuffix(String path, String suffix) {
        if (path.endsWith("_" + suffix)) return path.substring(0, path.length() - suffix.length() - 1);
        if (path.endsWith(suffix)) return path.substring(0, path.length() - suffix.length());
        return path;
    }

    private static String stripPrefix(String path, String prefix) {
        if (path.startsWith(prefix + "_")) return path.substring(prefix.length() + 1);
        if (path.startsWith(prefix)) return path.substring(prefix.length());
        return path;
    }
}
*///?}