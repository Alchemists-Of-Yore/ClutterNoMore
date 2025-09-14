package dev.tazer.clutternomore.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.common.networking.ShapeTooltip;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;

import java.util.List;

public class ClientShapeTooltip implements ClientTooltipComponent {
    public final List<Item> shapes;
    public int selectedIndex;

    public ClientShapeTooltip(ShapeTooltip shapeTooltip) {
        shapes = shapeTooltip.shapes();
        selectedIndex = shapeTooltip.selectedIndex();
    }

    @Override
    public int getHeight() {
        return ClutterNoMoreClient.showTooltip ? 22 : 0;
    }

    @Override
    public int getWidth(Font font) {
        return ClutterNoMoreClient.showTooltip ? shapes.size() * 22 : 0;
    }

    @Override
    public void renderImage(Font font, int mouseX, int mouseY, GuiGraphics guiGraphics) {
        if (ClutterNoMoreClient.showTooltip) {
            ResourceLocation selected = ClutterNoMore.location("textures/gui/selected_shape_inventory.png");

            int spacing = 22;
            int startX = mouseX + 2;

            for (int index = 0; index < shapes.size(); index++) {
                int x = startX + index * spacing;
                guiGraphics.renderItem(shapes.get(index).getDefaultInstance(), x, mouseY);
            }

            RenderSystem.enableBlend();
            guiGraphics.blit(selected, Mth.floor(startX + selectedIndex * spacing) - 3, mouseY - 3, 0, 0, 22, 22, 22, 22);
            RenderSystem.disableBlend();
        }
    }
}
