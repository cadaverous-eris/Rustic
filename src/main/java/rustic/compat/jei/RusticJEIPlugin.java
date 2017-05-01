package rustic.compat.jei;

import java.util.ArrayList;

import javax.annotation.Nullable;

import mezz.jei.api.BlankModPlugin;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import net.minecraft.item.ItemStack;
import rustic.common.blocks.ModBlocks;
import rustic.common.crafting.Recipes;
import rustic.common.items.ModItems;

@JEIPlugin
public class RusticJEIPlugin extends BlankModPlugin {
	
	@Nullable
	private ISubtypeRegistry subtypeRegistry;

	@Override
	public void register(IModRegistry reg) {
		IJeiHelpers helper = reg.getJeiHelpers();
		IGuiHelper guiHelper = helper.getGuiHelper();
		
		reg.addRecipes(OliveOilRecipeMaker.getOliveOilRecipes(), VanillaRecipeCategoryUid.CRAFTING);

		reg.addRecipeCategories(new CrushingTubRecipeCategory(guiHelper));
        reg.addRecipeHandlers(new CrushingTubRecipeHandler());
        ArrayList<CrushingTubRecipeWrapper> crushingTubRecipes = new ArrayList<CrushingTubRecipeWrapper>();
        for (int i = 0; i < Recipes.crushingTubRecipes.size(); i ++){
        	crushingTubRecipes.add(new CrushingTubRecipeWrapper(Recipes.crushingTubRecipes.get(i)));
        }
        reg.addRecipes(crushingTubRecipes, "rustic.crushing_tub");
        
        reg.addRecipeCategories(new EvaporatingRecipeCategory(guiHelper));
        reg.addRecipeHandlers(new EvaporatingRecipeHandler());
        ArrayList<EvaporatingRecipeWrapper> evaporatingRecipes = new ArrayList<EvaporatingRecipeWrapper>();
        for (int i = 0; i < Recipes.evaporatingRecipes.size(); i ++){
        	evaporatingRecipes.add(new EvaporatingRecipeWrapper(Recipes.evaporatingRecipes.get(i)));
        }
        reg.addRecipes(evaporatingRecipes, "rustic.evaporating");
        
        reg.addRecipeCategories(new SimpleAlchemyRecipeCategory(guiHelper));
        reg.addRecipes(SimpleAlchemyRecipeMaker.getSimpleAlchemyRecipes(helper), "rustic.alchemy_simple");
        
        reg.addRecipeCategories(new AdvancedAlchemyRecipeCategory(guiHelper));
        reg.addRecipes(AdvancedAlchemyRecipeMaker.getAlchemyRecipes(helper), "rustic.alchemy_advanced");
        
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

}
