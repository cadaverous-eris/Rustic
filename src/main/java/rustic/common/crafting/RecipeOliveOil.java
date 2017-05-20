package rustic.common.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.items.ModItems;

public class RecipeOliveOil implements IRecipe {
	
	private int foodSlot = 0;

	@Override
	public boolean matches(InventoryCrafting inv, World worldIn) {
		int numStacks = 0;
		List<Integer> occupiedSlots = new ArrayList<Integer>();
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			if (!inv.getStackInSlot(i).isEmpty()) {
				numStacks++;
				occupiedSlots.add(i);
			}
		}
		if (numStacks != getRecipeSize()) {
			return false;
		}
		ItemStack foodStack = ItemStack.EMPTY;
		ItemStack oilStack = ItemStack.EMPTY;
		for (int i : occupiedSlots) {
			ItemStack tempStack = inv.getStackInSlot(i);
			if (tempStack.getItem() instanceof ItemFood && foodStack.isEmpty()) {
				foodStack = tempStack;
				this.foodSlot = i;
			} else if (tempStack.getItem().equals(ModItems.FLUID_BOTTLE) && oilStack.isEmpty()) {
				FluidStack fluidStack = FluidUtil.getFluidContained(tempStack);
				if (fluidStack != null && fluidStack.getFluid() != null) {
					if (fluidStack.getFluid().equals(ModFluids.OLIVE_OIL)) {
						oilStack = tempStack;
					}
				}
			} else {
				return false;
			}
		}
		if (foodStack.hasTagCompound() && foodStack.getTagCompound().hasKey("oiled")) {
			return false;
		}
		
		return true;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
		ItemStack foodStack = inv.getStackInSlot(foodSlot);
		if (foodStack.isEmpty() || !(foodStack.getItem() instanceof ItemFood)) {
			return ItemStack.EMPTY;
		}
		ItemStack returnStack = new ItemStack(foodStack.getItem(), 1);
		returnStack.setTagInfo("oiled", new NBTTagByte((byte) 0));
		return returnStack;
	}

	@Override
	public int getRecipeSize() {
		return 2;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return ItemStack.EMPTY;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		NonNullList items = NonNullList.<ItemStack>withSize(inv.getSizeInventory(), ItemStack.EMPTY);
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			items.set(i, ForgeHooks.getContainerItem(inv.getStackInSlot(i)));
		}
		return items;
	}

}
