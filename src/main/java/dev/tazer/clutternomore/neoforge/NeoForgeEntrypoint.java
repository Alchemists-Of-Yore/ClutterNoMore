//? if neoforge {
/*package dev.tazer.clutternomore.neoforge;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.common.data.vanilla.CNMPackResources;
import dev.tazer.clutternomore.common.networking.ShapeMapPayload;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.shape_map.ShapeMapHandler;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
import net.mehvahdjukaar.moonlight.api.misc.ResourceLocationSearchTrie;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.api.resources.pack.IEditablePackResources;
import net.mehvahdjukaar.moonlight.api.resources.pack.InMemoryPackResources;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.AddPackFindersEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Mod(ClutterNoMore.MODID)
@EventBusSubscriber(modid = ClutterNoMore.MODID)
public class NeoForgeEntrypoint {
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");

    public NeoForgeEntrypoint(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        ClutterNoMore.init();

        if (dist.isClient()) {
            ClutterNoMoreClient.init();
        }
    }

//    @SubscribeEvent
//    private static void addPackFinders(AddPackFindersEvent event) {
//        if (event.getPackType() == PackType.SERVER_DATA) {
//            PackLocationInfo info = new PackLocationInfo(ClutterNoMore.MODID, Component.literal("Dynamic server data for ClutterNoMore"), PackSource.BUILT_IN, Optional.empty());
//            CNMPackResources resources = new CNMPackResources(info, PackType.SERVER_DATA);
//
//            JsonObject object = new JsonObject();
//            object.add("description", new JsonPrimitive("Clutter No More resources"));
//            object.add("pack_format", new JsonPrimitive(15));
//            JsonObject meta = new JsonObject();
//            meta.add("pack", object);
//            resources.addRootJson("pack.mcmeta", meta);
//
//            JsonArray array = new JsonArray();
//            array.add("minecraft:iron_block");
//            JsonObject tag = new JsonObject();
//            tag.add("values", array);
//            resources.addJson(ClutterNoMore.location("tags/block/stupid.json"), tag);
//
//            var pack = Pack.readMetaAndCreate(
//                    info,
//                    new Pack.ResourcesSupplier() {
//                        @Override
//                        public PackResources openPrimary(PackLocationInfo location) {
//                            return resources;
//                        }
//
//                        @Override
//                        public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
//                            return resources;
//                        }
//                    },
//                    PackType.SERVER_DATA,
//                    new PackSelectionConfig(
//                            true,
//                            Pack.Position.TOP,
//                            false
//                    )
//            );
//
//            if (pack != null) {
//                event.addRepositorySource(infoConsumer -> infoConsumer.accept(pack));
//            }
//        }
//    }

    @SubscribeEvent
    private static void registerPayloadHandlers(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playToServer(
                ChangeStackPayload.TYPE,
                ChangeStackPayload.STREAM_CODEC,
                ChangeStackPayload::handleDataOnServer
        );
        registrar.playToClient(
                ShapeMapPayload.TYPE,
                ShapeMapPayload.STREAM_CODEC,
                ShapeMapPayload::handleDataOnClient
        );
    }

    @SubscribeEvent
    private static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        ClutterNoMore.load(server.registryAccess(), server.getRecipeManager());
    }

    @SubscribeEvent
    private static void onServerStarted(OnDatapackSyncEvent event) {
        ShapeMap.sendShapeMap(event.getPlayer());
    }

    @SubscribeEvent
    private static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new CReloadListener(event.getServerResources()));
        event.addListener(new ShapeMapHandler());
    }

}
*///?}