package dev.tazer.clutternomore.common.mixin;


import com.mojang.serialization.Lifecycle;
import dev.tazer.clutternomore.common.access.RegistryAccess;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Needed for runtime slab/step registration after load has completed.
 * Based on DynReg by BasiqueEvangelist.
 * */
@Mixin(MappedRegistry.class)
public abstract class SimpleRegistryMixin<T> implements RegistryAccess, Registry<T> {
    @Shadow
    private boolean frozen;
    @Shadow
    @Nullable
    private Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

    //? if >1.21.2 {
    @Shadow
    MappedRegistry.TagSet<T> allTags;
    //?}

    @Override
    public void clutternomore$unfreeze() {
        frozen = false;
        //? if >1.21.2
        allTags = MappedRegistry.TagSet.unbound();
        this.unregisteredIntrusiveHolders = new IdentityHashMap<>();
    }
}