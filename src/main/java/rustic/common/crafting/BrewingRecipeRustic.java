package rustic.common.crafting;

import javax.annotation.Nonnull;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipe;
import net.minecraftforge.oredict.OreDictionary;

public class BrewingRecipeRustic extends BrewingRecipe {

	public static final int POTIONITEM = 0;
	public static final int SPLASH_POTION = 1;
	public static final int LINGERING_POTION = 2;

	public BrewingRecipeRustic(PotionType input, ItemStack ingredient, PotionType output, int type) {
		super(PotionUtils.addPotionToItemStack(new ItemStack(potionItemFromInt(type)), input), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(potionItemFromInt(type)), output));
	}

	public BrewingRecipeRustic(PotionType input, ItemStack ingredient, PotionType output, int typeIn, int typeOut) {
		super(PotionUtils.addPotionToItemStack(new ItemStack(potionItemFromInt(typeIn)), input), ingredient, PotionUtils.addPotionToItemStack(new ItemStack(potionItemFromInt(typeOut)), output));
	}

	@Override
	public boolean isInput(@Nonnull ItemStack stack) {
		return OreDictionary.itemMatches(this.getInput(), stack, true) && (PotionUtils.getPotionFromItem(this.getInput()).equals(PotionUtils.getPotionFromItem(stack)));
	}

	private static Item potionItemFromInt(int type) {
		switch (type) {
		case SPLASH_POTION:
			return Items.SPLASH_POTION;
		case LINGERING_POTION:
			return Items.LINGERING_POTION;
		default:
			return Items.POTIONITEM;
		}
	}

}
