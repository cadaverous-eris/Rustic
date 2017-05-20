package rustic.common.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.renderer.CabinetRenderer;
import rustic.common.tileentity.TileEntityBarrel;
import rustic.common.tileentity.TileEntityCabinet;
import rustic.core.Rustic;

public class BlockCabinet extends BlockRotatable implements ITileEntityProvider {

	public static final PropertyBool MIRROR = PropertyBool.create("mirror");
	public static final PropertyBool TOP = PropertyBool.create("top");
	public static final PropertyBool BOTTOM = PropertyBool.create("bottom");
	public static final int GUI_ID = 3;

	public BlockCabinet() {
		super(Material.WOOD, "cabinet");
		setHardness(2.5F);
		setSoundType(SoundType.WOOD);
		this.setDefaultState(this.getDefaultState().withProperty(MIRROR, false).withProperty(TOP, false).withProperty(BOTTOM, false));
	}

	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}

	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}

	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		}
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityCabinet)) {
			return false;
		}
		if (facing == state.getValue(FACING)) {
			player.openGui(Rustic.instance, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
			return true;
		}
		return false;
	}

	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		((TileEntityCabinet) worldIn.getTileEntity(pos)).breakBlock(worldIn, pos, state);
		worldIn.removeTileEntity(pos);

		if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockCabinet && worldIn.getBlockState(pos.up()).getValue(TOP)) {
			worldIn.setBlockState(pos.up(), worldIn.getBlockState(pos.up()).withProperty(TOP, false));
		}

		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCabinet();
	}

	public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		EnumFacing f = placer.getHorizontalFacing().getOpposite();
		boolean m = false;
		boolean t = false;

		if (f.equals(EnumFacing.NORTH)) {
			m = hitX >= 0.5;
		} else if (f.equals(EnumFacing.SOUTH)) {
			m = hitX < 0.5;
		} else if (f.equals(EnumFacing.EAST)) {
			m = hitZ >= 0.5;
		} else {
			m = hitZ < 0.5;
		}

		t = worldIn.getBlockState(pos.down()).getBlock() instanceof BlockCabinet && !worldIn.getBlockState(pos.down()).getValue(TOP) && worldIn.getBlockState(pos.down()).getValue(MIRROR) == m && worldIn.getBlockState(pos.down()).getValue(FACING).equals(f);

		if (t && worldIn.getTileEntity(pos.down()) instanceof TileEntityCabinet) {
			((TileEntityCabinet) worldIn.getTileEntity(pos.down())).doubleCabinetHandler = null;
		}

		return this.getDefaultState().withProperty(FACING, f).withProperty(MIRROR, m).withProperty(TOP, t);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);

			if (tileentity instanceof TileEntityCabinet) {
				((TileEntityCabinet) tileentity).setCustomName(stack.getDisplayName());
				((TileEntityCabinet) tileentity).markDirty();
			}
		}
	}
	
	

	public IBlockState getStateFromMeta(int meta) {
		IBlockState state = this.getDefaultState().withProperty(MIRROR, (meta & 4) > 0);
		state = state.withProperty(TOP, (meta & 8) > 0);
		return state.withProperty(FACING, EnumFacing.getFront(5 - (meta & 3)));
	}

	public int getMetaFromState(IBlockState state) {
		int meta = 0;

		if (state.getValue(TOP)) {
			meta |= 8;
		}
		if (state.getValue(MIRROR)) {
			meta |= 4;
		}
		meta |= 5 - ((EnumFacing) state.getValue(FACING)).getIndex();

		return meta;
	}

	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		state = state.withProperty(BOTTOM, !state.getValue(TOP) && worldIn.getBlockState(pos.up()).getBlock() instanceof BlockCabinet && worldIn.getBlockState(pos.up()).getValue(TOP));
		return state;
	}

	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, MIRROR, TOP, BOTTOM });
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCabinet.class, new CabinetRenderer());
	}

}
