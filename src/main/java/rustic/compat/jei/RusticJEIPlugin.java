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
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import rustic.common.blocks.ModBlocks;
import rustic.common.crafting.BasicCondenserRecipe;
import rustic.common.crafting.CondenserRecipe;
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
		
		reg.addRecipes(OliveOilRecipeMaker.getOliveOilRecipes(), VanillaRecipeCategoryUid.CRAFTING);

		crushingTubCat = new CrushingTubRecipeCategory(guiHelper);
		reg.addRecipeCategories(crushingTubCat);
		ArrayList<CrushingTubRecipe> crushingTubRecipes = new ArrayList<CrushingTubRecipe>();
        for (int i = 0; i < Recipes.crushingTubRecipes.size(); i ++){
        	crushingTubRecipes.add(Recipes.crushingTubRecipes.get(i));
        }
        reg.addRecipes(crushingTubRecipes, "rustic.crushing_tub");
        reg.handleRecipes(CrushingTubRecipe.class, new CrushingTubRecipeWrapperFactory(), "rustic.crushing_tub");
        
        reg.addRecipeCategories(new EvaporatingRecipeCategory(guiHelper));
        ArrayList<EvaporatingBasinRecipe> evaporatingRecipes = new ArrayList<EvaporatingBasinRecipe>();
        for (int i = 0; i < Recipes.evaporatingRecipes.size(); i ++){
        	evaporatingRecipes.add(Recipes.evaporatingRecipes.get(i));
        }
        reg.addRecipes(evaporatingRecipes, "rustic.evaporating");
        reg.handleRecipes(EvaporatingBasinRecipe.class, new EvaporatingRecipeWrapperFactory(), "rustic.evaporating");
        
        reg.addRecipeCategories(new SimpleAlchemyRecipeCategory(guiHelper));
        reg.addRecipes(SimpleAlchemyRecipeMaker.getSimpleAlchemyRecipes(helper), "rustic.alchemy_simple");
        reg.handleRecipes(BasicCondenserRecipe.class, new SimpleAlchemyRecipeWrapperFactory(), "rustic.alchemy_simple");
        
        reg.addRecipeCategories(new AdvancedAlchemyRecipeCategory(guiHelper));
        reg.addRecipes(AdvancedAlchemyRecipeMaker.getAlchemyRecipes(helper), "rustic.alchemy_advanced");
        reg.handleRecipes(CondenserRecipe.class, new AdvancedAlchemyRecipeWrapperFactory(), "rustic.alchemy_advanced");
        
        reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.CRUSHING_TUB),"rustic.crushing_tub");
        reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.EVAPORATING_BASIN), "rustic.evaporating");
        reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.CONDENSER), "rustic.alchemy_simple", VanillaRecipeCategoryUid.FUEL);
        reg.addRecipeCategoryCraftingItem(new ItemStack(ModBlocks.CONDENSER_ADVANCED), "rustic.alchemy_advanced", VanillaRecipeCategoryUid.FUEL);
	}
	
	@Override
	public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
		this.subtypeRegistry = subtypeRegistry;
		subtypeRegistry.useNbtForSubtypes(
				ModItems.FLUID_BOTTLE,
				ModItems.ELIXER
		);
	}
	
	@Override
    public void onRuntimeAvailable(IJeiRuntime iJeiRuntime) {
        recipeRegistry = iJeiRuntime.getRecipeRegistry();
    }

}
