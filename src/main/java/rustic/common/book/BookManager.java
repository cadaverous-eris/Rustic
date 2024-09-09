package rustic.common.book;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import rustic.common.Config;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.crops.Herbs;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.book.pages.BookPageCategories;
import rustic.common.book.pages.BookPageText;
import rustic.common.items.ModItems;
import rustic.core.Rustic;

public class BookManager {
	
	// category list
	public static List<BookCategory> categories = new ArrayList<BookCategory>();
	
	// categories
	public static BookCategory decoration;
	public static BookCategory agriculture;
	public static BookCategory production;
	
	// entries
	public static BookEntry categories_entry;

	public static BookEntry pots;
	public static BookEntry barrels;
	public static BookEntry cabinets;
	public static BookEntry rope;
	public static BookEntry chain;
	public static BookEntry candles;
	public static BookEntry chandeliers;
	public static BookEntry lanterns;
	public static BookEntry lattice;
	public static BookEntry tables;
	public static BookEntry chairs;
	public static BookEntry gargoyles;
	public static BookEntry slate;
	public static BookEntry pillars;
	public static BookEntry clay_walls;
	public static BookEntry painted_wood;
	
	public static BookEntry bees;
	public static BookEntry fertile_soil;
	public static BookEntry crop_stakes;
	public static BookEntry tomatoes;
	public static BookEntry chili_peppers;
	public static BookEntry wildberries;
	public static BookEntry grapes;
	public static BookEntry apple_trees;
	public static BookEntry olive_trees;
	public static BookEntry ironwood_trees;
	public static BookEntry herbs;
	
	public static BookEntry alchemy;
	public static BookEntry elixirs;
	public static BookEntry vanta_oil;
	public static BookEntry brewing;
	public static BookEntry alcoholic_beverages;
	public static BookEntry crushing;
	public static BookEntry drying;
	
