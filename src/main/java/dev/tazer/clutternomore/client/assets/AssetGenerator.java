package dev.tazer.clutternomore.client.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.Platform;
//? if <1.21.9 {
/*import dev.tazer.clutternomore.common.data.CNMPackResources;
*///?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

public class AssetGenerator {
    public static Set<String> keys;
    public static final Path pack = Platform.INSTANCE.getResourcePack().resolve("clutternomore");

    public static @Nullable String getModel(ResourceManager manager, ResourceLocation blockstate) {
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

    public static void generate() {
        //lang
        var jsonObject = new JsonObject();
        keys.forEach((s)-> {
            jsonObject.addProperty("block.clutternomore." + s.replace("/", "."), VerticalSlabGenerator.langName(s));
        });
        write("lang/en_us.json", jsonObject);

        // blockstates and models
        VerticalSlabGenerator.generate();
        StepGenerator.generate();
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
            var assets = pack.resolve("assets/clutternomore");
            writeFile(assets.resolve(fileName.substring(0, fileName.lastIndexOf("/"))), assets.resolve(fileName), contents.toString());
        }
    }


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
}