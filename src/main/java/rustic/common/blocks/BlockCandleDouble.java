package rustic.common.blocks;

import java.util.Locale;
import java.util.Map;
import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCandleDouble extends BlockBase {
	
	public static enum EnumFacingWallOrXZ implements IStringSerializable {
		UP_X(0, EnumFacing.UP, EnumFacing.Axis.X, "up_x"),
	    UP_Z(1, EnumFacing.UP, EnumFacing.Axis.Z, "up_z"),
	    NORTH(2, EnumFacing.NORTH, EnumFacing.Axis.Z, "north"),
	    SOUTH(3, EnumFacing.SOUTH, EnumFacing.Axis.Z, "south"),
	    WEST(4, EnumFacing.WEST, EnumFacing.Axis.X, "west"),
	    EAST(5, EnumFacing.EAST, EnumFacing.Axis.X, "east");
		
		private final int index;
		private final EnumFacing facing;
		private final EnumFacing.Axis facingAxis;
		private final String name;
		
		public static final EnumFacingWallOrXZ[] VALUES = new EnumFacingWallOrXZ[6];
		private static final Map<String, EnumFacingWallOrXZ> NAME_LOOKUP = Maps.<String, EnumFacingWallOrXZ>newHashMap();
		
		private EnumFacingWallOrXZ(int index, EnumFacing facing, EnumFacing.Axis facingAxis, String name) {
			this.index = index;
			this.facing = facing;
			this.facingAxis = facingAxis;
			this.name = name;
		}
		
		public int getIndex() {
	        return this.index;
	    }
		public EnumFacing getFacing() {
			return this.facing;
		}
		public EnumFacing.Axis getFacingAxis() {
			return this.facingAxis;
		}
		public String getName() {
	        return this.name;
	    }
		
		
		@Nullable
	    public static EnumFacingWallOrXZ byName(String name) {
	        return name == null ? null : (EnumFacingWallOrXZ) NAME_LOOKUP.get(name.toLowerCase(Locale.ROOT));
	    }
		
		public static EnumFacingWallOrXZ getFront(int index) {
	        return VALUES[MathHelper.abs(index % VALUES.length)];
	    }
		
		public String toString() {
	        return this.name;
	    }
		
		public boolean isUp() {
			switch (this) {
				case UP_X:
				case UP_Z:
					return true;
				default:
					return false;
			}
		}
		public boolean isWall() {
			switch (this) {
				case UP_X:
				case UP_Z:
					return false;
				default:
					return true;
			}
		}

		
		public EnumFacingWallOrXZ getOpposite() {
			switch (this) {
	        	case UP_X:
	        		return UP_X;
	        	case UP_Z:
	        		return UP_Z;
	            case NORTH:
	                return SOUTH;
	            case EAST:
	                return WEST;
	            case SOUTH:
	                return NORTH;
	            case WEST:
	                return EAST;
	            default:
	                throw new IllegalStateException("Unable to get opposite facing of " + this);
	        }
	    }
	    
		public EnumFacingWallOrXZ rotateY() {
	        switch (this) {
	        	case UP_X:
	        		return UP_Z;
	        	case UP_Z:
	        		return UP_X;
	            case NORTH:
	                return EAST;
	            case EAST:
	                return SOUTH;
	            case SOUTH:
	                return WEST;
	            case WEST:
	                return NORTH;
	            default:
	                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
	        }
	    }
		public EnumFacingWallOrXZ rotateYCCW() {
	        switch (this) {
		        case UP_X:
	        		return UP_Z;
	        	case UP_Z:
	        		return UP_X;
	            case NORTH:
	                return WEST;
	            case EAST:
	                return NORTH;
	            case SOUTH:
	                return EAST;
	            case WEST:
	                return SOUTH;
	            default:
	                throw new IllegalStateException("Unable to get CCW facing of " + this);
	        }
	    }
		
		public static EnumFacingWallOrXZ fromHorizontalFacing(EnumFacing facing) {
			switch(facing) {
			case NORTH:
				return NORTH;
			case SOUTH:
				return SOUTH;
			case WEST:
				return WEST;
			case EAST:
				return EAST;
			default:
				throw new IllegalStateException("Unable to get wall or xz facing of horizontal facing " + facing);
			}
		}
		
		static {
	        for (EnumFacingWallOrXZ enumfacing : values()) {
	            VALUES[enumfacing.index] = enumfacing;

	            NAME_LOOKUP.put(enumfacing.getName().toLowerCase(Locale.ROOT), enumfacing);
	        }
	    }
	} 

	public static final PropertyEnum<EnumFacingWallOrXZ> FACING = PropertyEnum.<EnumFacingWallOrXZ>create("facing", EnumFacingWallOrXZ.class);
	protected static final AxisAlignedBB STANDING_X_AABB = new AxisAlignedBB(0.4, 0.0, 0.22, 0.6, 0.9375, 0.78);
	protected static final AxisAlignedBB STANDING_Z_AABB = new AxisAlignedBB(0.22, 0.0, 0.4, 0.78, 0.9375, 0.6);
	protected static final AxisAlignedBB CANDLE_NORTH_AABB = new AxisAlignedBB(0.175, 0.0, 0.6375, 0.825, 0.8, 1.0);
	protected static final AxisAlignedBB CANDLE_SOUTH_AABB = new AxisAlignedBB(0.175, 0.0, 0.0, 0.825, 0.8, 0.3625);
	protected static final AxisAlignedBB CANDLE_WEST_AABB = new AxisAlignedBB(0.6375, 0.0, 0.175, 1.0, 0.8, 0.825);
	protected static final AxisAlignedBB CANDLE_EAST_AABB = new AxisAlignedBB(0.0D, 0.0, 0.175, 0.3625, 0.8, 0.825);

	public BlockCandleDouble() {
		this("candle_double");
	}
	
	public BlockCandleDouble(String name) {
		this(Material.CIRCUITS, name, true);
	}
	
	public BlockCandleDouble(Material mat, String name, boolean register) {
		super(mat, name, register);
		this.setHardness(1F);
		this.setLightLevel(1.0F);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacingWallOrXZ.UP_X));
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
	
	protected boolean canPlaceOn(World worldIn, BlockPos pos) {
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
		for (EnumFacing enumfacing : EnumFacing.values()) {
			if ((!enumfacing.equals(EnumFacing.DOWN)) && this.canPlaceAt(worldIn, pos, enumfacing)) {
				return true;
			}
		}
		return false;
	}

	protected boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
		BlockPos blockpos = pos.offset(facing.getOpposite());
		boolean flag = facing.getAxis().isHorizontal();
		return flag && worldIn.isSideSolid(blockpos, facing, true)
				|| facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, blockpos);
	}

	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		if (this.canPlaceAt(worldIn, pos, facing)) {
			if (facing.getAxis().equals(EnumFacing.Axis.Y)) {
				if (placer != null) {
					EnumFacing.Axis lookAxis = placer.getHorizontalFacing().getAxis();
					if (lookAxis.equals(EnumFacing.Axis.Z)) {
						return this.getDefaultState().withProperty(FACING, EnumFacingWallOrXZ.UP_Z);
					} else {
						return this.getDefaultState().withProperty(FACING, EnumFacingWallOrXZ.UP_X);
					}
				} else {					
					return this.getDefaultState().withProperty(FACING, EnumFacingWallOrXZ.UP_X);
				}
			} else {				
				return this.getDefaultState().withProperty(FACING, EnumFacingWallOrXZ.fromHorizontalFacing(facing));
			}
		} else {
			for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
				if (worldIn.isSideSolid(pos.offset(enumfacing.getOpposite()), enumfacing, true)) {
					return this.getDefaultState().withProperty(FACING, EnumFacingWallOrXZ.fromHorizontalFacing(enumfacing));
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
			EnumFacing enumfacing = (EnumFacing) state.getValue(FACING).getFacing();
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
		if ((state.getBlock() == this) && this.canPlaceAt(worldIn, pos, (EnumFacing) state.getValue(FACING).getFacing())) {
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
		switch (state.getValue(FACING)) {
		case UP_X:
			return STANDING_X_AABB;
		case UP_Z:
			return STANDING_Z_AABB;
		case EAST:
			return CANDLE_EAST_AABB;
		case WEST:
			return CANDLE_WEST_AABB;
		case SOUTH:
			return CANDLE_SOUTH_AABB;
		case NORTH:
			return CANDLE_NORTH_AABB;
		default:
			return STANDING_X_AABB;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		// TODO: implement
		EnumFacingWallOrXZ facing = stateIn.getValue(FACING);
		EnumFacing enumfacing = facing.getFacing();
		boolean isFacingX = facing.getFacingAxis().equals(EnumFacing.Axis.X);
		double d0 = (double) pos.getX() + 0.5D;
		double d1 = (double) pos.getY() + 0.7D;
		double d2 = (double) pos.getZ() + 0.5D;

		if (enumfacing.getAxis().isHorizontal()) {
			final double candleOffset = 0.23125;
			
			EnumFacing enumfacing1 = enumfacing.getOpposite();
			d0 += 0.2609375D * enumfacing1.getFrontOffsetX();
			d1 += 0.25D;
			d2 += 0.2609375D * enumfacing1.getFrontOffsetZ();
			
			if (isFacingX) {
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2 + candleOffset, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2 + candleOffset, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2 - candleOffset, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2 - candleOffset, 0.0D, 0.0D, 0.0D);
			} else {
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + candleOffset, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + candleOffset, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - candleOffset, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - candleOffset, d1, d2, 0.0D, 0.0D, 0.0D);
			}
		} else {
			final double candleOffset = 0.1875;
			
			d1 += 0.33D;
			
			if (isFacingX) {
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2 + candleOffset, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2 + candleOffset, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2 - candleOffset, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2 - candleOffset, 0.0D, 0.0D, 0.0D);
			} else {
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + candleOffset, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + candleOffset, d1, d2, 0.0D, 0.0D, 0.0D);				
				worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 - candleOffset, d1, d2, 0.0D, 0.0D, 0.0D);
				worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 - candleOffset, d1, d2, 0.0D, 0.0D, 0.0D);		
			}
		}
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState();

		switch (meta) {
		case 1:
			iblockstate = iblockstate.withProperty(FACING, EnumFacingWallOrXZ.EAST);
			break;
		case 2:
			iblockstate = iblockstate.withProperty(FACING, EnumFacingWallOrXZ.WEST);
			break;
		case 3:
			iblockstate = iblockstate.withProperty(FACING, EnumFacingWallOrXZ.SOUTH);
			break;
		case 4:
			iblockstate = iblockstate.withProperty(FACING, EnumFacingWallOrXZ.NORTH);
			break;
		case 5:
			iblockstate = iblockstate.withProperty(FACING, EnumFacingWallOrXZ.UP_Z);
			break;
		default:
			iblockstate = iblockstate.withProperty(FACING, EnumFacingWallOrXZ.UP_X);
		}

		return iblockstate;
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	public int getMetaFromState(IBlockState state) {
		int i = 0;

		switch ((EnumFacingWallOrXZ) state.getValue(FACING)) {
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
		case UP_Z:
			i = i | 5;
			break;
		case UP_X:
		default:
			break;
		}

		return i;
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		EnumFacingWallOrXZ facing = state.getValue(FACING);
		if (facing.isWall()) {
			return state.withProperty(FACING, EnumFacingWallOrXZ.fromHorizontalFacing(rot.rotate(facing.getFacing())));
		} else {
			if (facing.getFacingAxis().equals(EnumFacing.Axis.X)) {
				switch (rot) {
				case CLOCKWISE_90:
				case COUNTERCLOCKWISE_90:
					return state.withProperty(FACING, EnumFacingWallOrXZ.UP_Z);
				default:
					return state;
				}
			} else {
				switch (rot) {
				case CLOCKWISE_90:
				case COUNTERCLOCKWISE_90:
					return state.withProperty(FACING, EnumFacingWallOrXZ.UP_X);
				default:
					return state;
				}
			}
		}
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		EnumFacingWallOrXZ facing = state.getValue(FACING);
		if (facing.isWall()) {
			return state.withRotation(mirrorIn.toRotation(facing.getFacing()));
		} else {
			return state;
		}
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING });
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		return side == EnumFacing.DOWN ? BlockFaceShape.CENTER_SMALL : BlockFaceShape.UNDEFINED;
	}

}
