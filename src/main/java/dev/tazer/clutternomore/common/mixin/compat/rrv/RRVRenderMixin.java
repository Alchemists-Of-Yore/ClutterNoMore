package dev.tazer.clutternomore.common.mixin.compat.rrv;

import dev.tazer.clutternomore.common.mixin.annotation.IfModPresent;
//? if >1.21.9 {
import dev.tazer.clutternomore.client.RenderHelper;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@IfModPresent("rrv")
@Mixin(targets = "cc.cassian.rrv.common.overlay.ItemSlot")
public abstract class RRVRenderMixin {
    //? if >1.21.9 {
    @Shadow public abstract ItemStack getStack();

    @Shadow @Final private int x;
    @Shadow @Final private int y;

    @Inject(method = "extractRenderState", at = @At("RETURN"))
    private void cnm$badge(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        RenderHelper.shapeBadge(guiGraphics, this.getStack(), this.x, this.y);
    }
    //?}
}
