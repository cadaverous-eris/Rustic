package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.wrapper.IShapedCraftingRecipeWrapper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import rustic.common.Config;
import rustic.common.blocks.ModBlocks;
import rustic.core.Rustic;
import scala.actors.threadpool.Arrays;

public class CabinetRecipeWrapper implements IShapedCraftingRecipeWrapper {
	
	protected ItemStack output;
	protected ItemStack material = ItemStack.EMPTY;
	protected List<List<ItemStack>> inputs;
	
	public CabinetRecipeWrapper(ItemStack output) {
		this.output = output.copy();
		if (output.hasTagCompound()) {
			NBTTagCompound tag = output.getTagCompound();
			if (tag.hasKey("material", Constants.NBT.TAG_COMPOUND)) {
				this.material = new ItemStack(tag.getCompoundTag("material"));
			}
		}
		
		this.inputs = new ArrayList<List<ItemStack>>();
		
		List<ItemStack> trapdoors = new ArrayList<ItemStack>();
		trapdoors.addAll(OreDictionary.getOres("trapdoorWood"));
		if (trapdoors.size() == 0) trapdoors.add(new ItemStack(Blocks.TRAPDOOR));
		
		inputs.add(getMaterialAsList());
		inputs.add(getMaterialAsList());
		inputs.add(getMaterialAsList());
		inputs.add(getMaterialAsList());
		inputs.add(Lists.<ItemStack>newArrayList(ItemStack.EMPTY));
		inputs.add(trapdoors);
		inputs.add(getMaterialAsList());
		inputs.add(getMaterialAsList());
		inputs.add(getMaterialAsList());
	}
	
	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, inputs);
		ingredients.setOutput(ItemStack.class, this.output);
	}
	
	protected List<ItemStack> getMaterialAsList() {
		if (material.isEmpty()) return OreDictionary.getOres("plankWood");
		return Lists.<ItemStack>newArrayList(material.copy());
	}

	@Override
	public int getWidth() {
		return 3;
	}

	@Override
	public int getHeight() {
		return 3;
	}
	
	public static List<CabinetRecipeWrapper> getCabinetRecipes() {
		List<CabinetRecipeWrapper> recipes = new ArrayList<CabinetRecipeWrapper>();
		ItemStack output = new ItemStack(ModBlocks.CABINET);
		CabinetRecipeWrapper recipe = new CabinetRecipeWrapper(output);
		recipes.add(recipe);
		return recipes;
	}

}
