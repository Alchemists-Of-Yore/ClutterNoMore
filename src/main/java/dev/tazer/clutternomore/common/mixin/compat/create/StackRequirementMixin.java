package dev.tazer.clutternomore.common.mixin.compat.create;

import dev.tazer.clutternomore.common.mixin.annotation.IfModPresent;
//? if =1.20.1 && forge || =1.21.1 && neoforge {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@IfModPresent("create")
@Mixin(targets = "com.simibubi.create.content.schematics.requirement.ItemRequirement$StackRequirement")
public class StackRequirementMixin {
    //? if =1.20.1 && forge || =1.21.1 && neoforge {
    /*@WrapOperation(method = "matches", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isSameItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z"))
    public boolean alsoIfInSameSet(ItemStack first, ItemStack second, Operation<Boolean> original) {
        return original.call(first, second) || ShapeMap.inSameShapeSet(first.getItem(), second.getItem());
    }
    *///?}
}
