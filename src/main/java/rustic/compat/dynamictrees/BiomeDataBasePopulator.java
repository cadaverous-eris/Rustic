package rustic.compat.dynamictrees;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.api.worldgen.IBiomeDataBasePopulator;
import com.ferreusveritas.dynamictrees.api.worldgen.BiomePropertySelectors.RandomSpeciesSelector;
import com.ferreusveritas.dynamictrees.trees.Species;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase;
import com.ferreusveritas.dynamictrees.worldgen.BiomeDataBase.Operation;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import rustic.core.Rustic;

public class BiomeDataBasePopulator implements IBiomeDataBasePopulator {

	private static Species olive, ironwood;
	
	private static void createStaticAliases() {
		olive = TreeRegistry.findSpecies(new ResourceLocation(Rustic.MODID, "olive"));
		ironwood = TreeRegistry.findSpecies(new ResourceLocation(Rustic.MODID, "ironwood"));
	}
	
	@Override
	public void populate(BiomeDataBase dbase) {
		if (olive == null) {
			createStaticAliases();
		}
		
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
