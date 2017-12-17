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

public class CrushingTubRecipeCategory extends BlankRecipeCategory {

	private final IDrawable background;
	private final String name;

	public static ResourceLocation texture = new ResourceLocation("rustic:textures/gui/jei_crushing_tub.png");

	public CrushingTubRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(texture, 0, 0, 108, 64);
		this.name = I18n.format("rustic.jei.recipe.crushing_tub");
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
		return "rustic.crushing_tub";
	}

	@Override
	public void setRecipe(IRecipeLayout layout, IRecipeWrapper wrapper, IIngredients ingredients) {
		IGuiItemStackGroup stacks = layout.getItemStacks();

		stacks.init(0, true, 20, 23);
		
		if (ingredients.getInputs(ItemStack.class) instanceof List
				&& ingredients.getInputs(ItemStack.class).size() > 0) {
			stacks.set(0, ingredients.getInputs(ItemStack.class).get(0));
		}

		IGuiFluidStackGroup fluid = layout.getFluidStacks();
		fluid.init(1, false, 71, 5, 16, 32, 8000, true, null);
		fluid.set(1, ingredients.getOutputs(FluidStack.class).get(0));

		stacks.init(2, false, 70, 42);

		if (ingredients.getOutputs(ItemStack.class) instanceof List
				&& ingredients.getOutputs(ItemStack.class).size() > 0) {
			stacks.set(2, ingredients.getOutputs(ItemStack.class).get(0));
		}
	}
	

	@Override
	public String getModName() {
		return "Rustic";
	}

}
