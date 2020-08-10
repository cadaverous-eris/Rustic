package rustic.common.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public interface IEvaporatingBasinRecipe {
	Fluid getFluid();
	int getAmount();
	FluidStack getInput();
	ItemStack getOutput();
	int getTime();
}
