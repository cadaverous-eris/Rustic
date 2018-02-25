package rustic.common.crafting;

import javax.annotation.Nonnull;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public class RecipeNonIngredientReturn extends ShapelessOreRecipe {

	public RecipeNonIngredientReturn(ResourceLocation group, ItemStack result, Object... recipe) {
		super(group, result, recipe);
	}
	
	@Override
	@Nonnull
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList<ItemStack> ret = super.getRemainingItems(inv);
		for (int i = 0; i < ret.size(); i++) {
			if (ret.get(i).getItem() == Items.BUCKET || ret.get(i).getItem() == Items.GLASS_BOTTLE) {
				ret.set(i, ItemStack.EMPTY);
				break;
			}
		}
		return ret;
	}

}
