package dev.tazer.clutternomore.common.mixin.screen;

import dev.tazer.clutternomore.ClutterNoMoreClient;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//~ if >26.1.2 'Minecraft' -> 'gui.Gui' {
@Mixin(net.minecraft.client.Minecraft.class)
//~}
public class CloseScreenMixin {
    @Inject(method = "setScreen", at = @At("RETURN"))
    private void cnm$onSetScreen(Screen guiScreen, CallbackInfo ci) {
        ClutterNoMoreClient.OVERLAY = null;
    }
}
