package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.wrapper.ICustomCraftingRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.crafting.RecipeVantaOil;
import rustic.common.items.ModItems;
import rustic.common.util.ElixirUtils;
import rustic.common.util.RusticUtils;
import rustic.core.Rustic;

public class VantaOilRecipeWrapper implements ICustomCraftingRecipeWrapper {
	
	private final List<List<ItemStack>> inputs;
	private final List<List<ItemStack>> outputs;
	private final int numPotionIngredients;
	
	public VantaOilRecipeWrapper(List<ItemStack> weapons, List<ItemStack> potions, int numPotions) {
		inputs = new ArrayList<>();
		outputs = new ArrayList<>();
		numPotionIngredients = Math.max(1, Math.min(7, numPotions));
		
		ItemStack oilStack = new ItemStack(ModItems.FLUID_BOTTLE);
		NBTTagCompound fluidTag = new FluidStack(ModFluids.VANTA_OIL, 1000).writeToNBT(new NBTTagCompound());
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag(FluidHandlerItemStack.FLUID_NBT_KEY, fluidTag);
		oilStack.setTagCompound(tag);

		inputs.add(weapons);
		inputs.add(Lists.newArrayList(oilStack));
		for (int i = 0; i < numPotionIngredients; i++) {			
			inputs.add(potions);
		}

		outputs.add(weapons);
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(VanillaTypes.ITEM, this.inputs);
		ingredients.setOutputLists(VanillaTypes.ITEM, this.outputs);
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, IIngredients ingredients) {
		// TODO Auto-generated method stub
		
		ingredients.setInputLists(VanillaTypes.ITEM, this.inputs);
		ingredients.setOutputLists(VanillaTypes.ITEM, this.outputs);
		//recipeLayout.getItemStacks()
		
		recipeLayout.getIngredientsGroup(VanillaTypes.ITEM).addTooltipCallback((int slotIndex, boolean input, ItemStack ingredient, List<String> tooltip) -> {
			if (slotIndex == 0) {
				String modName = "";
				if (tooltip.size() >= 2) {
					modName = tooltip.get(tooltip.size() - 1);
					tooltip.remove(tooltip.size() - 1);
				}
				
				PotionEffect vantaEffect = RecipeVantaOil.getIngredientEffect(recipeLayout.getItemStacks().getGuiIngredients().get(3).getDisplayedIngredient());
				if (vantaEffect != null) {
					int totalInputDuration = vantaEffect.getDuration() * numPotionIngredients;
					
					if (!tooltip.isEmpty()) tooltip.add("");
					tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("tooltip.rustic.vanta_oil", vantaEffect.getDuration()).trim());
					
					String effectName = I18n.format(vantaEffect.getEffectName()).trim();
					Potion potion = vantaEffect.getPotion();

					if (vantaEffect.getAmplifier() > 0) {
						effectName = effectName + " " + I18n.format("potion.potency." + vantaEffect.getAmplifier()).trim();
					}
					if (!potion.isInstant()) {
						int duration = ElixirUtils.getNextVantaHitDuration(totalInputDuration);
						effectName = effectName + " (" + StringUtils.ticksToElapsedTime(duration) + ")";
					}
					
					if (potion.isBadEffect()) {
						tooltip.add(" " + TextFormatting.RED + effectName);
					} else {
						tooltip.add(" " + TextFormatting.BLUE + effectName);
					}
					
					int uses = ElixirUtils.getRemainingVantaUses(new PotionEffect(vantaEffect.getPotion(), totalInputDuration, vantaEffect.getAmplifier()));
					tooltip.add(" " + I18n.format((uses == 1) ? "tooltip.rustic.vanta_oil_use" : "tooltip.rustic.vanta_oil_uses", uses));
				}
				
				if (modName.length() > 0) {
					tooltip.add(modName);
				}
			}
		});
		
		recipeLayout.setShapeless();
		
		IGuiItemStackGroup guiItemStacks = recipeLayout.getItemStacks();
		guiItemStacks.set(ingredients);
		
		
		if ((recipeLayout.getFocus() != null) && (recipeLayout.getFocus().getValue() != null)) {
			Object focusVal = recipeLayout.getFocus().getValue();
			if (focusVal instanceof ItemStack) {
				ItemStack focusItem = (ItemStack) focusVal;
				for (ItemStack weapon : inputs.get(0)) {
					if (OreDictionary.itemMatches(weapon, focusItem, false)) {
						guiItemStacks.set(0, weapon);
						guiItemStacks.set(1, weapon);
						break;
					}
				}
			}
		}
	}
	
	@Override
	public ResourceLocation getRegistryName() {
		return new ResourceLocation(Rustic.MODID, "vanta_oiling");
	}
	
	
	public static List<VantaOilRecipeWrapper> getVantaOilRecipes() {
		List<VantaOilRecipeWrapper> recipes = new ArrayList<>();
		
		List<ItemStack> weapons = new ArrayList<>();
		List<ItemStack> potions = new ArrayList<>();
		
		for (Item item : GameRegistry.findRegistry(Item.class).getValues()) {
			List<ItemStack> subItems = RusticUtils.getSubItems(item);
			for (ItemStack stack : subItems) {
				if (RusticUtils.isVantaOilableWeapon(stack)) {
					weapons.add(stack);
				} else if (RecipeVantaOil.getIngredientEffect(stack) != null) {
					potions.add(stack);
				}
			}
		}

		for (int i = 1; i <= 7; i++) {			
			recipes.add(new VantaOilRecipeWrapper(weapons, potions, i));
		}
		
		return recipes;
	}

}
