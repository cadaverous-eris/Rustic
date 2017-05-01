package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import rustic.common.crafting.AdvancedCondenserRecipe;
import rustic.common.crafting.BasicCondenserRecipe;
import rustic.common.crafting.CondenserRecipe;

public class AdvancedAlchemyRecipeWrapper extends BlankRecipeWrapper {

	public CondenserRecipe recipe;
	
	AdvancedAlchemyRecipeWrapper(CondenserRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		if (recipe instanceof BasicCondenserRecipe) {
			ingredients.setInputs(ItemStack.class, recipe.getInputs());
		} else if (recipe instanceof AdvancedCondenserRecipe) {
			List<ItemStack> inputs = new ArrayList<ItemStack>();
			
			if (recipe.getInputs().size() > 0) {
				inputs.add(recipe.getInputs().get(0));
			} else {
				inputs.add(ItemStack.EMPTY);
			}
			if (recipe.getInputs().size() > 1) {
				inputs.add(recipe.getInputs().get(1));
			} else {
				inputs.add(ItemStack.EMPTY);
			}
			if (recipe.getInputs().size() > 2) {
				inputs.add(recipe.getInputs().get(2));
			} else {
				inputs.add(ItemStack.EMPTY);
			}
			
			inputs.add(((AdvancedCondenserRecipe) recipe).getModifier());
			
			ingredients.setInputs(ItemStack.class, inputs);
		}
		
		ingredients.setOutput(ItemStack.class, recipe.getResult());
	}

}
