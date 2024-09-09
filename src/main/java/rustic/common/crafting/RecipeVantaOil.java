package rustic.common.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import rustic.common.Config;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.items.ModItems;
import rustic.common.util.ElixirUtils;
import rustic.common.util.RusticUtils;

public class RecipeVantaOil extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	
	public static int getReturnBottleCount(InventoryCrafting inv) {
		int bottleCount = 0;
		
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack tempStack = inv.getStackInSlot(i);
			if (tempStack.isEmpty()) continue;
			
			if (RusticUtils.isVantaOilBottle(tempStack)) {
				bottleCount++;
				continue;
			}
			PotionEffect tempEffect = getIngredientEffect(tempStack);
			if (tempEffect != null) {
				bottleCount++;
				continue;
			}
		}
		
		return bottleCount;
	}
	

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ItemStack weaponStack = ItemStack.EMPTY;
		ItemStack oilStack = ItemStack.EMPTY;
		Potion ingPotion = null;
		PotionEffect weaponEffect = null;
		
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack tempStack = inv.getStackInSlot(i);
			if (tempStack.isEmpty()) continue;
			
			if (RusticUtils.isVantaOilBottle(tempStack)) {
				if (!oilStack.isEmpty()) return false;
				oilStack = tempStack;
				continue;
			}
			PotionEffect tempEffect = getIngredientEffect(tempStack);
			if (tempEffect != null) {
				if (ingPotion != null) {
					// check if ingredient effects are compatible
					if (ingPotion != tempEffect.getPotion()) return false;
				} else {
					ingPotion = tempEffect.getPotion();
					if (weaponEffect != null) {
						// check if weapon and ing potions are compatible
						Potion weaponPotion = weaponEffect.getPotion();
						if ((weaponPotion != null) && (weaponPotion != ingPotion)) return false;
					}
				}
				continue;
			}
			if (RusticUtils.isVantaOilableWeapon(tempStack)) {
				if (!weaponStack.isEmpty()) return false;
				weaponStack = tempStack;
				weaponEffect = ElixirUtils.getVantaOilEffect(weaponStack);
				if ((weaponEffect != null) && (ingPotion != null)) {
					// check if weapon and ing potions are compatible
					Potion weaponPotion = weaponEffect.getPotion();
					if ((weaponPotion != null) && (weaponPotion != ingPotion)) return false;
				}
				continue;
			}
			
			return false;
		}
		
		return (!weaponStack.isEmpty() && !oilStack.isEmpty() && (ingPotion != null));
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack weaponStack = ItemStack.EMPTY;
		ItemStack oilStack = ItemStack.EMPTY;
		Potion ingPotion = null;
		int amplifier = 0;
		int duration = 0;
		PotionEffect weaponEffect = null;
		
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack tempStack = inv.getStackInSlot(i);
			if (tempStack.isEmpty()) continue;
			
			if (RusticUtils.isVantaOilBottle(tempStack)) {
				if (!oilStack.isEmpty()) return ItemStack.EMPTY;
				oilStack = tempStack;
				continue;
			}
			PotionEffect tempEffect = getIngredientEffect(tempStack);
			if (tempEffect != null) {
				if (ingPotion != null) {
					if ((tempEffect.getAmplifier() >= 0) && (tempEffect.getAmplifier() < amplifier)) amplifier = tempEffect.getAmplifier();
					duration += ingPotion.isInstant() ? 1 : Math.max(tempEffect.getDuration(), 1);
					// check if ingredient effects are compatible
					if (ingPotion != tempEffect.getPotion()) return ItemStack.EMPTY;
				} else {
					ingPotion = tempEffect.getPotion();
					amplifier = tempEffect.getAmplifier();
					duration = ingPotion.isInstant() ? 1 : Math.max(tempEffect.getDuration(), 1);
					if (weaponEffect != null) {
						// check if weapon and ing potions are compatible
						Potion weaponPotion = weaponEffect.getPotion();
						if ((weaponPotion != null) && (weaponPotion != ingPotion)) return ItemStack.EMPTY;
					}
				}
				continue;
			}
			if (RusticUtils.isVantaOilableWeapon(tempStack)) {
				if (!weaponStack.isEmpty()) return ItemStack.EMPTY;
				weaponStack = tempStack;
				weaponEffect = ElixirUtils.getVantaOilEffect(weaponStack);
				if ((weaponEffect != null) && (ingPotion != null)) {
					// check if weapon and ing effects are compatible
					Potion weaponPotion = weaponEffect.getPotion();
					if ((weaponPotion != null) && (weaponPotion != ingPotion)) return ItemStack.EMPTY;
				}
				continue;
			}
			
			return ItemStack.EMPTY;
		}
		
		if (weaponStack.isEmpty() || oilStack.isEmpty() || (ingPotion == null)) return ItemStack.EMPTY;
		
		if (weaponEffect != null) {
			duration += Math.max(weaponEffect.getDuration(), 0);
			if ((weaponEffect.getAmplifier() >= 0) && (weaponEffect.getAmplifier() < amplifier)) amplifier = weaponEffect.getAmplifier();
		}
		
		ItemStack returnStack = weaponStack.copy();
		returnStack.setCount(1);
		ElixirUtils.setVantaOilEffect(returnStack, new PotionEffect(ingPotion, duration, amplifier));
		
		return returnStack;
	}
	
	
	public static PotionEffect getIngredientEffect(ItemStack stack) {
		final Item item = stack.getItem();
		if ((item == Items.POTIONITEM) || (item == Items.SPLASH_POTION) || (item == Items.LINGERING_POTION)) {
			List<PotionEffect> effects = PotionUtils.getEffectsFromStack(stack);
			if (effects.size() == 1) {
				PotionEffect effect = effects.get(0);
				if (effect.getPotion() != null) return effect;
			}
			return (effects.size() == 1) ? effects.get(0) : null;
		} else if (item == ModItems.ELIXIR) {
			List<PotionEffect> effects = ElixirUtils.getEffects(stack);
			if (effects.size() == 1) {
				PotionEffect effect = effects.get(0);
				if (effect.getPotion() != null) return effect;
			}
		}
		return null;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList items = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		return items;
	}

	@Override
	public boolean canFit(int width, int height) {
		return width <= 3 && height <= 3;
	}
	
	@Override
	public boolean isDynamic() {
        return true;
    }

}
