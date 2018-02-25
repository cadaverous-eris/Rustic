package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustic.common.Config;
import rustic.common.blocks.fluids.FluidBooze;
import rustic.common.crafting.BrewingBarrelRecipe;
import rustic.common.crafting.Recipes;
import rustic.common.inventory.ExternalItemHandler;
import rustic.common.items.ModItems;

public class TileEntityBrewingBarrel extends TileEntity implements ITickable {

	protected ItemStackHandler internalStackHandler = new ItemStackHandler(6) {
		@Override
		protected void onContentsChanged(int slot) {
			IBlockState state = world.getBlockState(pos);
			TileEntityBrewingBarrel.this.world.addBlockEvent(TileEntityBrewingBarrel.this.pos,
					TileEntityBrewingBarrel.this.getBlockType(), 1, 0);
			getWorld().notifyBlockUpdate(pos, state, state, 3);
			world.notifyNeighborsOfStateChange(TileEntityBrewingBarrel.this.pos,
					TileEntityBrewingBarrel.this.getBlockType(), true);
			TileEntityBrewingBarrel.this.markDirty();
		}
	};
	private ExternalItemHandler externalStackHandler = new ExternalItemHandler(internalStackHandler) {
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (isStackAllowedInSlot(slot, stack)) {
				return super.insertItem(slot, stack, simulate);
			}
			return stack;
		}

