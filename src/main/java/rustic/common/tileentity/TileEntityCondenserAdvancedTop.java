package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TileEntityCondenserAdvancedTop extends TileEntity {
	
	@Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
		TileEntity tile = world.getTileEntity(pos.down());
		if (tile instanceof TileEntityCondenserAdvancedBottom) {
			return ((TileEntityCondenserAdvancedBottom) tile).hasCapability(capability, facing);
		}
		return false;
	}
	
	@Override
    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
		TileEntity tile = world.getTileEntity(pos.down());
		if (tile instanceof TileEntityCondenserAdvancedBottom) {
			return ((TileEntityCondenserAdvancedBottom) tile).getCapability(capability, facing);
		}
		return null;
	}
	
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		this.invalidate();
		world.setTileEntity(pos, null);
	}

}
