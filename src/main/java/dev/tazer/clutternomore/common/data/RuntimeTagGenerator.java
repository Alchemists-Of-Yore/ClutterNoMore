package dev.tazer.clutternomore.common.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.io.BufferedReader;
import java.util.*;
import java.util.function.Function;

public final class RuntimeTagGenerator {
    private RuntimeTagGenerator() {
    }

    public static void generate(ResourceManager manager) {
        DataGenerator.generate();
        inheritTags(manager, "tags/block", RuntimeTagGenerator::blockId, RuntimeTagGenerator::shapeBlockIds);
        inheritTags(manager, "tags/item", BuiltInRegistries.ITEM::getKey, RuntimeTagGenerator::shapeItemIds);
    }

    private static void inheritTags(ResourceManager manager, String root, Function<Item, Identifier> parentId, Function<List<Item>, List<Identifier>> shapeIds) {
        Map<Identifier, Set<Identifier>> tags = loadTags(manager, root);
        if (tags.isEmpty()) return;
        Map<Identifier, Set<Identifier>> inherited = new LinkedHashMap<>();

        for (Map.Entry<Item, List<Item>> entry : ShapeMap.shapesView().entrySet()) {
            Identifier parent = parentId.apply(entry.getKey());
            if (parent == null) continue;

            List<Identifier> shapes = shapeIds.apply(entry.getValue());
            if (shapes.isEmpty()) continue;

            for (Map.Entry<Identifier, Set<Identifier>> tag : tags.entrySet()) {
                if (contains(tag.getKey(), parent, tags, new HashSet<>())) {
                    inherited.computeIfAbsent(tag.getKey(), ignored -> new LinkedHashSet<>()).addAll(shapes);
                }
            }
        }

        inherited.forEach((tag, ids) -> writeTag(root, tag, ids));
    }

    private static Map<Identifier, Set<Identifier>> loadTags(ResourceManager manager, String root) {
        Map<Identifier, Set<Identifier>> tags = new HashMap<>();
        Map<Identifier, Resource> resources = manager.listResources(root, id -> id.getPath().endsWith(".json"));
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier file = entry.getKey();
            Identifier tag = tagId(root, file);
            if (tag == null) continue;

            try (BufferedReader reader = entry.getValue().openAsReader()) {
                JsonElement json = JsonParser.parseReader(reader);
                if (!json.isJsonObject()) continue;
                JsonArray values = json.getAsJsonObject().getAsJsonArray("values");
                if (values == null) continue;

                Set<Identifier> entries = tags.computeIfAbsent(tag, ignored -> new LinkedHashSet<>());
                for (JsonElement value : values) {
                    String id = tagValue(value);
                    if (id == null || id.isBlank()) continue;
                    entries.add(ClutterNoMore.parse(id.startsWith("#") ? id.substring(1) : id));
                }
            } catch (Exception e) {
                ClutterNoMore.LOGGER.warn("Failed to inspect tag {} for runtime shape inheritance.", file, e);
            }
        }
        return tags;
    }

    private static Identifier tagId(String root, Identifier file) {
        String path = file.getPath();
        if (!path.startsWith(root + "/") || !path.endsWith(".json")) return null;
        return ClutterNoMore.location(file.getNamespace(), path.substring(root.length() + 1, path.length() - ".json".length()));
    }

    private static String tagValue(JsonElement value) {
        if (value.isJsonPrimitive()) return value.getAsString();
        if (value.isJsonObject()) {
            JsonObject object = value.getAsJsonObject();
            if (object.has("id")) return object.get("id").getAsString();
        }
        return null;
    }

    private static boolean contains(Identifier tag, Identifier target, Map<Identifier, Set<Identifier>> tags, Set<Identifier> visiting) {
        if (!visiting.add(tag)) return false;
        Set<Identifier> values = tags.get(tag);
        if (values == null) return false;
        if (values.contains(target)) return true;

        for (Identifier value : values) {
            if (tags.containsKey(value) && contains(value, target, tags, visiting)) return true;
        }
        return false;
    }

    private static void writeTag(String root, Identifier tag, Collection<Identifier> ids) {
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);
        JsonArray values = new JsonArray();
        for (Identifier id : ids) {
            JsonObject entry = new JsonObject();
            entry.addProperty("id", id.toString());
            entry.addProperty("required", false);
            values.add(entry);
        }
        json.add("values", values);
        ClutterNoMore.RESOURCES.addJson(PackType.SERVER_DATA, ClutterNoMore.location(tag.getNamespace(), root + "/" + tag.getPath() + ".json"), json);
    }

    private static Identifier blockId(Item item) {
        if (!(item instanceof BlockItem blockItem)) return null;
        return BuiltInRegistries.BLOCK.getKey(blockItem.getBlock());
    }

    private static List<Identifier> shapeItemIds(List<Item> shapeSet) {
        List<Identifier> ids = new ArrayList<>();
        for (int i = 1; i < shapeSet.size(); i++) {
            ids.add(BuiltInRegistries.ITEM.getKey(shapeSet.get(i)));
        }
        return ids;
    }

    private static List<Identifier> shapeBlockIds(List<Item> shapeSet) {
        List<Identifier> ids = new ArrayList<>();
        for (int i = 1; i < shapeSet.size(); i++) {
            Item shape = shapeSet.get(i);
            if (shape instanceof BlockItem blockItem) {
                Block block = blockItem.getBlock();
                ids.add(BuiltInRegistries.BLOCK.getKey(block));
            }
        }
        return ids;
    }
}
