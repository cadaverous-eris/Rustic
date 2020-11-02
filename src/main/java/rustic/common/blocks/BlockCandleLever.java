package rustic.common.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockButton;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCandleLever extends BlockCandle {
	
	public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
		public boolean apply(@Nullable EnumFacing p_apply_1_) {
			return p_apply_1_ != EnumFacing.DOWN;
		}
	});
	
	public BlockCandleLever() {
		this("candle_lever");
	}
	
	public BlockCandleLever(String name) {
		this(Material.CIRCUITS, name, true);
	}
	
	public BlockCandleLever(Material mat, String name, boolean register) {
		super(mat, name, register);
		this.setHardness(1F);
		this.setLightLevel(1.0F);
		this.setTickRandomly(true);
		this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP).withProperty(BlockButton.POWERED, Boolean.valueOf(false)));
		setSoundType(SoundType.METAL);
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
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta & 7).withProperty(BlockButton.POWERED, Boolean.valueOf((meta & 8) > 0));
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	public int getMetaFromState(IBlockState state) {
		int i = super.getMetaFromState(state);
		
		if (((Boolean)state.getValue(BlockButton.POWERED)).booleanValue()) {
			i |= 8;
		}
		
		return i;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] { FACING, BlockButton.POWERED });
	}
	
	
	public int tickRate(World worldIn) {
		return 80; // 4 seconds
	}
	
	
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (((Boolean)state.getValue(BlockButton.POWERED)).booleanValue()) {
			return true;
		}
		worldIn.setBlockState(pos, state.withProperty(BlockButton.POWERED, Boolean.valueOf(true)), 3);
		worldIn.markBlockRangeForRenderUpdate(pos, pos);
		playClickSound(playerIn, worldIn, pos);
		notifyNeighbors(worldIn, pos, (EnumFacing)state.getValue(FACING));
		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
		return true;
	}
	
	private void notifyNeighbors(World worldIn, BlockPos pos, EnumFacing facing) {
		worldIn.notifyNeighborsOfStateChange(pos, this, false);
		worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this, false);
	}
	
	protected void playClickSound(@Nullable EntityPlayer player, World worldIn, BlockPos pos) {
		worldIn.playSound(player, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON, SoundCategory.BLOCKS, 0.3F, 0.6F);
	}
	
	protected void playReleaseSound(World worldIn, BlockPos pos) {
		worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundCategory.BLOCKS, 0.3F, 0.5F);
	}
	
	
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (((Boolean)state.getValue(BlockButton.POWERED)).booleanValue()) {
			notifyNeighbors(worldIn, pos, (EnumFacing)state.getValue(FACING));
		}
		
		super.breakBlock(worldIn, pos, state);
	}
	
	public int getWeakPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		return ((Boolean)blockState.getValue(BlockButton.POWERED)).booleanValue() ? 15 : 0;
	}
	
	public int getStrongPower(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		if (!((Boolean)blockState.getValue(BlockButton.POWERED)).booleanValue()) {
			return 0;
		}
		return blockState.getValue(FACING) == side ? 15 : 0;
	}
	
	public boolean canProvidePower(IBlockState state) {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		EnumFacing enumfacing = (EnumFacing) stateIn.getValue(FACING);
		boolean pulled = stateIn.getValue(BlockButton.POWERED);
		double x = (double) pos.getX() + 0.5D;
		double y = (double) pos.getY() + 0.7D;
		double z = (double) pos.getZ() + 0.5D;
		//double d3 = 0.22D;
		//double d4 = 0.27D;

		if (enumfacing.getAxis().isHorizontal()) {
			EnumFacing enumfacing1 = enumfacing.getOpposite();
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL,
				x + 0.27D * (double) enumfacing1.getFrontOffsetX() * (pulled ? 0.0 : 1.0),
				y + 0.25D + (pulled ? -0.125 : 0.0),
				z + 0.27D * (double) enumfacing1.getFrontOffsetZ() * (pulled ? 0.0 : 1.0),
				0.0D, 0.0D, 0.0D, new int[0]
			);
			worldIn.spawnParticle(EnumParticleTypes.FLAME,
				x + 0.27D * (double) enumfacing1.getFrontOffsetX() * (pulled ? 0.0 : 1.0),
				y + 0.25D + (pulled ? -0.125 : 0.0),
				z + 0.27D * (double) enumfacing1.getFrontOffsetZ() * (pulled ? 0.0 : 1.0),
				0.0D, 0.0D, 0.0D, new int[0]
			);
		} else {
			worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y + 0.33D + (pulled ? -0.125 : 0.0), z + (pulled ? -0.35 : 0.0), 0.0D, 0.0D, 0.0D, new int[0]);
			worldIn.spawnParticle(EnumParticleTypes.FLAME, x, y + 0.33D + (pulled ? -0.125 : 0.0), z + (pulled ? -0.35 : 0.0), 0.0D, 0.0D, 0.0D, new int[0]);
		}
	}
	
	public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) { }
	
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (!worldIn.isRemote) {
			if (((Boolean)state.getValue(BlockButton.POWERED)).booleanValue()) {
				worldIn.setBlockState(pos, state.withProperty(BlockButton.POWERED, Boolean.valueOf(false)));
				notifyNeighbors(worldIn, pos, (EnumFacing)state.getValue(FACING));
				playReleaseSound(worldIn, pos);
				worldIn.markBlockRangeForRenderUpdate(pos, pos);
			}
		}
	}
	
}
