package rustic.compat.jei;

import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class AdvancedAlchemyRecipeCategory extends BlankRecipeCategory {

	private final IDrawable background;
	private final String name;

	public static ResourceLocation texture = new ResourceLocation("rustic:textures/gui/jei_alchemy_advanced.png");
	
	protected final IDrawableAnimated flame;
	protected final IDrawableAnimated arrow;

	public AdvancedAlchemyRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(texture, 0, 0, 130, 79);
		this.name = I18n.format("rustic.jei.recipe.advanced_alchemy");
		
		IDrawableStatic flameDrawable = helper.createDrawable(texture, 130, 0, 14, 14);
		flame = helper.createAnimatedDrawable(flameDrawable, 300, IDrawableAnimated.StartDirection.TOP, true);

		IDrawableStatic arrowDrawable = helper.createDrawable(texture, 130, 14, 50, 53);
		this.arrow = helper.createAnimatedDrawable(arrowDrawable, 300, IDrawableAnimated.StartDirection.LEFT, false);
	}
	
	@Override
	public void drawExtras(Minecraft minecraft) {
		flame.draw(minecraft, 44, 43);
		arrow.draw(minecraft, 21, 14);
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
		return "rustic.alchemy_advanced";
	}

	@Override
	public void setRecipe(IRecipeLayout layout, IRecipeWrapper wrapper, IIngredients ingredients) {
		IGuiItemStackGroup stacks = layout.getItemStacks();
		IGuiFluidStackGroup fluid = layout.getFluidStacks();
		
		stacks.init(0, true, 3, 55);
		stacks.init(1, true, 3, 31);
		stacks.init(2, true, 3, 7);
		if (ingredients.getInputs(ItemStack.class) instanceof List
				&& ingredients.getInputs(ItemStack.class).size() > 0) {
			stacks.set(0, ingredients.getInputs(ItemStack.class).get(0));
		}
		if (ingredients.getInputs(ItemStack.class) instanceof List
				&& ingredients.getInputs(ItemStack.class).size() > 1) {
			stacks.set(1, ingredients.getInputs(ItemStack.class).get(1));
		}
		if (ingredients.getInputs(ItemStack.class) instanceof List
				&& ingredients.getInputs(ItemStack.class).size() > 2) {
			stacks.set(2, ingredients.getInputs(ItemStack.class).get(2));
		}
		
		stacks.init(3, true, 42, 3);
		if (ingredients.getInputs(ItemStack.class) instanceof List
				&& ingredients.getInputs(ItemStack.class).size() > 3) {
			stacks.set(3, ingredients.getInputs(ItemStack.class).get(3));
		}
		
		stacks.init(4, true, 81, 3);
		stacks.set(4, new ItemStack(Items.GLASS_BOTTLE));
		
		fluid.init(5, true, 110, 24, 16, 32, 8000, true, null);
		fluid.set(5, new FluidStack(FluidRegistry.WATER, 125));
		
		stacks.init(6, false, 81, 31);
		if (ingredients.getOutputs(ItemStack.class) instanceof List
				&& ingredients.getOutputs(ItemStack.class).size() > 0) {
			stacks.set(6, ingredients.getOutputs(ItemStack.class).get(0));
		}
	}
	

	@Override
	public String getModName() {
		return "Rustic";
	}
	
}
