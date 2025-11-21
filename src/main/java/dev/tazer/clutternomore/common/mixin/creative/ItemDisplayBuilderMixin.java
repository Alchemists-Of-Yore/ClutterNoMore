package dev.tazer.clutternomore.common.mixin.creative;

import dev.tazer.clutternomore.common.CHooks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreativeModeTab.ItemDisplayBuilder.class)
public class ItemDisplayBuilderMixin {
    @Inject(method = "accept", at = @At(value = "HEAD"), cancellable = true)
    private void accept(ItemStack stack, CreativeModeTab.TabVisibility tabVisibility, CallbackInfo ci) {
        if (CHooks.denyItem(stack.getItem())) ci.cancel();
    }
}
