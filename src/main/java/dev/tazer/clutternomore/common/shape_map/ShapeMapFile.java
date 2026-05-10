package dev.tazer.clutternomore.common.shape_map;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tazer.clutternomore.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public record ShapeMapFile(int priority, Map<ShapeMapFile.ShapeMapKey, ShapeMapFile.ConditionalRule> add, Map<ShapeMapFile.ShapeMapKey, ShapeMapFile.ConditionalRule> remove) {

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
            if (raw.startsWith("#")) {
                String body = raw.substring(1);
                try {
                    return DataResult.success(new Tag(Identifier.parse(body)));
                } catch (Exception e) {
                    return DataResult.error(() -> "Invalid tag id in shape map key '" + raw + "': " + e.getMessage());
                }
            }
            if (raw.length() >= 2 && raw.startsWith("/") && raw.endsWith("/")) {
                String body = raw.substring(1, raw.length() - 1);
                try {
                    return DataResult.success(new Regex(Pattern.compile(body)));
                } catch (PatternSyntaxException e) {
                    return DataResult.error(() -> "Invalid regex in shape map key '" + raw + "': " + e.getMessage());
                }
            }
            try {
                return DataResult.success(new Literal(Identifier.parse(raw)));
            } catch (Exception e) {
                return DataResult.error(() -> "Invalid identifier in shape map key '" + raw + "': " + e.getMessage());
            }
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
                t -> DataResult.success(t.raw)
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
                b -> b ? DataResult.success(true)
                       : DataResult.error(() -> "is_blockitem must be `true` (use `not` to invert)"),
                DataResult::success
        );

        public static final Codec<Conditions> CODEC = Codec.recursive("Conditions", self ->
                RecordCodecBuilder.create(instance -> instance.group(
                        ShapeMapTemplate.CODEC.listOf().optionalFieldOf("exists", List.of()).forGetter(Conditions::exists),
                        Codec.STRING.listOf().optionalFieldOf("mod_loaded", List.of()).forGetter(Conditions::modLoaded),
                        IS_BLOCKITEM_CODEC.optionalFieldOf("is_blockitem", false).forGetter(Conditions::isBlockItem),
                        self.listOf().optionalFieldOf("all_of", List.of()).forGetter(Conditions::allOf),
                        self.listOf().optionalFieldOf("any_of", List.of()).forGetter(Conditions::anyOf),
                        self.listOf().optionalFieldOf("none_of", List.of()).forGetter(Conditions::noneOf),
                        self.optionalFieldOf("not").forGetter(Conditions::not)
                ).apply(instance, Conditions::new))
        );

        public boolean evaluate(Item parent, Map<String, String> vars) {
            for (ShapeMapTemplate t : exists) {
                String resolved = t.substitute(vars);
                try {
                    if (!BuiltInRegistries.ITEM.containsKey(Identifier.parse(resolved))) return false;
                } catch (Exception e) {
                    return false;
                }
            }
            for (String mod : modLoaded) {
                if (!Platform.INSTANCE.isModLoaded(mod)) return false;
            }
            if (isBlockItem && !(parent instanceof BlockItem)) return false;
            for (Conditions c : allOf) {
                if (!c.evaluate(parent, vars)) return false;
            }
            if (!anyOf.isEmpty()) {
                boolean any = false;
                for (Conditions c : anyOf) if (c.evaluate(parent, vars)) { any = true; break; }
                if (!any) return false;
            }
            for (Conditions c : noneOf) {
                if (c.evaluate(parent, vars)) return false;
            }
            if (not.isPresent() && not.get().evaluate(parent, vars)) return false;
            return true;
        }
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
