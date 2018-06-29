package rustic.compat.dynamictrees;

import java.util.List;
import java.util.Random;

import com.ferreusveritas.dynamictrees.api.TreeHelper;
import com.ferreusveritas.dynamictrees.blocks.BlockBranch;
import com.ferreusveritas.dynamictrees.systems.dropcreators.DropCreator;
import com.ferreusveritas.dynamictrees.trees.Species;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import rustic.core.Rustic;

public class DropCreatorFruit extends DropCreator {
	
	private final Item fruit;
	private final int chance;
	
	public DropCreatorFruit(Item item, int chance) {
		super(new ResourceLocation(Rustic.MODID, item.getRegistryName().getResourcePath()));
		this.fruit = item;
		this.chance = chance;
	}
	
	@Override
	public List<ItemStack> getLeavesDrop(IBlockAccess access, Species species, BlockPos breakPos, Random random, List<ItemStack> dropList, int fortune) {
		return getLeafDrops(access, species, breakPos, random, dropList, fortune);
	}
	
	@Override
	public List<ItemStack> getHarvestDrop(World world, Species species, BlockPos leafPos, Random random, List<ItemStack> dropList, int soilLife, int fortune) {
		return getLeafDrops(world, species, leafPos, random, dropList, fortune);
	}
	
	@Override
	public List<ItemStack> getVoluntaryDrop(World world, Species species, BlockPos rootPos, Random random, List<ItemStack> dropList, int soilLife) {
		BlockPos treePos = rootPos.up();
		IBlockState trunk = world.getBlockState(treePos);
		BlockBranch branch = TreeHelper.getBranch(trunk);
		
		if (branch != null && branch.getRadius(trunk, world, treePos) >= 8) {
			if (0.33f > random.nextFloat()) {
				dropList.add(new ItemStack(fruit, 1, 0));
			}
		}
		return dropList;
	}
	
	protected List<ItemStack> getLeafDrops(IBlockAccess access, Species species, BlockPos leafPos, Random random, List<ItemStack> dropList, int fortune) {
		int chance = this.chance;
		if (fortune > 0) {
			chance -= fortune;
			if (chance < 2)
				chance = 2;
		}
		
		if(random.nextInt(chance) == 0) {
			dropList.add(new ItemStack(fruit, 1, 0));
		}
		return dropList;
	}

}
