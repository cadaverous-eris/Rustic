package rustic.common.tileentity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustic.common.blocks.BlockCabinet;
import rustic.common.blocks.ModBlocks;
import rustic.common.inventory.DoubleCabinetItemHandler;
import rustic.common.util.ItemStackHandlerRustic;

public class TileEntityCabinet extends TileEntityLockableLoot implements ITickable {

	public float lidAngle = 0;
	public float prevLidAngle = 0;
	public int numPlayersUsing = 0;
	private int ticksSinceSync = 0;

	private ItemStackHandlerRustic itemStackHandler = new ItemStackHandlerRustic (27) {
		@Override
		protected void onContentsChanged(int slot) {
			TileEntityCabinet.this.markDirty();
		}
	};

	@SideOnly(Side.CLIENT)
	private AxisAlignedBB renderAABB;

	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		IBlockState state = world.getBlockState(pos);
		if (world.getBlockState(pos.up()).getBlock() == ModBlocks.CABINET && world.getBlockState(pos.up()).getPropertyKeys().contains(BlockCabinet.TOP) && world.getBlockState(pos.up()).getValue(BlockCabinet.TOP)) {
			return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1D, pos.getY() + 2D, pos.getZ() + 1D);
		}
		return new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1D, pos.getY() + 1D, pos.getZ() + 1D);
	}

	public void update() {

		int i = this.pos.getX();
		int j = this.pos.getY();
		int k = this.pos.getZ();
		++this.ticksSinceSync;

		if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
			
			int recordedUsers = numPlayersUsing;
			this.numPlayersUsing = 0;
			float f = 5.0F;

			for (EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class,
					new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F),
							(double) ((float) k - 5.0F), (double) ((float) (i + 1) + 5.0F),
							(double) ((float) (j + 1) + 5.0F), (double) ((float) (k + 1) + 5.0F)))) {
				if (entityplayer.openContainer instanceof ContainerCabinet) {
					TileEntityCabinet tec = ((ContainerCabinet) entityplayer.openContainer).getTile();

					if (tec.equals(this)) {
						++this.numPlayersUsing;
					}
				} else if (entityplayer.openContainer instanceof ContainerCabinetDouble) {

					TileEntityCabinet tec = ((ContainerCabinetDouble) entityplayer.openContainer).getTileBottom();

					if (tec.equals(this)) {
						++this.numPlayersUsing;
					}

				}
			}
			if (numPlayersUsing != recordedUsers) {
				markDirty();
			}
		}

		this.prevLidAngle = this.lidAngle;
		float f1 = 0.1F;

		if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
			this.world.playSound((EntityPlayer) null, i + 0.5D, j + 0.5D, k + 0.5D, SoundEvents.BLOCK_CHEST_OPEN,
					SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
		}

		if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
			float f2 = this.lidAngle;

			if (this.numPlayersUsing > 0) {
				this.lidAngle += 0.1F;
			} else {
				this.lidAngle -= 0.1F;
			}

			if (this.lidAngle > 1.0F) {
				this.lidAngle = 1.0F;
			}

			float f3 = 0.5F;

			if (this.lidAngle < 0.5F && f2 >= 0.5F) {
				this.world.playSound((EntityPlayer) null, i + 0.5D, j + 0.5D, k + 0.5D, SoundEvents.BLOCK_CHEST_CLOSE,
						SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
			}

			if (this.lidAngle < 0.0F) {
				this.lidAngle = 0.0F;
			}
		}
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.numPlayersUsing = type;
			return true;
		} else {
			return super.receiveClientEvent(id, type);
		}
	}

	@Override
	public void openInventory(EntityPlayer player) {
		if (!player.isSpectator()) {
			if (this.numPlayersUsing < 0) {
				this.numPlayersUsing = 0;
			}

			++this.numPlayersUsing;
			this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
			markDirty();
		}
	}

	public void closeInventory(EntityPlayer player) {
		if (!player.isSpectator() && this.getBlockType() instanceof BlockCabinet) {
			--this.numPlayersUsing;
			this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
			this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), false);
			markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (!this.checkLootAndRead(compound) && compound.hasKey("items")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
        }
        if (compound.hasKey("CustomName", 8)) {
        	this.setCustomName(compound.getString("CustomName"));
        }
		if (compound.hasKey("numUsers")) {
			numPlayersUsing = compound.getInteger("numUsers");
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (!this.checkLootAndWrite(compound)) {
        	compound.setTag("items", itemStackHandler.serializeNBT());
        }
		if (this.hasCustomName()) {
            compound.setString("CustomName", this.customName);
        }
		compound.setTag("numUsers", new NBTTagInt(numPlayersUsing));
		return compound;
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	public DoubleCabinetItemHandler doubleCabinetHandler;

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (!getWorld().isRemote) {
        		fillWithLoot((EntityPlayer) null);
        	}
			if (doubleCabinetHandler == null || doubleCabinetHandler.needsRefresh()) {
				doubleCabinetHandler = DoubleCabinetItemHandler.get(this);
			}
			if (doubleCabinetHandler != null
					&& doubleCabinetHandler != DoubleCabinetItemHandler.NO_ADJACENT_CABINETS_INSTANCE) {
				if (!getWorld().isRemote) {
	        		doubleCabinetHandler.getOtherCabinet().fillWithLoot((EntityPlayer) null);
	        	}
				return (T) doubleCabinetHandler;
			}
			return (T) itemStackHandler;
		}
		return super.getCapability(capability, facing);
	}

	public IItemHandler getSingleCabinetHandler() {
		return itemStackHandler;
	}

	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		doubleCabinetHandler = null;
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		this.invalidate();
		if (itemStackHandler != null && !world.isRemote) {
			this.fillWithLoot((EntityPlayer)null);
			for (int i = 0; i < itemStackHandler.getSlots(); i++) {
				if (itemStackHandler.getStackInSlot(i) != null) {
					state.getBlock().spawnAsEntity(world, pos, itemStackHandler.getStackInSlot(i));
				}
			}
		}
		world.setTileEntity(pos, null);
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState) {
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public int getSizeInventory() {
		return itemStackHandler.getSlots();
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < itemStackHandler.getSlots(); i++) {
			if (itemStackHandler.getStackInSlot(i) != ItemStack.EMPTY) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		this.fillWithLoot((EntityPlayer)null);
		return itemStackHandler.getStackInSlot(index);
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		this.fillWithLoot((EntityPlayer)null);
		return itemStackHandler.extractItem(index, count, false);
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		this.fillWithLoot((EntityPlayer)null);
		ItemStack stack = itemStackHandler.getStackInSlot(index);
		itemStackHandler.setStackInSlot(index, ItemStack.EMPTY);
		return stack;
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		this.fillWithLoot((EntityPlayer)null);
		itemStackHandler.setStackInSlot(index, stack);
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		this.fillWithLoot((EntityPlayer)null);
		for (int i = 0; i < itemStackHandler.getSlots(); i++) {
			itemStackHandler.setStackInSlot(i, ItemStack.EMPTY);
		}
	}

	public String getName() {
        return this.hasCustomName() ? this.customName : "container.rustic.cabinet";
    }

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		if (!world.isRemote) {
			this.fillWithLoot(playerIn);
		}
		if (getWorld().getBlockState(pos).getValue(BlockCabinet.TOP)) {
			if (!world.isRemote) {
				((TileEntityCabinet) world.getTileEntity(pos.down())).fillWithLoot(playerIn);
			}
			return new ContainerCabinetDouble(playerInventory, this,
					(TileEntityCabinet) getWorld().getTileEntity(pos.down()), playerIn);
		} else if (world.getBlockState(pos.up()).getBlock() instanceof BlockCabinet && world.getBlockState(pos.up()).getValue(BlockCabinet.TOP)) {
			if (!world.isRemote) {
				((TileEntityCabinet) world.getTileEntity(pos.up())).fillWithLoot(playerIn);
			}
			return new ContainerCabinetDouble(playerInventory, (TileEntityCabinet) getWorld().getTileEntity(pos.up()),
					this, playerIn);
		}
		return new ContainerCabinet(playerInventory, this, playerIn);
	}

	@Override
	public String getGuiID() {
		return "rustic:cabinet";
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
	
	/*
	public boolean dirty = false;
	
	public void markForUpdate(){
		dirty = true;
	}
	
	public boolean needsUpdate(){
		return dirty;
	}
	
	public void clean(){
		dirty = false;
	}
	
	@Override
	public void markDirty(){
		markForUpdate();
		super.markDirty();
	}
	*/

	@Override
	protected NonNullList<ItemStack> getItems() {
		return itemStackHandler.getStacks();
	}
	
	@Override
	public void setLootTable(ResourceLocation file, long seed) {
		super.setLootTable(file, seed);
		markDirty();
	}

}
