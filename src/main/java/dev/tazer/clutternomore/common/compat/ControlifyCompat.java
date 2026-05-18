package dev.tazer.clutternomore.common.compat;

import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.InputBindingSupplier;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.entrypoint.InitContext;
import dev.isxander.controlify.api.entrypoint.PreInitContext;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static dev.tazer.clutternomore.ClutterNoMoreClient.*;
//? forge {
/*import static dev.tazer.clutternomore.forge.ForgeClientEvents.SHAPE_KEY;
import dev.isxander.controlify.api.bind.ControlifyBindApi;
*///?} neoforge
//import static dev.tazer.clutternomore.neoforge.NeoForgeClientEvents.SHAPE_KEY;
//? fabric
import static dev.tazer.clutternomore.fabric.FabricClientEvents.SHAPE_KEY;

public class ControlifyCompat implements ControlifyEntrypoint {

    public static InputBindingSupplier SHAPE_KEY_BINDING;

    public static boolean currentInputModeIsController() {
        return ControlifyApi.get().currentInputMode().isController();
    }

    public static void checkForControllerInput(Minecraft minecraft) {
        if (currentInputModeIsController()) {
            var controller = ControlifyApi.get().getCurrentController();
            if (minecraft.player != null && controller.isPresent()) {
                var key = SHAPE_KEY_BINDING.on(controller.get());
                if (minecraft.screen != null) return;

                if (key.digitalNow() && !key.justPressed()) keyHeld = true;
                else if (key.justReleased()) keyHeld = false;

                Player player = minecraft.player;
                if (player == null) return;
                ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (!ShapeMap.contains(heldStack.getItem())) return;

                switch (CLIENT_CONFIG.HOLD.value()) {
                    case HOLD -> {
                        if (OVERLAY == null && key.digitalNow())
                            OVERLAY = new ShapeSwitcherOverlay(minecraft, heldStack, true);
                        else if (key.justReleased()) OVERLAY = null;
                    }
                    case TOGGLE -> {
                        if (key.justTapped() && !keyHeld) {
                            if (OVERLAY == null) OVERLAY = new ShapeSwitcherOverlay(minecraft, heldStack, true);
                            else OVERLAY = null;
                        }
                    }
                    case PRESS -> {
                        if (key.justTapped() && !keyHeld) {
                            if (OVERLAY == null)
                                OVERLAY = new ShapeSwitcherOverlay(minecraft, heldStack, false);
                            OVERLAY.onMouseScrolled(-1);
                            OVERLAY = null;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onControllersDiscovered(ControlifyApi controlify) {

    }

    @Override
    public void onControlifyInit(InitContext context) {

    }

    @Override
    public void onControlifyPreInit(PreInitContext context) {
        //? if >1.21 {
        var bindings = context.bindings();
        //?} else {
        /*var bindings = ControlifyBindApi.get();
        *///?}

        SHAPE_KEY_BINDING = bindings.registerBinding((inputBindingBuilder -> {
            return inputBindingBuilder.id(ClutterNoMore.location("change_block_shape"))
                    .name(Component.translatable("key.clutternomore.change_block_shape"))
                    .addKeyCorrelation(SHAPE_KEY
                    //? if !fabric
                    //.get()
                    )
                    .category(
                            //? if >1.21.8 {
                            net.minecraft.client.KeyMapping.Category.INVENTORY.label()
                            //?} else {
                            /*Component.translatable("key.categories.inventory")
                            *///?}
                    );
        }));
    }
}
