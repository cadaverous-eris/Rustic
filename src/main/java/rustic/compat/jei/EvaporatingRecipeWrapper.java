package rustic.compat.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.EvaporatingBasinRecipe;
import rustic.common.crafting.IEvaporatingBasinRecipe;

public class EvaporatingRecipeWrapper extends BlankRecipeWrapper {

	public IEvaporatingBasinRecipe recipe = null;
	
	public EvaporatingRecipeWrapper(IEvaporatingBasinRecipe recipe) {
		this.recipe = recipe;
	}
	
	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(FluidStack.class, recipe.getInput());
		ingredients.setOutput(ItemStack.class, recipe.getOutput());
	}

}
