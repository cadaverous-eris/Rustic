package rustic.common.crafting;

import javax.annotation.Nonnull;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeNonIngredientReturn extends ShapelessOreRecipe {

	public RecipeNonIngredientReturn(ItemStack result, Object... recipe) {
		super(result, recipe);
	}
	
	@Override
	@Nonnull
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> ret = super.getRemainingItems(inv);
		for (int i = 0; i < ret.size(); i++) {
			if (ret.get(i).getItem() == Items.BUCKET) {
				ret.set(i, ItemStack.EMPTY);
				break;
			}
		}
		return ret;
	}

}
