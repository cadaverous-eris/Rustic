package rustic.common.crafting;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
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

public class Recipes {
	
	public static List<CrushingTubRecipe> crushingTubRecipes = new ArrayList<CrushingTubRecipe>();
	public static List<EvaporatingBasinRecipe> evaporatingRecipes = new ArrayList<EvaporatingBasinRecipe>();
	public static List<CondenserRecipe> condenserRecipes = new ArrayList<CondenserRecipe>();

	public static void init() {
		addCraftingRecipes();
		addSmeltingRecipes();
		addFuels();
		addOreDictEntries();
		addCrushingTubRecipes();
		addEvaporatingRecipes();
		addCondenserRecipes();
	}

	private static void addSmeltingRecipes() {
		GameRegistry.addSmelting(new ItemStack(ModItems.HONEYCOMB), new ItemStack(ModItems.BEESWAX), 0.3F);
		for (int i = 0; i < BlockPlanksRustic.EnumType.values().length; i++) {
			IBlockState state = ModBlocks.LOG.getDefaultState().withProperty(BlockLogRustic.VARIANT, BlockPlanksRustic.EnumType.byMetadata(i));
			int meta = ModBlocks.LOG.getMetaFromState(state);
			GameRegistry.addSmelting(new ItemStack(ModBlocks.LOG, 1, meta), new ItemStack(Items.COAL, 1, 1), 0.15F);
		}
		GameRegistry.addSmelting(new ItemStack(ModItems.IRON_DUST), new ItemStack(Items.IRON_INGOT), 0.6F);
		GameRegistry.addSmelting(new ItemStack(ModItems.IRON_DUST_TINY), new ItemStack(Items.field_191525_da), 0.15F);
		if (Config.FLESH_SMELTING) {
			GameRegistry.addSmelting(new ItemStack(Items.ROTTEN_FLESH), new ItemStack(ModItems.TALLOW), 0.3F);
		}
	}

	private static void addFuels() {
		IFuelHandler rusticFuels = new IFuelHandler() {
			@Override
			public int getBurnTime(ItemStack fuel) {
				Item item = fuel.getItem();
				if (item == (Item.getItemFromBlock(ModBlocks.SAPLING))) {
					return 100;
				}
				return 0;
			}
		};
		GameRegistry.registerFuelHandler(rusticFuels);
	}

	private static void addOreDictEntries() {
		OreDictionary.registerOre("cropOlive", new ItemStack(ModItems.OLIVES));
		OreDictionary.registerOre("foodIronberries", new ItemStack(ModItems.IRONBERRIES));
		OreDictionary.registerOre("listAllfruit", new ItemStack(ModItems.OLIVES));
		OreDictionary.registerOre("listAllfruit", new ItemStack(ModItems.IRONBERRIES));

		for (int i = 0; i < BlockPlanksRustic.EnumType.values().length; i++) {
			IBlockState state = ModBlocks.LOG.getDefaultState().withProperty(BlockLogRustic.VARIANT, BlockPlanksRustic.EnumType.byMetadata(i));
			int meta = ModBlocks.LOG.getMetaFromState(state);
			OreDictionary.registerOre("treeWood", new ItemStack(ModBlocks.LOG, 1, meta));
			state = ModBlocks.LEAVES.getDefaultState().withProperty(BlockLeavesRustic.VARIANT, BlockPlanksRustic.EnumType.byMetadata(i));
			meta = ModBlocks.LEAVES.getMetaFromState(state);
			OreDictionary.registerOre("treeLeaves", new ItemStack(ModBlocks.LEAVES, 1, meta));
			state = ModBlocks.SAPLING.getDefaultState().withProperty(BlockSaplingRustic.VARIANT, BlockPlanksRustic.EnumType.byMetadata(i));
			meta = ModBlocks.SAPLING.getMetaFromState(state);
			OreDictionary.registerOre("treeSapling", new ItemStack(ModBlocks.SAPLING, 1, meta));
			state = ModBlocks.PLANKS.getDefaultState().withProperty(BlockPlanksRustic.VARIANT, BlockPlanksRustic.EnumType.byMetadata(i));
			meta = ModBlocks.PLANKS.getMetaFromState(state);
			OreDictionary.registerOre("plankWood", new ItemStack(ModBlocks.PLANKS, 1, meta));
		}

		OreDictionary.registerOre("wax", new ItemStack(ModItems.BEESWAX));
		OreDictionary.registerOre("wax", new ItemStack(ModItems.TALLOW));

		OreDictionary.registerOre("stone", new ItemStack(ModBlocks.SLATE));
		
		OreDictionary.registerOre("dustTinyIron", new ItemStack(ModItems.IRON_DUST_TINY));
		OreDictionary.registerOre("dustIron", new ItemStack(ModItems.IRON_DUST));
	}

