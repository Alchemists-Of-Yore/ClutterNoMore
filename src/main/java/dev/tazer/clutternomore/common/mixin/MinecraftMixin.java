package dev.tazer.clutternomore.common.mixin;

//? if >1.21.2 {
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
//?} else {
/*//? if forge {
/^import dev.tazer.clutternomore.forge.networking.ForgeNetworking;
^///?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Objects;
*///?}
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
//? if >1.20.1 {
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
//?} else {
/*import dev.tazer.clutternomore.forge.networking.ChangeStackPacket;
*///?}

//? if fabric
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
//? if neoforge {
/*import net.neoforged.neoforge.network.PacketDistributor;
import java.util.Objects;
 *///?} else {

//?}
//? if >1.21.2 {
@Mixin(ServerGamePacketListenerImpl.class)
//?} else {
/*@Mixin(Minecraft.class)
*///?}
public abstract class MinecraftMixin {

    //FIXME
    //? if <1.21.2 {
    /*@Shadow
    @Nullable
    public abstract ClientPacketListener getConnection();
    @Redirect(method = "pickBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"))
    private int pickBlock(Inventory inventory, ItemStack targetStack) {
        int exactIndex = inventory.findSlotMatchingItem(targetStack);

        if (exactIndex != -1) {
            ItemStack slotStack = inventory.
            //? if >1.21.2 {
            getNonEquipmentItems()
            //?} else {
            /^items
            ^///?}
            .get(exactIndex);

            if (ShapeMap.inSameShapeSet(targetStack.getItem(), slotStack.getItem())) {
                ItemStack replaced = targetStack.copyWithCount(slotStack.getCount());
                //? if fabric || neoforge {
                
                //? if neoforge {
                /^Objects.requireNonNull(getConnection())
                ^///?} else {
                ClientPlayNetworking
                //?}
                        .send(new ChangeStackPayload(-1, exactIndex, replaced));
                //?} else if forge {
                /^ForgeNetworking.INSTANCE.sendToServer(new ChangeStackPacket(-1, exactIndex, replaced));
                ^///?}
            }
        }

        return exactIndex;
    }
    *///?} else {
    @WrapOperation(method = "tryPickItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;findSlotMatchingItem(Lnet/minecraft/world/item/ItemStack;)I"))
    private int pickBlock(Inventory inventory, ItemStack targetStack, Operation<Integer> original) {
        var p = (ServerGamePacketListenerImpl) (Object) this;
        int exactIndex = inventory.findSlotMatchingItem(targetStack);

        if (exactIndex != -1) {
            ItemStack slotStack = inventory.getNonEquipmentItems()
            .get(exactIndex);

            if (ShapeMap.inSameShapeSet(targetStack.getItem(), slotStack.getItem())) {
                ItemStack replaced = targetStack.copyWithCount(slotStack.getCount());
                inventory.setItem(exactIndex, replaced);
                p.send(new ClientboundSetPlayerInventoryPacket(exactIndex, replaced));
            }
        }
        return exactIndex;
    }
    //?}
}
