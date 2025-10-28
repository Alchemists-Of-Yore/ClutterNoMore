package dev.tazer.clutternomore.fabric;

//? fabric {

import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.access.RegistryAccess;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
import dev.tazer.clutternomore.common.registry.CBlocks;
import dev.tazer.clutternomore.common.registry.CommonRegistry;
import dev.tazer.clutternomore.common.shape_map.ShapeMapHandler;
//? if >1.21.2 {
import fuzs.forgeconfigapiport.fabric.api.v5.ConfigRegistry;
//?} else {
/*import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
*///?}
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//? if >=1.21.9 {
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
//?} else {
/*import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
*///?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.fml.config.ModConfig;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static dev.tazer.clutternomore.ClutterNoMore.MODID;

public class FabricEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        ClutterNoMore.init();
        registerPayloadHandlers();
        //? if >=1.21.9 {
        ResourceLoader.get(PackType.SERVER_DATA).registerReloader(ClutterNoMore.location("shape_map"), new ShapeMapHandler());
        //?} else {
        /*ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new ShapeMapHandler());
        *///?}
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(((minecraftServer, closeableResourceManager) -> {
            ClutterNoMore.load(minecraftServer.registryAccess(), minecraftServer.getRecipeManager());
        }));
        ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
            ClutterNoMore.load(minecraftServer.registryAccess(), minecraftServer.getRecipeManager());
        });
    }

    public void registerPayloadHandlers() {
        PayloadTypeRegistry.playC2S().register(ChangeStackPayload.TYPE, ChangeStackPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ChangeStackPayload.TYPE, ChangeStackPayload::handleDataOnServer);
    }
}
//?}