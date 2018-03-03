package rustic.common;

import rustic.core.CommonProxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

public class Config {

	private final static String CATEGORY_GENERAL = "all.general";
	private final static String CATEGORY_BEES = "all.bees";
	private final static String CATEGORY_WORLD = "all.world";
	private final static String CATEGORY_COMPAT = "all.compat";

	private final static List<String> PROPERTY_ORDER_GENERAL = new ArrayList<String>();
	private final static List<String> PROPERTY_ORDER_BEES = new ArrayList<String>();
	private final static List<String> PROPERTY_ORDER_WORLD = new ArrayList<String>();
	private final static List<String> PROPERTY_ORDER_COMPAT = new ArrayList<String>();

	public static float IRONWOOD_GEN_CHANCE;
	public static float OLIVE_GEN_CHANCE;
	public static int MAX_IRONWOOD_GEN_ATTEMPTS;
	public static int MAX_OLIVE_GEN_ATTEMPTS;
	public static float BEEHIVE_GEN_CHANCE;
	public static int MAX_BEEHIVE_ATTEMPTS;
	public static boolean NETHER_SLATE;
	public static int SLATE_VEINS_PER_CHUNK;
	public static int SLATE_VEIN_SIZE;
	public static float BEE_GROWTH_MULTIPLIER;
	public static float BEE_REPRODUCTION_MULTIPLIER;
	public static float BEE_HONEYCOMB_MULTIPLIER;
	public static boolean FLESH_SMELTING;
	public static boolean TOUGHNESS_HUD;
	public static boolean EXTRA_ARMOR_HUD;
	public static float HERB_GEN_CHANCE;
	public static int MAX_HERB_ATTEMPTS;
	public static float WILDBERRY_GEN_CHANCE;
	public static int MAX_WILDBERRY_ATTEMPTS;
	public static boolean OFFSET_WILDBERRY_BUSHES;
	public static boolean ENABLE_SLATE;
	public static boolean ENABLE_PILLARS;
	public static boolean ENABLE_CLAY_WALLS;
	public static boolean ENABLE_PAINTED_WOOD;
	public static boolean ENABLE_TABLES;
	public static boolean ENABLE_CHAIRS;
	public static boolean ENABLE_LATTICE;
	public static List<String> OLIVE_OIL_BLACKLIST = new ArrayList<String>();
	public static boolean ENABLE_OLIVE_OILING;
	public static boolean ENABLE_FORESTRY_COMPAT;
	public static boolean GRAPE_DROP_NEEDS_TOOL;
	public static List<String> GRAPE_TOOL_WHITELIST = new ArrayList<String>();
	public static boolean ENABLE_SEED_DROPS;
	public static int SEED_DROP_RATE;
	public static int MIN_BREW_QUALITY_CHANGE;
	public static int MAX_BREW_QUALITY_CHANGE;
	public static int MAX_BREW_TIME;
	public static boolean ENABLE_BOTTLE_EMPTYING;
	public static List<Integer> OVERWORLD_GENERATION_WHITELIST = new ArrayList<Integer>();
	public static List<Integer> NETHER_GENERATION_WHITELIST = new ArrayList<Integer>();

	public static void readConfig() {
		Configuration cfg = CommonProxy.config;
		try {
			cfg.load();
			initGeneralConfig(cfg);
		} catch (Exception e1) {

		} finally {
			if (cfg.hasChanged()) {
				cfg.save();
			}
		}
	}

