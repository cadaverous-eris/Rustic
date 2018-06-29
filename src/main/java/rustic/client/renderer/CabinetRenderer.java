package rustic.client.renderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import rustic.client.models.ModelCabinet;
import rustic.client.models.ModelCabinetDouble;
import rustic.common.blocks.BlockCabinet;
import rustic.common.blocks.ModBlocks;
import rustic.common.tileentity.TileEntityCabinet;

public class CabinetRenderer extends TileEntitySpecialRenderer<TileEntityCabinet> {

	private static final ResourceLocation TEXTURE_NORMAL = new ResourceLocation("rustic:textures/models/cabinet.png");
	private static final ResourceLocation TEXTURE_DOUBLE = new ResourceLocation("rustic:textures/models/cabinet_double.png");
	private final ModelCabinet simpleCabinet = new ModelCabinet(false);
	private final ModelCabinet simpleCabinetM = new ModelCabinet(true);
	private final ModelCabinetDouble doubleCabinet = new ModelCabinetDouble(false);
	private final ModelCabinetDouble doubleCabinetM = new ModelCabinetDouble(true);

	@Override
	public void render(TileEntityCabinet te, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		GlStateManager.enableDepth();
		GlStateManager.depthFunc(515);
		GlStateManager.depthMask(true);

		if (te.hasWorld() && te.getWorld().getBlockState(te.getPos()).getBlock() == ModBlocks.CABINET && !te.getWorld().getBlockState(te.getPos()).getValue(BlockCabinet.TOP)) {

			ModelBase modelcabinet;
			
			boolean mirror = te.getWorld().getBlockState(te.getPos()).getValue(BlockCabinet.MIRROR);
			boolean renderDouble = te.getWorld().getBlockState(te.getPos()).getActualState(te.getWorld(), te.getPos()).getValue(BlockCabinet.BOTTOM);

			modelcabinet = simpleCabinet;
			this.bindTexture(TEXTURE_NORMAL);
			
			if (mirror) {
				modelcabinet = simpleCabinetM;
			}

			if (renderDouble) {
				modelcabinet = doubleCabinet;
				this.bindTexture(TEXTURE_DOUBLE);
				if (mirror) {
					modelcabinet = doubleCabinetM;
				}
			}

			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();
			if (destroyStage < 0) {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
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

}
