package dev.tazer.clutternomore.common.networking;

//? if neoforge || fabric {

/*import dev.tazer.clutternomore.ClutterNoMore;*/
//? if neoforge {
/*import net.neoforged.neoforge.network.handling.IPayloadContext;
*///?} else if fabric {
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//?}
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public record ChangeStackPayload(int containerId, int slot, ItemStack stack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeStackPayload> TYPE = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(ClutterNoMore.MODID, "player_change_stack"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeStackPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ChangeStackPayload::containerId,
            ByteBufCodecs.INT,
            ChangeStackPayload::slot,
            ItemStack.STREAM_CODEC,
            ChangeStackPayload::stack,
            ChangeStackPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleDataOnServer(final ChangeStackPayload data,
                                          //? neoforge {
                                          /*final IPayloadContext
                                          *///?} else {
                                          ServerPlayNetworking.Context
                                            //?}
                                                  context) {
        Player player = context.player();
        applyChange(player, data.containerId, data.slot, data.stack);
    }

    public static void applyChange(Player player, int containerId, int slotIndex, ItemStack stack) {
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
//?}