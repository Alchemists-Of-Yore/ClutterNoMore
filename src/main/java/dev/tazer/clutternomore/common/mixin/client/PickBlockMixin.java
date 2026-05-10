package dev.tazer.clutternomore.common.mixin.client;

//? if forge {
/*import dev.tazer.clutternomore.forge.networking.ChangeStackPacket;
import dev.tazer.clutternomore.forge.networking.ForgeNetworking;
*///?} else if fabric {
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
//?} else if neoforge {
/*import java.util.Objects;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
*///?}
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import java.util.List;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Inventory.class)
public abstract class PickBlockMixin {
    //? if <1.21.2 {
    /*@WrapMethod(method = "findSlotMatchingItem")
    private int cnm$pickBlock(ItemStack targetStack, Operation<Integer> original) {
        int exactIndex = original.call(targetStack);
        Inventory inventory = (Inventory)(Object)this;

        if (exactIndex != -1) {
            ItemStack slotStack = inventory.items.get(exactIndex);

            if (ShapeMap.inSameShapeSet(targetStack.getItem(), slotStack.getItem())) {
                List<Item> shapes = ShapeMap.getShapes(slotStack.getItem());
                int shapeIdx = shapes.indexOf(targetStack.getItem());
                if (shapeIdx < 0) return exactIndex;

                ItemStack replaced = targetStack.copyWithCount(slotStack.getCount());
                inventory.items.set(exactIndex, replaced);
                int menuSlot = exactIndex < 9 ? exactIndex + 36 : exactIndex;
                int containerId = Minecraft.getInstance().player.inventoryMenu.containerId;
                //? if fabric {
                ClientPlayNetworking.send(new ChangeStackPayload(containerId, menuSlot, shapeIdx));
                //?}
                //? if neoforge {
                /^Objects.requireNonNull(Minecraft.getInstance().getConnection()).send(new ChangeStackPayload(containerId, menuSlot, shapeIdx));
                ^///?}
                //? if forge {
                /^ForgeNetworking.INSTANCE.sendToServer(new ChangeStackPacket(containerId, menuSlot, shapeIdx));
                ^///?}
            }
        }

        return exactIndex;
    }
    *///?}
}
