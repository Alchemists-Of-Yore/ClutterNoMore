//? if forge && =1.20.1 {
package dev.tazer.clutternomore.common.mixin.dev;

import dev.tazer.clutternomore.common.mixin.annotation.IfDevEnvironment;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackLinkedSet;
import it.unimi.dsi.fastutil.Hash;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//? if forge {
/*import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
*///?}

import java.util.*;

@IfDevEnvironment
@Mixin(value = ShapeMap.class, remap = false)
public class ShapeMapHashContractMixin {
    @Inject(method = "setMappings", at = @At("RETURN"), remap = false)
    private static void cnm$verifyItemStackHashContract(List<ShapeMap.Mapping> mappings, boolean detailedLogs, CallbackInfo ci) {
        Iterator<Map.Entry<Item, Item>> shapes = ShapeMap.inverseView().entrySet().iterator();
        if (!shapes.hasNext()) return;

        Map.Entry<Item, Item> mapping = shapes.next();
        ItemStack shape = new ItemStack(mapping.getKey());
        ItemStack parent = new ItemStack(mapping.getValue());

        if (!ItemStack.isSameItemSameTags(shape, parent)) {
            throw new IllegalStateException("Before/after regression setup requires shape and parent stacks to compare equally");
        }
        if (cnm$vanillaHash(shape) == cnm$vanillaHash(parent)) {
            throw new IllegalStateException("Before regression was not reproduced: equal shape and parent stacks unexpectedly have equal vanilla hashes");
        }
        cnm$verifyBrokenStrategyReproducesIteratorCrash();

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
        ClutterNoMore.LOGGER.info("[HashContractRegression] BEFORE reproduced: equal shape/parent stacks had different vanilla hashes; AFTER verified: normalized hashes match and FFAPI iterator removal completed");
        *///?}
    }

    private static int cnm$vanillaHash(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return 31 * (31 + stack.getItem().hashCode()) + (tag == null ? 0 : tag.hashCode());
    }

    private static void cnm$verifyBrokenStrategyReproducesIteratorCrash() {
        Hash.Strategy<ItemStack> brokenStrategy = new Hash.Strategy<>() {
            @Override
            public int hashCode(ItemStack stack) {
                return stack == null ? 0 : cnm$vanillaHash(stack);
            }

            @Override
            public boolean equals(ItemStack first, ItemStack second) {
                return ItemStackLinkedSet.TYPE_AND_TAG.equals(first, second);
            }
        };

        List<ItemStack> stacks = new ArrayList<>();
        BuiltInRegistries.ITEM.forEach(item -> stacks.add(new ItemStack(item)));
        for (int offset = 0; offset < Math.min(stacks.size(), 128); offset++) {
            MutableHashedLinkedMap<ItemStack, Integer> entries = new MutableHashedLinkedMap<>(brokenStrategy);
            for (int index = 0; index < stacks.size(); index++) {
                entries.put(stacks.get((index + offset) % stacks.size()), index);
            }
            try {
                for (Iterator<Map.Entry<ItemStack, Integer>> iterator = entries.iterator(); iterator.hasNext(); ) {
                    iterator.next();
                    iterator.remove();
                }
            } catch (ConcurrentModificationException expected) {
                return;
            }
        }
        throw new IllegalStateException("Before regression did not reproduce MutableHashedLinkedMap iterator removal crash");
    }
}
//?} else {
/*package dev.tazer.clutternomore.common.mixin.dev;

import dev.tazer.clutternomore.common.mixin.annotation.IfDevEnvironment;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import org.spongepowered.asm.mixin.Mixin;

@IfDevEnvironment
@Mixin(value = ShapeMap.class, remap = false)
public class ShapeMapHashContractMixin {
}
*///?}
