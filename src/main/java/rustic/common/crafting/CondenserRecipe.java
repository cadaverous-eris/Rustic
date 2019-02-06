package rustic.common.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public abstract class CondenserRecipe {

	ItemStack output;

	List<ItemStack> inputs;

	public CondenserRecipe() {

	}

	public boolean matches(ItemStack modifier, ItemStack[] stacks) {
		if (stacks.length <= 0) {
			return false;
		}
		if (this.inputs == null || this.inputs.size() <= 0) {
			return false;
		}
		List<ItemStack> tempInputs = new ArrayList<ItemStack>(this.inputs);

		for (int i = 0; i < stacks.length; i++) {
			ItemStack stack = stacks[i].copy();
			if (stack.isEmpty()) {
				continue;
			}
			boolean stackNotInput = true;
			for (ItemStack temp : tempInputs) {
				if (temp.getItem().equals(stack.getItem())) {
					if (temp.getMetadata() == stack.getMetadata()) {
						if (temp.hasTagCompound() && stack.hasTagCompound()) {
							if (temp.getTagCompound().equals(stack.getTagCompound())) {
								tempInputs.remove(stack);
								stackNotInput = false;
								break;
							}
						} else if (!temp.hasTagCompound()) {
							tempInputs.remove(temp);
							stackNotInput = false;
							break;
						}
					}
				}
			}
			if (stackNotInput) {
				return false;
			}
			
		}
		return (tempInputs.size() == 0);
	}

	public List<ItemStack> getInputs() {
		return new ArrayList<ItemStack>(this.inputs);
	}

	public ItemStack getResult() {
		return this.output.copy();
	}

}
