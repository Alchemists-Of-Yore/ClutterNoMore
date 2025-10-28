package dev.tazer.clutternomore.fabric;

//? if fabric {

import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.ClientShapeTooltip;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.mixin.SlotAccessor;
import dev.tazer.clutternomore.common.mixin.screen.ScreenAccessor;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
//? if >1.21.8 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import static dev.tazer.clutternomore.ClutterNoMoreClient.*;


public class ClientEvents {

    public static final KeyMapping SHAPE_KEY = new KeyMapping(
            "key.clutternomore.change_block_shape",
            GLFW.GLFW_KEY_LEFT_ALT,
            //? if >1.21.8 {
            KeyMapping.Category.INVENTORY
            //?} else {
            /*"key.categories.inventory"
            *///?}
    );

    public static void registerKeyMappings() {
        KeyBindingHelper.registerKeyBinding(SHAPE_KEY);
    }

    public static ClientShapeTooltip registerTooltipComponent(TooltipComponent tooltipComponent) {
        if (tooltipComponent instanceof ShapeTooltip shapeTooltip) {
            return new ClientShapeTooltip(shapeTooltip);
        }
        return null;
    }

    public static void onKeyInput(int keyCode, int keyAction) {
        if (keyCode == KeyBindingHelper.getBoundKeyOf(SHAPE_KEY).getValue()) {
            ClutterNoMoreClient.onKeyInput(keyAction);
        }
    }

    public static boolean onMouseScrolling(double yOffset) {
        int direction = (int) yOffset;
        if (OVERLAY != null) {
            OVERLAY.onMouseScrolled(direction);
            return true;
        }
        return false;
    }

    public static boolean allowScreenScroll(Screen pScreen, double mouseX, double mouseY, double scrollX, double scrollY) {
        if (showTooltip) {
            if (pScreen instanceof AbstractContainerScreen<?> screen) {
                Slot slot = ((ScreenAccessor) screen).getSlotUnderMouse();
                Player player = Minecraft.getInstance().player;
                if (slot != null && slot.allowModification(player)) {
                    ItemStack heldStack = slot.getItem();
                    if (ShapeMap.contains(heldStack.getItem())) {
                        switchShapeInSlot(player, screen.getMenu().containerId, ((SlotAccessor) slot).getSlotIndex(), heldStack, (int) scrollY);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static void onScreenInputKeyPressedPost(Screen screen,
           //? if >1.21.8 {
           KeyEvent event
           //?} else {
           /*int keyCode, int scanCode, int modifiers
            *///?}
        ) {
        //? if >1.21.8
        var keyCode = event.input();
        if (keyCode == KeyBindingHelper.getBoundKeyOf(SHAPE_KEY).getValue()) {
            ClutterNoMoreClient.onKeyPress(screen);
        }
    }

    public static
    //? if >1.21.8 {
    boolean
    //?} else {
    /*void
    *///?}
    onScreenInputMouseButtonPressedPost(Screen screen,
           //? if >1.21.8 {
           MouseButtonEvent event, boolean doubleClick
           //?} else {
            /*double mouseX, double mouseY, int button
             *///?}
    ) {
        //? if >1.21.8
        var button = event.button();
        if (button == KeyBindingHelper.getBoundKeyOf(SHAPE_KEY).getValue()) {
            ClutterNoMoreClient.onKeyPress(screen);
        }
        //? if >1.21.8
        return false;
    }

    public static void onScreenInputKeyReleasedPost(Screen screen,
            //? if >1.21.8 {
            KeyEvent event
            //?} else {
            /*int keyCode, int scanCode, int modifiers
             *///?}
        ) {
        //? if >1.21.8
        var keyCode = event.input();
        if (keyCode == KeyBindingHelper.getBoundKeyOf(SHAPE_KEY).getValue()) {
            ClutterNoMoreClient.onKeyRelease();
        }
    }

    public static
    //? if >1.21.8 {
    boolean
    //?} else {
    /*void
    *///?}
    onScreenInputMouseButtonReleasedPost(Screen screen,
            //? if >1.21.8 {
            MouseButtonEvent event, boolean b
            //?} else {
            /*double mouseX, double mouseY, int button
             *///?}
    ) {
        //? if >1.21.8
        var button = event.button();
        if (button == KeyBindingHelper.getBoundKeyOf(SHAPE_KEY).getValue()) {
            ClutterNoMoreClient.onKeyRelease();
        }
        //? if >1.21.8
        return false;
    }

    public static void onRenderGui(GuiGraphics guiGraphics, DeltaTracker tracker) {
        if (OVERLAY != null && OVERLAY.render) {
            OVERLAY.render(guiGraphics, tracker.getGameTimeDeltaTicks());
        }
    }

    public static void onPlayerTick(Minecraft minecraft) {
        if (OVERLAY != null) {
            if (!OVERLAY.shouldStayOpenThisTick()) OVERLAY = null;
        }
    }
}
//?}