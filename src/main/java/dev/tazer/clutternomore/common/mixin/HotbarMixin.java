package dev.tazer.clutternomore.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class HotbarMixin {
    //? if >1.21.8 {
    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"))
    private void pickBlock(Inventory instance, int selectedIndex, Operation<Void> original) {
        if (ClutterNoMoreClient.OVERLAY != null) {
            int maxIndex = ClutterNoMoreClient.OVERLAY.shapes.size() - 1;
            if (selectedIndex < 0) selectedIndex = 0;
            if (selectedIndex > maxIndex) selectedIndex = maxIndex;
            ClutterNoMoreClient.OVERLAY.changeSlot(selectedIndex);
        } else {
            original.call(instance, selectedIndex);
        }
    }
     
    //?} else {
    /*@WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 2))
    private boolean pickBlock(KeyMapping instance, Operation<Boolean> original, @Local int selectedIndex) {
        var b = original.call(instance);
        if (ClutterNoMoreClient.OVERLAY != null && b) {
            int maxIndex = ClutterNoMoreClient.OVERLAY.shapes.size() - 1;
            if (selectedIndex < 0) selectedIndex = 0;
            if (selectedIndex > maxIndex) selectedIndex = maxIndex;
            ClutterNoMoreClient.OVERLAY.changeSlot(selectedIndex);
            return false;
        }
        return b;
    }
    *///?}

}
