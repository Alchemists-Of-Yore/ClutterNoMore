package dev.tazer.clutternomore.forge;

//? forge {
/*import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tazer.clutternomore.Platform;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

import static dev.tazer.clutternomore.forge.ForgeClientEvents.SHAPE_KEY;

public class ForgePlatformImpl implements Platform {

    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    public String loader() {
        return "forge";
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

    }

}
*///?}