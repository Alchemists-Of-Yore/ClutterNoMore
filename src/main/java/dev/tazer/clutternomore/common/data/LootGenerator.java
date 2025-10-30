package dev.tazer.clutternomore.common.data;

//? if neoforge {

/*import dev.tazer.clutternomore.common.registry.moonlight.BlockSetRegistry;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class LootGenerator implements DataGenerator {

    @Override
    public void generate(Block block, ResourceManager manager, ResourceSink sink) {
        BlockSetRegistry.ShapeSet set = null;
        if (block.asItem() != Items.AIR) set = BlockSetAPI.getBlockTypeOf(block, BlockSetRegistry.ShapeSet.class);
        if (set == null || block != set.mainChild()) return;

        if (set.hasChild("vertical_slab_block")) {
            Block verticalSlab = (Block) set.getChild("vertical_slab_block");
            ResourceLocation verticalSlabId = BuiltInRegistries.ITEM.getKey(verticalSlab.asItem());
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(block.asItem());

            StaticResource lootTable = StaticResource.getOrLog(manager, id.withPath(path -> "loot_table/blocks/" + path + ".json"));
            if (lootTable != null) {
                sink.addSimilarJsonResource(manager, lootTable, t -> t, path -> path
                        .replace(id.getPath(), verticalSlabId.getPath())
                );
            }
        }

        if (set.hasChild("step_block")) {
            Block step = (Block) set.getChild("step_block");
            ResourceLocation stepId = BuiltInRegistries.ITEM.getKey(step.asItem());
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(block.asItem());

            StaticResource lootTable = StaticResource.getOrLog(manager, id.withPath(path -> "loot_table/blocks/" + path + ".json"));
            if (lootTable != null) {
                sink.addSimilarJsonResource(manager, lootTable, t -> t, path -> path
                        .replace(id.getPath(), stepId.getPath())
                );
            }
        }

        set.getChildren().forEach(entry -> {
            if (entry.getKey().startsWith("vertical_slab") || entry.getKey().startsWith("step")) return;

            if (entry.getValue() instanceof Item item) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(block.asItem());

                StaticResource lootTable = StaticResource.getOrLog(manager, id.withPath(path -> "loot_table/blocks/" + path + ".json"));
                if (lootTable != null) {
                    Function<String, String> pathTransform = path -> path.replace(id.getPath(), itemId.getPath());
                    ResourceLocation fullPath = lootTable.location;
                    StringBuilder builder = new StringBuilder();
                    String[] partial = fullPath.getPath().split("/");

                    for(int i = 0; i < partial.length; ++i) {
                        if (i != 0) builder.append("/");

                        if (i == partial.length - 1) builder.append(pathTransform.apply(partial[i]));
                        else builder.append(partial[i]);
                    }

                    ResourceLocation newRes = ResourceLocation.fromNamespaceAndPath(fullPath.getNamespace(), builder.toString());
                    StaticResource newTable = StaticResource.create(lootTable.data, newRes);
                    sink.addResource(newTable);
                }
            }
        });
    }
}
*///?}
