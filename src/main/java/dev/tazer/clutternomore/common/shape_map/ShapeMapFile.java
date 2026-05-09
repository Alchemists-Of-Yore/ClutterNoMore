package dev.tazer.clutternomore.common.shape_map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public record ShapeMapFile(int priority, Map<ShapeMapKey, List<ShapeMapTemplate>> add, Map<ShapeMapKey, List<ShapeMapTemplate>> remove) {

    public static final int DEFAULT_PRIORITY = 1000;

    private static final Codec<Map<ShapeMapKey, List<ShapeMapTemplate>>> RULES_CODEC =
            Codec.unboundedMap(ShapeMapKey.CODEC, ShapeMapTemplate.CODEC.listOf());

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
}
