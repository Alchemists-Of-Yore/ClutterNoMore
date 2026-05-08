package dev.tazer.clutternomore.common.mixin.pack;

import dev.tazer.clutternomore.client.assets.AssetGenerator;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManager.class)
public class MinecraftReloadMixin {
    @Shadow @Final private PackType type;

    @Inject(method = "createReload", at = @At("HEAD"))
    private void clutternomore$populateRuntimePack(Executor backgroundExecutor, Executor gameExecutor, CompletableFuture<Unit> waitable, List<PackResources> packs, CallbackInfoReturnable<?> cir) {
        if (this.type != PackType.CLIENT_RESOURCES) return;
        ResourceManager temp = new MultiPackResourceManager(PackType.CLIENT_RESOURCES, packs);
        AssetGenerator.generate(temp);
    }
}
