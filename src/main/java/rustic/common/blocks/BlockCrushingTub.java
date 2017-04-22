package rustic.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.renderer.CrushingTubRenderer;
import rustic.client.renderer.LiquidBarrelRenderer;
import rustic.common.tileentity.TileEntityBarrel;
import rustic.common.tileentity.TileEntityCrushingTub;
import rustic.common.tileentity.TileEntityLiquidBarrel;

public class BlockCrushingTub extends BlockBase implements ITileEntityProvider {

	protected static final AxisAlignedBB TUB_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D);
	protected static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 1.0D, 0.0625D, 1.0D);
	protected static final AxisAlignedBB SIDE_NORTH_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0D, 0.9375D, 0.5625D,
			0.0625D);
	protected static final AxisAlignedBB SIDE_SOUTH_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.9375D, 0.9375D, 0.5625D,
			1.0D);
	protected static final AxisAlignedBB SIDE_WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0625D, 0.0625D, 0.5625D,
			0.9375D);
	protected static final AxisAlignedBB SIDE_EAST_AABB = new AxisAlignedBB(0.9375D, 0.0D, 0.0625D, 1.0D, 0.5625D,
			0.9375D);

	public BlockCrushingTub() {
		super(Material.WOOD, "crushing_tub");
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
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		((TileEntityCrushingTub) worldIn.getTileEntity(pos)).breakBlock(worldIn, pos, state);
		worldIn.removeTileEntity(pos);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		return ((TileEntityCrushingTub) world.getTileEntity(pos)).activate(world, pos, state, player, hand, side, hitX,
				hitY, hitZ);
	}

	@Override
	public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
		if (entityIn instanceof EntityLivingBase && !worldIn.isRemote) {
			if (worldIn.getTileEntity(pos) instanceof TileEntityCrushingTub) {
				((TileEntityCrushingTub) worldIn.getTileEntity(pos)).crush((EntityLivingBase) entityIn);
			}
		}
		super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return TUB_AABB;
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityCrushingTub();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0,
				new ModelResourceLocation(getRegistryName(), "inventory"));
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCrushingTub.class, new CrushingTubRenderer());
	}

}
