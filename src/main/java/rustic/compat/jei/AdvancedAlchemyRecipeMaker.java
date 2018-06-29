package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IJeiHelpers;
import rustic.common.crafting.AdvancedCondenserRecipe;
import rustic.common.crafting.CondenserRecipe;
import rustic.common.crafting.Recipes;

public class AdvancedAlchemyRecipeMaker {

	private AdvancedAlchemyRecipeMaker() {

	}

	public static List<CondenserRecipe> getAlchemyRecipes(IJeiHelpers helpers) {
		List<CondenserRecipe> recipes = new ArrayList<CondenserRecipe>();

		for (CondenserRecipe recipe : Recipes.condenserRecipes) {
			if (recipe instanceof AdvancedCondenserRecipe) {
				recipes.add(recipe);
			}
		}

		return recipes;
	}

}
