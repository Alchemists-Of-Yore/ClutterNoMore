package dev.tazer.clutternomore.forge.networking;
//? if forge {
/*import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ForgeNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ClutterNoMore.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int packetId = 0;
        INSTANCE.registerMessage(packetId++, ChangeStackPacket.class,
                ChangeStackPacket::encode,
                ChangeStackPacket::decode,
                ChangeStackPacket::handle);
    }

    public static void sendToServer(ChangeStackPacket packet) {
        INSTANCE.sendToServer(packet);
    }

    //FIXME
    public static void sendToPlayer(ServerPlayer serverPlayer) {
    }
}
*///?}
