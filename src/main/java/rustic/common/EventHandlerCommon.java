package rustic.common;

import java.util.Random;

import com.google.common.collect.Sets;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITasks.EntityAITaskEntry;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import rustic.common.blocks.ModBlocks;
import rustic.common.entities.ai.EntityAITemptRustic;
import rustic.common.items.ItemFluidBottle;
import rustic.common.items.ModItems;
import rustic.common.util.GenericUtils;

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

	@SubscribeEvent(priority = EventPriority.HIGHEST)
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

	@SubscribeEvent(priority = EventPriority.HIGHEST)
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
					if (state.getBlock() == ModBlocks.BREWING_BARREL && event.getFace() != EnumFacing.DOWN) {
						return;
					}
					
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
							NBTTagCompound fluidTag = fill.writeToNBT(new NBTTagCompound());
							NBTTagCompound tag = new NBTTagCompound();
							tag.setTag(ItemFluidBottle.FLUID_NBT_KEY, fluidTag);
							bottlestack.setTagCompound(tag);
							if (!player.inventory.addItemStackToInventory(bottlestack)) {
								player.dropItem(bottlestack, false);
							}
							event.setCanceled(true);
						} else if (tank.drain(1000, false).getFluid() == FluidRegistry.WATER) {
							//FluidStack fill = tank.drain(1000, true);
							tank.drain(1000, true);
							player.addStat(StatList.getObjectUseStats(stack.getItem()));
							player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
							stack.shrink(1);
							ItemStack bottlestack = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM),
									PotionTypes.WATER);
							if (!player.inventory.addItemStackToInventory(bottlestack)) {
								player.dropItem(bottlestack, false);
							}
							event.setCanceled(true);
							event.setUseBlock(Result.DENY);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onSeedInteractEvent(PlayerInteractEvent.EntityInteract event) {
		
		if (event.getTarget() instanceof EntityChicken) {
			ItemStack equippedItem = event.getEntityPlayer().getHeldItem(event.getHand());
			EntityAnimal targetAnimal = (EntityAnimal) event.getTarget();

			if (equippedItem != null && 
			(equippedItem.getItem() == ModItems.TOMATO_SEEDS || equippedItem.getItem() == ModItems.CHILI_PEPPER_SEEDS) &&
			targetAnimal.getGrowingAge() >= 0 &&
			!targetAnimal.isInLove()) {
				EntityPlayer player = event.getEntityPlayer();

				if (!player.capabilities.isCreativeMode) {
					equippedItem.shrink(1);
				}

				targetAnimal.setInLove(player);
			}
		}
	}

	@SubscribeEvent
	public void onChickenUpdate(LivingUpdateEvent event) {
		if ((event.getEntity().getClass().equals(EntityChicken.class))) {
			EntityChicken chicken = (EntityChicken) event.getEntity();
			
			for (EntityAITaskEntry task : chicken.tasks.taskEntries) {
				if (task.action instanceof EntityAITemptRustic) return;
			}
			
			chicken.tasks.addTask(4, new EntityAITemptRustic(chicken, 1, Sets.newHashSet(
					ModItems.CHILI_PEPPER_SEEDS,
					ModItems.TOMATO_SEEDS,
					Item.getItemFromBlock(ModBlocks.APPLE_SEEDS),
					Item.getItemFromBlock(ModBlocks.GRAPE_STEM)
			), false));
		}
	}
}
