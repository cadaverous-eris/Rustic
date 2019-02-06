package rustic.common.potions;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandlerPotions {

	private Random rand = new Random();

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onWaterBottleUse(LivingEntityUseItemEvent.Finish event) {
		EntityLivingBase entity = event.getEntityLiving();
		PotionEffect effect = entity.getActivePotionEffect(PotionsRustic.TIPSY);
		if (effect != null && !entity.world.isRemote) {
			ItemStack stack = event.getItem();
			if (stack.getItem() == Items.POTIONITEM && PotionUtils.getPotionFromItem(stack) == PotionTypes.WATER) {
				int duration = effect.getDuration();
				int amplifier = effect.getAmplifier();
				if (rand.nextFloat() < 0.1F) {
					amplifier--;
				} else {
					duration -= (rand.nextInt(800) + 200);
				}
				entity.removePotionEffect(PotionsRustic.TIPSY);
				if (amplifier >= 0 && duration > 0) {
					entity.addPotionEffect(new PotionEffect(PotionsRustic.TIPSY, duration, amplifier, false, false));
				}
			}
		}
	}

	// FULL, MAGIC RESISTANCE, WITHER WARD

	@SubscribeEvent
	public void onEntityDamage(LivingHurtEvent event) {
		DamageSource source = event.getSource();
		EntityLivingBase entity = event.getEntityLiving();
		if (source == DamageSource.STARVE) {
			PotionEffect effect = entity.getActivePotionEffect(PotionsRustic.FULL_POTION);
			if (effect != null) {
				float newAmount = Math.max(event.getAmount() - (effect.getAmplifier() + 1), 0);
				event.setAmount(newAmount);
			}
		} else if (source == DamageSource.MAGIC) {
			PotionEffect effect = entity.getActivePotionEffect(PotionsRustic.MAGIC_RESISTANCE_POTION);
			if (effect != null) {
				float newAmount = event.getAmount() / (2F * (effect.getAmplifier() + 1F));
				event.setAmount(newAmount);
			}
		} else if (source == DamageSource.WITHER) {
			PotionEffect effect = entity.getActivePotionEffect(PotionsRustic.WITHER_WARD_POTION);
			if (effect != null) {
				float newAmount = event.getAmount() / (2F * (effect.getAmplifier() + 1F));
				event.setAmount(newAmount);
			}
		}
	}

}
