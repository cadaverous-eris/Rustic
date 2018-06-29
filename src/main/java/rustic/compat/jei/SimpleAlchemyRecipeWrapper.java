package rustic.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import rustic.common.crafting.BasicCondenserRecipe;

public class SimpleAlchemyRecipeWrapper extends BlankRecipeWrapper {

	public BasicCondenserRecipe recipe = null;
	
	public SimpleAlchemyRecipeWrapper(BasicCondenserRecipe recipe) {
		this.recipe = recipe;
	}
	
	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, recipe.getInputs());
		ingredients.setOutput(ItemStack.class, recipe.getResult());
	}

}
