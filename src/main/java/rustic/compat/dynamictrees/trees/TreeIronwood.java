package rustic.compat.dynamictrees.trees;

import java.util.List;
import java.util.function.BiFunction;

import com.ferreusveritas.dynamictrees.blocks.BlockSurfaceRoot;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenClearVolume;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenFlareBottom;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenMound;
import com.ferreusveritas.dynamictrees.systems.featuregen.FeatureGenRoots;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import rustic.common.blocks.BlockLeavesRustic;
import rustic.common.blocks.BlockLogRustic;
import rustic.common.blocks.BlockPlanksRustic;
import rustic.common.blocks.ModBlocks;
import rustic.common.items.ModItems;
import rustic.compat.dynamictrees.DropCreatorFruit;
import rustic.compat.dynamictrees.DynamicTreesCompat;
import rustic.core.Rustic;

public class TreeIronwood extends TreeFamily {
	
	public class SpeciesIronwood extends Species {
		
		SpeciesIronwood(TreeFamily treeFamily) {
			super(treeFamily.getName(), treeFamily, DynamicTreesCompat.ironwoodLeavesProperties);
			
			setBasicGrowingParameters(0.4f, 14.0f, 4, 4, 1.0f);
						
			envFactor(Type.COLD, 0.5f);
			envFactor(Type.DRY, 0.5f);
			envFactor(Type.HOT, 0.75f);
			envFactor(Type.FOREST, 1.05f);
			
			generateSeed();
			
			setupStandardSeedDropping();
			addDropCreator(new DropCreatorFruit(ModItems.IRONBERRIES, 48));
			
			//Add species features
			addGenFeature(new FeatureGenClearVolume(6));//Clear a spot for the thick tree trunk
			addGenFeature(new FeatureGenFlareBottom());//Flare the bottom
			addGenFeature(new FeatureGenMound(5));//Establish mounds
			addGenFeature(new FeatureGenRoots(11).setScaler(getRootScaler()));//Finally Generate Roots
		}
		
		protected BiFunction<Integer, Integer, Integer> getRootScaler() {
			return (inRadius, trunkRadius) -> {
				float scale = MathHelper.clamp(trunkRadius >= 11 ? (trunkRadius / 20f) : 0, 0, 1);
				return (int) (inRadius * scale);
			};
		}
		
		@Override
		public boolean isBiomePerfect(Biome biome) {
			return BiomeDictionary.hasType(biome, Type.FOREST);
		}
		
		@Override
		public boolean isThick() {
			return true;
		}
		
	}
	
	
	BlockSurfaceRoot surfaceRootBlock;
	
	public TreeIronwood() {
		super(new ResourceLocation(Rustic.MODID, "ironwood"));
		
		IBlockState primLog = ModBlocks.LOG.getDefaultState().withProperty(BlockLogRustic.VARIANT, BlockPlanksRustic.EnumType.IRONWOOD);
		setPrimitiveLog(primLog, new ItemStack(ModBlocks.LOG, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()));
		
		DynamicTreesCompat.ironwoodLeavesProperties.setTree(this);
		
		surfaceRootBlock = new BlockSurfaceRoot(Material.WOOD, getName() + "root");
		
		this.addConnectableVanillaLeaves((state) -> {
			return state.getBlock() instanceof BlockLeavesRustic && state.getValue(BlockLeavesRustic.VARIANT) == BlockPlanksRustic.EnumType.IRONWOOD;
		});
	}
	
	@Override
	public void createSpecies() {
		setCommonSpecies(new SpeciesIronwood(this));
	}
	
	@Override
	public List<Block> getRegisterableBlocks(List<Block> blockList) {
		blockList.add(surfaceRootBlock);
		return super.getRegisterableBlocks(blockList);
	}
	
	@Override
	public boolean isThick() {
		return true;
	}
	
	@Override
	public BlockSurfaceRoot getSurfaceRoots() {
		return surfaceRootBlock;
	}
	
}
