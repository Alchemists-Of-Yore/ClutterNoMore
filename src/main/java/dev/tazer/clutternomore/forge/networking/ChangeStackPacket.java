package dev.tazer.clutternomore.forge.networking;

//? if forge {
/*import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ChangeStackPacket(int containerId, int slot, ItemStack stack) {

    public static void encode(ChangeStackPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.containerId);
        buf.writeInt(packet.slot);
        buf.writeItem(packet.stack);
    }

    public static ChangeStackPacket decode(FriendlyByteBuf buf) {
        return new ChangeStackPacket(
                buf.readInt(),
                buf.readInt(),
                buf.readItem()
        );
    }

    public static void handle(ChangeStackPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && ShapeMap.contains(packet.stack.getItem())) {
                Item main = ShapeMap.getParent(packet.stack.getItem());

                if (packet.slot == -1) {
                    Item item = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();
                    if (ShapeMap.isParentOfShape(main, item)) {
                        player.setItemInHand(InteractionHand.MAIN_HAND, packet.stack);
                    }
                } else {
                    InventoryMenu inventory = player.inventoryMenu;
                    Slot slot = inventory.getSlot(packet.slot);
                    Item item = slot.getItem().getItem();
                    if (ShapeMap.isParentOfShape(main, item)) {
                        slot.setByPlayer(packet.stack);
                        inventory.sendAllDataToRemote();
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
*///?}