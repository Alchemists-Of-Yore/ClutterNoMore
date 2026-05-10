package dev.tazer.clutternomore.common.mixin.compat.emi;

import dev.tazer.clutternomore.common.mixin.annotation.IfModPresent;
//? if <1.21.4 {
/*import dev.emi.emi.api.stack.EmiStack;
import dev.tazer.clutternomore.client.RenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@IfModPresent("emi")
@Mixin(targets = "dev.emi.emi.api.stack.ItemEmiStack")
public class EMIRenderMixin {
    //? if <1.21.4 {
    /*@Inject(method = "render(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIFI)V", at = @At("RETURN"))
    private void cnm$badgeRender(GuiGraphicsExtractor guiGraphics, int x, int y, float delta, int flags, CallbackInfo ci) {
        ItemStack stack = ((EmiStack) (Object) this).getItemStack();
        RenderHelper.shapeBadge(guiGraphics, stack, x, y);
    }

    @Inject(method = "renderForBatch(Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIIF)V", at = @At("RETURN"))
    private void cnm$badgeBatch(MultiBufferSource vcp, GuiGraphicsExtractor guiGraphics, int x, int y, int z, float delta, CallbackInfo ci) {
        ItemStack stack = ((EmiStack) (Object) this).getItemStack();
        RenderHelper.shapeBadge(guiGraphics, stack, x, y);
    }
    *///?}
}
