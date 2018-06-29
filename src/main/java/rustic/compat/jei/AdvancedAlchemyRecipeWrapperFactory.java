package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.AdvancedCondenserRecipe;

public class AdvancedAlchemyRecipeWrapperFactory implements IRecipeWrapperFactory<AdvancedCondenserRecipe> {

	@Override
	public IRecipeWrapper getRecipeWrapper(AdvancedCondenserRecipe recipe) {
		return new AdvancedAlchemyRecipeWrapper(recipe);
	}
	
}