	public static void init() {
		
		decoration = new BookCategory("decoration").setIcon(new ResourceLocation(Rustic.MODID, "textures/gui/book/anim_decoration.png"));
		agriculture = new BookCategory("agriculture").setIcon(new ResourceLocation(Rustic.MODID, "textures/gui/book/anim_agriculture.png"));
		production = new BookCategory("production").setIcon(new ResourceLocation(Rustic.MODID, "textures/gui/book/anim_production.png"));
		categories.add(decoration);
		categories.add(agriculture);
		categories.add(production);
		
		categories_entry = new BookEntry("categories", null).addPage(new BookPageCategories());
		
		pots = new BookEntry("pots", decoration).setIcon(new ItemStack(ModBlocks.VASE));
		barrels = new BookEntry("barrels", decoration).setIcon(new ItemStack(ModBlocks.BARREL));
		cabinets = new BookEntry("cabinets", decoration).setIcon(new ItemStack(ModBlocks.CABINET));
		rope = new BookEntry("rope", decoration).setIcon(new ItemStack(ModBlocks.ROPE));
		chain = new BookEntry("chain", decoration).setIcon(new ItemStack(ModBlocks.CHAIN));
		candles = new BookEntry("candles", decoration).setIcon(new ItemStack(ModBlocks.CANDLE));
		chandeliers = new BookEntry("chandeliers", decoration).setIcon(new ItemStack(ModBlocks.CHANDELIER));
		lanterns = new BookEntry("lanterns", decoration).setIcon(new ItemStack(ModBlocks.IRON_LANTERN));
		if (Config.ENABLE_LATTICE) {
			lattice = new BookEntry("lattice", decoration).setIcon(new ItemStack(ModBlocks.IRON_LATTICE));
		}
		if (Config.ENABLE_TABLES) {
			tables = new BookEntry("tables", decoration).setIcon(new ItemStack(ModBlocks.TABLE_OAK));
		}
		if (Config.ENABLE_CHAIRS) {
			chairs = new BookEntry("chairs", decoration).setIcon(new ItemStack(ModBlocks.CHAIR_OAK));
		}
		gargoyles = new BookEntry("gargoyles", decoration).setIcon(new ItemStack(ModBlocks.GARGOYLE));
		if (Config.ENABLE_SLATE) {
			slate = new BookEntry("slate", decoration).setIcon(new ItemStack(ModBlocks.SLATE));
		}
		if (Config.ENABLE_PILLARS) {
			pillars = new BookEntry("pillars", decoration).setIcon(new ItemStack(ModBlocks.STONE_PILLAR));
		}
		if (Config.ENABLE_CLAY_WALLS) {
			clay_walls = new BookEntry("clay_walls", decoration).setIcon(new ItemStack(ModBlocks.CLAY_WALL));
		}
		if (Config.ENABLE_PAINTED_WOOD) {
			painted_wood = new BookEntry("painted_wood", decoration).setIcon(new ItemStack(ModBlocks.PAINTED_WOOD_RED));
		}
		
		bees = new BookEntry("bees", agriculture).setIcon(new ItemStack(ModItems.BEE));
		fertile_soil = new BookEntry("fertile_soil", agriculture).setIcon(new ItemStack(ModBlocks.FERTILE_SOIL));
		crop_stakes = new BookEntry("crop_stakes", agriculture).setIcon(new ItemStack(ModBlocks.CROP_STAKE));
		tomatoes = new BookEntry("tomatoes", agriculture).setIcon(new ItemStack(ModItems.TOMATO));
		chili_peppers = new BookEntry("chili_peppers", agriculture).setIcon(new ItemStack(ModItems.CHILI_PEPPER));
		wildberries = new BookEntry("wildberries", agriculture).setIcon(new ItemStack(ModItems.WILDBERRIES));
		grapes = new BookEntry("grapes", agriculture).setIcon(new ItemStack(ModItems.GRAPES));
		apple_trees = new BookEntry("apple_trees", agriculture).setIcon(new ItemStack(Items.APPLE));
		olive_trees = new BookEntry("olive_trees", agriculture).setIcon(new ItemStack(ModItems.OLIVES));
		ironwood_trees = new BookEntry("ironwood_trees", agriculture).setIcon(new ItemStack(ModItems.IRONBERRIES));
		herbs = new BookEntry("herbs", agriculture).setIcon(new ItemStack(Herbs.MARSH_MALLOW));
		
		alchemy = new BookEntry("alchemy", production).setIcon(new ItemStack(ModBlocks.CONDENSER));
		elixirs = new BookEntry("elixirs", production).setIcon(new ItemStack(ModItems.ELIXIR));
		vanta_oil = new BookEntry("vanta_oil", production).setIcon(ModItems.FLUID_BOTTLE.getFilledBottle(ModFluids.VANTA_OIL));
		brewing = new BookEntry("brewing", production).setIcon(new ItemStack(ModBlocks.BREWING_BARREL));
		alcoholic_beverages = new BookEntry("alcoholic_beverages", production).setIcon(ModItems.FLUID_BOTTLE.getFilledBottle(ModFluids.WINE));
		crushing = new BookEntry("crushing", production).setIcon(new ItemStack(ModBlocks.CRUSHING_TUB));
		drying = new BookEntry("drying", production).setIcon(new ItemStack(ModBlocks.EVAPORATING_BASIN));
		
		pots.addPage(new BookPageText(pots, "pots"));
		barrels.addPage(new BookPageText(barrels, "barrel_item", "barrel_fluid"));
		cabinets.addPage(new BookPageText(cabinets, "cabinets"));
		rope.addPage(new BookPageText(rope, "rope").addRelatedEntries(crop_stakes, grapes, chain, chandeliers))
		.addPage(new BookPageText(rope, "rope_1").addRelatedEntries(crop_stakes, grapes, chain, chandeliers));
		chain.addPage(new BookPageText(chain, "chain").addRelatedEntries(rope, chandeliers, lattice));
		candles.addPage(new BookPageText(candles, "candles").addRelatedEntries(bees, chandeliers));
		chandeliers.addPage(new BookPageText(chandeliers, "chandeliers").addRelatedEntries(candles, rope, chain));
		lanterns.addPage(new BookPageText(lanterns, "lanterns").addRelatedEntries(chain, candles, lattice));
		if (Config.ENABLE_LATTICE) {
			lattice.addPage(new BookPageText(lattice, "lattice").addRelatedEntries(chain, lanterns));
		}
		if (Config.ENABLE_TABLES) {
			tables.addPage(new BookPageText(tables, "tables").addRelatedEntries(chairs));
		}
		if (Config.ENABLE_CHAIRS) {
			chairs.addPage(new BookPageText(chairs, "chairs").addRelatedEntries(tables));
		}
		gargoyles.addPage(new BookPageText(gargoyles, "gargoyles"));
		if (Config.ENABLE_SLATE) {
			if (Config.NETHER_SLATE) {
				slate.addPage(new BookPageText(slate, "slate_alt_nether").addRelatedEntries(pillars));
			} else {
				slate.addPage(new BookPageText(slate, "slate").addRelatedEntries(pillars));
			}
		}
		if (Config.ENABLE_PILLARS) {
			pillars.addPage(new BookPageText(pillars, "pillars").addRelatedEntries(slate));
		}
		if (Config.ENABLE_CLAY_WALLS) {
			clay_walls.addPage(new BookPageText(clay_walls, "clay_walls"));
		}
		if (Config.ENABLE_PAINTED_WOOD) {
			painted_wood.addPage(new BookPageText(painted_wood, "painted_wood"));
		}
		
		bees.addPage(new BookPageText(bees, "bees").addRelatedEntries(candles, crushing, alchemy, brewing)).addPage(new BookPageText(bees, "apiaries").addRelatedEntries(candles, crushing, alchemy, brewing)).addPage(new BookPageText(bees, "honeycomb", "honey").addRelatedEntries(candles, crushing, alchemy, brewing));
		fertile_soil.addPage(new BookPageText(fertile_soil, "fertile_soil"));
		crop_stakes.addPage(new BookPageText(crop_stakes, "crop_stakes").addRelatedEntries(tomatoes, chili_peppers, rope));
		tomatoes.addPage(new BookPageText(tomatoes, "tomatoes").addRelatedEntries(crop_stakes)).addPage(new BookPageText(tomatoes, "tomatoes_1").addRelatedEntries(crop_stakes));
		chili_peppers.addPage(new BookPageText(chili_peppers, "chili_peppers").addRelatedEntries(crop_stakes, alchemy)).addPage(new BookPageText(chili_peppers, "chili_peppers_1").addRelatedEntries(crop_stakes, alchemy));
		wildberries.addPage(new BookPageText(wildberries, "wildberries").addRelatedEntries(crushing, brewing)).addPage(new BookPageText(wildberries, "wildberries_1").addRelatedEntries(crushing, brewing)).addPage(new BookPageText(wildberries, "wildberry_juice").addRelatedEntries(crushing, brewing));
		grapes.addPage(new BookPageText(grapes, "grapes").addRelatedEntries(rope, crushing, brewing)).addPage(new BookPageText(grapes, "grapes_1").addRelatedEntries(rope, crushing, brewing)).addPage(new BookPageText(grapes, "grape_juice").addRelatedEntries(rope, crushing, brewing));
		apple_trees.addPage(new BookPageText(apple_trees, "apple_trees").addRelatedEntries(crushing, brewing)).addPage(new BookPageText(apple_trees, "apple_trees_1").addRelatedEntries(crushing, brewing)).addPage(new BookPageText(apple_trees, "apple_juice").addRelatedEntries(crushing, brewing));
		olive_trees.addPage(new BookPageText(olive_trees, "olive_trees").addRelatedEntries(crushing)).addPage(new BookPageText(olive_trees, "olive_oil").addRelatedEntries(crushing));
		ironwood_trees.addPage(new BookPageText(ironwood_trees, "ironwood_trees").addRelatedEntries(crushing, drying, alchemy, brewing)).addPage(new BookPageText(ironwood_trees, "ironberry_juice").addRelatedEntries(crushing, drying, alchemy, brewing));
		herbs.addPage(new BookPageText(herbs, "herbs").addRelatedEntries(alchemy))
		.addPage(new BookPageText(herbs, "aloe", "blood_orchid", "chamomile", "cloudsbluff", "cohosh").addRelatedEntries(alchemy))
		.addPage(new BookPageText(herbs, "core_root", "deathstalk", "ginseng", "horsetail", "marsh_mallow", "mooncap").addRelatedEntries(alchemy))
		.addPage(new BookPageText(herbs, "wind_thistle", "vanta_lily").addRelatedEntries(alchemy, crushing, vanta_oil));
		
		elixirs.addPage(new BookPageText(elixirs, "elixirs").addRelatedEntries(alchemy, vanta_oil));
		alchemy.addPage(new BookPageText(alchemy, "alchemy").addRelatedEntries(elixirs, herbs, bees, chili_peppers, ironwood_trees))
		.addPage(new BookPageText(alchemy, "alchemy_basic").addRelatedEntries(elixirs, herbs, bees, chili_peppers, ironwood_trees))
		.addPage(new BookPageText(alchemy, "alchemy_basic_1").addRelatedEntries(elixirs, herbs, bees, chili_peppers, ironwood_trees))
		.addPage(new BookPageText(alchemy, "alchemy_basic_2").addRelatedEntries(elixirs, herbs, bees, chili_peppers, ironwood_trees))
		.addPage(new BookPageText(alchemy, "alchemy_advanced").addRelatedEntries(elixirs, herbs, bees, chili_peppers, ironwood_trees))
		.addPage(new BookPageText(alchemy, "alchemy_advanced_1").addRelatedEntries(elixirs, herbs, bees, chili_peppers, ironwood_trees))
		.addPage(new BookPageText(alchemy, "alchemy_advanced_2").addRelatedEntries(elixirs, herbs, bees, chili_peppers, ironwood_trees));
		vanta_oil.addPage(new BookPageText(vanta_oil, "vanta_oil_weapons").addRelatedEntries(herbs, crushing, elixirs))
		.addPage(new BookPageText(vanta_oil, "vanta_oil_weapons_1").addRelatedEntries(herbs, crushing, elixirs));
		alcoholic_beverages.addPage(new BookPageText(alcoholic_beverages, "alcoholic_beverages").addRelatedEntries(brewing, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(alcoholic_beverages, "alcoholic_beverages_1").addRelatedEntries(brewing, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(alcoholic_beverages, "alcoholic_beverages_2").addRelatedEntries(brewing, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(alcoholic_beverages, "ale", "ale_wort").addRelatedEntries(brewing, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(alcoholic_beverages, "cider").addRelatedEntries(brewing, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(alcoholic_beverages, "iron_wine").addRelatedEntries(brewing, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(alcoholic_beverages, "mead").addRelatedEntries(brewing, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(alcoholic_beverages, "wildberry_wine").addRelatedEntries(brewing, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(alcoholic_beverages, "wine").addRelatedEntries(brewing, bees, ironwood_trees, apple_trees, grapes, wildberries));
		brewing.addPage(new BookPageText(brewing, "brewing").addRelatedEntries(alcoholic_beverages, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(brewing, "brewing_1").addRelatedEntries(alcoholic_beverages, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(brewing, "brewing_2", "brewing_3").addRelatedEntries(alcoholic_beverages, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(brewing, "brewing_4").addRelatedEntries(alcoholic_beverages, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(brewing, "brewing_5").addRelatedEntries(alcoholic_beverages, bees, ironwood_trees, apple_trees, grapes, wildberries))
		.addPage(new BookPageText(brewing, "brewing_6").addRelatedEntries(alcoholic_beverages, bees, ironwood_trees, apple_trees, grapes, wildberries));
		crushing.addPage(new BookPageText(crushing, "crushing").addRelatedEntries(bees, grapes, apple_trees, wildberries, olive_trees, ironwood_trees))
		.addPage(new BookPageText(crushing, "crushing_1").addRelatedEntries(bees, grapes, apple_trees, wildberries, olive_trees, ironwood_trees));
		drying.addPage(new BookPageText(drying, "drying").addRelatedEntries(ironwood_trees)).addPage(new BookPageText(drying, "drying_1").addRelatedEntries(ironwood_trees));
		
	}

}
