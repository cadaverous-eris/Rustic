package rustic.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.core.Rustic;

public class BlockChain extends BlockRopeBase {

	public BlockChain() {
		this("chain");
	}
	
	public BlockChain(String name) {
		this(Material.IRON, name, true);
	}
	
	public BlockChain(Material mat, String name, boolean register) {
		super(mat, name, register);
		this.setHardness(1F);
		this.setCreativeTab(Rustic.decorTab);
		setSoundType(SoundType.METAL);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
	}
	
	@Override
	public boolean isSideSupported(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
		IBlockState testState = world.getBlockState(pos.offset(facing));
		
		if (facing == EnumFacing.DOWN) {
			return false;
		}
		
		boolean isSame = testState.getBlock() == state.getBlock() && ((state.getValue(AXIS) == EnumFacing.Axis.Y && facing.getAxis() == EnumFacing.Axis.Y) || testState.getValue(AXIS) == state.getValue(AXIS));
		boolean isSideSolid = world.isSideSolid(pos.offset(facing), facing.getOpposite(), false);
		boolean isLattice = testState.getBlock() instanceof BlockLattice;
		
		return isSame || isSideSolid || isLattice;
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		IBlockState testState = world.getBlockState(pos.offset(side.getOpposite()));
		
		if (side == EnumFacing.UP) {
			return canPlaceBlockOnSide(world, pos, EnumFacing.DOWN);
		}
		
		boolean isThis = testState.getBlock() == this && testState.getValue(AXIS) == side.getAxis();
		boolean isSideSolid = world.isSideSolid(pos.offset(side.getOpposite()), side, false);
		boolean isLattice = testState.getBlock() instanceof BlockLattice;
		
		return isThis || isSideSolid || isLattice;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		if (state.getValue(AXIS) == side.getAxis()) {
			return BlockFaceShape.CENTER_SMALL;
		}
		return BlockFaceShape.UNDEFINED;
	}

}
