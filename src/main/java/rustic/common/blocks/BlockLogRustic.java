package rustic.common.blocks;

import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.BlockPlanksRustic.EnumType;
import rustic.core.Rustic;

public class BlockLogRustic extends BlockLog {

	public static final PropertyEnum<BlockPlanksRustic.EnumType> VARIANT = PropertyEnum.<BlockPlanksRustic.EnumType>create("variant", BlockPlanksRustic.EnumType.class);

	public BlockLogRustic() {
		super();
		setRegistryName("log");
		setUnlocalizedName(Rustic.MODID + "." + "log");
		setCreativeTab(Rustic.farmingTab);
		ItemBlock item = new ItemBlock(this) {
			@Override
			public String getUnlocalizedName(ItemStack stack) {
				IBlockState state = BlockLogRustic.this.getStateFromMeta(stack.getMetadata());
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
		setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanksRustic.EnumType.OLIVE).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
		
		Blocks.FIRE.setFireInfo(this, 5, 5);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		for (int i = 0; i < EnumType.values().length && i < 4; i++) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(getRegistryName() + "_" + EnumType.byMetadata(i).getName(), "inventory"));
		}
	}

	public int damageDropped(IBlockState state) {
		return ((BlockPlanksRustic.EnumType) state.getValue(VARIANT)).getMetadata();
	}

	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getStateFromMeta(meta).withProperty(LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()));
	}

	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for (BlockPlanksRustic.EnumType blockplanks$enumtype : BlockPlanksRustic.EnumType.values()) {
			if (blockplanks$enumtype.getMetadata() < 4) {
				list.add(new ItemStack(itemIn, 1, blockplanks$enumtype.getMetadata()));
			}
		}
	}

	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockPlanksRustic.EnumType.byMetadata((meta & 3) % 4));

		switch (meta & 12) {
		case 0:
			iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
			break;
		case 4:
			iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
			break;
		case 8:
			iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
			break;
		default:
			iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
		}

		return iblockstate;
	}

	@SuppressWarnings("incomplete-switch")
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = i | ((BlockPlanksRustic.EnumType) state.getValue(VARIANT)).getMetadata();

		switch ((BlockLog.EnumAxis) state.getValue(LOG_AXIS)) {
		case X:
			i |= 4;
			break;
		case Z:
			i |= 8;
			break;
		case NONE:
			i |= 12;
		}

		return i;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT, LOG_AXIS });
	}

	protected ItemStack getSilkTouchDrop(IBlockState state) {
		return new ItemStack(Item.getItemFromBlock(this), 1, ((BlockPlanksRustic.EnumType) state.getValue(VARIANT)).getMetadata());
	}

}
