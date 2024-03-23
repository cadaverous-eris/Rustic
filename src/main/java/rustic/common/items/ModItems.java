package rustic.common.items;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import rustic.common.Config;
import rustic.common.blocks.ModBlocks;
import rustic.common.entities.EntityTomato;
import rustic.common.potions.PotionsRustic;
import rustic.core.Rustic;

public class ModItems {
	
	public static ItemBook BOOK;

	public static ItemBase BEE;
	public static ItemBase HONEYCOMB;
	public static ItemBase BEESWAX;
	public static ItemBase TALLOW;
	public static ItemFoodBase OLIVES;
	public static ItemFoodBase IRONBERRIES;
	public static ItemFluidBottle FLUID_BOTTLE;
	public static ItemBase IRON_DUST_TINY;
	public static ItemElixir ELIXIR;
	public static ItemFoodBase TOMATO;
	public static ItemStakeCropSeed TOMATO_SEEDS;
	public static ItemFoodBase CHILI_PEPPER;
	public static ItemStakeCropSeed CHILI_PEPPER_SEEDS;
	public static ItemFoodBase WILDBERRIES;
	public static ItemFoodBase GRAPES;

	public static void init() {
		BOOK = new ItemBook();
		
		BEE = new ItemBase("bee");
		BEE.setCreativeTab(Rustic.farmingTab);
		HONEYCOMB = new ItemBase("honeycomb");
		HONEYCOMB.setCreativeTab(Rustic.farmingTab);
		BEESWAX = new ItemBase("beeswax");
		TALLOW = new ItemBase("tallow");
		OLIVES = new ItemFoodBase("olives", 1, 0.4F, false) {
			@Override
			public void initFood() {
				setPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 1, false, false), 0.95F);
			}

			@Override
			public int getMaxItemUseDuration(ItemStack stack) {
				return 24;
			}
		};
		IRONBERRIES = new ItemFoodBase("ironberries", 2, 0.4F, false) {
			@Override
			public void initFood() {
				setAlwaysEdible();
			}

			@Override
			public int getMaxItemUseDuration(ItemStack stack) {
				return 16;
			}

			@Override
			protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
				if (!worldIn.isRemote) {
					//player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 2, false, false));
					//player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 200, 2, false, false));
					//player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 15, false, false));
					//player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 15, false, false));
					//player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 15, false, false));
					//player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 200, 250, false, false));
					int duration = 15 * 20;
					PotionEffect effect = player.getActivePotionEffect(PotionsRustic.FULLMETAL_POTION);
					if (effect == null) {
						player.addPotionEffect(new PotionEffect(PotionsRustic.FULLMETAL_POTION, duration, 0, false, true));
					} else {
						player.addPotionEffect(new PotionEffect(PotionsRustic.FULLMETAL_POTION, effect.getDuration() + duration, 0, false, true));
					}
				}
			}
		};
		FLUID_BOTTLE = new ItemFluidBottle();
		IRON_DUST_TINY = new ItemBase("dust_tiny_iron");
		IRON_DUST_TINY.setCreativeTab(Rustic.farmingTab);
		ELIXIR = new ItemElixir();
		TOMATO = new ItemFoodBase("tomato", 4, 0.4F, false) {
			@Override
			public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
				ItemStack itemstack = playerIn.getHeldItem(handIn);
				if (playerIn.isSneaking()) {
					if (!playerIn.capabilities.isCreativeMode) {
						itemstack.shrink(1);
					}
					worldIn.playSound((EntityPlayer) null, playerIn.posX, playerIn.posY, playerIn.posZ,
							SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F,
							0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
					if (!worldIn.isRemote) {
						EntityTomato entitytomato = new EntityTomato(worldIn, playerIn);
						entitytomato.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw,
								0.0F, 1.5F, 1.0F);
						worldIn.spawnEntity(entitytomato);
					}
					playerIn.addStat(StatList.getObjectUseStats(this));
					return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
				}
				return super.onItemRightClick(worldIn, playerIn, handIn);
			}
		};
		TOMATO_SEEDS = new ItemStakeCropSeed("tomato_seeds", ModBlocks.TOMATO_CROP);
		if (Config.ENABLE_SEED_DROPS) {
			MinecraftForge.addGrassSeed(new ItemStack(TOMATO_SEEDS), Config.SEED_DROP_RATE);
		}
		CHILI_PEPPER = new ItemFoodBase("chili_pepper", 3, 0.4F, false) {
			private Random rand = new Random();
			@Override
			protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
				if (!worldIn.isRemote && (rand.nextInt(24) == 0)) {
					player.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
					player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 400));
				}
			}
		};
		CHILI_PEPPER_SEEDS = new ItemStakeCropSeed("chili_pepper_seeds", ModBlocks.CHILI_CROP);
		if (Config.ENABLE_SEED_DROPS) {
			MinecraftForge.addGrassSeed(new ItemStack(CHILI_PEPPER_SEEDS), Config.SEED_DROP_RATE);
		}
		WILDBERRIES = new ItemFoodBase("wildberries", 2, 0.5F, false) {
			@Override
			public int getMaxItemUseDuration(ItemStack stack) {
				return 16;
			}
		};
		GRAPES = new ItemFoodBase("grapes", 3, 0.3F, false) {
			@Override
			public int getMaxItemUseDuration(ItemStack stack) {
				return 16;
			}
		};
	}

	public static void initModels() {
		BOOK.initModel();
		
		BEE.initModel();
		HONEYCOMB.initModel();
		BEESWAX.initModel();
		TALLOW.initModel();
		OLIVES.initModel();
		IRONBERRIES.initModel();
		FLUID_BOTTLE.initModel();
		IRON_DUST_TINY.initModel();
		ELIXIR.initModel();
		TOMATO.initModel();
		TOMATO_SEEDS.initModel();
		CHILI_PEPPER.initModel();
		CHILI_PEPPER_SEEDS.initModel();
		WILDBERRIES.initModel();
		GRAPES.initModel();
	};
}
