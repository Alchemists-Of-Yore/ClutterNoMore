package dev.tazer.clutternomore.client.assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class AssetGenerator {
    public static Set<String> keys;

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
        if (ClutterNoMoreClient.CLIENT_CONFIG.RUNTIME_ASSET_GENERATION.value()) {
            //lang
            var jsonObject = new JsonObject();
            keys.forEach((s)-> {
                jsonObject.addProperty("block.clutternomore." + s, VerticalSlabGenerator.langName(s));
            });
            write("lang/en_us.json", jsonObject);

            // blockstates and models
            VerticalSlabGenerator.generate();
            StepGenerator.generate();
        }
    }

    public static void write(String path, JsonElement contents) {
        ClutterNoMore.RESOURCES.addJson(PackType.CLIENT_RESOURCES, ClutterNoMore.location(path), contents);
    }
}