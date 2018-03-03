package rustic.common.crafting;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import rustic.common.Config;
import rustic.common.blocks.BlockLeavesRustic;
import rustic.common.blocks.BlockLogRustic;
import rustic.common.blocks.BlockPlanksRustic;
import rustic.common.blocks.BlockSaplingRustic;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.crops.Herbs;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.items.ModItems;
import rustic.common.potions.PotionsRustic;
import rustic.compat.dynamictrees.DynamicTreesCompat;
import rustic.core.Rustic;

import java.util.Arrays;

public class Recipes {

	public static List<CrushingTubRecipe> crushingTubRecipes = new ArrayList<CrushingTubRecipe>();
	public static List<EvaporatingBasinRecipe> evaporatingRecipes = new ArrayList<EvaporatingBasinRecipe>();
	public static List<CondenserRecipe> condenserRecipes = new ArrayList<CondenserRecipe>();
	public static List<BrewingBarrelRecipe> brewingRecipes = new ArrayList<BrewingBarrelRecipe>();

	public static void initOres() {
		addOreDictEntries();
	}
	
	public static void init() {
		addCraftingRecipes();
		addSmeltingRecipes();
		addFuels();
		addCrushingTubRecipes();
		addEvaporatingRecipes();
		addCondenserRecipes();
		addBrewingRecipes();
	}

	private static void addSmeltingRecipes() {
		GameRegistry.addSmelting(new ItemStack(ModItems.HONEYCOMB), new ItemStack(ModItems.BEESWAX), 0.3F);
		for (int i = 0; i < BlockPlanksRustic.EnumType.values().length; i++) {
			IBlockState state = ModBlocks.LOG.getDefaultState().withProperty(BlockLogRustic.VARIANT,
					BlockPlanksRustic.EnumType.byMetadata(i));
			int meta = ModBlocks.LOG.getMetaFromState(state);
			GameRegistry.addSmelting(new ItemStack(ModBlocks.LOG, 1, meta), new ItemStack(Items.COAL, 1, 1), 0.15F);
		}
		GameRegistry.addSmelting(new ItemStack(ModItems.IRON_DUST_TINY), new ItemStack(Items.IRON_NUGGET), 0.15F);
		if (Config.FLESH_SMELTING) {
			GameRegistry.addSmelting(new ItemStack(Items.ROTTEN_FLESH), new ItemStack(ModItems.TALLOW), 0.3F);
		}
	}

	private static void addFuels() {
		IFuelHandler rusticFuels = new IFuelHandler() {
			@Override
			public int getBurnTime(ItemStack fuel) {
				Item item = fuel.getItem();
				if ((item == (Item.getItemFromBlock(ModBlocks.SAPLING)) || item == (Item.getItemFromBlock(ModBlocks.APPLE_SAPLING)))) {
					return 100;
				}
				if (item == Item.getItemFromBlock(ModBlocks.WILDBERRY_BUSH)) {
					return 150;
				}
				return 0;
			}
		};
		GameRegistry.registerFuelHandler(rusticFuels);
	}

