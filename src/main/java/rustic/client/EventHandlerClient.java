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
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent.OverlayType;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.models.BookBakedModel;
import rustic.client.models.LiquidBarrelItemModel;
import rustic.client.models.TEISRModel;
import rustic.client.renderer.LayerIronSkin;
import rustic.client.util.FluidClientUtil;
import rustic.common.Config;
import rustic.common.blocks.BlockChair;
import rustic.common.blocks.BlockVase;
import rustic.common.blocks.IAdvancedRotationPlacement;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.fluids.FluidBooze;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.network.MessageVaseMeta;
import rustic.common.network.PacketHandler;
import rustic.common.potions.PotionsRustic;
import rustic.core.ClientProxy;

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
	
	public static ResourceLocation FULLMETAL_OVERLAY = new ResourceLocation(
			"rustic:textures/misc/fullmetal_overlay.png");

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
				
				final int currentDamage = player.getHeldItemMainhand().getItemDamage();
				final int offset = (event.getDwheel() / 120);
				
				
				int currentIndex = -1;
				for (int i = 0; i < BlockVase.ITEM_ORDER.length; i++) {
					if (BlockVase.ITEM_ORDER[i] == currentDamage) {
						currentIndex = i;
						break;
					}
				}
				
				int damage = currentDamage;
				if (currentIndex >= 0) {
					final int diff = BlockVase.ITEM_ORDER.length;
					damage = BlockVase.ITEM_ORDER[(((currentIndex + offset) % diff) + diff) % diff];
				} else {
					final int diff = BlockVase.MAX_VARIANT - BlockVase.MIN_VARIANT + 1;
					damage = ((((damage - BlockVase.MIN_VARIANT + offset) % diff) + diff) % diff) + BlockVase.MIN_VARIANT;
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
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onFullmetalFov(FOVUpdateEvent event) {
		EntityPlayer player = event.getEntity();
		if (player.isPotionActive(PotionsRustic.FULLMETAL_POTION)) {
			float f = 1.0F;
			if (player.isHandActive() && player.getActiveItemStack().getItem() == Items.BOW) {
	            int i = player.getItemInUseMaxCount();
	            float f1 = (float) i / 20.0F;
	            if (f1 > 1.0F) {
	                f1 = 1.0F;
	            } else {
	                f1 = f1 * f1;
	            }
	            f *= 1.0F - f1 * 0.15F;
	        }
			event.setNewfov(f);
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onPlayerInputEvent(InputUpdateEvent event) {
		if (event.getEntityPlayer().isPotionActive(PotionsRustic.FULLMETAL_POTION)) {
			MovementInput mi = event.getMovementInput();
			mi.jump = false;
			mi.moveForward = 0F;
			mi.moveStrafe = 0F;
		}
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderLivingPreEvent(RenderLivingEvent.Pre<? extends EntityLivingBase> event) {
		if (!ClientProxy.IRONSKIN_LAYER_DISABLED_RENDERERS.contains(event.getRenderer().getClass()))
			LayerIronSkin.preRenderEntity(event.getEntity());
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderLivingPostEvent(RenderLivingEvent.Post<? extends EntityLivingBase> event) {
		if (!ClientProxy.IRONSKIN_LAYER_DISABLED_RENDERERS.contains(event.getRenderer().getClass()))
			LayerIronSkin.postRenderEntity(event.getEntity());
	}
	
	/* TODO: implement for empty hand and maps?
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderSpecificFirstPersonHandEvent(RenderSpecificHandEvent event) {
		if (!event.getItemStack().isEmpty() || (event.getHand() != EnumHand.MAIN_HAND)) return;
		
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayerSP player = mc.player;
		
		if (player.isInvisible()) return;
		
		PotionEffect ironSkinEffect = player.getActivePotionEffect(PotionsRustic.IRON_SKIN_POTION);
		boolean hasFullmetalEffect = player.isPotionActive(PotionsRustic.FULLMETAL_POTION);
		if ((ironSkinEffect == null) && !hasFullmetalEffect && !LayerIronSkin.FORCE_IRONSKIN_RENDER()) return;
		
		
		
		
		// TODO: implement
		
		
	}*/
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderFullmetalOverlayEvent(RenderHandEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		
		PotionEffect effect = player.getActivePotionEffect(PotionsRustic.FULLMETAL_POTION);
		if (effect == null) return;
		
		event.setCanceled(true);
		
		boolean flag = (mc.getRenderViewEntity() instanceof EntityLivingBase) && ((EntityLivingBase) mc.getRenderViewEntity()).isPlayerSleeping();
	
		if ((mc.gameSettings.thirdPersonView == 0) && !flag && !mc.gameSettings.hideGUI && !mc.playerController.isSpectator()) {
            mc.entityRenderer.enableLightmap();
            mc.entityRenderer.itemRenderer.renderItemInFirstPerson(event.getPartialTicks());
            mc.entityRenderer.disableLightmap();
        }
		
		GlStateManager.popMatrix();
		
		GlStateManager.pushMatrix();
		
        GlStateManager.disableAlpha();
		
		final float brightness = player.getBrightnessForRender();
		// TODO: modulate transparency depending on time remaining on effect?
		
		final float baseAlpha = 0.9625F;
		float durationFade = 1.0F;
		if (effect.getDuration() < 100) {
			float dur = (effect.getDuration() + event.getPartialTicks()) * 2F;
            float j1 = 10 - (dur / 20);
            durationFade = (
            	0.5F +
            	MathHelper.clamp(dur / 400F, 0.0F, 0.9375F - 0.5F) + (
            			MathHelper.cos((200 - dur) * (float) Math.PI / 200F * 9F) *
            			MathHelper.clamp(j1 / 10.0F * 0.25F, 0.0625F, 0.25F)
				)
			);
            durationFade = MathHelper.sqrt(durationFade);
            if (dur < 20) {
            	durationFade *= MathHelper.sqrt(dur / 20F);
            }
            //System.out.println(effect.getDuration() + ": " + durationFade);
            //durationFade = MathHelper.clamp(durationFade, 0.5F, 1.0F);
        }
		
		GlStateManager.color(brightness, brightness, brightness, baseAlpha * durationFade);
		
		mc.getTextureManager().bindTexture(FULLMETAL_OVERLAY);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_COLOR,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		GlStateManager.disableDepth();
		GlStateManager.pushMatrix();
		//GlStateManager.color(1F, 1F, 1F, 1F);
		/*float f1 = -1.0F;
        float f2 = 1.0F;
        float f3 = -1.0F;
        float f4 = 1.0F;
        float f5 = -0.5F;*/
        double f6 = -0.5F;
        double f7 = 1.5F;
        double f8 = -0.5F;
        double f9 = 1.5F;
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION_TEX);
        vertexbuffer.pos(-1.0D, -1.0D, -0.5D).tex(f7, f9).endVertex();
        vertexbuffer.pos(1.0D, -1.0D, -0.5D).tex(f6, f9).endVertex();
        vertexbuffer.pos(1.0D, 1.0D, -0.5D).tex(f6, f8).endVertex();
        vertexbuffer.pos(-1.0D, 1.0D, -0.5D).tex(f7, f8).endVertex();
		tessellator.draw();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableDepth();
		GlStateManager.disableBlend();
		GlStateManager.enableAlpha();
		
		//GlStateManager.pushMatrix();
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
	public void onUpdateSittingEntity(RenderLivingEvent.Pre<? extends EntityLivingBase> event) {
		EntityLivingBase entity = event.getEntity();
		if (entity == Minecraft.getMinecraft().player) return;
		
		if (entity.isRiding() && (entity.getRidingEntity() instanceof BlockChair.EntityChair)) {
			BlockChair.EntityChair chair = (BlockChair.EntityChair) entity.getRidingEntity();
			entity.setRenderYawOffset(chair.rotationYaw);
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
