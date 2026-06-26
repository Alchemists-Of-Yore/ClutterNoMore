//? if <26.1 {
/*package dev.tazer.clutternomore.common.mixin.client;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemColors.class)
public class ItemColorsMixin {
    @Inject(method = "getColor(Lnet/minecraft/world/item/ItemStack;I)I", at = @At("HEAD"), cancellable = true)
    private void cnm$getShapeItemColor(ItemStack itemStack, int tintIndex, CallbackInfoReturnable<Integer> cir) {
        Item item = itemStack.getItem();
        if (ShapeMap.isShape(item)) {
            Item parent = ShapeMap.getParent(item);
            ItemStack parentStack = itemStack.copy();

            //? if >1.20.4 {
            parentStack = parentStack.transmuteCopy(parent, itemStack.getCount());
            //?} else {
            *//*parentStack = new ItemStack(parent, itemStack.getCount());
            CompoundTag tag = itemStack.getTag();
            if (tag != null) parentStack.setTag(tag.copy());
            *//*//?}

            int color = ((ItemColors) (Object) this).getColor(parentStack, tintIndex);

            if (color != -1) {
                cir.setReturnValue(color);
            }
        }
    }
}*/
//?}