	private static void addOreDictEntries() {
		OreDictionary.registerOre("cropOlive", new ItemStack(ModItems.OLIVES));
		OreDictionary.registerOre("cropIronberry", new ItemStack(ModItems.IRONBERRIES));
		OreDictionary.registerOre("listAllfruit", new ItemStack(ModItems.OLIVES));
		OreDictionary.registerOre("listAllfruit", new ItemStack(ModItems.IRONBERRIES));
		OreDictionary.registerOre("listAllfruit", new ItemStack(ModItems.GRAPES));
		OreDictionary.registerOre("listAllberry", new ItemStack(ModItems.GRAPES));
		OreDictionary.registerOre("listAllberry", new ItemStack(ModItems.IRONBERRIES));
		OreDictionary.registerOre("listAllberry", new ItemStack(ModItems.WILDBERRIES));
		OreDictionary.registerOre("listAllfruit", new ItemStack(ModItems.WILDBERRIES));
		OreDictionary.registerOre("cropWildberry", new ItemStack(ModItems.WILDBERRIES));
		OreDictionary.registerOre("cropGrape", new ItemStack(ModItems.GRAPES));
		OreDictionary.registerOre("cropChilipepper", new ItemStack(ModItems.CHILI_PEPPER));
		OreDictionary.registerOre("listAllpepper", new ItemStack(ModItems.CHILI_PEPPER));
		OreDictionary.registerOre("cropTomato", new ItemStack(ModItems.TOMATO));
		OreDictionary.registerOre("listAllveggie", new ItemStack(ModItems.TOMATO));

		for (int i = 0; i < BlockPlanksRustic.EnumType.values().length; i++) {
			IBlockState state = ModBlocks.LOG.getDefaultState().withProperty(BlockLogRustic.VARIANT,
					BlockPlanksRustic.EnumType.byMetadata(i));
			int meta = ModBlocks.LOG.getMetaFromState(state);
			OreDictionary.registerOre("treeWood", new ItemStack(ModBlocks.LOG, 1, meta));
			OreDictionary.registerOre("logWood", new ItemStack(ModBlocks.LOG, 1, meta));
			state = ModBlocks.LEAVES.getDefaultState().withProperty(BlockLeavesRustic.VARIANT,
					BlockPlanksRustic.EnumType.byMetadata(i));
			meta = ModBlocks.LEAVES.getMetaFromState(state);
			OreDictionary.registerOre("treeLeaves", new ItemStack(ModBlocks.LEAVES, 1, meta));
			state = ModBlocks.SAPLING.getDefaultState().withProperty(BlockSaplingRustic.VARIANT,
					BlockPlanksRustic.EnumType.byMetadata(i));
			meta = ModBlocks.SAPLING.getMetaFromState(state);
			OreDictionary.registerOre("treeSapling", new ItemStack(ModBlocks.SAPLING, 1, meta));
			state = ModBlocks.PLANKS.getDefaultState().withProperty(BlockPlanksRustic.VARIANT,
					BlockPlanksRustic.EnumType.byMetadata(i));
			meta = ModBlocks.PLANKS.getMetaFromState(state);
			OreDictionary.registerOre("plankWood", new ItemStack(ModBlocks.PLANKS, 1, meta));
		}
		OreDictionary.registerOre("slabWood", new ItemStack(ModBlocks.OLIVE_SLAB_ITEM));
		OreDictionary.registerOre("slabWood", new ItemStack(ModBlocks.IRONWOOD_SLAB_ITEM));
		OreDictionary.registerOre("stairWood", new ItemStack(ModBlocks.OLIVE_STAIRS));
		OreDictionary.registerOre("stairWood", new ItemStack(ModBlocks.IRONWOOD_STAIRS));

		OreDictionary.registerOre("treeSapling", new ItemStack(ModBlocks.APPLE_SAPLING));
		OreDictionary.registerOre("treeLeaves", new ItemStack(ModBlocks.APPLE_LEAVES));

		OreDictionary.registerOre("wax", new ItemStack(ModItems.BEESWAX));
		OreDictionary.registerOre("wax", new ItemStack(ModItems.TALLOW));
		OreDictionary.registerOre("tallow", new ItemStack(ModItems.BEESWAX));
		OreDictionary.registerOre("tallow", new ItemStack(ModItems.TALLOW));

		OreDictionary.registerOre("dustTinyIron", new ItemStack(ModItems.IRON_DUST_TINY));
		
		OreDictionary.registerOre("dyeRed", new ItemStack(ModItems.WILDBERRIES));

	}

