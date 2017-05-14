package rustic.common.blocks.fluids;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class ModFluids {

	public static Fluid OLIVE_OIL;
	public static Fluid IRONBERRY_JUICE;
	public static Fluid WILDBERRY_JUICE;
	public static Fluid GRAPE_JUICE;
	public static Fluid APPLE_JUICE;
	public static Fluid ALE_WORT;
	public static Fluid HONEY;

	public static BlockFluidRustic BLOCK_OLIVE_OIL;
	public static BlockFluidRustic BLOCK_IRONBERRY_JUICE;
	public static BlockFluidRustic BLOCK_WILDBERRY_JUICE;
	public static BlockFluidRustic BLOCK_GRAPE_JUICE;
	public static BlockFluidRustic BLOCK_APPLE_JUICE;
	public static BlockFluidRustic BLOCK_ALE_WORT;
	public static BlockFluidRustic BLOCK_HONEY;

	public static void init() {
		OLIVE_OIL = new FluidDrinkable("oliveoil", new ResourceLocation("rustic:blocks/fluids/olive_oil_still"),
				new ResourceLocation("rustic:blocks/fluids/olive_oil_flow")) {
			@Override
			public void onDrank(World world, EntityPlayer player, ItemStack stack, FluidStack fluid) {
				player.getFoodStats().addStats(1, 0.4F);
				player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 600, 1));
			}
		}.setDensity(920).setViscosity(2000);
		register(OLIVE_OIL);

		IRONBERRY_JUICE = new FluidDrinkable("ironberryjuice",
				new ResourceLocation("rustic:blocks/fluids/ironberry_juice_still"),
				new ResourceLocation("rustic:blocks/fluids/ironberry_juice_flow")) {
			@Override
			public void onDrank(World world, EntityPlayer player, ItemStack stack, FluidStack fluid) {
				player.getFoodStats().addStats(1, 0.8F);
				player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 600, 15, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 600, 15, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 600, 15, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 600, 15, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 600, 15, false, false));
				player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 600, 250, false, false));
			}
		}.setDensity(1100).setViscosity(1100);
		register(IRONBERRY_JUICE);

		WILDBERRY_JUICE = new FluidDrinkable("wildberryjuice",
				new ResourceLocation("rustic:blocks/fluids/wildberry_juice_still"),
				new ResourceLocation("rustic:blocks/fluids/wildberry_juice_flow")) {
			@Override
			public void onDrank(World world, EntityPlayer player, ItemStack stack, FluidStack fluid) {
				player.getFoodStats().addStats(1, 1F);
				if (player.getRNG().nextFloat() < 0.2F) {
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 75));
				}
				if (player.getRNG().nextFloat() < 0.1F) {
					player.addPotionEffect(new PotionEffect(MobEffects.POISON, 200));
				}
			}
		}.setDensity(1070).setViscosity(1100);
		register(WILDBERRY_JUICE);

		GRAPE_JUICE = new FluidDrinkable("grapejuice", new ResourceLocation("rustic:blocks/fluids/grape_juice_still"),
				new ResourceLocation("rustic:blocks/fluids/grape_juice_flow")) {
			@Override
			public void onDrank(World world, EntityPlayer player, ItemStack stack, FluidStack fluid) {
				player.getFoodStats().addStats(1, 0.9F);
			}
		}.setDensity(1070).setViscosity(1100);
		register(GRAPE_JUICE);

		APPLE_JUICE = new FluidDrinkable("applejuice", new ResourceLocation("rustic:blocks/fluids/apple_juice_still"),
				new ResourceLocation("rustic:blocks/fluids/apple_juice_flow")) {
			@Override
			public void onDrank(World world, EntityPlayer player, ItemStack stack, FluidStack fluid) {
				player.getFoodStats().addStats(1, 1.2F);
			}
		}.setDensity(1050).setViscosity(1100);
		register(APPLE_JUICE);

		ALE_WORT = new FluidDrinkable("alewort", new ResourceLocation("rustic:blocks/fluids/ale_wort_still"),
				new ResourceLocation("rustic:blocks/fluids/ale_wort_flow")) {
			@Override
			public void onDrank(World world, EntityPlayer player, ItemStack stack, FluidStack fluid) {
				player.getFoodStats().addStats(1, 2F);
				player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 400, 1));
			}
		}.setDensity(1004).setViscosity(2000);
		register(ALE_WORT);

		HONEY = new FluidDrinkable("honey", new ResourceLocation("rustic:blocks/fluids/honey_still"),
				new ResourceLocation("rustic:blocks/fluids/honey_flow")) {
			@Override
			public void onDrank(World world, EntityPlayer player, ItemStack stack, FluidStack fluid) {
				player.getFoodStats().addStats(3, 0.4F);
				if (player.getRNG().nextFloat() < 0.6F) {
					player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 75));
				}
			}
		}.setDensity(1433).setViscosity(5500);
		register(HONEY);

		BLOCK_OLIVE_OIL = new BlockFluidRustic("olive_oil", OLIVE_OIL, Material.WATER);
		BLOCK_OLIVE_OIL.setQuantaPerBlock(4);

		BLOCK_IRONBERRY_JUICE = new BlockFluidRustic("ironberry_juice", IRONBERRY_JUICE, Material.WATER);
		BLOCK_IRONBERRY_JUICE.setQuantaPerBlock(6);

		BLOCK_WILDBERRY_JUICE = new BlockFluidRustic("wildberry_juice", WILDBERRY_JUICE, Material.WATER);
		BLOCK_WILDBERRY_JUICE.setQuantaPerBlock(6);

		BLOCK_GRAPE_JUICE = new BlockFluidRustic("grape_juice", GRAPE_JUICE, Material.WATER);
		BLOCK_GRAPE_JUICE.setQuantaPerBlock(6);

		BLOCK_APPLE_JUICE = new BlockFluidRustic("apple_juice", APPLE_JUICE, Material.WATER);
		BLOCK_APPLE_JUICE.setQuantaPerBlock(6);

		BLOCK_ALE_WORT = new BlockFluidRustic("ale_wort", ALE_WORT, Material.WATER);
		BLOCK_ALE_WORT.setQuantaPerBlock(4);

		BLOCK_HONEY = new BlockFluidRustic("honey", HONEY, Material.WATER) {
			@Override
			public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
				entity.motionX *= 0.5F;
				entity.motionY *= 0.5F;
				entity.motionZ *= 0.5F;
			}
		};
		BLOCK_HONEY.setQuantaPerBlock(2);
	}

	public static void initModels() {
		BLOCK_OLIVE_OIL.initModel();
		BLOCK_IRONBERRY_JUICE.initModel();
		BLOCK_WILDBERRY_JUICE.initModel();
		BLOCK_GRAPE_JUICE.initModel();
		BLOCK_APPLE_JUICE.initModel();
		BLOCK_ALE_WORT.initModel();
		BLOCK_HONEY.initModel();
	}

	private static void register(Fluid fluid) {
		if (!FluidRegistry.registerFluid(fluid)) {
			fluid = FluidRegistry.getFluid(fluid.getName());
		}
		FluidRegistry.addBucketForFluid(fluid);
	}

}
