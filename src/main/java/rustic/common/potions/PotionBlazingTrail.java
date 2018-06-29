package rustic.common.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;

public class PotionBlazingTrail extends PotionBase {
	
	protected PotionBlazingTrail() {
		super(false, 16738816, "blazing_trail");
		setIconIndex(2, 0);
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int p_76394_2_) {
		if (!entity.getEntityWorld().isRemote && entity.onGround && entity.getEntityWorld().isAirBlock(entity.getPosition()) && entity.getEntityWorld().getBlockState(entity.getPosition().down()).isNormalCube()) {
			entity.getEntityWorld().setBlockState(entity.getPosition(), Blocks.FIRE.getDefaultState(), 3);
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		return (duration % 10) == 0;
    }

}
