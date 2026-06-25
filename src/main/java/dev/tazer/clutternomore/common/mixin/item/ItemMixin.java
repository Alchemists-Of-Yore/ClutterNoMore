package dev.tazer.clutternomore.common.mixin.item;

import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.inventory.ShapeTooltip;
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
    private void cnm$getTooltipImage(ItemStack stack, CallbackInfoReturnable<Optional<TooltipComponent>> cir) {
        Item item = stack.getItem();

        if (ShapeMap.contains(item)) {
            List<Item> shapes = ShapeMap.getShapes(item);
            List<ItemStack> stacks = new ArrayList<>(shapes.size());
            for (int i = 0; i < shapes.size(); i++) {
                stacks.add(ShapeMap.transferStack(stack, i));
            }
            int selectedIndex = ShapeMap.currentIndex(stack);
            cir.setReturnValue(Optional.of(new ShapeTooltip(stacks, Math.max(selectedIndex, 0))));
        }
    }
}
