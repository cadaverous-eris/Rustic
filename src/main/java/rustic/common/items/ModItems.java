package rustic.common.items;

import java.awt.Color;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.core.Rustic;

public class ModItems {

	public static ItemBase BEE;
	public static ItemBase HONEYCOMB;
	public static ItemBase BEESWAX;
	public static ItemBase TALLOW;
	public static ItemFoodBase OLIVES;
	public static ItemFoodBase IRONBERRIES;
	public static ItemFluidBottle FLUID_BOTTLE;

	public static ItemBase TEST;

	public static void init() {
		BEE = new ItemBase("bee");
		HONEYCOMB = new ItemBase("honeycomb");
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

		TEST = new ItemBase("test") {
			@Override
			public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
				playerIn.getFoodStats().setFoodLevel(2);
				playerIn.getFoodStats().setFoodSaturationLevel(0.5F);
				return super.onItemRightClick(worldIn, playerIn, handIn);
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

		TEST.initModel();
	};
}
