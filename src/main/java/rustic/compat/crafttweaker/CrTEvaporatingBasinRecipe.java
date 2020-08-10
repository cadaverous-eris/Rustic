package rustic.compat.crafttweaker;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.IEvaporatingBasinRecipe;

public class CrTEvaporatingBasinRecipe implements IEvaporatingBasinRecipe {
	
	protected FluidStack input;
	protected ItemStack output;
	protected int time;
	
	public CrTEvaporatingBasinRecipe(ItemStack out, FluidStack in, int time) {
		this.output = out;
		this.input = in;
		this.time = time;
	}

	@Override
	public Fluid getFluid() {
		return this.input.getFluid();
	}

	@Override
	public int getAmount() {
		return this.input.amount;
	}
	
	@Override
	public FluidStack getInput() {
		return this.input.copy();
	}
	
	@Override
	public ItemStack getOutput() {
		return this.output.copy();
	}

	@Override
	public int getTime() {
		return this.time;
	}

}
