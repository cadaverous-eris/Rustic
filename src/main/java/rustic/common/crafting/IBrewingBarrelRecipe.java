package rustic.common.crafting;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.blocks.fluids.FluidBooze;

public interface IBrewingBarrelRecipe {
	public FluidStack getInput();
	public FluidStack getOuput();
	
	public boolean matches(FluidStack in);
	public boolean matches(FluidStack in, FluidStack aux);
	public FluidStack getResult(FluidStack in);
	FluidStack getResult(FluidStack in, FluidStack aux);
}
