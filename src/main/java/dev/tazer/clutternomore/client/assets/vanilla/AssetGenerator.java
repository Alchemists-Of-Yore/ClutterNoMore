package dev.tazer.clutternomore.client.assets.vanilla;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
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
    public final static Path pack = Platform.INSTANCE.getResourcePack().resolve("clutternomore");
    public final static Path assets = pack.resolve("assets/clutternomore");
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

    public static void generate(Minecraft minecraft) {
        if (ClutterNoMoreClient.CLIENT_CONFIG.RUNTIME_ASSET_GENERATION.value()) {
            //lang
            var jsonObject = new JsonObject();
            keys.forEach((s)-> {
                jsonObject.addProperty("block.clutternomore." + s, VerticalSlabGenerator.langName(s));
            });
            final Path assets = pack.resolve("assets/clutternomore");
            write(assets.resolve("lang"), "en_us.json", jsonObject.toString());
            // pack.mcmeta
            int packVersion =
            //? if >1.21.8 {
            69;
            //?} else if >1.21.6 {
            /*64;
             */
            //?} else if >1.21 {
            /*34;
             *///?} else {
            /*15;
             *///?}

            int minFormat = 15;
            int maxFormat = 69;

            JsonObject object = new JsonObject();
            object.add("description", new JsonPrimitive("Clutter No More dynamic assets."));
            object.add("pack_format", new JsonPrimitive(packVersion));
            object.add("min_format", new JsonPrimitive(minFormat));
            object.add("max_format", new JsonPrimitive(maxFormat));
            var supportedFormats = new JsonArray(2);
            supportedFormats.add(minFormat);
            supportedFormats.add(maxFormat);
            object.add("supported_formats", supportedFormats);
            JsonObject mcmeta = new JsonObject();
            mcmeta.add("pack", object);
            write(pack, "pack.mcmeta", mcmeta.toString());

            VerticalSlabGenerator.generate();
            StepGenerator.generate();
        }
        if (ClutterNoMore.STARTUP_CONFIG.VERTICAL_SLABS.value() || ClutterNoMore.STARTUP_CONFIG.STEPS.value())
            enablePack(minecraft);
    }

    static void enablePack(Minecraft client) {
        PackRepository m = client.getResourcePackRepository();
        AtomicBoolean alreadyEnabled = new AtomicBoolean(false);
        m.getSelectedPacks().forEach((pack)->{
            if (pack.getId().equals("file/clutternomore")) alreadyEnabled.set(true);
        });
        if (!alreadyEnabled.get()) {
            Path resourcepackPath = client.getResourcePackDirectory().resolve("clutternomore");
            m.reload();
            m.addPack("file/" + resourcepackPath.getFileName().toString());
            client.reloadResourcePacks();
        }
    }

    public static void write(Path path, String fileName, String contents) {
        try {
            path.toFile().mkdirs();
            FileWriter langWriter = new FileWriter(path.resolve(fileName).toFile());
            langWriter.write(contents);
            langWriter.close();  // must close manually
            ClutterNoMore.LOGGER.debug("Successfully wrote to {}", path.resolve(fileName));
        } catch (IOException e) {
            ClutterNoMore.LOGGER.error("Failed to write dynamic data. %s".formatted(e));
        }
    }
}