package dev.tazer.clutternomore.fabric;

//? if fabric {

import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.client.ClientShapeTooltip;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.screens.Screen;
//? if >1.21.8 {
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.lwjgl.glfw.GLFW;


public class FabricClientEvents {

    public static final KeyMapping SHAPE_KEY = new KeyMapping(
            "key.clutternomore.change_block_shape",
            GLFW.GLFW_KEY_LEFT_ALT,
            //? if >1.21.8 {
            KeyMapping.Category.INVENTORY
            //?} else {
            /*"key.categories.inventory"
            *///?}
    );

    public static ClientShapeTooltip registerTooltipComponent(TooltipComponent tooltipComponent) {
        if (tooltipComponent instanceof ShapeTooltip shapeTooltip) {
            return new ClientShapeTooltip(shapeTooltip);
        }
        return null;
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
        ClutterNoMoreClient.onKeyPress(screen, keyCode);
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
        //? if >1.21.8 {
        ClutterNoMoreClient.onKeyPress(screen, event.button());
        return false;
        //?} else {
        /*ClutterNoMoreClient.onKeyPress(screen, button);
        *///?}
    }

    public static void onScreenInputKeyReleasedPost(Screen screen,
            //? if >1.21.8 {
            KeyEvent event
            //?} else {
            /*int keyCode, int scanCode, int modifiers
             *///?}
        ) {
        //? if >1.21.8 {
        var keyCode = event.input();
        //?}
        ClutterNoMoreClient.onKeyReleased(keyCode);
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
        //? if >1.21.8 {
        return ClutterNoMoreClient.onKeyReleased(event.button());
        //?} else {
        /*ClutterNoMoreClient.onKeyReleased(button);
        *///?}
    }
}
//?}