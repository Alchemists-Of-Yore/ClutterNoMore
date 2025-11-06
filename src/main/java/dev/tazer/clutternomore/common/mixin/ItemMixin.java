package dev.tazer.clutternomore.common.mixin;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getTooltipImage", at = @At("HEAD"), cancellable = true)
    private void getTooltipImage(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        Item item = stack.getItem();

        if (ShapeMap.contains(item)) {
            Item originalItem = ShapeMap.getParent(item);
            List<Item> shapes = new ArrayList<>(ShapeMap.getShapes(originalItem));
            shapes.addFirst(originalItem);
            cir.setReturnValue(Optional.of(new ShapeTooltip(shapes, shapes.indexOf(item))));
        }
    }
}
