package dev.tazer.clutternomore.common.mixin;

import dev.tazer.clutternomore.common.CHooks;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
//? neoforge {

/*import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
 *///?} else if fabric {
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
//?} else {
/*import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
*///?}
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//? if neoforge || forge {
/*@Mixin(BuildCreativeModeTabContentsEvent.class)
*///?} else {
@Mixin(FabricItemGroupEntries.class)
//?}
public abstract class CreativeModeTabEntriesMixin {
    //? if forge {
    /*@Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void accept(ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(newEntry.getItem())) ci.cancel();
    }
    *///?} else if neoforge {
    /*@Shadow
    public abstract void insertBefore(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility);

    @Shadow
    public abstract void insertAfter(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility);

    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void accept(ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(newEntry.getItem())) ci.cancel();
    }

    @Inject(method = "insertAfter", at = @At("HEAD"), cancellable = true)
    private void insertAfter(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(newEntry.getItem())) ci.cancel();
        if (ShapeMap.isShape(existingEntry.getItem())) {
            insertAfter(ShapeMap.getParent(existingEntry.getItem()).getDefaultInstance(), newEntry, visibility);
            ci.cancel();
        }
    }

    @Inject(method = "insertBefore", at = @At("HEAD"), cancellable = true)
    private void insertBefore(ItemStack existingEntry, ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(newEntry.getItem())) ci.cancel();
        if (ShapeMap.isShape(existingEntry.getItem())) {
            insertBefore(ShapeMap.getParent(existingEntry.getItem()).getDefaultInstance(), newEntry, visibility);
            ci.cancel();
        }
    }
    *///?} else {
    
    @Inject(method = "accept", at = @At("HEAD"), cancellable = true)
    private void accept(ItemStack newEntry, CreativeModeTab.TabVisibility visibility, CallbackInfo ci) {
        if (CHooks.denyItem(newEntry.getItem())) ci.cancel();
    }

    @Inject(method = "isEnabled", at = @At("RETURN"), cancellable = true)
    private void accept(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (CHooks.denyItem(stack.getItem())) cir.setReturnValue(false);
    }
    //?}
}
