package rustic.common.blocks.crops;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustic.common.blocks.BlockBase;
import rustic.common.items.ItemStakeCropSeed;
import rustic.common.tileentity.TileEntityLiquidBarrel;
import rustic.core.Rustic;

public class BlockCropStake extends BlockBase {

	public static final Material CROP_STAKE = new Material(MapColor.WOOD) {
		@Override
		public boolean isSolid() {
			return false;
		}
	};

	protected static final AxisAlignedBB STAKE_AABB = new AxisAlignedBB(0.375F, 0.0F, 0.375F, 0.625F, 1.0F, 0.625F);

	public BlockCropStake() {
		super(CROP_STAKE, "crop_stake");
		setHardness(2F);
		setResistance(5F);
		setCreativeTab(Rustic.farmingTab);
		setSoundType(SoundType.WOOD);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
			EnumFacing side, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (!stack.isEmpty() && stack.getItem() instanceof ItemStakeCropSeed) {
			if (world.getBlockState(pos.down()).getBlock().canSustainPlant(world.getBlockState(pos.down()), world, pos.down(), EnumFacing.UP, ((ItemStakeCropSeed) stack.getItem()).getCrop())) {
				world.setBlockState(pos, ((ItemStakeCropSeed) stack.getItem()).getCropState(), 3);
				player.getHeldItem(hand).shrink(1);
			}
		}
		
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return STAKE_AABB;
	}

}
