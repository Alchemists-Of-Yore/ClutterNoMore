package dev.tazer.clutternomore.common.mixin.screen;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.OptionsList;
//? if >1.20.1 {
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
//?} else {
/*import net.minecraft.client.gui.screens.OptionsSubScreen;
*///?}
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OptionsSubScreen.class)
public abstract class OptionsSubScreenMixin extends Screen {
    //? if >1.20.1 {
    @Shadow
    @Nullable
    protected OptionsList list;
    //?}
    @Shadow
    @Final
    protected Options options;

    protected OptionsSubScreenMixin(Component title) {
        super(title);
    }
}
