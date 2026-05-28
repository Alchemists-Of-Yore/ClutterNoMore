package dev.tazer.clutternomore.common.shape_map;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tazer.clutternomore.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public record ShapeMapFile(
        int priority,
        Map<ShapeMapFile.ShapeMapKey, ShapeMapFile.ConditionalRule> add,
        Map<ShapeMapFile.ShapeMapKey, ShapeMapFile.ConditionalRule> remove
) {

    public static final int DEFAULT_PRIORITY = 1000;

    private static final Codec<Map<ShapeMapKey, ConditionalRule>> RULES_CODEC =
            Codec.unboundedMap(ShapeMapKey.CODEC, ConditionalRule.CODEC);

    public static final Codec<ShapeMapFile> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(1, Integer.MAX_VALUE).optionalFieldOf("priority", DEFAULT_PRIORITY).forGetter(ShapeMapFile::priority),
            RULES_CODEC.optionalFieldOf("add", Map.of()).forGetter(ShapeMapFile::add),
            RULES_CODEC.optionalFieldOf("remove", Map.of()).forGetter(ShapeMapFile::remove)
    ).apply(instance, ShapeMapFile::new));

    public sealed interface ShapeMapKey permits ShapeMapKey.Literal, ShapeMapKey.Tag, ShapeMapKey.Regex {
        String raw();

        Codec<ShapeMapKey> CODEC = Codec.STRING.flatXmap(ShapeMapKey::parse, key -> DataResult.success(key.raw()));

        static DataResult<ShapeMapKey> parse(String raw) {
            String[] error = { null };
            Selector sel = parseSelector(raw, msg -> error[0] = msg);
            if (sel == null) return DataResult.error(() -> "Invalid shape map key '" + raw + "': " + error[0]);
            if (sel instanceof Selector.Literal l) return DataResult.success(new Literal(l.id()));
            if (sel instanceof Selector.Tag t) return DataResult.success(new Tag(t.tagKey().location()));
            if (sel instanceof Selector.Regex r) return DataResult.success(new Regex(r.pattern()));
            return DataResult.error(() -> "Unreachable selector type for '" + raw + "'");
        }

        record Literal(Identifier id) implements ShapeMapKey {
            @Override public String raw() { return id.toString(); }
        }

        record Tag(Identifier tagId) implements ShapeMapKey {
            @Override public String raw() { return "#" + tagId; }
        }

        record Regex(Pattern pattern) implements ShapeMapKey {
            @Override public String raw() { return "/" + pattern.pattern() + "/"; }
        }
    }

    public record ShapeMapTemplate(String raw) {
        public static final Codec<ShapeMapTemplate> CODEC = Codec.STRING.flatXmap(
                ShapeMapTemplate::validate,
                template -> DataResult.success(template.raw)
        );

        private static DataResult<ShapeMapTemplate> validate(String raw) {
            int i = 0;
            while ((i = raw.indexOf("${", i)) != -1) {
                int end = raw.indexOf('}', i);
                if (end == -1) return DataResult.error(() -> "Unclosed ${ in shape map template '" + raw + "'");
                i = end + 1;
            }
            return DataResult.success(new ShapeMapTemplate(raw));
        }

        public String substitute(Map<String, String> vars) {
            if (!raw.contains("${")) return raw;
            StringBuilder sb = new StringBuilder(raw.length());
            int i = 0;
            while (i < raw.length()) {
                int dollar = raw.indexOf("${", i);
                if (dollar < 0) {
                    sb.append(raw, i, raw.length());
                    break;
                }
                sb.append(raw, i, dollar);
                int end = raw.indexOf('}', dollar);
                String name = raw.substring(dollar + 2, end);
                sb.append(vars.getOrDefault(name, ""));
                i = end + 1;
            }
            return sb.toString();
        }
    }

    public record Conditions(List<ShapeMapTemplate> exists,
                             List<String> modLoaded,
                             boolean isBlockItem,
                             List<Conditions> allOf,
                             List<Conditions> anyOf,
                             List<Conditions> noneOf,
                             Optional<Conditions> not) {

        private static final Codec<Boolean> IS_BLOCKITEM_CODEC = Codec.BOOL.flatXmap(
                value -> value ? DataResult.success(true)
                               : DataResult.error(() -> "is_blockitem must be `true` (use `not` to invert)"),
                DataResult::success
        );

        public static final Codec<Conditions> CODEC = new Codec<>() {
            @Override
            public <T> DataResult<Pair<Conditions, T>> decode(DynamicOps<T> ops, T input) {
                return DELEGATE.decode(ops, input);
            }

            @Override
            public <T> DataResult<T> encode(Conditions input, DynamicOps<T> ops, T prefix) {
                return DELEGATE.encode(input, ops, prefix);
            }
        };

        private static final Codec<Conditions> DELEGATE = RecordCodecBuilder.create(instance -> instance.group(
                ShapeMapTemplate.CODEC.listOf().optionalFieldOf("exists", List.of()).forGetter(Conditions::exists),
                Codec.STRING.listOf().optionalFieldOf("mod_loaded", List.of()).forGetter(Conditions::modLoaded),
                IS_BLOCKITEM_CODEC.optionalFieldOf("is_blockitem", false).forGetter(Conditions::isBlockItem),
                CODEC.listOf().optionalFieldOf("all_of", List.of()).forGetter(Conditions::allOf),
                CODEC.listOf().optionalFieldOf("any_of", List.of()).forGetter(Conditions::anyOf),
                CODEC.listOf().optionalFieldOf("none_of", List.of()).forGetter(Conditions::noneOf),
                CODEC.optionalFieldOf("not").forGetter(Conditions::not)
        ).apply(instance, Conditions::new));

        public boolean evaluate(Item parent, Map<String, String> vars, ScanIndex index) {
            for (ShapeMapTemplate template : exists) {
                if (!anyMatch(template.substitute(vars), index)) return false;
            }
            for (String mod : modLoaded) {
                if (!Platform.INSTANCE.isModLoaded(mod)) return false;
            }
            if (isBlockItem && !(parent instanceof BlockItem)) return false;
            for (Conditions condition : allOf) {
                if (!condition.evaluate(parent, vars, index)) return false;
            }
            if (!anyOf.isEmpty()) {
                boolean any = false;
                for (Conditions condition : anyOf) {
                    if (condition.evaluate(parent, vars, index)) {
                        any = true;
                        break;
                    }
                }
                if (!any) return false;
            }
            for (Conditions condition : noneOf) {
                if (condition.evaluate(parent, vars, index)) return false;
            }
            if (not.isPresent() && not.get().evaluate(parent, vars, index)) return false;
            return true;
        }

    }

    public record ScanIndex(List<Item> items, Identifier[] identifiers, String[] ids, Map<String, List<Item>> byPath) {
            private static ScanIndex cached;

        public static ScanIndex get() {
                ScanIndex local = cached;
                if (local == null || local.items.size() != BuiltInRegistries.ITEM.size()) {
                    local = build();
                    cached = local;
                }
                return local;
            }

            private static ScanIndex build() {
                List<Item> items = new ArrayList<>();
                for (Item item : BuiltInRegistries.ITEM) items.add(item);
                int n = items.size();
                Identifier[] identifiers = new Identifier[n];
                String[] ids = new String[n];
                Map<String, List<Item>> byPath = new HashMap<>();
                for (int i = 0; i < n; i++) {
                    Item item = items.get(i);
                    Identifier id = BuiltInRegistries.ITEM.getKey(item);
                    identifiers[i] = id;
                    ids[i] = id.toString();
                    byPath.computeIfAbsent(id.getPath(), k -> new ArrayList<>()).add(item);
                }
                return new ScanIndex(items, identifiers, ids, byPath);
            }
        }

    private static String literalPathAnchor(String regexBody) {
        int colon = regexBody.lastIndexOf(':');
        if (colon < 0 || colon == regexBody.length() - 1) return null;
        for (int i = colon + 1; i < regexBody.length(); i++) {
            char c = regexBody.charAt(i);
            boolean literal = (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '_' || c == '-' || c == '/';
            if (!literal) return null;
        }
        return regexBody.substring(colon + 1);
    }

    public sealed interface Selector permits Selector.Regex, Selector.Tag, Selector.Literal {
        record Regex(Pattern pattern) implements Selector {}
        record Tag(TagKey<Item> tagKey) implements Selector {}
        record Literal(Identifier id) implements Selector {}
    }

    public static Selector parseSelector(String resolved, Consumer<String> onError) {
        if (resolved.length() >= 2 && resolved.startsWith("/") && resolved.endsWith("/")) {
            try {
                return new Selector.Regex(Pattern.compile(resolved.substring(1, resolved.length() - 1)));
            } catch (PatternSyntaxException e) {
                if (onError != null) onError.accept("invalid regex '" + resolved + "': " + e.getMessage());
                return null;
            }
        }
        if (resolved.startsWith("#")) {
            try {
                return new Selector.Tag(TagKey.create(Registries.ITEM, Identifier.parse(resolved.substring(1))));
            } catch (Exception e) {
                if (onError != null) onError.accept("invalid tag id '" + resolved + "': " + e.getMessage());
                return null;
            }
        }
        try {
            return new Selector.Literal(Identifier.parse(resolved));
        } catch (Exception e) {
            if (onError != null) onError.accept("invalid identifier '" + resolved + "': " + e.getMessage());
            return null;
        }
    }

    public static boolean matches(Selector selector, Item item) {
        if (selector == null) return false;
        if (selector instanceof Selector.Regex r) {
            return r.pattern().matcher(BuiltInRegistries.ITEM.getKey(item).toString()).matches();
        }
        if (selector instanceof Selector.Tag t) {
            return BuiltInRegistries.ITEM.wrapAsHolder(item).is(t.tagKey());
        }
        if (selector instanceof Selector.Literal l) {
            return BuiltInRegistries.ITEM.getKey(item).equals(l.id());
        }
        return false;
    }

    public static boolean matches(String resolved, Item item) {
        return matches(parseSelector(resolved, null), item);
    }

    public static List<Item> resolveItems(String resolved, Consumer<String> onError) {
        return resolveItems(resolved, onError, null);
    }

    public static List<Item> resolveItems(String resolved, Consumer<String> onError, ScanIndex index) {
        Selector selector = parseSelector(resolved, onError);
        if (selector == null) return List.of();
        if (selector instanceof Selector.Literal l) {
            return BuiltInRegistries.ITEM.getOptional(l.id()).map(List::of).orElse(List.of());
        }
        if (index != null && selector instanceof Selector.Regex r) {
            Pattern pattern = r.pattern();
            String anchor = literalPathAnchor(pattern.pattern());
            if (anchor != null) {
                List<Item> candidates = index.byPath.get(anchor);
                if (candidates == null) return List.of();
                List<Item> result = new ArrayList<>();
                for (Item item : candidates) {
                    if (pattern.matcher(BuiltInRegistries.ITEM.getKey(item).toString()).matches()) result.add(item);
                }
                return result;
            }
            List<Item> result = new ArrayList<>();
            for (int i = 0; i < index.ids.length; i++) {
                if (pattern.matcher(index.ids[i]).matches()) result.add(index.items.get(i));
            }
            return result;
        }
        List<Item> items = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            if (matches(selector, item)) items.add(item);
        }
        return items;
    }

    public static boolean anyMatch(String resolved, ScanIndex index) {
        Selector selector = parseSelector(resolved, null);
        if (selector == null) return false;
        if (selector instanceof Selector.Literal l) {
            return BuiltInRegistries.ITEM.containsKey(l.id());
        }
        if (index != null && selector instanceof Selector.Regex r) {
            Pattern pattern = r.pattern();
            String anchor = literalPathAnchor(pattern.pattern());
            if (anchor != null) {
                List<Item> candidates = index.byPath.get(anchor);
                if (candidates == null) return false;
                for (Item item : candidates) {
                    if (pattern.matcher(BuiltInRegistries.ITEM.getKey(item).toString()).matches()) return true;
                }
                return false;
            }
            for (String id : index.ids) {
                if (pattern.matcher(id).matches()) return true;
            }
            return false;
        }
        for (Item item : BuiltInRegistries.ITEM) {
            if (matches(selector, item)) return true;
        }
        return false;
    }

    public record ConditionalRule(List<ShapeMapTemplate> shapes, Optional<Conditions> conditions) {

        private static final Codec<ConditionalRule> OBJECT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ShapeMapTemplate.CODEC.listOf().fieldOf("shapes").forGetter(ConditionalRule::shapes),
                Conditions.CODEC.optionalFieldOf("conditions").forGetter(ConditionalRule::conditions)
        ).apply(instance, ConditionalRule::new));

        public static final Codec<ConditionalRule> CODEC = Codec.either(
                ShapeMapTemplate.CODEC.listOf(),
                OBJECT_CODEC
        ).xmap(
                either -> either.map(
                        list -> new ConditionalRule(list, Optional.empty()),
                        rule -> rule
                ),
                rule -> rule.conditions.isEmpty()
                        ? Either.left(rule.shapes)
                        : Either.right(rule)
        );
    }
}
