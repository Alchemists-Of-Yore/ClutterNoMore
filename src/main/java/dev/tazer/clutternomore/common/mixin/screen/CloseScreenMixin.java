package dev.tazer.clutternomore.common.mixin.screen;

import dev.tazer.clutternomore.ClutterNoMoreClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class CloseScreenMixin {
    @Inject(method = "setScreen", at = @At("RETURN"))
    private void accept(Screen guiScreen, CallbackInfo ci) {
        ClutterNoMoreClient.OVERLAY = null;
    }
}
