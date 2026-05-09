package dev.tazer.clutternomore.common.mixin.client;

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.RenderHelper;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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
        if (!ClutterNoMoreClient.CLIENT_CONFIG.SHAPE_INDICATOR.value()) return;
        if (!ClutterNoMoreClient.isCreativeTabSlot(slot)) return;
        ItemStack stack = slot.getItem();
        if (stack.isEmpty() || !ShapeMap.contains(stack.getItem())) return;
        Identifier texture = ClutterNoMore.location("textures/gui/shape_indicator.png");
        //? if <1.21.6 {
        /*guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
        *///?}
        RenderHelper.blit(guiGraphics, texture, slot.x + 12, slot.y, 0, 0, 4, 4, 4, 4);
        //? if <1.21.6 {
        /*guiGraphics.pose().popPose();
        *///?}
    }
}
