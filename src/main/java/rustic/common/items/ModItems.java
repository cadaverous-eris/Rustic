package rustic.common.items;

import java.awt.Color;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.blocks.ModBlocks;
import rustic.core.Rustic;

public class ModItems {

	public static ItemBase BEE;
	public static ItemBase HONEYCOMB;
	public static ItemBase BEESWAX;
	public static ItemBase TALLOW;
	public static ItemFoodBase OLIVES;
	public static ItemFoodBase IRONBERRIES;
	public static ItemFluidBottle FLUID_BOTTLE;
	public static ItemBase IRON_DUST;
	public static ItemBase IRON_DUST_TINY;
	public static ItemElixer ELIXER;
	public static ItemFoodBase TOMATO;
	public static ItemStakeCropSeed TOMATO_SEEDS;
	public static ItemFoodBase CHILI_PEPPER;
	public static ItemStakeCropSeed CHILI_PEPPER_SEEDS;
	public static ItemFoodBase WILDBERRIES;

	public static void init() {
		BEE = new ItemBase("bee");
		BEE.setCreativeTab(Rustic.farmingTab);
		HONEYCOMB = new ItemBase("honeycomb");
		HONEYCOMB.setCreativeTab(Rustic.farmingTab);
		BEESWAX = new ItemBase("beeswax");
		TALLOW = new ItemBase("tallow");
		OLIVES = new ItemFoodBase("olives", 1, 0.1F, false) {
			@Override
			public void initFood() {
				setPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 1, false, false), 0.95F);
			}

			@Override
			public int getMaxItemUseDuration(ItemStack stack) {
				return 24;
			}
		};
		IRONBERRIES = new ItemFoodBase("ironberries", 2, 0.1F, false) {
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
					player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 200, 15, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 200, 15, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 200, 15, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 15, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 200, 15, false, false));
					player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 200, 250, false, false));
				}
			}
		};
		FLUID_BOTTLE = new ItemFluidBottle();
		IRON_DUST = new ItemBase("dust_iron");
		IRON_DUST.setCreativeTab(Rustic.farmingTab);
		IRON_DUST_TINY = new ItemBase("dust_tiny_iron");
		IRON_DUST_TINY.setCreativeTab(Rustic.farmingTab);
		ELIXER = new ItemElixer();
		TOMATO = new ItemFoodBase("tomato", 4, 0.6F, false);
		TOMATO_SEEDS = new ItemStakeCropSeed("tomato_seeds", ModBlocks.TOMATO_CROP);
		CHILI_PEPPER = new ItemFoodBase("chili_pepper", 4, 0.8F, false) {
			@Override
			protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
				player.attackEntityFrom(DamageSource.ON_FIRE, 1.0F);
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 200));
			}
		};
		CHILI_PEPPER_SEEDS = new ItemStakeCropSeed("chili_pepper_seeds", ModBlocks.CHILI_CROP);
		WILDBERRIES = new ItemFoodBase("wildberries", 2, 0.3F, false) {
			@Override
			public int getMaxItemUseDuration(ItemStack stack) {
				return 16;
			}
		};
	}

	public static void initModels() {
		BEE.initModel();
		HONEYCOMB.initModel();
		BEESWAX.initModel();
		TALLOW.initModel();
		OLIVES.initModel();
		IRONBERRIES.initModel();
		FLUID_BOTTLE.initModel();
		IRON_DUST.initModel();
		IRON_DUST_TINY.initModel();
		ELIXER.initModel();
		TOMATO.initModel();
		TOMATO_SEEDS.initModel();
		CHILI_PEPPER.initModel();
		CHILI_PEPPER_SEEDS.initModel();
		WILDBERRIES.initModel();
	};
}
