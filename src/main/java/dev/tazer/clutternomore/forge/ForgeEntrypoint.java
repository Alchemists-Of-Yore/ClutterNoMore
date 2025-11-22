package dev.tazer.clutternomore.forge;
//? forge {


/*import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.common.shape_map.ShapeMapFileHandler;
import dev.tazer.clutternomore.forge.networking.ForgeNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ClutterNoMore.MODID)
public class ForgeEntrypoint {
    public static final Logger LOGGER = LogManager.getLogger("ClutterNoMore");

    public ForgeEntrypoint() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        Dist dist = FMLEnvironment.dist;

        ClutterNoMore.init();
        ForgeNetworking.register();

        if (dist.isClient()) {
            ClutterNoMoreClient.init();
            modEventBus.addListener(ForgeClientEvents::clientSetup);
        }

        MinecraftForge.EVENT_BUS.addListener(ForgeEntrypoint::addReloadListeners);
        MinecraftForge.EVENT_BUS.addListener(ForgeEntrypoint::onServerStarted);
        modEventBus.addListener(ForgeEntrypoint::commonSetup);
        modEventBus.addListener(ForgeEntrypoint::registerBlocks);
    }

//    private static void onServerStarted(ServerStartedEvent event) {
//        MinecraftServer server = event.getServer();
//        ClutterNoMore.load(server.registryAccess(), server.getRecipeManager());
//    }

    private static void addReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new ShapeMapFileHandler());

    }

    @SuppressWarnings("deprecation")
    public static void commonSetup(FMLCommonSetupEvent event) {
        BuiltInRegistries.BLOCK.freeze();
        BuiltInRegistries.ITEM.freeze();
    }


    static void registerBlocks(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registries.POTION)) {
            ClutterNoMore.registerVariants();
        }
    }

}
*///?}