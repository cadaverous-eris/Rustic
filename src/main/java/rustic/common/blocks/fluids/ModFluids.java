package rustic.common.blocks.fluids;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import rustic.common.potions.PotionsRustic;

public class ModFluids {

	public static Fluid OLIVE_OIL;
	public static Fluid IRONBERRY_JUICE;
	public static Fluid WILDBERRY_JUICE;
	public static Fluid GRAPE_JUICE;
	public static Fluid APPLE_JUICE;
	public static Fluid ALE_WORT;
	public static Fluid HONEY;
	
	public static Fluid ALE;
	public static Fluid CIDER;
	public static Fluid IRON_WINE;
	public static Fluid MEAD;
	public static Fluid WILDBERRY_WINE;
	public static Fluid WINE;
	
	private static List<Fluid> FLUIDS = new ArrayList<Fluid>();

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
		
		
		ALE = new FluidBooze("ale", new ResourceLocation("rustic:blocks/fluids/booze/ale_still"), new ResourceLocation("rustic:blocks/fluids/booze/ale_flow")) {
			@Override
			protected void affectPlayer(World world, EntityPlayer player, float quality) {
				if (quality >= 0.5F) {
					float saturation = 4F * quality;
					player.getFoodStats().addStats(2, saturation);
					int duration = (int) (12000 * (Math.max(Math.abs((quality - 0.5F) * 2F), 0F)));
					player.addPotionEffect(new PotionEffect(PotionsRustic.FULL_POTION, duration));
				} else {
					int duration = (int) (6000 * Math.max(1 - quality, 0));
					player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, duration));
					player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration));
				}
			}
		}.setInebriationChance(0.5F).setDensity(1004).setViscosity(1016);
		register(ALE, false);
		
		CIDER = new FluidBooze("cider", new ResourceLocation("rustic:blocks/fluids/booze/cider_still"), new ResourceLocation("rustic:blocks/fluids/booze/cider_flow")) {
			@Override
			protected void affectPlayer(World world, EntityPlayer player, float quality) {
				if (quality >= 0.5F) {
					float saturation = 2F * quality;
					player.getFoodStats().addStats(1, saturation);
					int duration = (int) (12000 * (Math.max(Math.abs((quality - 0.5F) * 2F), 0F)));
					player.addPotionEffect(new PotionEffect(PotionsRustic.MAGIC_RESISTANCE_POTION, duration));
				} else {
					int duration = (int) (1200 * Math.max(1 - quality, 0));
					player.addPotionEffect(new PotionEffect(MobEffects.POISON, duration));
					duration = (int) (6000 * Math.max(1 - quality, 0));
					player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration));
				}
			}
		}.setInebriationChance(0.5F).setDensity(1004).setViscosity(1400);
		register(CIDER, false);
		
		IRON_WINE = new FluidBooze("ironwine", new ResourceLocation("rustic:blocks/fluids/booze/iron_wine_still"), new ResourceLocation("rustic:blocks/fluids/booze/iron_wine_flow")) {
			@Override
			protected void affectPlayer(World world, EntityPlayer player, float quality) {
				if (quality >= 0.5F) {
					float saturation = 2F * quality;
					float absorption = 10F * (Math.max((quality - 0.5F) * 2F, 0F));
					player.getFoodStats().addStats(1, saturation);
					player.setAbsorptionAmount(Math.max(Math.min(player.getAbsorptionAmount() + absorption, 20F), player.getAbsorptionAmount()));
				} else {
					int duration = (int) (6000 * Math.max(1 - quality, 0));
					float damage = 10F * (Math.max(Math.abs(quality - 0.5F) + 0.1F, 0F));
					player.attackEntityFrom(DamageSource.MAGIC, damage);
					player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration));
				}
			}
		}.setInebriationChance(0.5F).setDensity(1034).setViscosity(1400);
		register(IRON_WINE, false);
		
		MEAD = new FluidBooze("mead", new ResourceLocation("rustic:blocks/fluids/booze/mead_still"), new ResourceLocation("rustic:blocks/fluids/booze/mead_flow")) {
			@Override
			protected void affectPlayer(World world, EntityPlayer player, float quality) {
				if (quality >= 0.5F) {
					float saturation = 2F * quality;
					player.getFoodStats().addStats(1, saturation);
					int duration = (int) (6000 * (Math.max(Math.abs((quality - 0.5F) * 2F), 0F)));
					player.addPotionEffect(new PotionEffect(PotionsRustic.WITHER_WARD_POTION, duration));
				} else {
					int duration = (int) (800 * Math.max(1 - quality, 0));
					player.addPotionEffect(new PotionEffect(MobEffects.WITHER, duration));
					duration = (int) (6000 * Math.max(1 - quality, 0));
					player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration));
				}
			}
		}.setInebriationChance(0.5F).setDensity(1034).setViscosity(1500);
		register(MEAD, false);
		
		WILDBERRY_WINE = new FluidBooze("wildberrywine", new ResourceLocation("rustic:blocks/fluids/booze/wildberry_wine_still"), new ResourceLocation("rustic:blocks/fluids/booze/wildberry_wine_flow")) {
			@Override
			protected void affectPlayer(World world, EntityPlayer player, float quality) {
				if (quality >= 0.5F) {
					float saturation = 2F * quality;
					player.getFoodStats().addStats(1, saturation);
					for (PotionEffect effect : player.getActivePotionEffects()) {
						if (!effect.getPotion().isBadEffect() && effect.getAmplifier() < 2) {
							player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier() + 1, effect.getIsAmbient(), effect.doesShowParticles()));
						}
					}
				} else {
					PotionEffect[] effects = player.getActivePotionEffects().toArray(new PotionEffect[0]);
					for (int i = 0; i < effects.length; i++) {
						PotionEffect effect = effects[i];
						if (!effect.getPotion().isBadEffect()) {
							if (effect.getAmplifier() > 0) {
								player.removePotionEffect(effect.getPotion());
								player.addPotionEffect(new PotionEffect(effect.getPotion(), effect.getDuration(), effect.getAmplifier() - 1, effect.getIsAmbient(), effect.doesShowParticles()));
							} else if (effect.getAmplifier() == 0) {
								player.removePotionEffect(effect.getPotion());
							}
						}
					}
					int duration = (int) (6000 * Math.max(1 - quality, 0));
					player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration));
				}
			}
		}.setInebriationChance(0.5F).setDensity(1034).setViscosity(1500);
		register(WILDBERRY_WINE, false);
		
		WINE = new FluidBooze("wine", new ResourceLocation("rustic:blocks/fluids/booze/wine_still"), new ResourceLocation("rustic:blocks/fluids/booze/wine_flow")) {
			@Override
			protected void affectPlayer(World world, EntityPlayer player, float quality) {
				if (quality >= 0.5F) {
					float saturation = 2F * quality;
					player.getFoodStats().addStats(1, saturation);
					int durationIncrease = (int) (2400 * ((quality - 0.5F) * 2F));
					for (PotionEffect effect : player.getActivePotionEffects()) {
						if (!effect.getPotion().isBadEffect() && effect.getDuration() < 12000) {
							int duration = Math.max(Math.min(effect.getDuration() + durationIncrease, 12000), effect.getDuration());
							player.addPotionEffect(new PotionEffect(effect.getPotion(), duration, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
						}
					}
				} else {
					PotionEffect[] effects = player.getActivePotionEffects().toArray(new PotionEffect[0]);
					for (int i = 0; i < effects.length; i++) {
						PotionEffect effect = effects[i];
						int durationDecrease = (int) (2400 * (Math.abs(quality - 0.5)));
						if (!effect.getPotion().isBadEffect()) {
							int duration = effect.getDuration() - durationDecrease;
							if (duration > 0) {
								player.removePotionEffect(effect.getPotion());
								player.addPotionEffect(new PotionEffect(effect.getPotion(), duration, effect.getAmplifier(), effect.getIsAmbient(), effect.doesShowParticles()));
							} else {
								player.removePotionEffect(effect.getPotion());
							}
						}
					}
					int duration = (int) (6000 * Math.max(1 - quality, 0));
					player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, duration));
				}
			}
		}.setInebriationChance(0.5F).setDensity(1034).setViscosity(1500);
		register(WINE, false);
		

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
		FLUIDS.add(fluid);
	}
	
	private static void register(Fluid fluid, boolean addBucket) {
		if (!FluidRegistry.registerFluid(fluid)) {
			fluid = FluidRegistry.getFluid(fluid.getName());
		}
		if (addBucket) {
			FluidRegistry.addBucketForFluid(fluid);
		}
		FLUIDS.add(fluid);
	}
	
	public static ArrayList<Fluid> getFluids() {
		return new ArrayList<Fluid>(FLUIDS);
	}

}
