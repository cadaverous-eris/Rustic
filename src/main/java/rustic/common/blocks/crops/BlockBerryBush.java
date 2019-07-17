package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.Config;
import rustic.common.blocks.BlockBase;
import rustic.common.blocks.IColoredBlock;
import rustic.common.blocks.ModBlocks;
import rustic.core.ClientProxy;
import rustic.core.Rustic;

public abstract class BlockBerryBush extends BlockBase implements IColoredBlock, IPlantable, IGrowable {

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
		
		Blocks.FIRE.setFireInfo(this, 40, 80);
	}

	public abstract Item getBerries();

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
		this.checkAndDropBlock(worldIn, pos, state);

		if (worldIn.getLightFromNeighbors(pos.up()) >= 9) {

			if (!state.getValue(BERRIES)) {
				float f = 2f;

				if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state,
						rand.nextInt((int) (40.0F / f) + 1) == 0)) {
					worldIn.setBlockState(pos, state.withProperty(BERRIES, true), 3);
					net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state,
							worldIn.getBlockState(pos));
				}
			}/* else {
				float f = getGrowthChance(this, worldIn, pos);

				if (numNeighbors(worldIn, pos) < 2 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos,
						state, rand.nextInt((int) (100.0F / f) + 1) == 0)) {
					growOutward(worldIn, pos, rand);
					net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state,
							worldIn.getBlockState(pos));
				}
			}*/
		}
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
		ItemStack heldItem = player.getHeldItem(hand);
		
		if (state.getValue(BERRIES) && !(!heldItem.isEmpty() && heldItem.getItem() == Items.DYE && heldItem.getMetadata() == 15)) {
			world.setBlockState(pos, state.withProperty(BERRIES, false), 3);
			ItemStack stack = new ItemStack(getBerries());
			if (!player.addItemStackToInventory(stack)) {
			    Block.spawnAsEntity(world, pos.offset(player.getHorizontalFacing().getOpposite()), stack);
			}
			return true;
		}
		return false;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BUSH_AABB;
	}
	
	@Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }
	
	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
		entity.motionX *= 0.6F;
		entity.motionZ *= 0.6F;
	}
	
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
        return true;
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
		BlockPos down = pos.down();
		IBlockState soil = worldIn.getBlockState(down);
		if (soil.getMaterial().isLiquid()) return false;
		return super.canPlaceBlockAt(worldIn, pos)
				&& soil.getBlock().canSustainPlant(soil, worldIn, down, net.minecraft.util.EnumFacing.UP, this);
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
		return true;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		if (state.getValue(BERRIES)) {
			this.growOutward(worldIn, pos, rand);
		} else {
			worldIn.setBlockState(pos, state.withProperty(BERRIES, true), 3);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Block.EnumOffsetType getOffsetType() {
		if (Config.OFFSET_WILDBERRY_BUSHES) {
			return Block.EnumOffsetType.XZ;
		}
		return Block.EnumOffsetType.NONE;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return BlockFaceShape.UNDEFINED;
    }
	
	@SideOnly(Side.CLIENT)
	@Override
	public IBlockColor getBlockColor() {
		return new IBlockColor() {
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
				if (worldIn != null && pos != null && tintIndex == 1) {
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
	
	@SideOnly(Side.CLIENT)
	@Override
	public void initModel() {
		super.initModel();
		ClientProxy.addColoredBlock(this);
	}

}