	private static void initGeneralConfig(Configuration cfg) {
		cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General Options");
		cfg.addCustomCategoryComment(CATEGORY_WORLD, "World Generation Options");
		cfg.addCustomCategoryComment(CATEGORY_BEES, "Bee Related Options");
		cfg.addCustomCategoryComment(CATEGORY_COMPAT, "Mod Compatability Related Options");

		BEEHIVE_GEN_CHANCE = cfg.getFloat("Beehive Generation Chance", CATEGORY_BEES, 0.03F, 0, 1F, "chance for beehives to try to generate in a chunk");
		MAX_BEEHIVE_ATTEMPTS = cfg.getInt("Max Beehive Generation Attempts", CATEGORY_BEES, 3, 0, 128, "maximum number of times the generator will attempt to place a beehive in a chunk");
		NETHER_SLATE = cfg.getBoolean("Nether Slate", CATEGORY_WORLD, false, "if set to true, slate will generate in the nether instead of the overworld");
		SLATE_VEINS_PER_CHUNK = cfg.getInt("Slate Veins Per Chunk", CATEGORY_WORLD, 5, 0, 128, "number of times the generator will try to place a slate vein per chunk");
		SLATE_VEIN_SIZE = cfg.getInt("Slate Vein Size", CATEGORY_WORLD, 33, 0, 100, "number of blocks per slate vein");
		BEE_GROWTH_MULTIPLIER = cfg.getFloat("Bee Crop Boost Multiplier", CATEGORY_BEES, 1F, 0F, 10F, "higher values increase the frequency with which apiaries forcibly age a crop");
		BEE_REPRODUCTION_MULTIPLIER = cfg.getFloat("Bee Reproduction Multiplier", CATEGORY_BEES, 1F, 0F, 10F, "the time it takes for an apiary to produce a new bee is multiplied by this value\nLARGER numbers make bees reproduce LESS often");
		BEE_HONEYCOMB_MULTIPLIER = cfg.getFloat("Bee Honeycomb Multiplier", CATEGORY_BEES, 1F, 0F, 10F, "the time it takes for an apiary to produce a honeycomb is multiplied by this value\nLARGER numbers make bees produce honeycomb LESS often");
		FLESH_SMELTING = cfg.getBoolean("Flesh Smelting", CATEGORY_GENERAL, true, "enable smelting rotten flesh into tallow");
		OLIVE_GEN_CHANCE = cfg.getFloat("Olive Tree Generation Chance", CATEGORY_WORLD, 0.03F, 0, 1F, "chance for olive trees to try to generate in a chunk");
		IRONWOOD_GEN_CHANCE = cfg.getFloat("Ironwood Generation Chance", CATEGORY_WORLD, 0.015F, 0, 1F, "chance for ironwood trees to try to generate in a chunk");
		MAX_OLIVE_GEN_ATTEMPTS = cfg.getInt("Max Olive Tree Generation Attempts", CATEGORY_WORLD, 5, 0, 128, "maximum number of times the generator will attempt to place an olive tree in a chunk");
		MAX_IRONWOOD_GEN_ATTEMPTS = cfg.getInt("Max Ironwood Generation Attempts", CATEGORY_WORLD, 4, 0, 128, "maximum number of times the generator will attempt to place an ironwood tree in a chunk");
		EXTRA_ARMOR_HUD = cfg.getBoolean("Extra Armor HUD", CATEGORY_GENERAL, true, "if enabled, allows the armor meter to go beyond one row\nonly one extra row will ever be rendered, because the armor stat is naturally capped at 30");
		TOUGHNESS_HUD = cfg.getBoolean("Armor Toughness HUD", CATEGORY_GENERAL, true, "if enabled, adds a hud elemnt over the hunger meter to show armor toughness, if applicable");
		HERB_GEN_CHANCE = cfg.getFloat("Herb Generation Chance", CATEGORY_WORLD, 0.125F, 0, 1F, "chance for an herb to try to generate in a chunk");
		MAX_HERB_ATTEMPTS = cfg.getInt("Max Herb Generation Attempts", CATEGORY_WORLD, 8, 0, 128, "maximum number of times the generator will attempt to place an herb in a chunk");
		WILDBERRY_GEN_CHANCE = cfg.getFloat("Wildberry Generation Chance", CATEGORY_WORLD, 0.05F, 0, 1F, "chance for wildberry bushes to try to generate in a chunk");
		MAX_WILDBERRY_ATTEMPTS = cfg.getInt("Max Wildberry Generation Attempts", CATEGORY_WORLD, 4, 0, 128, "maximum number of times the generator will attempt to place a wildberry bush in a chunk");
		OFFSET_WILDBERRY_BUSHES = cfg.getBoolean("Wildberry Bush Offset", CATEGORY_GENERAL, true, "enable/disable the random offset added to wildberry bush models");
		ENABLE_SLATE = cfg.getBoolean("Enable Slate", CATEGORY_GENERAL, true, "enable/disable all slate blocks and world gen");
		ENABLE_PILLARS = cfg.getBoolean("Enable Stone Pillars", CATEGORY_GENERAL, true, "enable/disable all stone pillar blocks");
		ENABLE_CLAY_WALLS = cfg.getBoolean("Enable Clay Walls", CATEGORY_GENERAL, true, "enable/disable all clay wall blocks");
		ENABLE_PAINTED_WOOD = cfg.getBoolean("Enable Painted Wood", CATEGORY_GENERAL, true, "enable/disable all painted wood blocks");
		ENABLE_TABLES = cfg.getBoolean("Enable Tables", CATEGORY_GENERAL, true, "enable/disable all table blocks");
		ENABLE_CHAIRS = cfg.getBoolean("Enable Chairs", CATEGORY_GENERAL, true, "enable/disable all chair blocks");
		ENABLE_LATTICE = cfg.getBoolean("Enable Lattice", CATEGORY_GENERAL, true, "enable/disable lattice blocks");
		OLIVE_OIL_BLACKLIST = Arrays.asList(cfg.getStringList("Olive Oil Food Blacklist", CATEGORY_GENERAL, new String[0], "add an item's registry name to this list to prevent it from being craftable with olive oil\nput each item name on a new line, don't use commas\n"));
		ENABLE_OLIVE_OILING = cfg.getBoolean("Enable Olive Oiling", CATEGORY_GENERAL, true, "enable/disable the ability to add olive oil to food");
		ENABLE_FORESTRY_COMPAT = cfg.getBoolean("Enable Forestry Compat", CATEGORY_COMPAT, true, "with this enabled, Rustic will automatically add recipes for some of Forestry's machines using Rustic's fluids");
		GRAPE_DROP_NEEDS_TOOL = cfg.getBoolean("Grapeseed Drops Require Tool", CATEGORY_GENERAL, false, "with this value set to true, vines will only drop grape seeds when broken with tools from the whitelist");
		GRAPE_TOOL_WHITELIST = Arrays.asList(cfg.getStringList("Grapeseed Tool Whitelist", CATEGORY_GENERAL, new String[] {"minecraft:iron_hoe", "minecraft:diamond_hoe"}, "add an item's registry name to this list to allow vines to drop grape seeds when broken with it\nput each item name on a new line, don't use commas\n"));
		ENABLE_SEED_DROPS = cfg.getBoolean("Enable Seed Drops", CATEGORY_GENERAL, true, "set this to false to prevent any of Rustic's seeds from dropping from grass or vines");
		SEED_DROP_RATE = cfg.getInt("Seed Drop Rate",CATEGORY_GENERAL, 7, 1, 100, "decrease this number to make seeds more difficult to find (10 is wheat seed rarity)");
		MIN_BREW_QUALITY_CHANGE = cfg.getInt("Minimum Increase To Brew Quality", CATEGORY_GENERAL, -1, -50, 50, "the minimum amount of increase that booze culture will provide to the new brew, in percent");
		MAX_BREW_QUALITY_CHANGE = cfg.getInt("Maximum Increase To Brew Quality", CATEGORY_GENERAL, 4, -50, 50, "the maximum amount of increase that booze culture will provide to the new brew, in percent");
		MAX_BREW_TIME = cfg.getInt("Maximum Brew Time", CATEGORY_GENERAL, 12000, 1200, 120000, "how long it should take for a brewing barrel to finish a brew, in ticks");
		ENABLE_BOTTLE_EMPTYING = cfg.getBoolean("Enable Bottle Emptying", CATEGORY_GENERAL, true, "set this to false if you experience any issues with Rustic's glass bottle emptying recipe");
		List<String> overworldGenWhitelist = Arrays.asList(cfg.getStringList("Overworld Generation Dimension Whitelist", CATEGORY_WORLD, new String[] {"0"}, "add numerical dimension ids to this list to allow Rustic's overworld world gen to occur in those dimensions\ndimensions that are not listed here will not receive Rustic's overworld world generation\n"));
		List<String> netherGenWhitelist = Arrays.asList(cfg.getStringList("Nether Generation Dimension Whitelist", CATEGORY_WORLD, new String[] {"-1"}, "add numerical dimension ids to this list to allow Rustic's nether world gen to occur in those dimensions\ndimensions that are not listed here will not receive Rustic's nether world generation\n"));
		overworldGenWhitelist.forEach((dimId) -> {OVERWORLD_GENERATION_WHITELIST.add(Integer.parseInt(dimId));});
		netherGenWhitelist.forEach((dimId) -> {NETHER_GENERATION_WHITELIST.add(Integer.parseInt(dimId));});
		
		PROPERTY_ORDER_GENERAL.add("Flesh Smelting");
		PROPERTY_ORDER_GENERAL.add("Enable Olive Oiling");
		PROPERTY_ORDER_GENERAL.add("Olive Oil Food Blacklist");
		PROPERTY_ORDER_GENERAL.add("Extra Armor HUD");
		PROPERTY_ORDER_GENERAL.add("Armor Toughness HUD");
		PROPERTY_ORDER_GENERAL.add("Wildberry Bush Offset");
		PROPERTY_ORDER_GENERAL.add("Enable Seed Drops");
		PROPERTY_ORDER_GENERAL.add("Seed Drop Rate");
		PROPERTY_ORDER_GENERAL.add("Grapeseed Drops Require Tool");
		PROPERTY_ORDER_GENERAL.add("Grapeseed Tool Whitelist");
		PROPERTY_ORDER_GENERAL.add("Enable Bottle Emptying");
		PROPERTY_ORDER_GENERAL.add("Minimum Increase To Brew Quality");
		PROPERTY_ORDER_GENERAL.add("Maximum Increase To Brew Quality");
		PROPERTY_ORDER_GENERAL.add("Maximum Brew Time");
		PROPERTY_ORDER_GENERAL.add("Enable Slate");
		PROPERTY_ORDER_GENERAL.add("Enable Stone Pillars");
		PROPERTY_ORDER_GENERAL.add("Enable Clay Walls");
		PROPERTY_ORDER_GENERAL.add("Enable Painted Wood");
		PROPERTY_ORDER_GENERAL.add("Enable Tables");
		PROPERTY_ORDER_GENERAL.add("Enable Chairs");
		PROPERTY_ORDER_GENERAL.add("Enable Lattice");
		PROPERTY_ORDER_WORLD.add("Nether Slate");
		PROPERTY_ORDER_WORLD.add("Slate Veins Per Chunk");
		PROPERTY_ORDER_WORLD.add("Slate Vein Size");
		PROPERTY_ORDER_WORLD.add("Olive Tree Generation Chance");
		PROPERTY_ORDER_WORLD.add("Max Olive Tree Generation Attempts");
		PROPERTY_ORDER_WORLD.add("Ironwood Generation Chance");
		PROPERTY_ORDER_WORLD.add("Max Ironwood Generation Attempts");
		PROPERTY_ORDER_WORLD.add("Herb Generation Chance");
		PROPERTY_ORDER_WORLD.add("Max Herb Generation Attempts");
		PROPERTY_ORDER_WORLD.add("Wildberry Generation Chance");
		PROPERTY_ORDER_WORLD.add("Max Wildberry Generation Attempts");
		PROPERTY_ORDER_WORLD.add("Overworld Generation Dimension Whitelist");
		PROPERTY_ORDER_WORLD.add("Nether Generation Dimension Whitelist");
		PROPERTY_ORDER_BEES.add("Beehive Generation Chance");
		PROPERTY_ORDER_BEES.add("Max Beehive Generation Attempts");
		PROPERTY_ORDER_BEES.add("Bee Reproduction Multiplier");
		PROPERTY_ORDER_BEES.add("Bee Honeycomb Multiplier");
		PROPERTY_ORDER_BEES.add("Bee Crop Boost Multiplier");
		PROPERTY_ORDER_COMPAT.add("Enable Forestry Compat");

		cfg.setCategoryPropertyOrder(CATEGORY_GENERAL, PROPERTY_ORDER_GENERAL);
		cfg.setCategoryPropertyOrder(CATEGORY_BEES, PROPERTY_ORDER_BEES);
		cfg.setCategoryPropertyOrder(CATEGORY_WORLD, PROPERTY_ORDER_WORLD);
		cfg.setCategoryPropertyOrder(CATEGORY_COMPAT,  PROPERTY_ORDER_COMPAT);
		
		// Fix config issues
		ConfigCategory general = cfg.getCategory(CATEGORY_GENERAL);
		if (general != null) {
			Property minBrewQualityChange = general.get("Minimum Increase To Brew Quality");
			Property maxBrewQualityChange = general.get("Maximum Increase To Brew Quality");
			Property maxBrewTime = general.get("Maximum Brew Time");
			
			if (minBrewQualityChange != null && (minBrewQualityChange.getInt() == -50 || minBrewQualityChange.getInt() == 1)) {
				minBrewQualityChange.setValue(-1);
				MIN_BREW_QUALITY_CHANGE = -1;
			}
			if (maxBrewQualityChange != null && maxBrewQualityChange.getInt() == -50) {
				maxBrewQualityChange.setValue(4);
				MAX_BREW_QUALITY_CHANGE = 4;
			}
			if (maxBrewTime != null && maxBrewTime.getInt() == 1200) {
				maxBrewTime.setValue(12000);
				MAX_BREW_TIME = 12000;
			}
		}
	}

}
