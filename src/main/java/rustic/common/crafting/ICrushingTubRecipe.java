package rustic.common.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public interface ICrushingTubRecipe {
	public ItemStack getInput();
	
	public boolean matches(ItemStack in);
	public FluidStack getResult();
	public ItemStack getByproduct();
}
