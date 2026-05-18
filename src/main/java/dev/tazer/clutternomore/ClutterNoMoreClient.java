package dev.tazer.clutternomore;

import dev.tazer.clutternomore.client.ClientNetworking;
import dev.tazer.clutternomore.client.ShapeSwitcherOptionsScreen;
import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import dev.tazer.clutternomore.common.compat.ControlifyCompat;
import dev.tazer.clutternomore.common.compat.JEICompat;
//? if <1.21.4
//import dev.tazer.clutternomore.common.compat.EMICompat;
//? if >1.21.9
import dev.tazer.clutternomore.common.compat.RRVCompat;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.mixin.client.CreativeInventoryScreenAccessor;
import dev.tazer.clutternomore.common.mixin.client.CreativeSlotWrapperAccessor;
import dev.tazer.clutternomore.common.mixin.screen.ContainerScreenAccessor;
//? if !forge {
 import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
 import net.minecraft.client.DeltaTracker;
//?} else {
/*import dev.tazer.clutternomore.forge.networking.ChangeStackPayload;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
*///?}
//? if neoforge {
/*import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
*///?}
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.ChatFormatting;
import org.lwjgl.glfw.GLFW;

import java.util.List;

import static dev.tazer.clutternomore.ClutterNoMore.MODID;

public class ClutterNoMoreClient {
    public static boolean showTooltip = false;
    public static boolean keyHeld = false;
    public static ShapeSwitcherOverlay OVERLAY = null;
    public static final CNMConfig.ClientConfig CLIENT_CONFIG = CNMConfig.ClientConfig.createToml(Platform.INSTANCE.configPath(), MODID,  "client", CNMConfig.ClientConfig.class);

    public static void init() {
        //? neoforge
        //ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, ()-> (mod, screen) -> new ShapeSwitcherOptionsScreen(screen));
        //? forge
        //ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent)-> new ShapeSwitcherOptionsScreen(parent, minecraft.options)));
    }

    public static boolean isCreativeTabSlot(Slot slot) {
        return slot != null && slot.container == CreativeInventoryScreenAccessor.cnm$getContainer();
    }

    public static Slot hoveredSlot() {
        Screen screen = Minecraft.getInstance().screen;
        if (!(screen instanceof AbstractContainerScreen<?>)) return null;
        return ((ContainerScreenAccessor) screen).getSlotUnderMouse();
    }

    public static boolean isHoveringCreativeTabSlot() {
        return isCreativeTabSlot(hoveredSlot());
    }

    public static boolean isHoveringSwitchableSlot() {
        Slot slot = hoveredSlot();
        if (slot == null) return false;
        if (isCreativeTabSlot(slot)) return true;
        Player player = Minecraft.getInstance().player;
        if (player == null) return false;
        return slot.allowModification(player);
    }

    public static boolean isHoveringRecipeViewer() {
        try {
            if (Platform.INSTANCE.isModLoaded("jei") && JEICompat.isHoveringIngredient()) return true;
            //? if <1.21.4 {
            /*if (Platform.INSTANCE.isModLoaded("emi") && EMICompat.isHoveringIngredient()) return true;
            *///?}
            //? if >1.21.9 {
            if (Platform.INSTANCE.isModLoaded("rrv") && RRVCompat.isHoveringIngredient()) return true;
            //?}
        } catch (Throwable ignored) {}
        return false;
    }

    public static boolean iconsRendering() {
        return isHoveringCreativeTabSlot() || showTooltip || isHoveringRecipeViewer();
    }

    private static int nextShapeIndex(ItemStack heldStack, int direction, boolean wrap) {
        List<Item> shapes = ShapeMap.getShapes(heldStack.getItem());
        if (shapes.isEmpty()) return -1;
        int current = ShapeMap.currentIndex(heldStack);
        if (current < 0) current = 0;
        int idx = current - direction;
        int max = shapes.size() - 1;
        if (wrap) {
            if (idx < 0) idx = max;
            if (idx > max) idx = 0;
        } else {
            if (idx < 0) idx = 0;
            if (idx > max) idx = max;
        }
        return idx;
    }

