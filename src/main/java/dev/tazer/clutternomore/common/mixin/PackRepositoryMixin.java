package dev.tazer.clutternomore.common.mixin;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.data.vanilla.CNMPackResources;
import net.minecraft.client.resources.ClientPackSource;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(RepositorySource[] sources, CallbackInfo ci) {
        Pack pack = ClutterNoMore.createPack(Arrays.stream(sources).toList().stream().anyMatch(source -> source instanceof ClientPackSource) ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA);
        Set<RepositorySource> newSources = new HashSet<>(((PackRepositoryAccessor) this).getSources());
        newSources.add(consumer -> consumer.accept(pack));
        ((PackRepositoryAccessor) this).setSources(newSources);
    }
}
