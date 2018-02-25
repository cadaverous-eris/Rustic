package rustic.common.crafting;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.Config;
import rustic.common.blocks.fluids.FluidBooze;

public class BrewingBarrelRecipe {
	
	private static final Random rand = new Random();
	
	protected final FluidStack input;
	protected final FluidStack output;
	
	public BrewingBarrelRecipe(FluidStack output, FluidStack input) {
		this.output = output;
		this.input = input;
	}
	
	public boolean matches(FluidStack in) {
		if (input != null && input.getFluid() != null && in != null && in.getFluid() != null) {
			return (in.getFluid().getName().equals(input.getFluid().getName()));
		}
		return false;
	}
	
	public boolean matches(FluidStack in, FluidStack aux) {
		if (output != null && output.getFluid() != null && aux != null && aux.getFluid() != null) {
			return matches(in) && (aux.getFluid() == output.getFluid());
		}
		if (aux == null) {
			return matches(in);
		}
		return false;
	}
	
	public FluidStack getResult(FluidStack in) {
		if (matches(in) && output != null && output.getFluid() != null) {
			FluidStack out = output.copy();
			if (output.getFluid() instanceof FluidBooze) {
				if (out.tag == null) {
					out.tag = new NBTTagCompound();
				}
				out.tag.setFloat(FluidBooze.QUALITY_NBT_KEY, ((5 + rand.nextInt(71)) / 100F));
			}
			return out;
		}
		return null;
	}
	
	public FluidStack getResult(FluidStack in, FluidStack aux) {
		if (aux == null || aux.getFluid() == null || !matches(in, aux) || !(aux.getFluid() instanceof FluidBooze)) {
			return getResult(in);
		}
		if (matches(in, aux) && output != null && output.getFluid() != null) {
			FluidStack out = output.copy();
			if (output.getFluid() instanceof FluidBooze && aux.tag != null && aux.tag.hasKey(FluidBooze.QUALITY_NBT_KEY)) {
				float auxQuality = aux.tag.getFloat(FluidBooze.QUALITY_NBT_KEY);
				if (Config.MAX_BREW_QUALITY_CHANGE < Config.MIN_BREW_QUALITY_CHANGE) {
					Config.MAX_BREW_QUALITY_CHANGE = Config.MIN_BREW_QUALITY_CHANGE;
				}
				int brewQualityChange = rand.nextInt(Math.min((Config.MAX_BREW_QUALITY_CHANGE - Config.MIN_BREW_QUALITY_CHANGE) + 1, 1)) + Config.MIN_BREW_QUALITY_CHANGE;
				float quality = Math.max(Math.min(((brewQualityChange + (int) (100 * auxQuality)) / 100F), 1), 0);
				if (out.tag == null) {
					out.tag = new NBTTagCompound();
				}
				out.tag.setFloat(FluidBooze.QUALITY_NBT_KEY, quality);
			}
			return out;
		}
		return null;
	}
	
	public FluidStack getInput() {
		return input.copy();
	}

}
