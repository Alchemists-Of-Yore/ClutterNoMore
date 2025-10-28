package dev.tazer.clutternomore.fabric;

//? fabric {

import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.assets.AssetGenerator;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;

import static dev.tazer.clutternomore.ClutterNoMore.MODID;

public class FabricClientEntrypoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClutterNoMoreClient.init();
        ClientEvents.registerKeyMappings();
        TooltipComponentCallback.EVENT.register(ClientEvents::registerTooltipComponent);
        ItemTooltipCallback.EVENT.register(ClutterNoMoreClient::onItemTooltips);
        HudRenderCallback.EVENT.register(ClientEvents::onRenderGui);
        ClientTickEvents.START_CLIENT_TICK.register(ClientEvents::onPlayerTick);
        ScreenEvents.AFTER_INIT.register(this::afterInitScreen);
        ClientLifecycleEvents.CLIENT_STARTED.register((minecraft -> {
            AssetGenerator.generate();
        }));
    }

    private void afterInitScreen(Minecraft minecraft, Screen screen, int i, int i1) {
        if (screen instanceof AbstractContainerScreen<?>) {
            ScreenKeyboardEvents.afterKeyPress(screen).register(ClientEvents::onScreenInputKeyPressedPost);
            ScreenKeyboardEvents.afterKeyRelease(screen).register(ClientEvents::onScreenInputKeyReleasedPost);
            ScreenMouseEvents.afterMouseClick(screen).register(ClientEvents::onScreenInputMouseButtonPressedPost);
            ScreenMouseEvents.afterMouseRelease(screen).register(ClientEvents::onScreenInputMouseButtonReleasedPost);
            ScreenMouseEvents.allowMouseScroll(screen).register(ClientEvents::allowScreenScroll);
        }
    }
}
//?}