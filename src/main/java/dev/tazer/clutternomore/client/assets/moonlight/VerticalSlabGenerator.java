package dev.tazer.clutternomore.client.assets.moonlight;

//? if neoforge {

/*import com.google.gson.JsonObject;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.registry.moonlight.BlockSetRegistry;
import net.mehvahdjukaar.moonlight.api.events.AfterLanguageLoadEvent;
import net.mehvahdjukaar.moonlight.api.resources.StaticResource;
import net.mehvahdjukaar.moonlight.api.resources.pack.ResourceSink;
import net.mehvahdjukaar.moonlight.api.set.BlockSetAPI;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class VerticalSlabGenerator implements AssetGenerator {
    public static List<String> VERTICAL_SLABS = new ArrayList<>();

    @Override
    public void initialize(ResourceManager manager, ResourceSink sink) {
        VERTICAL_SLABS.clear();
    }

    public void generate(Item item, ResourceManager manager, ResourceSink sink) {
        BlockSetRegistry.ShapeSet set = BlockSetAPI.getBlockTypeOf(item, BlockSetRegistry.ShapeSet.class);
        if (set == null || !set.hasChild("vertical_slab_block") || item != set.getChild("slab")) return;

        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        String name = verticalSlabName(key.getPath());
        VERTICAL_SLABS.add(name);

        ResourceLocation id = ClutterNoMore.location(name);

        String modelPath = getModel(manager, key);
        if (modelPath == null) return;

        ResourceLocation sourceModel = ClutterNoMore.parse(modelPath).withPath(path -> "models/" + path + ".json");

        Optional<Resource> modelResource = manager.getResource(sourceModel);
        String bottom = null;
        String side = null;
        String top = null;

        if (modelResource.isPresent()) {
            try (BufferedReader reader = modelResource.get().openAsReader()) {
                String line = reader.readLine();
                while (line != null) {
                    int end = line.lastIndexOf("\"");

                    if (line.contains("\"bottom\": ")) bottom = line.substring(line.indexOf("\"bottom\": ") + 11, end);
                    if (line.contains("\"side\": ")) side = line.substring(line.indexOf("\"side\": ") + 9, end);
                    if (line.contains("\"top\": ")) top = line.substring(line.indexOf("\"top\": ") + 8, end);

                    if (bottom != null && side != null && top != null) break;
                    line = reader.readLine();
                }
            } catch (IOException e) {
                ClutterNoMore.LOGGER.catching(e);
                throw new RuntimeException(e);
            }
        }

        if (top == null) {
            if (side != null) {
                top = side;
            } else if (bottom != null) {
                top = bottom;
            }
        }

        if (side == null) {
            side = top;
        }

        if (bottom == null) {
            bottom = side;
        }

        JsonObject textures = new JsonObject();
        if (top != null) {
            textures.addProperty("bottom", bottom);
            textures.addProperty("side", side);
            textures.addProperty("top", top);
        }

        if (manager.getResource(id.withPath(path -> "blockstates/" + path + ".json")).isEmpty()) {
            if (manager.getResource(id.withPath(path -> "models/block/" + path + ".json")).isEmpty()) {
                JsonObject blockModel = new JsonObject();
                blockModel.addProperty("parent", ClutterNoMore.MODID + ":block/templates/vertical_slab");
                if (!textures.isEmpty()) blockModel.add("textures", textures);
                sink.addBlockModel(id, blockModel);
            }

            ResourceLocation doubleId = ClutterNoMore.location(name + "_double");
            if (manager.getResource(doubleId.withPath(path -> "models/block/" + path + ".json")).isEmpty()) {
                JsonObject blockModel = new JsonObject();
                blockModel.addProperty("parent", ClutterNoMore.MODID + ":block/templates/vertical_slab_double");
                if (!textures.isEmpty()) blockModel.add("textures", textures);
                sink.addBlockModel(doubleId, blockModel);
            }

            StaticResource template = StaticResource.getOrThrow(manager, ClutterNoMore.location("blockstates/vertical_slab.json"));
            sink.addSimilarJsonResource(manager, template, string -> string
                    .replace("vertical_slab", name)
            );
        }

        if (set.hasChild("vertical_slab") && manager.getResource(id.withPath(path -> "models/item/" + path + ".json")).isEmpty()) {
            JsonObject itemModel = new JsonObject();
            itemModel.addProperty("parent", ClutterNoMore.MODID + ":block/" + name);
            sink.addItemModel(ClutterNoMore.location(name), itemModel);
        }
    }

    @Override
    public void translate(AfterLanguageLoadEvent languageEvent) {
        VERTICAL_SLABS.forEach(name -> languageEvent.addEntry("block." + ClutterNoMore.MODID + "." + name, langName(name)));
    }

    public static String langName(String name) {
        String processed = name.replace("_", " ");

        List<String> nonCapital = List.of("of", "and", "with");

        String[] words = processed.split(" ");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (!word.isEmpty()) {
                if (!nonCapital.contains(word)) result.append(Character.toUpperCase(word.charAt(0)));
                else result.append(word.charAt(0));
                result.append(word.substring(1)).append(" ");
            }
        }

        return result.toString().trim();
    }

    public static String verticalSlabName(String name) {
        name = "vertical_" + name.substring(0, name.length() - 5);

        if (name.endsWith("_block")) name = name.substring(0, name.length() - 6);
        if (name.endsWith("_planks")) name = name.substring(0, name.length() - 7);
        if (name.endsWith("s")) name = name.substring(0, name.length() - 1);
        return name + "_slab";
    }
}

*///?}