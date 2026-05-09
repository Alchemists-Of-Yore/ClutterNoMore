package dev.tazer.clutternomore.common.mixin.client;

//? if >1.20.1 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
//?}

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@Pseudo
@Mixin(targets = "net.minecraft.client.multiplayer.SessionSearchTrees")
public class SessionSearchTreesMixin {
    //? if >1.20.1 {
    @WrapOperation(method = "*", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getTooltipLines(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;)Ljava/util/List;"))
    private static List<Component> cnm$getTooltipLines(ItemStack instance, Item.TooltipContext context, Player player, TooltipFlag flag, Operation<List<Component>> original) {
        List<Component> tooltipLines = original.call(instance, context, player, flag);
        for (String alias : ShapeMap.getSearchAliases(instance.getItem())) tooltipLines.add(Component.literal(alias));
        return tooltipLines;
    }
    //?}
}
