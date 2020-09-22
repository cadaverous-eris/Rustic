package rustic.common.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.crafting.IInfusionStabiliser;

import static rustic.common.Config.ENABLE_CANDLE_INFUSION;

@Optional.Interface(modid = "thaumcraft", iface = "thaumcraft.api.crafting.IInfusionStabiliser", striprefs = true)
public class BlockCandle extends BlockBase implements IInfusionStabiliser {

	public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
		public boolean apply(@Nullable EnumFacing p_apply_1_) {
			return p_apply_1_ != EnumFacing.DOWN;
		}
	});
	protected static final AxisAlignedBB STANDING_AABB = new AxisAlignedBB(0.4, 0.0, 0.4, 0.6, 0.9375, 0.6);
	protected static final AxisAlignedBB CANDLE_NORTH_AABB = new AxisAlignedBB(0.35, 0.0, 0.7, 0.65, 0.8, 1.0);
	protected static final AxisAlignedBB CANDLE_SOUTH_AABB = new AxisAlignedBB(0.35, 0.0, 0.0, 0.65, 0.8, 0.3);
	protected static final AxisAlignedBB CANDLE_WEST_AABB = new AxisAlignedBB(0.7, 0.0, 0.35, 1.0, 0.8, 0.65);
	protected static final AxisAlignedBB CANDLE_EAST_AABB = new AxisAlignedBB(0.0D, 0.0, 0.35, 0.3, 0.8, 0.65);

	public BlockCandle() {
		this("candle");
	}
	
	public BlockCandle(String name) {
		this(Material.CIRCUITS, name, true);
	}
	
	public BlockCandle(Material mat, String name, boolean register) {
		super(mat, name, register);
		this.setHardness(1F);
		this.setLightLevel(1.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
		setSoundType(SoundType.METAL);
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

	private boolean canPlaceOn(World worldIn, BlockPos pos) {
		IBlockState state = worldIn.getBlockState(pos);
		BlockFaceShape faceShape = state.getBlockFaceShape(worldIn, pos, EnumFacing.UP);
		
		if (
			faceShape == BlockFaceShape.SOLID ||
			faceShape == BlockFaceShape.CENTER ||
			faceShape == BlockFaceShape.CENTER_BIG ||
			faceShape == BlockFaceShape.CENTER_SMALL
		) {
			return true;
		} else {
			return state.getBlock().canPlaceTorchOnTop(state, worldIn, pos);
		}
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		for (EnumFacing enumfacing : FACING.getAllowedValues()) {
			if (this.canPlaceAt(worldIn, pos, enumfacing)) {
				return true;
			}
		}

		return false;
	}

	private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
		BlockPos blockpos = pos.offset(facing.getOpposite());
		boolean flag = facing.getAxis().isHorizontal();
		return flag && worldIn.isSideSolid(blockpos, facing, true)
				|| facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, blockpos);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		if (this.canPlaceAt(worldIn, pos, facing)) {
			return this.getDefaultState().withProperty(FACING, facing);
		} else {
			for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
				if (worldIn.isSideSolid(pos.offset(enumfacing.getOpposite()), enumfacing, true)) {
					return this.getDefaultState().withProperty(FACING, enumfacing);
				}
			}

			return this.getDefaultState();
		}
	}

	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		this.checkForDrop(worldIn, pos, state);
	}

	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.onNeighborChangeInternal(worldIn, pos, state);
	}

	protected boolean onNeighborChangeInternal(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.checkForDrop(worldIn, pos, state)) {
			return true;
		} else {
			EnumFacing enumfacing = (EnumFacing) state.getValue(FACING);
			EnumFacing.Axis enumfacing$axis = enumfacing.getAxis();
			EnumFacing enumfacing1 = enumfacing.getOpposite();
			boolean flag = false;

			if (enumfacing$axis.isHorizontal() && !worldIn.isSideSolid(pos.offset(enumfacing1), enumfacing, true)) {
				flag = true;
			} else if (enumfacing$axis.isVertical() && !this.canPlaceOn(worldIn, pos.offset(enumfacing1))) {
				flag = true;
			}

			if (flag) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
				worldIn.setBlockToAir(pos);
				return true;
			} else {
				return false;
			}
		}
	}

	protected boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
		if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, (EnumFacing) state.getValue(FACING))) {
			return true;
		} else {
			if (worldIn.getBlockState(pos).getBlock() == this) {
				this.dropBlockAsItem(worldIn, pos, state, 0);
				worldIn.setBlockToAir(pos);
			}

			return false;
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		switch ((EnumFacing) state.getValue(FACING)) {
		case EAST:
			return CANDLE_EAST_AABB;
		case WEST:
			return CANDLE_WEST_AABB;
		case SOUTH:
			return CANDLE_SOUTH_AABB;
		case NORTH:
			return CANDLE_NORTH_AABB;
		default:
			return STANDING_AABB;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		EnumFacing enumfacing = (EnumFacing) stateIn.getValue(FACING);
		double d0 = (double) pos.getX() + 0.5D;
		double d1 = (double) pos.getY() + 0.7D;
		double d2 = (double) pos.getZ() + 0.5D;
		//double d3 = 0.22D;
		//double d4 = 0.27D;

		if (enumfacing.getAxis().isHorizontal()) {
			EnumFacing enumfacing1 = enumfacing.getOpposite();
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + 0.27D * (double) enumfacing1.getFrontOffsetX(),
					d1 + 0.25D, d2 + 0.27D * (double) enumfacing1.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D, new int[0]);
			worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + 0.27D * (double) enumfacing1.getFrontOffsetX(),
					d1 + 0.25D, d2 + 0.27D * (double) enumfacing1.getFrontOffsetZ(), 0.0D, 0.0D, 0.0D, new int[0]);
		} else {
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1 + 0.33D, d2, 0.0D, 0.0D, 0.0D, new int[0]);
			worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1 + 0.33D, d2, 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState();

		switch (meta) {
		case 1:
			iblockstate = iblockstate.withProperty(FACING, EnumFacing.EAST);
			break;
		case 2:
			iblockstate = iblockstate.withProperty(FACING, EnumFacing.WEST);
			break;
		case 3:
			iblockstate = iblockstate.withProperty(FACING, EnumFacing.SOUTH);
			break;
		case 4:
			iblockstate = iblockstate.withProperty(FACING, EnumFacing.NORTH);
			break;
		case 5:
		default:
			iblockstate = iblockstate.withProperty(FACING, EnumFacing.UP);
		}

		return iblockstate;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	public int getMetaFromState(IBlockState state) {
		int i = 0;

		switch ((EnumFacing) state.getValue(FACING)) {
		case EAST:
			i = i | 1;
			break;
		case WEST:
			i = i | 2;
			break;
		case SOUTH:
			i = i | 3;
			break;
		case NORTH:
			i = i | 4;
			break;
		case DOWN:
		case UP:
		default:
			i = i | 5;
		}

		return i;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.DOWN ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean canStabaliseInfusion(World world, BlockPos blockPos) {
		return ENABLE_CANDLE_INFUSION;
	}
}
