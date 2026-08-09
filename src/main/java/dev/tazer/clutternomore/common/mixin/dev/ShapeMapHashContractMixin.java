package dev.tazer.clutternomore.common.mixin.dev;

import dev.tazer.clutternomore.common.mixin.annotation.IfDevEnvironment;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if forge {
/*import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
*///?}

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@IfDevEnvironment
@Mixin(ShapeMap.class)
public class ShapeMapHashContractMixin {
    @Inject(method = "setMappings", at = @At("RETURN"))
    private static void cnm$verifyItemStackHashContract(List<ShapeMap.Mapping> mappings, boolean detailedLogs, CallbackInfo ci) {
        Iterator<Map.Entry<Item, Item>> shapes = ShapeMap.inverseView().entrySet().iterator();
        if (!shapes.hasNext()) return;

        Map.Entry<Item, Item> mapping = shapes.next();
        ItemStack shape = new ItemStack(mapping.getKey());
        ItemStack parent = new ItemStack(mapping.getValue());

        if (!ItemStackLinkedSet.TYPE_AND_TAG.equals(shape, parent)) {
            throw new IllegalStateException("Shape and parent stacks must compare equally");
        }
        if (ItemStackLinkedSet.TYPE_AND_TAG.hashCode(shape) != ItemStackLinkedSet.TYPE_AND_TAG.hashCode(parent)) {
            throw new IllegalStateException("Equal shape and parent stacks must have equal hashes");
        }

        //? if forge {
        /*
        ItemStack taggedShape = shape.copy();
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("clutternomore_hash_contract_test", true);
        taggedShape.setTag(tag);
        if (ItemStackLinkedSet.TYPE_AND_TAG.equals(taggedShape, parent)) {
            throw new IllegalStateException("Shape stack data must participate in equality");
        }

        MutableHashedLinkedMap<ItemStack, Integer> entries = new MutableHashedLinkedMap<>(ItemStackLinkedSet.TYPE_AND_TAG);
        entries.put(shape, 0);
        entries.put(parent, 1);
        for (Iterator<Map.Entry<ItemStack, Integer>> iterator = entries.iterator(); iterator.hasNext(); ) {
            iterator.next();
            iterator.remove();
        }
        if (!entries.isEmpty()) {
            throw new IllegalStateException("Shape stack map must be empty after iterator removal");
        }
        *///?}
    }
}
