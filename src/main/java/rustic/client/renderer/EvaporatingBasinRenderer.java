package rustic.client.renderer;

import java.util.Random;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
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
import rustic.common.tileentity.TileEntityEvaporatingBasin;

public class EvaporatingBasinRenderer extends TileEntitySpecialRenderer<TileEntityEvaporatingBasin> {

	int blue, green, red, alpha;
	int lightx, lighty;
	double minU, minV, maxU, maxV, diffU, diffV;
	
	public void renderTileEntityAt(TileEntityEvaporatingBasin te, double x, double y, double z, float partialTicks,
			int destroyStage) {
		TileEntityEvaporatingBasin tank = (TileEntityEvaporatingBasin)te;
		 
        IItemHandler itemStackHandler = tank.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        if (itemStackHandler.getSlots() > 0 && !itemStackHandler.getStackInSlot(0).isEmpty() && tank.getWorld() != null) {
            ItemStack stack = itemStackHandler.getStackInSlot(0);
            int itemCount = (int)Math.ceil((stack.getCount())/16.0);
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
            alpha = (c >> 24) & 0xFF;
           
            TextureAtlasSprite sprite = FluidClientUtil.stillTextures.get(fluid);
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
            buffer.pos(x+0.1875, y+0.0625+0.1875*((float)amount/(float)capacity), z+0.1875).tex(minU, minV).lightmap(lightx,lighty).color(red,green,blue,alpha).endVertex();
            buffer.pos(x+0.8125, y+0.0625+0.1875*((float)amount/(float)capacity), z+0.1875).tex(maxU, minV).lightmap(lightx,lighty).color(red,green,blue,alpha).endVertex();
            buffer.pos(x+0.8125, y+0.0625+0.1875*((float)amount/(float)capacity), z+0.8125).tex(maxU, maxV).lightmap(lightx,lighty).color(red,green,blue,alpha).endVertex();
            buffer.pos(x+0.1875, y+0.0625+0.1875*((float)amount/(float)capacity), z+0.8125).tex(minU, maxV).lightmap(lightx,lighty).color(red,green,blue,alpha).endVertex();
            tess.draw();
           
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            GlStateManager.enableCull();
            GlStateManager.popAttrib();
        }
	}
	
}
