package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import rustic.common.crafting.BasicCondenserRecipe;
import rustic.common.crafting.CondenserRecipe;
import rustic.common.crafting.Recipes;

public class AdvancedAlchemyRecipeMaker {
	
	private AdvancedAlchemyRecipeMaker() {
		
	}
	
	public static List<AdvancedAlchemyRecipeWrapper> getAlchemyRecipes(IJeiHelpers helpers) {
		List<AdvancedAlchemyRecipeWrapper> recipes = new ArrayList<AdvancedAlchemyRecipeWrapper>();
		
		for (CondenserRecipe recipe : Recipes.condenserRecipes) {
			recipes.add(new AdvancedAlchemyRecipeWrapper(recipe));
		}
		
		return recipes;
	}

}
