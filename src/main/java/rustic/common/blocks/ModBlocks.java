package rustic.common.blocks;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.slab.BlockDoubleSlabBase;
import rustic.common.blocks.slab.BlockSlabBase;
import rustic.common.blocks.slab.ItemBlockSlabBase;
import rustic.common.tileentity.TileEntityApiary;
import rustic.common.tileentity.TileEntityBarrel;
import rustic.common.tileentity.TileEntityCabinet;
import rustic.common.tileentity.TileEntityCrushingTub;
import rustic.common.tileentity.TileEntityLiquidBarrel;
import rustic.common.tileentity.TileEntityVase;
import rustic.core.Rustic;

public class ModBlocks {

	public static BlockPillar STONE_PILLAR;
	public static BlockPillar ANDESITE_PILLAR;
	public static BlockPillar DIORITE_PILLAR;
	public static BlockPillar GRANITE_PILLAR;
	public static BlockPillar SLATE_PILLAR;
	public static BlockBase SLATE;
	public static BlockBase SLATE_ROOF;
	public static BlockBase SLATE_TILE;
	public static BlockBase SLATE_BRICK;
	public static BlockBase SLATE_CHISELED;
	public static BlockStairsBase SLATE_ROOF_STAIRS;
	public static BlockDoubleSlabBase SLATE_ROOF_DOUBLESLAB;
	public static BlockSlabBase SLATE_ROOF_SLAB;
	public static ItemBlockSlabBase SLATE_ROOF_SLAB_ITEM;
	public static BlockBase CLAY_WALL;
	public static BlockBase CLAY_WALL_CROSS;
	public static BlockClayWallDiag CLAY_WALL_DIAG;
	public static BlockChain CHAIN;
	public static BlockCandle CANDLE;
	public static BlockChandelier CHANDELIER;
	public static BlockBeehive BEEHIVE;
	public static BlockApiary APIARY;
	public static BlockChair CHAIR_OAK;
	public static BlockChair CHAIR_BIG_OAK;
	public static BlockChair CHAIR_BIRCH;
	public static BlockChair CHAIR_SPRUCE;
	public static BlockChair CHAIR_ACACIA;
	public static BlockChair CHAIR_JUNGLE;
	public static BlockTable TABLE_OAK;
	public static BlockTable TABLE_BIG_OAK;
	public static BlockTable TABLE_BIRCH;
	public static BlockTable TABLE_SPRUCE;
	public static BlockTable TABLE_ACACIA;
	public static BlockTable TABLE_JUNGLE;
	public static BlockVase VASE;
	public static BlockBarrel BARREL;
	public static BlockLattice IRON_LATTICE;
	public static BlockLantern IRON_LANTERN;
	public static BlockBase PAINTED_WOOD_WHITE;
	public static BlockBase PAINTED_WOOD_ORANGE;
	public static BlockBase PAINTED_WOOD_MAGENTA;
	public static BlockBase PAINTED_WOOD_LIGHT_BLUE;
	public static BlockBase PAINTED_WOOD_YELLOW;
	public static BlockBase PAINTED_WOOD_LIME;
	public static BlockBase PAINTED_WOOD_PINK;
	public static BlockBase PAINTED_WOOD_GRAY;
	public static BlockBase PAINTED_WOOD_SILVER;
	public static BlockBase PAINTED_WOOD_CYAN;
	public static BlockBase PAINTED_WOOD_PURPLE;
	public static BlockBase PAINTED_WOOD_BLUE;
	public static BlockBase PAINTED_WOOD_BROWN;
	public static BlockBase PAINTED_WOOD_GREEN;
	public static BlockBase PAINTED_WOOD_RED;
	public static BlockBase PAINTED_WOOD_BLACK;
	public static BlockDoubleSlabBase SLATE_BRICK_DOUBLESLAB;
	public static BlockSlabBase SLATE_BRICK_SLAB;
	public static ItemBlockSlabBase SLATE_BRICK_SLAB_ITEM;
	public static BlockStairsBase SLATE_BRICK_STAIRS;
	public static BlockGargoyle GARGOYLE;
	public static BlockCabinet CABINET;
	public static BlockLiquidBarrel LIQUID_BARREL;
	public static BlockFertileSoil FERTILE_SOIL;
	public static BlockPlanksRustic PLANKS;
	public static BlockLogRustic LOG;
	public static BlockLeavesRustic LEAVES;
	public static BlockSaplingRustic SAPLING;
	public static BlockCrushingTub CRUSHING_TUB;

