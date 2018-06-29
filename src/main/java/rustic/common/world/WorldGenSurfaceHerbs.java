package rustic.common.world;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import rustic.common.Config;
import rustic.common.blocks.crops.BlockHerbBase;
import rustic.common.blocks.crops.Herbs;

public class WorldGenSurfaceHerbs extends WorldGenerator {

	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		Biome biome = world.getBiome(pos);
		
		if (biome.isSnowyBiome()) {
			return false;
		}
		
		BlockHerbBase herb = Herbs.getRandomHerbForBiome(biome, rand);
		
		if (herb == null) {
			return false;
		}
		
		boolean ret = false;
		for (int i = 0; i < Config.MAX_HERB_ATTEMPTS; i++) {
			int x = pos.getX() + rand.nextInt(7) - rand.nextInt(7);
			int z = pos.getZ() + rand.nextInt(7) - rand.nextInt(7);
			BlockPos genPos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
			
			if (generateHerb(world, rand, genPos, herb)) {
				ret = true;
			}
		}
		
		return ret;
	}
	
	private boolean generateHerb(World world, Random rand, BlockPos pos, BlockHerbBase herb) {
		IBlockState state = world.getBlockState(pos.down());
		if (world.isAirBlock(pos)
				&& state.getBlock().canSustainPlant(state, world, pos.down(), EnumFacing.UP, herb)) {
			world.setBlockState(pos, herb.getDefaultState().withProperty(BlockHerbBase.AGE, 3));
			return true;
		}
		return false;
	}

}
