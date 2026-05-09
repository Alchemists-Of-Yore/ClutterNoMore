package dev.tazer.clutternomore.common.mixin.client;

//? if >1.21.2 {
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//?} else {
/*//? if forge {
/^import dev.tazer.clutternomore.forge.networking.ForgeNetworking;
^///?}
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.Redirect;
//? if neoforge {
/^import java.util.Objects;
^///?}
*///?}
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
//? if >1.20.1 {
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
//?} else {
/*import dev.tazer.clutternomore.forge.networking.ChangeStackPacket;
 *///?}

//? if <1.21.2 && fabric
/*import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;*/
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//? if >1.21.2 {
@Mixin(ServerGamePacketListenerImpl.class)
//?} else {
/*@Mixin(Minecraft.class)
 *///?}
public abstract class PickBlockMixin {

    //? if <1.21.2 {
    /*@Redirect(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"))
    private int pickBlock(Inventory inventory, ItemStack targetStack) {
        int exactIndex = inventory.findSlotMatchingItem(targetStack);

        if (exactIndex != -1) {
            ItemStack slotStack = inventory.
            //? if >1.21.2 {
            /^getNonEquipmentItems()
            ^///?} else {
            items
            //?}
            .get(exactIndex);

            if (slotStack.getItem() != targetStack.getItem() && ShapeMap.inSameShapeSet(targetStack.getItem(), slotStack.getItem())) {
                ItemStack replaced = targetStack.copyWithCount(slotStack.getCount());
                int menuSlot = exactIndex < 9 ? exactIndex + 36 : exactIndex;
                int containerId = Minecraft.getInstance().player.inventoryMenu.containerId;
                //? if fabric || neoforge {

                //? if neoforge {
                /^Objects.requireNonNull(((Minecraft) (Object) this).getConnection())
                ^///?} else {
                ClientPlayNetworking
                //?}
                        .send(new ChangeStackPayload(containerId, menuSlot, replaced));
                //?} else if forge {
                /^ForgeNetworking.INSTANCE.sendToServer(new ChangeStackPacket(containerId, menuSlot, replaced));
                ^///?}
            }
        }

        return exactIndex;
    }
    *///?} else {
    @Inject(method = "tryPickItem", at = @At("TAIL"))
    private void convertPickedItem(ItemStack stack, CallbackInfo ci) {
        if (!ShapeMap.contains(stack.getItem())) return;
        ServerGamePacketListenerImpl self = (ServerGamePacketListenerImpl) (Object) this;
        Inventory inventory = self.getPlayer().getInventory();
        int selected = inventory.getSelectedSlot();
        ItemStack slotStack = inventory.getItem(selected);
        if (slotStack.getItem() == stack.getItem()) return;
        if (!ShapeMap.inSameShapeSet(stack.getItem(), slotStack.getItem())) return;
        ItemStack replaced = stack.copyWithCount(slotStack.getCount());
        inventory.setItem(selected, replaced);
        self.send(new ClientboundSetPlayerInventoryPacket(selected, replaced));
    }
    //?}
}
