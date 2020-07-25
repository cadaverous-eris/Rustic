package rustic.compat.crafttweaker;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.crafting.ICondenserRecipe;

public class CrTCondenserRecipe implements ICondenserRecipe {
	private int time;
	private ItemStack output;
	private FluidStack fluid;
	private IIngredient bottle;
	private IIngredient modifier;
	private List<IIngredient> inputs;
	
	public CrTCondenserRecipe(@Nonnull ItemStack output, IIngredient[] inputs) {
		this(output, inputs, null);
	}
	
	public CrTCondenserRecipe(@Nonnull ItemStack output, IIngredient[] inputs, IIngredient modifier) {
		this(output, inputs, modifier, CraftTweakerMC.getIItemStack(new ItemStack(Items.GLASS_BOTTLE)));
	}
	
	public CrTCondenserRecipe(@Nonnull ItemStack output, IIngredient[] inputs, IIngredient modifier, IIngredient bottle) {
		this(output, inputs, modifier, bottle, new FluidStack(FluidRegistry.WATER, 125));
	}
	
	public CrTCondenserRecipe(@Nonnull ItemStack output, IIngredient[] inputs, IIngredient modifier, IIngredient bottle, @Nonnull FluidStack fluid) {
		this(output, inputs, modifier, bottle, fluid, 400);
	}
	
	public CrTCondenserRecipe(@Nonnull ItemStack output, IIngredient[] inputs, IIngredient modifier, IIngredient bottle, @Nonnull FluidStack fluid, int time) {
		this.output = output;
		this.fluid = fluid;
		this.bottle = bottle;
		this.modifier = modifier;
		this.inputs = new ArrayList<>();
		for (IIngredient ing : inputs) {
			this.inputs.add(ing);
		}
		this.time = Math.max(0, time);
		
	}

	@Override
	public boolean matches(Fluid fluid, ItemStack modifier, ItemStack bottle, ItemStack[] inputs) {
		if (fluid != this.fluid.getFluid()) {
			return false;
		}
		if (
				(this.modifier != null && !this.modifier.matchesExact(CraftTweakerMC.getIItemStack(modifier)))
				|| (this.modifier == null && !modifier.isEmpty())
		) {
			return false;
		}
		if (this.bottle != null && !this.bottle.matchesExact(CraftTweakerMC.getIItemStack(bottle))) {
			return false;
		}
		
		List<IIngredient> tempInputs = new ArrayList<IIngredient>(this.inputs);
		for (ItemStack stack : inputs) {
			if (stack == null || stack.isEmpty()) {
				continue;
			}
			boolean stackNotInput = true;
			IItemStack input = CraftTweakerMC.getIItemStack(stack);
			for (IIngredient in : tempInputs) {
				if (in == null) {
					tempInputs.remove(in);
					continue;
				}
				if (in.matchesExact(input)) {
					stackNotInput = false;
					tempInputs.remove(in);
					break;
				}
			}
			if (stackNotInput) {
				return false;
			}
		}
		return tempInputs.size() == 0;
		
	}
	
	@Override
	public boolean isBasic() {
		return this.modifier == null && this.inputs.size() <= 2;
	}

	@Override
	public boolean isAdvanced() {
		return this.inputs.size() <= 3;
	}

	@Override
	public FluidStack getFluid() {
		return this.fluid;
	}

	@Override
	public List<ItemStack> getModifiers() {
		if (this.modifier != null) {
			return this.modifier.getItems().stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
		} else {
			return new ArrayList<ItemStack>();
		}
	}

	@Override
	public List<ItemStack> getBottles() {
		if (this.bottle == null) {
			return Collections.singletonList(ItemStack.EMPTY);
		}
		return this.bottle.getItems().stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList());
	}

	@Override
	public List<List<ItemStack>> getInputs() {
		List<List<ItemStack>> inputs = new ArrayList<>();
		for (IIngredient input : this.inputs) {
			if (input == null) {
				inputs.add(new ArrayList<>());
			} else {
				inputs.add(input.getItems().stream().map(CraftTweakerMC::getItemStack).collect(Collectors.toList()));
			}
		}
		return inputs;
	}

	@Override
	public int getTime() {
		return this.time;
	}

	@Override
	public int getModifierConsumption(ItemStack modifier) {
		if (this.modifier != null) {
			IItemStack mod = CraftTweakerMC.getIItemStack(modifier);
			for (IItemStack stack : this.modifier.getItems()) {
				if (stack.matchesExact(mod)) {
					return stack.getAmount();
				}
			}
		}
		return 0;
	}

	@Override
	public int getBottleConsumption(ItemStack bottle) {
		if (this.bottle == null) {
			return 0;
		}
		IItemStack btl = CraftTweakerMC.getIItemStack(bottle);
		for (IItemStack stack : this.bottle.getItems()) {
			if (stack.matchesExact(btl)) {
				return stack.getAmount();
			}
		}
		return 1;
	}

	@Override
	public int[] getInputConsumption(ItemStack[] inputs) {
		int[] consume = new int[inputs.length];
		List<IIngredient> tempInputs = new ArrayList<IIngredient>(this.inputs);
		IItemStack current;
		for (int i=0; i<inputs.length; i++) {
			consume[i] = 0;
			if (inputs[i] != null) {
				current = CraftTweakerMC.getIItemStack(inputs[i]);
				foundAmount:
				for (IIngredient in : tempInputs) {
					for (IItemStack stack : in.getItems()) {
						if (stack.matchesExact(current)) {
							consume[i] = stack.getAmount();
							break foundAmount;
						}
					}
				}
			}
		}
		return consume;
	}

	@Override
	public ItemStack getResult() {
		return this.output.copy();
	}

}
