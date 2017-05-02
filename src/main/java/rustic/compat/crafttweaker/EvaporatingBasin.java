package rustic.compat.crafttweaker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.CrushingTubRecipe;
import rustic.common.crafting.EvaporatingBasinRecipe;
import rustic.common.crafting.Recipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.rustic.EvaporatingBasin")
public class EvaporatingBasin {

	@ZenMethod
	public static void addRecipe(IItemStack output, ILiquidStack input) {
		EvaporatingBasinRecipe r = new EvaporatingBasinRecipe(CraftTweakerHelper.toStack(output),
				CraftTweakerHelper.toFluidStack(input));
		MineTweakerAPI.apply(new Add(r));
	}

	private static class Add implements IUndoableAction {
		private final EvaporatingBasinRecipe recipe;

		public Add(EvaporatingBasinRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void apply() {
			Recipes.evaporatingRecipes.add(recipe);
			MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe);
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			Recipes.evaporatingRecipes.remove(recipe);
			MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipe);
		}

		@Override
		public String describe() {
			return "Adding Evaporating Recipe for Item " + recipe.getResult().getDisplayName();
		}

		@Override
		public String describeUndo() {
			return "Removing Evaporating Recipe for Item " + recipe.getResult().getDisplayName();
		}

		@Override
		public Object getOverrideKey() {
			return null;
		}
	}

	@ZenMethod
	public static void removeRecipe(IItemStack output) {
		if (CraftTweakerHelper.toStack(output) != ItemStack.EMPTY)
			MineTweakerAPI.apply(new Remove(CraftTweakerHelper.toStack(output)));
	}

	private static class Remove implements IUndoableAction {
		private final ItemStack output;
		List<EvaporatingBasinRecipe> removedRecipes = new ArrayList<EvaporatingBasinRecipe>();

		public Remove(ItemStack output) {
			this.output = output;
		}

		@Override
		public void apply() {
			Iterator<EvaporatingBasinRecipe> it = Recipes.evaporatingRecipes.iterator();
			while (it.hasNext()) {
				EvaporatingBasinRecipe r = it.next();
				if (r != null && r.getResult() != null && r.getResult().isItemEqual(output)) {
					removedRecipes.add(r);
					MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(r);
					it.remove();
				}
			}
		}

		@Override
		public void undo() {
			if (removedRecipes != null)
				for (EvaporatingBasinRecipe recipe : removedRecipes)
					if (recipe != null) {
						Recipes.evaporatingRecipes.add(recipe);
						MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe);
					}
		}

		@Override
		public String describe() {
			return "Removing Evaporating Recipes for Item " + output.getDisplayName();
		}

		@Override
		public String describeUndo() {
			return "Re-Adding Evaporating Recipes for Item " + output.getDisplayName();
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
