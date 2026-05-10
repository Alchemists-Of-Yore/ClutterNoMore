package dev.tazer.clutternomore.common.mixin.compat.create;

import dev.tazer.clutternomore.common.mixin.annotation.IfModPresent;
//? if =1.20.1 && forge || =1.21.1 && neoforge {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.content.schematics.requirement.ItemRequirement.StackRequirement;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@IfModPresent("create")
@Mixin(targets = "com.simibubi.create.content.schematics.requirement.ItemRequirement")
public class ItemRequirementMixin {
    //? if =1.20.1 && forge || =1.21.1 && neoforge {
    /*@Shadow
    protected List<StackRequirement> requiredItems;

    @Inject(method = "<init>(Ljava/util/List;)V", at = @At(value = "RETURN"))
    private void cnm$replaceWithParentBlocks(List<StackRequirement> requiredItems, CallbackInfo ci) {
        ArrayList<StackRequirement> newRequired = new ArrayList<>();
        for (StackRequirement sReq : requiredItems) {
            if (sReq.usage == ItemRequirement.ItemUseType.DAMAGE || sReq instanceof ItemRequirement.StrictNbtStackRequirement) {
                newRequired.add(sReq);
                continue;
            }

            ItemStack originalStack = sReq.stack;
            ItemStack parentStack = ShapeMap.getParent(originalStack.getItem()).getDefaultInstance();
            parentStack.setCount(originalStack.getCount());

            newRequired.add(new StackRequirement(parentStack, sReq.usage));
        }
        this.requiredItems = newRequired;
    }

    @WrapOperation(method = "defaultOf", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;hasProperty(Lnet/minecraft/world/level/block/state/properties/Property;)Z"))
    private static boolean cnm$allSlabsCostOne(BlockState instance, Property<SlabType> property, Operation<Boolean> original) {
        return false;
    }
    *///?}
}
