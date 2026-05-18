package dev.tazer.clutternomore.forge.networking;
//? if forge {
/*import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class ForgeNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new Identifier(ClutterNoMore.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void register() {
        int packetId = 0;
        INSTANCE.registerMessage(packetId++, ChangeStackPayload.class,
                ChangeStackPayload::encode,
                ChangeStackPayload::decode,
                ChangeStackPayload::handle);
        INSTANCE.registerMessage(packetId++, ShapeMapPacket.class,
                ShapeMapPacket::encode,
                ShapeMapPacket::decode,
                ShapeMapPacket::handle);
    }

    //FIXME
    public static void sendToPlayer(ServerPlayer serverPlayer, ShapeMapPacket packet) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);
    }
}
*///?}
