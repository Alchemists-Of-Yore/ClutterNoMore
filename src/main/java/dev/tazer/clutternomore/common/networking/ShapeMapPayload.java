package dev.tazer.clutternomore.common.networking;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
//? if fabric
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if neoforge
/*import net.neoforged.neoforge.network.handling.IPayloadContext;*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ShapeMapPayload(Map<ItemStack, List<ItemStack>> shapes, Map<ItemStack, ItemStack> inverseShapes) implements CustomPacketPayload {
    public static final Type<ShapeMapPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(ClutterNoMore.MODID, "shapes"));

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<ItemStack, List<ItemStack>>> SHAPE_MAP_CODEC = ByteBufCodecs.map(
            HashMap::new, ItemStack.STREAM_CODEC, ItemStack.OPTIONAL_LIST_STREAM_CODEC, BuiltInRegistries.ITEM.size()
    );

    private static final StreamCodec<RegistryFriendlyByteBuf, Map<ItemStack, ItemStack>> INVERSE_SHAPE_MAP_CODEC = ByteBufCodecs.map(
            HashMap::new, ItemStack.STREAM_CODEC, ItemStack.STREAM_CODEC, BuiltInRegistries.ITEM.size()
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
                                          /*IPayloadContext*/
                                          //? if forge
                                          /*Object*/
                                          context) {
        final Map<Item, List<Item>> SHAPES_DATAMAP = new HashMap<>();
        data.shapes.forEach(((item, items) -> {
            ArrayList<Item> objects = new ArrayList<>();
            items.forEach((stack -> objects.add(stack.getItem())));
            SHAPES_DATAMAP.put(item.getItem(), objects);
        }));
        final Map<Item, Item> INVERSE_SHAPES_DATAMAP = new HashMap<>();
        data.inverseShapes.forEach(((item, items) -> {
            INVERSE_SHAPES_DATAMAP.put(item.getItem(), items.getItem());
        }));
        ShapeMap.setShapeMaps(SHAPES_DATAMAP, INVERSE_SHAPES_DATAMAP);
    }
}
