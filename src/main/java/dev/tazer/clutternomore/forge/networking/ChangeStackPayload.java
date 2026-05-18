package dev.tazer.clutternomore.forge.networking;

//? if forge {
/*import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ChangeStackPayload(int containerId, int slot, int shapeIndex) {

    public static void encode(ChangeStackPayload packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.containerId);
        buf.writeInt(packet.slot);
        buf.writeInt(packet.shapeIndex);
    }

    public static ChangeStackPayload decode(FriendlyByteBuf buf) {
        return new ChangeStackPayload(
                buf.readInt(),
                buf.readInt(),
                buf.readInt()
        );
    }

    public static void handle(ChangeStackPayload packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            applyChange(player, packet.containerId, packet.slot, packet.shapeIndex);
        });
        context.setPacketHandled(true);
    }

    private static void applyChange(Player player, int containerId, int slotIndex, int shapeIndex) {
        if (shapeIndex < 0) return;

        if (slotIndex == -1) {
            ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (!ShapeMap.contains(hand.getItem())) return;
            ItemStack next = ShapeMap.transferStack(hand, shapeIndex);
            if (next.getItem() == hand.getItem() && next.getTag() == hand.getTag()) return;
            player.setItemInHand(InteractionHand.MAIN_HAND, next);
            return;
        }

        AbstractContainerMenu menu = resolveMenu(player, containerId);
        if (menu == null || slotIndex < 0 || slotIndex >= menu.slots.size()) return;
        Slot slot = menu.getSlot(slotIndex);
        ItemStack current = slot.getItem();
        if (!ShapeMap.contains(current.getItem())) return;

        ItemStack next = ShapeMap.transferStack(current, shapeIndex);
        if (next.getItem() == current.getItem() && next.getTag() == current.getTag()) return;

        slot.setByPlayer(next);
        menu.sendAllDataToRemote();
    }

    private static AbstractContainerMenu resolveMenu(Player player, int containerId) {
        if (player.containerMenu != null && player.containerMenu.containerId == containerId) return player.containerMenu;
        if (player.inventoryMenu.containerId == containerId) return player.inventoryMenu;
        return null;
    }
}
*///?}
