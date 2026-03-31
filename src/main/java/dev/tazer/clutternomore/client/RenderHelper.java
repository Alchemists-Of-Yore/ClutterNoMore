package dev.tazer.clutternomore.client;

import com.mojang.blaze3d.systems.RenderSystem;
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
	public static void item(GuiGraphicsExtractor guiGraphics, ItemStack stack, int x, int y) {
		//? if >26 {
		guiGraphics.item(stack, x, y);
		//?} else {
		/*guiGraphics.renderItem(stack, x, y);
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
