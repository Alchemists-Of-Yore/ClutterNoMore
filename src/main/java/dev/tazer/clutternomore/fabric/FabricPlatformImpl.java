package dev.tazer.clutternomore.fabric;

//? fabric {
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tazer.clutternomore.Platform;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;

public class FabricPlatformImpl implements Platform {

    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public String loader() {
        return "fabric";
    }

    @Override
    public Path getResourcePack() {
        return FabricLoader.getInstance().getGameDir().resolve("resourcepacks");
    }

    @Override
    public JsonObject getFileInJar(String namespace, String path) {
        try {
            return JsonParser.parseReader(new FileReader(FabricLoader.getInstance().getModContainer(namespace).get().findPath(path).get().toString())).getAsJsonObject();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path configPath() {
        return FabricLoader.getInstance().getConfigDir();
    }

}
//?}