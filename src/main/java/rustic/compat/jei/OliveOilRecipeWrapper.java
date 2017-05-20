package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeWrapper;
import mezz.jei.api.recipe.wrapper.ICraftingRecipeWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.items.ModItems;

public class OliveOilRecipeWrapper extends BlankRecipeWrapper implements ICraftingRecipeWrapper {
	
	private final List<ItemStack> inputs;
	private final ItemStack output;
	
	public OliveOilRecipeWrapper(Item foodItem) {
		ItemStack foodStack = new ItemStack(foodItem);
		ItemStack oilStack = new ItemStack(ModItems.FLUID_BOTTLE);
		NBTTagCompound fluidTag = new FluidStack(ModFluids.OLIVE_OIL, 1000).writeToNBT(new NBTTagCompound());
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag(FluidHandlerItemStack.FLUID_NBT_KEY, fluidTag);
		oilStack.setTagCompound(tag);
		
		this.inputs = new ArrayList<ItemStack>();
		this.inputs.add(foodStack);
		this.inputs.add(oilStack);
		ItemStack outputStack = new ItemStack(foodItem);
		outputStack.setTagInfo("oiled", new NBTTagByte((byte) 0));
		this.output = outputStack;
	}

	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputs(ItemStack.class, this.inputs);
		ingredients.setOutput(ItemStack.class, this.output);
	}

}
