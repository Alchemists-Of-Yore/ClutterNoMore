package dev.tazer.clutternomore.common.mixin.compat.create;

import dev.tazer.clutternomore.common.mixin.annotation.IfModPresent;
//? if =1.20.1 && forge || =1.21.1 && neoforge {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.injection.At;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@IfModPresent("create")
@Mixin(targets = "com.simibubi.create.content.schematics.requirement.ItemRequirement")
public class ItemRequirementMixin {
    //? if =1.20.1 && forge || =1.21.1 && neoforge {
    /*@WrapOperation(method = "defaultOf", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasProperty(Lnet/minecraft/world/level/block/state/properties/Property;)Z"))
    private static boolean cnm$allSlabsCostOne(BlockState instance, Property<SlabType> property, Operation<Boolean> original) {
        return false;
    }
    *///?}
}
