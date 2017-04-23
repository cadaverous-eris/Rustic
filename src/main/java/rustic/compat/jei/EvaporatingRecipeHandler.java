package rustic.compat.jei;

import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;

public class EvaporatingRecipeHandler implements IRecipeHandler<EvaporatingRecipeWrapper> {

	@Override
	public String getRecipeCategoryUid(EvaporatingRecipeWrapper recipe) {
		return "rustic.evaporating";
	}

	@Override
	public Class<EvaporatingRecipeWrapper> getRecipeClass() {
		return EvaporatingRecipeWrapper.class;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(EvaporatingRecipeWrapper recipe) {
		return recipe;
	}

	@Override
	public boolean isRecipeValid(EvaporatingRecipeWrapper recipe) {
		return true;
	}

}
