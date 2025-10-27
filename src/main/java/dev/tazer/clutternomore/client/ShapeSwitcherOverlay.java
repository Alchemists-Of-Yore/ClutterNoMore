package dev.tazer.clutternomore.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tazer.clutternomore.CNMConfig;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import dev.tazer.clutternomore.common.networking.ChangeStackPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
//? if >1.21.6
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? if neoforge {
/*import net.neoforged.neoforge.network.PacketDistributor;
 *///?} else {
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
//?}

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShapeSwitcherOverlay {

    public final Minecraft minecraft;
    public final boolean render;
    public final int selected;
    public int count;
    public final List<Item> shapes;
    public int selectedIndex;
    public float currentIndex;

    public ShapeSwitcherOverlay(Minecraft minecraft, ItemStack heldStack, boolean render) {
        this.minecraft = minecraft;
        this.render = render;
        selected = minecraft.player.getInventory()
        //? if >1.21.2 {
        .getSelectedSlot();
        //?} else {
        /*.selected;
        *///?}

        Item item = ShapeMap.getParent(heldStack.getItem());
        count = heldStack.getCount();

        shapes = new ArrayList<>(ShapeMap.getShapes(item));
        shapes.addFirst(item);

        selectedIndex = shapes.indexOf(heldStack.getItem());
        currentIndex = selectedIndex;
    }

    public void render(GuiGraphics guiGraphics, float partialTick) {
        ResourceLocation background = ClutterNoMore.location("textures/gui/shape_background.png");
        ResourceLocation selected = ClutterNoMore.location("textures/gui/selected_shape.png");

        int y = guiGraphics.guiHeight() / 2 + 20;
        int centreX = guiGraphics.guiWidth() / 2 - 8;
        int spacing = 22;
        int startX;

        float smoothing = 1 - (float) Math.exp(-5 * partialTick);
        currentIndex = Mth.lerp(smoothing, currentIndex, selectedIndex);

        if (ClutterNoMoreClient.CLIENT_CONFIG.SCROLLING.value()) {
            startX = Mth.floor(centreX - currentIndex * spacing);

            //? if <1.21.2
            /*RenderSystem.enableBlend();*/
            guiGraphics.blit(
                    //? if >1.21.6
                    RenderPipelines.GUI_TEXTURED,
                    selected, centreX - 3, y - 3, 0, 0, 22, 22, 22, 22);

            for (int index = 0; index < shapes.size(); index++) {
                int x = startX + index * spacing;
                //? if <1.21.2
                /*RenderSystem.enableBlend();*/
                guiGraphics.blit(
                        //? if >1.21.6
                        RenderPipelines.GUI_TEXTURED,
                        background, x, y, 0, 0, 16, 16, 16, 16);

                guiGraphics.renderItem(shapes.get(index).getDefaultInstance(), x, y);
            }

        } else {
            startX = Mth.floor(centreX - (float) shapes.size() / 2 * spacing) + spacing / 2;

            for (int index = 0; index < shapes.size(); index++) {
                int x = startX + index * spacing;
                //? if <1.21.2
                /*RenderSystem.enableBlend();*/
                guiGraphics.blit(
                        //? if >1.21.6
                        RenderPipelines.GUI_TEXTURED,
                        background, x, y, 0, 0, 16, 16, 16, 16);

                guiGraphics.renderItem(shapes.get(index).getDefaultInstance(), x, y);
            }
            //? if <1.21.2
            /*RenderSystem.enableBlend();*/
            guiGraphics.blit(
                    //? if >1.21.6
                    RenderPipelines.GUI_TEXTURED,
                    selected, Mth.floor(startX + currentIndex * spacing) - 3, y - 3, 0, 0, 22, 22, 22, 22);
        }
        //? if <1.21.2
        /*RenderSystem.disableBlend();*/
    }

    public void onMouseScrolled(int direction) {
        changeSlot(selectedIndex-direction);
    }

    public void changeSlot(int newIndex) {
        int maxIndex = shapes.size() - 1;
        selectedIndex = newIndex;
        if (selectedIndex < 0) selectedIndex = maxIndex;
        if (selectedIndex > maxIndex) selectedIndex = 0;

        Item nextItem = shapes.get(selectedIndex);
        ItemStack next = nextItem.getDefaultInstance();
        next.setCount(count);
        Player player = Objects.requireNonNull(minecraft.player);
        player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), 0.3F, 1.5F);
        player.setItemInHand(InteractionHand.MAIN_HAND, next);

        //? if neoforge {
        /*PacketDistributor.sendToServer
        *///?} else {
        ClientPlayNetworking.send
        //?}
        (new ChangeStackPayload(-1, -1, next));
    }

    public boolean shouldStayOpenThisTick() {
        int selected = minecraft.player.getInventory()
        //? if >1.21.2 {
        .getSelectedSlot();
        //?} else {
        /*.selected;
        *///?}
        ItemStack heldStack = minecraft.player.getItemInHand(InteractionHand.MAIN_HAND);
        count = heldStack.getCount();
        return shapes.contains(heldStack.getItem()) && selected == this.selected;
    }
}
