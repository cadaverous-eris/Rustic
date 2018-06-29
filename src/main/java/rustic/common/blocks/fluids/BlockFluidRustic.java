package rustic.common.blocks.fluids;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.core.Rustic;

public class BlockFluidRustic extends BlockFluidClassic {

	public BlockFluidRustic(String name, Fluid fluid, Material material) {
		super(fluid, material);
		setRegistryName(name);
		setUnlocalizedName(Rustic.MODID + "." + name);
		GameRegistry.findRegistry(Block.class).register(this);
		GameRegistry.findRegistry(Item.class).register(new ItemBlock(this).setRegistryName(getRegistryName()));
		fluid.setBlock(this);
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
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		Item item = Item.getItemFromBlock(this);

		FluidStateMapper stateMapper = new FluidStateMapper(this.stack.getFluid());

		//final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(Rustic.MODID + ":block_fluid", stack.getFluid().getName());

		ModelLoader.registerItemVariants(item);
		ModelLoader.setCustomMeshDefinition(item, stateMapper);

		ModelLoader.setCustomStateMapper(this, stateMapper);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getBlockState().getBaseState().withProperty(LEVEL, meta);
	}

	@Override
	public boolean shouldSideBeRendered(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos,
			@Nonnull EnumFacing side) {
		IBlockState neighbor = world.getBlockState(pos.offset(side));
		if (neighbor.getBlock() != state.getBlock()) {
			return true;
		} else {
			return false;
		}
		/*
		 * if (densityDir == -1 && side == EnumFacing.UP) { return true; } if
		 * (densityDir == 1 && side == EnumFacing.DOWN) { return true; } return
		 * super.shouldSideBeRendered(state, world, pos, side);
		 */
	}

	@Override
	protected boolean canFlowInto(IBlockAccess world, BlockPos pos) {
		if (world.isAirBlock(pos)) {
			return true;
		}

		IBlockState state = world.getBlockState(pos);
		if (state.getBlock() == this) {
			return true;
		}

		if (displacements.containsKey(state.getBlock())) {
			return displacements.get(state.getBlock());
		}

		Material material = state.getMaterial();
		if (material.blocksMovement() || material == Material.WATER || material == Material.LAVA
				|| material == Material.PORTAL) {
			return false;
		}

		if (state.getBlock() instanceof BlockFluidBase) {
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE) {
			return true;
		}

		if (this.density > density) {
			return true;
		} else {
			return false;
		}

	}

	@Override
	public float getFluidHeightAverage(float... flow) {
		float total = 0;
		int count = 0;

		float end = 0;

		for (int i = 0; i < flow.length; i++) {
			if (flow[i] >= 1) {
				return 1;
			}
			if (flow[i] >= 14f / 16) {
				total += flow[i] * 10;
				count += 10;
			}

			if (flow[i] >= 0) {
				total += flow[i];
				count++;
			}
		}

		if (end == 0) {
			end = total / count;
		}

		return end;
	}

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos) {
		if (world.isAirBlock(pos)) {
			return true;
		}

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == this) {
			return false;
		}

		if (block instanceof BlockFluidBase) {
			if (state.getValue(LEVEL) == 0) {
				return false;
			}
		}

		if (displacements.containsKey(block)) {
			if (displacements.get(block)) {
				if (state.getBlock() != Blocks.SNOW_LAYER)
					block.dropBlockAsItem(world, pos, state, 0);
				return true;
			}
			return false;
		}

		Material material = state.getMaterial();
		if (material.blocksMovement() || material == Material.PORTAL) {
			return false;
		}

		int density = getDensity(world, pos);
		if (density == Integer.MAX_VALUE) {
			block.dropBlockAsItem(world, pos, state, 0);
			return true;
		}

		if (this.density > density) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
        return BlockFaceShape.UNDEFINED;
    }

}
