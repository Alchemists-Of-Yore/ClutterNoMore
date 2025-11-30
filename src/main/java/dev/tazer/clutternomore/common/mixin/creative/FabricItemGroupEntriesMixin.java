package dev.tazer.clutternomore.common.mixin.creative;

import dev.tazer.clutternomore.common.CHooks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(FabricItemGroupEntries.class)
public abstract class FabricItemGroupEntriesMixin {

    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void accept(ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(newEntry.getItem())) ci.cancel();
    }

    @Inject(method = "isEnabled", remap = false, at = @At("RETURN"), cancellable = true)
    private void accept(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (CHooks.denyItem(stack.getItem())) cir.setReturnValue(false);
    }
}