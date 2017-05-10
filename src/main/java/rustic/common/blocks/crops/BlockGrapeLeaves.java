package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
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
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.BlockBase;
import rustic.common.blocks.BlockRope;
import rustic.common.blocks.IColoredBlock;
import rustic.common.blocks.ModBlocks;
import rustic.common.items.ModItems;
import rustic.core.ClientProxy;
import rustic.core.Rustic;

public class BlockGrapeLeaves extends BlockBase implements IGrowable, IColoredBlock {

	public static final PropertyBool GRAPES = PropertyBool.create("grapes");
	public static final PropertyInteger DIST = PropertyInteger.create("distance", 0, 1);
	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis",
			EnumFacing.Axis.class, new Predicate<EnumFacing.Axis>() {
				public boolean apply(@Nullable EnumFacing.Axis p_apply_1_) {
					return p_apply_1_ != EnumFacing.Axis.Y;
				}
			});
	public static final PropertyBool DANGLE = PropertyBool.create("dangle");
	public static final PropertyBool SUPPORTED = PropertyBool.create("supported");

	public static final AxisAlignedBB BRANCH_Z_AABB = new AxisAlignedBB(0.1875F, 0.1875F, 0.0F, 0.8125F, 0.8125F, 1.0F);
	public static final AxisAlignedBB BRANCH_X_AABB = new AxisAlignedBB(0.0F, 0.1875F, 0.1875F, 1.0F, 0.8125F, 0.8125F);

	public BlockGrapeLeaves() {
		super(Material.LEAVES, "grape_leaves", false);
		GameRegistry.register(this);
		this.setTickRandomly(true);
		setCreativeTab(Rustic.farmingTab);
		setSoundType(SoundType.PLANT);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.X)
				.withProperty(DANGLE, false).withProperty(GRAPES, false).withProperty(DIST, 0));
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
		boolean supported = state.getValue(SUPPORTED);
		super.breakBlock(world, pos, state);
		world.setBlockState(pos, ModBlocks.ROPE.getDefaultState().withProperty(BlockRope.AXIS, axis)
				.withProperty(BlockRope.SUPPORTED, supported), 3);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state.withProperty(AXIS, EnumFacing.Axis.Y).withProperty(SUPPORTED, false)
				.withProperty(GRAPES, false).withProperty(DIST, 0));
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
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		boolean supported = isSupported(state, worldIn, pos);
		if (supported != state.getValue(SUPPORTED)) {
			worldIn.setBlockState(pos, state.withProperty(SUPPORTED, supported), 3);
		}
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			SoundType soundType = getSoundType(state, worldIn, pos, null);
			worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), soundType.getBreakSound(), SoundCategory.BLOCKS,
					(soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, true);
		}
	}

	@SuppressWarnings("incomplete-switch")
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		boolean canStay = state.getValue(SUPPORTED);
		if (!canStay) {
			boolean flag = false;
			boolean flag1 = false;
			if (state.getValue(AXIS) == EnumFacing.Axis.Z) {
				IBlockState state1 = world.getBlockState(pos.north());
				if (state1.getBlock() == ModBlocks.ROPE && state1.getValue(BlockRope.AXIS) == EnumFacing.Axis.Z) {
					flag = true;
				} else if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(AXIS) == EnumFacing.Axis.Z) {
					flag = true;
				}
				state1 = world.getBlockState(pos.south());
				if (state1.getBlock() == ModBlocks.ROPE && state1.getValue(BlockRope.AXIS) == EnumFacing.Axis.Z) {
					flag1 = true;
				} else if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(AXIS) == EnumFacing.Axis.Z) {
					flag1 = true;
				}
				canStay = flag && flag1;
			} else if (state.getValue(AXIS) == EnumFacing.Axis.X) {
				IBlockState state1 = world.getBlockState(pos.west());
				if (state1.getBlock() == ModBlocks.ROPE && state1.getValue(BlockRope.AXIS) == EnumFacing.Axis.X) {
					flag = true;
				} else if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(AXIS) == EnumFacing.Axis.X) {
					flag = true;
				}
				state1 = world.getBlockState(pos.east());
				if (state1.getBlock() == ModBlocks.ROPE && state1.getValue(BlockRope.AXIS) == EnumFacing.Axis.X) {
					flag1 = true;
				} else if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(AXIS) == EnumFacing.Axis.X) {
					flag1 = true;
				}
				canStay = flag && flag1;
			}
		}
		if (state.getValue(DIST) == 1) {
			switch (state.getValue(AXIS)) {
			case X:
				return canStay && ((world.getBlockState(pos.west()).getBlock() == ModBlocks.GRAPE_LEAVES
						&& world.getBlockState(pos.west()).getValue(DIST) == 0)
						|| (world.getBlockState(pos.east()).getBlock() == ModBlocks.GRAPE_LEAVES
								&& world.getBlockState(pos.east()).getValue(DIST) == 0));
			case Z:
				return canStay && ((world.getBlockState(pos.north()).getBlock() == ModBlocks.GRAPE_LEAVES
						&& world.getBlockState(pos.north()).getValue(DIST) == 0)
						|| (world.getBlockState(pos.south()).getBlock() == ModBlocks.GRAPE_LEAVES
								&& world.getBlockState(pos.south()).getValue(DIST) == 0));
			}
		}
		return canStay && world.getBlockState(pos.down()).getBlock() == ModBlocks.GRAPE_STEM
				&& world.getBlockState(pos.down()).getValue(BlockGrapeStem.AGE) > 2;
	}

	public boolean isSupported(IBlockState state, World world, BlockPos pos) {
		boolean supported = false;

		if (state.getValue(AXIS) == EnumFacing.Axis.Z) {
			if (world.isSideSolid(pos.north(), EnumFacing.SOUTH)
					|| world.getBlockState(pos.north()).getBlock() == ModBlocks.STAKE_TIED) {
				supported = true;
			}
			if (world.isSideSolid(pos.south(), EnumFacing.NORTH)
					|| world.getBlockState(pos.south()).getBlock() == ModBlocks.STAKE_TIED) {
				supported = true;
			}
		} else if (state.getValue(AXIS) == EnumFacing.Axis.X) {
			if (world.isSideSolid(pos.west(), EnumFacing.EAST)
					|| world.getBlockState(pos.west()).getBlock() == ModBlocks.STAKE_TIED) {
				supported = true;
			}
			if (world.isSideSolid(pos.east(), EnumFacing.WEST)
					|| world.getBlockState(pos.east()).getBlock() == ModBlocks.STAKE_TIED) {
				supported = true;
			}
		}

		return supported;
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
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		checkAndDropBlock(worldIn, pos, state);

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
		return 6F;
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
		boolean supported = world.getBlockState(newPos).getValue(BlockRope.SUPPORTED);
		world.setBlockState(newPos,
				getDefaultState().withProperty(AXIS, axis).withProperty(SUPPORTED, supported).withProperty(DIST, 1), 3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { AXIS, DANGLE, SUPPORTED, DIST, GRAPES });
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

		i = meta & 4;

		if (i > 0) {
			supported = true;
		}

		i = meta & 8;

		if (i > 0) {
			grapes = true;
		}

		return this.getDefaultState().withProperty(AXIS, enumfacing$axis).withProperty(DIST, dist)
				.withProperty(SUPPORTED, true).withProperty(GRAPES, grapes);
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

		if (state.getValue(SUPPORTED)) {
			i |= 4;
		}

		if (state.getValue(GRAPES)) {
			i |= 8;
		}

		return i;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (world.getBlockState(pos.down()).getBlock() instanceof BlockRope
				&& world.getBlockState(pos.down()).getValue(BlockRope.AXIS) == EnumFacing.Axis.Y) {
			return state.withProperty(DANGLE, true);
		}
		return state.withProperty(DANGLE, false);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, (new StateMap.Builder()).ignore(new IProperty[] { SUPPORTED }).build());
		ClientProxy.addColoredBlock(this);
	}

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

	@Override
	public IItemColor getItemColor() {
		return null;
	}

}
