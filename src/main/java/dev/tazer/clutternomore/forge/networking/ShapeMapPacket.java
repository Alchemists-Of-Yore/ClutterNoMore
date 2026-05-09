package dev.tazer.clutternomore.forge.networking;

//? if forge {
/*import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public record ShapeMapPacket(Map<Identifier, List<Identifier>> shapes, Map<Identifier, Identifier> inverseShapes) {

    public static void encode(ShapeMapPacket packet, FriendlyByteBuf buf) {
        buf.writeMap(packet.shapes, FriendlyByteBuf::writeIdentifier,
                (b, ids) -> b.writeCollection(ids, FriendlyByteBuf::writeIdentifier)
        );
        buf.writeMap(packet.inverseShapes, FriendlyByteBuf::writeIdentifier, FriendlyByteBuf::writeIdentifier);
    }

    public static ShapeMapPacket decode(FriendlyByteBuf buf) {
        return new ShapeMapPacket(
                buf.readMap(FriendlyByteBuf::readIdentifier,
                        b -> b.readList(FriendlyByteBuf::readIdentifier)),
                buf.readMap(FriendlyByteBuf::readIdentifier, FriendlyByteBuf::readIdentifier)
        );
    }

    public static void handle(ShapeMapPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            final Map<Item, List<Item>> SHAPES_DATAMAP = new HashMap<>();
            packet.shapes.forEach((parentId, shapeIds) -> {
                Item parent = BuiltInRegistries.ITEM.getOptional(parentId).orElse(null);
                if (parent == null) return;
                ArrayList<Item> shapes = new ArrayList<>(shapeIds.size());
                for (Identifier shapeId : shapeIds) {
                    BuiltInRegistries.ITEM.getOptional(shapeId).ifPresent(shapes::add);
                }
                SHAPES_DATAMAP.put(parent, shapes);
            });
            final Map<Item, Item> INVERSE_SHAPES_DATAMAP = new HashMap<>();
            packet.inverseShapes.forEach((shapeId, parentId) -> {
                Item shape = BuiltInRegistries.ITEM.getOptional(shapeId).orElse(null);
                Item parent = BuiltInRegistries.ITEM.getOptional(parentId).orElse(null);
                if (shape != null && parent != null) INVERSE_SHAPES_DATAMAP.put(shape, parent);
            });
            ShapeMap.setShapeMaps(SHAPES_DATAMAP, INVERSE_SHAPES_DATAMAP);
        }));
        context.setPacketHandled(true);
    }
}
*///?}
