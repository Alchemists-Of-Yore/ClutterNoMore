package dev.tazer.clutternomore.client;

public class ClientNetworking {
    public static void sendToServer(
            //? if >1.21 {
             net.minecraft.network.protocol.common.custom.CustomPacketPayload p
            //?} else {
            /*Object p
            *///?}
    ) {
        //? if fabric
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(p);
        //? if neoforge && <26
        //net.neoforged.neoforge.network.PacketDistributor.sendToServer(p);
        //? if neoforge && >26
        //net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(p);
        //? if forge && <1.21.1 {
        /* dev.tazer.clutternomore.forge.networking.ForgeNetworking.INSTANCE.sendToServer(p);
        *///?}
    }
}
