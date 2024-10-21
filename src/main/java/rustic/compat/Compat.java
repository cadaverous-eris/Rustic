package rustic.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.ModBlocks;
import rustic.common.items.ModItems;
import rustic.compat.dynamictrees.DynamicTreesCompat;
import rustic.core.Rustic;

public class Compat {
	public static final String QUARK = "quark";

	@Optional.Method(modid = "forestry")
	public static void doForestryCompat() {

		if (forestry.api.fuels.FuelManager.bronzeEngineFuel != null) {
			Fluid fluid = FluidRegistry.getFluid("honey");
			forestry.api.fuels.FuelManager.bronzeEngineFuel.put(fluid,
					new forestry.api.fuels.EngineBronzeFuel(fluid,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_HONEY,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_HONEY, 1));
			fluid = FluidRegistry.getFluid("oliveoil");
			forestry.api.fuels.FuelManager.bronzeEngineFuel.put(fluid,
					new forestry.api.fuels.EngineBronzeFuel(fluid,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_SEED_OIL,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_SEED_OIL, 1));
			fluid = FluidRegistry.getFluid("applejuice");
			forestry.api.fuels.FuelManager.bronzeEngineFuel.put(fluid,
					new forestry.api.fuels.EngineBronzeFuel(fluid,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_JUICE,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_JUICE, 1));
			fluid = FluidRegistry.getFluid("grapejuice");
			forestry.api.fuels.FuelManager.bronzeEngineFuel.put(fluid,
					new forestry.api.fuels.EngineBronzeFuel(fluid,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_JUICE,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_JUICE, 1));
			fluid = FluidRegistry.getFluid("ironberryjuice");
			forestry.api.fuels.FuelManager.bronzeEngineFuel.put(fluid,
					new forestry.api.fuels.EngineBronzeFuel(fluid,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_JUICE,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_JUICE, 1));
			fluid = FluidRegistry.getFluid("wildberryjuice");
			forestry.api.fuels.FuelManager.bronzeEngineFuel.put(fluid,
					new forestry.api.fuels.EngineBronzeFuel(fluid,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_JUICE,
							forestry.core.config.Constants.ENGINE_FUEL_VALUE_JUICE, 1));
		}

		if (forestry.api.recipes.RecipeManagers.fermenterManager != null) {
			forestry.core.recipes.RecipeUtil.addFermenterRecipes(new ItemStack(ModBlocks.SAPLING, 1, 0), 250,
					forestry.core.fluids.Fluids.BIOMASS);
			forestry.core.recipes.RecipeUtil.addFermenterRecipes(new ItemStack(ModBlocks.SAPLING, 1, 1), 250,
					forestry.core.fluids.Fluids.BIOMASS);
			forestry.core.recipes.RecipeUtil.addFermenterRecipes(new ItemStack(ModBlocks.APPLE_SAPLING, 1, 0), 250,
					forestry.core.fluids.Fluids.BIOMASS);
		}

		if (forestry.api.recipes.RecipeManagers.carpenterManager != null) {
			List<forestry.api.recipes.ICarpenterRecipe> carpenterToAdd = new ArrayList<forestry.api.recipes.ICarpenterRecipe>();
			for (forestry.api.recipes.ICarpenterRecipe recipe : forestry.api.recipes.RecipeManagers.carpenterManager
					.recipes()) {
				if (recipe.getFluidResource() != null
						&& recipe.getFluidResource().getFluid().getName().equals("for.honey")) {
					if (recipe.getCraftingGridRecipe() instanceof forestry.core.recipes.ShapedRecipeCustom) {
						forestry.core.recipes.ShapedRecipeCustom shapedRecipe = (forestry.core.recipes.ShapedRecipeCustom) recipe
								.getCraftingGridRecipe();
						FluidStack fluid = new FluidStack(FluidRegistry.getFluid("honey"),
								recipe.getFluidResource().amount);
						int packingTime = recipe.getPackagingTime();
						ItemStack box = recipe.getBox();

						carpenterToAdd.add(
								new forestry.factory.recipes.CarpenterRecipe(packingTime, fluid, box, shapedRecipe));
					}
				} else if (recipe.getFluidResource() != null
						&& recipe.getFluidResource().getFluid().getName().equals("seed.oil")) {
					if (recipe.getCraftingGridRecipe() instanceof forestry.core.recipes.ShapedRecipeCustom) {
						forestry.core.recipes.ShapedRecipeCustom shapedRecipe = (forestry.core.recipes.ShapedRecipeCustom) recipe
								.getCraftingGridRecipe();
						FluidStack fluid = new FluidStack(FluidRegistry.getFluid("oliveoil"),
								recipe.getFluidResource().amount);
						int packingTime = recipe.getPackagingTime();
						ItemStack box = recipe.getBox();

						carpenterToAdd.add(
								new forestry.factory.recipes.CarpenterRecipe(packingTime, fluid, box, shapedRecipe));
					}
				}
			}
			for (forestry.api.recipes.ICarpenterRecipe recipe : carpenterToAdd) {
				forestry.api.recipes.RecipeManagers.carpenterManager.addRecipe(recipe);
			}
		}

		if (forestry.api.recipes.RecipeManagers.fermenterManager != null) {
			List<forestry.api.recipes.IFermenterRecipe> fermenterToAdd = new ArrayList<forestry.api.recipes.IFermenterRecipe>();
			for (forestry.api.recipes.IFermenterRecipe recipe : forestry.api.recipes.RecipeManagers.fermenterManager
					.recipes()) {
				if (recipe.getFluidResource() != null
						&& recipe.getFluidResource().getFluid().getName().equals("for.honey")) {
					Fluid fluidOut = recipe.getOutput();
					FluidStack fluidIn = new FluidStack(FluidRegistry.getFluid("honey"),
							recipe.getFluidResource().amount);
					int value = recipe.getFermentationValue();
					float mod = recipe.getModifier();
					ItemStack itemIn = recipe.getResource();
					String oreItemIn = recipe.getResourceOreName();

					if (!itemIn.isEmpty()) {
						fermenterToAdd.add(
								new forestry.factory.recipes.FermenterRecipe(itemIn, value, mod, fluidOut, fluidIn));
					} else if (!oreItemIn.isEmpty()) {
						fermenterToAdd.add(
								new forestry.factory.recipes.FermenterRecipe(oreItemIn, value, mod, fluidOut, fluidIn));
					}
				}
				if (recipe.getFluidResource() != null
						&& recipe.getFluidResource().getFluid().getName().equals("juice")) {
					Fluid fluidOut = recipe.getOutput();
					FluidStack fluidIn = new FluidStack(FluidRegistry.getFluid("applejuice"),
							recipe.getFluidResource().amount);
					int value = recipe.getFermentationValue();
					float mod = recipe.getModifier();
					ItemStack itemIn = recipe.getResource();
					String oreItemIn = recipe.getResourceOreName();

					if (!itemIn.isEmpty()) {
						fermenterToAdd.add(
								new forestry.factory.recipes.FermenterRecipe(itemIn, value, mod, fluidOut, fluidIn));

						fluidIn = new FluidStack(FluidRegistry.getFluid("applejuice"),
								recipe.getFluidResource().amount);

						fermenterToAdd.add(
								new forestry.factory.recipes.FermenterRecipe(itemIn, value, mod, fluidOut, fluidIn));

						fluidIn = new FluidStack(FluidRegistry.getFluid("grapejuice"),
								recipe.getFluidResource().amount);

						fermenterToAdd.add(
								new forestry.factory.recipes.FermenterRecipe(itemIn, value, mod, fluidOut, fluidIn));

						fluidIn = new FluidStack(FluidRegistry.getFluid("ironberryjuice"),
								recipe.getFluidResource().amount);

						fermenterToAdd.add(
								new forestry.factory.recipes.FermenterRecipe(itemIn, value, 1F, fluidOut, fluidIn));

						fluidIn = new FluidStack(FluidRegistry.getFluid("wildberryjuice"),
								recipe.getFluidResource().amount);

						fermenterToAdd.add(
								new forestry.factory.recipes.FermenterRecipe(itemIn, value, 1.25F, fluidOut, fluidIn));
					} else if (!oreItemIn.isEmpty()) {
						fermenterToAdd.add(
								new forestry.factory.recipes.FermenterRecipe(oreItemIn, value, mod, fluidOut, fluidIn));

						fluidIn = new FluidStack(FluidRegistry.getFluid("applejuice"),
								recipe.getFluidResource().amount);

						fermenterToAdd.add(
								new forestry.factory.recipes.FermenterRecipe(oreItemIn, value, mod, fluidOut, fluidIn));

						fluidIn = new FluidStack(FluidRegistry.getFluid("grapejuice"),
								recipe.getFluidResource().amount);

						fermenterToAdd.add(
								new forestry.factory.recipes.FermenterRecipe(oreItemIn, value, mod, fluidOut, fluidIn));

						fluidIn = new FluidStack(FluidRegistry.getFluid("ironberryjuice"),
								recipe.getFluidResource().amount);

						fermenterToAdd.add(
								new forestry.factory.recipes.FermenterRecipe(oreItemIn, value, 1F, fluidOut, fluidIn));

						fluidIn = new FluidStack(FluidRegistry.getFluid("wildberryjuice"),
								recipe.getFluidResource().amount);

						fermenterToAdd.add(new forestry.factory.recipes.FermenterRecipe(oreItemIn, value, 1.25F,
								fluidOut, fluidIn));
					}
				}
			}
			for (forestry.api.recipes.IFermenterRecipe recipe : fermenterToAdd) {
				forestry.api.recipes.RecipeManagers.fermenterManager.addRecipe(recipe);
			}
		}

		if (forestry.api.recipes.RecipeManagers.squeezerManager != null) {
			if (FluidRegistry.getFluid("seed.oil") != null) {
				forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10,
						new ItemStack(ModItems.CHILI_PEPPER_SEEDS),
						new FluidStack(FluidRegistry.getFluid("seed.oil"), 10));
				forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.TOMATO_SEEDS),
						new FluidStack(FluidRegistry.getFluid("seed.oil"), 10));
				forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModBlocks.APPLE_SEEDS),
						new FluidStack(FluidRegistry.getFluid("seed.oil"), 10));
				forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModBlocks.GRAPE_STEM),
						new FluidStack(FluidRegistry.getFluid("seed.oil"), 10));
			}
			if (FluidRegistry.getFluid("juice") != null) {
				forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.TOMATO),
						new FluidStack(FluidRegistry.getFluid("juice"), 200));
				forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.CHILI_PEPPER),
						new FluidStack(FluidRegistry.getFluid("juice"), 100));
			}
			forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.OLIVES),
					new FluidStack(FluidRegistry.getFluid("oliveoil"), 250));
			forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.IRONBERRIES),
					new FluidStack(FluidRegistry.getFluid("ironberryjuice"), 250),
					new ItemStack(ModItems.IRON_DUST_TINY), 5);
			forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.GOLDEN_APPLE, 1, 0),
					new FluidStack(FluidRegistry.getFluid("goldenapplejuice"), 100),
					new ItemStack(ModItems.GOLD_DUST, 1), 20);
			forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(Items.GOLDEN_APPLE, 1, 1),
					new FluidStack(FluidRegistry.getFluid("goldenapplejuice"), 1000),
					new ItemStack(ModItems.GOLD_DUST, 1), 90);
			forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.WILDBERRIES),
					new FluidStack(FluidRegistry.getFluid("wildberryjuice"), 250));
			forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.GRAPES),
					new FluidStack(FluidRegistry.getFluid("grapejuice"), 250));
			forestry.api.recipes.RecipeManagers.squeezerManager.addRecipe(10, new ItemStack(ModItems.HONEYCOMB),
					new FluidStack(FluidRegistry.getFluid("honey"), 250));
		}

		Rustic.logger.info("Initialized Forestry compat module");

	}
	
	@Optional.Method(modid = "dynamictrees")
	public static void preInitDynamicTreesCompat() {
		DynamicTreesCompat.preInit();
	}
	
	@Optional.Method(modid = "dynamictrees")
	public static void initDynamicTreesCompat() {
		DynamicTreesCompat.init();
	}
	
	@Optional.Method(modid = "dynamictrees")
	@SideOnly(Side.CLIENT)
	public static void preInitDynamicTreesClientCompat() {
		DynamicTreesCompat.clientPreInit();
	}
	
	@Optional.Method(modid = "dynamictrees")
	@SideOnly(Side.CLIENT)
	public static void initDynamicTreesClientCompat() {
		DynamicTreesCompat.clientInit();
	}

}
