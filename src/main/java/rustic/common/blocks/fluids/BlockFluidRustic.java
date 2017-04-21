package rustic.common.blocks.fluids;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.core.Rustic;

public class BlockFluidRustic extends BlockFluidClassic {

	public BlockFluidRustic(String name, Fluid fluid, Material material) {
		super(fluid, material);
		setRegistryName(name);
		setUnlocalizedName(Rustic.MODID + "." + name);
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
		fluid.setBlock(this);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
        Item item = Item.getItemFromBlock(this);   

        FluidStateMapper stateMapper = new FluidStateMapper(this.stack.getFluid());
        
        final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(Rustic.MODID + ":block_fluid", stack.getFluid().getName());

        ModelLoader.registerItemVariants(item);
        ModelLoader.setCustomMeshDefinition(item, stateMapper);

        ModelLoader.setCustomStateMapper(this, stateMapper);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getBlockState().getBaseState().withProperty(LEVEL, meta);
	}

}
