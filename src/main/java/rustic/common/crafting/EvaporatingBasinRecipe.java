package rustic.common.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class EvaporatingBasinRecipe implements IEvaporatingBasinRecipe {

	protected FluidStack input;
	protected ItemStack output;
	
	public EvaporatingBasinRecipe(ItemStack out, FluidStack in) {
		this.input = in;
		this.output = out;
	}
	
//	public boolean matches(FluidStack in) {
//		if (in.containsFluid(this.input)) {
//			return true;
//		}
//		return false;
//	}
	
	public FluidStack getInput() {
		return this.input.copy();
	}
	
	public Fluid getFluid() {
		return this.input.getFluid();
	}
	
	public int getAmount() {
		return this.input.amount;
	}
	
	public ItemStack getOutput() {
		return this.output.copy();
	}

	public int getTime() {
		return this.input.amount;
	}
	
}
