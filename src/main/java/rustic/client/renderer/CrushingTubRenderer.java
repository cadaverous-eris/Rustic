package rustic.client.renderer;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import rustic.client.util.FluidClientUtil;
import rustic.common.tileentity.TileEntityCrushingTub;

public class CrushingTubRenderer extends TileEntitySpecialRenderer<TileEntityCrushingTub> {

	int blue, green, red, a;
	int lightx, lighty;
	double minU, minV, maxU, maxV, diffU, diffV;
	
	@Override
	public void render(TileEntityCrushingTub te, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		TileEntityCrushingTub tank = (TileEntityCrushingTub)te;
		 
        IItemHandler itemStackHandler = tank.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (itemStackHandler.getSlots() > 0 && !itemStackHandler.getStackInSlot(0).isEmpty() && tank.getWorld() != null) {
            ItemStack stack = itemStackHandler.getStackInSlot(0);
            int itemCount = (int)Math.ceil((stack.getCount())/8.0);
            Random rand = new Random();
            rand.setSeed(tank.getWorld().getSeed());
            for (int i = 0; i < itemCount; i ++){
                GL11.glPushMatrix();
                RenderHelper.enableStandardItemLighting();
                GlStateManager.enableLighting();
                GL11.glTranslated(x, y+0.062+(i*0.0625), z);
                GL11.glTranslated(0.5, 0.0, 0.5);
                GL11.glRotated(rand.nextFloat()*360.0, 0, 1.0, 0);
                GL11.glTranslated(-0.5, 0, -0.5);
                GL11.glRotated(90, 1, 0, 0);
                GL11.glTranslated(0.5, -0.1875, 0.0);
                GL11.glTranslated(0.0, 0.6875, 0.0);
                GL11.glScaled(0.5, 0.5, 0.5);
                Minecraft.getMinecraft().getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.NONE);
                GL11.glPopMatrix();
            }
        }
 
        int amount = tank.getAmount();
        int capacity = tank.getCapacity();
        Fluid fluid = tank.getFluid();
 
        if (fluid != null){
            int c = fluid.getColor();
            blue = c & 0xFF;
            green = (c >> 8) & 0xFF;
            red = (c >> 16) & 0xFF;
            a = (c >> 24) & 0xFF;
           
            TextureAtlasSprite sprite = FluidClientUtil.stillTextures.get(fluid);
            
            if (sprite == null) return;
            
            diffU = maxU - minU;
            diffV = maxV - minV;
           
            minU = sprite.getMinU() + diffU * 0.0625;
            maxU = sprite.getMaxU() - diffU * 0.0625;
            minV = sprite.getMinV() + diffV * 0.0625;
            maxV = sprite.getMaxV() - diffV * 0.0625;
           
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
            BufferBuilder buffer = tess.getBuffer();
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            buffer.pos(x+0.0625, y+0.0625+0.5*((float)amount/(float)capacity), z+0.0625).tex(minU, minV).lightmap(lightx,lighty).color(red,green,blue,a).endVertex();
            buffer.pos(x+0.9375, y+0.0625+0.5*((float)amount/(float)capacity), z+0.0625).tex(maxU, minV).lightmap(lightx,lighty).color(red,green,blue,a).endVertex();
            buffer.pos(x+0.9375, y+0.0625+0.5*((float)amount/(float)capacity), z+0.9375).tex(maxU, maxV).lightmap(lightx,lighty).color(red,green,blue,a).endVertex();
            buffer.pos(x+0.0625, y+0.0625+0.5*((float)amount/(float)capacity), z+0.9375).tex(minU, maxV).lightmap(lightx,lighty).color(red,green,blue,a).endVertex();
            tess.draw();
           
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.popAttrib();
        }
	}
	
}
