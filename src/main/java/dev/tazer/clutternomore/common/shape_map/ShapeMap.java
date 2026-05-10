package dev.tazer.clutternomore.common.shape_map;

//? if >1.21.4 {
import dev.tazer.clutternomore.Platform;
import dev.tazer.clutternomore.common.compat.RRVCompat;
//?}
//? if fabric || neoforge {
import dev.tazer.clutternomore.common.networking.ShapeMapPayload;
//?}
//? if fabric {
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
//?}
import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if >26
import net.minecraft.world.item.ItemStackTemplate;
//? if neoforge {
/*import net.neoforged.neoforge.network.PacketDistributor;
*///?}
//? if forge {
/*import dev.tazer.clutternomore.forge.networking.ForgeNetworking;
import dev.tazer.clutternomore.forge.networking.ShapeMapPacket;
*///?}


import java.util.*;

public class ShapeMap {
    private static final Map<Item, List<Item>> SHAPES_DATAMAP = new HashMap<>();
    private static final Map<Item, Item> INVERSE_SHAPES_DATAMAP = new HashMap<>();

    public record Edge(Item parent, Item shape, int priority, Identifier source) {}

    public static void setShapeMaps(Map<Item, List<Item>> newShapeMap, Map<Item, Item> newInverseShapeMap) {
        SHAPES_DATAMAP.clear();
        SHAPES_DATAMAP.putAll(newShapeMap);
        INVERSE_SHAPES_DATAMAP.clear();
        INVERSE_SHAPES_DATAMAP.putAll(newInverseShapeMap);
    }

    public static Map<Item, List<Item>> shapesView() {
        return Collections.unmodifiableMap(SHAPES_DATAMAP);
    }

    public static Map<Item, Item> inverseView() {
        return Collections.unmodifiableMap(INVERSE_SHAPES_DATAMAP);
    }

    public static boolean hasShapes(Item item) {
        return SHAPES_DATAMAP.containsKey(item);
    }

    //? if >26 {
    public static boolean isShape(ItemStackTemplate item) {
        return INVERSE_SHAPES_DATAMAP.containsKey(item.item().value());
    }
    //?}
    public static boolean isShape(ItemStack item) {
        return INVERSE_SHAPES_DATAMAP.containsKey(item.getItem());
    }

    public static boolean isShape(Item item) {
        return INVERSE_SHAPES_DATAMAP.containsKey(item);
    }

    public static boolean contains(Item item) {
        return hasShapes(item) || isShape(item);
    }

    public static Item getParent(Item item) {
        return INVERSE_SHAPES_DATAMAP.getOrDefault(item, item);
    }

    public static boolean isParentOfShape(Item parent, Item shape) {
        return getParent(shape) == parent;
    }

    public static boolean inSameShapeSet(Item item, Item other) {
        if (isShape(item) || isShape(other))
            return (getParent(item) == getParent(other));
        return false;
    }

    public static List<Item> getShapes(Item item) {
        return SHAPES_DATAMAP.getOrDefault(getParent(item), List.of());
    }

    public static List<String> getSearchAliases(Item item) {
        List<Item> shapes = SHAPES_DATAMAP.get(item);
        if (shapes == null || shapes.isEmpty()) return List.of();
        List<String> aliases = new ArrayList<>(shapes.size());
        for (Item shape : shapes) aliases.add(Component.translatable(shape.getDescriptionId()).getString());
        return aliases;
    }

