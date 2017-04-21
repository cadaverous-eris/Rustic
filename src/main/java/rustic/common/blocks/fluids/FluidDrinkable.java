package rustic.common.blocks.fluids;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public abstract class FluidDrinkable extends Fluid {

	public FluidDrinkable(String fluidName, ResourceLocation still, ResourceLocation flowing) {
		super(fluidName, still, flowing);
	}
	
	public abstract void onDrank(@Nonnull World world, @Nonnull EntityPlayer player, @Nonnull ItemStack stack);

}
