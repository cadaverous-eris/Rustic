package rustic.client;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.models.LiquidBarrelItemModel;
import rustic.common.Config;
import rustic.common.blocks.IAdvancedRotationPlacement;
import rustic.common.util.FluidTextureUtil;

public class EventHandlerClient {

	public static ResourceLocation RUSTIC_ICONS = new ResourceLocation("rustic:textures/gui/icons.png");
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre event) {
		FluidTextureUtil.initTextures(event.getMap());
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onModelBakeEvent(ModelBakeEvent event) {
		Object object = event.getModelRegistry().getObject(LiquidBarrelItemModel.modelResourceLocation);
		if (object instanceof IBakedModel) {
			IBakedModel existingModel = (IBakedModel) object;
			LiquidBarrelItemModel customModel = new LiquidBarrelItemModel(existingModel);
			event.getModelRegistry().putObject(LiquidBarrelItemModel.modelResourceLocation, customModel);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderArmorToughnessEvent(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.FOOD) {
			if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityLivingBase) {
				EntityLivingBase viewEntity = (EntityLivingBase) Minecraft.getMinecraft().getRenderViewEntity();
				int armorToughness = MathHelper.floor(viewEntity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
				if (armorToughness > 0 && Config.TOUGHNESS_HUD) {
					
					int width = event.getResolution().getScaledWidth();
					int height = event.getResolution().getScaledHeight();
					GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
					Minecraft.getMinecraft().getTextureManager().bindTexture(RUSTIC_ICONS);
					GlStateManager.enableBlend();
					
					int toughnessRows = MathHelper.ceil(armorToughness / 20.0F);
					int rowHeight = Math.min(Math.max(10 - (toughnessRows - 2), 3), 10);
					
			        int top = (height - GuiIngameForge.right_height) - ((toughnessRows * rowHeight) - 10);
					for (int i = toughnessRows - 1; i >= 0; i--) {
						int right = width / 2 + 82;
						for (int j = 1; armorToughness > 0 && j < 20; j += 2) {
							if (j + (i * 20) < armorToughness) {
								gui.drawTexturedModalRect(right, top, 34, 0, 9, 9);
							} else if (j + (i * 20) == armorToughness) {
								gui.drawTexturedModalRect(right, top, 25, 0, 9, 9);
							} else if (j + (i * 20) > armorToughness) {
								gui.drawTexturedModalRect(right, top, 16, 0, 9, 9);
							}
							right -= 8;
						}
						top += rowHeight;
						GuiIngameForge.right_height += rowHeight;
					}
					if (rowHeight < 10) {
						GuiIngameForge.right_height += (10 - rowHeight);
					}
					
					Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
					GlStateManager.disableBlend();
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderArmorOverlayEvent(RenderGameOverlayEvent.Pre event) {
		if (event.getType() == ElementType.ARMOR) {
			if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityLivingBase) {
				EntityLivingBase viewEntity = (EntityLivingBase) Minecraft.getMinecraft().getRenderViewEntity();
				event.setCanceled(true);
				int width = event.getResolution().getScaledWidth();
				int height = event.getResolution().getScaledHeight();
				GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
				Minecraft.getMinecraft().mcProfiler.startSection("armor");
				Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
				GlStateManager.enableBlend();

				int armorLevel = viewEntity.getTotalArmorValue();
				
				int armorRows = MathHelper.ceil(armorLevel / 20.0F);
				if (!Config.EXTRA_ARMOR_HUD && armorRows > 1) {
					armorRows = 1;
				}
		        int rowHeight = Math.min(Math.max(10 - (armorRows - 2), 3), 10);

				int top = (height - GuiIngameForge.left_height) - ((armorRows * rowHeight) - 10);
				for (int i = armorRows - 1; i >= 0; i--) {
					int left = width / 2 - 91;
					for (int j = 1; armorLevel > 0 && j < 20; j += 2) {
						if (j + (i * 20) < armorLevel) {
							gui.drawTexturedModalRect(left, top, 34, 9, 9, 9);
						} else if (j + (i * 20) == armorLevel) {
							gui.drawTexturedModalRect(left, top, 25, 9, 9, 9);
						} else if (j + (i * 20) > armorLevel) {
							gui.drawTexturedModalRect(left, top, 16, 9, 9, 9);
						}
						left += 8;
					}
					top += rowHeight;
					GuiIngameForge.left_height += rowHeight;
				}
				if (rowHeight < 10) {
					GuiIngameForge.left_height += (10 - rowHeight);
				}
				
				Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
				GlStateManager.disableBlend();
				Minecraft.getMinecraft().mcProfiler.endSection();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onBlockHighlight(DrawBlockHighlightEvent event) {
		EntityPlayer player = event.getPlayer();
		ItemStack stack = ItemStack.EMPTY;
		if ((player.getHeldItem(EnumHand.MAIN_HAND).isEmpty() || !(player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock)) && !player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
			stack = player.getHeldItem(EnumHand.OFF_HAND);
		} else if (!player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
			stack = player.getHeldItem(EnumHand.MAIN_HAND);
		}
		if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK && event.getTarget().sideHit.getAxis() == Axis.Y && !stack.isEmpty() && Block.getBlockFromItem(stack.getItem()) instanceof IAdvancedRotationPlacement) {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
			GlStateManager.glLineWidth(2.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			BlockPos pos = event.getTarget().getBlockPos();
			IBlockState iblockstate = player.getEntityWorld().getBlockState(pos);
			AxisAlignedBB AABB = iblockstate.getBoundingBox(player.getEntityWorld(), pos);
			boolean isAABBCenteredXZ = (((AABB.minX + ((AABB.maxX - AABB.minX) / 2D)) == 0.5D) || ((AABB.maxX - ((AABB.maxX - AABB.minX) / 2D)) == 0.5D)) && (((AABB.minZ + ((AABB.maxZ - AABB.minZ) / 2D)) == 0.5D) || ((AABB.maxZ - ((AABB.maxZ - AABB.minZ) / 2D)) == 0.5D));

			if (iblockstate.getMaterial() != Material.AIR && player.getEntityWorld().getWorldBorder().contains(pos) && isAABBCenteredXZ) {
				double d0 = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) event.getPartialTicks();
				double d1 = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) event.getPartialTicks();
				double d2 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) event.getPartialTicks();
				Tessellator tessellator = Tessellator.getInstance();
				VertexBuffer vertexbuffer = tessellator.getBuffer();
				vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
				vertexbuffer.setTranslation(-d0, -d1, -d2);
				AABB = AABB.expandXyz(0.0020000000949949026D);
				drawRotablePlacementBlockHighlight(vertexbuffer, AABB, event.getTarget().sideHit, pos);
				tessellator.draw();
				vertexbuffer.setTranslation(0D, 0D, 0D);
			}

			GlStateManager.depthMask(true);
			GlStateManager.enableTexture2D();
			GlStateManager.disableBlend();
		}
	}
	
	@SideOnly(Side.CLIENT)
	private void drawRotablePlacementBlockHighlight(VertexBuffer buffer, AxisAlignedBB AABB, EnumFacing side, BlockPos pos) {
		double minXZ = Math.max(AABB.minX, AABB.minZ);
		double maxXZ = Math.min(AABB.maxX, AABB.maxZ);
		double y = (side == EnumFacing.UP) ? AABB.maxY : AABB.minY;
		y += pos.getY();
		buffer.pos(minXZ + pos.getX(), y, minXZ + pos.getZ()).color(0F, 0F, 0f, 0.0F).endVertex();
		buffer.pos(minXZ + pos.getX(), y, minXZ + pos.getZ()).color(0F, 0F, 0f, 0.4F).endVertex();
		buffer.pos(maxXZ + pos.getX(), y, maxXZ + pos.getZ()).color(0F, 0F, 0f, 0.4F).endVertex();
		buffer.pos(minXZ + pos.getX(), y, maxXZ + pos.getZ()).color(0F, 0F, 0f, 0.0F).endVertex();
		buffer.pos(maxXZ + pos.getX(), y, minXZ + pos.getZ()).color(0F, 0F, 0f, 0.4F).endVertex();
		buffer.pos(maxXZ + pos.getX(), y, minXZ + pos.getZ()).color(0F, 0F, 0f, 0.0F).endVertex();
	}
	
}
