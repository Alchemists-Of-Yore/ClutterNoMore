package dev.tazer.clutternomore.common.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.tazer.clutternomore.common.blocks.VerticalSlabBlock;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;

@Mixin(BlockItem.class)
public class BlockItemMixin {
    @Redirect(method = "place", at = @At(
            value = "INVOKE",
            //? if >1.20.1 {
            target = "Lnet/minecraft/world/item/ItemStack;consume(ILnet/minecraft/world/entity/LivingEntity;)V")
            //?} else {
            /*target = "Lnet/minecraft/world/item/ItemStack;shrink(I)V")
            *///?}
    )
    private void place(ItemStack instance,
                       int amount,
                       //? if >1.20.1 {
                       LivingEntity entity,
                       //?}
                       @Local(argsOnly = true) BlockPlaceContext context,
                       @Local(ordinal = 0) BlockState blockstate
    ) {
        boolean consume = true;
        if (ShapeMap.isShape(instance.getItem())) {
            Optional<SlabType> slabType = blockstate.getOptionalValue(SlabBlock.TYPE);
            Optional<Boolean> isDouble = blockstate.getOptionalValue(VerticalSlabBlock.DOUBLE);

            if (slabType.isPresent()) consume = slabType.get() != SlabType.DOUBLE;
            if (isDouble.isPresent()) consume = !isDouble.get();
        }

        if (consume)
            //? if >1.20.1 {
            instance.consume(amount, entity);
            //?} else {
            /*instance.shrink(amount);
            *///?}
    }
}
