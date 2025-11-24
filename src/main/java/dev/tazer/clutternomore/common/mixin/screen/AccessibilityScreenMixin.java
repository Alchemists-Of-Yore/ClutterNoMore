package dev.tazer.clutternomore.common.mixin.screen;

import dev.tazer.clutternomore.client.ShapeSwitcherOptionsScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
//? if >1.21 {
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
//?} else {
/*import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
*///?}
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(AccessibilityOptionsScreen.class)
public abstract class AccessibilityScreenMixin {
    //? if >1.20.1 {
    @Inject(
            method = "addOptions",
            at = @At(value = "RETURN")
    )
    public void addOptions(CallbackInfo ci) {
        Button shapeSwitcherButton = Button
                .builder(
                        Component.translatable("key.clutternomore.shape_switcher"),
                        button -> ((ScreenAccessor) this).getMinecraft().setScreen(new ShapeSwitcherOptionsScreen(((AccessibilityOptionsScreen) (Object) this), ((OptionsSubScreenAccessor) (this)).getOptions()))
                ).bounds(0, 0, 150, 20).build();
        ((OptionsSubScreenAccessor) this).getList().addSmall(List.of(shapeSwitcherButton));
    }
    //?} else {
    /*@Redirect(
            method = "createFooter",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/Button$Builder;bounds(IIII)Lnet/minecraft/client/gui/components/Button$Builder;")
    )
    private Button.Builder redirectCreateFooter(Button.Builder builder, int x, int y, int width, int height) {
        return builder.bounds(x - 80, y, width, height);
    }

    @Inject(
            method = "createFooter",
            at = @At(value = "RETURN")
    )
    private void injectCreateFooter(CallbackInfo ci) {
        Button shapeSwitcherButton = Button
                .builder(
                        Component.translatable("key.clutternomore.shape_switcher"),
                        button -> ((ScreenAccessor) this).getMinecraft().setScreen(new ShapeSwitcherOptionsScreen(((AccessibilityOptionsScreen) (Object) this), ((OptionsSubScreenAccessor) (this)).getOptions()))
                ).bounds(((ScreenAccessor) this).getWidth() / 2 + 80, ((ScreenAccessor) this).getHeight() - 27, 150, 20).build();
        //? if >1.21 {
        ((ScreenAccessor) this).invokeAddRenderableWidget(shapeSwitcherButton);
        //?} else {
        /^((AccessibilityOptionsScreen) (Object) this).addRenderableWidget(shapeSwitcherButton);
        ^///?}

    }
    *///?}
}
