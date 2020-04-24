package rustic.common.crafting;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface ICondenserRecipe {
	
	public boolean matches(Fluid fluid, ItemStack modifier, ItemStack bottle, ItemStack[] inputs);
	public boolean isBasic();
	public boolean isAdvanced();
	public FluidStack getFluid();
	public List<ItemStack> getModifiers();
	public List<ItemStack> getBottles();
	public List<List<ItemStack>> getInputs();
	public int getTime();
	public int getModifierConsumption(ItemStack modifier);
	public int getBottleConsumption(ItemStack bottle);
	public int[] getInputConsumption(ItemStack[] inputs);
	public ItemStack getResult();
}