	private static void addCraftingRecipes() {
		if (Config.ENABLE_PILLARS) {
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "pillar_stone"), null, new ItemStack(ModBlocks.STONE_PILLAR, 6), "SS ", "SS ", "SS ", 'S',
					new ItemStack(Blocks.STONE, 1, 0));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "pillar_andesite"), null, new ItemStack(ModBlocks.ANDESITE_PILLAR, 6), "SS ", "SS ", "SS ", 'S',
					new ItemStack(Blocks.STONE, 1, 5));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "pillar_diorite"), null, new ItemStack(ModBlocks.DIORITE_PILLAR, 6), "SS ", "SS ", "SS ", 'S',
					new ItemStack(Blocks.STONE, 1, 3));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "pillar_granite"), null, new ItemStack(ModBlocks.GRANITE_PILLAR, 6), "SS ", "SS ", "SS ", 'S',
					new ItemStack(Blocks.STONE, 1, 1));
			if (Config.ENABLE_SLATE) {
				GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "pillar_slate"), null, new ItemStack(ModBlocks.SLATE_PILLAR, 6), "SS", "SS", "SS", 'S',
						new ItemStack(ModBlocks.SLATE_TILE));
			}
		}
		if (Config.ENABLE_SLATE) {
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_roof"), null, new ItemStack(ModBlocks.SLATE_ROOF, 4), "SS", "SS", 'S',
					new ItemStack(ModBlocks.SLATE));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_roof_stairs"), null, new ItemStack(ModBlocks.SLATE_ROOF_STAIRS, 4), "S  ", "SS ", "SSS", 'S',
					new ItemStack(ModBlocks.SLATE_ROOF));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_roof_slab"), null, new ItemStack(ModBlocks.SLATE_ROOF_SLAB_ITEM, 6), "SSS", 'S',
					new ItemStack(ModBlocks.SLATE_ROOF));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_brick_stairs"), null, new ItemStack(ModBlocks.SLATE_BRICK_STAIRS, 4), "S  ", "SS ", "SSS", 'S',
					new ItemStack(ModBlocks.SLATE_BRICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_brick_slab"), null, new ItemStack(ModBlocks.SLATE_BRICK_SLAB_ITEM, 6), "SSS", 'S',
					new ItemStack(ModBlocks.SLATE_BRICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_tile"), null, new ItemStack(ModBlocks.SLATE_TILE), "S", 'S', new ItemStack(ModBlocks.SLATE));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_tile_1"), null, new ItemStack(ModBlocks.SLATE_TILE), "S", 'S',
					new ItemStack(ModBlocks.SLATE_BRICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_brick"), null, new ItemStack(ModBlocks.SLATE_BRICK, 4), "SS", "SS", 'S',
					new ItemStack(ModBlocks.SLATE_TILE));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "slate_chiseled"), null, new ItemStack(ModBlocks.SLATE_CHISELED, 4), "SS", "SS", 'S',
					new ItemStack(ModBlocks.SLATE_BRICK));
		}
		if (Config.ENABLE_CLAY_WALLS) {
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.CLAY_WALL, 8), " P ", "PCP", " P ", 'P',
					"plankWood", 'C', Blocks.CLAY).setRegistryName(new ResourceLocation(Rustic.MODID, "clay_wall")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.CLAY_WALL_CROSS), "P P", " C ", "P P",
					'P', "plankWood", 'C', ModBlocks.CLAY_WALL).setRegistryName(new ResourceLocation(Rustic.MODID, "clay_wall_cross")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.CLAY_WALL_DIAG), "P  ", " C ", "  P",
					'P', "plankWood", 'C', ModBlocks.CLAY_WALL).setRegistryName(new ResourceLocation(Rustic.MODID, "clay_wall_diag")));
		}
		if (Config.ENABLE_CHAIRS) {
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "acacia_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_ACACIA, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 4), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "big_oak_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_BIG_OAK, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 5), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "birch_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_BIRCH, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 2), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "jungle_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_JUNGLE, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 3), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "oak_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_OAK, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 0), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "spruce_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_SPRUCE, 4), "P  ", "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 1), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "olive_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_OLIVE, 4), "p  ", "ppp", "s s", 'p',
					new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()), 's',
					new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "ironwood_chair"), new ResourceLocation(Rustic.MODID, "chair"), new ItemStack(ModBlocks.CHAIR_IRONWOOD, 4), "p  ", "ppp", "s s", 'p',
					new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()), 's',
					new ItemStack(Items.STICK));
		}
		if (Config.ENABLE_TABLES) {
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "acacia_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_ACACIA, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 4), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "big_oak_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_BIG_OAK, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 5), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "birch_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_BIRCH, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 2), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "jungle_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_JUNGLE, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 3), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "oak_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_OAK, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 0), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "spruce_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_SPRUCE, 2), "PPP", "S S", 'P',
					new ItemStack(Blocks.PLANKS, 1, 1), 'S', new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "olive_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_OLIVE, 2), "ppp", "s s", 'p',
					new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()), 's',
					new ItemStack(Items.STICK));
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "ironwood_table"), new ResourceLocation(Rustic.MODID, "table"), new ItemStack(ModBlocks.TABLE_IRONWOOD, 2), "ppp", "s s", 'p',
					new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()), 's',
					new ItemStack(Items.STICK));
		}
		if (Config.ENABLE_LATTICE) {
			GameRegistry.addShapedRecipe(new ResourceLocation(Rustic.MODID, "iron_lattice"), null, new ItemStack(ModBlocks.IRON_LATTICE, 16), " I ", "III", " I ", 'I',
					new ItemStack(Items.IRON_INGOT));
		}
		if (Config.ENABLE_PAINTED_WOOD) {
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_WHITE, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeWhite").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_white")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_ORANGE, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeOrange").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_orange")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_MAGENTA, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeMagenta").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_magenta")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_LIGHT_BLUE, 8), "PPP",
					"PDP", "PPP", 'P', "plankWood", 'D', "dyeLightBlue").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_light_blue")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_YELLOW, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeYellow").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_yellow")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_LIME, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeLime").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_lime")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_PINK, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyePink").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_pink")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_GRAY, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeGray").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_gray")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_SILVER, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeLightGray").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_silver")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_CYAN, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeCyan").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_cyan")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_PURPLE, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyePurple").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_purple")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_BLUE, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeBlue").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_blue")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_BROWN, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeBrown").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_brown")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_GREEN, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeGreen").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_green")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_RED, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeRed").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_red")));
			GameRegistry.findRegistry(IRecipe.class).register(new ShapedOreRecipe(null, new ItemStack(ModBlocks.PAINTED_WOOD_BLACK, 8), "PPP", "PDP",
					"PPP", 'P', "plankWood", 'D', "dyeBlack").setRegistryName(new ResourceLocation(Rustic.MODID, "painted_wood_black")));
		}
		
		RecipeSorter.register("rustic:shapeless_nonreturn", RecipeNonIngredientReturn.class,
				RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		ItemStack aleWortBucket = FluidUtil.getFilledBucket(new FluidStack(ModFluids.ALE_WORT, 1000));
		GameRegistry.findRegistry(IRecipe.class).register(new RecipeNonIngredientReturn(null, aleWortBucket, new ItemStack(Items.BREAD),
				new ItemStack(Items.SUGAR), new ItemStack(Items.WATER_BUCKET)).setRegistryName(new ResourceLocation(Rustic.MODID, "ale_wort")));
		if (Config.ENABLE_BOTTLE_EMPTYING) {
			GameRegistry.findRegistry(IRecipe.class).register(new RecipeNonIngredientReturn(null, new ItemStack(Items.GLASS_BOTTLE),
					new ItemStack(ModItems.FLUID_BOTTLE)).setRegistryName(new ResourceLocation(Rustic.MODID, "bottle_emptying")));
		}	

		if (Config.ENABLE_OLIVE_OILING) {
			RecipeSorter.register("rustic:olive_oil", RecipeOliveOil.class, RecipeSorter.Category.SHAPELESS,
					"after:minecraft:shapeless");
			GameRegistry.findRegistry(IRecipe.class).register(new RecipeOliveOil().setRegistryName(new ResourceLocation(Rustic.MODID, "olive_oiling")));
		}
		
		if (Loader.isModLoaded("dynamictrees")) {
			GameRegistry.addShapelessRecipe(new ResourceLocation(Rustic.MODID, "oliveseed"), null, new ItemStack(DynamicTreesCompat.getOliveSeed()), Ingredient.fromItem(ModItems.OLIVES));
			GameRegistry.addShapelessRecipe(new ResourceLocation(Rustic.MODID, "ironwoodseed"), null, new ItemStack(DynamicTreesCompat.getIronwoodSeed()), Ingredient.fromItem(ModItems.IRONBERRIES));
		}
	}

	private static void addCrushingTubRecipes() {
		crushingTubRecipes
				.add(new CrushingTubRecipe(new FluidStack(ModFluids.OLIVE_OIL, 250), new ItemStack(ModItems.OLIVES)));
		crushingTubRecipes.add(new CrushingTubRecipe(new FluidStack(ModFluids.IRONBERRY_JUICE, 250),
				new ItemStack(ModItems.IRONBERRIES)));
		crushingTubRecipes.add(new CrushingTubRecipe(new FluidStack(FluidRegistry.WATER, 250),
				new ItemStack(Items.REEDS), new ItemStack(Items.SUGAR, 2)));
		crushingTubRecipes.add(new CrushingTubRecipe(new FluidStack(ModFluids.WILDBERRY_JUICE, 250),
				new ItemStack(ModItems.WILDBERRIES)));
		crushingTubRecipes
				.add(new CrushingTubRecipe(new FluidStack(ModFluids.GRAPE_JUICE, 250), new ItemStack(ModItems.GRAPES)));
		if (!Loader.isModLoaded("dynamictrees")) {
			crushingTubRecipes.add(new CrushingTubRecipe(new FluidStack(ModFluids.APPLE_JUICE, 250),
				new ItemStack(Items.APPLE), new ItemStack(ModBlocks.APPLE_SEEDS)));
		} else {
			crushingTubRecipes.add(new CrushingTubRecipe(new FluidStack(ModFluids.APPLE_JUICE, 250),
					new ItemStack(Items.APPLE), new ItemStack(DynamicTreesCompat.getAppleSeed())));
		}
		crushingTubRecipes
				.add(new CrushingTubRecipe(new FluidStack(ModFluids.HONEY, 250), new ItemStack(ModItems.HONEYCOMB)));
	}

	private static void addEvaporatingRecipes() {
		evaporatingRecipes.add(new EvaporatingBasinRecipe(new ItemStack(ModItems.IRON_DUST_TINY, 1),
				new FluidStack(ModFluids.IRONBERRY_JUICE, 500)));
	}

	private static void addCondenserRecipes() {
		condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(MobEffects.INSTANT_HEALTH, 1),
				new ItemStack(Herbs.CHAMOMILE), new ItemStack(Items.BEEF)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.CHAMOMILE), new ItemStack(Items.BEEF)));
		condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(MobEffects.REGENERATION, 900),
				new ItemStack(Herbs.COHOSH), new ItemStack(ModItems.HONEYCOMB)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.REGENERATION, 1800),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.COHOSH), new ItemStack(ModItems.HONEYCOMB)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.REGENERATION, 450, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.COHOSH), new ItemStack(ModItems.HONEYCOMB)));
		condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(MobEffects.WITHER, 900),
				new ItemStack(Herbs.DEATHSTALK), new ItemStack(Blocks.SOUL_SAND)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.WITHER, 1800),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.DEATHSTALK), new ItemStack(Blocks.SOUL_SAND)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.WITHER, 450, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.DEATHSTALK), new ItemStack(Blocks.SOUL_SAND)));
		condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(MobEffects.NIGHT_VISION, 3600),
				new ItemStack(Herbs.MOONCAP), new ItemStack(Items.SPIDER_EYE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.NIGHT_VISION, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.MOONCAP), new ItemStack(Items.SPIDER_EYE)));
		condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(MobEffects.SPEED, 3600),
				new ItemStack(Herbs.WIND_THISTLE), new ItemStack(Items.SUGAR)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.SPEED, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.WIND_THISTLE), new ItemStack(Items.SUGAR)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.SPEED, 1800, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.WIND_THISTLE), new ItemStack(Items.SUGAR)));
		condenserRecipes
				.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.FIRE_RESISTANCE, 3600), ItemStack.EMPTY,
						new ItemStack(Herbs.ALOE_VERA), new ItemStack(Items.BRICK), new ItemStack(Items.COAL)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.FIRE_RESISTANCE, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.ALOE_VERA), new ItemStack(Items.BRICK),
				new ItemStack(Items.COAL)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HEALTH_BOOST, 3600),
				ItemStack.EMPTY, new ItemStack(Herbs.BLOOD_ORCHID), new ItemStack(Items.ROTTEN_FLESH),
				new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HEALTH_BOOST, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.BLOOD_ORCHID), new ItemStack(Items.ROTTEN_FLESH),
				new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HEALTH_BOOST, 1800, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.BLOOD_ORCHID), new ItemStack(Items.ROTTEN_FLESH),
				new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HASTE, 3600), ItemStack.EMPTY,
				new ItemStack(Herbs.CORE_ROOT), new ItemStack(Items.IRON_NUGGET), new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HASTE, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.CORE_ROOT), new ItemStack(Items.IRON_NUGGET),
				new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HASTE, 1800, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.CORE_ROOT), new ItemStack(Items.IRON_NUGGET),
				new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.STRENGTH, 3600), ItemStack.EMPTY,
				new ItemStack(Herbs.GINSENG), new ItemStack(Items.BONE), new ItemStack(Items.GUNPOWDER)));
		condenserRecipes.add(
				new AdvancedCondenserRecipe(new PotionEffect(MobEffects.STRENGTH, 9600), new ItemStack(Herbs.HORSETAIL),
						new ItemStack(Herbs.GINSENG), new ItemStack(Items.BONE), new ItemStack(Items.GUNPOWDER)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.STRENGTH, 1800, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.GINSENG), new ItemStack(Items.BONE),
				new ItemStack(Items.GUNPOWDER)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.IRON_SKIN_POTION, 3600),
				ItemStack.EMPTY, new ItemStack(ModItems.IRONBERRIES), new ItemStack(Items.LEATHER),
				new ItemStack(Items.CLAY_BALL)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.IRON_SKIN_POTION, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(ModItems.IRONBERRIES), new ItemStack(Items.LEATHER),
				new ItemStack(Items.CLAY_BALL)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.IRON_SKIN_POTION, 1800, 1),
				new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(ModItems.IRONBERRIES), new ItemStack(Items.LEATHER),
				new ItemStack(Items.CLAY_BALL)));
		condenserRecipes
				.add(new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.FEATHER_POTION, 3600), ItemStack.EMPTY,
						new ItemStack(Herbs.CLOUDSBLUFF), new ItemStack(Items.FEATHER), new ItemStack(Items.PAPER)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.FEATHER_POTION, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.CLOUDSBLUFF), new ItemStack(Items.FEATHER),
				new ItemStack(Items.PAPER)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.BLAZING_TRAIL_POTION, 3600),
				ItemStack.EMPTY, new ItemStack(ModItems.CHILI_PEPPER), new ItemStack(Items.BLAZE_POWDER),
				new ItemStack(Blocks.NETHERRACK)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.BLAZING_TRAIL_POTION, 9600),
				new ItemStack(Herbs.HORSETAIL), new ItemStack(ModItems.CHILI_PEPPER), new ItemStack(Items.BLAZE_POWDER),
				new ItemStack(Blocks.NETHERRACK)));
	}

	private static void addBrewingRecipes() {
		brewingRecipes
				.add(new BrewingBarrelRecipe(new FluidStack(ModFluids.ALE, 1), new FluidStack(ModFluids.ALE_WORT, 1)));
		brewingRecipes.add(
				new BrewingBarrelRecipe(new FluidStack(ModFluids.CIDER, 1), new FluidStack(ModFluids.APPLE_JUICE, 1)));
		brewingRecipes.add(new BrewingBarrelRecipe(new FluidStack(ModFluids.IRON_WINE, 1),
				new FluidStack(ModFluids.IRONBERRY_JUICE, 1)));
		brewingRecipes
				.add(new BrewingBarrelRecipe(new FluidStack(ModFluids.MEAD, 1), new FluidStack(ModFluids.HONEY, 1)));
		if (FluidRegistry.isFluidRegistered("for.honey")) {
			brewingRecipes.add(new BrewingBarrelRecipe(new FluidStack(ModFluids.MEAD, 1), new FluidStack(FluidRegistry.getFluid("for.honey"), 1)));
		}
		brewingRecipes.add(new BrewingBarrelRecipe(new FluidStack(ModFluids.WILDBERRY_WINE, 1),
				new FluidStack(ModFluids.WILDBERRY_JUICE, 1)));
		brewingRecipes.add(
				new BrewingBarrelRecipe(new FluidStack(ModFluids.WINE, 1), new FluidStack(ModFluids.GRAPE_JUICE, 1)));
	}

}
