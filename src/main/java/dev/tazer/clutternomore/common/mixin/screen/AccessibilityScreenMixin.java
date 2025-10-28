package dev.tazer.clutternomore.common.mixin.screen;

import dev.tazer.clutternomore.client.ShapeSwitcherOptionsScreen;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
//? if >1.21 {
import net.minecraft.client.gui.screens.options.AccessibilityOptionsScreen;
//?} else {
/*import net.minecraft.client.gui.screens.AccessibilityOptionsScreen;
*///?}
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.List;

@Mixin(AccessibilityOptionsScreen.class)
public abstract class AccessibilityScreenMixin extends OptionsSubScreenMixin {
    protected AccessibilityScreenMixin(Component title) {
        super(title);
    }

    //? if >1.20.1 {
    @Inject(
            method = "addOptions",
            at = @At(value = "RETURN")
    )
    public void addOptions(CallbackInfo ci) {
        Button shapeSwitcherButton = Button
                .builder(
                        Component.translatable("key.clutternomore.shape_switcher"),
                        button -> minecraft.setScreen(new ShapeSwitcherOptionsScreen(((AccessibilityOptionsScreen) (Object) this), options))
                ).bounds(0, 0, 150, 20).build();
        list.addSmall(List.of(shapeSwitcherButton));
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
                        button -> minecraft.setScreen(new ShapeSwitcherOptionsScreen(((AccessibilityOptionsScreen) (Object) this), options))
                ).bounds(this.width / 2 + 80, this.height - 27, 150, 20).build();
        this.addRenderableWidget(shapeSwitcherButton);
    }
    *///?}
}