	public static void init() {
		STONE_PILLAR = new BlockPillar("stone");
		ANDESITE_PILLAR = new BlockPillar("andesite");
		DIORITE_PILLAR = new BlockPillar("diorite");
		GRANITE_PILLAR = new BlockPillar("granite");
		SLATE_PILLAR = new BlockPillar("slate");
		CHAIN = new BlockChain();
		CANDLE = new BlockCandle();
		CHANDELIER = new BlockChandelier();
		BEEHIVE = new BlockBeehive();
		APIARY = new BlockApiary();
		SLATE = (BlockBase) new BlockBase(Material.ROCK, "slate").setHardness(2.0F);
		SLATE_ROOF = (BlockBase) new BlockBase(Material.ROCK, "slate_roof").setHardness(2.0F);
		SLATE_TILE = (BlockBase) new BlockBase(Material.ROCK, "slate_tile").setHardness(2.0F);
		SLATE_BRICK = (BlockBase) new BlockBase(Material.ROCK, "slate_brick").setHardness(2.0F);
		SLATE_CHISELED = (BlockBase) new BlockBase(Material.ROCK, "slate_chiseled").setHardness(2.0F);
		SLATE_ROOF_STAIRS = new BlockStairsBase(SLATE_ROOF.getDefaultState(), "stairs_slate_roof");
		SLATE_ROOF_DOUBLESLAB = new BlockDoubleSlabBase(Material.ROCK, "slate_roof_doubleslab");
		SLATE_ROOF_SLAB = new BlockSlabBase(Material.ROCK, "slate_roof_slab", SLATE_ROOF_DOUBLESLAB);
		SLATE_ROOF_DOUBLESLAB.setSlab(SLATE_ROOF_SLAB);
		SLATE_ROOF_SLAB_ITEM = new ItemBlockSlabBase(SLATE_ROOF_SLAB, SLATE_ROOF_DOUBLESLAB);
		SLATE_BRICK_STAIRS = new BlockStairsBase(SLATE_BRICK.getDefaultState(), "stairs_slate_brick");
		SLATE_BRICK_DOUBLESLAB = new BlockDoubleSlabBase(Material.ROCK, "slate_brick_doubleslab");
		SLATE_BRICK_SLAB = new BlockSlabBase(Material.ROCK, "slate_brick_slab", SLATE_BRICK_DOUBLESLAB);
		SLATE_BRICK_DOUBLESLAB.setSlab(SLATE_BRICK_SLAB);
		SLATE_BRICK_SLAB_ITEM = new ItemBlockSlabBase(SLATE_BRICK_SLAB, SLATE_BRICK_DOUBLESLAB);
		CLAY_WALL = ((BlockBase) new BlockBase(Material.CLAY, "clay_wall").setHardness(1F)).setBlockSoundType(SoundType.GROUND);
		CLAY_WALL_CROSS = ((BlockBase) new BlockBase(Material.CLAY, "clay_wall_cross").setHardness(1F)).setBlockSoundType(SoundType.GROUND);
		CLAY_WALL_DIAG = (BlockClayWallDiag) new BlockClayWallDiag().setHardness(1F);
		CHAIR_OAK = new BlockChair("oak");
		CHAIR_BIG_OAK = new BlockChair("big_oak");
		CHAIR_BIRCH = new BlockChair("birch");
		CHAIR_SPRUCE = new BlockChair("spruce");
		CHAIR_ACACIA = new BlockChair("acacia");
		CHAIR_JUNGLE = new BlockChair("jungle");
		TABLE_OAK = new BlockTable("oak");
		TABLE_BIG_OAK = new BlockTable("big_oak");
		TABLE_BIRCH = new BlockTable("birch");
		TABLE_SPRUCE = new BlockTable("spruce");
		TABLE_ACACIA = new BlockTable("acacia");
		TABLE_JUNGLE = new BlockTable("jungle");
		VASE = new BlockVase();
		BARREL = new BlockBarrel();
		IRON_LATTICE = new BlockLattice(Material.IRON, "iron_lattice");
		IRON_LANTERN = new BlockLantern(Material.IRON, "iron_lantern");
		PAINTED_WOOD_WHITE = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_white").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_ORANGE = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_orange").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_MAGENTA = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_magenta").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_LIGHT_BLUE = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_light_blue")
				.setHardness(2.0F)).setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_YELLOW = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_yellow").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_LIME = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_lime").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_PINK = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_pink").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_GRAY = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_gray").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_SILVER = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_silver").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_CYAN = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_cyan").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_PURPLE = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_purple").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_BLUE = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_blue").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_BROWN = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_brown").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_GREEN = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_green").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_RED = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_red").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		PAINTED_WOOD_BLACK = ((BlockBase) new BlockBase(Material.WOOD, "painted_wood_black").setHardness(2.0F))
				.setBlockSoundType(SoundType.WOOD);
		GARGOYLE = new BlockGargoyle();
		CABINET = new BlockCabinet();
		LIQUID_BARREL = new BlockLiquidBarrel();
		FERTILE_SOIL = new BlockFertileSoil();
		PLANKS = new BlockPlanksRustic();
		LOG = new BlockLogRustic();
		LEAVES = new BlockLeavesRustic();
		SAPLING = new BlockSaplingRustic();
		CRUSHING_TUB = new BlockCrushingTub();

		GameRegistry.registerTileEntity(TileEntityApiary.class, Rustic.MODID + ":tileEntityApiary");
		GameRegistry.registerTileEntity(TileEntityVase.class, Rustic.MODID + ":tileEntityVase");
		GameRegistry.registerTileEntity(TileEntityBarrel.class, Rustic.MODID + ":tileEntityBarrel");
		GameRegistry.registerTileEntity(TileEntityCabinet.class, Rustic.MODID + ":tileEntityCabinet");
		GameRegistry.registerTileEntity(TileEntityLiquidBarrel.class, Rustic.MODID + ":tileEntityLiquidBarrel");
		GameRegistry.registerTileEntity(TileEntityCrushingTub.class, Rustic.MODID + ":tileEntityCrushingTub");
	}

	@SideOnly(Side.CLIENT)
	public static void initModels() {
		STONE_PILLAR.initModel();
		ANDESITE_PILLAR.initModel();
		DIORITE_PILLAR.initModel();
		GRANITE_PILLAR.initModel();
		SLATE_PILLAR.initModel();
		CHAIN.initModel();
		CANDLE.initModel();
		CHANDELIER.initModel();
		BEEHIVE.initModel();
		APIARY.initModel();
		SLATE.initModel();
		SLATE_ROOF.initModel();
		SLATE_TILE.initModel();
		SLATE_BRICK.initModel();
		SLATE_CHISELED.initModel();
		SLATE_ROOF_STAIRS.initModel();
		SLATE_ROOF_DOUBLESLAB.initModel();
		SLATE_ROOF_SLAB.initModel();
		SLATE_ROOF_SLAB_ITEM.initModel();
		CLAY_WALL.initModel();
		CLAY_WALL_CROSS.initModel();
		CLAY_WALL_DIAG.initModel();
		CHAIR_OAK.initModel();
		CHAIR_BIG_OAK.initModel();
		CHAIR_BIRCH.initModel();
		CHAIR_SPRUCE.initModel();
		CHAIR_ACACIA.initModel();
		CHAIR_JUNGLE.initModel();
		TABLE_OAK.initModel();
		TABLE_BIG_OAK.initModel();
		TABLE_BIRCH.initModel();
		TABLE_SPRUCE.initModel();
		TABLE_ACACIA.initModel();
		TABLE_JUNGLE.initModel();
		VASE.initModel();
		BARREL.initModel();
		IRON_LATTICE.initModel();
		IRON_LANTERN.initModel();
		PAINTED_WOOD_WHITE.initModel();
		PAINTED_WOOD_ORANGE.initModel();
		PAINTED_WOOD_MAGENTA.initModel();
		PAINTED_WOOD_LIGHT_BLUE.initModel();
		PAINTED_WOOD_YELLOW.initModel();
		PAINTED_WOOD_LIME.initModel();
		PAINTED_WOOD_PINK.initModel();
		PAINTED_WOOD_GRAY.initModel();
		PAINTED_WOOD_SILVER.initModel();
		PAINTED_WOOD_CYAN.initModel();
		PAINTED_WOOD_PURPLE.initModel();
		PAINTED_WOOD_BLUE.initModel();
		PAINTED_WOOD_BROWN.initModel();
		PAINTED_WOOD_GREEN.initModel();
		PAINTED_WOOD_RED.initModel();
		PAINTED_WOOD_BLACK.initModel();
		SLATE_BRICK_DOUBLESLAB.initModel();
		SLATE_BRICK_SLAB.initModel();
		SLATE_BRICK_SLAB_ITEM.initModel();
		SLATE_BRICK_STAIRS.initModel();
		GARGOYLE.initModel();
		CABINET.initModel();
		LIQUID_BARREL.initModel();
		FERTILE_SOIL.initModel();
		PLANKS.initModel();
		LOG.initModel();
		LEAVES.initModel();
		SAPLING.initModel();
		CRUSHING_TUB.initModel();
	}

}
