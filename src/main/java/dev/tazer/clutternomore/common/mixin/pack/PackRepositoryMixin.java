package dev.tazer.clutternomore.common.mixin.pack;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PackRepository.class)
public class PackRepositoryMixin {
    @Inject(method = "openAllSelected", at = @At("RETURN"), cancellable = true)
    private void clutternomore$injectRuntimePack(CallbackInfoReturnable<List<PackResources>> cir) {
        List<PackResources> opened = new ArrayList<>(cir.getReturnValue());
        if (!opened.contains(ClutterNoMore.RESOURCES)) {
            opened.add(ClutterNoMore.RESOURCES);
        }
        cir.setReturnValue(List.copyOf(opened));
    }
}
