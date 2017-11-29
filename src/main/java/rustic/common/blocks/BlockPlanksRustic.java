package rustic.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.renderer.LiquidBarrelRenderer;
import rustic.common.tileentity.TileEntityLiquidBarrel;
import rustic.core.Rustic;

public class BlockPlanksRustic extends BlockBase {

	public static final PropertyEnum<BlockPlanksRustic.EnumType> VARIANT = PropertyEnum.<BlockPlanksRustic.EnumType>create("variant", BlockPlanksRustic.EnumType.class);

	public BlockPlanksRustic() {
		super(Material.WOOD, "planks", false);
		setHardness(2F);
		setResistance(5F);
		setSoundType(SoundType.WOOD);

		ItemBlock item = new ItemBlock(this) {
			@Override
			public String getUnlocalizedName(ItemStack stack) {
				IBlockState state = BlockPlanksRustic.this.getStateFromMeta(stack.getMetadata());
				return getUnlocalizedName() + "_" + state.getValue(BlockPlanksRustic.VARIANT);
			}

			@Override
			public int getMetadata(int damage) {
				return damage;
			}
		};
		item.setHasSubtypes(true);
		item.setMaxDamage(0);
		item.setRegistryName(this.getRegistryName());
		register(item);

		Blocks.FIRE.setFireInfo(this, 5, 20);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int i, EntityLivingBase player, EnumHand hand) {
		return getStateFromMeta(i);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		for (int i = 0; i < EnumType.values().length; i++) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(getRegistryName() + "_" + EnumType.byMetadata(i).getName(), "inventory"));
		}
	}

	public int damageDropped(IBlockState state) {
		return ((BlockPlanksRustic.EnumType) state.getValue(VARIANT)).getMetadata();
	}

	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (BlockPlanksRustic.EnumType blockplanks$enumtype : BlockPlanksRustic.EnumType.values()) {
			list.add(new ItemStack(Item.getItemFromBlock(this), 1, blockplanks$enumtype.getMetadata()));
		}
	}

	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, BlockPlanksRustic.EnumType.byMetadata(meta));
	}

	public int getMetaFromState(IBlockState state) {
		return ((BlockPlanksRustic.EnumType) state.getValue(VARIANT)).getMetadata();
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { VARIANT });
	}

	public static enum EnumType implements IStringSerializable {
		OLIVE(0, "olive"), IRONWOOD(1, "ironwood");

		private static final BlockPlanksRustic.EnumType[] META_LOOKUP = new BlockPlanksRustic.EnumType[values().length];
		private final int meta;
		private final String name;
		private final String unlocalizedName;

		private EnumType(int metaIn, String nameIn) {
			this(metaIn, nameIn, nameIn);
		}

		private EnumType(int metaIn, String nameIn, String unlocalizedNameIn) {
			this.meta = metaIn;
			this.name = nameIn;
			this.unlocalizedName = unlocalizedNameIn;
		}

		public int getMetadata() {
			return this.meta;
		}

		public String toString() {
			return this.name;
		}

		public static BlockPlanksRustic.EnumType byMetadata(int meta) {
			if (meta < 0 || meta >= META_LOOKUP.length) {
				meta = 0;
			}

			return META_LOOKUP[meta];
		}

		public String getName() {
			return this.name;
		}

		public String getUnlocalizedName() {
			return this.unlocalizedName;
		}

		static {
			for (BlockPlanksRustic.EnumType blockplanks$enumtype : values()) {
				META_LOOKUP[blockplanks$enumtype.getMetadata()] = blockplanks$enumtype;
			}
		}
	}

}
