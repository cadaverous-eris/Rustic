package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.BasicCondenserRecipe;

public class SimpleAlchemyRecipeWrapperFactory implements IRecipeWrapperFactory<BasicCondenserRecipe> {

	@Override
	public IRecipeWrapper getRecipeWrapper(BasicCondenserRecipe recipe) {
		return new SimpleAlchemyRecipeWrapper(recipe);
	}

}
