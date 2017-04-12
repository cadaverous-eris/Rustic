package rustic.client.models;

import java.util.List;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import rustic.common.blocks.ModBlocks;
import rustic.common.tileentity.TileEntityLiquidBarrel;

public class LiquidBarrelItemOverrideList extends ItemOverrideList {

	public LiquidBarrelItemOverrideList(List<ItemOverride> overridesIn) {
		super(overridesIn);
	}
	
	@Override
	  public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
		TileEntityLiquidBarrel tank = new TileEntityLiquidBarrel();
	    if (stack != null && stack.getItem().equals(Item.getItemFromBlock(ModBlocks.LIQUID_BARREL)) && stack.hasTagCompound()) {
	    	tank.getTank().readFromNBT(stack.getTagCompound());
	    	int amount = tank.getAmount();
			int capacity = tank.getCapacity();
			Fluid fluid = tank.getFluid();
		    return new LiquidBarrelFilledItemModel(originalModel, amount, capacity, fluid);
	    }
	    return originalModel;
	  }

}
