package dev.tazer.clutternomore.neoforge;

//? neoforge {
/*import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.Platform;
import dev.tazer.clutternomore.common.data.DataGenerator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

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
            return JsonParser.parseReader(new FileReader(ModLoadingContext.get().getActiveContainer().getModInfo().getOwningFile().getFile().findResource(path).toString())).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path configPath() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }

    @Override
    public int shapeKey() {
        return SHAPE_KEY.get().getKey().getValue();
    }

    @Override
    public void finalizeCopperBlockRegistration() {
        JsonObject map = new JsonObject();
        JsonObject values = new JsonObject();
        ClutterNoMore.COPPER_BLOCKS.forEach((less, more) -> {
            JsonObject next_stage = new JsonObject();
            next_stage.addProperty("next_oxidation_stage", more.toString());
            values.add(less.toString(), next_stage);
        });
        map.add("values", values);
        DataGenerator.writeServerData(ResourceLocation.fromNamespaceAndPath("neoforge", "data_maps/block/oxidizables.json"), map);
    }

}
*///?}