package rustic.compat.jei;

import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class EvaporatingRecipeCategory extends BlankRecipeCategory {

	private final IDrawable background;
	private final String name;

	public static ResourceLocation texture = new ResourceLocation("rustic:textures/gui/jei_evaporating_basin.png");
	
	public EvaporatingRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(texture, 0, 0, 108, 64);
		this.name = I18n.format("rustic.jei.recipe.evaporating");
	}
	
	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public String getTitle() {
		return this.name;
	}

	@Override
	public String getUid() {
		return "rustic.evaporating";
	}

	@Override
	public void setRecipe(IRecipeLayout layout, IRecipeWrapper wrapper, IIngredients ingredients) {
		IGuiFluidStackGroup fluid = layout.getFluidStacks();
		IGuiItemStackGroup stacks = layout.getItemStacks();
		
		fluid.init(0, true, 21, 16, 16, 32, 6000, true, null);
		fluid.set(0, ingredients.getInputs(FluidStack.class).get(0));
		
		stacks.init(1, false, 70, 23);
		if (ingredients.getOutputs(ItemStack.class) instanceof List
				&& ingredients.getOutputs(ItemStack.class).size() > 0) {
			stacks.set(1, ingredients.getOutputs(ItemStack.class).get(0));
		}
		
	}
	

	@Override
	public String getModName() {
		return "Rustic";
	}

}
