package dev.tazer.clutternomore.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShapeSwitcherOverlay {

    public static final float LOOK_DEGREES_PER_SWITCH = 25.0F;

    public final Minecraft minecraft;
    public final boolean render;
    public final int selected;
    public int count;
    public final List<Item> shapes;
    public int selectedIndex;
    public float currentIndex;
    public float lastYaw;
    public float accumulatedYaw;

    public ShapeSwitcherOverlay(Minecraft minecraft, ItemStack heldStack, boolean render) {
        this.minecraft = minecraft;
        this.render = render;
        selected = ClutterNoMoreClient.selectedSlot(minecraft.player);

        Item item = ShapeMap.getParent(heldStack.getItem());
        count = heldStack.getCount();

        shapes = new ArrayList<>(ShapeMap.getShapes(item));
        shapes.add(0, item);

        selectedIndex = shapes.indexOf(heldStack.getItem());
        currentIndex = selectedIndex;
        lastYaw = minecraft.player.getYRot();
        accumulatedYaw = 0;
    }

    public void render(GuiGraphicsExtractor guiGraphics, float partialTick) {
        Identifier background = ClutterNoMore.location("textures/gui/shape_background.png");
        Identifier selected = ClutterNoMore.location("textures/gui/selected_shape.png");

        int y = guiGraphics.guiHeight() / 2 + 20;
        int centreX = guiGraphics.guiWidth() / 2 - 8;
        int spacing = 22;
        int startX;

        float smoothing = 1 - (float) Math.exp(-5 * partialTick);
        currentIndex = Mth.lerp(smoothing, currentIndex, selectedIndex);

        if (ClutterNoMoreClient.CLIENT_CONFIG.SCROLLING.value()) {
            startX = Mth.floor(centreX - currentIndex * spacing);
            RenderHelper.blit(guiGraphics, selected, centreX - 3, y - 3, 0, 0, 22, 22, 22, 22);

            for (int index = 0; index < shapes.size(); index++) {
                int x = startX + index * spacing;
                RenderHelper.blit(guiGraphics, background, x, y, 0, 0, 16, 16, 16, 16);
                RenderHelper.item(guiGraphics, shapes.get(index).getDefaultInstance(), x, y);
            }

        } else {
            startX = Mth.floor(centreX - (float) shapes.size() / 2 * spacing) + spacing / 2;

            for (int index = 0; index < shapes.size(); index++) {
                int x = startX + index * spacing;
                RenderHelper.blit(guiGraphics, background, x, y, 0, 0, 16, 16, 16, 16);
                RenderHelper.item(guiGraphics, shapes.get(index).getDefaultInstance(), x, y);
            }
            RenderHelper.blit(guiGraphics, selected, Mth.floor(startX + currentIndex * spacing) - 3, y - 3, 0, 0, 22, 22, 22, 22);
        }
        //? if <1.21.2
        //RenderSystem.disableBlend();
    }

    public void onMouseScrolled(int direction) {
        changeSlot(selectedIndex-direction);
    }

    public void tickLook() {
        if (!ClutterNoMoreClient.CLIENT_CONFIG.LOOK_TO_SWITCH.value()) {
            lastYaw = minecraft.player.getYRot();
            accumulatedYaw = 0;
            return;
        }

        float yaw = minecraft.player.getYRot();
        float delta = Mth.wrapDegrees(yaw - lastYaw);
        lastYaw = yaw;
        accumulatedYaw += delta;

        while (accumulatedYaw >= LOOK_DEGREES_PER_SWITCH) {
            accumulatedYaw -= LOOK_DEGREES_PER_SWITCH;
            changeSlot(selectedIndex + 1);
        }
        while (accumulatedYaw <= -LOOK_DEGREES_PER_SWITCH) {
            accumulatedYaw += LOOK_DEGREES_PER_SWITCH;
            changeSlot(selectedIndex - 1);
        }
    }

    public void changeSlot(int newIndex) {
        int maxIndex = shapes.size() - 1;
        int previousIndex = selectedIndex;
        selectedIndex = newIndex;
        boolean wrap = ClutterNoMoreClient.CLIENT_CONFIG.WRAP_SCROLLING.value()
                || ClutterNoMoreClient.CLIENT_CONFIG.HOLD.value() == CNMConfig.InputType.PRESS;
        if (wrap) {
            if (selectedIndex < 0) selectedIndex = maxIndex;
            if (selectedIndex > maxIndex) selectedIndex = 0;
        } else {
            if (selectedIndex < 0) selectedIndex = 0;
            if (selectedIndex > maxIndex) selectedIndex = maxIndex;
        }
        if (selectedIndex == previousIndex) return;

        Item nextItem = shapes.get(selectedIndex);
        ItemStack next = nextItem.getDefaultInstance();
        next.setCount(count);
        Player player = Objects.requireNonNull(minecraft.player);
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 1.5F);
        player.setItemInHand(InteractionHand.MAIN_HAND, next);

        ClutterNoMoreClient.sendChangeStack(-1, -1, next);
    }

    public boolean shouldStayOpenThisTick() {
        int selected = ClutterNoMoreClient.selectedSlot(minecraft.player);
        ItemStack heldStack = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
        count = heldStack.getCount();
        return shapes.contains(heldStack.getItem()) && selected == this.selected;
    }
}
