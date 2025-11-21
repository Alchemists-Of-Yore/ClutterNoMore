package dev.tazer.clutternomore;

import dev.tazer.clutternomore.client.ShapeSwitcherOverlay;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.mixin.SlotAccessor;
import dev.tazer.clutternomore.common.mixin.screen.ScreenAccessor;
//? if !forge {
 import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
 import net.minecraft.client.DeltaTracker;
//?} else if forge && <1.21.1 {
/*import dev.tazer.clutternomore.forge.networking.ChangeStackPacket;
import dev.tazer.clutternomore.forge.networking.ForgeNetworking;
*///?}
//? if fabric {
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//?}
import net.minecraft.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
//? if neoforge {
/*import net.neoforged.neoforge.network.PacketDistributor;
*///?}
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static dev.tazer.clutternomore.ClutterNoMore.LOGGER;
import static dev.tazer.clutternomore.ClutterNoMore.MODID;

public class ClutterNoMoreClient {
    public static boolean showTooltip = false;
    public static ShapeSwitcherOverlay OVERLAY = null;
    public static final CNMConfig.ClientConfig CLIENT_CONFIG = CNMConfig.ClientConfig.createToml(Platform.INSTANCE.configPath(), MODID,  "client", CNMConfig.ClientConfig.class);

    public static void init() {
    }

    public static void onItemTooltips(ItemStack stack,
                                      //? if >1.21 {
                                      Item.TooltipContext
                                              //?} else
                                              /*Object*/
                                              tooltipContext, TooltipFlag tooltipFlag, List<Component> tooltip) {
        if (!showTooltip) {
            if (ShapeMap.contains(stack.getItem())) {
                Component component = tooltip.get(0).copy().append(Component.literal(" [+]").withStyle(ChatFormatting.DARK_GRAY));
                tooltip.remove(0);
                tooltip.add(0, component);
            }
        }
    }

    public static void onKeyInput(int keyCode, int action) {
        if (keyCode == shapeKey()) {
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft.screen == null) {
                Player player = minecraft.player;
                if (player != null) {
                    ItemStack heldStack = player.getItemInHand(InteractionHand.MAIN_HAND);
                    if (ShapeMap.contains(heldStack.getItem())) {
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
                }
            }
        }
    }

    public static void onKeyPress(Screen screen, int button) {
        if (button == shapeKey() && screen instanceof AbstractContainerScreen<?> containerScreen) {
            Slot slot = ((ScreenAccessor) screen).getSlotUnderMouse();
            if (slot != null) {
                ItemStack heldStack = slot.getItem();
                //? if neoforge
                /*Player player = screen.getMinecraft().player;*/
                //? if fabric || forge
                Player player = Minecraft.getInstance().player;

                if (slot.allowModification(player) && (ShapeMap.contains(heldStack.getItem()))) {
                    switch (CLIENT_CONFIG.HOLD.value()) {
                        case HOLD -> showTooltip = true;
                        case TOGGLE -> showTooltip = !showTooltip;
                        case PRESS -> switchShapeInSlot(
                                player,
                                containerScreen.getMenu().containerId,
                                ((SlotAccessor) slot).getSlotIndex(),
                                heldStack,
                                -1
                        );
                    }
                }
            }
        }
    }

    public static boolean onKeyReleased(int button) {
        if (button == shapeKey()) {
            if (CLIENT_CONFIG.HOLD.value() == CNMConfig.InputType.HOLD) {
                showTooltip = false;
            }
        }
        return false;
    }

    public static void switchShapeInSlot(Player player, int containerId, int slotId, ItemStack heldStack, int direction) {
        Item item = ShapeMap.getParent(heldStack.getItem());
        int count = heldStack.getCount();

        List<Item> shapes = new ArrayList<>(ShapeMap.getShapes(item));
        shapes.add(0, item);
        int selectedIndex = shapes.indexOf(heldStack.getItem());

        int maxIndex = shapes.size() - 1;
        selectedIndex = selectedIndex - direction;
        if (selectedIndex < 0) selectedIndex = maxIndex;
        if (selectedIndex > maxIndex) selectedIndex = 0;

        Item nextItem = shapes.get(selectedIndex);
        ItemStack next = nextItem.getDefaultInstance();
        next.setCount(count);
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 1.5F);
        if (slotId < 9) slotId += 36;
        //? if !forge {
        var p = new ChangeStackPayload(containerId, slotId, next);
        //?}
        //? if fabric
        ClientPlayNetworking.send(p);
        //? if neoforge
        /*PacketDistributor.sendToServer(p);*/
        //? if forge && <1.21.1 {
        /*ChangeStackPacket p = new ChangeStackPacket(containerId, slotId, next);
        ForgeNetworking.sendToServer(p);
        *///?}
    }

    public static int shapeKey() {
        return Platform.INSTANCE.shapeKey();
    }

    public static void onPlayerTick(Minecraft minecraft) {
        ClutterNoMoreClient.enablePack();
        if (OVERLAY != null) {
            if (!OVERLAY.shouldStayOpenThisTick()) OVERLAY = null;
        }
    }

    //? if >1.21 {
    public static void onRenderGui(GuiGraphics guiGraphics, DeltaTracker tracker) {
        if (OVERLAY != null && OVERLAY.render) {
            OVERLAY.render(guiGraphics, tracker.getGameTimeDeltaTicks());
        }
    }
    //?}

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

    public static boolean onMouseScrolling(double yOffset) {
        int direction = (int) yOffset;
        if (OVERLAY != null) {
            OVERLAY.onMouseScrolled(direction);
            return true;
        }
        return false;
    }

    static boolean hasReloaded = false;

    public static void enablePack() {
        if (!hasReloaded) {
            LOGGER.info("Attempting to enable pack!");
            Minecraft.getInstance().reloadResourcePacks();
            hasReloaded = true;
        }
    }
}
