package rustic.compat.dynamictrees.trees;

import java.util.List;

import com.ferreusveritas.dynamictrees.blocks.BlockDynamicSapling;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.trees.TreeFamily;

import net.minecraft.block.Block;
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

public class TreeIronwood extends TreeFamily {
	
	public class SpeciesIronwood extends Species {
		
		SpeciesIronwood(TreeFamily treeFamily) {
			super(treeFamily.getName(), treeFamily, DynamicTreesCompat.ironwoodLeavesProperties);
			
			setBasicGrowingParameters(0.35f, 14.0f, 4, 4, 1.25f);
			
			setDynamicSapling(new BlockDynamicSapling("ironwoodsapling").getDefaultState());
			
			envFactor(Type.COLD, 0.5f);
			envFactor(Type.DRY, 0.5f);
			envFactor(Type.HOT, 0.75f);
			envFactor(Type.FOREST, 1.05f);
			
			generateSeed();
			
			setupStandardSeedDropping();
			addDropCreator(new DropCreatorFruit(ModItems.IRONBERRIES, 48));
		}
		
		@Override
		public boolean isBiomePerfect(Biome biome) {
			return BiomeDictionary.hasType(biome, Type.FOREST);
		}
		
	}
	
	public TreeIronwood() {
		super(new ResourceLocation(Rustic.MODID, "ironwood"));
		
		IBlockState primLog = ModBlocks.LOG.getDefaultState().withProperty(BlockLogRustic.VARIANT, BlockPlanksRustic.EnumType.IRONWOOD);
		setPrimitiveLog(primLog, new ItemStack(ModBlocks.LOG, 1, BlockPlanksRustic.EnumType.IRONWOOD.getMetadata()));
		
		DynamicTreesCompat.ironwoodLeavesProperties.setTree(this);
		
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
		blockList.add(getCommonSpecies().getDynamicSapling().getBlock());
		return super.getRegisterableBlocks(blockList);
	}
	
}
