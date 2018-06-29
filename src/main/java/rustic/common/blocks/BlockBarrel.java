package rustic.common.blocks;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.tileentity.TileEntityBarrel;
import rustic.core.Rustic;

public class BlockBarrel extends BlockBase implements ITileEntityProvider {
	
	public static final int GUI_ID = 2;
	protected static final AxisAlignedBB BARREL_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1D, 0.875D);

	public BlockBarrel() {
		super(Material.WOOD, "barrel");
		this.setHardness(1.5F);
		setSoundType(SoundType.WOOD);
	}
	
	@Override
	public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param) {
		super.eventReceived(state, worldIn, pos, id, param);
		TileEntity tileentity = worldIn.getTileEntity(pos);
		return tileentity == null ? false : tileentity.receiveClientEvent(id, param);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityBarrel();
	}
	
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		((TileEntityBarrel)worldIn.getTileEntity(pos)).breakBlock(worldIn,pos,state);
		worldIn.removeTileEntity(pos);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (world.isRemote) {
			return true;
		}
		TileEntity te = world.getTileEntity(pos);
		if (!(te instanceof TileEntityBarrel)) {
			return false;
		}
		player.openGui(Rustic.instance, GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return BARREL_AABB;
	}
	
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof TileEntityBarrel) {
                ((TileEntityBarrel)tileentity).setCustomName(stack.getDisplayName());
                ((TileEntityBarrel)tileentity).markDirty();
            }
        }
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing side) {
		return BlockFaceShape.UNDEFINED;
    }
	
}
		
		
		