	private static void addCraftingRecipes() {
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.STONE_PILLAR, 6), "SS ", "SS ", "SS ", 'S', new ItemStack(Blocks.STONE, 1, 0));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.ANDESITE_PILLAR, 6), "SS ", "SS ", "SS ", 'S', new ItemStack(Blocks.STONE, 1, 5));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.DIORITE_PILLAR, 6), "SS ", "SS ", "SS ", 'S', new ItemStack(Blocks.STONE, 1, 3));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.GRANITE_PILLAR, 6), "SS ", "SS ", "SS ", 'S', new ItemStack(Blocks.STONE, 1, 1));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CHAIN, 12), "I", "I", "I", 'I', new ItemStack(Items.IRON_INGOT));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CHANDELIER, 4), " I ", "C C", "III", 'I', new ItemStack(Items.IRON_INGOT), 'C', new ItemStack(ModBlocks.CHAIN));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CANDLE, 6), "S", "W", "I", 'S', new ItemStack(Items.STRING), 'W', new ItemStack(ModItems.BEESWAX), 'I', new ItemStack(Items.IRON_INGOT));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CANDLE, 6), "S", "T", "I", 'S', new ItemStack(Items.STRING), 'T', new ItemStack(ModItems.TALLOW), 'I', new ItemStack(Items.IRON_INGOT));
		GameRegistry.addRecipe(new ShapedOreRecipe(ModBlocks.APIARY, true, "LLL", "P P", "LLL", 'L', "logWood", 'P', "plankWood"));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.SLATE_ROOF, 4), "SS", "SS", 'S', new ItemStack(ModBlocks.SLATE));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.SLATE_ROOF_STAIRS, 4), "S  ", "SS ", "SSS", 'S', new ItemStack(ModBlocks.SLATE_ROOF));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.SLATE_ROOF_SLAB_ITEM, 6), "SSS", 'S', new ItemStack(ModBlocks.SLATE_ROOF));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.SLATE_BRICK_STAIRS, 4), "S  ", "SS ", "SSS", 'S', new ItemStack(ModBlocks.SLATE_BRICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.SLATE_BRICK_SLAB_ITEM, 6), "SSS", 'S', new ItemStack(ModBlocks.SLATE_BRICK));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.CLAY_WALL, 8), " P ", "PCP", " P ", 'P', "plankWood", 'C', Blocks.CLAY));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.CLAY_WALL_CROSS), "P P", " C ", "P P", 'P', "plankWood", 'C', ModBlocks.CLAY_WALL));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.CLAY_WALL_DIAG), "P  ", " C ", "  P", 'P', "plankWood", 'C', ModBlocks.CLAY_WALL));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.TABLE_ACACIA, 2), "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 4), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CHAIR_ACACIA, 4), "P  ", "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 4), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.TABLE_BIG_OAK, 2), "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 5), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CHAIR_BIG_OAK, 4), "P  ", "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 5), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.TABLE_BIRCH, 2), "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 2), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CHAIR_BIRCH, 4), "P  ", "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 2), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.TABLE_JUNGLE, 2), "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 3), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CHAIR_JUNGLE, 4), "P  ", "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 3), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.TABLE_OAK, 2), "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 0), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CHAIR_OAK, 4), "P  ", "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 0), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.TABLE_SPRUCE, 2), "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 1), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CHAIR_SPRUCE, 4), "P  ", "PPP", "S S", 'P', new ItemStack(Blocks.PLANKS, 1, 1), 'S', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.VASE, 6), " C ", "C C", "CCC", 'C', new ItemStack(Blocks.HARDENED_CLAY));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.SLATE_TILE), "S", 'S', new ItemStack(ModBlocks.SLATE));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.SLATE_TILE), "S", 'S', new ItemStack(ModBlocks.SLATE_BRICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.SLATE_BRICK, 4), "SS", "SS", 'S', new ItemStack(ModBlocks.SLATE_TILE));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.SLATE_PILLAR, 6), "SS", "SS", "SS", 'S', new ItemStack(ModBlocks.SLATE_TILE));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.SLATE_CHISELED, 4), "SS", "SS", 'S', new ItemStack(ModBlocks.SLATE_BRICK));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.BARREL), "PSP", "P P", "PSP", 'P', "plankWood", 'S', "slabWood"));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.IRON_LATTICE, 16), " I ", "III", " I ", 'I', new ItemStack(Items.IRON_INGOT));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.IRON_LANTERN, 4), "I", "C", "I", 'I', new ItemStack(Items.IRON_INGOT), 'C', new ItemStack(Items.COAL));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_WHITE, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeWhite"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_ORANGE, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeOrange"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_MAGENTA, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeMagenta"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_LIGHT_BLUE, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeLightBlue"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_YELLOW, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeYellow"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_LIME, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeLime"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_PINK, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyePink"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_GRAY, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeGray"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_SILVER, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeLightGray"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_CYAN, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeCyan"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_PURPLE, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyePurple"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_BLUE, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeBlue"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_BROWN, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeBrown"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_GREEN, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeGreen"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_RED, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeRed"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.PAINTED_WOOD_BLACK, 8), "PPP", "PDP", "PPP", 'P', "plankWood", 'D', "dyeBlack"));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.GARGOYLE, 2), "PRP", "SSS", 'P', new ItemStack(Blocks.STONE_PRESSURE_PLATE), 'R', new ItemStack(Blocks.STONE), 'S', new ItemStack(Blocks.STONE_SLAB, 1, 0));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.CABINET), "WWW", "W D", "WWW", 'W', "plankWood", 'D', Blocks.TRAPDOOR));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.LIQUID_BARREL), "P P", "P P", "PSP", 'P', "plankWood", 'S', "slabWood"));
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.FERTILE_SOIL), Blocks.DIRT, new ItemStack(Items.DYE, 1, 15));
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.PLANKS, 4, 0), new ItemStack(ModBlocks.LOG, 1, 0));
		GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.PLANKS, 4, 1), new ItemStack(ModBlocks.LOG, 1, 1));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.CRUSHING_TUB), "p p", "i i", "sss", 'p', "plankWood", 'i', new ItemStack(Items.IRON_INGOT), 's', "slabWood"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModItems.IRON_DUST), "ddd", "ddd", "ddd", 'd', "dustTinyIron"));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.EVAPORATING_BASIN), "c c", "ccc", 'c', new ItemStack(Blocks.HARDENED_CLAY));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.OLIVE_FENCE, 3), "psp", "psp", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()), 's', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.OLIVE_FENCE_GATE), "sps", "sps", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()), 's', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.IRONWOOD_FENCE, 3), "psp", "psp", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()), 's', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.IRONWOOD_FENCE_GATE), "sps", "sps", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()), 's', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.OLIVE_SLAB_ITEM), "ppp", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.IRONWOOD_SLAB_ITEM), "ppp", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.OLIVE_STAIRS), "p  ", "pp ", "ppp", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.IRONWOOD_STAIRS), "p  ", "pp ", "ppp", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CHAIR_OLIVE, 4), "p  ", "ppp", "s s", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()), 's', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CHAIR_IRONWOOD, 4), "p  ", "ppp", "s s", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()), 's', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.TABLE_OLIVE, 2), "ppp", "s s", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()), 's', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.TABLE_IRONWOOD, 2), "ppp", "s s", 'p', new ItemStack(ModBlocks.PLANKS, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()), 's', new ItemStack(Items.STICK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CONDENSER), " b ", "beb", "bcb", 'b', new ItemStack(Items.BRICK), 'e', new ItemStack(Items.BUCKET), 'c', new ItemStack(Blocks.STAINED_HARDENED_CLAY, 1, 0));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.RETORT), " b", "ie", " b", 'b', new ItemStack(Items.BRICK), 'i', new ItemStack(Items.IRON_INGOT), 'e', new ItemStack(Items.BUCKET));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.CONDENSER_ADVANCED), " b ", "beb", "bib", 'b', new ItemStack(Items.NETHERBRICK), 'e', new ItemStack(Items.BUCKET), 'i', new ItemStack(Blocks.IRON_BLOCK));
		GameRegistry.addShapedRecipe(new ItemStack(ModBlocks.RETORT_ADVANCED), " b", "ie", " b", 'b', new ItemStack(Items.NETHERBRICK), 'i', new ItemStack(Items.IRON_INGOT), 'e', new ItemStack(Items.BUCKET));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ModBlocks.CROP_STAKE, 3), "p", "p", "p", 'p', "plankWood"));
		
		GameRegistry.addRecipe(new RecipeOliveOil());
	}
	
	private static void addCrushingTubRecipes() {
		crushingTubRecipes.add(new CrushingTubRecipe(new FluidStack(ModFluids.OLIVE_OIL, 250), new ItemStack(ModItems.OLIVES)));
		crushingTubRecipes.add(new CrushingTubRecipe(new FluidStack(ModFluids.IRONBERRY_JUICE, 250), new ItemStack(ModItems.IRONBERRIES)));
		crushingTubRecipes.add(new CrushingTubRecipe(new FluidStack(FluidRegistry.WATER, 250), new ItemStack(Items.REEDS), new ItemStack(Items.SUGAR, 2)));
	}
	
	private static void addEvaporatingRecipes() {
		evaporatingRecipes.add(new EvaporatingBasinRecipe(new ItemStack(ModItems.IRON_DUST_TINY, 1), new FluidStack(ModFluids.IRONBERRY_JUICE, 500)));
	}
	
	private static void addCondenserRecipes() {
		condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(MobEffects.INSTANT_HEALTH, 1), new ItemStack(Herbs.CHAMOMILE), new ItemStack(Items.BEEF)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.INSTANT_HEALTH, 1, 1), new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.CHAMOMILE), new ItemStack(Items.BEEF)));
		condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(MobEffects.REGENERATION, 900), new ItemStack(Herbs.COHOSH), new ItemStack(Items.MUTTON)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.REGENERATION, 1800), new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.COHOSH), new ItemStack(Items.MUTTON)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.REGENERATION, 450, 1), new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.COHOSH), new ItemStack(Items.MUTTON)));
		condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(MobEffects.WITHER, 900), new ItemStack(Herbs.DEATHSTALK), new ItemStack(Blocks.SOUL_SAND)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.WITHER, 1800), new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.DEATHSTALK), new ItemStack(Blocks.SOUL_SAND)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.WITHER, 450, 1), new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.DEATHSTALK), new ItemStack(Blocks.SOUL_SAND)));
		condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(MobEffects.NIGHT_VISION, 3600), new ItemStack(Herbs.MOONCAP), new ItemStack(Items.SPIDER_EYE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.NIGHT_VISION, 9600), new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.MOONCAP), new ItemStack(Items.SPIDER_EYE)));
		condenserRecipes.add(new BasicCondenserRecipe(new PotionEffect(MobEffects.SPEED, 3600), new ItemStack(Herbs.WIND_THISTLE), new ItemStack(Items.SUGAR)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.SPEED, 9600), new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.WIND_THISTLE), new ItemStack(Items.SUGAR)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.SPEED, 1800, 1), new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.WIND_THISTLE), new ItemStack(Items.SUGAR)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.FIRE_RESISTANCE, 3600), ItemStack.EMPTY, new ItemStack(Herbs.ALOE_VERA), new ItemStack(Items.BRICK), new ItemStack(Items.COAL)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.FIRE_RESISTANCE, 9600), new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.ALOE_VERA), new ItemStack(Items.BRICK), new ItemStack(Items.COAL)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HEALTH_BOOST, 3600), ItemStack.EMPTY, new ItemStack(Herbs.BLOOD_ORCHID), new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HEALTH_BOOST, 9600), new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.BLOOD_ORCHID), new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HEALTH_BOOST, 1800, 1), new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.BLOOD_ORCHID), new ItemStack(Items.ROTTEN_FLESH), new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HASTE, 3600), ItemStack.EMPTY, new ItemStack(Herbs.CORE_ROOT), new ItemStack(Items.field_191525_da), new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HASTE, 9600), new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.CORE_ROOT), new ItemStack(Items.field_191525_da), new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.HASTE, 1800, 1), new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.CORE_ROOT), new ItemStack(Items.field_191525_da), new ItemStack(Items.REDSTONE)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.STRENGTH, 3600), ItemStack.EMPTY, new ItemStack(Herbs.GINSENG), new ItemStack(Items.BONE), new ItemStack(Items.GUNPOWDER)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.STRENGTH, 9600), new ItemStack(Herbs.HORSETAIL), new ItemStack(Herbs.GINSENG), new ItemStack(Items.BONE), new ItemStack(Items.GUNPOWDER)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(MobEffects.STRENGTH, 1800, 1), new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(Herbs.GINSENG), new ItemStack(Items.BONE), new ItemStack(Items.GUNPOWDER)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.IRON_SKIN_POTION, 3600), ItemStack.EMPTY, new ItemStack(ModItems.IRONBERRIES), new ItemStack(Items.LEATHER), new ItemStack(Items.CLAY_BALL)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.IRON_SKIN_POTION, 9600), new ItemStack(Herbs.HORSETAIL), new ItemStack(ModItems.IRONBERRIES), new ItemStack(Items.LEATHER), new ItemStack(Items.CLAY_BALL)));
		condenserRecipes.add(new AdvancedCondenserRecipe(new PotionEffect(PotionsRustic.IRON_SKIN_POTION, 1800, 1), new ItemStack(Herbs.MARSH_MALLOW), new ItemStack(ModItems.IRONBERRIES), new ItemStack(Items.LEATHER), new ItemStack(Items.CLAY_BALL)));
	}

}