		private boolean isStackAllowedInSlot(int slot, ItemStack stack) {
			if (slot == 1) {
				return stack.getItem() == Items.GLASS_BOTTLE;
			} else if (slot == 2) {
				if (FluidUtil.getFluidHandler(stack) != null) {
					FluidStack fluid = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)
							.drain(1, false);
					return fluid != null && fluid.getFluid() != null && fluid.getFluid() instanceof FluidBooze;
				} else {
					return stack.getItem() == Items.GLASS_BOTTLE;
				}
			} else if (slot == 0) {
				return FluidUtil.getFluidHandler(stack) != null || stack.getItem() instanceof UniversalBucket || stack.getItem() instanceof ItemFluidContainer
						|| stack.getItem() == Items.GLASS_BOTTLE || stack.getItem() instanceof ItemBucket;
			}
			return false;
		}
	};

	protected FluidTank input = new FluidTank(8000);
	protected FluidTank output = new FluidTank(8000);
	protected FluidTank auxiliary = new FluidTank(1000);

	protected int brewTime;

	protected BrewingBarrelRecipe recipe = null;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("items", internalStackHandler.serializeNBT());
		NBTTagCompound inputTank = new NBTTagCompound();
		input.writeToNBT(inputTank);
		tag.setTag("inputTank", inputTank);
		NBTTagCompound outputTank = new NBTTagCompound();
		output.writeToNBT(outputTank);
		tag.setTag("outputTank", outputTank);
		NBTTagCompound auxTank = new NBTTagCompound();
		auxiliary.writeToNBT(auxTank);
		tag.setTag("auxTank", auxTank);
		tag.setInteger("brewTime", brewTime);
		return tag;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("items")) {
			internalStackHandler.deserializeNBT((NBTTagCompound) tag.getTag("items"));
		}
		input.readFromNBT(tag.getCompoundTag("inputTank"));
		output.readFromNBT(tag.getCompoundTag("outputTank"));
		auxiliary.readFromNBT(tag.getCompoundTag("auxTank"));
		if (tag.hasKey("brewTime")) {
			brewTime = tag.getInteger("brewTime");
		}
	}
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		readFromNBT(pkt.getNbtCompound());
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
			this.markDirty();
			return true;
		} else {
			return super.receiveClientEvent(id, type);
		}
	}

	public ITextComponent getDisplayName() {
		return (ITextComponent) new TextComponentTranslation(this.getName(), new Object[0]);
	}

	public String getName() {
		return "container.rustic.brewing_barrel";
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
				|| super.hasCapability(capability, facing);
	}

	public int getInputAmount() {
		return input.getFluidAmount();
	}

	public int getOutputAmount() {
		return output.getFluidAmount();
	}

	public int getAuxiliaryAmount() {
		return auxiliary.getFluidAmount();
	}

	public int getInputCapacity() {
		return input.getCapacity();
	}

	public int getOutputCapacity() {
		return output.getCapacity();
	}

	public int getAuxiliaryCapacity() {
		return auxiliary.getCapacity();
	}

	public FluidStack getInputFluid() {
		if (input.getFluid() != null) {
			return input.getFluid().copy();
		}
		return null;
	}

	public FluidStack getOutputFluid() {
		if (output.getFluid() != null) {
			return output.getFluid().copy();
		}
		return null;
	}

	public FluidStack getAuxiliaryFluid() {
		if (auxiliary.getFluid() != null) {
			return auxiliary.getFluid().copy();
		}
		return null;
	}

	public boolean slot0Empty() {
		return internalStackHandler.getStackInSlot(0).isEmpty();
	}

	public boolean slot1Empty() {
		return internalStackHandler.getStackInSlot(1).isEmpty();
	}

	public boolean slot2Empty() {
		return internalStackHandler.getStackInSlot(2).isEmpty();
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) externalStackHandler;
		}
		return super.getCapability(capability, facing);
	}
	
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		this.invalidate();
		if (internalStackHandler != null && !world.isRemote) {
			for (int i = 0; i < internalStackHandler.getSlots(); i++) {
				if (internalStackHandler.getStackInSlot(i) != null) {
					state.getBlock().spawnAsEntity(world, pos, internalStackHandler.getStackInSlot(i));
				}
			}
		}
		world.setTileEntity(pos, null);
	}

	@Override
	public void update() {
		boolean fluidChanged = false;
		if (!internalStackHandler.getStackInSlot(0).isEmpty() && !world.isRemote) {
			ItemStack stack = internalStackHandler.getStackInSlot(0);
			ItemStack in = stack.copy();
			in.setCount(1);
			if (in.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
				FluidStack fluid = FluidUtil.getFluidContained(in);
				if (fluid != null && fluid.getFluid() != null && input.fill(fluid, false) == fluid.amount) {
					ItemStack out = new ItemStack(in.getItem());
					IFluidHandlerItem fluidHandlerDummy = out
							.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
					fluidHandlerDummy.drain(input.getCapacity() - input.getFluidAmount(), true);
					out = fluidHandlerDummy.getContainer();
					if (out.getItem() == ForgeModContainer.getInstance().universalBucket) {
						out = new ItemStack(Items.BUCKET);
					}
					if (internalStackHandler.insertItem(3, out, true).isEmpty()) {
						input.fill(fluid, true);
						internalStackHandler.getStackInSlot(0).shrink(1);
						internalStackHandler.insertItem(3, out, false);
						fluidChanged |= true;
					}
				} else if ((fluid == null || fluid.getFluid() == null) && input.getFluidAmount() > 0) {
					ItemStack out = new ItemStack(in.getItem());
					IFluidHandlerItem fluidHandlerDummy = out
							.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
					int amount = fluidHandlerDummy.fill(input.getFluid(), true);
					out = fluidHandlerDummy.getContainer();
					if (amount > 0 && internalStackHandler.insertItem(3, out, true).isEmpty()) {
						input.drain(amount, true);
						internalStackHandler.getStackInSlot(0).shrink(1);
						internalStackHandler.insertItem(3, out, false);
						fluidChanged |= true;
					}
				}
			} else if (stack.getItem() == Items.GLASS_BOTTLE && input.getFluidAmount() > 0) {
				ItemStack out = new ItemStack(ModItems.FLUID_BOTTLE);
				IFluidHandlerItem fluidHandlerDummy = out
						.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				int amount = fluidHandlerDummy.fill(input.getFluid(), true);
				out = fluidHandlerDummy.getContainer();
				if (amount > 0 && internalStackHandler.insertItem(3, out, true).isEmpty()) {
					input.drain(amount, true);
					internalStackHandler.getStackInSlot(0).shrink(1);
					internalStackHandler.insertItem(3, out, false);
					fluidChanged |= true;
				}
			}
		}
		if (!internalStackHandler.getStackInSlot(1).isEmpty() && !world.isRemote) {
			ItemStack stack = internalStackHandler.getStackInSlot(1);
			ItemStack in = stack.copy();
			in.setCount(1);
			if (in.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
				FluidStack fluid = FluidUtil.getFluidContained(in);
				if ((fluid == null || fluid.getFluid() == null) && output.getFluidAmount() > 0) {
					ItemStack out = new ItemStack(in.getItem());
					IFluidHandlerItem fluidHandlerDummy = out
							.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
					int amount = fluidHandlerDummy.fill(output.getFluid(), true);
					out = fluidHandlerDummy.getContainer();
					if (amount > 0 && internalStackHandler.insertItem(4, out, true).isEmpty()) {
						output.drain(amount, true);
						internalStackHandler.getStackInSlot(1).shrink(1);
						internalStackHandler.insertItem(4, out, false);
						fluidChanged |= true;
					}
				}
			} else if (stack.getItem() == Items.GLASS_BOTTLE && output.getFluidAmount() > 0) {
				ItemStack out = new ItemStack(ModItems.FLUID_BOTTLE);
				IFluidHandlerItem fluidHandlerDummy = out
						.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				int amount = fluidHandlerDummy.fill(output.getFluid(), true);
				out = fluidHandlerDummy.getContainer();
				if (amount > 0 && internalStackHandler.insertItem(4, out, true).isEmpty()) {
					output.drain(amount, true);
					internalStackHandler.getStackInSlot(1).shrink(1);
					internalStackHandler.insertItem(4, out, false);
					fluidChanged |= true;
				}
			}
		}
		if (!internalStackHandler.getStackInSlot(2).isEmpty() && !world.isRemote) {
			ItemStack stack = internalStackHandler.getStackInSlot(2);
			ItemStack in = stack.copy();
			in.setCount(1);
			if (in.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
				FluidStack fluid = FluidUtil.getFluidContained(in);
				if (fluid != null && fluid.getFluid() != null && fluid.getFluid() instanceof FluidBooze
						&& auxiliary.fill(fluid, false) == fluid.amount) {
					ItemStack out = new ItemStack(in.getItem());
					IFluidHandlerItem fluidHandlerDummy = out
							.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
					fluidHandlerDummy.drain(auxiliary.getCapacity() - auxiliary.getFluidAmount(), true);
					out = fluidHandlerDummy.getContainer();
					if (out.getItem() == ForgeModContainer.getInstance().universalBucket) {
						out = new ItemStack(Items.BUCKET);
					}
					if (internalStackHandler.insertItem(5, out, true).isEmpty()) {
						auxiliary.fill(fluid, true);
						internalStackHandler.getStackInSlot(2).shrink(1);
						internalStackHandler.insertItem(5, out, false);
						fluidChanged |= true;
					}
				} else if ((fluid == null || fluid.getFluid() == null) && auxiliary.getFluidAmount() > 0) {
					ItemStack out = new ItemStack(in.getItem());
					IFluidHandlerItem fluidHandlerDummy = out
							.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
					int amount = fluidHandlerDummy.fill(auxiliary.getFluid(), true);
					out = fluidHandlerDummy.getContainer();
					if (amount > 0 && internalStackHandler.insertItem(5, out, true).isEmpty()) {
						auxiliary.drain(amount, true);
						internalStackHandler.getStackInSlot(2).shrink(1);
						internalStackHandler.insertItem(5, out, false);
						fluidChanged |= true;
					}
				}
			} else if (stack.getItem() == Items.GLASS_BOTTLE && auxiliary.getFluidAmount() > 0) {
				ItemStack out = new ItemStack(ModItems.FLUID_BOTTLE);
				IFluidHandlerItem fluidHandlerDummy = out
						.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
				int amount = fluidHandlerDummy.fill(auxiliary.getFluid(), true);
				out = fluidHandlerDummy.getContainer();
				if (out.hasTagCompound() && out.getTagCompound().hasKey(FluidHandlerItemStack.FLUID_NBT_KEY)) {
					NBTTagCompound fluidTag = out.getTagCompound().getCompoundTag(FluidHandlerItemStack.FLUID_NBT_KEY);
					if (!fluidTag.hasKey("Tag") && auxiliary.getFluid().tag != null) {
						fluidTag.setTag("Tag", auxiliary.getFluid().tag);
					}
				}
				if (amount > 0 && internalStackHandler.insertItem(5, out, true).isEmpty()) {
					auxiliary.drain(amount, true);
					internalStackHandler.getStackInSlot(2).shrink(1);
					internalStackHandler.insertItem(5, out, false);
					fluidChanged |= true;
				}
			}
		}
		if (fluidChanged) {
			brewTime = 0;
			if (recipe != null && !recipe.matches(input.getFluid(), auxiliary.getFluid())) {
				recipe = null;
			}
			getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
			this.markDirty();
		}
		if (recipe == null && input.getFluidAmount() > 0) {
			recipe = getRecipe();
		}
		if (recipe != null && brewTime % 20 == 0) {
			recipe = getRecipe();
		}
		if (recipe != null) {
			brewTime++;
			if (brewTime >= getMaxBrewTime()) {
				brew();
				brewTime = 0;
				recipe = null;
			}
		} else {
			brewTime = 0;
		}
	}

	private void brew() {
		brewTime = 0;
		int amount = output.getCapacity() - output.getFluidAmount();
		amount = Math.min(amount, input.getFluidAmount());
		if (amount > 0 && recipe.matches(input.getFluid(), auxiliary.getFluid()) && !world.isRemote) {
			FluidStack out = recipe.getResult(input.getFluid(), auxiliary.getFluid());
			if (output.getFluidAmount() > 0 && output.getFluid().getFluid() == out.getFluid()) {
				out = output.getFluid().copy();
			}
			out.amount = amount;
			if (output.fill(out, false) == amount && input.drain(amount, false).amount == amount) {
				output.fill(out, true);
				input.drain(amount, true);
				recipe = null;
			}
		}
		getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
		this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
		this.markDirty();
	}

	private BrewingBarrelRecipe getRecipe() {
		if (input.getFluidAmount() > 0) {
			for (BrewingBarrelRecipe recipe : Recipes.brewingRecipes) {
				if (recipe.matches(input.getFluid(), auxiliary.getFluid())) {
					if (output.getFluid() == null
							|| (recipe.getResult(input.getFluid(), auxiliary.getFluid()).getFluid() == output.getFluid()
									.getFluid() && output.getFluidAmount() < output.getCapacity())) {
						return recipe;
					}
				}
			}
		}
		return null;
	}

	public boolean isBrewing() {
		return brewTime > 0;
	}

	public int getBrewTime() {
		return brewTime;
	}

	public int getMaxBrewTime() {
		return Config.MAX_BREW_TIME;
	}

}
