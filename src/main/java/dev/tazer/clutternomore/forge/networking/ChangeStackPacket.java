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
            if (player == null) return;
            applyChange(player, packet.containerId, packet.slot, packet.stack);
        });
        context.setPacketHandled(true);
    }

    private static void applyChange(Player player, int containerId, int slotIndex, ItemStack stack) {
        if (!ShapeMap.contains(stack.getItem())) return;

        if (slotIndex == -1) {
            ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);
            if (hand.getItem() != stack.getItem() && ShapeMap.inSameShapeSet(stack.getItem(), hand.getItem())) {
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            }
            return;
        }

        AbstractContainerMenu menu = resolveMenu(player, containerId);
        if (menu == null || slotIndex < 0 || slotIndex >= menu.slots.size()) return;
        Slot slot = menu.getSlot(slotIndex);
        ItemStack current = slot.getItem();
        if (current.getItem() == stack.getItem()) return;
        if (!ShapeMap.inSameShapeSet(stack.getItem(), current.getItem())) return;

        ItemStack replaced = stack.copyWithCount(current.getCount());
        slot.setByPlayer(replaced);
        menu.sendAllDataToRemote();
    }

    private static AbstractContainerMenu resolveMenu(Player player, int containerId) {
        if (player.containerMenu != null && player.containerMenu.containerId == containerId) return player.containerMenu;
        if (player.inventoryMenu.containerId == containerId) return player.inventoryMenu;
        return null;
    }
}
*///?}