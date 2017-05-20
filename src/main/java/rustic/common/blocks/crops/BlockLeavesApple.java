package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.BlockPlanksRustic;
import rustic.common.blocks.IColoredBlock;
import rustic.common.blocks.ModBlocks;
import rustic.common.items.ModItems;
import rustic.core.ClientProxy;
import rustic.core.Rustic;

public class BlockLeavesApple extends BlockLeaves implements IColoredBlock, IGrowable {

	public static final PropertyInteger AGE = PropertyInteger.create("apple_age", 0, 3);

	public BlockLeavesApple() {
		super();
		setRegistryName("leaves_apple");
		setUnlocalizedName(Rustic.MODID + "." + "leaves_apple");
		setCreativeTab(Rustic.farmingTab);
		setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0).withProperty(CHECK_DECAY, true)
				.withProperty(DECAYABLE, true));
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
	}

	public int getMaxAge() {
		return 3;
	}

	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);

		int i = state.getValue(AGE);

		if (i < this.getMaxAge() && isAirAdjacent(worldIn, pos, state)) {
			float f = getGrowthChance(this, worldIn, pos);

			if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state,
					rand.nextInt((int) (25.0F / f) + 1) == 0)) {
				worldIn.setBlockState(pos, state.withProperty(AGE, (i + 1)), 2);
				net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state, worldIn.getBlockState(pos));
			}
		}
	}

	protected static float getGrowthChance(Block blockIn, World worldIn, BlockPos pos) {
		return 7F;
	}

	protected static boolean isAirAdjacent(World world, BlockPos pos, IBlockState state) {
		if (world.isAirBlock(pos.down()) || world.isAirBlock(pos.north()) || world.isAirBlock(pos.south())
				|| world.isAirBlock(pos.west()) || world.isAirBlock(pos.east())) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return state.getValue(AGE) < getMaxAge() && isAirAdjacent(worldIn, pos, state);
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return state.getValue(AGE) < getMaxAge() && isAirAdjacent(worldIn, pos, state);
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
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (state.getValue(AGE) >= getMaxAge()) {
			world.setBlockState(pos, state.withProperty(AGE, 0), 3);
			state.getBlock().spawnAsEntity(world, pos.offset(side), new ItemStack(Items.APPLE));
			return true;
		}
		return false;
	}

	@Override
	protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
		if (state.getValue(AGE) >= getMaxAge()) {
			spawnAsEntity(worldIn, pos, new ItemStack(Items.APPLE));
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(ModBlocks.APPLE_SAPLING);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> ret = new ArrayList<ItemStack>();
		Random rand = world instanceof World ? ((World) world).rand : new Random();
		int chance = this.getSaplingDropChance(state);
		if (fortune > 0) {
			chance -= 2 << fortune;
			if (chance < 10)
				chance = 10;
		}
		if (rand.nextInt(chance) == 0) {
			ItemStack drop = new ItemStack(getItemDropped(state, rand, fortune), 1, damageDropped(state));
			if (!drop.isEmpty()) {
				ret.add(drop);
			}
		}
		this.captureDrops(true);
		if (world instanceof World) {
			this.dropApple((World) world, pos, state, chance);
		}
		ret.addAll(this.captureDrops(false));
		return ret;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		IStateMapper custom_mapper = (new StateMap.Builder()).ignore(new IProperty[] { CHECK_DECAY, DECAYABLE })
				.build();
		ModelLoader.setCustomStateMapper(this, custom_mapper);
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
				new ModelResourceLocation(getRegistryName(), "inventory"));
		ClientProxy.addColoredBlock(this);
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return NonNullList.withSize(1, new ItemStack(Blocks.LEAVES, 1, BlockPlanks.EnumType.OAK.getMetadata()));
	}

	@Override
	public int damageDropped(IBlockState state) {
		return 0;
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return new ItemStack(Blocks.LEAVES, 1, BlockPlanks.EnumType.OAK.getMetadata());
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state,
			@Nullable TileEntity te, ItemStack stack) {
		if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
			player.addStat(StatList.getBlockStats(Blocks.LEAVES));
		} else {
			super.harvestBlock(worldIn, player, pos, state, te, stack);
		}
	}

	@Override
	protected int getSaplingDropChance(IBlockState state) {
		return 20;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return Blocks.LEAVES.getBlockLayer();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return Blocks.LEAVES.isOpaqueCube(state);
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return Blocks.LEAVES.shouldSideBeRendered(state, world, pos, side);
	}

	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos,
			EntityPlayer player) {
		return new ItemStack(this, 1, this.getMetaFromState(this.getDefaultState()));
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(CHECK_DECAY, Boolean.valueOf(false)).withProperty(DECAYABLE,
				Boolean.valueOf(false));
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AGE, (meta & 3)).withProperty(DECAYABLE, (meta & 4) == 0)
				.withProperty(CHECK_DECAY, (meta & 8) > 0);
	}

	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i |= state.getValue(AGE);

		if (!state.getValue(DECAYABLE)) {
			i |= 4;
		}
		if (state.getValue(CHECK_DECAY)) {
			i |= 8;
		}

		return i;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { CHECK_DECAY, DECAYABLE, AGE });
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IBlockColor getBlockColor() {
		return new IBlockColor() {
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
				if (worldIn != null && pos != null && tintIndex == 0) {
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
			public int getColorFromItemstack(ItemStack stack, int tintIndex) {
				IBlockState state = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
				IBlockColor blockColor = ((IColoredBlock) state.getBlock()).getBlockColor();
				return blockColor == null ? 0xFFFFFF : blockColor.colorMultiplier(state, null, null, tintIndex);
			}
		};
	}

	@Override
	public EnumType getWoodType(int meta) {
		return null;
	}

}
