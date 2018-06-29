package rustic.compat.jei;

import java.util.ArrayList;
import java.util.List;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IGuiFluidStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.BlankRecipeCategory;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class BrewingRecipeCategory extends BlankRecipeCategory {
	
	private final IDrawable background;
	private final String name;

	public static ResourceLocation texture = new ResourceLocation("rustic:textures/gui/jei_brewing.png");
	
	protected final IDrawableAnimated bubbles;
	protected final IDrawableAnimated arrow;
	protected final IDrawableAnimated arrow1;

	public BrewingRecipeCategory(IGuiHelper helper) {
		this.background = helper.createDrawable(texture, 0, 0, 130, 80);
		this.name = I18n.format("rustic.jei.recipe.brewing");
		
		IDrawableStatic bubbleDrawable = helper.createDrawable(texture, 130, 0, 11, 28);
		bubbles = helper.createAnimatedDrawable(bubbleDrawable, 100, IDrawableAnimated.StartDirection.BOTTOM, false);

		IDrawableStatic arrowDrawable = helper.createDrawable(texture, 130, 28, 24, 16);
		this.arrow = helper.createAnimatedDrawable(arrowDrawable, 1200, IDrawableAnimated.StartDirection.LEFT, false);
		
		IDrawableStatic arrow1Drawable = helper.createDrawable(texture, 130, 44, 10, 10);
		this.arrow1 = helper.createAnimatedDrawable(arrow1Drawable, 1200, IDrawableAnimated.StartDirection.LEFT, false);
	}
	
	@Override
	public void drawExtras(Minecraft minecraft) {
		bubbles.draw(minecraft, 117, 26);
		arrow.draw(minecraft, 63, 32);
		arrow1.draw(minecraft, 23, 35);
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
		return "rustic.brewing";
	}

	@Override
	public void setRecipe(IRecipeLayout layout, IRecipeWrapper wrapper, IIngredients ingredients) {
		IGuiFluidStackGroup fluid = layout.getFluidStacks();
		
		fluid.init(0, true, 40, 24, 16, 32, 8000, true, null);
		fluid.set(0, ingredients.getInputs(FluidStack.class).get(0));
		
		fluid.init(1, false, 94, 24, 16, 32, 8000, true, null);
		fluid.set(1, ingredients.getOutputs(FluidStack.class).get(0));
		
		fluid.init(2, true, 4, 32, 16, 16, 1000, true, null);
		FluidStack aux = ingredients.getOutputs(FluidStack.class).get(0).get(0).copy();
		aux.amount = 1000;
		List<FluidStack> auxList = new ArrayList<FluidStack>();
		auxList.add(null);
		auxList.add(aux);
		fluid.set(2, auxList);
	}

	@Override
	public String getModName() {
		return "Rustic";
	}

}
