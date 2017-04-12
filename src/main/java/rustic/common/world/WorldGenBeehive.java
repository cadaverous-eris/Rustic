package rustic.common.world;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import rustic.common.Config;
import rustic.common.blocks.BlockBeehive;
import rustic.common.blocks.ModBlocks;

public class WorldGenBeehive extends WorldGenerator {

	protected static IBlockState[] orientations = new IBlockState[] { ModBlocks.BEEHIVE.getDefaultState().withProperty(BlockBeehive.FACING, EnumFacing.NORTH), ModBlocks.BEEHIVE.getDefaultState().withProperty(BlockBeehive.FACING, EnumFacing.EAST), ModBlocks.BEEHIVE.getDefaultState().withProperty(BlockBeehive.FACING, EnumFacing.SOUTH), ModBlocks.BEEHIVE.getDefaultState().withProperty(BlockBeehive.FACING, EnumFacing.WEST) };

	@Override
	public boolean generate(World worldIn, Random rand, BlockPos centerPos) {
		for (int i = 0; i < Config.MAX_BEEHIVE_ATTEMPTS; ++i) {
			int j = centerPos.getX() + rand.nextInt(8) - rand.nextInt(8);
			int l = centerPos.getZ() + rand.nextInt(8) - rand.nextInt(8);
			int k = worldIn.getHeight(j, l);
			
			if (worldIn.getBlockState(new BlockPos(j, k - 1, l)).getBlock() instanceof BlockLeaves) {
				BlockPos tempPos = new BlockPos(j, k - 1, l);
				while (worldIn.getBlockState(tempPos).getBlock() instanceof BlockLeaves) {
					tempPos = tempPos.down();
				}
				if (ModBlocks.BEEHIVE.canPlaceBlockAt(worldIn, tempPos)) {
					worldIn.setBlockState(tempPos, orientations[rand.nextInt(4)]);
					return true;
				}
			}
		}
		return false;
	}

}
