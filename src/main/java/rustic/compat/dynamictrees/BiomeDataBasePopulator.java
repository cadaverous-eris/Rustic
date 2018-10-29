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
import rustic.common.Config;
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

		int oliveWeight = (int) (Config.OLIVE_GEN_CHANCE * 500f);
		int ironWeight = (int) (Config.IRONWOOD_GEN_CHANCE * 500f);
		
		//Reuse selectors instead of filling memory with identical objects for each biome
		RandomSpeciesSelector oliveSelector = (oliveWeight == 0) ? null : new RandomSpeciesSelector().add(1000 - oliveWeight).add(olive, oliveWeight);
		RandomSpeciesSelector ironSelector = (ironWeight == 0) ? null : new RandomSpeciesSelector().add(1000 - ironWeight).add(ironwood, ironWeight);
		RandomSpeciesSelector bothSelector = (oliveWeight == 0 || ironWeight == 0) ? null : new RandomSpeciesSelector().add(1000 - (oliveWeight + ironWeight)).add(olive, oliveWeight).add(ironwood, ironWeight);
		
		Biome.REGISTRY.forEach(biome -> {
			boolean oliveSplice = (oliveSelector != null && !BiomeDictionary.hasType(biome, Type.SPOOKY) && !BiomeDictionary.hasType(biome, Type.SNOWY) && !BiomeDictionary.hasType(biome, Type.DEAD) && !BiomeDictionary.hasType(biome, Type.SAVANNA)) && (BiomeDictionary.hasType(biome, Type.FOREST) || BiomeDictionary.hasType(biome, Type.PLAINS) || BiomeDictionary.hasType(biome, Type.MOUNTAIN));
			boolean ironSplice = (ironSelector != null && !BiomeDictionary.hasType(biome, Type.SPOOKY) && !BiomeDictionary.hasType(biome, Type.DRY) && !BiomeDictionary.hasType(biome, Type.DEAD) && !BiomeDictionary.hasType(biome, Type.SAVANNA)) && (BiomeDictionary.hasType(biome, Type.FOREST) || BiomeDictionary.hasType(biome, Type.PLAINS) || BiomeDictionary.hasType(biome, Type.MOUNTAIN) || BiomeDictionary.hasType(biome, Type.SWAMP) || BiomeDictionary.hasType(biome, Type.JUNGLE) );
			
			if (oliveSplice || ironSplice) {
				dbase.setSpeciesSelector(biome, (oliveSplice && ironSplice) ? bothSelector : (ironSplice ? ironSelector : oliveSelector), Operation.SPLICE_BEFORE);
			}
		});
	}

}
