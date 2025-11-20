package dev.tazer.clutternomore.common.mixin;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.Platform;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void init(RepositorySource[] sources, CallbackInfo ci) {
        Pack pack = Platform.INSTANCE.isClient() && Arrays.stream(sources).toList().stream().anyMatch(source -> source instanceof ClientPackSource) ? ClutterNoMore.createPack(PackType.CLIENT_RESOURCES) : ClutterNoMore.createPack(PackType.SERVER_DATA);
        Set<RepositorySource> newSources = new HashSet<>(((PackRepositoryAccessor) this).getSources());
        newSources.add(consumer -> consumer.accept(pack));
        ((PackRepositoryAccessor) this).setSources(newSources);
    }
}
