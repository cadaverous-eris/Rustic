package rustic.common.blocks.fluids;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ModFluids {
	
	public static Fluid OLIVE_OIL;
	
	public static BlockFluidRustic BLOCK_OLIVE_OIL;
	
	public static void init() {
		OLIVE_OIL = new FluidDrinkable("oliveoil", new ResourceLocation("rustic:blocks/fluids/olive_oil_still"), new ResourceLocation("rustic:blocks/fluids/olive_oil_flow")) {
			@Override
			public void onDrank(World world, EntityPlayer player, ItemStack stack) {
				player.getFoodStats().addStats(0, 0.5F);
				player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 5, 1));
			}
		}.setDensity(920).setViscosity(6349);
		register(OLIVE_OIL);
		
		BLOCK_OLIVE_OIL = new BlockFluidRustic("olive_oil", OLIVE_OIL, Material.WATER);
	}
	
	public static void initModels() {
		BLOCK_OLIVE_OIL.initModel();
	}
	
	private static void register(Fluid fluid) {
		if (!FluidRegistry.registerFluid(fluid)) {
			fluid = FluidRegistry.getFluid(fluid.getName());
		}
		FluidRegistry.addBucketForFluid(fluid);
	}

}
