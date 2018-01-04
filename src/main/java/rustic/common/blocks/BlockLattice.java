package rustic.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ColorizerFoliage;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.BlockPlanksRustic.EnumType;
import rustic.common.blocks.properties.UnlistedPropertyBool;
import rustic.core.ClientProxy;

public class BlockLattice extends BlockBase implements IColoredBlock {

	public static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(.375f, .375f, .375f, .625f, .625f, .625f);
	public static final AxisAlignedBB NORTH_AABB = new AxisAlignedBB(.4375f, .4375f, .4375f, .5625f, .5625f, 0f);
	public static final AxisAlignedBB EAST_AABB = new AxisAlignedBB(.5625f, .4375f, .4375f, 1f, .5625f, .5625f);
	public static final AxisAlignedBB SOUTH_AABB = new AxisAlignedBB(.4375f, .4375f, 1f, .5625f, .5625f, .5625f);
	public static final AxisAlignedBB WEST_AABB = new AxisAlignedBB(0f, .4375f, .4375f, .4375f, .5625f, .5625f);
	public static final AxisAlignedBB UP_AABB = new AxisAlignedBB(.4375f, .5625f, .4375f, .5625f, 1f, .5625f);
	public static final AxisAlignedBB DOWN_AABB = new AxisAlignedBB(.4375f, 0f, .4375f, .5625f, .4375f, .5625f);

	public static final UnlistedPropertyBool[] CONNECTIONS = new UnlistedPropertyBool[] {
		new UnlistedPropertyBool("down"),
		new UnlistedPropertyBool("up"),
		new UnlistedPropertyBool("north"),
		new UnlistedPropertyBool("south"),
		new UnlistedPropertyBool("west"),
		new UnlistedPropertyBool("east")
	};
	public static final PropertyBool LEAVES = PropertyBool.create("leaves");

	public BlockLattice(Material mat, String name) {
		super(mat, name);
		setHardness(2.0F);
		setSoundType(SoundType.METAL);
	}

	public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox,
			List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean p_185477_7_) {
		IExtendedBlockState	extendedState = (IExtendedBlockState) getExtendedState(state, worldIn, pos);

		addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);
		if (extendedState.getValue(CONNECTIONS[0])) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, DOWN_AABB);
		}
		if (extendedState.getValue(CONNECTIONS[1])) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, UP_AABB);
		}
		if (extendedState.getValue(CONNECTIONS[2])) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, NORTH_AABB);
		}
		if (extendedState.getValue(CONNECTIONS[3])) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, SOUTH_AABB);
		}
		if (extendedState.getValue(CONNECTIONS[4])) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, WEST_AABB);
		}
		if (extendedState.getValue(CONNECTIONS[5])) {
			addCollisionBoxToList(pos, entityBox, collidingBoxes, EAST_AABB);
		}
	}

	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		double x1 = 0.375;
		double y1 = 0.375;
		double z1 = 0.375;
		double x2 = 0.625;
		double y2 = 0.625;
		double z2 = 0.625;
		
		IExtendedBlockState extendedState = (IExtendedBlockState) getExtendedState(state, source, pos);

		if (extendedState.getValue(CONNECTIONS[0])) {
			y1 = 0;
		}
		if (extendedState.getValue(CONNECTIONS[1])) {
			y2 = 1;
		}
		if (extendedState.getValue(CONNECTIONS[2])) {
			z1 = 0;
		}
		if (extendedState.getValue(CONNECTIONS[3])) {
			z2 = 1;
		}
		if (extendedState.getValue(CONNECTIONS[4])) {
			x1 = 0;
		}
		if (extendedState.getValue(CONNECTIONS[5])) {
			x2 = 1;
		}

		return new AxisAlignedBB(x1, y1, z1, x2, y2, z2);
	}
	
	@Override
	public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
		return true;
	}
	
	@Override
	public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
		IExtendedBlockState extendedState = (IExtendedBlockState) state;
		for (int i = 0; i < CONNECTIONS.length; i++) {
			boolean connected = getConnection(world, pos, EnumFacing.getFront(i));
			extendedState = extendedState.withProperty(CONNECTIONS[i], connected);
		}
		return extendedState;
	}
	
	private boolean getConnection(IBlockAccess world, BlockPos pos, EnumFacing facing) {
		IBlockState state = world.getBlockState(pos.offset(facing));
		Block block = state.getBlock();
		return world.isSideSolid(pos.offset(facing), facing.getOpposite(), false) || block instanceof BlockLattice || (block instanceof BlockChain && state.getValue(BlockChain.AXIS) == facing.getAxis()) || (block instanceof BlockLantern && facing.getAxis() == EnumFacing.Axis.Y && state.getValue(BlockLantern.FACING) == facing);
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack heldItem = playerIn.getHeldItem(hand);
		if (Block.getBlockFromItem(heldItem.getItem()) != null && Block.getBlockFromItem(heldItem.getItem()) instanceof BlockLeaves) {
			worldIn.setBlockState(pos, state.withProperty(LEAVES, true), 3);
			playerIn.getHeldItem(hand).shrink(1);
			return true;
		}
		return false;
	}

	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}

	public int getMetaFromState(IBlockState state) {
		return (state.getValue(LEAVES) ? 1 : 0);
	}

	public IBlockState getStateFromMeta(int meta) {
		IBlockState iblockstate = this.getDefaultState();
		if (meta == 0) {
			iblockstate = iblockstate.withProperty(LEAVES, false);
		} else {
			iblockstate = iblockstate.withProperty(LEAVES, true);
		}
		return iblockstate;
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new ExtendedBlockState(this, new IProperty[] { LEAVES }, new IUnlistedProperty[] { CONNECTIONS[0], CONNECTIONS[1], CONNECTIONS[2],
				CONNECTIONS[3], CONNECTIONS[4], CONNECTIONS[5] });
	}
	
	@Override
	public void initModel() {
		super.initModel();
		ClientProxy.addColoredBlock(this);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IBlockColor getBlockColor() {
		return new IBlockColor() {
			@Override
			public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
				if (worldIn != null && pos != null && tintIndex == 1) {
					return BiomeColorHelper.getFoliageColorAtPos(worldIn, pos);
				}
				return ColorizerFoliage.getFoliageColorBasic();
			}
		};
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IItemColor getItemColor() {
		return new IItemColor() {
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				if (!(stack.getItem() instanceof ItemBlock)) return 0xFFFFFF;
				IBlockState state = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
				IBlockColor blockColor = ((IColoredBlock) state.getBlock()).getBlockColor();
				return blockColor == null ? 0xFFFFFF : blockColor.colorMultiplier(state, null, null, tintIndex);
			}
		};
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		return BlockFaceShape.UNDEFINED;
	}

}
