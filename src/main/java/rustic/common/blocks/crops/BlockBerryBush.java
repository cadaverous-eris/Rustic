package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.BlockBase;
import rustic.common.blocks.BlockPlanksRustic;
import rustic.common.blocks.BlockRope;
import rustic.common.blocks.ModBlocks;
import rustic.core.Rustic;

public abstract class BlockBerryBush extends BlockBase implements IPlantable, IGrowable {

	public static final PropertyBool BERRIES = PropertyBool.create("berries");

	protected static final AxisAlignedBB BUSH_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1D, 0.875D);

	public BlockBerryBush(String name) {
		super(Material.PLANTS, name);
		setCreativeTab(Rustic.farmingTab);
		setSoundType(SoundType.PLANT);
		setHardness(0.2F);
		setLightOpacity(1);
		setTickRandomly(true);
		setDefaultState(this.blockState.getBaseState().withProperty(BERRIES, false));
	}

	public abstract Item getBerries();

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);

		if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {

			if (!state.getValue(BERRIES)) {
				float f = getGrowthChance(this, worldIn, pos);

				if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state,
						rand.nextInt((int) (25.0F / f) + 1) == 0)) {
					worldIn.setBlockState(pos, state.withProperty(BERRIES, true), 3);
					net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state,
							worldIn.getBlockState(pos));
				}
			} else {
				float f = getGrowthChance(this, worldIn, pos);

				if (numNeighbors(worldIn, pos) < 3 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos,
						state, rand.nextInt((int) (25.0F / f) + 1) == 0)) {
					growOutward(worldIn, pos, rand);
					net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state,
							worldIn.getBlockState(pos));
				}
			}
		}
	}

	protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos) {
		return 3F;
	}

	protected int numNeighbors(World world, BlockPos pos) {
		int n = 0;

		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				if (world.getBlockState(pos.add(i, 0, j)).getBlock() == this) {
					n++;
				}
			}
		}

		return n;
	}

	protected void growOutward(World world, BlockPos pos, Random rand) {
		List<BlockPos> positions = new ArrayList<BlockPos>();
		for (int i = -1; i <= 1; i++) {
			for (int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) {
					continue;
				}
				if (canPlaceBlockAt(world, pos.add(i, 0, j))) {
					positions.add(pos.add(i, 0, j));
				}
			}
		}
		if (positions.size() > 0) {
			BlockPos placePos = positions.get(rand.nextInt(positions.size()));
			world.setBlockState(placePos, getDefaultState(), 3);
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> stacks = super.getDrops(world, pos, state, fortune);
		if (state.getValue(BERRIES)) {
			if (getBerries() != null) {
				stacks.add(new ItemStack(getBerries()));
			}
		}
		return stacks;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (state.getValue(BERRIES)) {
			world.setBlockState(pos, state.withProperty(BERRIES, false), 3);
			state.getBlock().spawnAsEntity(world, pos.offset(side),
					new ItemStack(getBerries(), world.rand.nextInt(3) + 1));
			return true;
		}
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BUSH_AABB;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Plains;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return this.getDefaultState();
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
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		return super.canPlaceBlockAt(worldIn, pos)
				&& soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
	}

	protected boolean canSustainBush(IBlockState state) {
		return state.getBlock() == Blocks.GRASS || state.getBlock() == Blocks.DIRT
				|| state.getBlock() == Blocks.FARMLAND || state.getBlock() == ModBlocks.FERTILE_SOIL;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
		}
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		if (state.getBlock() == this) {
			IBlockState soil = worldIn.getBlockState(pos.down());
			return soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this);
		}
		return this.canSustainBush(worldIn.getBlockState(pos.down()));
	}

	@Override
	public int damageDropped(IBlockState state) {
		return getMetaFromState(getDefaultState().withProperty(BERRIES, false));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(BERRIES, meta > 0 ? true : false);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(BERRIES) ? 1 : 0;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { BERRIES });
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return !state.getValue(BERRIES);
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		worldIn.setBlockState(pos, state.withProperty(BERRIES, true), 3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		return Block.EnumOffsetType.XZ;
	}

}
