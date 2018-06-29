package rustic.common.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

public class PotionTipsy extends PotionBase {
	
	public PotionTipsy() {
		super(true, 7900290, "tipsy");
		setIconIndex(4, 0);
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amp) {
		if (entity != null && !entity.world.isRemote) {
			if (amp > 2) {
				entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 400, 1, false, false));
				entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 400, 1, false, false));
				entity.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 400, 0, false, false));
			} else if (amp > 1) {
				entity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 400, 0, false, false));
				entity.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 400, 0, false, false));
			}
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		return amplifier > 1 && duration % 100 == 0;
	}

}
