package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.EvaporatingBasinRecipe;

public class EvaporatingRecipeWrapperFactory implements IRecipeWrapperFactory<EvaporatingBasinRecipe> {

	@Override
	public IRecipeWrapper getRecipeWrapper(EvaporatingBasinRecipe recipe) {
		return new EvaporatingRecipeWrapper(recipe);
	}

}
