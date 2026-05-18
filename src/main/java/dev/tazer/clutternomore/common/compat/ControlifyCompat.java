package dev.tazer.clutternomore.common.compat;

import dev.isxander.controlify.Controlify;
import dev.isxander.controlify.api.ControlifyApi;
import dev.isxander.controlify.api.bind.ControlifyBindApi;
import dev.isxander.controlify.api.entrypoint.ControlifyEntrypoint;
import dev.isxander.controlify.api.entrypoint.InitContext;
import dev.isxander.controlify.api.entrypoint.PreInitContext;
import dev.isxander.controlify.bindings.BindContext;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static dev.tazer.clutternomore.ClutterNoMoreClient.*;
//? forge
//import static dev.tazer.clutternomore.forge.ForgeClientEvents.SHAPE_KEY;
//? neoforge
//import static dev.tazer.clutternomore.neoforge.NeoForgeClientEvents.SHAPE_KEY;
//? fabric
import static dev.tazer.clutternomore.fabric.FabricClientEvents.SHAPE_KEY;

public class ControlifyCompat implements ControlifyEntrypoint {

    public static final Identifier SHAPE_KEY_ID = ClutterNoMore.location("change_block_shape");
    public static final Identifier OVERLAY_OPEN = ClutterNoMore.location("overlay_open");
    public static final BindContext OVERLAY_CONTEXT = new BindContext(OVERLAY_OPEN, (minecraft) -> OVERLAY != null && OVERLAY.render);

    public static boolean currentInputModeIsController() {
        return Controlify.instance().currentInputMode().isController();
    }

    public static void checkForControllerInput(Minecraft minecraft) {
        if (currentInputModeIsController()) {
            var controller = Controlify.instance().getCurrentController();
            if (minecraft.player != null && controller.isPresent()) {
                var key = ControlifyBindApi.get().createSupplier(ControlifyCompat.SHAPE_KEY_ID).on(controller.get());
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
        context.bindings().registerBindContext(OVERLAY_CONTEXT);

        context.bindings().registerBinding((inputBindingBuilder -> {
            return inputBindingBuilder.id(SHAPE_KEY_ID)
                    .allowedContexts(BindContext.IN_GAME, OVERLAY_CONTEXT)
                    .name(Component.translatable("key.clutternomore.change_block_shape"))
                    .addKeyCorrelation(SHAPE_KEY
                    //? if !fabric
                    //.get()
                    )
                    .category(
                            //? if >1.21.8 {
                            KeyMapping.Category.INVENTORY.label()
                            //?} else {
                            /*Component.translatable("key.categories.inventory")
                            *///?}
                    );
        }));
    }
}
