package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class CrushingTubRecipeHandler implements IRecipeHandler<CrushingTubRecipeWrapper> {

	@Override
	public String getRecipeCategoryUid(CrushingTubRecipeWrapper recipe) {
		return "rustic.crushing_tub";
	}

	@Override
	public Class<CrushingTubRecipeWrapper> getRecipeClass() {
		return CrushingTubRecipeWrapper.class;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(CrushingTubRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(CrushingTubRecipeWrapper recipe) {
		return true;
	}

}