    private static void playSwitchSound(Player player) {
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 1.5F);
    }

    public static void onItemTooltips(ItemStack stack,
                                      //? if >1.21 {
                                      Item.TooltipContext
                                              //?} else
                                              //Object
                                              tooltipContext, TooltipFlag tooltipFlag, List<Component> tooltip) {
        if (!ShapeMap.contains(stack.getItem())) return;
        if (tooltip.isEmpty()) return;

        if (CLIENT_CONFIG.DETAILED_TOOLTIPS.value()) {
            if (Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen && hoveredSlot() != null) return;
            if (isHoveringSwitchableSlot()) {
                tooltip.add(1, hintComponent());
                return;
            }
        }

        if (iconsRendering()) return;

        tooltip.set(0, tooltip.get(0).copy().append(Component.literal(" [+]").withStyle(ChatFormatting.DARK_GRAY)));
    }

    public static Component hintComponent() {
        Component hint;
        if (showTooltip) {
            hint = Component.translatable("tooltip.clutternomore.scroll_to_change",
                    Component.translatable("tooltip.clutternomore.scroll").withStyle(ChatFormatting.GRAY));
        } else {
            String key = CLIENT_CONFIG.HOLD.value() == CNMConfig.InputType.HOLD
                    ? "tooltip.clutternomore.hold_to_change"
                    : "tooltip.clutternomore.press_to_change";
            hint = Component.translatable(key,
                    Component.keybind("key.clutternomore.change_block_shape").copy().withStyle(ChatFormatting.GRAY));
        }
        return hint.copy().withStyle(ChatFormatting.DARK_GRAY);
    }

    /**
     * Used for ingame key presses.
     */
    public static void onKeyInput(int keyCode, int action) {
        if (keyCode != shapeKey() || Platform.INSTANCE.isModLoaded("controlify") && ControlifyCompat.currentInputModeIsController()) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.screen != null) return;

        if (action == 1) keyHeld = true;
        else if (action == 0) keyHeld = false;

        Player player = minecraft.player;
        if (player == null) return;
        ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!ShapeMap.contains(heldStack.getItem())) return;

        switch (CLIENT_CONFIG.HOLD.value()) {
            case HOLD -> {
                if (OVERLAY == null && action == 1)
                    OVERLAY = new ShapeSwitcherOverlay(minecraft, heldStack, true);
                else if (action == 0) OVERLAY = null;
            }
            case TOGGLE -> {
                if (action == 1) {
                    if (OVERLAY == null) OVERLAY = new ShapeSwitcherOverlay(minecraft, heldStack, true);
                    else OVERLAY = null;
                }
            }
            case PRESS -> {
                if (action == 1) {
                    if (OVERLAY == null)
                        OVERLAY = new ShapeSwitcherOverlay(minecraft, heldStack, false);
                    OVERLAY.onMouseScrolled(-1);
                    OVERLAY = null;
                }
            }
        }
    }

    // Used for key presses while a screen is open.
    public static void onKeyPress(Screen screen, int button) {
        if (button != shapeKey()) return;
        if (keyHeld) return;
        keyHeld = true;
        switch (CLIENT_CONFIG.HOLD.value()) {
            case HOLD -> showTooltip = true;
            case TOGGLE -> showTooltip = !showTooltip;
            case PRESS -> {
                if (!(screen instanceof AbstractContainerScreen<?> containerScreen)) return;
                Slot slot = ((ContainerScreenAccessor) screen).getSlotUnderMouse();
                if (slot == null) return;
                ItemStack heldStack = slot.getItem();
                if (!ShapeMap.contains(heldStack.getItem())) return;
                if (!isCreativeTabSlot(slot) && !slot.allowModification(Minecraft.getInstance().player)) return;
                switchShape(slot, containerScreen.getMenu().containerId, heldStack, -1, true);
            }
        }
    }

    // Used for key releases while a screen is open.
    public static void onKeyReleased(int button) {
        if (button == shapeKey()) {
            keyHeld = false;
            if (CLIENT_CONFIG.HOLD.value() == CNMConfig.InputType.HOLD) {
                showTooltip = false;
            }
        }
    }

    public static int selectedSlot(Player player) {
        return player.getInventory()
                //? if >1.21.2 {
                .getSelectedSlot();
                //?} else {
                /*.selected;
                *///?}
    }

    public static void renderOverlay(GuiGraphicsExtractor guiGraphics, float partialTick) {
        if (OVERLAY != null) {
            OVERLAY.tickLook();
            if (OVERLAY.render) OVERLAY.render(guiGraphics, partialTick);
        }
    }

    public static void switchShape(Slot slot, int containerId, ItemStack heldStack, int direction, boolean wrap) {
        int nextIdx = nextShapeIndex(heldStack, direction, wrap);
        if (nextIdx < 0) return;
        int currentIdx = ShapeMap.currentIndex(heldStack);
        if (nextIdx == currentIdx) return;

        Player player = Minecraft.getInstance().player;
        playSwitchSound(player);
        if (isCreativeTabSlot(slot)) {
            slot.set(ShapeMap.transferStack(heldStack, nextIdx));
            return;
        }
        if (Minecraft.getInstance().screen instanceof CreativeModeInventoryScreen) {
            if (slot instanceof CreativeSlotWrapperAccessor wrapper) {
                Slot target = wrapper.cnm$getTarget();
                if (target != null) {
                    sendChangeStack(player.inventoryMenu.containerId, target.index, nextIdx);
                    return;
                }
            }
            if (slot.container == player.getInventory()) {
                for (Slot s : player.inventoryMenu.slots) {
                    if (s.container == slot.container && s.getContainerSlot() == slot.getContainerSlot()) {
                        sendChangeStack(player.inventoryMenu.containerId, s.index, nextIdx);
                        return;
                    }
                }
            }
        }
        sendChangeStack(containerId, slot.index, nextIdx);
    }

    private static final long SEND_INTERVAL_MS = 50L;
    private static long lastSendMs = 0L;
    private static int pendingContainerId;
    private static int pendingSlotId;
    private static int pendingShapeIndex = Integer.MIN_VALUE;

    public static void sendChangeStack(int containerId, int slotId, int shapeIndex) {
        long now = System.currentTimeMillis();
        if (pendingShapeIndex != Integer.MIN_VALUE && (pendingContainerId != containerId || pendingSlotId != slotId)) {
            sendChangeStackNow(pendingContainerId, pendingSlotId, pendingShapeIndex);
            lastSendMs = now;
            pendingShapeIndex = Integer.MIN_VALUE;
        }
        if (now - lastSendMs >= SEND_INTERVAL_MS) {
            sendChangeStackNow(containerId, slotId, shapeIndex);
            lastSendMs = now;
            pendingShapeIndex = Integer.MIN_VALUE;
        } else {
            pendingContainerId = containerId;
            pendingSlotId = slotId;
            pendingShapeIndex = shapeIndex;
        }
    }

    private static void sendChangeStackNow(int containerId, int slotId, int shapeIndex) {
        ClientNetworking.sendToServer(new ChangeStackPayload(containerId, slotId, shapeIndex));
    }

    public static int shapeKey() {
        return Platform.INSTANCE.shapeKey();
    }

    public static boolean isShapeKeyPhysicallyDown() {
        int code = shapeKey();
        if (code < 0) return false;
        long window =
                //? if >26 {
                Minecraft.getInstance().getWindow().handle();
                //?} else {
                /*Minecraft.getInstance().getWindow().getWindow();
                *///?}
        if (code < GLFW.GLFW_KEY_SPACE) {
            return GLFW.glfwGetMouseButton(window, code) == GLFW.GLFW_PRESS;
        }
        return GLFW.glfwGetKey(window, code) == GLFW.GLFW_PRESS;
    }

    public static void onPlayerTick(Minecraft minecraft) {
        if (Platform.INSTANCE.isModLoaded("controlify")) {
            ControlifyCompat.checkForControllerInput(minecraft);
        }

        boolean physical = isShapeKeyPhysicallyDown();
        boolean rising = physical && !keyHeld;
        keyHeld = physical;

        switch (CLIENT_CONFIG.HOLD.value()) {
            case HOLD -> showTooltip = physical;
            case TOGGLE -> { if (rising) showTooltip = !showTooltip; }
            case PRESS -> showTooltip = false;
        }

        if (OVERLAY != null && !OVERLAY.shouldStayOpenThisTick()) OVERLAY = null;
        if (pendingShapeIndex != Integer.MIN_VALUE && System.currentTimeMillis() - lastSendMs >= SEND_INTERVAL_MS) {
            sendChangeStackNow(pendingContainerId, pendingSlotId, pendingShapeIndex);
            lastSendMs = System.currentTimeMillis();
            pendingShapeIndex = Integer.MIN_VALUE;
        }
    }

    //? if >1.21 {
    public static void onRenderGui(GuiGraphicsExtractor guiGraphics, DeltaTracker tracker) {
        renderOverlay(guiGraphics, tracker.getGameTimeDeltaTicks());
    }
    //?}

    public static boolean allowScreenScroll(Screen pScreen, double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!showTooltip) return true;
        if (!(pScreen instanceof AbstractContainerScreen<?> screen)) return true;

        Slot slot = ((ContainerScreenAccessor) screen).getSlotUnderMouse();
        if (slot == null) return true;
        ItemStack heldStack = slot.getItem();
        if (!ShapeMap.contains(heldStack.getItem())) return true;
        if (!isCreativeTabSlot(slot) && !slot.allowModification(Minecraft.getInstance().player)) return true;

        switchShape(slot, screen.getMenu().containerId, heldStack, (int) scrollY, CLIENT_CONFIG.WRAP_SCROLLING.value());
        return false;
    }

    public static boolean onMouseScrolling(double yOffset) {
        int direction = (int) yOffset;
        if (OVERLAY != null) {
            OVERLAY.onMouseScrolled(direction);
            return true;
        }
        return false;
    }

    public static boolean changeHotbarSlot(int selectedIndex) {
        if (ClutterNoMoreClient.OVERLAY != null) {
            int maxIndex = ClutterNoMoreClient.OVERLAY.shapes.size() - 1;
            if (selectedIndex < 0) selectedIndex = 0;
            if (selectedIndex > maxIndex) selectedIndex = maxIndex;
            ClutterNoMoreClient.OVERLAY.changeSlot(selectedIndex);
            return true;
        } else {
            return false;
        }
    }

    public static boolean prevSlot() {
        if (ClutterNoMoreClient.OVERLAY != null) {
            return changeHotbarSlot(ClutterNoMoreClient.OVERLAY.selectedIndex+1);
        }
        return false;
    }

    public static boolean nextSlot() {
        if (ClutterNoMoreClient.OVERLAY != null) {
            return changeHotbarSlot(ClutterNoMoreClient.OVERLAY.selectedIndex-1);
        }
        return false;
    }
}
