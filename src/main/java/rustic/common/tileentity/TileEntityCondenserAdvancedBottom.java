package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import rustic.common.blocks.BlockCondenser;
import rustic.common.blocks.BlockCondenserAdvanced;
import rustic.common.blocks.ModBlocks;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.Recipes;
import rustic.core.Rustic;

public class TileEntityCondenserAdvancedBottom extends TileEntityCondenserBase {
	
	public static final int SLOT_NUM = 7;
	
	protected int getInternalSize() {
		return SLOT_NUM;
	}
	
	private IItemHandler handlerBottom = new RangedWrapper(externalStackHandler, SLOT_RESULT, SLOT_RESULT + 1); // result
	private IItemHandler handlerBack = new RangedWrapper(externalStackHandler, SLOT_FUEL, SLOT_BOTTLE + 1); // fuel and bottles - unlikely to ever be the same
	private IItemHandler handlerTop = new RangedWrapper(externalStackHandler, SLOT_INGREDIENTS_START, SLOT_INGREDIENTS_START + 1); // modifier
	private IItemHandler handlerSide = new RangedWrapper(externalStackHandler, SLOT_INGREDIENTS_START+1, SLOT_INGREDIENTS_START+4); // ingredients
	
	public String getName() {
		return "container.rustic.condenser_advanced";
	}

	public TileEntityCondenserAdvancedBottom() {
		super();
	}
	

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		if (world.getBlockState(pos).getBlock() != ModBlocks.CONDENSER_ADVANCED
				|| !((BlockCondenserAdvanced) world.getBlockState(pos).getBlock()).hasRetorts(world, pos,
						world.getBlockState(pos))) {
			return false;
		}
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
				|| capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
				|| super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (world.getBlockState(pos).getBlock() != ModBlocks.CONDENSER_ADVANCED
				|| !((BlockCondenserAdvanced) world.getBlockState(pos).getBlock()).hasRetorts(world, pos,
						world.getBlockState(pos))) {
			return null;
		}
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return (T) tank;
		} else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == null)
				return (T) externalStackHandler;
			else if (facing == EnumFacing.DOWN)
				return (T) handlerBottom;
			else if (facing == EnumFacing.UP)
				return (T) handlerTop;
			else if (facing == world.getBlockState(pos).getValue(BlockCondenser.FACING).getOpposite())
				return (T) handlerBack;
			else if (facing.getAxis() != Axis.Y)
				return (T) handlerSide;
			else
				return (T) externalStackHandler;
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
	protected boolean canBrew() {
		IBlockState state = world.getBlockState(pos);
		
		if (state.getBlock() != ModBlocks.CONDENSER_ADVANCED
				|| !state.getValue(BlockCondenserAdvanced.BOTTOM)) {
			return false;
		}
		
		if (this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 1).isEmpty()
				&& this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 2).isEmpty()
				&& this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 3).isEmpty()) {
			return false;
		}
		
//		if (internalStackHandler.getStackInSlot(SLOT_BOTTLE).isEmpty()
//				|| internalStackHandler.getStackInSlot(SLOT_BOTTLE).getCount() < 1) {
//			return false;
//		}

		if (!((BlockCondenserAdvanced) state.getBlock()).hasRetorts(world, pos, state)) {
			return false;
		}

		return true;
	}
	
	@Override
	protected void refreshCurrentRecipe() {
		if (this.hasContentChanged) {
			this.hasContentChanged = false;
			Fluid fluid = this.getFluid();
			ItemStack modifier = internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START);
			ItemStack bottle = internalStackHandler.getStackInSlot(SLOT_BOTTLE);
			ItemStack[] inputs = new ItemStack[]{ this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 1), this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 2), this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 3) };
			if (this.currentRecipe != null && this.currentRecipe.matches(fluid, modifier, bottle, inputs)) {
				return;
			}
			this.brewTime = 0;
			this.currentRecipe = null;
			for (ICondenserRecipe recipe : Recipes.condenserRecipes) {
				if (!recipe.isBasic() && !recipe.isAdvanced()) {
					continue;
				}
				if (recipe.matches(fluid, modifier, bottle, inputs)) {
					this.currentRecipe = recipe;
					this.totalBrewTime = recipe.getTime();
					return;
				}
			}
		}
	}

	@Override
	protected void renderParticles() {
		if (world.isRemote) {
			if (world.getBlockState(pos).getBlock() == ModBlocks.CONDENSER_ADVANCED) {
				EnumFacing blockFacing = world.getBlockState(pos).getValue(BlockCondenserAdvanced.FACING);
				double yVel = 0.125;
				switch (blockFacing) {
				case NORTH:
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() - 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 1.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 1.5D, 0, 0.05D, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() - 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, yVel, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 1.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, yVel, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 1.5D, 0, yVel, 0);
					break;
				case SOUTH:
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() - 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 1.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() - 0.5D, 0, 0.05D, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() - 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, yVel, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 1.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, yVel, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() - 0.5D, 0, yVel, 0);
					break;
				case WEST:
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() - 0.5D, 0, 0.05D, 0);
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 1.5D, 0, 0.05D, 0);
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 1.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() - 0.5D, 0, yVel, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 1.5D, 0, yVel, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 1.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, yVel, 0);
					break;
				case EAST:
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() - 0.5D, 0, 0.05D, 0);
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 1.5D, 0, 0.05D, 0);
					//world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() - 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() - 0.5D, 0, yVel, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 1.5D, 0, yVel, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() - 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, yVel, 0);
					break;
				default:
					break;
				}
			}
		}
	}
	
	@Override
	protected void brew() {
		if (this.canBrew()) {
			Fluid fluid = this.getFluid();
			ItemStack modifier = internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START);
			ItemStack bottle = internalStackHandler.getStackInSlot(SLOT_BOTTLE);
			ItemStack[] inputs = new ItemStack[]{ this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 1), this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 2), this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 3) };
			ICondenserRecipe recipe = this.currentRecipe;
			if (recipe.matches(fluid, modifier, bottle, inputs) && this.getAmount() >= recipe.getFluid().amount) {
				internalStackHandler.insertItem(SLOT_RESULT, recipe.getResult(), false);
				internalStackHandler.extractItem(SLOT_INGREDIENTS_START, recipe.getModifierConsumption(modifier), false);
				int[] consume = recipe.getInputConsumption(inputs);
				internalStackHandler.extractItem(SLOT_INGREDIENTS_START + 1, consume[0], false);
				internalStackHandler.extractItem(SLOT_INGREDIENTS_START + 2, consume[1], false);
				internalStackHandler.extractItem(SLOT_INGREDIENTS_START + 3, consume[2], false);
				internalStackHandler.extractItem(SLOT_BOTTLE, recipe.getBottleConsumption(bottle), false);
				tank.drain(recipe.getFluid().amount, true);
			}
		}
	}
}
