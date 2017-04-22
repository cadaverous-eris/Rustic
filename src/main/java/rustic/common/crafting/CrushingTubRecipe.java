package rustic.common.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import scala.actors.threadpool.Arrays;

public class CrushingTubRecipe {

	protected ItemStack input;
	protected List<ItemStack> byproducts;
	protected FluidStack output;
	
	public CrushingTubRecipe(ItemStack in, FluidStack out) {
		this(in, out, new ArrayList<ItemStack>());
	}
	
	public CrushingTubRecipe(ItemStack in, FluidStack out, List<ItemStack> by) {
		this.input = in;
		this.output = out;
		this.byproducts = by;
	}
	
	public CrushingTubRecipe(ItemStack in, FluidStack out, ItemStack... by) {
		this.input = in;
		this.output = out;
		this.byproducts = Arrays.asList(by);
	}
	
	public boolean matches(ItemStack in) {
		return (in.getItem().equals(this.input.getItem()) && in.getMetadata() == this.input.getMetadata()) && ItemStack.areItemStackTagsEqual(in, this.input);
	}
	
	public FluidStack getResult() {
		return this.output;
	}
	
	public List<ItemStack> getByproducts() {
		return this.byproducts;
	}
	
}
