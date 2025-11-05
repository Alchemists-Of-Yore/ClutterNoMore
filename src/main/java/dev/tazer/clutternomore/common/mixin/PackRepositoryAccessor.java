package dev.tazer.clutternomore.common.mixin;

import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.repository.RepositorySource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(PackRepository.class)
public interface PackRepositoryAccessor {
    // ? if fabric {
    @Accessor("sources")
    Set<RepositorySource> getSources();

    @Mutable
    @Accessor("sources")
    void setSources(Set<RepositorySource> sources);
    // ?}
}
