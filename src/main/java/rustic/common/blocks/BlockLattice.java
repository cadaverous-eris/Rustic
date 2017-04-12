package rustic.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockLattice extends BlockBase {

	public static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(.375f, .375f, .375f, .625f, .625f, .625f);
	public static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(.4375f, .4375f, .4375f, .5625f, .5625f, 0f);
	public static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(.5625f, .4375f, .4375f, 1f, .5625f, .5625f);
	public static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(.4375f, .4375f, 1f, .5625f, .5625f, .5625f);
	public static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0f, .4375f, .4375f, .4375f, .5625f, .5625f);
	public static final AxisAlignedBB UP_AABB = new AxisAlignedBB(.4375f, .5625f, .4375f, .5625f, 1f, .5625f);
	public static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(.4375f, 0f, .4375f, .5625f, .4375f, .5625f);

	public static final PropertyBool N = PropertyBool.create("n");
	public static final PropertyBool E = PropertyBool.create("e");
	public static final PropertyBool S = PropertyBool.create("s");
	public static final PropertyBool W = PropertyBool.create("w");
	public static final PropertyBool U = PropertyBool.create("u");
	public static final PropertyBool D = PropertyBool.create("d");
	public static final PropertyBool LEAVES = PropertyBool.create("leaves");
	public static final PropertyBool LEAVES_NORTH = PropertyBool.create("leaves_north");
	public static final PropertyBool LEAVES_EAST = PropertyBool.create("leaves_east");
	public static final PropertyBool LEAVES_SOUTH = PropertyBool.create("leaves_south");
	public static final PropertyBool LEAVES_WEST = PropertyBool.create("leaves_west");
	public static final PropertyBool LEAVES_UP = PropertyBool.create("leaves_up");
	public static final PropertyBool LEAVES_DOWN = PropertyBool.create("leaves_down");

	public BlockLattice(Material mat, String name) {
		super(mat, name);
		setHardness(2.0F);
		setSoundType(SoundType.METAL);
	}

	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		if (!p_185477_7_) {
			state = state.getActualState(worldIn, pos);
		}

		addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);

		if (((Boolean) state.getValue(N)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
		}

		if (((Boolean) state.getValue(E)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
		}

		if (((Boolean) state.getValue(S)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
		}

		if (((Boolean) state.getValue(W)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
		}

		if (((Boolean) state.getValue(U)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, UP_AABB);
		}

		if (((Boolean) state.getValue(D)).booleanValue()) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, DOWN_AABB);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		double x1 = 0.375;
		double y1 = 0.375;
		double z1 = 0.375;
		double x2 = 0.625;
		double y2 = 0.625;
		double z2 = 0.625;
		
		state = state.getActualState(source, pos);

		if (((Boolean) state.getValue(U)).booleanValue()) {
			y2 = 1;
		}
		if (((Boolean) state.getValue(D)).booleanValue()) {
			y1 = 0;
		}
		if (((Boolean) state.getValue(N)).booleanValue()) {
			z1 = 0;
		}
		if (((Boolean) state.getValue(S)).booleanValue()) {
			z2 = 1;
		}
		if (((Boolean) state.getValue(W)).booleanValue()) {
			x1 = 0;
		}
		if (((Boolean) state.getValue(E)).booleanValue()) {
			x2 = 1;
		}

		return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if (heldItem.getItem().equals(Item.getItemFromBlock(Blocks.LEAVES))
				|| heldItem.getItem().equals(Item.getItemFromBlock(Blocks.LEAVES2))) {
			worldIn.setBlockState(pos, state.withProperty(LEAVES, true));
			playerIn.getHeldItem(hand).shrink(1);
			return true;
		}
		return false;
	}

	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	public int getMetaFromState(IBlockState state) {
		return (state.getValue(LEAVES) ? 1 : 0);
	}

	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState();
		if (meta == 0) {
			iblockstate = iblockstate.withProperty(LEAVES, false);
		} else {
			iblockstate = iblockstate.withProperty(LEAVES, true);
		}
		return iblockstate;
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		IBlockState stateTemp = worldIn.getBlockState(pos.north());
		Block blockTemp = stateTemp.getBlock();
		boolean nSolid = blockTemp.isSideSolid(stateTemp, worldIn, pos, EnumFacing.SOUTH)
				|| blockTemp instanceof BlockLattice;
		stateTemp = worldIn.getBlockState(pos.east());
		blockTemp = stateTemp.getBlock();
		boolean eSolid = blockTemp.isSideSolid(stateTemp, worldIn, pos, EnumFacing.WEST)
				|| blockTemp instanceof BlockLattice;
		stateTemp = worldIn.getBlockState(pos.south());
		blockTemp = stateTemp.getBlock();
		boolean sSolid = blockTemp.isSideSolid(stateTemp, worldIn, pos, EnumFacing.NORTH)
				|| blockTemp instanceof BlockLattice;
		stateTemp = worldIn.getBlockState(pos.west());
		blockTemp = stateTemp.getBlock();
		boolean wSolid = blockTemp.isSideSolid(stateTemp, worldIn, pos, EnumFacing.EAST)
				|| blockTemp instanceof BlockLattice;
		stateTemp = worldIn.getBlockState(pos.up());
		blockTemp = stateTemp.getBlock();
		boolean uSolid = blockTemp.isSideSolid(stateTemp, worldIn, pos, EnumFacing.DOWN)
				|| blockTemp instanceof BlockLattice || blockTemp instanceof BlockChain || blockTemp instanceof BlockLantern;
		stateTemp = worldIn.getBlockState(pos.down());
		blockTemp = stateTemp.getBlock();
		boolean dSolid = blockTemp.isSideSolid(stateTemp, worldIn, pos, EnumFacing.UP)
				|| blockTemp instanceof BlockLattice || blockTemp instanceof BlockChain || blockTemp instanceof BlockLantern;
		boolean leaves = state.getValue(LEAVES);

		return state.withProperty(N, nSolid).withProperty(LEAVES_NORTH, nSolid && leaves).withProperty(E, eSolid)
				.withProperty(LEAVES_EAST, eSolid && leaves).withProperty(S, sSolid)
				.withProperty(LEAVES_SOUTH, sSolid && leaves).withProperty(W, wSolid)
				.withProperty(LEAVES_WEST, wSolid && leaves).withProperty(U, uSolid)
				.withProperty(LEAVES_UP, uSolid && leaves).withProperty(D, dSolid)
				.withProperty(LEAVES_DOWN, dSolid && leaves);
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { N, E, S, W, U, D, LEAVES, LEAVES_NORTH, LEAVES_EAST,
				LEAVES_SOUTH, LEAVES_WEST, LEAVES_UP, LEAVES_DOWN });
	}

	public IBlockState withRotation(IBlockState state, Rotation rot) {
		switch (rot) {
		case CLOCKWISE_180:
			return state.withProperty(N, state.getValue(S)).withProperty(E, state.getValue(W))
					.withProperty(S, state.getValue(N)).withProperty(W, state.getValue(E));
		case COUNTERCLOCKWISE_90:
			return state.withProperty(N, state.getValue(E)).withProperty(E, state.getValue(S))
					.withProperty(S, state.getValue(W)).withProperty(W, state.getValue(N));
		case CLOCKWISE_90:
			return state.withProperty(N, state.getValue(W)).withProperty(E, state.getValue(N))
					.withProperty(S, state.getValue(E)).withProperty(W, state.getValue(S));
		default:
			return state;
		}
	}

	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		switch (mirrorIn) {
		case LEFT_RIGHT:
			return state.withProperty(N, state.getValue(S)).withProperty(S, state.getValue(N));
		case FRONT_BACK:
			return state.withProperty(E, state.getValue(W)).withProperty(W, state.getValue(E));
		default:
			return super.withMirror(state, mirrorIn);
		}
	}

}
