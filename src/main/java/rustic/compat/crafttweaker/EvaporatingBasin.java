package rustic.compat.crafttweaker;

import java.util.Iterator;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import rustic.common.crafting.EvaporatingBasinRecipe;
import rustic.common.crafting.IEvaporatingBasinRecipe;
import rustic.common.crafting.Recipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.rustic.EvaporatingBasin")
public class EvaporatingBasin {

	@ZenMethod
	public static void addRecipe(IItemStack output, ILiquidStack input) {
		EvaporatingBasinRecipe r = new EvaporatingBasinRecipe(
				CraftTweakerHelper.toStack(output),
				CraftTweakerHelper.toFluidStack(input)
		);
		CraftTweakerAPI.apply(new Add(r));
	}
	
	@ZenMethod
	public static void addRecipe(IItemStack output, ILiquidStack input, int time) {
		if (time < 20) {
			throw new IllegalArgumentException("Minimum evaporation time for evaporating basin is 20 ticks");
		}
		CraftTweakerAPI.apply(new Add(new CrTEvaporatingBasinRecipe(
				CraftTweakerMC.getItemStack(output),
				CraftTweakerMC.getLiquidStack(input),
				time
		)));
	}

	private static class Add implements IAction {
		private final IEvaporatingBasinRecipe recipe;

		public Add(IEvaporatingBasinRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void apply() {
			Recipes.evaporatingRecipes.put(recipe.getFluid(), recipe);
		}

		@Override
		public String describe() {
			return "Adding Evaporating Recipe for Item " + recipe.getOutput().getDisplayName();
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
			Recipes.evaporatingRecipes.entrySet().removeIf(entry -> output.isItemEqual(entry.getValue().getOutput()));
		}

		@Override
		public String describe() {
			return "Removing Evaporating Recipes for Item " + output.getDisplayName();
		}

	}

}
