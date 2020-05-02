package rustic.compat.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import rustic.common.crafting.ICondenserRecipe;

public class AdvancedAlchemyRecipeWrapper extends BlankRecipeWrapper {

	public ICondenserRecipe recipe;
	
	AdvancedAlchemyRecipeWrapper(ICondenserRecipe recipe) {
		this.recipe = recipe;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		List<List<ItemStack>> inputs = new ArrayList<>();
		// Bottle
		inputs.add(recipe.getBottles());
		// Modifier
		if (recipe.getModifiers() != null) {
			inputs.add(recipe.getModifiers());
		} else {
			inputs.add(Collections.singletonList(ItemStack.EMPTY));
		}
		// Ingredients
		inputs.addAll(recipe.getInputs());
		ingredients.setInputLists(VanillaTypes.ITEM, inputs);
		ingredients.setInput(VanillaTypes.FLUID, recipe.getFluid());
		ingredients.setOutput(VanillaTypes.ITEM, recipe.getResult());
	}

}
