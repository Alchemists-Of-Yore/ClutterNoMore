package dev.tazer.clutternomore.common.mixin;

//? fabric {
import dev.tazer.clutternomore.fabric.ClientEvents;
//?}
import net.minecraft.client.KeyboardHandler;
//? if >1.21.8
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {
    //? fabric {

    @Inject(method = "keyPress", at = @At("HEAD"), cancellable = true)
    //? if >1.21.8 {
    private void key(long windowPointer, int action, KeyEvent keyEvent, CallbackInfo ci) {
        int key = keyEvent.key();
    //?} else {
    
    /*private void key(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
    *///?}
        ClientEvents.onKeyInput(key, action);
    }
    //?}

}