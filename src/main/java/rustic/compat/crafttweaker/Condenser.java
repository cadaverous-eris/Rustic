package rustic.compat.crafttweaker;

import java.util.Iterator;
import java.util.List;

import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import rustic.common.crafting.AdvancedCondenserRecipe;
import rustic.common.crafting.BasicCondenserRecipe;
import rustic.common.crafting.CondenserRecipe;
import rustic.common.crafting.Recipes;
import rustic.common.util.ElixirUtils;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.rustic.Condenser")
public class Condenser {

	@ZenMethod
	public static void addRecipe(IItemStack output, IItemStack input1, IItemStack input2) {
		CondenserRecipe r = new BasicCondenserRecipe(CraftTweakerHelper.toStack(output),
				CraftTweakerHelper.toStack(input1), CraftTweakerHelper.toStack(input2));
		CraftTweakerAPI.apply(new Add(r));
	}

	@ZenMethod
	public static void addRecipe(IItemStack output, IItemStack modifier, IItemStack[] inputs) {
		CondenserRecipe r = new AdvancedCondenserRecipe(CraftTweakerHelper.toStack(output),
				CraftTweakerHelper.toStack(modifier), CraftTweakerHelper.toStacks(inputs));
		CraftTweakerAPI.apply(new Add(r));
	}

	private static class Add implements IAction {
		private final CondenserRecipe recipe;

		public Add(CondenserRecipe recipe) {
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
			List<PotionEffect> effects = ElixirUtils.getEffects(output);
			Iterator<CondenserRecipe> it = Recipes.condenserRecipes.iterator();
			while (it.hasNext()) {
				CondenserRecipe r = it.next();
				List<PotionEffect> rEffects = ElixirUtils.getEffects(r.getResult());
				if (r != null && r.getResult() != null && r.getResult().isItemEqual(output)) {
					//boolean matches = true;
					for (PotionEffect pe : effects) {
						boolean hasMatch = false;
						for (PotionEffect pe1 : rEffects) {
							if (pe.getPotion() == pe1.getPotion() && pe.getAmplifier() == pe1.getAmplifier()
									&& pe.getDuration() == pe1.getDuration()) {
								hasMatch = true;
								break;
							}
						}
						if (!hasMatch) {
							//matches = false;
							break;
						}
					}
					if (effects.equals(rEffects)) {
						it.remove();
					}
				}
			}
		}

		@Override
		public String describe() {
			return "Removing Condenser Recipes for Item " + output.getDisplayName();
		}

	}

}
