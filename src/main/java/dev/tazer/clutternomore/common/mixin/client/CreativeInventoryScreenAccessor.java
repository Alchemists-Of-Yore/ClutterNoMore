package dev.tazer.clutternomore.common.mixin.client;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.world.SimpleContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(CreativeModeInventoryScreen.class)
public interface CreativeInventoryScreenAccessor {
    @Accessor("CONTAINER")
    static SimpleContainer cnm$getContainer() {
        throw new AssertionError();
    }
}
