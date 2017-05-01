package rustic.common.items;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.common.blocks.crops.BlockHerbBase;
import rustic.core.Rustic;

public class ItemHerbEdible extends ItemFood implements IPlantable {
	
	private BlockHerbBase herbBlock;
	
	public ItemHerbEdible(BlockHerbBase herbBlock, int hunger, float saturation) {
		super(hunger, saturation, false);
		this.herbBlock = herbBlock;
		setRegistryName(herbBlock.getRegistryName());
		setUnlocalizedName(herbBlock.getUnlocalizedName());
		setCreativeTab(Rustic.alchemyTab);
		GameRegistry.register(this);
	}
	
	public void initModel(){
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName().toString()));
	}

	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack itemstack = player.getHeldItem(hand);
		IBlockState state = worldIn.getBlockState(pos);
		if (facing == EnumFacing.UP && player.canPlayerEdit(pos.offset(facing), facing, itemstack) && state.getBlock().canSustainPlant(state, worldIn, pos, EnumFacing.UP, this) && worldIn.isAirBlock(pos.up())) {
            worldIn.setBlockState(pos.up(), this.herbBlock.getDefaultState());
            itemstack.shrink(1);
            return EnumActionResult.SUCCESS;
        }
		return EnumActionResult.FAIL;
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return this.herbBlock.getPlantType(world, pos);
	}

	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return this.herbBlock.getDefaultState();
	}
	
}
