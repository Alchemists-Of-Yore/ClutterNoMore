package dev.tazer.clutternomore.common.networking;

//? if neoforge || fabric {

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
//? if fabric
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
//? if neoforge
//import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ShapeMapPayload(Map<Identifier, List<Identifier>> shapes, Map<Identifier, Identifier> inverseShapes) implements CustomPacketPayload {
    public static final Type<ShapeMapPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(ClutterNoMore.MODID, "shapes"));

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Identifier>> ID_LIST_CODEC = StreamCodec.of(
            (buf, list) -> {
                buf.writeVarInt(list.size());
                for (Identifier id : list) Identifier.STREAM_CODEC.encode(buf, id);
            },
            buf -> {
                int size = buf.readVarInt();
                List<Identifier> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) list.add(Identifier.STREAM_CODEC.decode(buf));
                return list;
            }
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<Identifier, List<Identifier>>> SHAPE_MAP_CODEC = ByteBufCodecs.map(
            HashMap::new, Identifier.STREAM_CODEC, ID_LIST_CODEC, BuiltInRegistries.ITEM.size()
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<Identifier, Identifier>> INVERSE_SHAPE_MAP_CODEC = ByteBufCodecs.map(
            HashMap::new, Identifier.STREAM_CODEC, Identifier.STREAM_CODEC, BuiltInRegistries.ITEM.size()
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, ShapeMapPayload> STREAM_CODEC = StreamCodec.composite(
            SHAPE_MAP_CODEC,
            ShapeMapPayload::shapes,
            INVERSE_SHAPE_MAP_CODEC,
            ShapeMapPayload::inverseShapes,
            ShapeMapPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleDataOnClient(final ShapeMapPayload data,
                                          //? if fabric
                                          ClientPlayNetworking.Context
                                          //? if neoforge
                                          //IPayloadContext
                                          //? if forge
                                          //Object
                                          context) {
        final Map<Item, List<Item>> shapesByParent = new HashMap<>();
        data.shapes.forEach((parentId, shapeIds) -> {
            Item parent = BuiltInRegistries.ITEM.getOptional(parentId).orElse(null);
            if (parent == null) return;
            ArrayList<Item> shapes = new ArrayList<>(shapeIds.size());
            for (Identifier shapeId : shapeIds) {
                BuiltInRegistries.ITEM.getOptional(shapeId).ifPresent(shapes::add);
            }
            shapesByParent.put(parent, shapes);
        });
        final Map<Item, Item> parentByShape = new HashMap<>();
        data.inverseShapes.forEach((shapeId, parentId) -> {
            Item shape = BuiltInRegistries.ITEM.getOptional(shapeId).orElse(null);
            Item parent = BuiltInRegistries.ITEM.getOptional(parentId).orElse(null);
            if (shape != null && parent != null) parentByShape.put(shape, parent);
        });
        ShapeMap.setShapeMaps(shapesByParent, parentByShape);
    }
}

//?}
