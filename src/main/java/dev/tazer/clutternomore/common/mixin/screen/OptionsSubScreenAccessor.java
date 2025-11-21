package dev.tazer.clutternomore.common.mixin.screen;

import net.minecraft.client.Options;
//? if >1.20.1 {
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
 //?} else {
/*import net.minecraft.client.gui.screens.OptionsSubScreen;
*///?}
import net.minecraft.client.gui.components.OptionsList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(OptionsSubScreen.class)
public interface OptionsSubScreenAccessor {
    @Accessor
    Options getOptions();

    //? if >1.20.1 {
    @Accessor
    OptionsList getList();
    //?}
}
