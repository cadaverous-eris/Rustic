package rustic.compat.crafttweaker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
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
		MineTweakerAPI.apply(new Add(r));
	}

	private static class Add implements IUndoableAction {
		private final CrushingTubRecipe recipe;

		public Add(CrushingTubRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void apply() {
			Recipes.crushingTubRecipes.add(recipe);
			MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(new CrushingTubRecipeWrapper(recipe));
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			Recipes.crushingTubRecipes.remove(recipe);
			MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(new CrushingTubRecipeWrapper(recipe));
		}

		@Override
		public String describe() {
			return "Adding Crushing Tub Recipe for Fluid " + recipe.getResult().getLocalizedName();
		}

		@Override
		public String describeUndo() {
			return "Removing Crushing Tub Recipe for Fluid " + recipe.getResult().getLocalizedName();
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}

	@ZenMethod
	public static void removeRecipe(ILiquidStack output, IItemStack input) {
		if (CraftTweakerHelper.toFluidStack(output) != null && CraftTweakerHelper.toStack(input) != ItemStack.EMPTY)
			MineTweakerAPI
					.apply(new RemoveFluid(CraftTweakerHelper.toFluidStack(output), CraftTweakerHelper.toStack(input)));
	}

	private static class RemoveFluid implements IUndoableAction {
		private final FluidStack output;
		private final ItemStack input;
		List<CrushingTubRecipe> removedRecipes = new ArrayList<CrushingTubRecipe>();

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
						removedRecipes.add(r);
						MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(new CrushingTubRecipeWrapper(r));
						MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe((r));
						it.remove();
					}
				}
			}
		}

		@Override
		public void undo() {
			if (removedRecipes != null)
				for (CrushingTubRecipe recipe : removedRecipes)
					if (recipe != null) {
						Recipes.crushingTubRecipes.add(recipe);
						MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(new CrushingTubRecipeWrapper(recipe));
					}
		}

		@Override
		public String describe() {
			return "Removing Crushing Tub Recipes for Fluid " + output.getLocalizedName();
		}

		@Override
		public String describeUndo() {
			return "Re-Adding Crushing Tub Recipes for Fluid " + output.getLocalizedName();
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}

		@Override
		public boolean canUndo() {
			return true;
		}
	}

}
