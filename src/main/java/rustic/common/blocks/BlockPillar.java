package rustic.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import rustic.core.Rustic;

public class BlockPillar extends BlockBase {

	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis",
			EnumFacing.Axis.class);

	public BlockPillar(String type) {
		super(Material.ROCK, type + "_pillar");
		this.setHardness(2.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumFacing.Axis.Y));
		this.setCreativeTab(Rustic.decorTab);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state.withProperty(AXIS, EnumFacing.Axis.Y));
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { AXIS });
	}

	public IBlockState getStateFromMeta(int meta) {
		EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Y;
		int i = meta;

		if (i == 0) {
			enumfacing$axis = EnumFacing.Axis.Y;
		} else if (i == 1) {
			enumfacing$axis = EnumFacing.Axis.X;
		} else if (i == 2) {
			enumfacing$axis = EnumFacing.Axis.Z;
		}

		return this.getDefaultState().withProperty(AXIS, enumfacing$axis);
	}

	public int getMetaFromState(IBlockState state) {
		int i = 0;
		EnumFacing.Axis enumfacing$axis = (EnumFacing.Axis) state.getValue(AXIS);

		if (enumfacing$axis == EnumFacing.Axis.X) {
			i = 1;
		} else if (enumfacing$axis == EnumFacing.Axis.Z) {
			i = 2;
		}

		return i;
	}
	
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(AXIS, facing.getAxis());
    }

	public IBlockState withRotation(IBlockState state, Rotation rot) {
		switch (rot) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:

			switch ((EnumFacing.Axis) state.getValue(AXIS)) {
			case X:
				return state.withProperty(AXIS, EnumFacing.Axis.Z);
			case Z:
				return state.withProperty(AXIS, EnumFacing.Axis.X);
			default:
				return state;
			}

		default:
			return state;
		}
	}
}
