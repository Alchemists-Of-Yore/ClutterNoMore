package dev.tazer.clutternomore.client.assets;

import com.google.gson.*;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.Platform;
//? if <1.21.9 {
/*import dev.tazer.clutternomore.common.data.CNMPackResources;
*///?}
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static dev.tazer.clutternomore.ClutterNoMore.pack;
import static dev.tazer.clutternomore.ClutterNoMore.writeFile;

public class AssetGenerator {
    public static Set<String> keys;

    public static void generate() {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();

        //lang
        JsonObject lang = new JsonObject();
        keys.forEach((s)-> {
            lang.addProperty("block.clutternomore." + s.replace("/", "."), langName(s));
        });
        boolean writeLang = true;

        try {
            Optional<Resource> existingLang = manager.getResource(ClutterNoMore.location("lang/en_us.json"));
            if (existingLang.isPresent()) {
                JsonObject existingLangJson = JsonParser.parseReader(existingLang.get().openAsReader()).getAsJsonObject();
                if (existingLangJson.equals(lang)) writeLang = false;
            }
        } catch (IOException e) {
            ClutterNoMore.LOGGER.catching(e);
            throw new RuntimeException(e);
        }

        if (writeLang) write("lang/en_us.json", lang);

        // block assets
        VerticalSlabGenerator.generate(manager);
        StepGenerator.generate(manager);

        if (ClutterNoMoreClient.CLIENT_CONFIG.RUNTIME_ASSET_GENERATION.value()) {
            int minFormat = 15;
            int maxFormat = 70;

            JsonObject object = new JsonObject();
            object.add("description", new JsonPrimitive("Generated resources for ClutterNoMore"));
            //? if <1.21.9 {
            /*object.add("pack_format", new JsonPrimitive(CNMPackResources.resourcePackVersion));
            *///?} else {
            object.add("pack_format", new JsonPrimitive(15));
            //?}
            object.add("min_format", new JsonPrimitive(minFormat));
            object.add("max_format", new JsonPrimitive(maxFormat));
            var supportedFormats = new JsonArray(2);
            supportedFormats.add(minFormat);
            supportedFormats.add(maxFormat);
            object.add("supported_formats", supportedFormats);
            JsonObject mcmeta = new JsonObject();
            mcmeta.add("pack", object);
            writeFile(pack, pack.resolve("pack.mcmeta"), mcmeta.toString());
        }
    }

    public static void write(String fileName, JsonElement contents) {
        ClutterNoMore.RESOURCES.addJson(PackType.CLIENT_RESOURCES, ClutterNoMore.location(fileName), contents);
        if (ClutterNoMoreClient.CLIENT_CONFIG.RUNTIME_ASSET_GENERATION.value()) {
            ClutterNoMoreClient.requireReload = true;
            Path assets = pack.resolve("assets/clutternomore");
            writeFile(assets.resolve(fileName.substring(0, fileName.lastIndexOf("/"))), assets.resolve(fileName), contents.toString());
        }
    }

    public static String langName(String name) {
        if (name.contains("/")) name = name.substring(name.lastIndexOf("/")+1);
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

    public static @Nullable JsonObject getTextures(ResourceManager manager, ResourceLocation parent) throws IOException {
        Optional<Resource> parentBlockState = manager.getResource(parent.withPrefix("blockstates/").withSuffix(".json"));
        if (parentBlockState.isEmpty()) return null;

        String model = null;
        BufferedReader reader = parentBlockState.get().openAsReader();
        String line = reader.readLine();
        while (line != null) {
            if (line.contains("\"model\":")) {
                int firstIndex = line.indexOf("\"", line.indexOf("model\":") + 7);
                int secondIndex = line.indexOf("\"", firstIndex + 1);
                model = line.substring(firstIndex + 1, secondIndex);
                break;
            }

            line = reader.readLine();
        }

        if (model == null) return null;

        String[] modelParts = model.split(":");
        if (modelParts.length != 2) return null;

        Optional<Resource> parentModel = manager.getResource(ClutterNoMore.location(modelParts[0], "models/%s.json".formatted(modelParts[1])));
        if (parentModel.isEmpty()) return null;

        JsonObject textures = JsonParser.parseReader(parentModel.get().openAsReader()).getAsJsonObject().getAsJsonObject("textures");

        if (textures.get("top") == null) {
            if (textures.get("side") != null) {
                textures.add("top", textures.get("side"));
            } else if (textures.get("bottom") != null) {
                textures.add("top", textures.get("bottom"));
            }
        }

        if (textures.get("side") == null) {
            textures.add("side", textures.get("top"));
        }

        if (textures.get("bottom") == null) {
            textures.add("bottom", textures.get("top"));
        }

        return textures.get("top") == null ? null : textures;
    }

    public static void generateItem(ResourceLocation shape, ResourceManager manager) {
        var modelString = shape.getPath();
        modelString = modelString.replace("waxed_", "");
        //? if >1.21.4 {
        Optional<Resource> existingItemState = manager.getResource(shape.withPrefix("items/").withSuffix(".json"));
        if (existingItemState.isPresent()) return;

        JsonObject itemState = new JsonObject();
        JsonObject model = new JsonObject();
        model.addProperty("type", "minecraft:model");
        model.addProperty("model", "clutternomore:block/"+modelString);
        itemState.add("model", model);
        write("items/%s.json".formatted(shape.getPath()), itemState);
        //?} else {
        /*Optional<Resource> existingItemState = manager.getResource(shape.withPrefix("models/item/").withSuffix(".json"));
        if (existingItemState.isPresent()) return;

        JsonObject model = new JsonObject();
        model.addProperty("parent", "clutternomore:block/"+modelString);
        write("models/item/%s.json".formatted(shape.getPath()), model);
        *///?}
    }
}