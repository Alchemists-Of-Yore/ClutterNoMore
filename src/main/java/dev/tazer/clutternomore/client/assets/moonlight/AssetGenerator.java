package dev.tazer.clutternomore.client.assets.moonlight;

//? if neoforge {

/*import dev.tazer.clutternomore.ClutterNoMore;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

public interface AssetGenerator {
    default void initialize(ResourceManager manager, ResourceSink sink) {};
    default void generate(Item item, ResourceManager manager, ResourceSink sink) {};
    default void translate(AfterLanguageLoadEvent languageEvent) {};

    default @Nullable String getModel(ResourceManager manager, ResourceLocation blockstate) {
        Optional<Resource> file = manager.getResource(blockstate.withPath(path -> "blockstates/" + path + ".json"));
        if (file.isEmpty()) return null;

        try (BufferedReader reader = file.get().openAsReader()) {
            String line = reader.readLine();
            while (line != null) {
                int idx = line.indexOf("\"model\":");
                if (idx >= 0) {
                    int start = line.indexOf('"', idx + 8) + 1;
                    int end = line.indexOf('"', start);
                    if (start > 0 && end > start) return line.substring(start, end);
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            ClutterNoMore.LOGGER.catching(e);
            throw new RuntimeException(e);
        }

        return null;
    }
}

*///?}