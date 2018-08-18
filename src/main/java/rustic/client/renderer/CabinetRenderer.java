package rustic.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import rustic.client.models.ModelCabinet;
import rustic.client.models.ModelCabinetDouble;
import rustic.client.util.ItemColorCache;
import rustic.common.blocks.BlockCabinet;
import rustic.common.blocks.ModBlocks;
import rustic.common.tileentity.TileEntityCabinet;

public class CabinetRenderer extends TileEntitySpecialRenderer<TileEntityCabinet> {

	protected static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("rustic:textures/models/cabinet.png");
	protected static final ResourceLocation TEXTURE_DOUBLE = new ResourceLocation("rustic:textures/models/cabinet_double.png");
	protected static final ResourceLocation TEXTURE_NORMAL_COLOR = new ResourceLocation("rustic:textures/models/cabinet_color.png");
	protected static final ResourceLocation TEXTURE_DOUBLE_COLOR = new ResourceLocation("rustic:textures/models/cabinet_double_color.png");
	protected final ModelCabinet simpleCabinet = new ModelCabinet(false);
	protected final ModelCabinet simpleCabinetM = new ModelCabinet(true);
	protected final ModelCabinetDouble doubleCabinet = new ModelCabinetDouble(false);
	protected final ModelCabinetDouble doubleCabinetM = new ModelCabinetDouble(true);

	@Override
	public void render(TileEntityCabinet te, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);

		if (te.hasWorld() && te.getWorld().getBlockState(te.getPos()).getBlock() == ModBlocks.CABINET && !te.getWorld().getBlockState(te.getPos()).getValue(BlockCabinet.TOP)) {

			ModelBase modelcabinet;
			
			ItemStack material = te.material;
			
			boolean mirror = te.getWorld().getBlockState(te.getPos()).getValue(BlockCabinet.MIRROR);
			boolean renderDouble = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos()).getValue(BlockCabinet.BOTTOM);
			boolean useColoredTexture = material.isEmpty();
			
			modelcabinet = simpleCabinet;
			this.bindTexture(useColoredTexture ? TEXTURE_NORMAL_COLOR : TEXTURE_NORMAL);
			
			if (mirror) {
				modelcabinet = simpleCabinetM;
			}

			if (renderDouble) {
				modelcabinet = doubleCabinet;
				this.bindTexture(useColoredTexture ? TEXTURE_DOUBLE_COLOR : TEXTURE_DOUBLE);
				if (mirror) {
					modelcabinet = doubleCabinetM;
				}
			}

			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();
			if (!useColoredTexture) {
				 int color = ItemColorCache.INSTANCE.getColor(material);
				 float r = ((color >> 16) & 0xFF) / 255f;
				 float g = ((color >> 8) & 0xFF) / 255f;
				 float b = ((color) & 0xFF) / 255f;
				 GlStateManager.color(r, g, b, alpha);
			} else {
				GlStateManager.color(1F, 1F, 1F, alpha);
			}
			GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
			GlStateManager.scale(1.0F, -1.0F, -1.0F);

			int rotation = 0;

			switch (te.getWorld().getBlockState(te.getPos()).getValue(BlockCabinet.FACING)) {
			case WEST:
				rotation = 90;
				break;
			case NORTH:
				rotation = 180;
				break;
			case EAST:
				rotation = 270;
				break;
			default:
				break;
			}
			
			GlStateManager.translate(0.5F, 0.5F, 0.5F);		
			GlStateManager.rotate((float) rotation, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);

			float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;

			f = 1.0F - f;
			f = 1.0F - f * f * f;
			if (renderDouble) {
				((ModelCabinetDouble) modelcabinet).setRotation(((ModelCabinetDouble) modelcabinet).door, 0, (mirror) ? -(f * ((float) Math.PI / 2F)) : (f * ((float) Math.PI / 2F)), 0);
			} else {
				((ModelCabinet) modelcabinet).setRotation(((ModelCabinet) modelcabinet).door, 0, (mirror) ? -(f * ((float) Math.PI / 2F)) : (f * ((float) Math.PI / 2F)), 0);
			}
			GlStateManager.translate(0.5F, -0.5F, 0.5F);
			if (renderDouble) {
				((ModelCabinetDouble) modelcabinet).renderAll();
			} else {
				((ModelCabinet) modelcabinet).renderAll();
			}
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		}
	}
	
	public static class CabinetTEISR extends TileEntityItemStackRenderer {
		
		private static ModelCabinet model = new ModelCabinet(false);
		
		public void renderByItem(ItemStack stack, float partialTicks) {
			GlStateManager.enableDepth();
			GlStateManager.depthFunc(515);
			GlStateManager.depthMask(true);
			
			ItemStack material = ItemStack.EMPTY;
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey("material")) {
				material = new ItemStack(stack.getTagCompound().getCompoundTag("material"));
			}
			
			boolean useColoredTexture = material.isEmpty();
			
			Minecraft.getMinecraft().renderEngine.bindTexture(useColoredTexture ? TEXTURE_NORMAL_COLOR : TEXTURE_NORMAL);

			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();
			if (!useColoredTexture) {
				 int color = ItemColorCache.INSTANCE.getColor(material);
				 float r = ((color >> 16) & 0xFF) / 255f;
				 float g = ((color >> 8) & 0xFF) / 255f;
				 float b = ((color) & 0xFF) / 255f;
				 GlStateManager.color(r, g, b, 1F);
			} else {
				GlStateManager.color(1F, 1F, 1F, 1F);
			}
			GlStateManager.translate(0F, 2F, 1F);
			GlStateManager.scale(1.0F, -1.0F, -1.0F);
			GlStateManager.translate(0.5F, 0.5F, 0.5F);
			model.renderAll();
			
			GlStateManager.disableRescaleNormal();
			GlStateManager.popMatrix();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		}
		
	}

}
