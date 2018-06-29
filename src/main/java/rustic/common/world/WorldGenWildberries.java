package rustic.common.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import rustic.common.Config;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.crops.BlockBerryBush;

public class WorldGenWildberries extends WorldGenerator {
	
	public static List<Type> biomeBlacklist = new ArrayList<Type>();
	static {
		biomeBlacklist.add(Type.COLD);
		biomeBlacklist.add(Type.SNOWY);
		biomeBlacklist.add(Type.SANDY);
		biomeBlacklist.add(Type.SAVANNA);
		biomeBlacklist.add(Type.MESA);
		biomeBlacklist.add(Type.MUSHROOM);
		biomeBlacklist.add(Type.NETHER);
		biomeBlacklist.add(Type.END);
		biomeBlacklist.add(Type.DEAD);
		biomeBlacklist.add(Type.WASTELAND);
	}
	
	@Override
	public boolean generate(World world, Random rand, BlockPos pos) {
		Biome biome = world.getBiome(pos);
		
		for (Type type : biomeBlacklist) {
			if (BiomeDictionary.hasType(biome, type)) {
				return false;
			}
		}
		
		boolean ret = false;
		for (int i = 0; i < Config.MAX_WILDBERRY_ATTEMPTS; i++) {
			int x = pos.getX() + rand.nextInt(7) - rand.nextInt(7);
			int z = pos.getZ() + rand.nextInt(7) - rand.nextInt(7);
			BlockPos genPos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
			
			if (generateBush(world, rand, genPos)) {
				ret = true;
			}
		}
		
		return ret;
	}
	
	private boolean generateBush(World world, Random rand, BlockPos pos) {
		//IBlockState state = world.getBlockState(pos.down());
		if (ModBlocks.WILDBERRY_BUSH.canPlaceBlockAt(world, pos)) {
			world.setBlockState(pos, ModBlocks.WILDBERRY_BUSH.getDefaultState().withProperty(BlockBerryBush.BERRIES, true));
			return true;
		}
		return false;
	}

}
