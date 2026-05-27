package dev.tazer.clutternomore.common.mixin.compat.create;

import dev.tazer.clutternomore.common.mixin.annotation.IfModPresent;
//? if =1.20.1 && forge || =1.21.1 && neoforge {
/*import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.simibubi.create.content.schematics.requirement.ItemRequirement;
import com.simibubi.create.content.schematics.requirement.ItemRequirement.ItemUseType;
import com.simibubi.create.content.schematics.requirement.ItemRequirement.StackRequirement;
import com.simibubi.create.content.schematics.requirement.ItemRequirement.StrictNbtStackRequirement;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;
*///?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@IfModPresent("create")
@Mixin(targets = "com.simibubi.create.content.schematics.cannon.MaterialChecklist")
public class MaterialChecklistMixin {
    //? if =1.20.1 && forge || =1.21.1 && neoforge {
    /*@WrapOperation(method = "require", at = @At(value = "INVOKE", target = "Lcom/simibubi/create/content/schematics/requirement/ItemRequirement;getRequiredItems()Ljava/util/List;"))
    private List<StackRequirement> cnm$rewriteToParents(ItemRequirement instance, Operation<List<StackRequirement>> original) {
        List<StackRequirement> items = original.call(instance);
        List<StackRequirement> rewritten = new ArrayList<>(items.size());
        for (StackRequirement sReq : items) {
            if (sReq.usage == ItemUseType.DAMAGE || sReq instanceof StrictNbtStackRequirement) {
                rewritten.add(sReq);
                continue;
            }
            rewritten.add(new StackRequirement(ShapeMap.transferStack(sReq.stack, 0), sReq.usage));
        }
        return rewritten;
    }
    *///?}
}
