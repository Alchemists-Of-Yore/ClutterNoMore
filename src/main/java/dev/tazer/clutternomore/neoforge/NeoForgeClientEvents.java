package dev.tazer.clutternomore.neoforge;
//? if neoforge {
/*import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.ClientShapeTooltip;
import dev.tazer.clutternomore.client.assets.AssetGenerator;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = ClutterNoMore.MODID, value = Dist.CLIENT)
public class NeoForgeClientEvents {

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
        ClutterNoMoreClient.onKeyInput(event.getKey(), event.getAction());
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.MouseButton.Post event) {
        ClutterNoMoreClient.onKeyInput(event.getButton(), event.getAction());
    }

    @SubscribeEvent
    public static void onMouseScrolling(InputEvent.MouseScrollingEvent event) {
        event.setCanceled(ClutterNoMoreClient.onMouseScrolling(event.getScrollDeltaY()));
    }

    @SubscribeEvent
    public static void onScreenScroll(ScreenEvent.MouseScrolled.Post event) {
        ClutterNoMoreClient.allowScreenScroll(event.getScreen(), event.getMouseX(), event.getMouseY(), event.getScrollDeltaX(), event.getScrollDeltaY());
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.KeyPressed.Post event) {
        ClutterNoMoreClient.onKeyPress(event.getScreen(), event.getKeyCode());
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.MouseButtonPressed.Post event) {
        ClutterNoMoreClient.onKeyPress(event.getScreen(), event.getButton());
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.KeyReleased.Post event) {
        ClutterNoMoreClient.onKeyReleased(event.getKeyCode());
    }

    @SubscribeEvent
    public static void onScreenInput(ScreenEvent.MouseButtonReleased.Post event) {
        ClutterNoMoreClient.onKeyReleased(event.getButton());
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        ClutterNoMoreClient.onRenderGui(event.getGuiGraphics(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        ClutterNoMoreClient.onPlayerTick(Minecraft.getInstance());
    }

    @SubscribeEvent
    private static void clientSetup(FMLClientSetupEvent event) {
        ClutterNoMoreClient.clientStarted(Minecraft.getInstance());
    }
}
*///?}