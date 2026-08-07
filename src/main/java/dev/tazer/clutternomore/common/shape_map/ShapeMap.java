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
    private static final Map<Item, List<Item>> SHAPES_BY_PARENT = new HashMap<>();
    private static final Map<Item, Item> PARENT_BY_SHAPE = new HashMap<>();

    public record Mapping(Item parent, Item shape, int priority, Identifier source) {}

    public static void setShapeMaps(Map<Item, List<Item>> newShapeMap, Map<Item, Item> newInverseShapeMap) {
        SHAPES_BY_PARENT.clear();
        SHAPES_BY_PARENT.putAll(newShapeMap);
        PARENT_BY_SHAPE.clear();
        PARENT_BY_SHAPE.putAll(newInverseShapeMap);
    }

    public static Map<Item, List<Item>> shapesView() {
        return Collections.unmodifiableMap(SHAPES_BY_PARENT);
    }

    public static Map<Item, Item> inverseView() {
        return Collections.unmodifiableMap(PARENT_BY_SHAPE);
    }

    public static boolean hasShapes(Item item) {
        return SHAPES_BY_PARENT.containsKey(item);
    }

    //? if >26 {
    public static boolean isShape(ItemStackTemplate item) {
        return PARENT_BY_SHAPE.containsKey(item.item().value());
    }
    //?}
    public static boolean isShape(ItemStack item) {
        return PARENT_BY_SHAPE.containsKey(item.getItem());
    }

    public static boolean isShape(Item item) {
        return PARENT_BY_SHAPE.containsKey(item);
    }

    public static boolean contains(Item item) {
        return hasShapes(item) || isShape(item);
    }

    public static Item getParent(Item item) {
        return PARENT_BY_SHAPE.getOrDefault(item, item);
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
        return SHAPES_BY_PARENT.getOrDefault(getParent(item), List.of());
    }

    public static List<String> getSearchAliases(Item item) {
        List<Item> shapes = SHAPES_BY_PARENT.get(item);
        if (shapes == null || shapes.isEmpty()) return List.of();
        List<String> aliases = new ArrayList<>(shapes.size());
        for (Item shape : shapes) aliases.add(Component.translatable(shape.getDescriptionId()).getString());
        return aliases;
    }

    public static int currentIndex(ItemStack stack) {
        List<Item> shapes = getShapes(stack.getItem());
        return shapes.indexOf(stack.getItem());
    }

    public static ItemStack transferStack(ItemStack stack, int index) {
        List<Item> shapes = getShapes(stack.getItem());
        if (index < 0 || index >= shapes.size()) return stack.copy();
        Item target = shapes.get(index);
        //? if >1.20.4 {
        return stack.transmuteCopy(target, stack.getCount());
        //?} else {
        /*ItemStack result = new ItemStack(target, stack.getCount());
        net.minecraft.nbt.CompoundTag tag = stack.getTag();
        if (tag != null) result.setTag(tag.copy());
        return result;
        *///?}
    }

    public static void setMappings(List<Mapping> mappings, boolean detailedLogs) {
        SHAPES_BY_PARENT.clear();
        PARENT_BY_SHAPE.clear();
        if (mappings.isEmpty()) return;

        Map<Item, Item> unionFindRoots = new HashMap<>();
        Set<Item> nodes = new LinkedHashSet<>();
        for (Mapping m : mappings) {
            nodes.add(m.parent);
            nodes.add(m.shape);
            union(unionFindRoots, m.parent, m.shape);
        }

        Map<Item, List<Item>> shapeSets = new LinkedHashMap<>();
        for (Item item : nodes) {
            shapeSets.computeIfAbsent(find(unionFindRoots, item), k -> new ArrayList<>()).add(item);
        }

        Map<Item, List<Mapping>> mappingsByGroup = new HashMap<>();
        for (Mapping m : mappings) {
            mappingsByGroup.computeIfAbsent(find(unionFindRoots, m.parent), k -> new ArrayList<>()).add(m);
        }

        for (Map.Entry<Item, List<Item>> entry : shapeSets.entrySet()) {
            List<Item> members = entry.getValue();
            if (members.size() < 2) continue;
            List<Mapping> groupMappings = mappingsByGroup.getOrDefault(entry.getKey(), List.of());

            Item parent = pickParent(groupMappings);
            if (parent == null || !members.contains(parent)) parent = lexicographicallyFirst(members);

            List<Item> shapeList = new ArrayList<>();
            shapeList.add(parent);
            for (Item member : members) {
                if (member == parent) continue;
                shapeList.add(member);
                PARENT_BY_SHAPE.put(member, parent);
                //? if >1.21.9 {
                if (Platform.INSTANCE.isModLoaded("rrv") && Platform.INSTANCE.isClient()) RRVCompat.hide(member);
                //?}
            }
            SHAPES_BY_PARENT.put(parent, shapeList);

            if (detailedLogs && groupMappings.size() > members.size() - 1) {
                ClutterNoMore.LOGGER.info("[ShapeMap] circular shape set resolved: parent={} shapes={}",
                        BuiltInRegistries.ITEM.getKey(parent),
                        members.stream().map(BuiltInRegistries.ITEM::getKey).toList());
            }
        }
    }

    private static Item pickParent(List<Mapping> groupMappings) {
        Mapping best = null;
        for (Mapping m : groupMappings) {
            if (best == null
                    || m.priority > best.priority
                    || (m.priority == best.priority && m.source.compareTo(best.source) < 0)) {
                best = m;
            }
        }
        return best == null ? null : best.parent;
    }

    private static Item lexicographicallyFirst(List<Item> items) {
        Item first = items.get(0);
        Identifier firstId = BuiltInRegistries.ITEM.getKey(first);
        for (int i = 1; i < items.size(); i++) {
            Item candidate = items.get(i);
            Identifier candidateId = BuiltInRegistries.ITEM.getKey(candidate);
            if (candidateId.compareTo(firstId) < 0) {
                first = candidate;
                firstId = candidateId;
            }
        }
        return first;
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

    public static void sendShapeMap(ServerPlayer serverPlayer) {
        if (serverPlayer == null) return;
        final Map<Identifier, List<Identifier>> shapes = new HashMap<>();
        SHAPES_BY_PARENT.forEach((item, items) -> {
            List<Identifier> ids = new ArrayList<>(items.size());
            for (Item shape : items) ids.add(BuiltInRegistries.ITEM.getKey(shape));
            shapes.put(BuiltInRegistries.ITEM.getKey(item), ids);
        });
        final Map<Identifier, Identifier> inverseShapes = new HashMap<>();
        PARENT_BY_SHAPE.forEach((item, parent) ->
                inverseShapes.put(BuiltInRegistries.ITEM.getKey(item), BuiltInRegistries.ITEM.getKey(parent)));
        //? if fabric
        ServerPlayNetworking.send(serverPlayer, new ShapeMapPayload(shapes, inverseShapes));
        //? if neoforge
        //PacketDistributor.sendToPlayer(serverPlayer, new ShapeMapPayload(shapes, inverseShapes));
        //? if forge
        //ForgeNetworking.sendToPlayer(serverPlayer, new ShapeMapPacket(shapes, inverseShapes));
    }
}
