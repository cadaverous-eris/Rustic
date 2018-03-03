package rustic.compat.dynamictrees.worldgen;

import java.util.HashMap;
import java.util.Random;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeSpeciesSelector;
import com.ferreusveritas.dynamictrees.trees.Species;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import rustic.core.Rustic;

public class BiomeSpeciesSelector implements IBiomeSpeciesSelector {
	
	Species olive, ironwood;
	
	HashMap<Integer, DecisionProvider> fastTreeLookup = new HashMap<Integer, DecisionProvider>();
	
	@Override
	public ResourceLocation getName() {
		return new ResourceLocation(Rustic.MODID, "default");
	}
	
	@Override
	public int getPriority() {
		return 3;
	}
	
	@Override
	public Decision getSpecies(World world, Biome biome, BlockPos pos, IBlockState state, Random rand) {
		if (biome == null) return new Decision();
		
		int biomeId = Biome.getIdForBiome(biome);
		DecisionProvider select;
				
		if (fastTreeLookup.containsKey(biomeId)) {
			select = fastTreeLookup.get(biomeId); // Speedily look up the selector for the biome id
		} else {
			select = new RandomDecision(rand).addUnhandled(50);
			boolean flag = false;
			
			if ((!BiomeDictionary.hasType(biome, Type.SNOWY)) && (BiomeDictionary.hasType(biome, Type.FOREST) || BiomeDictionary.hasType(biome, Type.PLAINS) || BiomeDictionary.hasType(biome, Type.MOUNTAIN))) {
				((RandomDecision) select).addSpecies(olive, 2);
				flag = true;
			}
			if ((!BiomeDictionary.hasType(biome, Type.DRY)) && (BiomeDictionary.hasType(biome, Type.FOREST) || BiomeDictionary.hasType(biome, Type.PLAINS) || BiomeDictionary.hasType(biome, Type.MOUNTAIN) || BiomeDictionary.hasType(biome, Type.SWAMP) || BiomeDictionary.hasType(biome, Type.JUNGLE))) {
				((RandomDecision) select).addSpecies(ironwood, 1);
				flag = true;
			}
			
			if (!flag) select = new StaticDecision(new Decision());
			
			fastTreeLookup.put(biomeId, select); // Cache decision for future use
		}
		
		return select.getDecision();
	}

	@Override
	public void init() {
		olive = TreeRegistry.findSpecies(new ResourceLocation(Rustic.MODID, "olive"));
		ironwood = TreeRegistry.findSpecies(new ResourceLocation(Rustic.MODID, "ironwood"));
	}

}
