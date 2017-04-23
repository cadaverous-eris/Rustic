package rustic.client.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.Fluid;
import rustic.common.tileentity.TileEntityCabinet;
import rustic.common.tileentity.TileEntityLiquidBarrel;
import rustic.common.util.FluidTextureUtil;

public class LiquidBarrelRenderer extends TileEntitySpecialRenderer<TileEntityLiquidBarrel> {
	
	int blue, green, red, alpha;
	int lightx, lighty;
	double minU, minV, maxU, maxV, diffU, diffV;
	
	public void renderTileEntityAt(TileEntityLiquidBarrel te, double x, double y, double z, float partialTicks,
			int destroyStage) {
		TileEntityLiquidBarrel tank = (TileEntityLiquidBarrel)te;
		int amount = tank.getAmount();
		int capacity = tank.getCapacity();
		Fluid fluid = tank.getFluid();
		if (fluid != null){
			int c = fluid.getColor();
            blue = c & 0xFF;
            green = (c >> 8) & 0xFF;
            red = (c >> 16) & 0xFF;
            alpha = (c >> 24) & 0xFF;
            
            TextureAtlasSprite sprite = FluidTextureUtil.stillTextures.get(fluid);
            diffU = maxU-minU;
            diffV = maxV-minV;
            
            minU = sprite.getMinU()+diffU*0.1875;
            maxU = sprite.getMaxU()-diffU*0.1875;
            minV = sprite.getMinV()+diffV*0.1875;
            maxV = sprite.getMaxV()-diffV*0.1875;
            
            int i = getWorld().getCombinedLight(te.getPos(), fluid.getLuminosity());
            lightx = i >> 0x10 & 0xFFFF;
            lighty = i & 0xFFFF;
            
            GlStateManager.pushAttrib();
            GlStateManager.disableCull();
            GlStateManager.disableLighting();
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            Tessellator tess = Tessellator.getInstance();
            VertexBuffer buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(x+0.1875, y+0.125+0.8125*((float)amount/(float)capacity), z+0.1875).tex(minU, minV).lightmap(lightx,lighty).color(red,green,blue,alpha).endVertex();
			buffer.pos(x+0.8125, y+0.125+0.8125*((float)amount/(float)capacity), z+0.1875).tex(maxU, minV).lightmap(lightx,lighty).color(red,green,blue,alpha).endVertex();
			buffer.pos(x+0.8125, y+0.125+0.8125*((float)amount/(float)capacity), z+0.8125).tex(maxU, maxV).lightmap(lightx,lighty).color(red,green,blue,alpha).endVertex();
			buffer.pos(x+0.1875, y+0.125+0.8125*((float)amount/(float)capacity), z+0.8125).tex(minU, maxV).lightmap(lightx,lighty).color(red,green,blue,alpha).endVertex();
			tess.draw();
			
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.enableCull();
			GlStateManager.popAttrib();
		}
	}

}
