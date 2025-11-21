package dev.tazer.clutternomore.fabric;

//? fabric {

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.assets.AssetGenerator;
import dev.tazer.clutternomore.common.networking.ShapeMapPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
//? if >1.21.6 {
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
//?} else {
/*import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
*///?}
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;

import static dev.tazer.clutternomore.fabric.FabricClientEvents.SHAPE_KEY;

public class FabricClientEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClutterNoMoreClient.init();
        KeyBindingHelper.registerKeyBinding(SHAPE_KEY);
        TooltipComponentCallback.EVENT.register(FabricClientEvents::registerTooltipComponent);
        ItemTooltipCallback.EVENT.register(ClutterNoMoreClient::onItemTooltips);
        //? if >1.21.6 {
        HudElementRegistry.addLast(ClutterNoMore.location("overlay"), ClutterNoMoreClient::onRenderGui);
        //?} else {
        /*HudRenderCallback.EVENT.register(ClutterNoMoreClient::onRenderGui);
        *///?}
        ClientTickEvents.START_CLIENT_TICK.register(ClutterNoMoreClient::onPlayerTick);
        ScreenEvents.AFTER_INIT.register(this::afterInitScreen);
        ClientLifecycleEvents.CLIENT_STARTED.register(ClutterNoMoreClient::clientStarted);
        ClientPlayNetworking.registerGlobalReceiver(ShapeMapPayload.TYPE, ShapeMapPayload::handleDataOnClient);
    }

    private void afterInitScreen(Minecraft minecraft, Screen screen, int i, int i1) {
        if (screen instanceof AbstractContainerScreen<?>) {
            ScreenKeyboardEvents.afterKeyPress(screen).register(FabricClientEvents::onScreenInputKeyPressedPost);
            ScreenKeyboardEvents.afterKeyRelease(screen).register(FabricClientEvents::onScreenInputKeyReleasedPost);
            ScreenMouseEvents.afterMouseClick(screen).register(FabricClientEvents::onScreenInputMouseButtonPressedPost);
            ScreenMouseEvents.afterMouseRelease(screen).register(FabricClientEvents::onScreenInputMouseButtonReleasedPost);
            ScreenMouseEvents.allowMouseScroll(screen).register(ClutterNoMoreClient::allowScreenScroll);
        }
    }
}
//?}