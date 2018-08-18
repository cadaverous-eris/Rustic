package rustic.client.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

public class ClientUtils {
	
	public static void drawTexturedColoredRect(float x, float y, float width, float height, float minU, float minV, float maxU, float maxV, int color) {
		
		int blue, green, red, alpha;
		
		blue = color & 0xFF;
        green = (color >> 8) & 0xFF;
        red = (color >> 16) & 0xFF;
        alpha = (color >> 24) & 0xFF;
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		
		vertexBuffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
		vertexBuffer.pos(x,  y + height,  0).tex(minU, maxV).color(red, green, blue, alpha).endVertex();
		vertexBuffer.pos(x + width, y + height,  0).tex(maxU, maxV).color(red, green, blue, alpha).endVertex();
		vertexBuffer.pos(x + width, y,  0).tex(maxU, minV).color(red, green, blue, alpha).endVertex();
		vertexBuffer.pos(x, y,  0).tex(minU, minV).color(red, green, blue, alpha).endVertex();
		
		tessellator.draw();
		
	}
	
	public static IBakedModel getItemModel(ItemStack stack) {
		return Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, null, null);
	}
	
	public static TextureAtlasSprite getItemTexture(ItemStack stack) {
		IBakedModel model = getItemModel(stack);
		
		TextureAtlasSprite sprite = model.getParticleTexture();
		if (!sprite.getIconName().equals("missingno")) {
			return sprite;
		}
		
		EnumFacing[] faces = { null, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST, EnumFacing.UP, EnumFacing.DOWN };
		for (EnumFacing face : faces) {
			for (BakedQuad quad : model.getQuads(null, face, 0)) {
				sprite = quad.getSprite();
				if (!sprite.getIconName().equals("missingno")) {
					return sprite;
				}
			}
		}
		
		return sprite;
	}
	
	public static int getItemColor(ItemStack stack) {
		return getTextureColor(getItemTexture(stack));
	}
	
	// returns the average color of a texture
	public static int getTextureColor(TextureAtlasSprite sprite) {
		if (sprite == null) return 0xFFFF00FF;
		
		int numPixels = 0;
		int[] data = sprite.getFrameTextureData(0)[0];
		int width = sprite.getIconWidth();
		int height = sprite.getIconHeight();
		
		int a = 0xFF;
		int r = 0;
		int g = 0;
		int b = 0;
		
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int c = data[(y * width) + x];
				if (((c >> 24) & 0xFF) <= 0) continue;
				
				r += (c >> 16) & 0xFF;
				g += (c >> 8) & 0xFF;
				b += (c) & 0xFF;
				
				numPixels++;
			}
		}
		
		r = MathHelper.clamp(Math.round(((float) r / (float) numPixels)), 0, 255);
		g = MathHelper.clamp(Math.round(((float) g / (float) numPixels)), 0, 255);
		b = MathHelper.clamp(Math.round(((float) b / (float) numPixels)), 0, 255);
		
		int color = (a << 24) | (r << 16) | (g << 8) | (b);
		return color;
	}

}
