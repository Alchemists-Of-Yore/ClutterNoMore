package dev.tazer.clutternomore.common.mixin.compat.jei;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.clutternomore.client.RenderHelper;
import dev.tazer.clutternomore.common.mixin.annotation.IfModPresent;
//? if >26 {
import mezz.jei.api.ingredients.rendering.BatchRenderElement;
import mezz.jei.library.render.ItemStackRenderer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//?}
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Pseudo
@IfModPresent("jei")
@Mixin(targets = "mezz.jei.library.render.batch.SimpleItemStackBatchRenderer")
public class SimpleJEIRenderMixin {
    //? if >26 {
    @Inject(method = "renderBatch", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fakeItem(Lnet/minecraft/world/item/ItemStack;II)V"))
    private void cnm$badge(GuiGraphicsExtractor guiGraphics, ItemStackRenderer itemStackRenderer, List<BatchRenderElement<ItemStack>> elements, CallbackInfo ci, @Local(name = "element") BatchRenderElement<ItemStack> element) {
        RenderHelper.shapeBadge(guiGraphics, element.ingredient(), element.x(), element.y());
    }
    //?}
}
