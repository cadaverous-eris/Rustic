package rustic.compat.dynamictrees;

import java.util.ArrayList;

import com.ferreusveritas.dynamictrees.ModConfigs;
import com.ferreusveritas.dynamictrees.ModItems;
import com.ferreusveritas.dynamictrees.ModRecipes;
import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.WorldGenRegistry;
import com.ferreusveritas.dynamictrees.api.client.ModelHelper;
import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors.RandomSpeciesSelector;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicLeaves;
import com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling;
import com.ferreusveritas.dynamictrees.blocks.LeavesProperties;
import com.ferreusveritas.dynamictrees.items.DendroPotion.DendroPotionType;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase.Operation;
import com.ferreusveritas.dynamictrees.worldgen.TreeGenerator;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;
import rustic.common.blocks.BlockLeavesRustic;
import rustic.common.blocks.BlockPlanksRustic;
import rustic.common.blocks.ModBlocks;
import rustic.compat.dynamictrees.trees.TreeIronwood;
import rustic.compat.dynamictrees.trees.TreeOlive;
import rustic.core.Rustic;

public class DynamicTreesCompat {
	
	public static ILeavesProperties oliveLeavesProperties, ironwoodLeavesProperties;
	public static TreeFamily oliveTree, ironwoodTree;
	
	public static void preInit() {
		IForgeRegistry<Block> blockRegistry = GameRegistry.findRegistry(Block.class);
		IForgeRegistry<Item> itemRegistry = GameRegistry.findRegistry(Item.class);
		
		oliveLeavesProperties = new LeavesProperties(
				ModBlocks.LEAVES.getDefaultState().withProperty(BlockLeavesRustic.VARIANT, BlockPlanksRustic.EnumType.OLIVE),
				new ItemStack(Item.getItemFromBlock(ModBlocks.LEAVES), 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()));
		ironwoodLeavesProperties = new LeavesProperties(
				ModBlocks.LEAVES.getDefaultState().withProperty(BlockLeavesRustic.VARIANT, BlockPlanksRustic.EnumType.IRONWOOD),
				new ItemStack(Item.getItemFromBlock(ModBlocks.LEAVES), 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()));
		
		TreeHelper.getLeavesBlockForSequence(Rustic.MODID, 0, oliveLeavesProperties);
		TreeHelper.getLeavesBlockForSequence(Rustic.MODID, 1, ironwoodLeavesProperties);
		
		oliveTree = new TreeOlive();
		ironwoodTree = new TreeIronwood();
		
		oliveTree.registerSpecies(Species.REGISTRY);
		ironwoodTree.registerSpecies(Species.REGISTRY);
		
		ArrayList<Block> treeBlocks = new ArrayList<>();
		oliveTree.getRegisterableBlocks(treeBlocks);
		ironwoodTree.getRegisterableBlocks(treeBlocks);
		treeBlocks.addAll(TreeHelper.getLeavesMapForModId(Rustic.MODID).values());
		blockRegistry.registerAll(treeBlocks.toArray(new Block[treeBlocks.size()]));
		
		ArrayList<Item> treeItems = new ArrayList<>();
		oliveTree.getRegisterableItems(treeItems);
		ironwoodTree.getRegisterableItems(treeItems);
		itemRegistry.registerAll(treeItems.toArray(new Item[treeItems.size()]));
		
		if (ModConfigs.replaceVanillaSapling) {
			MinecraftForge.EVENT_BUS.register(new SaplingReplacer());
		}
	}
	
	public static void init() {
		registerBiomeHandlers();
	}
	
	@SideOnly(Side.CLIENT)
	public static void clientPreInit() {
		ModelHelper.regModel(oliveTree.getDynamicBranch());
		ModelHelper.regModel(ironwoodTree.getDynamicBranch());
		ModelHelper.regModel(oliveTree.getCommonSpecies().getSeed());
		ModelHelper.regModel(ironwoodTree.getCommonSpecies().getSeed());
		ModelHelper.regModel(oliveTree);
		ModelHelper.regModel(ironwoodTree);
		TreeHelper.getLeavesMapForModId(Rustic.MODID).forEach((key,leaves) -> ModelLoader.setCustomStateMapper(leaves, new StateMap.Builder().ignore(BlockLeaves.DECAYABLE).build()));
	}
	
