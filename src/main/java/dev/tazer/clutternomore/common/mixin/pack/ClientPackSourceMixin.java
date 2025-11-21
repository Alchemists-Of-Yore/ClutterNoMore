package dev.tazer.clutternomore.common.mixin.pack;

import dev.tazer.clutternomore.ClutterNoMore;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Mixin(ClientPackSource.class)
public class ClientPackSourceMixin {
    @Inject(method = "populatePackList", at = @At("TAIL"))
    private void addBuiltinResourcePacks(BiConsumer<String, Function<String, Pack>> populator, CallbackInfo ci) {
        Pack pack = ClutterNoMore.createPack(PackType.CLIENT_RESOURCES);
        populator.accept("clutternomore-runtime", (s) -> pack);
    }
}