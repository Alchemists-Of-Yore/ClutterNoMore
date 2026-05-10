package dev.tazer.clutternomore.neoforge;

//? neoforge {
/*import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.Platform;
import dev.tazer.clutternomore.common.data.DataGenerator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
//? if >26
import net.neoforged.fml.jarcontents.JarResource;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforgespi.locating.IModFile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static dev.tazer.clutternomore.neoforge.NeoForgeClientEvents.SHAPE_KEY;

public class NeoForgePlatformImpl implements Platform {

    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    public String loader() {
        return "neoforge";
    }

    @Override
    public Path getResourcePack() {
        return FMLPaths.getOrCreateGameRelativePath(Path.of("resourcepacks"));
    }

    @Override
    public JsonObject getFileInJar(String namespace, String path) {
        try {
            var owningFile = ModLoadingContext.get().getActiveContainer().getModInfo().getOwningFile().getFile();
            //? if >26 {
            JarResource jarResource = owningFile.getContents().get(path);
            if (jarResource == null) throw new IOException("Resource not found: " + path);
            var file = JsonParser.parseReader(jarResource.bufferedReader());
            //?} else
            //var file = JsonParser.parseReader(new FileReader(owningFile.findResource(path).toString()));
            return file.getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path configPath() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isClient() {
        //? if <26
        //return FMLEnvironment.dist.isClient();
        //? if >26
        return FMLEnvironment.getDist().isClient();
    }

    @Override
    public int shapeKey() {
        return SHAPE_KEY.get().getKey().getValue();
    }

    @Override
    public void finalizeCopperBlockRegistration() {
        // oxidizables
        JsonObject oxidizableMap = new JsonObject();
        JsonObject oxidizableValues = new JsonObject();
        ClutterNoMore.COPPER_BLOCKS.forEach((less, more) -> {
            JsonObject next_stage = new JsonObject();
            next_stage.addProperty("next_oxidation_stage", more.toString());
            oxidizableValues.add(less.toString(), next_stage);
        });
        oxidizableMap.add("values", oxidizableValues);
        DataGenerator.writeServerData(Identifier.fromNamespaceAndPath("neoforge", "data_maps/block/oxidizables.json"), oxidizableMap);

        // waxables
        JsonObject waxableMap = new JsonObject();
        JsonObject waxableValues = new JsonObject();
        ClutterNoMore.WAXED_COPPER_BLOCKS.forEach(waxedId -> {
            Identifier unwaxedId = ClutterNoMore.location(waxedId.getNamespace(), waxedId.getPath().replace("waxed_", ""));
            JsonObject next_stage = new JsonObject();
            next_stage.addProperty("waxed", waxedId.toString());
            waxableValues.add(unwaxedId.toString(), next_stage);
        });
        waxableMap.add("values", waxableValues);
        DataGenerator.writeServerData(Identifier.fromNamespaceAndPath("neoforge", "data_maps/block/waxables.json"), waxableMap);

    }

}
*///?}