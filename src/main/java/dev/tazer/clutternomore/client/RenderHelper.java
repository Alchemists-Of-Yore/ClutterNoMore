package dev.tazer.clutternomore.client;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.tazer.clutternomore.ClutterNoMore;
import dev.tazer.clutternomore.ClutterNoMoreClient;
import dev.tazer.clutternomore.common.shape_map.ShapeMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
//? if >1.21.6 {
import net.minecraft.client.renderer.RenderPipelines;
 //?}
//? if =1.21.5 {
/*import net.minecraft.client.renderer.RenderType;
 *///?}
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public class RenderHelper {

	public static void shapeBadge(GuiGraphicsExtractor guiGraphics, ItemStack stack, int x, int y) {
		if (!ClutterNoMoreClient.CLIENT_CONFIG.SHAPE_INDICATOR.value()) return;
		if (stack == null || stack.isEmpty() || !ShapeMap.contains(stack.getItem())) return;
		Identifier texture = ClutterNoMore.location("textures/gui/shape_indicator.png");
		//? if <1.21.6 {
		/*guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(0.0F, 0.0F, 200.0F);
		*///?}
		blit(guiGraphics, texture, x - 4, y - 4, 0, 0, 24, 24, 24, 24);
		//? if <1.21.6 {
		/*guiGraphics.pose().popPose();
		*///?}
	}

	public static void item(GuiGraphicsExtractor guiGraphics, ItemStack stack, int x, int y) {
		//? if >26 {
		guiGraphics.fakeItem(stack, x, y);
		//?} else {
		/*guiGraphics.renderFakeItem(stack, x, y);
		*///?}
	}

	public static void blit(GuiGraphicsExtractor guiGraphics, Identifier selected, int x, int y, int u, int v, int width, int height, int textureWidth, int textureHeight) {
		//? if <1.21.2
		//RenderSystem.enableBlend();
		guiGraphics.blit(
				//? if >1.21.6
				RenderPipelines.GUI_TEXTURED,
				//? =1.21.5
				/*RenderType::guiTextured,*/
				selected,
				x, y, u, v, width, height, textureWidth, textureHeight);
	}

}
