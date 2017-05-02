package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.CrushingTubRecipe;

public class CrushingTubRecipeWrapperFactory implements IRecipeWrapperFactory<CrushingTubRecipe> {

	@Override
	public IRecipeWrapper getRecipeWrapper(CrushingTubRecipe recipe) {
		return new CrushingTubRecipeWrapper(recipe);
	}

}
