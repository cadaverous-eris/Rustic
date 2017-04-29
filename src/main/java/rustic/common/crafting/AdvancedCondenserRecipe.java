package rustic.common.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import rustic.common.items.ModItems;
import scala.actors.threadpool.Arrays;

public class AdvancedCondenserRecipe extends CondenserRecipe {
	
	private ItemStack modifier;
	
	public AdvancedCondenserRecipe(ItemStack output, ItemStack modifier, ItemStack... inputs) {
		this.output = output;
		this.modifier = modifier;
		this.inputs = Arrays.asList(inputs);
	}
	
	public AdvancedCondenserRecipe(PotionEffect effect, ItemStack modifier, ItemStack... inputs) {
		List<PotionEffect> effects = new ArrayList<PotionEffect>();
		effects.add(effect);
		this.output = PotionUtils.appendEffects(new ItemStack(ModItems.ELIXER), effects);
		this.modifier = modifier;
		this.inputs = Arrays.asList(inputs);
	}
	
	@Override
	public boolean matches(ItemStack mod, ItemStack[] stacks) {
		if ((!this.modifier.isEmpty() && mod.isEmpty()) || (this.modifier.isEmpty() && !mod.isEmpty())) {
			return false;
		}
		if (!this.modifier.getItem().equals(mod.getItem())) {
			return false;
		}
		if (this.modifier.getMetadata() != mod.getMetadata()) {
			return false;
		}
		if (this.modifier.hasTagCompound() && (mod.hasTagCompound())) {
			if (!this.modifier.getTagCompound().equals(mod.getTagCompound())) {
				return false;
			}
		} else if ((this.modifier.hasTagCompound() && !mod.hasTagCompound()) || (!this.modifier.hasTagCompound() && mod.hasTagCompound())) {
			return false;
		}
		return super.matches(mod, stacks);
	}
	
	public ItemStack getModifier() {
		return this.modifier;
	}

}