	@SideOnly(Side.CLIENT)
	public static void clientInit() {
		final int magenta = 0x00FF00FF; // for errors.. because magenta sucks.
		
		for (BlockDynamicLeaves leaves : TreeHelper.getLeavesMapForModId(Rustic.MODID).values()) {
			ModelHelper.regColorHandler(leaves, new IBlockColor() {
				@Override
				public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
					//boolean inWorld = worldIn != null && pos != null;
					//IBlockState primLeaves = leaves.getProperties(state).getPrimitiveLeaves();
					
					Block block = state.getBlock();

					if(TreeHelper.isLeaves(block)) {
						return ((BlockDynamicLeaves) block).getProperties(state).foliageColorMultiplier(state, worldIn, pos);
					}
					return magenta;
				}
			});
		}

		BlockDynamicSapling oliveSapling = (BlockDynamicSapling) oliveTree.getCommonSpecies().getDynamicSapling().getBlock();
		ModelHelper.regDynamicSaplingColorHandler(oliveSapling);
		BlockDynamicSapling ironwoodSapling = (BlockDynamicSapling) ironwoodTree.getCommonSpecies().getDynamicSapling().getBlock();
		ModelHelper.regDynamicSaplingColorHandler(ironwoodSapling);
	}
	
	private static void registerBiomeHandlers() {
		if(WorldGenRegistry.isWorldGenEnabled()) {
			Species olive = TreeRegistry.findSpecies(new ResourceLocation(Rustic.MODID, "olive"));
			Species ironwood = TreeRegistry.findSpecies(new ResourceLocation(Rustic.MODID, "ironwood"));
			
			BiomeDataBase dbase = TreeGenerator.getTreeGenerator().biomeDataBase;
			
			Biome.REGISTRY.forEach(biome -> {
				RandomSpeciesSelector selector = new RandomSpeciesSelector().add(75);
				boolean flag = false;
				
				if ((!BiomeDictionary.hasType(biome, Type.SNOWY) && !BiomeDictionary.hasType(biome, Type.DEAD) && !BiomeDictionary.hasType(biome, Type.SAVANNA)) && (BiomeDictionary.hasType(biome, Type.FOREST) || BiomeDictionary.hasType(biome, Type.PLAINS) || BiomeDictionary.hasType(biome, Type.MOUNTAIN))) {
					selector.add(olive, 1);
					flag = true;
				}
				if ((!BiomeDictionary.hasType(biome, Type.DRY) && !BiomeDictionary.hasType(biome, Type.DEAD) && !BiomeDictionary.hasType(biome, Type.SAVANNA)) && (BiomeDictionary.hasType(biome, Type.FOREST) || BiomeDictionary.hasType(biome, Type.PLAINS) || BiomeDictionary.hasType(biome, Type.MOUNTAIN) || BiomeDictionary.hasType(biome, Type.SWAMP) || BiomeDictionary.hasType(biome, Type.JUNGLE))) {
					selector.add(ironwood, 1);
					flag = true;
				}
				
				if (flag) {
					dbase.setSpeciesSelector(biome, selector, Operation.SPLICE_BEFORE);
				}
			});
		}
	}
	
	public static Item getOliveSeed() {
		return oliveTree.getCommonSpecies().getSeed();
	}
	
	public static Item getIronwoodSeed() {
		return ironwoodTree.getCommonSpecies().getSeed();
	}
	
	public static Item getAppleSeed() {
		return TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "apple")).getSeed();
	}
	
	public static void addRecipes() {
		ItemStack oliveSeeds = oliveTree.getCommonSpecies().getSeedStack(1);
		ItemStack ironwoodSeeds = ironwoodTree.getCommonSpecies().getSeedStack(1);
		
		ItemStack transformationPotion = new ItemStack(ModItems.dendroPotion, 1, DendroPotionType.TRANSFORM.getIndex());
		
		ModRecipes.createDirtBucketExchangeRecipes(new ItemStack(ModBlocks.SAPLING, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()), oliveSeeds, true);
		ModRecipes.createDirtBucketExchangeRecipes(new ItemStack(ModBlocks.SAPLING, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()), ironwoodSeeds, true);
		
		BrewingRecipeRegistry.addRecipe(transformationPotion, oliveSeeds, ModItems.dendroPotion.setTargetTree(transformationPotion.copy(), oliveTree));
		BrewingRecipeRegistry.addRecipe(transformationPotion, ironwoodSeeds, ModItems.dendroPotion.setTargetTree(transformationPotion.copy(), ironwoodTree));
	}
	
	public static boolean replaceWorldGen() {
		return WorldGenRegistry.isWorldGenEnabled();
	}

}
