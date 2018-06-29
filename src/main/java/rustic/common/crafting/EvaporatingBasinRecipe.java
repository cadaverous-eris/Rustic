package rustic.common.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class EvaporatingBasinRecipe {

	protected FluidStack input;
	protected ItemStack output;
	
	public EvaporatingBasinRecipe(ItemStack out, FluidStack in) {
		this.input = in;
		this.output = out;
	}
	
	public boolean matches(FluidStack in) {
		if (in.containsFluid(this.input)) {
			return true;
		}
		return false;
	}
	
	public ItemStack getResult() {
		return this.output.copy();
	}
	
	public FluidStack getInput() {
		return this.input.copy();
	}
	
}
