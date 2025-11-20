package dev.tazer.clutternomore;

//? fabric {

import dev.tazer.clutternomore.fabric.FabricPlatformImpl;
//?}
//? neoforge {
/*import dev.tazer.clutternomore.neoforge.NeoForgePlatformImpl;
*///?}
//? forge {
/*import dev.tazer.clutternomore.forge.ForgePlatformImpl;
 *///?}
import java.nio.file.Path;
import net.minecraft.resources.ResourceLocation;
import java.nio.file.Path;
import com.google.gson.JsonObject;
import net.minecraft.server.packs.repository.Pack;


public interface Platform {

    //? if fabric {
    Platform INSTANCE = new FabricPlatformImpl();
    //?}
    //? if neoforge {
    /*Platform INSTANCE = new NeoForgePlatformImpl();
    *///?}
    //? if forge {
    /*Platform INSTANCE = new ForgePlatformImpl();
     *///?}


    boolean isModLoaded(String modid);
    String loader();

    Path getResourcePack();

    JsonObject getFileInJar(String namespace, String path);

    Path configPath();

    boolean isClient();

    int shapeKey();
}
