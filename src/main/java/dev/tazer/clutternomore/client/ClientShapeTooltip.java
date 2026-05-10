package dev.tazer.clutternomore.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
//? if >1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
//?}
//? if =1.21.5 {
/*import net.minecraft.client.renderer.RenderType;
*///?}
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class ClientShapeTooltip implements ClientTooltipComponent {
    public final List<ItemStack> stacks;
    public int selectedIndex;

    public ClientShapeTooltip(ShapeTooltip shapeTooltip) {
        stacks = shapeTooltip.stacks();
        selectedIndex = shapeTooltip.selectedIndex();
    }

    @Override
    //? if >1.21.2 {
    public int getHeight(Font font) {
    //?} else {
    /*public int getHeight() {
    *///?}
        return ClutterNoMoreClient.iconsRendering() ? 22 : 0;
    }

    @Override
    public int getWidth(Font font) {
        return ClutterNoMoreClient.iconsRendering() ? stacks.size() * 22 : 0;
    }

    @Override
    //? if >26 {
    public void extractImage(Font font, int mouseX, int mouseY, int width, int height, GuiGraphicsExtractor guiGraphics) {
    //?} else {
    /*public void renderImage(Font font, int mouseX, int mouseY, GuiGraphicsExtractor guiGraphics) {
    *///?}
        if (ClutterNoMoreClient.iconsRendering()) {
            Identifier selected = ClutterNoMore.location("textures/gui/selected_shape_inventory.png");

            int spacing = 22;
            int startX = mouseX + 2;

            for (int index = 0; index < stacks.size(); index++) {
                int x = startX + index * spacing;
                RenderHelper.item(guiGraphics, stacks.get(index), x, mouseY);
            }
            RenderHelper.blit(
                    guiGraphics, selected, Mth.floor(startX + selectedIndex * spacing) - 3, mouseY - 3, 0, 0, 22, 22, 22, 22);
            //? if <1.21.2
            //RenderSystem.disableBlend();
        }
    }
}
