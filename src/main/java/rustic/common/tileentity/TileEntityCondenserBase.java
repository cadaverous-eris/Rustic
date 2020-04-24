package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.inventory.ExternalItemHandler;

public abstract class TileEntityCondenserBase extends TileFluidHandler implements ITickable {
	public static final int SLOT_RESULT = 0;
	public static final int SLOT_FUEL = 1;
	public static final int SLOT_BOTTLE = 2;
	public static final int SLOT_INGREDIENTS_START = 3;

	public static final int CAPACITY = Fluid.BUCKET_VOLUME * 8;
	
	protected abstract int getInternalSize();

	protected ItemStackHandler internalStackHandler = new ItemStackHandler(getInternalSize()) {
		@Override
		protected void onContentsChanged(int slot) {
			IBlockState state = world.getBlockState(pos);
			TileEntityCondenserBase.this.world.addBlockEvent(TileEntityCondenserBase.this.pos,
					TileEntityCondenserBase.this.getBlockType(), 1, 0);
			getWorld().notifyBlockUpdate(pos, state, state, 3);
			world.notifyNeighborsOfStateChange(TileEntityCondenserBase.this.pos, TileEntityCondenserBase.this.getBlockType(),
					true);
			TileEntityCondenserBase.this.markDirty();
			TileEntityCondenserBase.this.hasContentChanged = true;
		}
	};
	
	protected ExternalItemHandler externalStackHandler = new ExternalItemHandler(internalStackHandler) {
		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (slot == SLOT_FUEL && !TileEntityFurnace.isItemFuel(stack)) {
				return stack;
			}
			if (slot == SLOT_BOTTLE && TileEntityFurnace.isItemFuel(stack)) {
				return stack;
			}
			if (slot == SLOT_RESULT) {
				return stack;
			}
			return super.insertItem(slot, stack, simulate);
		}
	};

	protected ICondenserRecipe currentRecipe;
	protected boolean hasContentChanged;

	public int condenserBurnTime;
	public int currentItemBurnTime;
	public int brewTime;
	public int totalBrewTime;

	public TileEntityCondenserBase() {
		super();
		tank = new FluidTank(CAPACITY) {
			@Override
			protected void onContentsChanged() {
				markDirty();
				TileEntityCondenserBase.this.hasContentChanged = true;
			}
		};
		tank.setTileEntity(this);
		tank.setCanFill(true);
		tank.setCanDrain(true);
		this.currentRecipe = null;
		this.hasContentChanged = true;
	}
	
	public abstract String getName();
	
	public ITextComponent getDisplayName() {
		return (ITextComponent) new TextComponentTranslation(this.getName(), new Object[0]);
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

	@Override
	public void update() {
		boolean isBrewing = this.canBrew();
		boolean hasRecipe = isBrewing && this.hasValidRecipe();
		if (this.burnFuel(isBrewing && hasRecipe) && isBrewing && hasRecipe) {
			++this.brewTime;
			this.renderParticles();
			if (this.brewTime >= this.currentRecipe.getTime()) {
				this.brewTime = 0;
				if (!world.isRemote) {
					this.brew();
				}
			}
		} else if (this.brewTime > 0) {
			this.brewTime = MathHelper.clamp(this.brewTime - 2, 0, this.totalBrewTime);
		}
	}

	protected abstract boolean canBrew();

	protected boolean hasValidRecipe() {
		// short-cut recipe matching if there is no fuel or no retorts
		if (this.condenserBurnTime <= 0 && this.internalStackHandler.getStackInSlot(SLOT_FUEL).isEmpty()) {
			return false;
		}
		if (this.hasContentChanged) {
			this.refreshCurrentRecipe();
		}
		if (this.currentRecipe == null) {
			return false;
		}
		if (this.getAmount() < this.currentRecipe.getFluid().amount) {
			return false;
		}
		return this.internalStackHandler.insertItem(SLOT_RESULT, this.currentRecipe.getResult(), true).isEmpty();
	}
	
	protected abstract void refreshCurrentRecipe();
	
	private boolean burnFuel(boolean consumeNewFuel) {
		if (this.condenserBurnTime > 0) {
			--this.condenserBurnTime;
			return true;
		}
		ItemStack fuelStack = internalStackHandler.getStackInSlot(SLOT_FUEL);
		if (consumeNewFuel && !fuelStack.isEmpty()) {
			this.condenserBurnTime = TileEntityFurnace.getItemBurnTime(fuelStack);
			this.currentItemBurnTime = this.condenserBurnTime;
			if (!world.isRemote) {
				Item fuelItem = fuelStack.getItem();
				fuelStack.shrink(1);
				if (fuelStack.isEmpty()) {
					internalStackHandler.setStackInSlot(SLOT_FUEL, fuelItem.getContainerItem(fuelStack));
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean isBurning() {
		return this.condenserBurnTime > 0;
	}
	
	protected abstract void renderParticles();
	
	protected abstract void brew();
	

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
		this.hasContentChanged = true;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setTag("items", internalStackHandler.serializeNBT());
		tag.setInteger("BurnTime", (short) this.condenserBurnTime);
		tag.setInteger("BrewTime", (short) this.brewTime);
		tag.setInteger("BrewTimeTotal", (short) this.totalBrewTime);
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
					Block.spawnAsEntity(world, pos, internalStackHandler.getStackInSlot(i));
				}
			}
		}
		world.setTileEntity(pos, null);
	}
	
	public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (heldItem != ItemStack.EMPTY) {
			if ((FluidUtil.getFluidHandler(heldItem) != null || heldItem.getItem() instanceof ItemBucket || heldItem.getItem() instanceof UniversalBucket)) {
				boolean didFill = FluidUtil.interactWithFluidHandler(player, hand,
						this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side));
				if (didFill) {
					//player.setHeldItem(hand, didFill.getResult());
					this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
					getWorld().notifyBlockUpdate(pos, state, state, 3);
					this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
					this.markDirty();
					return true;
				}
			}
		} else if (player.isSneaking() && this.tank.getFluidAmount() > 0) {
			FluidStack drained = this.tank.drainInternal(CAPACITY, true);
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
}
