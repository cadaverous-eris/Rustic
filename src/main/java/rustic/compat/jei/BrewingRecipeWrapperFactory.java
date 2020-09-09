package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.BrewingBarrelRecipe;
import rustic.common.crafting.IBrewingBarrelRecipe;

public class BrewingRecipeWrapperFactory implements IRecipeWrapperFactory<IBrewingBarrelRecipe> {

	@Override
	public IRecipeWrapper getRecipeWrapper(IBrewingBarrelRecipe recipe) {
		return new BrewingRecipeWrapper(recipe);
	}

}
