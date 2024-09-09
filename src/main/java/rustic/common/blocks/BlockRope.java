package rustic.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustic.common.blocks.crops.BlockGrapeLeaves;

public class BlockRope extends BlockRopeBase {

	public static class MaterialRope extends Material {
		public MaterialRope() {
			this(MapColor.CLOTH);
		}
		public MaterialRope(MapColor mapColor) {
			super(mapColor);
			this.setBurning();
			this.setNoPushMobility();
		}
	}
	
	public static final Material MATERIAL_ROPE = new MaterialRope();
	
	public BlockRope(String name) {
		super(MATERIAL_ROPE, name, true);
		setHardness(0.5F);
		setSoundType(SoundType.CLOTH);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));

		Blocks.FIRE.setFireInfo(this, 20, 60);
	}

	@Override
	public boolean isSideSupported(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
		IBlockState testState = world.getBlockState(pos.offset(facing));

		if (facing == EnumFacing.DOWN) {
			return false;
		}

		boolean isSame = testState.getBlock() == state.getBlock() && (testState.getValue(AXIS) == state.getValue(AXIS)
				|| (state.getValue(AXIS) == EnumFacing.Axis.Y && facing.getAxis() == EnumFacing.Axis.Y));
		boolean isSideSolid = world.isSideSolid(pos.offset(facing), facing.getOpposite(), false);
		boolean isTiedStake = testState.getBlock() == ModBlocks.STAKE_TIED;
		boolean isGrapeLeaves = testState.getBlock() == ModBlocks.GRAPE_LEAVES
				&& testState.getValue(BlockGrapeLeaves.AXIS) == state.getValue(AXIS);
		boolean isLattice = testState.getBlock() instanceof BlockLattice;

		return isSame || isSideSolid || isTiedStake || isGrapeLeaves || isLattice;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		IBlockState testState = world.getBlockState(pos.offset(side.getOpposite()));

		if (side == EnumFacing.UP) {
			return canPlaceBlockOnSide(world, pos, EnumFacing.DOWN);
		}

		boolean isThis = testState.getBlock() == this && testState.getValue(AXIS) == side.getAxis();
		boolean isSideSolid = world.isSideSolid(pos.offset(side.getOpposite()), side, false);
		boolean isTiedStake = testState.getBlock() == ModBlocks.STAKE_TIED;
		boolean isGrapeLeaves = testState.getBlock() == ModBlocks.GRAPE_LEAVES
				&& testState.getValue(BlockGrapeLeaves.AXIS) == side.getAxis();
		boolean isLattice = testState.getBlock() instanceof BlockLattice;

		return isThis || isSideSolid || isTiedStake || isGrapeLeaves || isLattice;
	}

	@Override
	public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
		if (!worldIn.isRemote) {
			if (entityIn instanceof EntityArrow && isArrowInAABB(worldIn, pos, state, (EntityArrow) entityIn)) {
				this.dropBlock(worldIn, pos, state);
			}
		}
	}

	protected boolean isArrowInAABB(World worldIn, BlockPos pos, IBlockState state, EntityArrow entity) {
		double xExp = (state.getValue(AXIS) == EnumFacing.Axis.X) ? 0 : 0.125;
		double yExp = (state.getValue(AXIS) == EnumFacing.Axis.Y) ? 0 : 0.125;
		double zExp = (state.getValue(AXIS) == EnumFacing.Axis.Z) ? 0 : 0.125;

		AxisAlignedBB aabb = this.getBoundingBox(state, worldIn, pos);
		if (aabb != null) {
			aabb = aabb.expand(xExp, yExp, zExp).offset(pos);

			if (entity.getEntityBoundingBox() != null && aabb.intersects(entity.getEntityBoundingBox())) {
				return true;
			}
		}
		return false;
	}

	//@Deprecated
	//public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
	//		List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
	//	if (entityIn != null && (!(entityIn instanceof EntityArrow) || true)) {
	//		addCollisionBoxToList(pos, entityBox, collidingBoxes, getBoundingBox(state, worldIn, pos));
	//	}
	//}
		
	
	//@Override
	//public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
	//	return null;
	//}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		if (state.getValue(AXIS) == side.getAxis()) {
			return BlockFaceShape.CENTER_SMALL;
		}
		return BlockFaceShape.UNDEFINED;
	}

}
