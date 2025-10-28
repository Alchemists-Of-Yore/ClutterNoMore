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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public record ChangeStackPayload(int containerId, int slot, ItemStack stack) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ChangeStackPayload> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(ClutterNoMore.MODID, "player_change_stack"));

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
        if (ShapeMap.contains(data.stack.getItem())) {
            Item main = ShapeMap.getParent(data.stack.getItem());

            if (data.slot == -1) {
                Item item = context.player().getItemInHand(InteractionHand.MAIN_HAND).getItem();
                if (ShapeMap.isParentOfShape(main, item)) {
                    context.player().setItemInHand(InteractionHand.MAIN_HAND, data.stack);
                }
            } else {
                InventoryMenu inventory = context.player().inventoryMenu;
                Slot slot = inventory.getSlot(data.slot);
                Item item = slot.getItem().getItem();
                if (ShapeMap.isParentOfShape(main, item)) {
                    slot.setByPlayer(data.stack);
                    inventory.sendAllDataToRemote();
                }
            }
        }
    }
}
//?}