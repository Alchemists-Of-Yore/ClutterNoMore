package dev.tazer.clutternomore.common.mixin.client;

//? if <1.21.2 {
/*//? if forge {
/^import dev.tazer.clutternomore.forge.networking.ChangeStackPacket;
/^import dev.tazer.clutternomore.forge.networking.ForgeNetworking;
^///?} else {
/^import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
^///?}
//? if fabric {
/^import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
^///?}
//? if neoforge {
/^import java.util.Objects;
^///?}
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
*///?}

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Minecraft.class)
public abstract class PickBlockMixin {
    //? if <1.21.2 {
    /*@Redirect(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"))
    private int cnm$pickBlock(Inventory inventory, ItemStack targetStack) {
        int exactIndex = inventory.findSlotMatchingItem(targetStack);

        if (exactIndex != -1) {
            ItemStack slotStack = inventory.items.get(exactIndex);

            if (ShapeMap.inSameShapeSet(targetStack.getItem(), slotStack.getItem())) {
                ItemStack replaced = targetStack.copyWithCount(slotStack.getCount());
                int menuSlot = exactIndex < 9 ? exactIndex + 36 : exactIndex;
                int containerId = Minecraft.getInstance().player.inventoryMenu.containerId;
                //? if fabric {
                /^ClientPlayNetworking.send(new ChangeStackPayload(containerId, menuSlot, replaced));
                ^///?}
                //? if neoforge {
                /^Objects.requireNonNull(((Minecraft) (Object) this).getConnection()).send(new ChangeStackPayload(containerId, menuSlot, replaced));
                ^///?}
                //? if forge {
                /^ForgeNetworking.INSTANCE.sendToServer(new ChangeStackPacket(containerId, menuSlot, replaced));
                ^///?}
            }
        }

        return exactIndex;
    }
    *///?}
}
