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
	public static Fluid IRONBERRY_JUICE;
	
	public static BlockFluidRustic BLOCK_OLIVE_OIL;
	public static BlockFluidRustic BLOCK_IRONBERRY_JUICE;
	
	public static void init() {
		OLIVE_OIL = new FluidDrinkable("oliveoil", new ResourceLocation("rustic:blocks/fluids/olive_oil_still"), new ResourceLocation("rustic:blocks/fluids/olive_oil_flow")) {
			@Override
			public void onDrank(World world, EntityPlayer player, ItemStack stack) {
				player.getFoodStats().addStats(0, 0.5F);
				player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 600, 1));
			}
		}.setDensity(920).setViscosity(6349);
		register(OLIVE_OIL);
		
		IRONBERRY_JUICE = new FluidDrinkable("ironberryjuice", new ResourceLocation("rustic:blocks/fluids/ironberry_juice_still"), new ResourceLocation("rustic:blocks/fluids/ironberry_juice_flow")) {
			@Override
			public void onDrank(World world, EntityPlayer player, ItemStack stack) {
				player.getFoodStats().addStats(0, 0.5F);
				player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 600, 15, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 600, 15, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 600, 15, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 600, 15, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 600, 15, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 600, 250, false, false));
			}
		}.setDensity(1300).setViscosity(2100);
		register(IRONBERRY_JUICE);
		
		BLOCK_OLIVE_OIL = new BlockFluidRustic("olive_oil", OLIVE_OIL, Material.WATER);
		BLOCK_OLIVE_OIL.setQuantaPerBlock(4);
		
		BLOCK_IRONBERRY_JUICE = new BlockFluidRustic("ironberry_juice", IRONBERRY_JUICE, Material.WATER);
		BLOCK_IRONBERRY_JUICE.setQuantaPerBlock(6);
	}
	
	public static void initModels() {
		BLOCK_OLIVE_OIL.initModel();
		BLOCK_IRONBERRY_JUICE.initModel();
	}
	
	private static void register(Fluid fluid) {
		if (!FluidRegistry.registerFluid(fluid)) {
			fluid = FluidRegistry.getFluid(fluid.getName());
		}
		FluidRegistry.addBucketForFluid(fluid);
	}

}
