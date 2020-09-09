package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;
import rustic.common.crafting.CrushingTubRecipe;
import rustic.common.crafting.ICrushingTubRecipe;

public class CrushingTubRecipeWrapperFactory implements IRecipeWrapperFactory<ICrushingTubRecipe> {

	@Override
	public IRecipeWrapper getRecipeWrapper(ICrushingTubRecipe recipe) {
		return new CrushingTubRecipeWrapper(recipe);
	}

}
