package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustic.common.blocks.BlockCondenser;
import rustic.common.blocks.BlockCondenserAdvanced;
import rustic.common.blocks.ModBlocks;
import rustic.common.crafting.AdvancedCondenserRecipe;
import rustic.common.crafting.CondenserRecipe;
import rustic.common.crafting.Recipes;
import rustic.common.inventory.ExternalItemHandler;

public class TileEntityCondenserAdvanced extends TileFluidHandler implements ITickable {

	private int capacity = Fluid.BUCKET_VOLUME * 8;

	private ItemStackHandler internalStackHandler = new ItemStackHandler(7) {
		@Override
		protected void onContentsChanged(int slot) {
			IBlockState state = world.getBlockState(pos);
			TileEntityCondenserAdvanced.this.world.addBlockEvent(TileEntityCondenserAdvanced.this.pos,
					TileEntityCondenserAdvanced.this.getBlockType(), 1, 0);
			getWorld().notifyBlockUpdate(pos, state, state, 3);
			world.notifyNeighborsOfStateChange(TileEntityCondenserAdvanced.this.pos, TileEntityCondenserAdvanced.this.getBlockType(),
					true);
			TileEntityCondenserAdvanced.this.markDirty();
		}
	};

	private ExternalItemHandler externalStackHandler = new ExternalItemHandler(internalStackHandler) {

		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (slot == 4 && !TileEntityFurnace.isItemFuel(stack)) {
				return stack;
			}
			if (slot == 5 && stack.getItem() != Items.GLASS_BOTTLE) {
				return stack;
			}
			if (slot == 6) {
				return stack;
			}
			return super.insertItem(slot, stack, simulate);
		}

	};

	public int condenserBurnTime;
	public int currentItemBurnTime;
	public int brewTime;
	public int totalBrewTime;

	public TileEntityCondenserAdvanced() {
		super();
		tank = new FluidTank(capacity);
		tank.setTileEntity(this);
		tank.setCanFill(true);
		tank.setCanDrain(true);
	}

	@Override
	public void update() {
		
		if (this.isBurning()) {
			this.condenserBurnTime--;
		}
		if (this.totalBrewTime <= 0) {
			this.totalBrewTime = this.getBrewTime();
		}
		ItemStack fuelStack = (ItemStack) this.internalStackHandler.getStackInSlot(4);
		if (this.isBurning() || !fuelStack.isEmpty()) {
			if (!this.isBurning() && this.canBrew()) {
				this.condenserBurnTime = TileEntityFurnace.getItemBurnTime(fuelStack);
				this.currentItemBurnTime = this.condenserBurnTime;
				if (this.isBurning() && !fuelStack.isEmpty() && !world.isRemote) {
					Item item = fuelStack.getItem();
					this.internalStackHandler.getStackInSlot(4).shrink(1);
					if (fuelStack.isEmpty()) {
						ItemStack item1 = item.getContainerItem(fuelStack);
						this.internalStackHandler.setStackInSlot(4, item1);
					}
				}
			}
			if (this.isBurning() && this.canBrew()) {
				++this.brewTime;
				
				if (world.isRemote) {
					if (world.getBlockState(pos).getBlock() == ModBlocks.CONDENSER_ADVANCED) {
						EnumFacing blockFacing = world.getBlockState(pos).getValue(BlockCondenserAdvanced.FACING);
						switch (blockFacing) {
						case NORTH:
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() - 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 1.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 1.5D, 0, 0.05D, 0);
							break;
						case SOUTH:
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() - 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 1.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() - 0.5D, 0, 0.05D, 0);
							break;
						case WEST:
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() - 0.5D, 0, 0.05D, 0);
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 1.5D, 0, 0.05D, 0);
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 1.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
							break;
						case EAST:
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() - 0.5D, 0, 0.05D, 0);
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() + 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 1.5D, 0, 0.05D, 0);
							world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, true, this.pos.getX() - 0.5D, this.pos.getY() + 1.0625D, this.pos.getZ() + 0.5D, 0, 0.05D, 0);
							break;
						default:
							break;
						}
					}
				}

				if (this.brewTime >= this.totalBrewTime) {
					this.brewTime = 0;
					this.totalBrewTime = this.getBrewTime();
					if (!world.isRemote) {
						this.brew();
					}
				}
			} else {
				this.brewTime = 0;
			}
		} else if (!this.isBurning() && this.brewTime > 0) {
			this.brewTime = MathHelper.clamp(this.brewTime - 2, 0, this.totalBrewTime);
		}
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
			return (T) externalStackHandler;
		}
		return super.getCapability(capability, facing);
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
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (tag.hasKey("items")) {
			internalStackHandler.deserializeNBT((NBTTagCompound) tag.getTag("items"));
		}
		this.condenserBurnTime = tag.getInteger("BurnTime");
        this.brewTime = tag.getInteger("BrewTime");
        this.totalBrewTime = tag.getInteger("BrewTimeTotal");
        this.currentItemBurnTime = tag.getInteger("ItemBurnTime");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setTag("items", internalStackHandler.serializeNBT());
		tag.setInteger("BurnTime", (short)this.condenserBurnTime);
        tag.setInteger("BrewTime", (short)this.brewTime);
        tag.setInteger("BrewTimeTotal", (short)this.totalBrewTime);
        tag.setInteger("ItemBurnTime", this.currentItemBurnTime);
		return tag;
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

	public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (heldItem != ItemStack.EMPTY) {
			if ((heldItem.getItem() instanceof ItemBucket || heldItem.getItem() instanceof UniversalBucket)) {
				FluidActionResult didFill = FluidUtil.interactWithFluidHandler(heldItem,
						this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side), player);
				if (didFill.success) {
					player.setHeldItem(hand, didFill.getResult());
					this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
					getWorld().notifyBlockUpdate(pos, state, state, 3);
					this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
					this.markDirty();
					return true;
				}
			}
		} else if (player.isSneaking() && this.tank.getFluidAmount() > 0) {
			FluidStack drained = this.tank.drainInternal(capacity, true);
			SoundEvent soundevent = drained.getFluid().getEmptySound(drained);
			this.world.playSound(null, this.pos, soundevent, SoundCategory.BLOCKS, 1F, 1F);
			this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
			getWorld().notifyBlockUpdate(pos, state, state, 3);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
			markDirty();
			return true;
		}
		return false;
	}

	public ITextComponent getDisplayName() {
		return (ITextComponent) new TextComponentTranslation(this.getName(), new Object[0]);
	}

	public String getName() {
		return "container.rustic.condenser_advanced";
	}

	public int getCapacity() {
		return tank.getCapacity();
	}

	public int getAmount() {
		return tank.getFluidAmount();
	}

	public FluidTank getTank() {
		return tank;
	}

	public Fluid getFluid() {
		if (tank.getFluid() != null) {
			return tank.getFluid().getFluid();
		}
		return null;
	}

	public int getBrewTime() {
		return 300;
	}

	public boolean isBurning() {
		return this.condenserBurnTime > 0;
	}

	public boolean canBrew() {
		if (world.getBlockState(pos).getBlock() != ModBlocks.CONDENSER_ADVANCED
				|| !world.getBlockState(pos).getValue(BlockCondenserAdvanced.BOTTOM)) {
			return false;
		}

		if (!((BlockCondenserAdvanced) world.getBlockState(pos).getBlock()).hasRetorts(world, pos, world.getBlockState(pos))) {
			return false;
		}

		if (this.internalStackHandler.getStackInSlot(0).isEmpty()
				&& this.internalStackHandler.getStackInSlot(1).isEmpty()
				&& this.internalStackHandler.getStackInSlot(2).isEmpty()) {
			return false;
		}

		if (internalStackHandler.getStackInSlot(5).isEmpty()
				|| internalStackHandler.getStackInSlot(5).getItem() != Items.GLASS_BOTTLE
				|| internalStackHandler.getStackInSlot(5).getCount() < 1) {
			return false;
		}

		if (this.getAmount() <= 125) {
			return false;
		}

		if (this.getFluid() != FluidRegistry.WATER) {
			return false;
		}

		for (CondenserRecipe recipe : Recipes.condenserRecipes) {
			if (recipe.matches(internalStackHandler.getStackInSlot(3), new ItemStack[] { internalStackHandler.getStackInSlot(0), internalStackHandler.getStackInSlot(1), internalStackHandler.getStackInSlot(2)} )) {
				if (internalStackHandler.insertItem(6, recipe.getResult(), true).isEmpty()) {
					return true;
				}
			}
		}

		return false;
	}

	private void brew() {
		if (this.canBrew()) {
			for (CondenserRecipe recipe : Recipes.condenserRecipes) {
				if (recipe.matches(internalStackHandler.getStackInSlot(3), new ItemStack[] { internalStackHandler.getStackInSlot(0), internalStackHandler.getStackInSlot(1), internalStackHandler.getStackInSlot(2)} )) {

					internalStackHandler.insertItem(6, recipe.getResult(), false);
					internalStackHandler.extractItem(0, 1, false);
					internalStackHandler.extractItem(1, 1, false);
					internalStackHandler.extractItem(2, 1, false);
					internalStackHandler.extractItem(3, 1, false);

					internalStackHandler.extractItem(5, 1, false);
					tank.drain(125, true);

				}
			}
		}
	}
	
}
