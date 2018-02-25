package rustic.common.world;

import java.util.Random;

import com.google.common.base.Predicate;

import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.IWorldGenerator;
import rustic.common.Config;
import rustic.common.blocks.ModBlocks;

public class WorldGeneratorRustic implements IWorldGenerator {

	private WorldGenMinable slate = (!Config.ENABLE_SLATE) ? null
			: (Config.NETHER_SLATE) ? new WorldGenMinable(ModBlocks.SLATE.getDefaultState(), Config.SLATE_VEIN_SIZE, new NetherPredicate())
					: new WorldGenMinable(ModBlocks.SLATE.getDefaultState(), Config.SLATE_VEIN_SIZE);
	private WorldGenBeehive beehives = new WorldGenBeehive();
	private WorldGenAllTrees trees = new WorldGenAllTrees();
	private WorldGenSurfaceHerbs surfaceHerbs = new WorldGenSurfaceHerbs();
	private WorldGenCaveHerbs caveHerbs = new WorldGenCaveHerbs();
	private WorldGenNetherHerbs netherHerbs = new WorldGenNetherHerbs();
	private WorldGenWildberries wildberries = new WorldGenWildberries();

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			net.minecraft.world.gen.IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		BlockPos chunkCenter = new BlockPos(chunkX * 16 + 8, world.getHeight(chunkX * 16 + 8, chunkZ * 16 + 8),
				chunkZ * 16 + 8);

		if (Config.OVERWORLD_GENERATION_WHITELIST.contains(world.provider.getDimension()) && (world.getWorldType() != WorldType.FLAT
				|| world.getWorldInfo().getGeneratorOptions().contains("decoration"))) {
			if (random.nextFloat() < Config.WILDBERRY_GEN_CHANCE) {
				wildberries.generate(world, random, chunkCenter);
			}

			if (random.nextFloat() < Config.HERB_GEN_CHANCE) {
				surfaceHerbs.generate(world, random, chunkCenter);
			}
			if (random.nextFloat() < Config.HERB_GEN_CHANCE) {
				caveHerbs.generate(world, random, chunkCenter);
			}

			trees.generate(world, random, chunkCenter);

			if (!world.getBiome(chunkCenter).isSnowyBiome() && random.nextFloat() < Config.BEEHIVE_GEN_CHANCE) {
				beehives.generate(world, random, chunkCenter);
			}

			if (Config.ENABLE_SLATE && !Config.NETHER_SLATE) {
				for (int i = 0; i < Config.SLATE_VEINS_PER_CHUNK; i++) {
					int x = chunkX * 16 + random.nextInt(16);
					int y = random.nextInt(80) + 4;
					int z = chunkZ * 16 + random.nextInt(16);
					slate.generate(world, random, new BlockPos(x, y, z));
				}
			}
		} else if (Config.NETHER_GENERATION_WHITELIST.contains(world.provider.getDimension())) {
			if (random.nextFloat() < Config.HERB_GEN_CHANCE) {
				netherHerbs.generate(world, random, chunkCenter);
			}

			if (Config.ENABLE_SLATE && Config.NETHER_SLATE) {
				for (int i = 0; i < Config.SLATE_VEINS_PER_CHUNK; i++) {
					int x = chunkX * 16 + random.nextInt(16);
					int y = random.nextInt(112) + 8;
					int z = chunkZ * 16 + random.nextInt(16);
					slate.generate(world, random, new BlockPos(x, y, z));
				}
			}

		}
	}

	static class NetherPredicate implements Predicate<IBlockState> {
		
		private NetherPredicate() {}

		@Override
		public boolean apply(IBlockState state) {
			return (state != null && state.getBlock() == Blocks.NETHERRACK);
		}
		
	}

}
