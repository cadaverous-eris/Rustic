package rustic.compat.dynamictrees.trees;

import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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

public class TreeOlive extends TreeFamily {
	
	public class SpeciesOlive extends Species {
		
		SpeciesOlive(TreeFamily treeFamily) {
			super(treeFamily.getName(), treeFamily, DynamicTreesCompat.oliveLeavesProperties);
			
			setBasicGrowingParameters(0.3f, 12.0f, upProbability, lowestBranchHeight, 1.0f);
						
			envFactor(Type.COLD, 0.75f);
			envFactor(Type.SNOWY, 0.25f);
			envFactor(Type.HOT, 0.50f);
			envFactor(Type.PLAINS, 1.05f);
			
			generateSeed();
			
			setupStandardSeedDropping();
			addDropCreator(new DropCreatorFruit(ModItems.OLIVES, 18));
		}
		
		@Override
		public boolean isBiomePerfect(Biome biome) {
			return BiomeDictionary.hasType(biome, Type.PLAINS);
		}
		
	}
	
	public TreeOlive() {
		super(new ResourceLocation(Rustic.MODID, "olive"));
		
		IBlockState primLog = ModBlocks.LOG.getDefaultState().withProperty(BlockLogRustic.VARIANT, BlockPlanksRustic.EnumType.OLIVE);
		setPrimitiveLog(primLog, new ItemStack(ModBlocks.LOG, 1, BlockPlanksRustic.EnumType.OLIVE.getMetadata()));
		
		DynamicTreesCompat.oliveLeavesProperties.setTree(this);
		
		this.addConnectableVanillaLeaves((state) -> {
			return state.getBlock() instanceof BlockLeavesRustic && state.getValue(BlockLeavesRustic.VARIANT) == BlockPlanksRustic.EnumType.OLIVE;
		});
	}
	
	@Override
	public void createSpecies() {
		setCommonSpecies(new SpeciesOlive(this));
	}
	
}
