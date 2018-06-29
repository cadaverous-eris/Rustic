package rustic.compat.crafttweaker;

import java.util.Iterator;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
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
		CraftTweakerAPI.apply(new Add(r));
	}

	private static class Add implements IAction {
		private final EvaporatingBasinRecipe recipe;

		public Add(EvaporatingBasinRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void apply() {
			Recipes.evaporatingRecipes.add(recipe);
		}

		@Override
		public String describe() {
			return "Adding Evaporating Recipe for Item " + recipe.getResult().getDisplayName();
		}

	}

	@ZenMethod
	public static void removeRecipe(IItemStack output) {
		if (!CraftTweakerHelper.toStack(output).isEmpty())
			CraftTweakerAPI.apply(new Remove(CraftTweakerHelper.toStack(output)));
	}

	private static class Remove implements IAction {
		private final ItemStack output;

		public Remove(ItemStack output) {
			this.output = output;
		}

		@Override
		public void apply() {
			Iterator<EvaporatingBasinRecipe> it = Recipes.evaporatingRecipes.iterator();
			while (it.hasNext()) {
				EvaporatingBasinRecipe r = it.next();
				if (r != null && r.getResult() != null && r.getResult().isItemEqual(output)) {
					it.remove();
				}
			}
		}

		@Override
		public String describe() {
			return "Removing Evaporating Recipes for Item " + output.getDisplayName();
		}

	}

}
