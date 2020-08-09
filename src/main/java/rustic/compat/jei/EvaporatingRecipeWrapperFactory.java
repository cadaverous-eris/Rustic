package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.EvaporatingBasinRecipe;
import rustic.common.crafting.IEvaporatingBasinRecipe;

public class EvaporatingRecipeWrapperFactory implements IRecipeWrapperFactory<IEvaporatingBasinRecipe> {

	@Override
	public IRecipeWrapper getRecipeWrapper(IEvaporatingBasinRecipe recipe) {
		return new EvaporatingRecipeWrapper(recipe);
	}

}
