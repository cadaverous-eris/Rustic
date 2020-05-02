package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.ICondenserRecipe;

public class SimpleAlchemyRecipeWrapperFactory implements IRecipeWrapperFactory<ICondenserRecipe> {

	@Override
	public IRecipeWrapper getRecipeWrapper(ICondenserRecipe recipe) {
		return new SimpleAlchemyRecipeWrapper(recipe);
	}

}
