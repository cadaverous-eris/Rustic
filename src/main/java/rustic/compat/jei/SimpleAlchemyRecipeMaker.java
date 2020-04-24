package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import rustic.common.crafting.BasicCondenserRecipe;
import rustic.common.crafting.CondenserRecipe;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.Recipes;

public class SimpleAlchemyRecipeMaker {
	
	private SimpleAlchemyRecipeMaker() {
		
	}
	
	public static List<ICondenserRecipe> getSimpleAlchemyRecipes(IJeiHelpers helpers) {
		List<ICondenserRecipe> recipes = new ArrayList<ICondenserRecipe>();
		
		for (ICondenserRecipe recipe : Recipes.condenserRecipes) {
			if (recipe.isBasic()) {
				recipes.add(recipe);
			}
		}
		
		return recipes;
	}

}
