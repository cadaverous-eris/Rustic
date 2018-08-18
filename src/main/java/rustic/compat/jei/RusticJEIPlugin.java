package rustic.compat.jei;

import java.util.ArrayList;

import javax.annotation.Nullable;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.IRecipeRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import rustic.client.gui.GuiBrewingBarrel;
import rustic.client.gui.GuiCondenser;
import rustic.client.gui.GuiCondenserAdvanced;
import rustic.common.Config;
import rustic.common.blocks.ModBlocks;
import rustic.common.crafting.AdvancedCondenserRecipe;
import rustic.common.crafting.BasicCondenserRecipe;
import rustic.common.crafting.BrewingBarrelRecipe;
import rustic.common.crafting.CrushingTubRecipe;
import rustic.common.crafting.EvaporatingBasinRecipe;
import rustic.common.crafting.Recipes;
import rustic.common.items.ModItems;

@JEIPlugin
public class RusticJEIPlugin extends BlankModPlugin {

	public static IRecipeRegistry recipeRegistry;

	public CrushingTubRecipeCategory crushingTubCat;

	@Nullable
	private ISubtypeRegistry subtypeRegistry;

	@Override
	public void register(IModRegistry reg) {
		IJeiHelpers helper = reg.getJeiHelpers();
		IGuiHelper guiHelper = helper.getGuiHelper();

		if (Config.ENABLE_OLIVE_OILING) {
			reg.addRecipes(OliveOilRecipeMaker.getOliveOilRecipes(), VanillaRecipeCategoryUid.CRAFTING);
		}
		
		reg.addRecipes(CabinetRecipeWrapper.getCabinetRecipes(), VanillaRecipeCategoryUid.CRAFTING);

		crushingTubCat = new CrushingTubRecipeCategory(guiHelper);
		reg.addRecipeCategories(crushingTubCat);
		ArrayList<CrushingTubRecipe> crushingTubRecipes = new ArrayList<CrushingTubRecipe>();
		for (int i = 0; i < Recipes.crushingTubRecipes.size(); i++) {
			crushingTubRecipes.add(Recipes.crushingTubRecipes.get(i));
		}
		reg.addRecipes(crushingTubRecipes, "rustic.crushing_tub");
		reg.handleRecipes(CrushingTubRecipe.class, new CrushingTubRecipeWrapperFactory(), "rustic.crushing_tub");

		reg.addRecipeCategories(new EvaporatingRecipeCategory(guiHelper));
		ArrayList<EvaporatingBasinRecipe> evaporatingRecipes = new ArrayList<EvaporatingBasinRecipe>();
		for (int i = 0; i < Recipes.evaporatingRecipes.size(); i++) {
			evaporatingRecipes.add(Recipes.evaporatingRecipes.get(i));
		}
		reg.addRecipes(evaporatingRecipes, "rustic.evaporating");
		reg.handleRecipes(EvaporatingBasinRecipe.class, new EvaporatingRecipeWrapperFactory(), "rustic.evaporating");

		reg.addRecipeCategories(new AdvancedAlchemyRecipeCategory(guiHelper));
		reg.addRecipes(AdvancedAlchemyRecipeMaker.getAlchemyRecipes(helper), "rustic.alchemy_advanced");
		reg.handleRecipes(AdvancedCondenserRecipe.class, new AdvancedAlchemyRecipeWrapperFactory(),
				"rustic.alchemy_advanced");

		reg.addRecipeCategories(new SimpleAlchemyRecipeCategory(guiHelper));
		reg.addRecipes(SimpleAlchemyRecipeMaker.getSimpleAlchemyRecipes(helper), "rustic.alchemy_simple");
		reg.handleRecipes(BasicCondenserRecipe.class, new SimpleAlchemyRecipeWrapperFactory(), "rustic.alchemy_simple");

		reg.addRecipeCategories(new BrewingRecipeCategory(guiHelper));
		reg.addRecipes(Recipes.brewingRecipes, "rustic.brewing");
		reg.handleRecipes(BrewingBarrelRecipe.class, new BrewingRecipeWrapperFactory(), "rustic.brewing");

		reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.CRUSHING_TUB), "rustic.crushing_tub");
		reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.EVAPORATING_BASIN), "rustic.evaporating");
		reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.CONDENSER), "rustic.alchemy_simple",
				VanillaRecipeCategoryUid.FUEL);
		reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.CONDENSER_ADVANCED), "rustic.alchemy_advanced",
				"rustic.alchemy_simple", VanillaRecipeCategoryUid.FUEL);
		reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.BREWING_BARREL), "rustic.brewing");

		reg.addRecipeClickArea(GuiBrewingBarrel.class, 85, 35, 24, 16, "rustic.brewing");
		reg.addRecipeClickArea(GuiCondenser.class, 44, 29, 50, 28, "rustic.alchemy_simple");
		reg.addRecipeClickArea(GuiCondenserAdvanced.class, 44, 25, 50, 35, "rustic.alchemy_simple",
				"rustic.alchemy_advanced");
	}

	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		this.subtypeRegistry = subtypeRegistry;
		subtypeRegistry.useNbtForSubtypes(ModItems.FLUID_BOTTLE, ModItems.ELIXIR);
	}

	@Override
	public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
		recipeRegistry = iJeiRuntime.getRecipeRegistry();
	}

}
