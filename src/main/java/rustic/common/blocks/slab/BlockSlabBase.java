package rustic.common.blocks.slab;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.core.Rustic;

public class BlockSlabBase extends BlockSlab {

	public Item itemBlock = null;
	//private Block doubleSlab;

	public BlockSlabBase(Material materialIn, String name, Block block) {
		super(materialIn);
		setRegistryName(name);
		setUnlocalizedName(Rustic.MODID + "." + name);
		GameRegistry.findRegistry(Block.class).register(this);
		//GameRegistry.register(new ItemBlock(this), getRegistryName());
		this.setHardness(2.0F);
		this.useNeighborBrightness = true;
		//this.doubleSlab = block;
	}
	
	public BlockSlabBase(Material materialIn, String name, Block block, SoundType sound) {
		super(materialIn);
		setRegistryName(name);
		setUnlocalizedName(Rustic.MODID + "." + name);
		GameRegistry.findRegistry(Block.class).register(this);
		//GameRegistry.register(new ItemBlock(this), getRegistryName());
		this.setHardness(2.0F);
		this.useNeighborBrightness = true;
		//this.doubleSlab = block;
		this.setSoundType(sound);
	}

	@SideOnly(Side.CLIENT)
	protected static boolean isHalfSlab(IBlockState state) {
		return true;
	}

	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM));
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
				new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState();
		if (!this.isDouble())
			iblockstate = iblockstate.withProperty(HALF,
					(meta) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);

		return iblockstate;
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(HALF) == EnumBlockHalf.BOTTOM ? 0 : 1;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, HALF);
	}

	@Override
	public boolean isDouble() {
		return false;
	}

	@Override
	public String getUnlocalizedName(int meta) {
		return null;
	}

	@Override
	public IProperty getVariantProperty() {
		return HALF;
	}

	@Override
	public Comparable<?> getTypeForItem(ItemStack stack) {
		return 0;
	}

}
