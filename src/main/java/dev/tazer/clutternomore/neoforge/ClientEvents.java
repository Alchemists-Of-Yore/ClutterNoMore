package dev.tazer.clutternomore.neoforge;
//? if neoforge {
/*import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.ClientShapeTooltip;
import dev.tazer.clutternomore.client.assets.AssetGenerator;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import dev.tazer.clutternomore.common.shape_map.ShapeMapFileHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lwjgl.glfw.GLFW;

import static dev.tazer.clutternomore.ClutterNoMoreClient.*;

@EventBusSubscriber(modid = ClutterNoMore.MODID, value = Dist.CLIENT)
public class ClientEvents {

    public static final Lazy<KeyMapping> SHAPE_KEY = Lazy.of(() -> new KeyMapping(
            "key.clutternomore.change_block_shape",
            GLFW.GLFW_KEY_LEFT_ALT,
            "key.categories.inventory"
    ));

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(SHAPE_KEY.get());
    }

    @SubscribeEvent
    public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(ShapeTooltip.class, ClientShapeTooltip::new);
    }

    @SubscribeEvent
    public static void onItemTooltips(ItemTooltipEvent event) {
        ClutterNoMoreClient.onItemTooltips(event.getItemStack(), event.getContext(), event.getFlags(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        int action = event.getAction();
        if (event.getKey() == SHAPE_KEY.get().getKey().getValue()) {
            ClutterNoMoreClient.onKeyInput(action);
        }
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.MouseButton.Post event) {
        int action = event.getAction();
        if (event.getButton() == SHAPE_KEY.get().getKey().getValue()) {
            ClutterNoMoreClient.onKeyInput(action);
        }
    }

    @SubscribeEvent
    public static void onMouseScrolling(InputEvent.MouseScrollingEvent event) {
        int direction = (int) event.getScrollDeltaY();
        if (OVERLAY != null) {
            OVERLAY.onMouseScrolled(direction);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onScreenScroll(ScreenEvent.MouseScrolled.Post event) {
        if (showTooltip) {
            if (event.getScreen() instanceof AbstractContainerScreen<?> screen) {
                Slot slot = screen.getSlotUnderMouse();
                Player player = screen.getMinecraft().player;
                if (slot != null && slot.allowModification(player)) {
                    ItemStack heldStack = slot.getItem();
                    if (ShapeMap.contains(heldStack.getItem())) {
                        switchShapeInSlot(player, screen.getMenu().containerId, slot.getSlotIndex(), heldStack, (int) event.getScrollDeltaY());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.KeyPressed.Post event) {
        if (event.getKeyCode() == SHAPE_KEY.get().getKey().getValue()) {
            ClutterNoMoreClient.onKeyPress(event.getScreen());
        }
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.MouseButtonPressed.Post event) {
        if (event.getButton() == SHAPE_KEY.get().getKey().getValue()) {
            ClutterNoMoreClient.onKeyPress(event.getScreen());
        }
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.KeyReleased.Post event) {
        if (event.getKeyCode() == SHAPE_KEY.get().getKey().getValue()) {
            ClutterNoMoreClient.onKeyRelease();
        }
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.MouseButtonReleased.Post event) {
        if (event.getButton() == SHAPE_KEY.get().getKey().getValue()) {
            ClutterNoMoreClient.onKeyRelease();
        }
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (OVERLAY != null && OVERLAY.render) {
            OVERLAY.render(event.getGuiGraphics(), event.getPartialTick().getGameTimeDeltaTicks());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if (event.getEntity() instanceof LocalPlayer && OVERLAY != null) {
            if (!OVERLAY.shouldStayOpenThisTick()) OVERLAY = null;
        }
    }
}
*///?}