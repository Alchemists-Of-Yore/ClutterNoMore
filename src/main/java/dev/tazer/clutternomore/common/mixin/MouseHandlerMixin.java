package dev.tazer.clutternomore.common.mixin;

import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.MouseHandler;
//? if fabric {
import dev.tazer.clutternomore.fabric.ClientEvents;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}
@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    //? if fabric {
    @Inject(method = "onPress", at = @At("HEAD"), cancellable = true)
    private void key(long windowPointer, int button, int action, int modifiers, CallbackInfo ci) {
        ClientEvents.onKeyInput(button, action);
    }

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void key(long windowPointer, double xOffset, double yOffset, CallbackInfo ci) {
        if (ClientEvents.onMouseScrolling(yOffset)) {
            ci.cancel();
        }
    }
    //?}
}