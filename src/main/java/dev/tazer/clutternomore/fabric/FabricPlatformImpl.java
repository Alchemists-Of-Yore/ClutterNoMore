package dev.tazer.clutternomore.fabric;

//? fabric {
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.registry.OxidizableBlocksRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Optional;

import static dev.tazer.clutternomore.fabric.FabricClientEvents.SHAPE_KEY;

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

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT);
    }

    @Override
    public int shapeKey() {
        return KeyBindingHelper.getBoundKeyOf(SHAPE_KEY).getValue();
    }

    @Override
    public void finalizeCopperBlockRegistration() {
        ClutterNoMore.COPPER_BLOCKS.forEach((less, more) -> {
            //? if >1.21.2 {
            Block lessBlock = BuiltInRegistries.BLOCK.getValue(less);
            Block moreBlock = BuiltInRegistries.BLOCK.getValue(more);
            //?} else {
            /*Block lessBlock = BuiltInRegistries.BLOCK.get(less);
            Block moreBlock = BuiltInRegistries.BLOCK.get(more);
            *///?}
            OxidizableBlocksRegistry.registerOxidizableBlockPair(lessBlock, moreBlock);
        });
        ClutterNoMore.WAXED_COPPER_BLOCKS.forEach(resourceLocation -> {
            Optional<Block> waxedBlock = BuiltInRegistries.BLOCK.getOptional(resourceLocation);
            Optional<Block> unwaxedBlock = BuiltInRegistries.BLOCK.getOptional(ClutterNoMore.location(resourceLocation.getNamespace(), resourceLocation.getPath().replace("waxed_", "")));
            if (waxedBlock.isPresent() && unwaxedBlock.isPresent()) {
               OxidizableBlocksRegistry.registerWaxableBlockPair(unwaxedBlock.get(), waxedBlock.get());
            }
        });
    }

}
//?}