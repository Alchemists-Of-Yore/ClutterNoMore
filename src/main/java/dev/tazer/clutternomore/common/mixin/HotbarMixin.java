package dev.tazer.clutternomore.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.fabric.ClientEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Minecraft.class)
public class HotbarMixin {
    @WrapOperation(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Inventory;setSelectedSlot(I)V"))
    private void pickBlock(Inventory instance, int selectedIndex, Operation<Void> original) {
        if (ClutterNoMoreClient.OVERLAY != null) {
            int maxIndex = ClutterNoMoreClient.OVERLAY.shapes.size() - 1;
            if (selectedIndex < 0) selectedIndex = 0;
            if (selectedIndex > maxIndex) selectedIndex = maxIndex;
            ClutterNoMoreClient.OVERLAY.changeSlot(selectedIndex);
        } else {
            original.call(instance, selectedIndex);
        }
    }
}
