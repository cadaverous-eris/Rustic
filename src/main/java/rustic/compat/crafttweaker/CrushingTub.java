package rustic.compat.crafttweaker;

import java.util.Iterator;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.CrushingTubRecipe;
import rustic.common.crafting.Recipes;
import stanhebben.zenscript.annotations.Optional;
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
	public static void removeRecipe(IItemStack input) {
		CraftTweakerAPI.apply(
				new Remove(null, CraftTweakerMC.getItemStack(input))
		);
	}

	@ZenMethod
	public static void removeRecipe(ILiquidStack output, @Optional IItemStack input) {
		if (output == null) {
			throw new IllegalArgumentException("Cannot remove Crushing Tub recipe for null Fluid input");
		}
		CraftTweakerAPI.apply(
				new Remove(CraftTweakerMC.getLiquidStack(output), input == null ? null : CraftTweakerMC.getItemStack(input)));
	}

	private static class Remove implements IAction {
		private final FluidStack output;
		private final ItemStack input;

		public Remove(FluidStack output, ItemStack input) {
			this.output = output;
			this.input = input;
		}

		@Override
		public void apply() {
			if (this.output == null) {
				Recipes.removeCrushingRecipe(this.input);
			} else {
				Recipes.removeCrushingRecipe(this.output, this.input);
			}
		}

		@Override
		public String describe() {
			if (this.output == null) {
				return "Removing Crushing Tub Recipes for Item " + input.getDisplayName();
			} else {
				return "Removing Crushing Tub Recipes for Fluid " + output.getLocalizedName();
			}
		}

	}

}
