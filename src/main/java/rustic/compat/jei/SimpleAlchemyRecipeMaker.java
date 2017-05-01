package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import rustic.common.crafting.BasicCondenserRecipe;
import rustic.common.crafting.CondenserRecipe;
import rustic.common.crafting.Recipes;

public class SimpleAlchemyRecipeMaker {
	
	private SimpleAlchemyRecipeMaker() {
		
	}
	
	public static List<SimpleAlchemyRecipeWrapper> getSimpleAlchemyRecipes(IJeiHelpers helpers) {
		List<SimpleAlchemyRecipeWrapper> recipes = new ArrayList<SimpleAlchemyRecipeWrapper>();
		
		for (CondenserRecipe recipe : Recipes.condenserRecipes) {
			if (recipe instanceof BasicCondenserRecipe) {
				recipes.add(new SimpleAlchemyRecipeWrapper((BasicCondenserRecipe) recipe));
			}
		}
		
		return recipes;
	}

}
