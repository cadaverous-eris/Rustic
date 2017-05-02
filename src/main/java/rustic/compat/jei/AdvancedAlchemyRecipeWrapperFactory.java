package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.CondenserRecipe;

public class AdvancedAlchemyRecipeWrapperFactory implements IRecipeWrapperFactory<CondenserRecipe> {

	@Override
	public IRecipeWrapper getRecipeWrapper(CondenserRecipe recipe) {
		return new AdvancedAlchemyRecipeWrapper(recipe);
	}
	
}
