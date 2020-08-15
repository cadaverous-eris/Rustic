package rustic.compat.crafttweaker;

import java.util.Iterator;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.EvaporatingBasinRecipe;
import rustic.common.crafting.IEvaporatingBasinRecipe;
import rustic.common.crafting.Recipes;
import stanhebben.zenscript.annotations.NotNull;
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
			Recipes.evaporatingRecipesMap.put(recipe.getFluid(), recipe);
		}

		@Override
		public String describe() {
			return "Adding Evaporating Recipe for Item " + recipe.getOutput().getDisplayName();
		}

	}

	@ZenMethod
	public static void removeRecipe(@NotNull IItemStack output) {
		CraftTweakerAPI.apply(new RemoveItem(CraftTweakerMC.getItemStack(output)));
	}
	
	@ZenMethod
	public static void removeRecipe(@NotNull ILiquidStack input) {
		CraftTweakerAPI.apply(new RemoveFluid(CraftTweakerMC.getLiquidStack(input)));
	}

	private static class RemoveItem implements IAction {
		private final ItemStack output;

		public RemoveItem(ItemStack output) {
			this.output = output;
		}

		@Override
		public void apply() {
			Recipes.removeEvaporatingRecipe(output);
		}

		@Override
		public String describe() {
			return "Removing Evaporating Recipes for Item " + output.getDisplayName();
		}

	}
	
	private static class RemoveFluid implements IAction {
		private final FluidStack input;
		
		public RemoveFluid(FluidStack input) {
			this.input = input;
		}
		
		@Override
		public void apply() {
			Recipes.removeEvaporatingRecipe(this.input);
		}
		
		@Override
		public String describe() {
			return "Removing Evaporating Recipe for Fluid " + input.getLocalizedName();
		}
	}

}
