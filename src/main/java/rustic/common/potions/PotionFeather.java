package rustic.common.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionFeather extends Potion {
	
	public static ResourceLocation POTION_ICONS = new ResourceLocation("rustic:textures/gui/potion_icons.png");

	protected PotionFeather() {
		super(false, 14474460);
		setPotionName("effect.feather");
		setBeneficial();
		setIconIndex(1, 0);
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int p_76394_2_) {
		if (!entity.onGround && entity.motionY < -0.4) {
			entity.motionY += 0.1;
			entity.velocityChanged = true;
			entity.fallDistance = 0;
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
    }

	@SideOnly(Side.CLIENT)
	@Override
	public int getStatusIconIndex() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(POTION_ICONS);
		return super.getStatusIconIndex();
	}

}
