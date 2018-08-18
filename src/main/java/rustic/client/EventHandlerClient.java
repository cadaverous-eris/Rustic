package rustic.client;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.models.BookBakedModel;
import rustic.client.models.LiquidBarrelItemModel;
import rustic.client.models.TEISRModel;
import rustic.client.util.FluidClientUtil;
import rustic.common.Config;
import rustic.common.blocks.BlockVase;
import rustic.common.blocks.IAdvancedRotationPlacement;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.fluids.FluidBooze;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.network.MessageVaseMeta;
import rustic.common.network.PacketHandler;

public class EventHandlerClient {

	public static ResourceLocation RUSTIC_ICONS = new ResourceLocation("rustic:textures/gui/icons.png");

	public static ResourceLocation OLIVE_OIL_OVERLAY = new ResourceLocation(
			"rustic:textures/blocks/fluids/olive_oil_overlay.png");
	public static ResourceLocation IRONBERRY_JUICE_OVERLAY = new ResourceLocation(
			"rustic:textures/blocks/fluids/ironberry_juice_overlay.png");
	public static ResourceLocation WILDBERRY_JUICE_OVERLAY = new ResourceLocation(
			"rustic:textures/blocks/fluids/wildberry_juice_overlay.png");
	public static ResourceLocation GRAPE_JUICE_OVERLAY = new ResourceLocation(
			"rustic:textures/blocks/fluids/grape_juice_overlay.png");
	public static ResourceLocation APPLE_JUICE_OVERLAY = new ResourceLocation(
			"rustic:textures/blocks/fluids/apple_juice_overlay.png");
	public static ResourceLocation ALE_WORT_OVERLAY = new ResourceLocation(
			"rustic:textures/blocks/fluids/ale_wort_overlay.png");
	public static ResourceLocation HONEY_OVERLAY = new ResourceLocation(
			"rustic:textures/blocks/fluids/honey_overlay.png");

