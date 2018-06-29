package rustic.common.inventory;

import java.lang.ref.WeakReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Objects;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import rustic.common.blocks.BlockCabinet;
import rustic.common.tileentity.TileEntityCabinet;

public class DoubleCabinetItemHandler extends WeakReference<TileEntityCabinet> implements IItemHandlerModifiable {

	public static final DoubleCabinetItemHandler NO_ADJACENT_CABINETS_INSTANCE = new DoubleCabinetItemHandler(null,
			null, false);
	private final boolean mainCabinetIsUpper;
	private final TileEntityCabinet mainCabinet;
	private final int hashCode;

	public DoubleCabinetItemHandler(@Nullable TileEntityCabinet mainCabinet, @Nullable TileEntityCabinet other,
			boolean mainCabinetIsUpper) {
		super(other);
		this.mainCabinet = mainCabinet;
		this.mainCabinetIsUpper = mainCabinetIsUpper;
		hashCode = Objects.hashCode(mainCabinetIsUpper ? mainCabinet : other) * 31
				+ Objects.hashCode(!mainCabinetIsUpper ? mainCabinet : other);
	}

	@Nullable
	public static DoubleCabinetItemHandler get(TileEntityCabinet cabinet) {
		World world = cabinet.getWorld();
		BlockPos pos = cabinet.getPos();
		if (world == null || pos == null || !world.isBlockLoaded(pos))
			return null; // Still loading

		Block blockType = cabinet.getBlockType();

		if (world.getBlockState(pos).getValue(BlockCabinet.TOP)) {
			BlockPos blockpos = pos.down();
			Block block = world.getBlockState(blockpos).getBlock();

			if (block == blockType) {
				TileEntity otherTE = world.getTileEntity(blockpos);

				if (otherTE instanceof TileEntityCabinet) {
					TileEntityCabinet otherCabinet = (TileEntityCabinet) otherTE;
					return new DoubleCabinetItemHandler(cabinet, otherCabinet, true);
				}
			}
		} else if (world.getBlockState(pos).getBlock().getActualState(world.getBlockState(pos), world, pos)
				.getValue(BlockCabinet.BOTTOM)) {
			BlockPos blockpos = pos.up();
			Block block = world.getBlockState(blockpos).getBlock();

			if (block == blockType) {
				TileEntity otherTE = world.getTileEntity(blockpos);

				if (otherTE instanceof TileEntityCabinet) {
					TileEntityCabinet otherCabinet = (TileEntityCabinet) otherTE;
					return new DoubleCabinetItemHandler(cabinet, otherCabinet, false);
				}
			}
		}
		return NO_ADJACENT_CABINETS_INSTANCE; // All alone
	}

	@Nullable
	public TileEntityCabinet getCabinet(boolean accessingUpper) {
		if (accessingUpper == mainCabinetIsUpper)
			return mainCabinet;
		else {
			return getOtherCabinet();
		}
	}

	@Nullable
	public TileEntityCabinet getOtherCabinet() {
		TileEntityCabinet tileEntityCabinet = get();
		return tileEntityCabinet != null && !tileEntityCabinet.isInvalid() ? tileEntityCabinet : null;
	}

	@Override
	public int getSlots() {
		return 27 * 2;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		boolean accessingUpperCabinet = slot < 27;
		int targetSlot = accessingUpperCabinet ? slot : slot - 27;
		TileEntityCabinet cabinet = getCabinet(accessingUpperCabinet);
		return cabinet != null ? cabinet.getStackInSlot(targetSlot) : ItemStack.EMPTY;
	}

	@Override
	public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
		boolean accessingUpperCabinet = slot < 27;
		int targetSlot = accessingUpperCabinet ? slot : slot - 27;
		TileEntityCabinet cabinet = getCabinet(accessingUpperCabinet);
		if (cabinet != null) {
			cabinet.setInventorySlotContents(targetSlot, stack);
		}
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		boolean accessingUpperCabinet = slot < 27;
		int targetSlot = accessingUpperCabinet ? slot : slot - 27;
		TileEntityCabinet cabinet = getCabinet(accessingUpperCabinet);
		return cabinet != null ? cabinet.getSingleCabinetHandler().insertItem(targetSlot, stack, simulate) : stack;
	}

	@Override
	@Nonnull
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		boolean accessingUpperCabinet = slot < 27;
		int targetSlot = accessingUpperCabinet ? slot : slot - 27;
		TileEntityCabinet cabinet = getCabinet(accessingUpperCabinet);
		return cabinet != null ? cabinet.getSingleCabinetHandler().extractItem(targetSlot, amount, simulate) : ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		boolean accessingUpperCabinet = slot < 27;
		return getCabinet(accessingUpperCabinet).getInventoryStackLimit();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		DoubleCabinetItemHandler that = (DoubleCabinetItemHandler) o;

		if (hashCode != that.hashCode)
			return false;

		final TileEntityCabinet otherCabinet = getOtherCabinet();
		if (mainCabinetIsUpper == that.mainCabinetIsUpper)
			return Objects.equal(mainCabinet, that.mainCabinet) && Objects.equal(otherCabinet, that.getOtherCabinet());
		else
			return Objects.equal(mainCabinet, that.getOtherCabinet()) && Objects.equal(otherCabinet, that.mainCabinet);
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	public boolean needsRefresh() {
		if (this == NO_ADJACENT_CABINETS_INSTANCE)
			return false;
		TileEntityCabinet tileEntityCabinet = get();
		return tileEntityCabinet == null || tileEntityCabinet.isInvalid();
	}

}
