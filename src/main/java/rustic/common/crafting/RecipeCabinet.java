package rustic.common.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import rustic.client.util.ItemColorCache;
import rustic.common.Config;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.items.ModItems;

public class RecipeCabinet extends net.minecraftforge.registries.IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {
	
	protected boolean hasOre(ItemStack stack, int ore) {
		if (stack.isEmpty()) return false;
		
		int[] oreIds = OreDictionary.getOreIDs(stack);
		for (int oreId : oreIds) {
			if (oreId == ore) return true;
		}
		return false;
	}
	
	protected boolean isWood(ItemStack stack) {
		if (hasOre(stack, OreDictionary.getOreID("plankWood"))) return true;
		if (stack.getItem().getRegistryName().toString().equals("embers:sealed_planks")) return true;
		if (stack.getItem().getRegistryName().toString().equals("botania:livingwood") && stack.getMetadata() == 1) return true;
		if (stack.getItem().getRegistryName().toString().equals("botania:dreamwood") && stack.getMetadata() == 1) return true;
		if (stack.getItem().getRegistryName().toString().equals("astralsorcery:blockinfusedwood") && stack.getMetadata() == 1) return true;
		if (hasOre(stack, OreDictionary.getOreID("plankTreatedWood"))) return true;
		
		return false;
	}
	
	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		ItemStack stack = ItemStack.EMPTY;
		for (int y = 0; y < 3; y += 2) {
			for (int x = 0; x < 3; x++) {
				stack = inv.getStackInRowAndColumn(x, y);
				if (!isWood(stack)) return false;
			}
		}
		stack = inv.getStackInRowAndColumn(1, 1);
		if (!stack.isEmpty()) return false;

		int trapdoorWood = OreDictionary.getOreID("trapdoorWood");
		ItemStack stack2 = inv.getStackInRowAndColumn(2, 1);
		stack = inv.getStackInRowAndColumn(0, 1);
		return (isWood(stack) && hasOre(stack2, trapdoorWood)) || (isWood(stack2) && hasOre(stack, trapdoorWood));
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack result = getRecipeOutput();
		ItemStack material = ItemStack.EMPTY;
		
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			ItemStack stack = inv.getStackInSlot(i).copy();
			stack.setCount(1);
			
			if (isWood(stack)) {
				if (!material.isEmpty() && !ItemStack.areItemStacksEqual(material, stack)) {
					material = ItemStack.EMPTY;
					break;
				}
				material = stack;
			}
		}
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("material", material.serializeNBT());
		result.setTagCompound(tag);
		
		return result;
	}
	
	@Override
	public ItemStack getRecipeOutput() {
		return new ItemStack(ModBlocks.CABINET);
	}
	
	@Override
	public boolean canFit(int width, int height) {
		return width >= 3 && height >= 3;
	}
	
	@Override
	public boolean isDynamic() {
        return true;
    }

}
