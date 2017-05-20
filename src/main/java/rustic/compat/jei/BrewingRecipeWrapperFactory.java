package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.BrewingBarrelRecipe;

public class BrewingRecipeWrapperFactory implements IRecipeWrapperFactory<BrewingBarrelRecipe> {

	@Override
	public IRecipeWrapper getRecipeWrapper(BrewingBarrelRecipe recipe) {
		return new BrewingRecipeWrapper(recipe);
	}

}
