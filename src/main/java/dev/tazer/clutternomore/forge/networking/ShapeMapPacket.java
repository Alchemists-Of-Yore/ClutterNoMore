package dev.tazer.clutternomore.forge.networking;

//? if forge {
/*import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record ShapeMapPacket(Map<ItemStack, List<ItemStack>> shapes, Map<ItemStack, ItemStack> inverseShapes) {

    public static void encode(ShapeMapPacket packet, FriendlyByteBuf buf) {
        buf.writeMap(packet.shapes, FriendlyByteBuf::writeItem,
                (friendlyByteBuf, itemStacks) -> friendlyByteBuf.writeCollection(itemStacks, FriendlyByteBuf::writeItem)
        );
        buf.writeMap(packet.inverseShapes, FriendlyByteBuf::writeItem, FriendlyByteBuf::writeItem);
    }

    public static ShapeMapPacket decode(FriendlyByteBuf buf) {
        return new ShapeMapPacket(
                buf.readMap(FriendlyByteBuf::readItem, friendlyByteBuf -> friendlyByteBuf.readList(FriendlyByteBuf::readItem)),
                buf.readMap(FriendlyByteBuf::readItem, FriendlyByteBuf::readItem)
        );
    }

    public static void handle(ShapeMapPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        ClutterNoMore.LOGGER.info("Recieved shape map!");
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            final Map<Item, List<Item>> SHAPES_DATAMAP = new HashMap<>();
            packet.shapes.forEach(((item, items) -> {
                ArrayList<Item> objects = new ArrayList<>();
                items.forEach((stack -> objects.add(stack.getItem())));
                SHAPES_DATAMAP.put(item.getItem(), objects);
            }));
            final Map<Item, Item> INVERSE_SHAPES_DATAMAP = new HashMap<>();
            packet.inverseShapes.forEach(((item, items) -> {
                INVERSE_SHAPES_DATAMAP.put(item.getItem(), items.getItem());
            }));
            ShapeMap.setShapeMaps(SHAPES_DATAMAP, INVERSE_SHAPES_DATAMAP);
        }));
        context.setPacketHandled(true);
    }
}
*///?}