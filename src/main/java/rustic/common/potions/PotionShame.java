package rustic.common.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.items.ModItems;
import rustic.common.network.MessageShameFX;
import rustic.common.network.PacketHandler;
import rustic.core.Rustic;

public class PotionShame extends PotionBase {

	protected PotionShame() {
		super(true, 16409650, "shame");
		setIconIndex(3, 0);
	}

	@Override
	public void performEffect(EntityLivingBase entity, int amp) {
		if (entity != null && !entity.world.isRemote) {
			PacketHandler.INSTANCE.sendToAll(new MessageShameFX(entity.world.rand.nextInt(6) + 6, entity.posX, entity.posY,
					entity.posZ, entity.motionX, entity.motionY, entity.motionZ, entity.width, entity.height));
		}
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return (duration % 5) == 0;
	}

}
