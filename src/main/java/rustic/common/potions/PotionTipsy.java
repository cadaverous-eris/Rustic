package rustic.common.potions;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

public class PotionTipsy extends PotionBase {
	
	public PotionTipsy() {
		super(true, 7900290, "tipsy");
		setIconIndex(4, 0);
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amp) {
		if (entity != null && !entity.world.isRemote && amp > 1) {
			if (amp > 2) {
				entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 400, 1, false, false));
				entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 400, 1, false, false));
				entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 400, 0, false, false));
			} else {
				entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 400, 0, false, false));
				entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 400, 0, false, false));
			}
			// remove curative items after, in case the effects were already active before
			List<ItemStack> cureItems = new ArrayList<>();
			PotionEffect nausea = entity.getActivePotionEffect(MobEffects.NAUSEA);
			PotionEffect slowness = entity.getActivePotionEffect(MobEffects.SLOWNESS);
			PotionEffect blindness = entity.getActivePotionEffect(MobEffects.BLINDNESS);
			PotionEffect tipsy = entity.getActivePotionEffect(this);
			if (nausea != null) nausea.setCurativeItems(cureItems);
			if (slowness != null) slowness.setCurativeItems(cureItems);
			if (blindness != null) blindness.setCurativeItems(cureItems);
			if (tipsy != null) tipsy.setCurativeItems(cureItems);
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		return (amplifier > 1) && ((duration % 100) == 0);
	}
	
	@Override
	public java.util.List<net.minecraft.item.ItemStack> getCurativeItems() {
        return new ArrayList<>();
    }

}
