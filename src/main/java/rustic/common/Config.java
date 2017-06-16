package rustic.common;

import rustic.core.CommonProxy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;

public class Config {

	private final static String CATEGORY_GENERAL = "all.general";
	private final static String CATEGORY_BEES = "all.bees";
	private final static String CATEGORY_WORLD = "all.world";

	private final static List<String> PROPERTY_ORDER_GENERAL = new ArrayList<String>();
	private final static List<String> PROPERTY_ORDER_BEES = new ArrayList<String>();
	private final static List<String> PROPERTY_ORDER_WORLD = new ArrayList<String>();

	public static float IRONWOOD_GEN_CHANCE = 0.015F;
	public static float OLIVE_GEN_CHANCE = 0.06F;
	public static int MAX_IRONWOOD_GEN_ATTEMPTS = 4;
	public static int MAX_OLIVE_GEN_ATTEMPTS = 5;
	public static float BEEHIVE_GEN_CHANCE = 0.03F;
	public static int MAX_BEEHIVE_ATTEMPTS = 3;
	public static int SLATE_VEINS_PER_CHUNK = 5;
	public static int SLATE_VEIN_SIZE = 33;
	public static float BEE_GROWTH_MULTIPLIER = 1.0F;
	public static float BEE_REPRODUCTION_MULTIPLIER = 1.0F;
	public static float BEE_HONEYCOMB_MULTIPLIER = 1.0F;
	public static boolean FLESH_SMELTING = true;
	public static boolean TOUGHNESS_HUD = true;
	public static boolean EXTRA_ARMOR_HUD = true;
	public static float HERB_GEN_CHANCE = 0.125F;
	public static int MAX_HERB_ATTEMPTS = 8;
	public static float WILDBERRY_GEN_CHANCE = 0.05F;
	public static int MAX_WILDBERRY_ATTEMPTS = 4;
	public static boolean OFFSET_WILDBERRY_BUSHES = true;
	public static boolean ENABLE_SLATE = true;
	public static boolean ENABLE_PILLARS = true;
	public static boolean ENABLE_CLAY_WALLS = true;
	public static boolean ENABLE_PAINTED_WOOD = true;
	public static boolean ENABLE_TABLES = true;
	public static boolean ENABLE_CHAIRS = true;
	public static boolean ENABLE_LATTICE = true;
	public static List<String> OLIVE_OIL_BLACKLIST = new ArrayList<String>();

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

		BEEHIVE_GEN_CHANCE = cfg.getFloat("Beehive Generation Chance", CATEGORY_BEES, 0.03F, 0, 1F, "chance for beehives to try to generate in a chunk");
		MAX_BEEHIVE_ATTEMPTS = cfg.getInt("Max Beehive Generation Attempts", CATEGORY_BEES, 3, 0, 128, "maximum number of times the generator will attempt to place a beehive in a chunk");
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
		OLIVE_OIL_BLACKLIST = Arrays.asList(cfg.getStringList("Olive Oil Food Blacklist", CATEGORY_GENERAL, new String[0], "add registry names of items to this list to prevent them from being craftable with olive oil\nput each item name on a new line, don't use commas\n"));
		
		PROPERTY_ORDER_GENERAL.add("Flesh Smelting");
		PROPERTY_ORDER_GENERAL.add("Olive Oil Food Blacklist");
		PROPERTY_ORDER_GENERAL.add("Extra Armor HUD");
		PROPERTY_ORDER_GENERAL.add("Armor Toughness HUD");
		PROPERTY_ORDER_GENERAL.add("Wildberry Bush Offset");
		PROPERTY_ORDER_GENERAL.add("Enable Slate");
		PROPERTY_ORDER_GENERAL.add("Enable Stone Pillars");
		PROPERTY_ORDER_GENERAL.add("Enable Clay Walls");
		PROPERTY_ORDER_GENERAL.add("Enable Painted Wood");
		PROPERTY_ORDER_GENERAL.add("Enable Tables");
		PROPERTY_ORDER_GENERAL.add("Enable Chairs");
		PROPERTY_ORDER_GENERAL.add("Enable Lattice");
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
		PROPERTY_ORDER_BEES.add("Beehive Generation Chance");
		PROPERTY_ORDER_BEES.add("Max Beehive Generation Attempts");
		PROPERTY_ORDER_BEES.add("Bee Reproduction Multiplier");
		PROPERTY_ORDER_BEES.add("Bee Honeycomb Multiplier");
		PROPERTY_ORDER_BEES.add("Bee Crop Boost Multiplier");

		cfg.setCategoryPropertyOrder(CATEGORY_GENERAL, PROPERTY_ORDER_GENERAL);
		cfg.setCategoryPropertyOrder(CATEGORY_BEES, PROPERTY_ORDER_BEES);
		cfg.setCategoryPropertyOrder(CATEGORY_WORLD, PROPERTY_ORDER_WORLD);
	}

}
