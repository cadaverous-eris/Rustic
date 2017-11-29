package rustic.client.util;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

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

}
