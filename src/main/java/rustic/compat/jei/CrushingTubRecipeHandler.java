package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import rustic.common.crafting.CrushingTubRecipe;

public class CrushingTubRecipeHandler implements IRecipeHandler<CrushingTubRecipe> {

	@Override
	public String getRecipeCategoryUid(CrushingTubRecipe recipe) {
		return "rustic.crushing_tub";
	}

	@Override
	public Class getRecipeClass() {
		return CrushingTubRecipe.class;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(CrushingTubRecipe recipe) {
		return new CrushingTubRecipeWrapper(recipe);
	}

	@Override
	public boolean isRecipeValid(CrushingTubRecipe recipe) {
		return true;
	}

}