    public static void setEdges(List<Edge> edges, boolean detailedLogs) {
        SHAPES_DATAMAP.clear();
        INVERSE_SHAPES_DATAMAP.clear();
        if (edges.isEmpty()) return;

        Map<Item, Item> ufParent = new HashMap<>();
        Set<Item> nodes = new LinkedHashSet<>();
        for (Edge e : edges) {
            nodes.add(e.parent);
            nodes.add(e.shape);
            union(ufParent, e.parent, e.shape);
        }

        Map<Item, List<Item>> shapeSets = new LinkedHashMap<>();
        for (Item item : nodes) {
            shapeSets.computeIfAbsent(find(ufParent, item), k -> new ArrayList<>()).add(item);
        }

        Map<Item, List<Edge>> edgesByShapeSet = new HashMap<>();
        for (Edge e : edges) {
            edgesByShapeSet.computeIfAbsent(find(ufParent, e.parent), k -> new ArrayList<>()).add(e);
        }

        for (Map.Entry<Item, List<Item>> entry : shapeSets.entrySet()) {
            List<Item> shapeSet = entry.getValue();
            if (shapeSet.size() < 2) continue;
            List<Edge> setEdges = edgesByShapeSet.getOrDefault(entry.getKey(), List.of());

            Item parent = pickParent(setEdges, shapeSet);
            if (parent == null || !shapeSet.contains(parent)) parent = lexSmallest(shapeSet);

            List<Item> shapes = new ArrayList<>(shapeSet.size() - 1);
            for (Item m : shapeSet) {
                if (m == parent) continue;
                shapes.add(m);
                INVERSE_SHAPES_DATAMAP.put(m, parent);
                //? if >1.21.9 {
                if (Platform.INSTANCE.isModLoaded("rrv")) RRVCompat.hide(m);
                //?}
            }
            SHAPES_DATAMAP.put(parent, shapes);

            if (detailedLogs && setEdges.size() > shapeSet.size() - 1) {
                ClutterNoMore.LOGGER.info("[ShapeMap] circular shape set resolved: parent={} shapes={}",
                        BuiltInRegistries.ITEM.getKey(parent),
                        shapeSet.stream().map(BuiltInRegistries.ITEM::getKey).toList());
            }
        }
    }

    private static Item pickParent(List<Edge> setEdges, List<Item> shapeSet) {
        Edge best = null;
        for (Edge e : setEdges) {
            if (best == null
                    || e.priority > best.priority
                    || (e.priority == best.priority && e.source.compareTo(best.source) < 0)) {
                best = e;
            }
        }
        return best == null ? null : best.parent;
    }

    private static Item lexSmallest(List<Item> items) {
        Item smallest = items.get(0);
        Identifier smallestId = BuiltInRegistries.ITEM.getKey(smallest);
        for (int i = 1; i < items.size(); i++) {
            Item candidate = items.get(i);
            Identifier candidateId = BuiltInRegistries.ITEM.getKey(candidate);
            if (candidateId.compareTo(smallestId) < 0) {
                smallest = candidate;
                smallestId = candidateId;
            }
        }
        return smallest;
    }

    private static <T> T find(Map<T, T> parent, T x) {
        T p = parent.getOrDefault(x, x);
        if (p.equals(x)) return x;
        T root = find(parent, p);
        parent.put(x, root);
        return root;
    }

    private static <T> void union(Map<T, T> parent, T a, T b) {
        T ra = find(parent, a);
        T rb = find(parent, b);
        if (!ra.equals(rb)) parent.put(ra, rb);
    }

    public static ItemStack transferStack(ItemStack from, Item toItem) {
        //? if >1.20.4 {
        return from.transmuteCopy(toItem, from.getCount());
        //?} else {
        /*ItemStack result = new ItemStack(toItem, from.getCount());
        net.minecraft.nbt.CompoundTag tag = from.getTag();
        if (tag != null) result.setTag(tag.copy());
        return result;
        *///?}
    }

    public static void sendShapeMap(ServerPlayer serverPlayer) {
        if (serverPlayer == null) return;
        final Map<Identifier, List<Identifier>> shapes = new HashMap<>();
        SHAPES_DATAMAP.forEach((item, items) -> {
            List<Identifier> ids = new ArrayList<>(items.size());
            for (Item shape : items) ids.add(BuiltInRegistries.ITEM.getKey(shape));
            shapes.put(BuiltInRegistries.ITEM.getKey(item), ids);
        });
        final Map<Identifier, Identifier> inverseShapes = new HashMap<>();
        INVERSE_SHAPES_DATAMAP.forEach((item, parent) ->
                inverseShapes.put(BuiltInRegistries.ITEM.getKey(item), BuiltInRegistries.ITEM.getKey(parent)));
        //? if fabric
        ServerPlayNetworking.send(serverPlayer, new ShapeMapPayload(shapes, inverseShapes));
        //? if neoforge
        //PacketDistributor.sendToPlayer(serverPlayer, new ShapeMapPayload(shapes, inverseShapes));
        //? if forge
        //ForgeNetworking.sendToPlayer(serverPlayer, new ShapeMapPacket(shapes, inverseShapes));
    }
}
