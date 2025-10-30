package dev.tazer.clutternomore.common.data;

//? if neoforge {

/*import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.registry.moonlight.BlockSetRegistry;
import net.mehvahdjukaar.moonlight.api.resources.SimpleTagBuilder;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.mehvahdjukaar.moonlight.api.set.wood.WoodType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class TagsGenerator implements DataGenerator {
    public SimpleTagBuilder verticalSlabItems;
    public SimpleTagBuilder stepItems;
    public SimpleTagBuilder verticalSlabBlocks;
    public SimpleTagBuilder stepBlocks;
    public SimpleTagBuilder woodenVerticalSlabItems;
    public SimpleTagBuilder woodenStepItems;
    public SimpleTagBuilder woodenVerticalSlabBlocks;
    public SimpleTagBuilder woodenStepBlocks;
    public SimpleTagBuilder pickaxeMineable;

    @Override
    public void initialize(ResourceManager manager, ResourceSink sink) {
        verticalSlabItems = SimpleTagBuilder.of(ClutterNoMore.location("vertical_slabs"));
        stepItems = SimpleTagBuilder.of(ClutterNoMore.location("steps"));
        verticalSlabBlocks = SimpleTagBuilder.of(ClutterNoMore.location("vertical_slabs"));
        stepBlocks = SimpleTagBuilder.of(ClutterNoMore.location("steps"));
        woodenVerticalSlabItems = SimpleTagBuilder.of(ClutterNoMore.location(("wooden_vertical_slabs")));
        woodenStepItems = SimpleTagBuilder.of(ClutterNoMore.location("wooden_steps"));
        woodenVerticalSlabBlocks = SimpleTagBuilder.of(ClutterNoMore.location(("wooden_vertical_slabs")));
        woodenStepBlocks = SimpleTagBuilder.of(ClutterNoMore.location("wooden_steps"));
        pickaxeMineable = SimpleTagBuilder.of(ClutterNoMore.location("mineable/pickaxe"));
    }

    @Override
    public void generate(Item item, ResourceManager manager, ResourceSink sink) {
        BlockSetRegistry.ShapeSet set = BlockSetAPI.getBlockTypeOf(item, BlockSetRegistry.ShapeSet.class);
        if (set == null || item != set.mainChild().asItem()) return;
        String id = BuiltInRegistries.ITEM.getKey(item).getPath();
        boolean wood = id.contains("wood") || id.contains("planks");

        if (!wood) {
            WoodType woodType = BlockSetAPI.getBlockTypeOf(item, WoodType.class);
            if (woodType != null) wood = true;
        }

        if (set.hasChild("vertical_slab")) {
            Item verticalSlab = (Item) set.getChild("vertical_slab");
            verticalSlabItems.addEntry(verticalSlab);


            if (wood) woodenVerticalSlabItems.addEntry(verticalSlab);
            else pickaxeMineable.addEntry(verticalSlab);
        }

        if (set.hasChild("step")) {
            Item step = (Item) set.getChild("step");
            stepItems.addEntry(step);

            if (wood) woodenStepItems.addEntry(step);
            else pickaxeMineable.addEntry(step);
        }
    }

    @Override
    public void generate(Block block, ResourceManager manager, ResourceSink sink) {
        BlockSetRegistry.ShapeSet set = null;
        if (block.asItem() != Items.AIR) set = BlockSetAPI.getBlockTypeOf(block, BlockSetRegistry.ShapeSet.class);
        if (set == null || block != set.mainChild()) return;
        String id = BuiltInRegistries.BLOCK.getKey(block).getPath();
        boolean wood = id.contains("wood") || id.contains("planks");

        if (!wood) {
            WoodType woodType = BlockSetAPI.getBlockTypeOf(block, WoodType.class);
            if (woodType != null) wood = true;
        }

        if (set.hasChild("vertical_slab_block")) {
            Block verticalSlab = (Block) set.getChild("vertical_slab_block");
            verticalSlabBlocks.addEntry(verticalSlab);

            if (wood) woodenVerticalSlabBlocks.addEntry(verticalSlab);
            else pickaxeMineable.addEntry(verticalSlab);
        }

        if (set.hasChild("step_block")) {
            Block step = (Block) set.getChild("step_block");
            stepBlocks.addEntry(step);

            if (wood) woodenStepBlocks.addEntry(step);
            else pickaxeMineable.addEntry(step);
        }
    }

    @Override
    public void finish(ResourceManager manager, ResourceSink sink) {
        sink.addTag(verticalSlabItems, Registries.ITEM);
        sink.addTag(verticalSlabBlocks, Registries.BLOCK);

        sink.addTag(woodenVerticalSlabItems, Registries.ITEM);
        sink.addTag(woodenVerticalSlabBlocks, Registries.BLOCK);

        SimpleTagBuilder builder = SimpleTagBuilder.of(BlockTags.SLABS);
        builder.addTag(verticalSlabBlocks);
        sink.addTag(builder, Registries.BLOCK);
        builder = SimpleTagBuilder.of(ItemTags.SLABS);
        builder.addTag(verticalSlabItems);
        sink.addTag(builder, Registries.ITEM);

        builder = SimpleTagBuilder.of(BlockTags.WOODEN_SLABS);
        builder.addTag(woodenVerticalSlabBlocks);
        sink.addTag(builder, Registries.BLOCK);
        builder = SimpleTagBuilder.of(ItemTags.WOODEN_SLABS);
        builder.addTag(woodenVerticalSlabItems);
        sink.addTag(builder, Registries.ITEM);

        sink.addTag(stepItems, Registries.ITEM);
        sink.addTag(stepBlocks, Registries.BLOCK);

        sink.addTag(woodenStepItems, Registries.ITEM);
        sink.addTag(woodenStepBlocks, Registries.BLOCK);

        builder = SimpleTagBuilder.of(BlockTags.STAIRS);
        builder.addTag(stepBlocks);
        sink.addTag(builder, Registries.BLOCK);
        builder = SimpleTagBuilder.of(ItemTags.STAIRS);
        builder.addTag(stepItems);
        sink.addTag(builder, Registries.ITEM);

        builder = SimpleTagBuilder.of(BlockTags.WOODEN_STAIRS);
        builder.addTag(woodenStepBlocks);
        sink.addTag(builder, Registries.BLOCK);
        builder = SimpleTagBuilder.of(ItemTags.WOODEN_STAIRS);
        builder.addTag(woodenStepItems);
        sink.addTag(builder, Registries.ITEM);

        builder = SimpleTagBuilder.of(BlockTags.MINEABLE_WITH_PICKAXE);
        builder.merge(pickaxeMineable);
        sink.addTag(builder, Registries.BLOCK);
    }
}

*///?}