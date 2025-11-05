package dev.tazer.clutternomore.common.data.vanilla;

import com.google.gson.JsonElement;
import dev.tazer.clutternomore.ClutterNoMore;
import net.mehvahdjukaar.moonlight.api.misc.ResourceLocationSearchTrie;
import net.mehvahdjukaar.moonlight.api.resources.RPUtils;
import net.mehvahdjukaar.moonlight.core.Moonlight;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CNMPackResources extends AbstractPackResources {
    protected final PackType packType;
    protected final Set<String> namespaces;
    protected final Map<ResourceLocation, byte[]> resources;
    protected final Map<String, byte[]> rootResources;
    protected final ResourceLocationSearchTrie searchTrie;

    public CNMPackResources(PackLocationInfo info, PackType type) {
        this(info, type, false);
    }

    public CNMPackResources(PackLocationInfo info, PackType type, boolean hidden) {
        super(info);
        this.namespaces = new HashSet<>();
        this.resources = new ConcurrentHashMap<>();
        this.rootResources = new ConcurrentHashMap<>();
        this.searchTrie = new ResourceLocationSearchTrie();
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
            this.searchTrie.insert(id);
        }
    }

    public void addJson(ResourceLocation path, JsonElement json) {
        try {
            addResource(path, RPUtils.serializeJson(json).getBytes());
        } catch (IOException e) {
            ClutterNoMore.LOGGER.error("Failed to write JSON {} to resource pack.", path, e);
        }
    }

    public void addRootJson(String name, JsonElement json) {
        try {
            addRootResource(name, RPUtils.serializeJson(json).getBytes());
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

    public void listResources(PackType packType, String namespace, String id, PackResources.ResourceOutput output) {
        if (packType == this.packType) {
            synchronized(this) {
                this.searchTrie.search(namespace + "/" + id).forEach((r) -> {
                    byte[] buf = this.resources.get(r);
                    output.accept(r, () -> {
                        if (buf == null) {
                            throw new IllegalStateException("Somehow search tree returned a resource not in resources " + String.valueOf(r));
                        } else {
                            return new ByteArrayInputStream(buf);
                        }
                    });
                });
            }
        }
    }
    @Override
    public Set<String> getNamespaces(PackType packType) {
        return packType != this.packType ? Set.of() : this.namespaces;
    }

    @Override
    public void close() {
    }
}
