//? if neoforge {
/*package dev.tazer.clutternomore.neoforge;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.assets.AssetGenerator;
import dev.tazer.clutternomore.common.networking.ShapeMapPayload;
import dev.tazer.clutternomore.common.shape_map.ShapeMapHandler;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ClutterNoMore.MODID)
public class NeoForgeEntrypoint {
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");

    public NeoForgeEntrypoint(IEventBus modEventBus, ModContainer modContainer, Dist dist) {
        ClutterNoMore.init();

        if (dist.isClient()) {
            ClutterNoMoreClient.init();
            modEventBus.addListener(NeoForgeEntrypoint::clientSetup);
        }

        modEventBus.addListener(NeoForgeEntrypoint::registerPayloadHandlers);
        NeoForge.EVENT_BUS.addListener(NeoForgeEntrypoint::addReloadListeners);
        NeoForge.EVENT_BUS.addListener(NeoForgeEntrypoint::onServerStarted);
        modEventBus.addListener(NeoForgeEntrypoint::commonSetup);
    }

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

    private static void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        ClutterNoMore.load(server.registryAccess(), server.getRecipeManager());
    }

    private static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new CReloadListener(event.getServerResources()));
        event.addListener(new ShapeMapHandler());
    }

    private static void commonSetup(FMLCommonSetupEvent event) {
        ClutterNoMore.registerVariants();
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        AssetGenerator.generate();
    }

}
*///?}