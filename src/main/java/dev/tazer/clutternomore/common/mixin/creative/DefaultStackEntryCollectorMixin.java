package dev.tazer.clutternomore.common.mixin.creative;

import dev.tazer.clutternomore.common.CHooks;
import dev.tazer.clutternomore.common.mixin.annotation.IfModPresent;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@IfModPresent("fractal")
@Mixin(targets = "de.dafuqs.fractal.api.DefaultStackEntryCollector.DefaultStackEntryCollector")
public abstract class DefaultStackEntryCollectorMixin {

    @Inject(method = "accept(Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/world/item/CreativeModeTab$TabVisibility;)V", at = @At(value = "HEAD"), cancellable = true)
    private void cnm$accept(ItemLike item, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(item.asItem())) ci.cancel();
    }

    @Inject(method = "accept(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/CreativeModeTab$TabVisibility;)V", at = @At(value = "HEAD"), cancellable = true)
    private void cnm$accept(ItemStack stack, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(stack.getItem())) ci.cancel();
    }
}