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
import rustic.common.blocks.ModBlocks;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.Recipes;
import rustic.core.Rustic;

public class TileEntityCondenser extends TileEntityCondenserBase {
	public static int SLOT_NUM = 5;
	
	protected int getInternalSize() {
		return SLOT_NUM;
	}
	
	private IItemHandler handlerBottom = new RangedWrapper(externalStackHandler, SLOT_RESULT, SLOT_RESULT + 1); // result
	private IItemHandler handlerBack = new RangedWrapper(externalStackHandler, SLOT_FUEL, SLOT_BOTTLE + 1); // fuel + bottle (unlikely to ever be the same)
	private IItemHandler handlerSide = new RangedWrapper(externalStackHandler, SLOT_INGREDIENTS_START, SLOT_INGREDIENTS_START + 2); // ingredients
	
	public String getName() {
		return "container.rustic.condenser";
	}
	
	public TileEntityCondenser() {
		super();
	}
	
	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		if (world.getBlockState(pos).getBlock() != ModBlocks.CONDENSER
				|| !((BlockCondenser) world.getBlockState(pos).getBlock()).hasRetorts(world, pos,
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
		if (world.getBlockState(pos).getBlock() != ModBlocks.CONDENSER
				|| !((BlockCondenser) world.getBlockState(pos).getBlock()).hasRetorts(world, pos,
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
		
		if (state.getBlock() != ModBlocks.CONDENSER
				|| !state.getValue(BlockCondenser.BOTTOM)) {
			return false;
		}

		if (this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START).isEmpty()
				&& this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 1).isEmpty()) {
			return false;
		}

		if (internalStackHandler.getStackInSlot(SLOT_BOTTLE).isEmpty()
				|| internalStackHandler.getStackInSlot(SLOT_BOTTLE).getCount() < 1) {
			return false;
		}

			
		if (!((BlockCondenser) state.getBlock()).hasRetorts(world, pos, state)) {
			return false;
		}
		
		return true;
	}
	
	@Override
	protected void refreshCurrentRecipe() {
		if (this.hasContentChanged) {
			this.hasContentChanged = false;
			Fluid fluid = this.getFluid();
			ItemStack bottle = internalStackHandler.getStackInSlot(SLOT_BOTTLE);
			ItemStack[] inputs = new ItemStack[]{ this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START), this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 1) };
			if (this.currentRecipe != null && this.currentRecipe.matches(fluid, ItemStack.EMPTY, bottle, inputs)) {
				return;
			}
			this.brewTime = 0;
			this.currentRecipe = null;
			for (ICondenserRecipe recipe : Recipes.condenserRecipes ) {
				if (!recipe.isBasic()) {
					continue;
				}
				if (recipe.matches(fluid, ItemStack.EMPTY, bottle, inputs)) {
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
			if (world.getBlockState(pos).getBlock() == ModBlocks.CONDENSER) {
				EnumFacing blockFacing = world.getBlockState(pos).getValue(BlockCondenser.FACING);
				double yVel = 0.125;
				if (blockFacing == EnumFacing.NORTH || blockFacing == EnumFacing.SOUTH) {
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() - 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, yVel, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 1.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, yVel, 0);
				} else if (blockFacing == EnumFacing.EAST || blockFacing == EnumFacing.WEST) {
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() - 0.5D, 0, yVel, 0);
					Rustic.proxy.spawnAlchemySmokeFX(world, this.brewTime, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 1.5D, 0, yVel, 0);
				}
			}
		}
	}
	
	@Override
	protected void brew() {
		if (this.canBrew()) {
			ItemStack[] inputs = new ItemStack[]{ this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START), this.internalStackHandler.getStackInSlot(SLOT_INGREDIENTS_START + 1) };
			ItemStack bottle = internalStackHandler.getStackInSlot(SLOT_BOTTLE);
			ICondenserRecipe recipe = this.currentRecipe;
			if (recipe.matches(this.getFluid(), ItemStack.EMPTY, bottle, inputs) && this.getAmount() >= recipe.getFluid().amount) {
				internalStackHandler.insertItem(SLOT_RESULT, recipe.getResult(), false);
				int[] consume = recipe.getInputConsumption(inputs);
				internalStackHandler.extractItem(SLOT_INGREDIENTS_START, consume[0], false);
				internalStackHandler.extractItem(SLOT_INGREDIENTS_START + 1, consume[1], false);
				internalStackHandler.extractItem(SLOT_BOTTLE, recipe.getBottleConsumption(bottle), false);
				tank.drain(recipe.getFluid().amount, true);
			}
		}
	}
	
	
	
}