	//private Random rand = new Random();

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre event) {
		FluidClientUtil.initTextures(event.getMap());
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
		object = event.getModelRegistry().getObject(BookBakedModel.modelResourceLocation);
		if (object instanceof IBakedModel) {
			IBakedModel existingModel = (IBakedModel) object;
			BookBakedModel customModel = new BookBakedModel(existingModel);
			event.getModelRegistry().putObject(BookBakedModel.modelResourceLocation, customModel);
		}
		ModelResourceLocation mrl = new ModelResourceLocation(ModBlocks.CABINET.getRegistryName(), "inventory");
		object = event.getModelRegistry().getObject(mrl);
		if (object instanceof IBakedModel) {
			IBakedModel existingModel = (IBakedModel) object;
			event.getModelRegistry().putObject(mrl, new TEISRModel(existingModel));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onVaseMouseWheel(MouseEvent event) {
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		if (player != null) {
			if (player.isSneaking() && player.getHeldItemMainhand().getItem() == Item.getItemFromBlock(ModBlocks.VASE)
					&& event.getDwheel() != 0) {

				event.setCanceled(true);

				int damage = (player.getHeldItemMainhand().getItemDamage() - BlockVase.MIN_VARIANT
						+ (event.getDwheel() / 120)) % (BlockVase.MAX_VARIANT - BlockVase.MIN_VARIANT + 1)
						+ BlockVase.MIN_VARIANT;
				if (damage < BlockVase.MIN_VARIANT) {
					damage = BlockVase.MAX_VARIANT;
				}
				player.getHeldItemMainhand().setItemDamage(damage);
				PacketHandler.INSTANCE.sendToServer(new MessageVaseMeta(damage));

			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onFoodTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if (!stack.isEmpty() && stack.getItem() instanceof ItemFood && stack.hasTagCompound()
				&& stack.getTagCompound().hasKey("oiled")) {
			event.getToolTip().add(
					TextFormatting.DARK_GREEN + "" + TextFormatting.ITALIC + I18n.format("tooltip.rustic.olive_oil"));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onBoozeTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if (!stack.isEmpty()) {
			if (stack.getItem() instanceof ItemFluidContainer) {
				FluidStack fluid = FluidUtil.getFluidContained(stack);
				if (fluid != null && fluid.getFluid() != null && fluid.getFluid() instanceof FluidBooze) {
					if (fluid.tag != null && fluid.tag.hasKey(FluidBooze.QUALITY_NBT_KEY, 5)) {
						float quality = fluid.tag.getFloat(FluidBooze.QUALITY_NBT_KEY);
						event.getToolTip()
								.add(TextFormatting.GOLD + "" + I18n.format("tooltip.rustic.quality") + quality);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderBlockOverlayEvent(RenderBlockOverlayEvent event) {
		if (event.getOverlayType() == OverlayType.WATER) {
			Minecraft minecraft = Minecraft.getMinecraft();
			EntityPlayer player = event.getPlayer();
			BlockPos pos = event.getBlockPos().add(0, player.getEyeHeight(), 0);
			IBlockState state = minecraft.world.getBlockState(pos);

			if (state.getBlock().equals(ModFluids.BLOCK_OLIVE_OIL)) {
				event.setCanceled(true);
				float brightness = player.getBrightnessForRender();
				GlStateManager.color(brightness, brightness, brightness, 0.99F);
				drawBlockOverlay(OLIVE_OIL_OVERLAY);
				GlStateManager.color(1, 1, 1, 1);
			} else if (state.getBlock().equals(ModFluids.BLOCK_IRONBERRY_JUICE)) {
				event.setCanceled(true);
				float brightness = player.getBrightnessForRender();
				GlStateManager.color(brightness, brightness, brightness, 0.99F);
				drawBlockOverlay(IRONBERRY_JUICE_OVERLAY);
				GlStateManager.color(1, 1, 1, 1);
			} else if (state.getBlock().equals(ModFluids.BLOCK_WILDBERRY_JUICE)) {
				event.setCanceled(true);
				float brightness = player.getBrightnessForRender();
				GlStateManager.color(brightness, brightness, brightness, 0.99F);
				drawBlockOverlay(WILDBERRY_JUICE_OVERLAY);
				GlStateManager.color(1, 1, 1, 1);
			} else if (state.getBlock().equals(ModFluids.BLOCK_GRAPE_JUICE)) {
				event.setCanceled(true);
				float brightness = player.getBrightnessForRender();
				GlStateManager.color(brightness, brightness, brightness, 0.99F);
				drawBlockOverlay(GRAPE_JUICE_OVERLAY);
				GlStateManager.color(1, 1, 1, 1);
			} else if (state.getBlock().equals(ModFluids.BLOCK_APPLE_JUICE)) {
				event.setCanceled(true);
				float brightness = player.getBrightnessForRender();
				GlStateManager.color(brightness, brightness, brightness, 0.99F);
				drawBlockOverlay(APPLE_JUICE_OVERLAY);
				GlStateManager.color(1, 1, 1, 1);
			} else if (state.getBlock().equals(ModFluids.BLOCK_ALE_WORT)) {
				event.setCanceled(true);
				float brightness = player.getBrightnessForRender();
				GlStateManager.color(brightness, brightness, brightness, 0.99F);
				drawBlockOverlay(ALE_WORT_OVERLAY);
				GlStateManager.color(1, 1, 1, 1);
			} else if (state.getBlock().equals(ModFluids.BLOCK_HONEY)) {
				event.setCanceled(true);
				float brightness = player.getBrightnessForRender();
				GlStateManager.color(brightness, brightness, brightness, 0.99F);
				drawBlockOverlay(HONEY_OVERLAY);
				GlStateManager.color(1, 1, 1, 1);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderArmorToughnessEvent(RenderGameOverlayEvent.Post event) {
		if (event.getType() == ElementType.FOOD) {
			if (Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityLivingBase) {
				EntityLivingBase viewEntity = (EntityLivingBase) Minecraft.getMinecraft().getRenderViewEntity();
				int armorToughness = MathHelper.floor(
						viewEntity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
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
		if (event.getType() == ElementType.ARMOR && Config.EXTRA_ARMOR_HUD) {
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

				if (viewEntity instanceof EntityPlayer) {
					armorLevel = ForgeHooks.getTotalArmorValue((EntityPlayer) viewEntity);
				}

				int armorRows = MathHelper.ceil(armorLevel / 20.0F);
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
		if ((player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()
				|| !(player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBlock))
				&& !player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
			stack = player.getHeldItem(EnumHand.OFF_HAND);
		} else if (!player.getHeldItem(EnumHand.MAIN_HAND).isEmpty()) {
			stack = player.getHeldItem(EnumHand.MAIN_HAND);
		}
		if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK && event.getTarget().sideHit.getAxis() == Axis.Y
				&& !stack.isEmpty() && Block.getBlockFromItem(stack.getItem()) instanceof IAdvancedRotationPlacement) {
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
					GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
					GlStateManager.DestFactor.ZERO);
			GlStateManager.glLineWidth(2.0F);
			GlStateManager.disableTexture2D();
			GlStateManager.depthMask(false);
			BlockPos pos = event.getTarget().getBlockPos();
			IBlockState iblockstate = player.getEntityWorld().getBlockState(pos);
			AxisAlignedBB AABB = iblockstate.getBoundingBox(player.getEntityWorld(), pos);
			boolean isAABBCenteredXZ = (((AABB.minX + ((AABB.maxX - AABB.minX) / 2D)) == 0.5D)
					|| ((AABB.maxX - ((AABB.maxX - AABB.minX) / 2D)) == 0.5D))
					&& (((AABB.minZ + ((AABB.maxZ - AABB.minZ) / 2D)) == 0.5D)
							|| ((AABB.maxZ - ((AABB.maxZ - AABB.minZ) / 2D)) == 0.5D));

			if (iblockstate.getMaterial() != Material.AIR && player.getEntityWorld().getWorldBorder().contains(pos)
					&& isAABBCenteredXZ) {
				double d0 = player.lastTickPosX
						+ (player.posX - player.lastTickPosX) * (double) event.getPartialTicks();
				double d1 = player.lastTickPosY
						+ (player.posY - player.lastTickPosY) * (double) event.getPartialTicks();
				double d2 = player.lastTickPosZ
						+ (player.posZ - player.lastTickPosZ) * (double) event.getPartialTicks();
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder vertexbuffer = tessellator.getBuffer();
				vertexbuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);
				vertexbuffer.setTranslation(-d0, -d1, -d2);
				AABB = AABB.grow(0.0020000000949949026D);
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
	private void drawBlockOverlay(ResourceLocation location) {

		Minecraft.getMinecraft().getTextureManager().bindTexture(location);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.pushMatrix();
		/*
		float f1 = 4.0F;
		float f2 = -1.0F;
		float f3 = 1.0F;
		float f4 = -1.0F;
		float f5 = 1.0F;
		float f6 = -0.5F;
		*/
		float f7 = -Minecraft.getMinecraft().player.rotationYaw / 64.0F;
		float f8 = Minecraft.getMinecraft().player.rotationPitch / 64.0F;
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
		vertexbuffer.pos(-1.0D, -1.0D, -0.5D).tex((double) (4.0F + f7), (double) (4.0F + f8)).endVertex();
		vertexbuffer.pos(1.0D, -1.0D, -0.5D).tex((double) (0.0F + f7), (double) (4.0F + f8)).endVertex();
		vertexbuffer.pos(1.0D, 1.0D, -0.5D).tex((double) (0.0F + f7), (double) (0.0F + f8)).endVertex();
		vertexbuffer.pos(-1.0D, 1.0D, -0.5D).tex((double) (4.0F + f7), (double) (0.0F + f8)).endVertex();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableBlend();

	}

	@SideOnly(Side.CLIENT)
	private void drawRotablePlacementBlockHighlight(BufferBuilder buffer, AxisAlignedBB AABB, EnumFacing side,
			BlockPos pos) {
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
