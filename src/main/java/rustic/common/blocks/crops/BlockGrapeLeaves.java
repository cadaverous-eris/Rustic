package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.BlockBase;
import rustic.common.blocks.BlockRope;
import rustic.common.blocks.BlockRopeBase;
import rustic.common.blocks.IColoredBlock;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.properties.UnlistedPropertyBool;
import rustic.common.items.ModItems;
import rustic.core.ClientProxy;
import rustic.core.Rustic;

public class BlockGrapeLeaves extends BlockRopeBase implements IGrowable, IColoredBlock {

	public static final PropertyBool GRAPES = PropertyBool.create("grapes");
	public static final PropertyInteger DIST = PropertyInteger.create("distance", 0, 1);
	
	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis",
			EnumFacing.Axis.class, new Predicate<EnumFacing.Axis>() {
				public boolean apply(@Nullable EnumFacing.Axis p_apply_1_) {
					return p_apply_1_ != EnumFacing.Axis.Y;
				}
			});

	public static final AxisAlignedBB BRANCH_Z_AABB = new AxisAlignedBB(0.1875F, 0.1875F, 0.0F, 0.8125F, 0.8125F, 1.0F);
	public static final AxisAlignedBB BRANCH_X_AABB = new AxisAlignedBB(0.0F, 0.1875F, 0.1875F, 1.0F, 0.8125F, 0.8125F);

	public BlockGrapeLeaves() {
		super(Material.LEAVES, "grape_leaves", false);
		GameRegistry.findRegistry(Block.class).register(this);
		this.setTickRandomly(true);
		setCreativeTab(Rustic.farmingTab);
		setSoundType(SoundType.PLANT);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X).withProperty(GRAPES, false).withProperty(DIST, 0));
		
		Blocks.FIRE.setFireInfo(this, 30, 60);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		if (state.getValue(GRAPES)) {
			stacks.add(new ItemStack(ModItems.GRAPES));
		}
		return stacks;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		EnumFacing.Axis axis = state.getValue(AXIS);
		super.breakBlock(world, pos, state);
		world.setBlockState(pos, ModBlocks.ROPE.getDefaultState().withProperty(BlockRope.AXIS, axis), 3);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (state.getValue(GRAPES)) {
			world.setBlockState(pos, state.withProperty(GRAPES, false), 3);
			state.getBlock().spawnAsEntity(world, pos.offset(side),
					new ItemStack(ModItems.GRAPES, world.rand.nextInt(2) + 1));
			return true;
		}
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (state.getValue(DIST) < 1) {
			return Block.FULL_BLOCK_AABB;
		}
		if (state.getValue(AXIS) == EnumFacing.Axis.Z) {
			return BRANCH_Z_AABB;
		} else {
			return BRANCH_X_AABB;
		}
	}

	@Override
	public boolean causesSuffocation(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return false;
	}

	@Override
	public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
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
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		return new ItemStack(ModItems.GRAPES);
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
			if ((state.getValue(AXIS) == dir.getAxis())
					&& worldIn.isAirBlock(fromPos)) {
				if (dir == EnumFacing.DOWN) {
					this.dropBlock(worldIn, pos, state);
				} else if (!this.isBlockSupported(worldIn, pos, state)) {
					this.dropBlock(worldIn, pos, state);
				}
			}
		}
	}
	
	@Override
	public boolean isSideSupported(World world, BlockPos pos, IBlockState state, EnumFacing facing) {
		IBlockState testState = world.getBlockState(pos.offset(facing));
		
		boolean isSame = testState.getBlock() == state.getBlock() && (testState.getValue(AXIS) == state.getValue(AXIS));
		boolean isRope = testState.getBlock() == ModBlocks.ROPE && state.getValue(AXIS) == testState.getValue(BlockRope.AXIS);
		boolean isSideSolid = world.isSideSolid(pos.offset(facing), facing.getOpposite(), false);
		boolean isTiedStake = testState.getBlock() == ModBlocks.STAKE_TIED;
		boolean isLattice = testState.getBlock() == ModBlocks.IRON_LATTICE;
		
		return isSame || isRope || isSideSolid || isTiedStake || isLattice;
	}
	
	@Override
	public boolean isBlockSupported(World world, BlockPos pos, IBlockState state) {
		if (state.getValue(AXIS) == EnumFacing.Axis.X) {
			return this.isSideSupported(world, pos, state, EnumFacing.WEST) && this.isSideSupported(world, pos, state, EnumFacing.EAST);
		} else if (state.getValue(AXIS) == EnumFacing.Axis.Z) {
			return this.isSideSupported(world, pos, state, EnumFacing.NORTH) && this.isSideSupported(world, pos, state, EnumFacing.SOUTH);
		}
		return false;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		if (!this.isBlockSupported(worldIn, pos, state)) {
			this.dropBlock(worldIn, pos, state);
		}

		if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
			int i = state.getValue(DIST);

			if (i > 0 && !state.getValue(GRAPES) && worldIn.isAirBlock(pos.down())) {
				float f = getGrowthChance(this, worldIn, pos);

				if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state,
						rand.nextInt((int) (25.0F / f) + 1) == 0)) {
					worldIn.setBlockState(pos, state.withProperty(GRAPES, true), 3);
					net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state,
							worldIn.getBlockState(pos));
				}
			} else if (i < 1 && canSpread(worldIn, pos, state)) {
				float f = getGrowthChance(this, worldIn, pos);

				if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state,
						rand.nextInt((int) (25.0F / f) + 1) == 0)) {
					spread(worldIn, pos, state);
					net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state,
							worldIn.getBlockState(pos));
				}
			}
		}
	}

	protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos) {
		return 3F;
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		if (state.getValue(DIST) > 0) {
			return !state.getValue(GRAPES) && worldIn.isAirBlock(pos.down());
		}
		return canSpread(worldIn, pos, state);
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		if (state.getValue(DIST) > 0) {
			worldIn.setBlockState(pos, state.withProperty(GRAPES, true), 3);
		} else {
			spread(worldIn, pos, state);
		}
	}

	@SuppressWarnings("incomplete-switch")
	public boolean canSpread(World world, BlockPos pos, IBlockState state) {
		if (state.getValue(DIST) == 0) {
			switch (state.getValue(AXIS)) {
			case X:
				return (world.getBlockState(pos.west()).getBlock() == ModBlocks.ROPE
						&& world.getBlockState(pos.west()).getValue(BlockRope.AXIS) == state.getValue(AXIS))
						|| (world.getBlockState(pos.east()).getBlock() == ModBlocks.ROPE
								&& world.getBlockState(pos.east()).getValue(BlockRope.AXIS) == state.getValue(AXIS));
			case Z:
				return (world.getBlockState(pos.north()).getBlock() == ModBlocks.ROPE
						&& world.getBlockState(pos.north()).getValue(BlockRope.AXIS) == state.getValue(AXIS))
						|| (world.getBlockState(pos.south()).getBlock() == ModBlocks.ROPE
								&& world.getBlockState(pos.south()).getValue(BlockRope.AXIS) == state.getValue(AXIS));
			}
		}
		return false;
	}

	@SuppressWarnings("incomplete-switch")
	public void spread(World world, BlockPos pos, IBlockState state) {
		if (state.getValue(DIST) < 1) {
			switch (state.getValue(AXIS)) {
			case X:
				boolean westRope = world.getBlockState(pos.west()).getBlock() == ModBlocks.ROPE
						&& world.getBlockState(pos.west()).getValue(BlockRope.AXIS) == state.getValue(AXIS);
				boolean eastRope = world.getBlockState(pos.east()).getBlock() == ModBlocks.ROPE
						&& world.getBlockState(pos.east()).getValue(BlockRope.AXIS) == state.getValue(AXIS);
				if (westRope && eastRope) {
					spreadToValidRope(world, pos, (world.rand.nextFloat() < 0.5) ? pos.west() : pos.east(), state);
				} else if (westRope) {
					spreadToValidRope(world, pos, pos.west(), state);
				} else if (eastRope) {
					spreadToValidRope(world, pos, pos.east(), state);
				}
				break;
			case Z:
				boolean northRope = world.getBlockState(pos.north()).getBlock() == ModBlocks.ROPE
						&& world.getBlockState(pos.north()).getValue(BlockRope.AXIS) == state.getValue(AXIS);
				boolean southRope = world.getBlockState(pos.south()).getBlock() == ModBlocks.ROPE
						&& world.getBlockState(pos.south()).getValue(BlockRope.AXIS) == state.getValue(AXIS);
				if (northRope && southRope) {
					spreadToValidRope(world, pos, (world.rand.nextFloat() < 0.5) ? pos.north() : pos.south(), state);
				} else if (northRope) {
					spreadToValidRope(world, pos, pos.north(), state);
				} else if (southRope) {
					spreadToValidRope(world, pos, pos.south(), state);
				}
				break;
			}
		}
	}

	private void spreadToValidRope(World world, BlockPos origPos, BlockPos newPos, IBlockState state) {
		EnumFacing.Axis axis = world.getBlockState(newPos).getValue(BlockRope.AXIS);
		world.setBlockState(newPos,
				getDefaultState().withProperty(AXIS, axis).withProperty(DIST, 1), 3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { AXIS, DANGLE, DIST, GRAPES });
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (world.getBlockState(pos.down()).getBlock() instanceof BlockRope
				&& world.getBlockState(pos.down()).getValue(BlockRope.AXIS) == EnumFacing.Axis.Y) {
			return state.withProperty(DANGLE, true);
		}
		return state.withProperty(DANGLE, false);
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState();
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.X;
		boolean supported = false;
		int dist = 0;
		boolean grapes = false;
		int i = meta & 1;

		if (i == 0) {
			enumfacing$axis = EnumFacing.Axis.X;
		} else if (i == 1) {
			enumfacing$axis = EnumFacing.Axis.Z;
		}

		i = meta & 2;

		if (i > 0) {
			dist = 1;
		}

		i = meta & 8;

		if (i > 0) {
			grapes = true;
		}

		return this.getDefaultState().withProperty(AXIS, enumfacing$axis).withProperty(DIST, dist).withProperty(GRAPES, grapes);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		EnumFacing.Axis enumfacing$axis = (EnumFacing.Axis) state.getValue(AXIS);

		if (enumfacing$axis == EnumFacing.Axis.X) {
			i = 0;
		} else if (enumfacing$axis == EnumFacing.Axis.Z) {
			i = 1;
		}

		if (state.getValue(DIST) == 1) {
			i |= 2;
		}

		if (state.getValue(GRAPES)) {
			i |= 8;
		}

		return i;
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

	@SideOnly(Side.CLIENT)
	@Override
	public IBlockColor getBlockColor() {
		return new IBlockColor() {
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
				if (worldIn != null && pos != null) {
					return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
				}
				return ColorizerFoliage.getFoliageColorBasic();
			}
		};
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IItemColor getItemColor() {
		return new IItemColor() {
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				if (!(stack.getItem() instanceof ItemBlock)) return 0xFFFFFF;
				IBlockState state = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
				IBlockColor blockColor = ((IColoredBlock) state.getBlock()).getBlockColor();
				return blockColor == null ? 0xFFFFFF : blockColor.colorMultiplier(state, null, null, tintIndex);
			}
		};
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ClientProxy.addColoredBlock(this);
	}

}
