package dev.tazer.clutternomore.common.data;

import com.google.gson.JsonElement;
//? if >1.21.9 {
import com.google.gson.Strictness;
//?}
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
//? if =1.21.1 || 1.20.1 {
/*import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
 *///?}
import net.minecraft.server.packs.metadata.MetadataSectionType;
//? if >1.21.9 {
import net.minecraft.server.packs.metadata.pack.PackFormat;
//?}
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.util.InclusiveRange;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CNMPackResources extends AbstractPackResources {
    protected final Map<ResourceLocation, byte[]> clientResources;
    protected final Map<ResourceLocation, byte[]> serverData;
    protected final Map<String, byte[]> rootResources;
    protected final PackMetadataSection clientMetadata;
    protected final PackMetadataSection serverMetadata;

    private static final WorldVersion currentVersion = SharedConstants.getCurrentVersion();

    //? if >1.21.9 {
    public static final InclusiveRange<PackFormat> resourcePackVersion = currentVersion.packVersion(PackType.CLIENT_RESOURCES).minorRange();
    public static final InclusiveRange<PackFormat> dataPackVersion = currentVersion.packVersion(PackType.SERVER_DATA).minorRange();
    //?} else if >1.21.1 {
    /*public static final int resourcePackVersion = currentVersion.packVersion(PackType.CLIENT_RESOURCES);
    public static final int dataPackVersion = currentVersion.packVersion(PackType.SERVER_DATA);
    *///?} else {
    /*public static final int resourcePackVersion = currentVersion.getPackVersion(PackType.CLIENT_RESOURCES);
    public static final int dataPackVersion = currentVersion.getPackVersion(PackType.SERVER_DATA);
    *///?}

    public CNMPackResources(
            //? if >1.21 {
            PackLocationInfo
            //?} else {
            /*String
            *///?}
             info
    ) {
        super(info
        //? if <1.21 {
            /*, true
        *///?}
        );
        this.clientResources = new ConcurrentHashMap<>();
        this.serverData = new ConcurrentHashMap<>();
        this.rootResources = new ConcurrentHashMap<>();
        this.clientMetadata = new PackMetadataSection(Component.literal("ClutterNoMore Runtime Client Resources"), resourcePackVersion
                //? if >1.21 && <1.21.9
                /*, Optional.empty()*/
        );
        this.serverMetadata = new PackMetadataSection(Component.literal("ClutterNoMore Runtime Server Data"), dataPackVersion
                //? if >1.21 && <1.21.9
                /*, Optional.empty()*/
        );
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return Set.of(ClutterNoMore.MODID);
    }

    //? if >1.21.9 {
    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionType<T> type) {
        return type == PackMetadataSection.CLIENT_TYPE ? (T) clientMetadata : type == PackMetadataSection.SERVER_TYPE ? (T) serverMetadata : null;
    }
    //?} else if >1.21.1 {
    
    /*@Override
    public @Nullable <T> T getMetadataSection(MetadataSectionType<T> type) throws IOException {
        try {
            return (T) (type == PackMetadataSection.TYPE ? clientMetadata : null);
        } catch (Exception var3) {
            return null;
        }
    }

    *///?} else {
    /*@Nullable
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) throws IOException {
        try {
            return (T) (deserializer == PackMetadataSection.TYPE ? clientMetadata : null);
        } catch (Exception var3) {
            return null;
        }
    }
    *///?}

    public void addResource(PackType packType, ResourceLocation id, byte[] bytes) {
        Map<ResourceLocation, byte[]> resources = packType == PackType.CLIENT_RESOURCES ? clientResources : serverData;
        resources.put(id, bytes);
    }

    public void addJson(PackType packType, ResourceLocation path, JsonElement json) {
        try {
            addResource(packType, path, serializeJson(json).getBytes());
        } catch (IOException e) {
            ClutterNoMore.LOGGER.error("Failed to write JSON {} to resource pack.", path, e);
        }
    }

    public @Nullable IoSupplier<InputStream> getRootResource(String... strings) {
        byte[] resource = rootResources.get(String.join("/", strings));
        return resource != null ? () -> new ByteArrayInputStream(resource) : null;    }

    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation id) {
        Map<ResourceLocation, byte[]> resources = packType == PackType.CLIENT_RESOURCES ? clientResources : serverData;
        byte[] resource = resources.get(id);
        return resource != null ? () -> new ByteArrayInputStream(resource) : null;
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, PackResources.ResourceOutput output) {
        Map<ResourceLocation, byte[]> resources = packType == PackType.CLIENT_RESOURCES ? clientResources : serverData;
        for (ResourceLocation location : resources.keySet()) {
            if (location.getPath().startsWith(path)) {
                byte[] resource = resources.get(location);
                output.accept(location, () -> new ByteArrayInputStream(resource));
            }
        }
    }

    @Override
    public void close() {
    }

    public static String serializeJson(JsonElement json) throws IOException {
        try {
            String string;
            try (StringWriter stringWriter = new StringWriter(); JsonWriter jsonWriter = new JsonWriter(stringWriter)) {
                //? if >1.21.9 {
                jsonWriter.setStrictness(Strictness.LENIENT);
                //?} else {
                /*jsonWriter.setLenient(true);
                 *///?}
                jsonWriter.setIndent("  ");
                Streams.write(json, jsonWriter);
                string = stringWriter.toString();
            }

            return string;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}