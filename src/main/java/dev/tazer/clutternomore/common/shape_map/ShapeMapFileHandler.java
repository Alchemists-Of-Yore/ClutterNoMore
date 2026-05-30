package dev.tazer.clutternomore.common.shape_map;

import com.google.gson.JsonElement;
//? if <1.21.2 {
/*import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
*///?}
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMapFile.ConditionalRule;
import dev.tazer.clutternomore.common.shape_map.ShapeMapFile.Conditions;
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
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ShapeMapFileHandler
//? if >1.21.2 {
extends SimpleJsonResourceReloadListener<JsonElement>
//?} else
//extends SimpleJsonResourceReloadListener
//? if fabric && <1.21.9 {
/*implements IdentifiableResourceReloadListener
*///?}
{
    //? if >1.21.2 {
    public static final FileToIdConverter CONVERTER = FileToIdConverter.json("shape_map");
    //?} else {
    /*public static final Gson GSON = new GsonBuilder().create();
    *///?}

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
            ShapeMap.setMappings(List.of(), false);
            return;
        }
        boolean detailed = ClutterNoMore.STARTUP_CONFIG.DETAILED_LOGS.value();

        List<LoadedFile> loadedFiles = new ArrayList<>();
        for (Map.Entry<Identifier, JsonElement> entry : files.entrySet()) {
            DataResult<ShapeMapFile> parsed = ShapeMapFile.CODEC.parse(JsonOps.INSTANCE, entry.getValue());
            parsed.error().ifPresent(err -> ClutterNoMore.LOGGER.error(
                    "[ShapeMap] failed to parse '{}': {}", entry.getKey(), err.message()));
            parsed.result().ifPresent(file -> loadedFiles.add(new LoadedFile(entry.getKey(), file)));
        }

        loadedFiles.sort(Comparator
                .comparingInt((LoadedFile l) -> l.contents.priority())
                .thenComparing(l -> l.id.toString()));

        ShapeMapFile.ScanIndex index = ShapeMapFile.ScanIndex.get();
        List<ShapeMap.Mapping> mappings = new ArrayList<>();
        for (LoadedFile file : loadedFiles) {
            applyRules(file, mappings, detailed, true, index);
            applyRules(file, mappings, detailed, false, index);
        }

        ShapeMap.setMappings(mappings, detailed);
    }

    private record LoadedFile(Identifier id, ShapeMapFile contents) {}

    private record KeyMatch(Item item, Map<String, String> vars) {}

    private void applyRules(LoadedFile file, List<ShapeMap.Mapping> mappings, boolean detailed, boolean isAdd, ShapeMapFile.ScanIndex index) {
        Map<ShapeMapKey, ConditionalRule> rules = isAdd ? file.contents.add() : file.contents.remove();
        for (Map.Entry<ShapeMapKey, ConditionalRule> rule : rules.entrySet()) {
            ShapeMapKey key = rule.getKey();
            ConditionalRule body = rule.getValue();
            List<ShapeMapTemplate> templates = body.shapes();
            Conditions conditions = body.conditions().orElse(null);

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
                List<KeyMatch> matches = resolveKey(key, file.id, detailed, index);
                for (KeyMatch match : matches) {
                    if (conditions != null && !conditions.evaluate(match.item, match.vars, index)) continue;
                    for (ShapeMapTemplate template : templates) {
                        String resolved = template.substitute(match.vars);
                        List<Item> shapes = resolveValue(resolved, template.raw(), file.id, detailed, index);
                        for (Item shape : shapes) {
                            if (shape == match.item) continue;
                            mappings.add(new ShapeMap.Mapping(match.item, shape, file.contents.priority(), file.id));
                        }
                    }
                }
            } else {
                Map<Item, List<Item>> parentsByShape = buildParentsByShape(mappings);
                ShapeMapFile.Selector[] valueSelectors = precomputeConstantSelectors(templates);
                mappings.removeIf(mapping -> ruleMatchesMapping(key, templates, valueSelectors, conditions, mapping, index, parentsByShape));
            }
        }
    }

    private boolean ruleMatchesMapping(ShapeMapKey key, List<ShapeMapTemplate> templates, ShapeMapFile.Selector[] valueSelectors,
                                       Conditions conditions, ShapeMap.Mapping mapping, ShapeMapFile.ScanIndex index,
                                       Map<Item, List<Item>> parentsByShape) {
        Item parent = mapping.parent();
        Item shape = mapping.shape();
        if (candidateMatches(key, templates, valueSelectors, conditions, parent, shape, index)) return true;

        List<Item> immediate = parentsByShape.get(parent);
        if (immediate == null) return false;
        List<Item> queue = new ArrayList<>(immediate);
        Set<Item> visited = new HashSet<>();
        visited.add(parent);
        for (int i = 0; i < queue.size(); i++) {
            Item candidate = queue.get(i);
            if (!visited.add(candidate)) continue;
            if (candidateMatches(key, templates, valueSelectors, conditions, candidate, shape, index)) return true;
            List<Item> up = parentsByShape.get(candidate);
            if (up != null) queue.addAll(up);
        }
        return false;
    }

    private boolean candidateMatches(ShapeMapKey key, List<ShapeMapTemplate> templates, ShapeMapFile.Selector[] valueSelectors,
                                     Conditions conditions, Item candidate, Item shape, ShapeMapFile.ScanIndex index) {
        Map<String, String> vars = matchKeyAgainst(key, candidate);
        if (vars == null) return false;
        if (conditions != null && !conditions.evaluate(candidate, vars, index)) return false;
        for (int i = 0; i < templates.size(); i++) {
            ShapeMapFile.Selector selector = valueSelectors[i];
            if (selector == null) selector = ShapeMapFile.parseSelector(templates.get(i).substitute(vars), null);
            if (ShapeMapFile.matches(selector, shape)) return true;
        }
        return false;
    }

    private static Map<Item, List<Item>> buildParentsByShape(List<ShapeMap.Mapping> mappings) {
        Map<Item, List<Item>> parentsByShape = new HashMap<>();
        for (ShapeMap.Mapping mapping : mappings) {
            parentsByShape.computeIfAbsent(mapping.shape(), k -> new ArrayList<>()).add(mapping.parent());
        }
        return parentsByShape;
    }

    private static ShapeMapFile.Selector[] precomputeConstantSelectors(List<ShapeMapTemplate> templates) {
        ShapeMapFile.Selector[] selectors = new ShapeMapFile.Selector[templates.size()];
        for (int i = 0; i < templates.size(); i++) {
            String raw = templates.get(i).raw();
            if (!raw.contains("${")) selectors[i] = ShapeMapFile.parseSelector(raw, null);
        }
        return selectors;
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

    private List<KeyMatch> resolveKey(ShapeMapKey key, Identifier source, boolean detailed, ShapeMapFile.ScanIndex index) {
        if (key instanceof ShapeMapKey.Literal literal) {
            Optional<Item> item = BuiltInRegistries.ITEM.getOptional(literal.id());
            return item.map(value -> List.of(new KeyMatch(value, defaultVars(literal.id())))).orElseGet(List::of);
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
            for (int i = 0; i < index.ids().length; i++) {
                Matcher m = pattern.matcher(index.ids()[i]);
                if (!m.matches()) continue;
                Identifier id = index.identifiers()[i];
                Map<String, String> vars = new HashMap<>();
                vars.put("ns", id.getNamespace());
                vars.put("path", id.getPath());
                for (int g = 1; g <= m.groupCount(); g++) {
                    String captured = m.group(g);
                    if (captured != null) vars.put(String.valueOf(g), captured);
                }
                for (String name : names) {
                    try {
                        String captured = m.group(name);
                        if (captured != null) vars.put(name, captured);
                    } catch (IllegalArgumentException ignored) {}
                }
                matches.add(new KeyMatch(index.items().get(i), vars));
            }
            return matches;
        }
        return List.of();
    }

    private List<Item> resolveValue(String resolved, String original, Identifier source, boolean detailed, ShapeMapFile.ScanIndex index) {
        List<Item> items = ShapeMapFile.resolveItems(resolved, msg ->
                ClutterNoMore.LOGGER.error("[ShapeMap] {} (from template '{}') in {}", msg, original, source), index);
        if (detailed && resolved.startsWith("#") && items.isEmpty()) {
            ClutterNoMore.LOGGER.info("[ShapeMap] missing/empty tag '{}' (value in {})", resolved, source);
        }
        return items;
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
