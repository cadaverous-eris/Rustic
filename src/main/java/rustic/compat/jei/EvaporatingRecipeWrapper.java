package rustic.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.EvaporatingBasinRecipe;

public class EvaporatingRecipeWrapper extends BlankRecipeWrapper {

	public EvaporatingBasinRecipe recipe = null;
	
	public EvaporatingRecipeWrapper(EvaporatingBasinRecipe recipe) {
		this.recipe = recipe;
	}
	
	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(FluidStack.class, recipe.getInput());
		ingredients.setOutput(ItemStack.class, recipe.getResult());
	}

}
