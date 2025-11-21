package dev.tazer.clutternomore.forge;
//? if forge {
/*import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.ClientShapeTooltip;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

import static dev.tazer.clutternomore.ClutterNoMoreClient.*;

@Mod.EventBusSubscriber(modid = ClutterNoMore.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ForgeClientEvents {
    @Mod.EventBusSubscriber(modid = ClutterNoMore.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBus {
        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(SHAPE_KEY.get());
        }

        @SubscribeEvent
        public static void registerTooltipComponent(RegisterClientTooltipComponentFactoriesEvent event) {
            ClutterNoMore.LOGGER.info("Registering tooltip component factory");
            event.register(ShapeTooltip.class, ClientShapeTooltip::new);
        }
    }

    public static final Lazy<KeyMapping> SHAPE_KEY = Lazy.of(() ->
            new KeyMapping(
            "key.clutternomore.change_block_shape",
            GLFW.GLFW_KEY_LEFT_ALT,
            "key.categories.inventory"
    ));

    @SubscribeEvent
    public static void onItemTooltips(ItemTooltipEvent event) {
        ClutterNoMoreClient.onItemTooltips(event.getItemStack(), null, event.getFlags(), event.getToolTip());
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        ClutterNoMoreClient.onKeyInput(event.getKey(), event.getAction());
    }

    @SubscribeEvent
    public static void onKeyInputPost(InputEvent.MouseButton.Post event) {
        ClutterNoMoreClient.onKeyInput(event.getButton(), event.getAction());
    }

    @SubscribeEvent
    public static void onMouseScrolling(InputEvent.MouseScrollingEvent event) {
        event.setCanceled(ClutterNoMoreClient.onMouseScrolling(event.getScrollDelta()));
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
                        switchShapeInSlot(player, screen.getMenu().containerId, slot.getSlotIndex(), heldStack, (int) event.getScrollDelta());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onScreenKeyPressedPost(ScreenEvent.KeyPressed.Post event) {
        ClutterNoMoreClient.onKeyPress(event.getScreen(), event.getKeyCode());
    }

    @SubscribeEvent
    public static void onScreenMouseButtonPressedPost(ScreenEvent.MouseButtonPressed.Post event) {
        ClutterNoMoreClient.onKeyPress(event.getScreen(), event.getButton());
    }

    @SubscribeEvent
    public static void onScreenKeyReleasedPost(ScreenEvent.KeyReleased.Post event) {
        ClutterNoMoreClient.onKeyReleased(event.getKeyCode());
    }

    @SubscribeEvent
    public static void onScreenMouseButtonReleasedPost(ScreenEvent.MouseButtonReleased.Post event) {
        ClutterNoMoreClient.onKeyReleased(event.getButton());
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        if (OVERLAY != null && OVERLAY.render) {
            OVERLAY.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        ClutterNoMoreClient.onPlayerTick(Minecraft.getInstance());
    }

    public static void clientSetup(FMLClientSetupEvent event) {
        ClutterNoMoreClient.clientStarted(Minecraft.getInstance());
    }
}
*///?}