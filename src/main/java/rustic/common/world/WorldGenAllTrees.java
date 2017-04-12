package rustic.common.world;

import java.util.Random;
import java.util.Set;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import rustic.common.Config;
import rustic.common.blocks.ModBlocks;

public class WorldGenAllTrees extends WorldGenerator {
	
	private WorldGenOliveTree olives = new WorldGenOliveTree(true);
	private WorldGenIronwoodTree ironwoods = new WorldGenIronwoodTree(true);

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		boolean ret = false;

		if (generateOliveTrees(world, rand, pos)) {
			ret = true;
		}
		
		if (generateIronwoodTrees(world, rand, pos)) {
			ret = true;
		}
		
		return ret;
	}
	
	private boolean generateOliveTrees(World world, Random rand, BlockPos pos) {
		boolean generated = false;
		Biome biome = world.getBiome(pos);
		boolean validBiome = (!BiomeDictionary.hasType(biome, Type.SNOWY)) && (BiomeDictionary.hasType(biome, Type.FOREST) || BiomeDictionary.hasType(biome, Type.PLAINS) || BiomeDictionary.hasType(biome, Type.MOUNTAIN));

		if (validBiome && rand.nextFloat() < Config.OLIVE_GEN_CHANCE) {
			for (int i = 0; i < Config.MAX_OLIVE_GEN_ATTEMPTS; i++) {
				int x = pos.getX() + (5 - rand.nextInt(12));
				int z = pos.getZ() + (5 - rand.nextInt(12));
				int y = world.getHeight(x, z);
				BlockPos genPos = new BlockPos(x, y, z);
				
				if (!world.canSnowAt(genPos, true) && ModBlocks.SAPLING.canPlaceBlockAt(world, genPos) && olives.generate(world, rand, genPos)) {
					generated = true;
				}
			}
		}
		
		return generated;
	}
	
	private boolean generateIronwoodTrees(World world, Random rand, BlockPos pos) {
		boolean generated = false;
		Biome biome = world.getBiome(pos);
		boolean validBiome = (!BiomeDictionary.hasType(biome, Type.DRY)) && (BiomeDictionary.hasType(biome, Type.FOREST) || BiomeDictionary.hasType(biome, Type.PLAINS) || BiomeDictionary.hasType(biome, Type.MOUNTAIN) || BiomeDictionary.hasType(biome, Type.SWAMP) || BiomeDictionary.hasType(biome, Type.JUNGLE));
		
		if (validBiome && rand.nextFloat() < Config.IRONWOOD_GEN_CHANCE) {
			for (int i = 0; i < Config.MAX_IRONWOOD_GEN_ATTEMPTS; i++) {
				int x = pos.getX() + (5 - rand.nextInt(12));
				int z = pos.getZ() + (5 - rand.nextInt(12));
				int y = world.getHeight(x, z);
				BlockPos genPos = new BlockPos(x, y, z);
				
				if (ModBlocks.SAPLING.canPlaceBlockAt(world, genPos) && ironwoods.generate(world, rand, genPos)) {
					generated = true;
				}
			}
		}
		
		return generated;
	}

}
