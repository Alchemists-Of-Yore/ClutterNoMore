package dev.tazer.clutternomore.fabric;

//? fabric {

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
import dev.tazer.clutternomore.common.networking.ShapeMapPayload;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.shape_map.ShapeMapHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//? if >=1.21.9 {
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
//?} else {
/*import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
*///?}
import net.minecraft.server.packs.PackType;

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
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((serverPlayer, b) -> {
            ShapeMap.sendShapeMap(serverPlayer);
        });
    }

    public void registerPayloadHandlers() {
        PayloadTypeRegistry.playC2S().register(ChangeStackPayload.TYPE, ChangeStackPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ChangeStackPayload.TYPE, ChangeStackPayload::handleDataOnServer);
        PayloadTypeRegistry.playS2C().register(ShapeMapPayload.TYPE, ShapeMapPayload.STREAM_CODEC);
    }
}
//?}