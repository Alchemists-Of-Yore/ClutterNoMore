package dev.tazer.clutternomore.common.mixin;

//? if >1.21.2 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
//?}

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class PickBlockMixin {
    //? if >1.21.2 {
    @WrapOperation(method = "tryPickItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"))
    private int cnm$pickBlock(Inventory inventory, ItemStack targetStack, Operation<Integer> original) {
        ServerGamePacketListenerImpl self = (ServerGamePacketListenerImpl) (Object) this;
        int exactIndex = original.call(inventory, targetStack);

        if (exactIndex != -1) {
            ItemStack slotStack = inventory.getNonEquipmentItems().get(exactIndex);

            if (ShapeMap.inSameShapeSet(targetStack.getItem(), slotStack.getItem())) {
                ItemStack replaced = targetStack.copyWithCount(slotStack.getCount());
                inventory.setItem(exactIndex, replaced);
                self.send(new ClientboundSetPlayerInventoryPacket(exactIndex, replaced));
            }
        }
        return exactIndex;
    }
    //?}
}
