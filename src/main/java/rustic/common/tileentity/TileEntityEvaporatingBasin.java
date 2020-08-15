package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustic.common.crafting.IEvaporatingBasinRecipe;
import rustic.common.crafting.Recipes;

public class TileEntityEvaporatingBasin extends TileFluidHandler implements ITickable {

	private int capacity = Fluid.BUCKET_VOLUME * 6;

	private FluidStack evaporatedFluid;
	private int age = 0;
	private int remainder = 0;

	private ItemStackHandler itemStackHandler = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			IBlockState state = world.getBlockState(pos);
			TileEntityEvaporatingBasin.this.world.addBlockEvent(TileEntityEvaporatingBasin.this.pos,
					TileEntityEvaporatingBasin.this.getBlockType(), 1, 0);
			getWorld().notifyBlockUpdate(pos, state, state, 3);
			world.notifyNeighborsOfStateChange(TileEntityEvaporatingBasin.this.pos,
					TileEntityEvaporatingBasin.this.getBlockType(), true);
			TileEntityEvaporatingBasin.this.markDirty();
		}
	};

	public TileEntityEvaporatingBasin() {
		super();
		tank = new FluidTank(capacity) {
			public boolean canFillFluidType(FluidStack fluid)
		    {
		        return fluid != null && Recipes.evaporatingRecipesMap.containsKey(fluid.getFluid()) && canFill();
		    }
		};
		tank.setTileEntity(this);
		tank.setCanFill(true);
		tank.setCanDrain(true);
		this.evaporatedFluid = null;
	}

	@Override
	public void update() {
		age++;
		if (age >= 1000000) {
			age = 0;
		}
		if (this.age % 20 == 0) {
			if (this.getFluid() == null  || (this.evaporatedFluid != null && this.evaporatedFluid.getFluid() != this.getFluid())) {
				// change of recipe - reset
				this.evaporatedFluid = null;
				this.remainder = 0;
			}
			int to_drain = 0;
			IEvaporatingBasinRecipe recipe;
			if (this.getFluid() != null) {
				recipe = Recipes.evaporatingRecipesMap.get(this.getFluid());
				int temp = 20 * recipe.getAmount() + this.remainder;
				to_drain = temp / recipe.getTime();
				this.remainder = temp % recipe.getTime();
			}
			
			FluidStack drained = tank.drain(to_drain, true);
			
			if (drained != null) {
				if (evaporatedFluid == null) {
					evaporatedFluid = drained;
				} else {
					if (!evaporatedFluid.getFluid().equals(drained.getFluid())) {
						evaporatedFluid = drained;
					} else {
						evaporatedFluid.amount += drained.amount;
					}
				}
				getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 3);
				this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
				this.markDirty();
			}
			
			if (evaporatedFluid != null && evaporatedFluid.amount > 0) {
				recipe = Recipes.evaporatingRecipesMap.get(evaporatedFluid.getFluid());
				ItemStack result = recipe.getOutput();
				if (evaporatedFluid.amount >= recipe.getAmount() && itemStackHandler.insertItem(0, result, true).isEmpty()) {
					evaporatedFluid.amount -= recipe.getAmount();
					itemStackHandler.insertItem(0, result, false);
					getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos),
							3);
					this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
					this.markDirty();
				}
			}
		}
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
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
				|| capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
				|| super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return (T) tank;
		} else if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) itemStackHandler;
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
			itemStackHandler.deserializeNBT((NBTTagCompound) tag.getTag("items"));
		}
		if (tag.hasKey("EvaporatedFluid")) {
			this.evaporatedFluid = FluidStack.loadFluidStackFromNBT(tag.getCompoundTag("EvaporatedFluid"));
		}
		if (tag.hasKey("remainder")) {
			this.remainder = tag.getInteger("remainder");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setTag("items", itemStackHandler.serializeNBT());
		if (this.evaporatedFluid != null) {
			tag.setTag("EvaporatedFluid", this.evaporatedFluid.writeToNBT(new NBTTagCompound()));
		}
		tag.setInteger("remainder", this.remainder);
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
		if (itemStackHandler != null && !world.isRemote) {
			for (int i = 0; i < itemStackHandler.getSlots(); i++) {
				if (itemStackHandler.getStackInSlot(i) != null) {
					Block.spawnAsEntity(world, pos, itemStackHandler.getStackInSlot(i));
				}
			}
		}
		world.setTileEntity(pos, null);
	}

	public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (heldItem != ItemStack.EMPTY) {
			if (FluidUtil.getFluidHandler(heldItem) != null || heldItem.getItem() instanceof ItemBucket || heldItem.getItem() instanceof UniversalBucket) {
				boolean didFill = FluidUtil.interactWithFluidHandler(player, hand,
						this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side));
				if (didFill) {
					//player.setHeldItem(hand, didFill.getResult());
					this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
					getWorld().notifyBlockUpdate(pos, state, state, 3);
					this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
					this.markDirty();
				}
				return true; // prevents placing a bucket by clicking a basin with less than 1000mb space
			}
		} else if (player.isSneaking() && this.getAmount() > 0) {
			FluidStack drained = this.tank.drainInternal(capacity, true);
			SoundEvent soundevent = drained.getFluid().getEmptySound(drained);
			this.world.playSound(null, this.pos, soundevent, SoundCategory.BLOCKS, 1F, 1F);
			this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
			getWorld().notifyBlockUpdate(pos, state, state, 3);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
			markDirty();
			return true;
		}
		if (itemStackHandler.getStackInSlot(0) != ItemStack.EMPTY && !world.isRemote) {
			world.spawnEntity(
					new EntityItem(world, player.posX, player.posY, player.posZ, itemStackHandler.getStackInSlot(0)));
			itemStackHandler.setStackInSlot(0, ItemStack.EMPTY);
			this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
			getWorld().notifyBlockUpdate(pos, state, state, 3);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
			markDirty();
			return true;
		}
		return false;
	}

}
