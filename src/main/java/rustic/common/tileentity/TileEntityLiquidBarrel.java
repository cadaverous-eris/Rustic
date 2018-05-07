package rustic.common.tileentity;

import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.TileFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.ModBlocks;
import rustic.common.items.ItemFluidBottle;

public class TileEntityLiquidBarrel extends TileFluidHandler {

	public static int capacity = Fluid.BUCKET_VOLUME * 16;
	public static final int MAX_TEMP = 573;

	public TileEntityLiquidBarrel() {
		super();
		tank = new FluidTank(capacity) {
			@Override
			protected void onContentsChanged() {
				markDirty();
				getWorld().notifyBlockUpdate(pos, getWorld().getBlockState(pos), getWorld().getBlockState(pos), 2);
			}
		};
		tank.setTileEntity(this);
		tank.setCanFill(true);
		tank.setCanDrain(true);
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

	public boolean activate(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = player.getHeldItem(hand);
		if (heldItem != ItemStack.EMPTY) {
			if (FluidUtil.getFluidHandler(heldItem) != null || heldItem.getItem() instanceof ItemBucket || heldItem.getItem() instanceof UniversalBucket) {
				FluidStack f = FluidUtil.getFluidContained(heldItem);
				if ((f != null && !f.getFluid().isGaseous() && !(f.getFluid().getTemperature() > MAX_TEMP)) || f == null) {
					boolean didFill = FluidUtil.interactWithFluidHandler(player, hand, this.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side));
					if (didFill) {
						this.world.addBlockEvent(this.pos, this.getBlockType(), 1, 0);
						getWorld().notifyBlockUpdate(pos, state, state, 3);
						this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
						this.markDirty();
					}
				}
				return true;
			}
		}
		return false;
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		this.invalidate();
		if (!world.isRemote && !player.capabilities.isCreativeMode) {
			ItemStack toDrop = new ItemStack(ModBlocks.LIQUID_BARREL, 1);
			if (getTank().getFluidAmount() > 0) {
				NBTTagCompound tag = new NBTTagCompound();
				getTank().writeToNBT(tag);
				toDrop.setTagCompound(tag);
			}
			world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, toDrop));
		}
		world.setTileEntity(pos, null);
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
	/*
	 * public boolean dirty = false;
	 * 
	 * @Override public void markForUpdate(){ dirty = true; }
	 * 
	 * @Override public boolean needsUpdate(){ return dirty; }
	 * 
	 * @Override public void clean(){ dirty = false; }
	 * 
	 * @Override public void markDirty(){ markForUpdate(); super.markDirty(); }
	 */

}
