package rustic.compat.crafttweaker;

import java.util.Iterator;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.ICondenserRecipe;
import rustic.common.crafting.Recipes;
import stanhebben.zenscript.annotations.NotNull;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.rustic.Condenser")
public class Condenser {

	@ZenMethod
	public static void addRecipe(@NotNull IItemStack output, IIngredient input1, IIngredient input2) {
		ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), new IIngredient[] {input1, input2});
		CraftTweakerAPI.apply(new Add(r));
	}
	
	@ZenMethod
	public static void addRecipe(@NotNull IItemStack output, IIngredient[] inputs) {
		if (inputs.length > 3) {
			throw new IllegalArgumentException("Condenser recipe has at most 3 inputs");
		}
		ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), inputs);
		CraftTweakerAPI.apply(new Add(r));
	}
	
	@ZenMethod
	public static void addRecipe(@NotNull IItemStack output, IIngredient[] inputs, IIngredient modifier) {
		if (inputs.length > 3) {
			throw new IllegalArgumentException("Condenser recipe has at most 3 inputs");
		}
		ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), inputs, modifier);
		CraftTweakerAPI.apply(new Add(r));
	}
	
	@ZenMethod
	public static void addRecipe(@NotNull IItemStack output, @NotNull IIngredient[] inputs, IIngredient modifier, IIngredient bottle) {
		if (inputs.length > 3) {
			throw new IllegalArgumentException("Condenser recipe has at most 3 inputs");
		}
		ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), inputs, modifier, bottle);
		CraftTweakerAPI.apply(new Add(r));
	}
	
	@ZenMethod
	public static void addRecipe(@NotNull IItemStack output, @NotNull IIngredient[] inputs, IIngredient modifier, IIngredient bottle, ILiquidStack fluid) {
		if (inputs.length > 3) {
			throw new IllegalArgumentException("Condenser recipe has at most 3 inputs");
		}
		if (fluid == null) {
			fluid = CraftTweakerMC.getILiquidStack(new FluidStack(FluidRegistry.WATER, 125));
		}
		ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), inputs, modifier, bottle, CraftTweakerMC.getLiquidStack(fluid));
		CraftTweakerAPI.apply(new Add(r));
	}
	
	@ZenMethod
	public static void addRecipe(@NotNull IItemStack output, @NotNull IIngredient[] inputs, IIngredient modifier, IIngredient bottle, ILiquidStack fluid, int time) {
		if (inputs.length > 3) {
			throw new IllegalArgumentException("Condenser recipe has at most 3 inputs");
		}
		if (fluid == null) {
			fluid = CraftTweakerMC.getILiquidStack(new FluidStack(FluidRegistry.WATER, 125));
		}
		if (time <= 0) {
			throw new IllegalArgumentException("Brew time must be positive");
		}
		ICondenserRecipe r = new CrTCondenserRecipe(CraftTweakerMC.getItemStack(output), inputs, modifier, bottle, CraftTweakerMC.getLiquidStack(fluid), time);
		CraftTweakerAPI.apply(new Add(r));
	}

	private static class Add implements IAction {
		private final ICondenserRecipe recipe;

		public Add(ICondenserRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void apply() {
			Recipes.condenserRecipes.add(recipe);
		}

		@Override
		public String describe() {
			return "Adding Alchemy Recipe for Item " + recipe.getResult().getDisplayName();
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
			//List<PotionEffect> effects = ElixirUtils.getEffects(output);
			Iterator<ICondenserRecipe> it = Recipes.condenserRecipes.iterator();
			while (it.hasNext()) {
				ICondenserRecipe r = it.next();
				if (r != null && r.getResult() != null && ItemStack.areItemStacksEqual(r.getResult(), output)) {
					it.remove();
				}
//				List<PotionEffect> rEffects = ElixirUtils.getEffects(r.getResult());
//				if (r != null && r.getResult() != null && r.getResult().isItemEqual(output)) {
//					//boolean matches = true;
//					for (PotionEffect pe : effects) {
//						boolean hasMatch = false;
//						for (PotionEffect pe1 : rEffects) {
//							if (pe.getPotion() == pe1.getPotion() && pe.getAmplifier() == pe1.getAmplifier()
//									&& pe.getDuration() == pe1.getDuration()) {
//								hasMatch = true;
//								break;
//							}
//						}
//						if (!hasMatch) {
//							//matches = false;
//							break;
//						}
//					}
//					if (effects.equals(rEffects)) {
//						it.remove();
//					}
//				}
			}
		}

		@Override
		public String describe() {
			return "Removing Condenser Recipes for Item " + output.getDisplayName();
		}

	}

}
