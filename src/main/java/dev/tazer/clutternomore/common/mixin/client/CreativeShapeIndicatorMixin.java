package dev.tazer.clutternomore.common.mixin.client;

import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.RenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerScreen.class)
public class CreativeShapeIndicatorMixin {
    //? if >26 {
    @Inject(method = "extractSlot", at = @At("RETURN"))
    private void cnm$drawShapeIndicator(GuiGraphicsExtractor guiGraphics, Slot slot, int mouseX, int mouseY, CallbackInfo ci) {
    //?} else {
    /*@Inject(method = "renderSlot", at = @At("RETURN"))
    private void cnm$drawShapeIndicator(GuiGraphicsExtractor guiGraphics, Slot slot, CallbackInfo ci) {
    *///?}
        if (!ClutterNoMoreClient.isCreativeTabSlot(slot)) return;
        RenderHelper.shapeBadge(guiGraphics, slot.getItem(), slot.x, slot.y);
    }
}
