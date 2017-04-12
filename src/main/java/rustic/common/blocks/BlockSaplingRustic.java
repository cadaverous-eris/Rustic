package rustic.common.blocks;

import java.util.Random;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenBirchTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.BlockPlanksRustic.EnumType;
import rustic.common.world.WorldGenIronwoodTree;
import rustic.common.world.WorldGenOliveTree;
import rustic.core.Rustic;

public class BlockSaplingRustic extends BlockBush implements IGrowable {

	public static final PropertyEnum<BlockPlanksRustic.EnumType> VARIANT = PropertyEnum.create("variant", BlockPlanksRustic.EnumType.class);
	public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
	protected static final AxisAlignedBB SAPLING_AABB = new AxisAlignedBB(0.09999999403953552D, 0.0D, 0.09999999403953552D, 0.8999999761581421D, 0.800000011920929D, 0.8999999761581421D);

	public BlockSaplingRustic() {
		super();
		setRegistryName("sapling");
		setUnlocalizedName(Rustic.MODID + "." + "sapling");
		setCreativeTab(Rustic.tab);
		ItemBlock item = new ItemBlock(this) {
			@Override
			public String getUnlocalizedName(ItemStack stack) {
				IBlockState state = BlockSaplingRustic.this.getStateFromMeta(stack.getMetadata());
				return getUnlocalizedName() + "_" + state.getValue(BlockPlanksRustic.VARIANT);
			}

			@Override
			public int getMetadata(int damage) {
				return damage;
			}
		};
		item.setHasSubtypes(true);
		item.setMaxDamage(0);
		GameRegistry.register(this);
		GameRegistry.register(item, getRegistryName());
		setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanksRustic.EnumType.OLIVE).withProperty(STAGE, 0));
		setSoundType(SoundType.PLANT);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		IStateMapper custom_mapper = (new StateMap.Builder()).ignore(new IProperty[] { STAGE }).build();
		ModelLoader.setCustomStateMapper(this, custom_mapper);
		for (int i = 0; i < EnumType.values().length; i++) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(getRegistryName() + "_" + EnumType.byMetadata(i).getName(), "inventory"));
		}
	}

	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(STAGE, 0);
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return SAPLING_AABB;
	}

	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			super.updateTick(worldIn, pos, state, rand);

			if (worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
				this.grow(worldIn, rand, pos, state);
			}
		}
	}

	public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
			return;
		}
		WorldGenerator worldgenerator = null;
		int i = 0;
		int j = 0;

		switch ((BlockPlanksRustic.EnumType) state.getValue(VARIANT)) {
		case OLIVE:
			worldgenerator = new WorldGenOliveTree(true);
			break;
		case IRONWOOD:
			worldgenerator = new WorldGenIronwoodTree(true);
			break;
		}

		IBlockState iblockstate2 = Blocks.AIR.getDefaultState();

		worldIn.setBlockState(pos, iblockstate2, 4);

		if (worldgenerator == null || !worldgenerator.generate(worldIn, rand, pos.add(i, 0, j))) {
			worldIn.setBlockState(pos, state, 4);
		}
	}

	public boolean isTypeAt(World worldIn, BlockPos pos, BlockPlanksRustic.EnumType type) {
		IBlockState iblockstate = worldIn.getBlockState(pos);
		return iblockstate.getBlock() == this && iblockstate.getValue(VARIANT) == type;
	}

	public int damageDropped(IBlockState state) {
		return ((BlockPlanksRustic.EnumType) state.getValue(VARIANT)).getMetadata();
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for (BlockPlanksRustic.EnumType blockplanks$enumtype : BlockPlanksRustic.EnumType.values()) {
			list.add(new ItemStack(itemIn, 1, blockplanks$enumtype.getMetadata()));
		}
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return true;
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return (double) worldIn.rand.nextFloat() < 0.45D;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		if (state.getValue(STAGE) == 0) {
			worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
		} else {
			this.generateTree(worldIn, pos, state, rand);
		}
	}

	@Override
	public net.minecraftforge.common.EnumPlantType getPlantType(net.minecraft.world.IBlockAccess world, BlockPos pos) {
		return net.minecraftforge.common.EnumPlantType.Plains;
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, BlockPlanksRustic.EnumType.byMetadata(meta & 7)).withProperty(STAGE, (meta & 8) >> 3);
	}

	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = i | ((BlockPlanksRustic.EnumType) state.getValue(VARIANT)).getMetadata();
		i = i | (state.getValue(STAGE) << 3);
		return i;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT, STAGE });
	}

}
