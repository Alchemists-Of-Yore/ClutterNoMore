package dev.tazer.clutternomore.common.data.vanilla;

import com.google.gson.JsonElement;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.FileUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CNMPackResources extends AbstractPackResources {
    protected final PackType packType;
    protected final Set<String> namespaces;
    protected final Map<ResourceLocation, byte[]> resources;
    protected final Map<String, byte[]> rootResources;

    public CNMPackResources(PackLocationInfo info, PackType type) {
        this(info, type, false);
    }

    public CNMPackResources(PackLocationInfo info, PackType type, boolean hidden) {
        super(info);
        this.namespaces = new HashSet<>();
        this.resources = new ConcurrentHashMap<>();
        this.rootResources = new ConcurrentHashMap<>();
        this.packType = type;
    }

//                public <T> T getMetadataSection(MetadataSectionSerializer<T> serializer) {
//                    try {
//                        return (T)(serializer == PackMetadataSection.TYPE ? this.metadata : null);
//                    } catch (Exception var3) {
//                        return null;
//                    }
//                }

    public void addRootResource(String name, byte[] resource) {
        this.rootResources.put(name, resource);
    }

    public void addResource(ResourceLocation id, byte[] bytes) {
        synchronized(this) {
            this.namespaces.add(id.getNamespace());
            this.resources.put(id, bytes);
        }
    }

    public void addJson(ResourceLocation path, JsonElement json) {
        try {
            addResource(path, serializeJson(json).getBytes());
        } catch (IOException e) {
            ClutterNoMore.LOGGER.error("Failed to write JSON {} to resource pack.", path, e);
        }
    }

    public void addRootJson(String name, JsonElement json) {
        try {
            addRootResource(name, serializeJson(json).getBytes());
        } catch (IOException e) {
            ClutterNoMore.LOGGER.error("Failed to write JSON {} to resource pack.", name, e);
        }
    }

    public @Nullable IoSupplier<InputStream> getRootResource(String... strings) {
        String fileName = String.join("/", strings);
        byte[] resource = this.rootResources.get(fileName);
        return resource == null ? null : () -> new ByteArrayInputStream(resource);
    }

    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation id) {
        byte[] res = this.resources.get(id);
        return res != null ? () -> {
            if (type != this.packType) {
                throw new IOException(String.format("Tried to access wrong type of resource on %s.", this.packId()));
            } else {
                return new ByteArrayInputStream(res);
            }
        } : null;
    }

    public void listResources(PackType packType, String namespace, String p_path, PackResources.ResourceOutput output) {
        if (packType == this.packType) {
            FileUtil.decomposePath(p_path).ifSuccess((p_248228_) -> {
                List<ResourceLocation> list = resources.keySet().stream().toList();
                int i = list.size();
                if (i == 1) {
                    getResources(output, namespace, list.get(0), p_248228_);
                } else if (i > 1) {
                    Map<ResourceLocation, IoSupplier<InputStream>> map = new HashMap<>();

                    for (int j = 0; j < i - 1; ++j) {
                        Objects.requireNonNull(map);
                        getResources(map::putIfAbsent, namespace, list.get(j), p_248228_);
                    }

                    ResourceLocation path = list.get(i - 1);
                    if (map.isEmpty()) {
                        getResources(output, namespace, path, p_248228_);
                    } else {
                        Objects.requireNonNull(map);
                        getResources(map::putIfAbsent, namespace, path, p_248228_);
                        map.forEach(output);
                    }
                }

            }).ifError((p_337564_) -> ClutterNoMore.LOGGER.error("Invalid path {}: {}", p_path, p_337564_.message()));

        }
    }

    private static void getResources(PackResources.ResourceOutput resourceOutput, String namespace, ResourceLocation root, List<String> paths) {
        Path path = Path.of(root.getPath());
        PathPackResources.listPath(namespace, path, paths, resourceOutput);
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return packType != this.packType ? Set.of() : this.namespaces;
    }

    @Override
    public void close() {
    }

    public static String serializeJson(JsonElement json) throws IOException {
        try {
            String var3;
            try (
                    StringWriter stringWriter = new StringWriter();
                    JsonWriter jsonWriter = new JsonWriter(stringWriter);
            ) {
                jsonWriter.setLenient(true);
                jsonWriter.setIndent("  ");
                Streams.write(json, jsonWriter);
                var3 = stringWriter.toString();
            }

            return var3;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
