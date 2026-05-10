package dev.tazer.clutternomore.common.mixin.compat.jei;

import dev.tazer.clutternomore.common.mixin.annotation.IfModPresent;
//? if <1.21.4 {
/*import dev.tazer.clutternomore.client.RenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@IfModPresent("jei")
@Mixin(targets = "mezz.jei.library.render.batch.ItemStackBatchRenderer")
public class JEIRenderMixin {
    //? if <1.21.4 {
    /*@Inject(method = "renderItem", at = @At("RETURN"))
    private void cnm$badge(GuiGraphicsExtractor guiGraphics, ItemRenderer itemRenderer, BakedModel bakedmodel, ItemStack itemStack, int x, int y, CallbackInfo ci) {
        RenderHelper.shapeBadge(guiGraphics, itemStack, x, y);
    }
    *///?}
}
