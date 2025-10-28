package dev.tazer.clutternomore.common.mixin;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.CHooks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BuiltInRegistries.class)
public class BuiltInRegistriesMixin {

    //? if fabric {
    @Inject(method = "bootStrap", at = @At(value = "HEAD"))
    private static void accept(CallbackInfo ci) {
        ClutterNoMore.registerVariants();
    }
    //?}
}
