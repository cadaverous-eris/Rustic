package rustic.compat.crafttweaker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
import minetweaker.api.liquid.ILiquidStack;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.Loader;
import rustic.common.crafting.AdvancedCondenserRecipe;
import rustic.common.crafting.BasicCondenserRecipe;
import rustic.common.crafting.CondenserRecipe;
import rustic.common.crafting.EvaporatingBasinRecipe;
import rustic.common.crafting.Recipes;
import rustic.common.util.ElixirUtils;
import rustic.compat.jei.AdvancedAlchemyRecipeWrapper;
import rustic.compat.jei.RusticJEIPlugin;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.rustic.Condenser")
public class Condenser {

	@ZenMethod
	public static void addRecipe(IItemStack output, IItemStack input1, IItemStack input2) {
		CondenserRecipe r = new BasicCondenserRecipe(CraftTweakerHelper.toStack(output),
				CraftTweakerHelper.toStack(input1), CraftTweakerHelper.toStack(input2));
		MineTweakerAPI.apply(new Add(r));
	}

	@ZenMethod
	public static void addRecipe(IItemStack output, IItemStack modifier, IItemStack[] inputs) {
		CondenserRecipe r = new AdvancedCondenserRecipe(CraftTweakerHelper.toStack(output),
				CraftTweakerHelper.toStack(modifier), CraftTweakerHelper.toStacks(inputs));
		MineTweakerAPI.apply(new Add(r));
	}

	private static class Add implements IUndoableAction {
		private final CondenserRecipe recipe;

		public Add(CondenserRecipe recipe) {
			this.recipe = recipe;
		}

		@Override
		public void apply() {
			Recipes.condenserRecipes.add(recipe);
			MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe);
		}

		@Override
		public boolean canUndo() {
			return true;
		}

		@Override
		public void undo() {
			Recipes.condenserRecipes.remove(recipe);
			MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipe);
		}

		@Override
		public String describe() {
			return "Adding Alchemy Recipe for Item " + recipe.getResult().getDisplayName();
		}

		@Override
		public String describeUndo() {
			return "Removing Alchemy Recipe for Item " + recipe.getResult().getDisplayName();
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
		List<CondenserRecipe> removedRecipes = new ArrayList<CondenserRecipe>();

		public Remove(ItemStack output) {
			this.output = output;
		}

		@Override
		public void apply() {
			List<PotionEffect> effects = ElixirUtils.getEffects(output);
			Iterator<CondenserRecipe> it = Recipes.condenserRecipes.iterator();
			while (it.hasNext()) {
				CondenserRecipe r = it.next();
				List<PotionEffect> rEffects = ElixirUtils.getEffects(r.getResult());
				if (r != null && r.getResult() != null && r.getResult().isItemEqual(output)
						&& (effects.equals(rEffects))) {
					removedRecipes.add(r);
					MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(r);
					it.remove();
				}
			}
		}

		@Override
		public void undo() {
			if (removedRecipes != null)
				for (CondenserRecipe recipe : removedRecipes)
					if (recipe != null) {
						Recipes.condenserRecipes.add(recipe);
						MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe);
					}
		}

		@Override
		public String describe() {
			return "Removing Condenser Recipes for Item " + output.getDisplayName();
		}

		@Override
		public String describeUndo() {
			return "Re-Adding Condenser Recipes for Item " + output.getDisplayName();
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
