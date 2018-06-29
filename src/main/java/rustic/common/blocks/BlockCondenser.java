package rustic.common.blocks;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustic.common.tileentity.TileEntityCondenser;
import rustic.core.Rustic;

public class BlockCondenser extends BlockBase implements ITileEntityProvider {
	
	public static final int GUI_ID = 4;

	public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
	public static final PropertyBool BOTTOM = PropertyBool.create("bottom");
	
	protected static final AxisAlignedBB TOP_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D);
	
	public BlockCondenser() {
		super(Material.ROCK, "condenser");
		setHardness(2F);
		setCreativeTab(Rustic.alchemyTab);
		setSoundType(SoundType.STONE);
		setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(BOTTOM, true));
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		if (!state.getValue(BOTTOM)) {
			return TOP_AABB;
		}
		return super.getBoundingBox(state, source, pos);
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return state.getValue(BOTTOM);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return state.getValue(BOTTOM);
	}
	
	@Override
	public int getLightOpacity(IBlockState state) {
		return state.getValue(BOTTOM) ? super.getLightOpacity(state) : 0;
	}
	
	@Override
	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY,
			float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(BOTTOM, true);
	}
	
	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		return super.canPlaceBlockAt(worldIn, pos) && worldIn.isAirBlock(pos.up());
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		if (state.getValue(BOTTOM)) {
			world.setBlockState(pos.up(), state.withProperty(BOTTOM, false));
		}
	}
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (state.getValue(BOTTOM) && world.getBlockState(pos.up()).getBlock() == this || !state.getValue(BOTTOM) && world.getBlockState(pos.down()).getBlock() == this) {
			if (!world.isRemote && !player.capabilities.isCreativeMode){
				world.spawnEntity(new EntityItem(world,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,new ItemStack(this,1,0)));
			}
		}
		if (state.getValue(BOTTOM)){
			world.setBlockToAir(pos.up());
		}
		else {
			world.setBlockToAir(pos.down());
		}
	}
	
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		return new ArrayList<ItemStack>();
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = this.getDefaultState().withProperty(BOTTOM, (meta & 4) > 0);
		return state.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;

		if (state.getValue(BOTTOM)) {
			meta |= 4;
		}
		meta |= 5 - ((EnumFacing) state.getValue(FACING)).getIndex();

		return meta;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, BOTTOM });
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		if (this.getStateFromMeta(meta).getValue(BOTTOM)) {
			return new TileEntityCondenser(); 
		}
		return null;
	}
	
	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (state.getValue(BOTTOM)) {
			if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileEntityCondenser) {
				((TileEntityCondenser) worldIn.getTileEntity(pos)).breakBlock(worldIn, pos, state);
				worldIn.removeTileEntity(pos);
			}
		}
		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		if (state.getValue(BOTTOM)) {
			if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityCondenser && hasRetorts(world, pos, state)) {
				if (!((TileEntityCondenser) world.getTileEntity(pos)).activate(world, pos, state, player, hand, side, hitX,
						hitY, hitZ)) {
					if (world.isRemote) {
						return true;
					}
					player.openGui(Rustic.instance, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
					return true;
				}
				return true;
			}
		} else if (!state.getValue(BOTTOM)) {
			if (world.getTileEntity(pos.down()) != null && world.getTileEntity(pos.down()) instanceof TileEntityCondenser && hasRetorts(world, pos.down(), state)) {
				if (!((TileEntityCondenser) world.getTileEntity(pos.down())).activate(world, pos.down(), state, player, hand, side, hitX,
						hitY, hitZ)) {
					if (world.isRemote) {
						return true;
					}
					player.openGui(Rustic.instance, GUI_ID, world, pos.getX(), pos.down().getY(), pos.getZ());
					return true;
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean hasRetorts(World world, BlockPos pos, IBlockState state) {
		if (state.getBlock() != this) {
			return false;
		}
		switch (state.getValue(FACING)) {
		case NORTH:
			if (world.getBlockState(pos.east()).getBlock() != ModBlocks.RETORT || world.getBlockState(pos.west()).getBlock() != ModBlocks.RETORT) {
				return false;
			}
			if (world.getBlockState(pos.east()).getValue(BlockRetort.FACING) != EnumFacing.EAST || world.getBlockState(pos.west()).getValue(BlockRetort.FACING) != EnumFacing.WEST) {
				return false;
			}
			break;
		case SOUTH:
			if (world.getBlockState(pos.east()).getBlock() != ModBlocks.RETORT || world.getBlockState(pos.west()).getBlock() != ModBlocks.RETORT) {
				return false;
			}
			if (world.getBlockState(pos.east()).getValue(BlockRetort.FACING) != EnumFacing.EAST || world.getBlockState(pos.west()).getValue(BlockRetort.FACING) != EnumFacing.WEST) {
				return false;
			}
			break;
		case WEST:
			if (world.getBlockState(pos.north()).getBlock() != ModBlocks.RETORT || world.getBlockState(pos.south()).getBlock() != ModBlocks.RETORT) {
				return false;
			}
			if (world.getBlockState(pos.north()).getValue(BlockRetort.FACING) != EnumFacing.NORTH || world.getBlockState(pos.south()).getValue(BlockRetort.FACING) != EnumFacing.SOUTH) {
				return false;
			}
			break;
		case EAST:
			if (world.getBlockState(pos.north()).getBlock() != ModBlocks.RETORT || world.getBlockState(pos.south()).getBlock() != ModBlocks.RETORT) {
				return false;
			}
			if (world.getBlockState(pos.north()).getValue(BlockRetort.FACING) != EnumFacing.NORTH || world.getBlockState(pos.south()).getValue(BlockRetort.FACING) != EnumFacing.SOUTH) {
				return false;
			}
			break;
		default:
			return false;
		}
		return true;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		if (state.getValue(BOTTOM)) {
			return BlockFaceShape.SOLID;
		}
		return BlockFaceShape.UNDEFINED;
	}
	
}
