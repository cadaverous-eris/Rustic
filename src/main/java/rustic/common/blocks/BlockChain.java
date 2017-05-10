package rustic.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.crops.BlockGrapeLeaves;
import rustic.core.Rustic;

public class BlockChain extends BlockBase {

	public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis",
			EnumFacing.Axis.class);
	public static final PropertyBool DANGLE = PropertyBool.create("dangle");
	public static final PropertyBool SUPPORTED = PropertyBool.create("supported");

	protected static final AxisAlignedBB Y_AABB = new AxisAlignedBB(0.4375F, 0.0F, 0.4375F, 0.5625F, 1.0F, 0.5625F);
	protected static final AxisAlignedBB X_AABB = new AxisAlignedBB(0.0, 0.4375F, 0.4375F, 1.0F, 0.5625F, 0.5625F);
	protected static final AxisAlignedBB Z_AABB = new AxisAlignedBB(0.4375F, 0.4375F, 0.0F, 0.5625F, 0.5625F, 1.0F);
	protected static final AxisAlignedBB X_DANGLE_AABB = new AxisAlignedBB(0.0, 0.0F, 0.4375F, 1.0F, 0.5625F,
			0.5625F);
	protected static final AxisAlignedBB Z_DANGLE_AABB = new AxisAlignedBB(0.4375F, 0.0F, 0.0F, 0.5625F, 0.5625F,
			1.0F);

	public BlockChain() {
		super(Material.IRON, "chain");
		this.setHardness(1F);
		this.setCreativeTab(Rustic.decorTab);
		setSoundType(SoundType.METAL);
		this.setDefaultState(this.blockState.getBaseState().withProperty(DANGLE, false).withProperty(AXIS, EnumFacing.Axis.Y).withProperty(SUPPORTED, false));
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (!canPlaceBlockOnSide(world, pos.offset(side), side) && stack.getItem() == Item.getItemFromBlock(this)) {
			int yOffset = 1;
			while (yOffset < 64 && world.getBlockState(pos.down(yOffset)).getBlock() == this) {
				if (world.getBlockState(pos.down(yOffset)).getValue(AXIS) != EnumFacing.Axis.Y) {
					return false;
				}
				yOffset++;
			}
			if (canPlaceBlockAt(world, pos.down(yOffset))) {
				world.setBlockState(pos.down(yOffset), state.withProperty(AXIS, EnumFacing.Axis.Y), 3);
				player.getHeldItem(hand).shrink(1);
				SoundType soundType = getSoundType(state, world, pos, player);
				world.playSound(pos.getX(), pos.getY() - yOffset, pos.getZ(), soundType.getPlaceSound(),
						SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F,
						false);
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return true;
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state.withProperty(AXIS, EnumFacing.Axis.Y).withProperty(SUPPORTED, false));
	}
	
	@Override
	public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
		BlockPos blockPos = pos.offset(side.getOpposite());
		if (side == EnumFacing.UP && world.getBlockState(pos.up()).getBlock() != this) {
			return false;
		}
		if (world.getBlockState(blockPos).getBlock() == ModBlocks.CHAIN
				&& world.getBlockState(blockPos).getValue(AXIS) == side.getAxis()) {
			return true;
		}
		if (world.getBlockState(blockPos).getBlock() == ModBlocks.IRON_LATTICE) {
			return true;
		}
		if (world.isSideSolid(blockPos, side)) {
			return true;
		}

		return false;
	}
	
	protected void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!this.canBlockStay(worldIn, pos, state)) {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			SoundType soundType = getSoundType(state, worldIn, pos, null);
			worldIn.playSound(pos.getX(), pos.getY(), pos.getZ(), soundType.getBreakSound(), SoundCategory.BLOCKS,
					(soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F, true);
		}
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
		boolean supported = isSupported(state, worldIn, pos);
		if (supported != state.getValue(SUPPORTED)) {
			worldIn.setBlockState(pos, state.withProperty(SUPPORTED, supported), 3);
		}
		this.checkAndDropBlock(worldIn, pos, state);
	}
	
	public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
		boolean canStay = state.getValue(SUPPORTED);
		if (!canStay) {
			boolean flag = false;
			boolean flag1 = false;
			if (state.getValue(AXIS) == EnumFacing.Axis.Y) {
				if (world.getBlockState(pos.up()).getBlock() instanceof BlockChain) {
					canStay = true;
				}
			} else if (state.getValue(AXIS) == EnumFacing.Axis.Z) {
				IBlockState state1 = world.getBlockState(pos.north());
				if (state1.getBlock() == ModBlocks.CHAIN
						&& state1.getValue(AXIS) == EnumFacing.Axis.Z) {
					flag = true;
				}
				state1 = world.getBlockState(pos.south());
				if (state1.getBlock() == ModBlocks.CHAIN
						&& state1.getValue(AXIS) == EnumFacing.Axis.Z) {
					flag1 = true;
				}
				canStay = flag && flag1;
			} else if (state.getValue(AXIS) == EnumFacing.Axis.X) {
				IBlockState state1 = world.getBlockState(pos.west());
				if (state1.getBlock() == ModBlocks.CHAIN
						&& state1.getValue(AXIS) == EnumFacing.Axis.X) {
					flag = true;
				}
				state1 = world.getBlockState(pos.east());
				if (state1.getBlock() == ModBlocks.CHAIN
						&& state1.getValue(AXIS) == EnumFacing.Axis.X) {
					flag1 = true;
				}
				canStay = flag && flag1;
			}
		}
		return canStay;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { AXIS, DANGLE, SUPPORTED });
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Y;
		boolean supported = false;
		int i = meta & 3;

		if (i == 0) {
			enumfacing$axis = EnumFacing.Axis.Y;
		} else if (i == 1) {
			enumfacing$axis = EnumFacing.Axis.X;
		} else if (i == 2) {
			enumfacing$axis = EnumFacing.Axis.Z;
		}

		i = meta & 4;

		if (i > 0) {
			supported = true;
		}

		return this.getDefaultState().withProperty(AXIS, enumfacing$axis).withProperty(SUPPORTED, true);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		EnumFacing.Axis enumfacing$axis = (EnumFacing.Axis) state.getValue(AXIS);

		if (enumfacing$axis == EnumFacing.Axis.X) {
			i = 1;
		} else if (enumfacing$axis == EnumFacing.Axis.Z) {
			i = 2;
		}

		if (state.getValue(SUPPORTED)) {
			i |= 4;
		}

		return i;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		if (state.getValue(AXIS) != EnumFacing.Axis.Y) {
			if (world.getBlockState(pos.down()).getBlock() instanceof BlockChain
					&& world.getBlockState(pos.down()).getValue(AXIS) == EnumFacing.Axis.Y) {
				return state.withProperty(DANGLE, true);
			}
		}
		return state.withProperty(DANGLE, false);
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		boolean supported = false;
		BlockPos blockPos = pos.offset(facing.getOpposite());
		if (worldIn.isSideSolid(blockPos, facing)) {
			supported = true;
		} else if (worldIn.getBlockState(blockPos).getBlock() == ModBlocks.IRON_LATTICE) {
			supported = true;
		}
		blockPos = pos.offset(facing);
		if (worldIn.isSideSolid(blockPos, facing.getOpposite())) {
			supported = true;
		} else if (worldIn.getBlockState(blockPos).getBlock() == ModBlocks.IRON_LATTICE) {
			supported = true;
		}
		return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer)
				.withProperty(AXIS, facing.getAxis()).withProperty(SUPPORTED, supported);
	}
	
	public boolean isSupported(IBlockState state, World world, BlockPos pos) {
		boolean supported = false;

		if (state.getValue(AXIS) == EnumFacing.Axis.Y) {
			supported = world.isSideSolid(pos.up(), EnumFacing.DOWN) || world.getBlockState(pos.up()).getBlock() instanceof BlockLattice;
		} else if (state.getValue(AXIS) == EnumFacing.Axis.Z) {
			if (world.isSideSolid(pos.north(), EnumFacing.SOUTH)
					|| world.getBlockState(pos.north()).getBlock() == ModBlocks.IRON_LATTICE) {
				supported = true;
			}
			if (world.isSideSolid(pos.south(), EnumFacing.NORTH)
					|| world.getBlockState(pos.south()).getBlock() == ModBlocks.IRON_LATTICE) {
				supported = true;
			}
		} else if (state.getValue(AXIS) == EnumFacing.Axis.X) {
			if (world.isSideSolid(pos.west(), EnumFacing.EAST)
					|| world.getBlockState(pos.west()).getBlock() == ModBlocks.IRON_LATTICE) {
				supported = true;
			}
			if (world.isSideSolid(pos.east(), EnumFacing.WEST)
					|| world.getBlockState(pos.east()).getBlock() == ModBlocks.IRON_LATTICE) {
				supported = true;
			}
		}

		return supported;
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomStateMapper(this, (new StateMap.Builder()).ignore(new IProperty[] { SUPPORTED }).build());
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
				new ModelResourceLocation(getRegistryName().toString(), "inventory"));
	}
	
	@Override
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
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		state = getActualState(state, source, pos);
		switch (state.getValue(AXIS)) {
		case Y:
			return Y_AABB;
		case X:
			if (state.getValue(DANGLE)) {
				return X_DANGLE_AABB;
			}
			return X_AABB;
		case Z:
			if (state.getValue(DANGLE)) {
				return Z_DANGLE_AABB;
			}
			return Z_AABB;
		}
		return Y_AABB;
	}

}
