package rustic.common.blocks.crops;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.common.blocks.BlockBase;
import rustic.common.blocks.BlockRope;
import rustic.common.blocks.ModBlocks;

public class BlockStakeTied extends BlockBase {
	
	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool EAST = PropertyBool.create("east");
	
	protected static final AxisAlignedBB KNOT_AABB = new AxisAlignedBB(0.3125F, 0.0F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
	protected static final AxisAlignedBB STAKE_AABB = new AxisAlignedBB(0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);
	
	public BlockStakeTied() {
		super(Material.WOOD, "stake_tied", false);
		setSoundType(SoundType.WOOD);
		setHardness(1F);
		setResistance(5F);
		setDefaultState(this.blockState.getBaseState().withProperty(NORTH,  false).withProperty(WEST, false).withProperty(SOUTH, false).withProperty(EAST,  false));
		GameRegistry.register(this);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return KNOT_AABB;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return STAKE_AABB;
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
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		super.breakBlock(world, pos, state);
		world.setBlockState(pos, ModBlocks.CROP_STAKE.getDefaultState(), 3);
		dropBlockAsItem(world, pos, state, 0);
		world.scheduleUpdate(pos, ModBlocks.CROP_STAKE, 1);
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		stacks.add(new ItemStack(ModBlocks.ROPE, 1));
		return stacks;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { NORTH, WEST, SOUTH, EAST });
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
		boolean n = false;
		boolean w = false;
		boolean s = false;
		boolean e = false;
		
		IBlockState state1 = world.getBlockState(pos.north());
		if (state1.getBlock() instanceof BlockRope && state1.getValue(BlockRope.AXIS) == EnumFacing.Axis.Z) {
			n = true;
		}
		if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(BlockGrapeLeaves.AXIS) == EnumFacing.Axis.Z) {
			n = true;
		}
		if (state1.getBlock() == this) {
			n = true;
		}
		state1 = world.getBlockState(pos.west());
		if (state1.getBlock() instanceof BlockRope && state1.getValue(BlockRope.AXIS) == EnumFacing.Axis.X) {
			w = true;
		}
		if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(BlockGrapeLeaves.AXIS) == EnumFacing.Axis.X) {
			w = true;
		}
		if (state1.getBlock() == this) {
			w = true;
		}
		state1 = world.getBlockState(pos.south());
		if (state1.getBlock() instanceof BlockRope && state1.getValue(BlockRope.AXIS) == EnumFacing.Axis.Z) {
			s = true;
		}
		if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(BlockGrapeLeaves.AXIS) == EnumFacing.Axis.Z) {
			s = true;
		}
		if (state1.getBlock() == this) {
			s = true;
		}
		state1 = world.getBlockState(pos.east());
		if (state1.getBlock() instanceof BlockRope && state1.getValue(BlockRope.AXIS) == EnumFacing.Axis.X) {
			e = true;
		}
		if (state1.getBlock() == ModBlocks.GRAPE_LEAVES && state1.getValue(BlockGrapeLeaves.AXIS) == EnumFacing.Axis.X) {
			e = true;
		}
		if (state1.getBlock() == this) {
			e = true;
		}
		
		return this.getDefaultState().withProperty(NORTH, n).withProperty(WEST, w).withProperty(SOUTH, s).withProperty(EAST, e);
	}
	
}
