package rustic.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustic.common.blocks.crops.BlockGrapeLeaves;

public abstract class BlockRopeBase extends BlockBase {

	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis",
			EnumFacing.Axis.class);
	public static final PropertyBool DANGLE = PropertyBool.create("dangle");

	protected static final AxisAlignedBB Y_AABB = new AxisAlignedBB(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.0F, 0.5625F);
	protected static final AxisAlignedBB X_AABB = new AxisAlignedBB(0.0, 0.4375F, 0.4375F, 1.0F, 0.5625F, 0.5625F);
	protected static final AxisAlignedBB Z_AABB = new AxisAlignedBB(0.4375F, 0.4375F, 0.0F, 0.5625F, 0.5625F, 1.0F);
	protected static final AxisAlignedBB X_DANGLE_AABB = new AxisAlignedBB(0.0, 0.0F, 0.4375F, 1.0F, 0.5625F, 0.5625F);
	protected static final AxisAlignedBB Z_DANGLE_AABB = new AxisAlignedBB(0.4375F, 0.0F, 0.0F, 0.5625F, 0.5625F, 1.0F);

	public BlockRopeBase(Material mat, String name, boolean register) {
		super(mat, name, register);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (!canPlaceBlockOnSide(world, pos.offset(side), side) && stack.getItem() == Item.getItemFromBlock(this)) {
			if (!this.isBlockSupported(world, pos, state)) {
				this.dropBlock(world, pos, state);
				return true;
			}
			
			int yOffset = 1;
			while (yOffset < 64 && world.getBlockState(pos.down(yOffset)).getBlock() == this) {
				if (world.getBlockState(pos.down(yOffset)).getValue(AXIS) != EnumFacing.Axis.Y) {
					return false;
				}
				yOffset++;
			}
			if (canPlaceBlockAt(world, pos.down(yOffset))) {
				world.setBlockState(pos.down(yOffset), state.withProperty(AXIS, EnumFacing.Axis.Y), 3);
				if (!player.capabilities.isCreativeMode) {
					player.getHeldItem(hand).shrink(1);
				}
				SoundType soundType = getSoundType(state, world, pos, player);
				world.playSound(pos.getX(), pos.getY() - yOffset, pos.getZ(), soundType.getPlaceSound(),
						SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F,
						false);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		IBlockState testState = world.getBlockState(pos.offset(side.getOpposite()));

		if (side == EnumFacing.UP) {
			return canPlaceBlockOnSide(world, pos, EnumFacing.DOWN);
		}
		
		boolean isThis = testState.getBlock() == this && testState.getValue(AXIS) == side.getAxis();
		boolean isSideSolid = world.isSideSolid(pos.offset(side.getOpposite()), side, false);

		return isThis || isSideSolid;
	}

	protected void dropBlock(World worldIn, BlockPos pos, IBlockState state) {
		this.dropBlockAsItem(worldIn, pos, state, 0);
		worldIn.setBlockToAir(pos);
		SoundType soundType = getSoundType(state, worldIn, pos, null);
		worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), soundType.getBreakSound(), SoundCategory.BLOCKS,
				(soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, true);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		EnumFacing dir = null;
		if (fromPos.getX() != pos.getX()) {
			dir = (fromPos.getX() - pos.getX()) < 0 ? EnumFacing.WEST : EnumFacing.EAST;
		} else if (fromPos.getY() != pos.getY()) {
			dir = (fromPos.getY() - pos.getY()) < 0 ? EnumFacing.DOWN : EnumFacing.UP;
		} else if (fromPos.getZ() != pos.getZ()) {
			dir = (fromPos.getZ() - pos.getZ()) < 0 ? EnumFacing.NORTH : EnumFacing.SOUTH;
		}
		if (dir != null) {
			// System.out.println(dir + ", " + this.isSideSupported(worldIn, pos, state, dir));
			if ((state.getValue(AXIS) == dir.getAxis())
					&& !this.isSideSupported(worldIn, pos, state, dir)) {
				if (dir == EnumFacing.UP) {
					this.dropBlock(worldIn, pos, state);
				} else if (!this.isBlockSupported(worldIn, pos, state)) {
					this.dropBlock(worldIn, pos, state);
				}
			}
		}
	}

	public boolean isSideSupported(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
		IBlockState testState = world.getBlockState(pos.offset(facing));

		if (facing == EnumFacing.DOWN) {
			return false;
		}

		boolean isSame = testState.getBlock() == state.getBlock()
				&& ((state.getValue(AXIS) == EnumFacing.Axis.Y && facing.getAxis() == EnumFacing.Axis.Y)
						|| testState.getValue(AXIS) == state.getValue(AXIS));
		boolean isSideSolid = world.isSideSolid(pos.offset(facing), facing.getOpposite(), false);

		return isSame || isSideSolid;
	}

	public boolean isBlockSupported(World world, BlockPos pos, IBlockState state) {
		if (state.getValue(AXIS) == EnumFacing.Axis.X) {
			return this.isSideSupported(world, pos, state, EnumFacing.WEST)
					&& this.isSideSupported(world, pos, state, EnumFacing.EAST);
		} else if (state.getValue(AXIS) == EnumFacing.Axis.Y) {
			return this.isSideSupported(world, pos, state, EnumFacing.UP);
		} else if (state.getValue(AXIS) == EnumFacing.Axis.Z) {
			return this.isSideSupported(world, pos, state, EnumFacing.NORTH)
					&& this.isSideSupported(world, pos, state, EnumFacing.SOUTH);
		}
		return false;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { AXIS, DANGLE });
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Y;
		//boolean supported = false;
		int i = meta & 3;

		if (i == 0) {
			enumfacing$axis = EnumFacing.Axis.Y;
		} else if (i == 1) {
			enumfacing$axis = EnumFacing.Axis.X;
		} else if (i == 2) {
			enumfacing$axis = EnumFacing.Axis.Z;
		}

		return this.getDefaultState().withProperty(AXIS, enumfacing$axis);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		EnumFacing.Axis enumfacing$axis = (EnumFacing.Axis) state.getValue(AXIS);

		if (enumfacing$axis == EnumFacing.Axis.X) {
			i = 1;
		} else if (enumfacing$axis == EnumFacing.Axis.Z) {
			i = 2;
		}

		return i;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(AXIS) != EnumFacing.Axis.Y && world.getBlockState(pos.down()).getBlock() instanceof BlockRopeBase && !(world.getBlockState(pos.down()).getBlock() instanceof BlockGrapeLeaves)
				&& world.getBlockState(pos.down()).getValue(BlockRope.AXIS) == EnumFacing.Axis.Y) {
			return state.withProperty(DANGLE, true);
		}
		return state.withProperty(DANGLE, false);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(AXIS,
				facing.getAxis());
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		switch (rot) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:

			switch ((EnumFacing.Axis) state.getValue(AXIS)) {
			case X:
				return state.withProperty(AXIS, EnumFacing.Axis.Z);
			case Z:
				return state.withProperty(AXIS, EnumFacing.Axis.X);
			default:
				return state;
			}

		default:
			return state;
		}
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = this.getActualState(state, source, pos);
		switch (state.getValue(AXIS)) {
		case Y:
			return Y_AABB;
		case X:
			if (state.getValue(DANGLE)) {
				return X_DANGLE_AABB;
			}
			return X_AABB;
		case Z:
			if (state.getValue(DANGLE)) {
				return Z_DANGLE_AABB;
			}
			return Z_AABB;
		}
		return Y_AABB;
	}
	
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

}
