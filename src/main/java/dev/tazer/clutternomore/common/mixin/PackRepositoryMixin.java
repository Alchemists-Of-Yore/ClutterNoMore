package dev.tazer.clutternomore.common.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.data.vanilla.CNMPackResources;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Optional;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(RepositorySource[] sources, CallbackInfo ci) {
        PackLocationInfo info = new PackLocationInfo(ClutterNoMore.MODID, Component.literal("Dynamic server data for ClutterNoMore"), PackSource.BUILT_IN, Optional.empty());
        CNMPackResources resources = new CNMPackResources(info, PackType.SERVER_DATA);

        JsonObject object = new JsonObject();
        object.add("description", new JsonPrimitive("Clutter No More dynamic data resources"));
        object.add("min_format", new JsonPrimitive(88));
        object.add("max_format", new JsonPrimitive(88));
        JsonObject mcmeta = new JsonObject();
        mcmeta.add("pack", object);
        resources.addRootJson("pack.mcmeta", mcmeta);

        JsonArray array = new JsonArray();
        array.add("minecraft:iron_block");
        JsonObject tag = new JsonObject();
        tag.add("values", array);
        resources.addJson(ClutterNoMore.location("tags/block/stupid.json"), tag);

        var pack = Pack.readMetaAndCreate(
                info,
                new Pack.ResourcesSupplier() {
                    @Override
                    public PackResources openPrimary(PackLocationInfo location) {
                        return resources;
                    }

                    @Override
                    public PackResources openFull(PackLocationInfo location, Pack.Metadata metadata) {
                        return resources;
                    }
                },
                PackType.SERVER_DATA,
                new PackSelectionConfig(
                        true,
                        Pack.Position.TOP,
                        false
                )
        );
        var newSources = new HashSet<>(((PackRepositoryAccessor) this).getSources());
        newSources.add((infoConsumer) -> infoConsumer.accept(pack));
        ((PackRepositoryAccessor) this).setSources(newSources);
    }
}
