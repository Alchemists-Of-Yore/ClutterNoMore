package dev.tazer.clutternomore.common.data;

//? if neoforge {

/*import dev.tazer.clutternomore.ClutterNoMore;

import net.mehvahdjukaar.moonlight.api.platform.RegHelper;
import net.mehvahdjukaar.moonlight.api.resources.pack.DynamicServerResourceProvider;
import net.mehvahdjukaar.moonlight.api.resources.pack.PackGenerationStrategy;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceGenTask;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
//? if neoforge {
/^import net.neoforged.fml.ModList;
^///?} else if forge {
/^import net.minecraftforge.fml.ModList;
^///?} else {
import net.fabricmc.loader.api.FabricLoader;
//?}

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public class DynamicServerResources extends DynamicServerResourceProvider {

    public static void register() {
//        RegHelper.registerDynamicResourceProvider(new DynamicServerResources());
    }

    protected DynamicServerResources() {
        super(ClutterNoMore.location("generated_pack"), PackGenerationStrategy.REGEN_ON_EVERY_RELOAD);
    }

    @Override
    protected Collection<String> gatherSupportedNamespaces() {
        List<String> namespaces = new ArrayList<>(List.of("minecraft", ClutterNoMore.MODID));
        //? if neoforge || forge {
        /^ModList.get().getMods().forEach(info -> namespaces.add(info.getNamespace()));
        ^///?} else {
        FabricLoader.getInstance().getAllMods().forEach(info -> namespaces.add(info.getMetadata().getId()));
        //?}
        return namespaces;
    }

    private static final List<DataGenerator> GENERATORS = List.of(
            new TagsGenerator(),
            new LootGenerator()
    );

    @Override
    public void regenerateDynamicAssets(Consumer<ResourceGenTask> executor) {
        executor.accept((resourceManager, sink) -> {
            for (DataGenerator generator : GENERATORS) {
                generator.initialize(resourceManager, sink);

                generator.generate(resourceManager, sink);

                List<Item> items = BuiltInRegistries.ITEM.stream().toList();
                for (Item item : items) {
                    generator.generate(item, resourceManager, sink);
                }

                List<Block> blocks = BuiltInRegistries.BLOCK.stream().toList();
                for (Block block : blocks) {
                    generator.generate(block, resourceManager, sink);
                }

                generator.finish(resourceManager, sink);
            }
        });
    }
}

*///?}