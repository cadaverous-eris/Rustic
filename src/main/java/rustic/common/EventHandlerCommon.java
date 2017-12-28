package rustic.common;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.models.LiquidBarrelItemModel;
import rustic.client.util.FluidClientUtil;
import rustic.common.blocks.IAdvancedRotationPlacement;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.fluids.FluidBooze;
import rustic.common.items.ItemFluidBottle;
import rustic.common.items.ModItems;
import rustic.common.network.PacketHandler;
import rustic.common.potions.PotionsRustic;
import rustic.common.tileentity.ITileEntitySyncable;
import rustic.common.util.GenericUtils;
import rustic.core.Rustic;

public class EventHandlerCommon {

	private Random rand = new Random();

	@SubscribeEvent
	public void onOliveOilCraftingEvent(PlayerEvent.ItemCraftedEvent event) {
		if (event.player != null) {
			if (!event.crafting.isEmpty() && event.crafting.getItem() instanceof ItemFood
					&& event.crafting.hasTagCompound() && event.crafting.getTagCompound().hasKey("oiled")) {
				if (!event.player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE))) {
					event.player.dropItem(new ItemStack(Items.GLASS_BOTTLE), false);
				}
			}
		}
	}

	@SubscribeEvent
	public void onVineDropEvent(BlockEvent.HarvestDropsEvent event) {
		if (event.getState().getBlock() == Blocks.VINE) {
			boolean tryDrop = false;
			if (Config.ENABLE_SEED_DROPS) {
				if (!Config.GRAPE_DROP_NEEDS_TOOL) {
					tryDrop = true;
				} else if (event.getHarvester() != null && !event.getHarvester().getHeldItemMainhand().isEmpty()) {
					ItemStack stack = event.getHarvester().getHeldItemMainhand();
					String itemName = stack.getItem().getRegistryName().toString();
					if (Config.GRAPE_TOOL_WHITELIST.contains(itemName)) {
						tryDrop = true;
					}
				}
			}
			if (tryDrop && rand.nextInt(10) == 0) {
				try {
					event.getDrops().add(new ItemStack(ModBlocks.GRAPE_STEM));
				} catch (UnsupportedOperationException e) {

				}
			}
		}
	}

	@SubscribeEvent
	public void onItemUseTick(LivingEntityUseItemEvent.Tick event) {
		ItemStack originalStack = event.getItem();
		if (!originalStack.isEmpty() && originalStack.getItem() instanceof ItemSoup && originalStack.hasTagCompound()
				&& originalStack.getTagCompound().hasKey("oiled")) {
			EntityLivingBase entityLiving = event.getEntityLiving();
			if (entityLiving instanceof EntityPlayer && event.getDuration() == 1) {
				EntityPlayer entityplayer = (EntityPlayer) entityLiving;
				entityplayer.getFoodStats().addStats(2, 0.3F);
			}
		}
	}

	@SubscribeEvent
	public void onItemUseFinish(LivingEntityUseItemEvent.Finish event) {
		ItemStack originalStack = event.getItem();
		if (!originalStack.isEmpty() && originalStack.getItem() instanceof ItemFood && originalStack.hasTagCompound()
				&& originalStack.getTagCompound().hasKey("oiled")) {
			EntityLivingBase entityLiving = event.getEntityLiving();
			if (entityLiving instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entityLiving;
				entityplayer.getFoodStats().addStats(2, 0.3F);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerUseGlassBottle(PlayerInteractEvent.RightClickBlock event) {
		if (event.getItemStack().getItem().equals(Items.GLASS_BOTTLE)) {
			EntityPlayer player = event.getEntityPlayer();
			BlockPos pos = event.getPos();
			ItemStack stack = event.getItemStack();
			World world = event.getWorld();
			RayTraceResult raytraceresult = GenericUtils.rayTrace(world, player, true);
			if (raytraceresult == null || raytraceresult.getBlockPos() == null) return;
			BlockPos pos2 = raytraceresult.getBlockPos();
			if (player.canPlayerEdit(pos2, event.getFace(), stack)
					&& player.canPlayerEdit(pos2.offset(raytraceresult.sideHit), raytraceresult.sideHit, stack)) {
				IBlockState state = world.getBlockState(pos2);
				if (state.getBlock() instanceof IFluidBlock) {
					IFluidBlock fluidblock = ((IFluidBlock) state.getBlock());
					if (ItemFluidBottle.VALID_FLUIDS.contains(fluidblock.getFluid())
							&& fluidblock.getFilledPercentage(world, pos2) == 1) {
						world.setBlockState(pos2, Blocks.AIR.getDefaultState(), 11);
						player.addStat(StatList.getObjectUseStats(stack.getItem()));
						player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
						stack.shrink(1);
						ItemStack bottlestack = new ItemStack(ModItems.FLUID_BOTTLE, 1);
						NBTTagCompound fluidTag = new FluidStack(fluidblock.getFluid(), 1000)
								.writeToNBT(new NBTTagCompound());
						NBTTagCompound tag = new NBTTagCompound();
						tag.setTag(ItemFluidBottle.FLUID_NBT_KEY, fluidTag);
						bottlestack.setTagCompound(tag);
						if (!player.inventory.addItemStackToInventory(bottlestack)) {
							player.dropItem(bottlestack, false);
						}
					}
				} else if (state.getBlock() instanceof ITileEntityProvider && world.getTileEntity(pos) != null
						&& world.getTileEntity(pos).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
								event.getFace())) {
					IFluidHandler tank = world.getTileEntity(pos)
							.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, event.getFace());
					if (tank != null && tank.drain(1000, false) != null && tank.drain(1000, false).getFluid() != null) {
						if (ItemFluidBottle.VALID_FLUIDS.contains(tank.drain(1000, false).getFluid())
								&& tank.drain(1000, false).amount >= 1000) {
							FluidStack fill = tank.drain(1000, true);
							player.addStat(StatList.getObjectUseStats(stack.getItem()));
							player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
							stack.shrink(1);
							ItemStack bottlestack = new ItemStack(ModItems.FLUID_BOTTLE, 1);
							NBTTagCompound fluidTag = new FluidStack(fill.getFluid(), 1000)
									.writeToNBT(new NBTTagCompound());
							NBTTagCompound tag = new NBTTagCompound();
							tag.setTag(ItemFluidBottle.FLUID_NBT_KEY, fluidTag);
							bottlestack.setTagCompound(tag);
							if (!player.inventory.addItemStackToInventory(bottlestack)) {
								player.dropItem(bottlestack, false);
							}
							event.setCanceled(true);
						} else if (tank.drain(1000, false).getFluid() == FluidRegistry.WATER) {
							FluidStack fill = tank.drain(1000, true);
							player.addStat(StatList.getObjectUseStats(stack.getItem()));
							player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
							stack.shrink(1);
							ItemStack bottlestack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM),
									PotionTypes.WATER);
							if (!player.inventory.addItemStackToInventory(bottlestack)) {
								player.dropItem(bottlestack, false);
							}
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}

}
