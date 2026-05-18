package dev.tazer.clutternomore.common.mixin.compat.controlify;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.isxander.controlify.ingame.InGameInputHandler;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.common.mixin.annotation.IfModPresent;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@IfModPresent("controlify")
@Mixin(InGameInputHandler.class)
public class ControlifyInGameInputHandlerMixin {
    //? if >26 {
    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V", ordinal = 0))
    private void cnm$prevHotbar(Inventory instance, int selectedIndex, Operation<Void> original) {
        if (!ClutterNoMoreClient.prevSlot())
            original.call(instance, selectedIndex);
    }

    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V", ordinal = 1))
    private void cnm$nextHotbar(Inventory instance, int selectedIndex, Operation<Void> original) {
        if (!ClutterNoMoreClient.nextSlot())
            original.call(instance, selectedIndex);
    }
    //?} else {
    /*@WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;swapPaint(D)V", ordinal = 0))
    private void cnm$prevHotbar(Inventory instance, double selectedIndex, Operation<Void> original) {
        if (!ClutterNoMoreClient.prevSlot())
            original.call(instance, selectedIndex);
    }

    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;swapPaint(D)V", ordinal = 1))
    private void cnm$nextHotbar(Inventory instance, double selectedIndex, Operation<Void> original) {
        if (!ClutterNoMoreClient.nextSlot())
            original.call(instance, selectedIndex);
    }
    *///?}
}
