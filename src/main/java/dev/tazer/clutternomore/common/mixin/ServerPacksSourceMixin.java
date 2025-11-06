package dev.tazer.clutternomore.common.mixin;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.*;
import net.minecraft.world.level.validation.DirectoryValidator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

@Mixin(ServerPacksSource.class)
public class ServerPacksSourceMixin {
    @Inject(method = "createPackRepository(Ljava/nio/file/Path;Lnet/minecraft/world/level/validation/DirectoryValidator;)Lnet/minecraft/server/packs/repository/PackRepository;", at = @At("RETURN"), cancellable = true)
    private static void addPacks(Path folder, DirectoryValidator validator, CallbackInfoReturnable<PackRepository> cir) {
        PackRepository packRepository = cir.getReturnValue();
        Pack pack = ClutterNoMore.createPack(PackType.SERVER_DATA);
        Set<RepositorySource> newSources = new HashSet<>(((PackRepositoryAccessor) packRepository).getSources());
        newSources.add((infoConsumer) -> infoConsumer.accept(pack));
        ((PackRepositoryAccessor) packRepository).setSources(newSources);
        cir.setReturnValue(packRepository);
    }
}
