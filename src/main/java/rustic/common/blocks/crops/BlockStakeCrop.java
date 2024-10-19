package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.BlockBase;
import rustic.common.blocks.ModBlocks;

public class BlockStakeCrop extends BlockBase implements IGrowable, IPlantable {

	public static final Material STAKE_CROP = new Material(MapColor.FOLIAGE) {
		@Override
		public boolean isSolid() {
			return false;
		}

		@Override
		public boolean blocksLight() {
			return false;
		}

		@Override
		public EnumPushReaction getMobilityFlag() {
			return EnumPushReaction.BLOCK;
		}
	};

	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 3);

	protected static final AxisAlignedBB CROP_AABB = new AxisAlignedBB(0.125F, 0F, 0.125F, 0.875F, 1.0F, 0.875F);
	protected static final AxisAlignedBB STAKE_AABB = new AxisAlignedBB(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.0F, 0.5625F);

	public BlockStakeCrop(String name) {
		super(STAKE_CROP, name, false);
		setSoundType(SoundType.PLANT);
		setHardness(0);
		setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)));
		GameRegistry.findRegistry(Block.class).register(this);
		
		Blocks.FIRE.setFireInfo(this, 30, 100);
	}

	public int getMaxAge() {
		return 3;
	}
	
	public int getMaxHeight() {
		return 3;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return CROP_AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return STAKE_AABB;
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
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return false;
    }

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.setBlockState(pos, ModBlocks.CROP_STAKE.getDefaultState(), 3);
		dropBlockAsItem(world, pos, state, 0);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		Random rand = world instanceof World ? ((World)world).rand : new Random();
		stacks.add(new ItemStack(getSeed(), rand.nextInt(2) + 1));
		if (state.getValue(AGE) >= getMaxAge()) {
			stacks.add(new ItemStack(getCrop(rand)));
		}
		return stacks;
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		this.checkAndDropBlock(worldIn, pos, state);
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);

		this.checkAndDropBlock(worldIn, pos, state);
		
		if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {
			int i = state.getValue(AGE);
			
			if (i < getMaxAge()) {
				float f = getGrowthChance(this, worldIn, pos);

				if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state,
						rand.nextInt((int) (50.0F / f) + 1) == 0)) {
					worldIn.setBlockState(pos, state.withProperty(AGE, i + 1), 2);
					net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state,
							worldIn.getBlockState(pos));
				}
			} else if (worldIn.getBlockState(pos.up()).getBlock() == ModBlocks.CROP_STAKE && worldIn.getBlockState(pos.down(getMaxHeight() - 1)).getBlock() != this) {
				float f = getGrowthChance(this, worldIn, pos);

				if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state,
						rand.nextInt((int) (30.0F / f) + 1) == 0)) {
					worldIn.setBlockState(pos.up(), state.withProperty(AGE, 0), 3);
					net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state,
							worldIn.getBlockState(pos));
				}
			}
		}
	}

	protected static float getGrowthChance(Block block, World world, BlockPos pos) {
		float growth = 0.125F * (world.getLight(pos) - 11);
		IBlockState soil = world.getBlockState(pos.add(0, -1, 0));
		if (soil.getBlock().isFertile(world, pos.add(0, -1, 0)) || soil.getBlock() == block)
			growth *= 1.5F;
		return Math.abs(1.5F + growth);
	}

	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockState(pos, ModBlocks.CROP_STAKE.getDefaultState(), 3);
		}
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return state.getValue(AGE) < getMaxAge();
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		int i = state.getValue(AGE) + this.getBonemealAgeIncrease(worldIn);
		int j = this.getMaxAge();

		if (i > j) {
			i = j;
		}

		worldIn.setBlockState(pos, state.withProperty(AGE, i), 2);
	}

	protected int getBonemealAgeIncrease(World worldIn) {
		return MathHelper.getInt(worldIn.rand, 2, 5);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AGE, meta);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(AGE);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { AGE });
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Crop;
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return this.getDefaultState();
	}

	public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		return (worldIn.getLight(pos) >= 8 || worldIn.canSeeSky(pos)) && (soil.getBlock() == this
				|| soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), net.minecraft.util.EnumFacing.UP, this));
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this.getSeed());
	}

	protected Item getSeed() {
		return null;
	}

	protected Item getCrop() {
		return null;
	}
	
	protected Item getCrop(Random rand) {
		return this.getCrop();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		boolean result = tryHarvest(world, pos, state, player, hand, side);
		BlockPos.MutableBlockPos colPos = new BlockPos.MutableBlockPos(pos);
		for (int y = pos.getY() + 1; y < world.getHeight(); y++) {
			colPos.setY(y);
			IBlockState colState = world.getBlockState(colPos);
			if (colState.getBlock() != this) break;
			result = tryHarvest(world, colPos, colState, player, hand, side);
		}
		for (int y = pos.getY() - 1; y >= 0; y--) {
			colPos.setY(y);
			IBlockState colState = world.getBlockState(colPos);
			if (colState.getBlock() != this) break;
			result = tryHarvest(world, colPos, colState, player, hand, side) || result;
		}
		return result;
	}
	
	public boolean tryHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing interactSide) {
		if (state.getValue(AGE) >= getMaxAge()) {
			world.setBlockState(pos, state.withProperty(AGE, getMaxAge() - 1), 2);
			ItemStack stack = new ItemStack(getCrop(world.rand));
			if (!player.addItemStackToInventory(stack)) {
			    Block.spawnAsEntity(world, pos.offset(interactSide), stack);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return side != EnumFacing.UP && side != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE : BlockFaceShape.CENTER;
    }

}
