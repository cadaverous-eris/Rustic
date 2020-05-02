package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.common.Config;

public final class OliveOilRecipeMaker {

	public static List<OliveOilRecipeWrapper> getOliveOilRecipes() {
		List<OliveOilRecipeWrapper> recipes = new ArrayList<OliveOilRecipeWrapper>();
		for (Item item : GameRegistry.findRegistry(Item.class).getValues()) {
			if (item instanceof ItemFood) {
				String itemName = Objects.requireNonNull(item.getRegistryName()).toString();
				if (Config.OLIVE_OIL_USE_WHITELIST == Config.OLIVE_OIL_BLACKLIST.contains(itemName)) { // item found in list
					OliveOilRecipeWrapper recipe = new OliveOilRecipeWrapper(item);
					recipes.add(recipe);
				}
			}
		}
		return recipes;
	}

	private OliveOilRecipeMaker() {

	}

}
