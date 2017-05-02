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
	
	public static List<BasicCondenserRecipe> getSimpleAlchemyRecipes(IJeiHelpers helpers) {
		List<BasicCondenserRecipe> recipes = new ArrayList<BasicCondenserRecipe>();
		
		for (CondenserRecipe recipe : Recipes.condenserRecipes) {
			if (recipe instanceof BasicCondenserRecipe) {
				recipes.add((BasicCondenserRecipe) recipe);
			}
		}
		
		return recipes;
	}

}
