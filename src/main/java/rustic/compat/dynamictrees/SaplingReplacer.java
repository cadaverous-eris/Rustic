package rustic.compat.dynamictrees;

import com.ferreusveritas.dynamictrees.api.TreeRegistry;
import com.ferreusveritas.dynamictrees.trees.Species;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import rustic.common.blocks.BlockPlanksRustic;
import rustic.common.blocks.BlockSaplingRustic;
import rustic.common.blocks.ModBlocks;
import rustic.core.Rustic;

public class SaplingReplacer {

	@SubscribeEvent
	public void onPlaceSaplingEvent(PlaceEvent event) {
		IBlockState state = event.getPlacedBlock();
		
		Species species = null;
		
		if (state.getBlock() == ModBlocks.SAPLING) {
			if (state.getValue(BlockSaplingRustic.VARIANT) == BlockPlanksRustic.EnumType.OLIVE) {
				species = TreeRegistry.findSpecies(new ResourceLocation(Rustic.MODID, "olive"));
			} else if (state.getValue(BlockSaplingRustic.VARIANT) == BlockPlanksRustic.EnumType.IRONWOOD) {
				species = TreeRegistry.findSpecies(new ResourceLocation(Rustic.MODID, "ironwood"));
			}
		}
		if (state.getBlock() == ModBlocks.APPLE_SEEDS || state.getBlock() == ModBlocks.APPLE_SAPLING) {
			species = TreeRegistry.findSpecies(new ResourceLocation("dynamictrees", "apple"));
		}
		
		if (species != null) {
			event.getWorld().setBlockToAir(event.getPos());
			if(!species.plantSapling(event.getWorld(), event.getPos())) {
				double x = event.getPos().getX() + 0.5;
				double y = event.getPos().getY() + 0.5;
				double z = event.getPos().getZ() + 0.5;
				EntityItem itemEntity = new EntityItem(event.getWorld(), x, y, z, species.getSeedStack(1));
				event.getWorld().spawnEntity(itemEntity);
			}
		}
	}
	
}
