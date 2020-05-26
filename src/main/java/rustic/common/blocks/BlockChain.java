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
		if (facing == EnumFacing.DOWN) {
			return false; // A surface below a chain can never be used as support
		}
		
		IBlockState otherState = world.getBlockState(pos.offset(facing));
		
		if (otherState.getBlock() == state.getBlock() && ((state.getValue(AXIS) == EnumFacing.Axis.Y && facing.getAxis() == EnumFacing.Axis.Y) || otherState.getValue(AXIS) == state.getValue(AXIS))) {
			return true;//Is Same
		}
		
		if (otherState.getBlock() instanceof BlockLattice) {
			return true;//Lattice blocks always support
		}
		
		BlockFaceShape faceShape = otherState.getBlockFaceShape(world, pos.offset(facing), facing.getOpposite());
		
		return 	faceShape == BlockFaceShape.SOLID || // A Full Face
				faceShape == BlockFaceShape.CENTER || // Railing(Cathedral Mod) (bottom and top)
				faceShape == BlockFaceShape.CENTER_BIG || // Walls, Vases(bottom and top)
				faceShape == BlockFaceShape.CENTER_SMALL; // Fences(bottom and top)
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		BlockPos otherPos = pos.offset(side.getOpposite());
		IBlockState otherState = world.getBlockState(otherPos);
		
		if (side == EnumFacing.UP) { // When attempting to place chain on ground see if there's a block above it to hang from instead.
			return canPlaceBlockOnSide(world, pos, EnumFacing.DOWN);
		}
		
		if (otherState.getBlock() == this && otherState.getValue(AXIS) == side.getAxis()) {
			return true; // Is this
		}
		
		if (otherState.getBlock() instanceof BlockLattice) {
			return true; // Always connect to Lattice blocks
		}
		
		BlockFaceShape faceShape = otherState.getBlockFaceShape(world, otherPos, side);
		
		return 	faceShape == BlockFaceShape.SOLID || // A Full Face
				faceShape == BlockFaceShape.CENTER || // Railing (Cathedral Mod) (bottom and top)
				faceShape == BlockFaceShape.CENTER_BIG || // Walls, Vases (bottom and top)
				faceShape == BlockFaceShape.CENTER_SMALL; // Fences(bottom and top)
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
