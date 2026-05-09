package dev.tazer.clutternomore.common.shape_map;

import com.google.gson.JsonElement;
//? if <1.21.2 {
/*import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
*///?}
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMapFile.ShapeMapKey;
import dev.tazer.clutternomore.common.shape_map.ShapeMapFile.ShapeMapTemplate;
//? if fabric && <1.21.9 {
/*import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
*///?}
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
//? if >1.21.2
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ShapeMapFileHandler extends SimpleJsonResourceReloadListener
//? >1.21.2
<JsonElement>
//? if fabric && <1.21.9 {
/*implements IdentifiableResourceReloadListener
*///?}
{
    //? if >1.21.2 {
    public static final FileToIdConverter CONVERTER = FileToIdConverter.json("shape_map");
    //?} else {
    /*public static final Gson GSON = new GsonBuilder().create();
    *///?}

    // Heuristic for extracting named group names from a Pattern's source — Java has no
    // public API for it before 20. Matches `(?<name>` only; ignores escapes.
    private static final Pattern NAMED_GROUP_NAME = Pattern.compile("\\(\\?<([a-zA-Z][a-zA-Z0-9]*)>");

    //? if >1.21.2 {
    public ShapeMapFileHandler() {
        super(ExtraCodecs.JSON, CONVERTER);
    }
    //?} else {
    /*public ShapeMapFileHandler() {
        super(GSON, "shape_map");
    }
    *///?}

    //? if fabric && <1.21.9 {
    /*@Override
    public Identifier getFabricId() {
        return ClutterNoMore.location("shape_map");
    }
    *///?}

    @Override
    protected void apply(Map<Identifier, JsonElement> files, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        if (!ClutterNoMore.STARTUP_CONFIG.SHAPE_MAPS.value()) {
            ShapeMap.setEdges(List.of(), false);
            return;
        }
        boolean detailed = ClutterNoMore.STARTUP_CONFIG.DETAILED_LOGS.value();

        List<Loaded> loaded = new ArrayList<>();
        for (Map.Entry<Identifier, JsonElement> entry : files.entrySet()) {
            DataResult<ShapeMapFile> parsed = ShapeMapFile.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            parsed.error().ifPresent(err -> ClutterNoMore.LOGGER.error(
                    "[ShapeMap] failed to parse '{}': {}", entry.getKey(), err.message()));
            parsed.result().ifPresent(file -> loaded.add(new Loaded(entry.getKey(), file)));
        }

        loaded.sort(Comparator
                .comparingInt((Loaded l) -> l.contents.priority())
                .thenComparing(l -> l.id.toString()));

        List<ShapeMap.Edge> edges = new ArrayList<>();
        for (Loaded l : loaded) {
            applyRules(l, edges, detailed, true);
            applyRules(l, edges, detailed, false);
        }

        ShapeMap.setEdges(edges, detailed);
    }

    private record Loaded(Identifier id, ShapeMapFile contents) {}

    private record KeyMatch(Item item, Map<String, String> vars) {}

    private void applyRules(Loaded file, List<ShapeMap.Edge> edges, boolean detailed, boolean isAdd) {
        Map<ShapeMapKey, List<ShapeMapTemplate>> rules = isAdd ? file.contents.add() : file.contents.remove();
        for (Map.Entry<ShapeMapKey, List<ShapeMapTemplate>> rule : rules.entrySet()) {
            ShapeMapKey key = rule.getKey();
            List<ShapeMapTemplate> templates = rule.getValue();

            if (isAdd) {
                if (detailed && key instanceof ShapeMapKey.Tag) {
                    for (ShapeMapTemplate t : templates) {
                        if (t.raw().startsWith("#")) {
                            ClutterNoMore.LOGGER.info("[ShapeMap] tag-on-tag rule '{}' -> '{}' produces a full cross product shape set in {}. This is not recommended and likely a mistake!",
                                    key.raw(), t.raw(), file.id);
                            break;
                        }
                    }
                }
                List<KeyMatch> matches = resolveKey(key, file.id, detailed);
                for (KeyMatch match : matches) {
                    for (ShapeMapTemplate template : templates) {
                        String resolved = template.substitute(match.vars);
                        List<Item> shapes = resolveValue(resolved, template.raw(), file.id, detailed);
                        for (Item shape : shapes) {
                            if (shape == match.item) continue;
                            edges.add(new ShapeMap.Edge(match.item, shape, file.contents.priority(), file.id));
                        }
                    }
                }
            } else {
                edges.removeIf(edge -> ruleMatchesEdge(key, templates, edge));
            }
        }
    }

    private boolean ruleMatchesEdge(ShapeMapKey key, List<ShapeMapTemplate> templates, ShapeMap.Edge edge) {
        Map<String, String> vars = matchKeyAgainst(key, edge.parent());
        if (vars == null) return false;
        for (ShapeMapTemplate template : templates) {
            String resolved = template.substitute(vars);
            if (matchesValueAgainst(resolved, edge.shape())) return true;
        }
        return false;
    }

    private Map<String, String> matchKeyAgainst(ShapeMapKey key, Item parent) {
        Identifier parentId = BuiltInRegistries.ITEM.getKey(parent);
        if (key instanceof ShapeMapKey.Literal literal) {
            return literal.id().equals(parentId) ? defaultVars(parentId) : null;
        }
        if (key instanceof ShapeMapKey.Tag tag) {
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tag.tagId());
            return BuiltInRegistries.ITEM.wrapAsHolder(parent).is(tagKey) ? defaultVars(parentId) : null;
        }
        if (key instanceof ShapeMapKey.Regex regex) {
            Matcher m = regex.pattern().matcher(parentId.toString());
            if (!m.matches()) return null;
            Map<String, String> vars = new HashMap<>();
            vars.put("ns", parentId.getNamespace());
            vars.put("path", parentId.getPath());
            for (int i = 1; i <= m.groupCount(); i++) {
                String g = m.group(i);
                if (g != null) vars.put(String.valueOf(i), g);
            }
            for (String name : namedGroupNames(regex.pattern())) {
                try {
                    String g = m.group(name);
                    if (g != null) vars.put(name, g);
                } catch (IllegalArgumentException ignored) {}
            }
            return vars;
        }
        return null;
    }

    private boolean matchesValueAgainst(String resolved, Item shape) {
        Identifier shapeId = BuiltInRegistries.ITEM.getKey(shape);
        if (resolved.length() >= 2 && resolved.startsWith("/") && resolved.endsWith("/")) {
            try {
                return Pattern.compile(resolved.substring(1, resolved.length() - 1))
                        .matcher(shapeId.toString()).matches();
            } catch (PatternSyntaxException e) {
                return false;
            }
        }
        if (resolved.startsWith("#")) {
            Identifier tagId;
            try { tagId = Identifier.parse(resolved.substring(1)); }
            catch (Exception e) { return false; }
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
            return BuiltInRegistries.ITEM.wrapAsHolder(shape).is(tagKey);
        }
        try {
            return Identifier.parse(resolved).equals(shapeId);
        } catch (Exception e) {
            return false;
        }
    }

    private List<KeyMatch> resolveKey(ShapeMapKey key, Identifier source, boolean detailed) {
        if (key instanceof ShapeMapKey.Literal literal) {
            Optional<Item> item = BuiltInRegistries.ITEM.getOptional(literal.id());
            if (item.isEmpty()) return List.of();
            return List.of(new KeyMatch(item.get(), defaultVars(literal.id())));
        }
        if (key instanceof ShapeMapKey.Tag tag) {
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tag.tagId());
            List<Item> tagged = itemsInTag(tagKey);
            if (tagged.isEmpty()) {
                if (detailed) ClutterNoMore.LOGGER.info("[ShapeMap] missing/empty tag '#{}' (requested in {})", tag.tagId(), source);
                return List.of();
            }
            List<KeyMatch> matches = new ArrayList<>(tagged.size());
            for (Item item : tagged) {
                Identifier id = BuiltInRegistries.ITEM.getKey(item);
                matches.add(new KeyMatch(item, defaultVars(id)));
            }
            return matches;
        }
        if (key instanceof ShapeMapKey.Regex regex) {
            Pattern pattern = regex.pattern();
            Set<String> names = namedGroupNames(pattern);
            List<KeyMatch> matches = new ArrayList<>();
            for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
                Identifier id = entry.getKey().identifier();
                Matcher m = pattern.matcher(id.toString());
                if (!m.matches()) continue;
                Map<String, String> vars = new HashMap<>();
                vars.put("ns", id.getNamespace());
                vars.put("path", id.getPath());
                for (int i = 1; i <= m.groupCount(); i++) {
                    String g = m.group(i);
                    if (g != null) vars.put(String.valueOf(i), g);
                }
                for (String name : names) {
                    try {
                        String g = m.group(name);
                        if (g != null) vars.put(name, g);
                    } catch (IllegalArgumentException ignored) {}
                }
                matches.add(new KeyMatch(entry.getValue(), vars));
            }
            return matches;
        }
        return List.of();
    }

    private List<Item> resolveValue(String resolved, String original, Identifier source, boolean detailed) {
        if (resolved.length() >= 2 && resolved.startsWith("/") && resolved.endsWith("/")) {
            String body = resolved.substring(1, resolved.length() - 1);
            Pattern pattern;
            try {
                pattern = Pattern.compile(body);
            } catch (PatternSyntaxException e) {
                ClutterNoMore.LOGGER.error("[ShapeMap] invalid regex value '{}' (from template '{}') in {}: {}",
                        resolved, original, source, e.getMessage());
                return List.of();
            }
            List<Item> items = new ArrayList<>();
            for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
                Identifier id = entry.getKey().identifier();
                if (pattern.matcher(id.toString()).matches()) items.add(entry.getValue());
            }
            return items;
        }

        if (resolved.startsWith("#")) {
            Identifier tagId;
            try {
                tagId = Identifier.parse(resolved.substring(1));
            } catch (Exception e) {
                ClutterNoMore.LOGGER.error("[ShapeMap] invalid tag id '{}' (from template '{}') in {}: {}",
                        resolved, original, source, e.getMessage());
                return List.of();
            }
            TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
            List<Item> tagged = itemsInTag(tagKey);
            if (tagged.isEmpty() && detailed) {
                ClutterNoMore.LOGGER.info("[ShapeMap] missing/empty tag '{}' (value in {})", resolved, source);
            }
            return tagged;
        }

        Identifier id;
        try {
            id = Identifier.parse(resolved);
        } catch (Exception e) {
            ClutterNoMore.LOGGER.error("[ShapeMap] invalid identifier '{}' (from template '{}') in {}: {}",
                    resolved, original, source, e.getMessage());
            return List.of();
        }
        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(id);
        if (item.isEmpty()) return List.of();
        return List.of(item.get());
    }

    private static List<Item> itemsInTag(TagKey<Item> tagKey) {
        List<Item> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            Holder<Item> holder = BuiltInRegistries.ITEM.wrapAsHolder(item);
            if (holder.is(tagKey)) items.add(item);
        }
        return items;
    }

    private static Map<String, String> defaultVars(Identifier id) {
        Map<String, String> vars = new HashMap<>();
        vars.put("ns", id.getNamespace());
        vars.put("path", id.getPath());
        return vars;
    }

    private static Set<String> namedGroupNames(Pattern pattern) {
        Set<String> names = new LinkedHashSet<>();
        Matcher m = NAMED_GROUP_NAME.matcher(pattern.pattern());
        while (m.find()) names.add(m.group(1));
        return names;
    }
}
