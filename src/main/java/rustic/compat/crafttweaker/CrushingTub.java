package rustic.compat.crafttweaker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import crafttweaker.IAction;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.CrushingTubRecipe;
import rustic.common.crafting.Recipes;
import rustic.compat.jei.CrushingTubRecipeWrapper;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.rustic.CrushingTub")
public class CrushingTub {

	@ZenMethod
	public static void addRecipe(ILiquidStack output, IItemStack byproduct, IItemStack input) {
		CrushingTubRecipe r = new CrushingTubRecipe(CraftTweakerHelper.toFluidStack(output),
				CraftTweakerHelper.toStack(input), CraftTweakerHelper.toStack(byproduct));
		CraftTweakerAPI.apply(new Add(r));
	}

	private static class Add implements IAction {
		private final CrushingTubRecipe recipe;

		public Add(CrushingTubRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void apply() {
			Recipes.crushingTubRecipes.add(recipe);
		}

		@Override
		public String describe() {
			return "Adding Crushing Tub Recipe for Fluid " + recipe.getResult().getLocalizedName();
		}

	}

	@ZenMethod
	public static void removeRecipe(ILiquidStack output, IItemStack input) {
		if (CraftTweakerHelper.toFluidStack(output) != null && !CraftTweakerHelper.toStack(input).isEmpty())
			CraftTweakerAPI
					.apply(new RemoveFluid(CraftTweakerHelper.toFluidStack(output), CraftTweakerHelper.toStack(input)));
	}

	private static class RemoveFluid implements IAction {
		private final FluidStack output;
		private final ItemStack input;

		public RemoveFluid(FluidStack output, ItemStack input) {
			this.output = output;
			this.input = input;
		}

		@Override
		public void apply() {
			Iterator<CrushingTubRecipe> it = Recipes.crushingTubRecipes.iterator();
			while (it.hasNext()) {
				CrushingTubRecipe r = it.next();
				if (r != null && r.getResult() != null && r.getResult().isFluidEqual(output)) {
					if (r.getInput().isItemEqual(input)) {
						it.remove();
					}
				}
			}
		}

		@Override
		public String describe() {
			return "Removing Crushing Tub Recipes for Fluid " + output.getLocalizedName();
		}

	}

}
