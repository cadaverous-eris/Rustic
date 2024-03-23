package rustic.common.potions;

import java.util.Collection;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.world.WorldServer;

public class PotionFullmetal extends PotionBase {
	
	public static final String MOVEMENT_SPEED_MODIFIER_UUID = "0CA3AAC1-2CD1-4552-AC7E-DD4984B8D7A3";
	public static final String SWIM_SPEED_MODIFIER_UUID = "FD2B7891-0F1F-4212-93B5-C084E1D0BFD9";
	public static final String FLYING_SPEED_MODIFIER_UUID = "1D42C4B3-57CA-4DB5-99E9-F873E2C84A47";
	public static final String ATTACK_SPEED_MODIFIER_UUID = "1809F109-92FE-410A-B78C-3BB9D0C4CC9E";
	public static final String KNOCKBACK_RESISTANCE_MODIFIER_UUID = "639E0E9E-0B0D-474C-86FA-626901789BAC";
	
	protected PotionFullmetal() {
		super(false, 8220521, "fullmetal");
		setIconIndex(0, 1);
		registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, MOVEMENT_SPEED_MODIFIER_UUID, -1F, 2);
		registerPotionAttributeModifier(EntityLivingBase.SWIM_SPEED, SWIM_SPEED_MODIFIER_UUID, -1F, 2);
		//registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE, ATTACK_DAMAGE_MODIFIER_UUID, -1F, 2);
		registerPotionAttributeModifier(SharedMonsterAttributes.FLYING_SPEED, FLYING_SPEED_MODIFIER_UUID, -1F, 2);
		registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, ATTACK_SPEED_MODIFIER_UUID, -0.5F, 2);
		registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, KNOCKBACK_RESISTANCE_MODIFIER_UUID, 9001F, 0);
		this.setEffectiveness(1.0D);
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int p_76394_2_) {
		entity.setJumping(false);
		entity.setSprinting(false);
		//entity.moveStrafing = 0.0F;
		//entity.moveForward = 0.0F;
		//entity.randomYawVelocity = 0.0F;
		if ((entity.getRidingEntity() != null) && (entity.getRidingEntity() instanceof EntityLivingBase)) {
			entity.dismountRidingEntity();
		}
		if (!entity.onGround && !entity.hasNoGravity()) {
			if ((entity.isInWater() || entity.isInLava())) {
				entity.motionY -= 0.27 / 4;
				entity.velocityChanged = true;
			} else if (entity.isElytraFlying()) {
				entity.motionY -= 0.32 / 4;
				entity.velocityChanged = true;
			} else {
				entity.motionY -= 0.07;
				entity.velocityChanged = true;
			}
		}
		if (entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			player.capabilities.isFlying = false;
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;//(duration % 5) == 0;
    }
	
	@Override
	public List<net.minecraft.item.ItemStack> getCurativeItems() {
		return super.getCurativeItems();
	}
	
	@Override
	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		if (/*!(entityLivingBaseIn instanceof EntityPlayer) && */entityLivingBaseIn.isServerWorld()) {
			((WorldServer) entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketEntityEffect(entityLivingBaseIn.getEntityId(), entityLivingBaseIn.getActivePotionEffect(this)));
			((WorldServer) entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketEntityProperties(entityLivingBaseIn.getEntityId(), (Collection<IAttributeInstance>) entityLivingBaseIn.getAttributeMap().getAllAttributes()));
		}
	}
	
	@Override
	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		if (/*!(entityLivingBaseIn instanceof EntityPlayer) && */entityLivingBaseIn.isServerWorld()) {
			((WorldServer) entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketRemoveEntityEffect(entityLivingBaseIn.getEntityId(), this));
			((WorldServer) entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketEntityProperties(entityLivingBaseIn.getEntityId(), (Collection<IAttributeInstance>) entityLivingBaseIn.getAttributeMap().getAllAttributes()));
		}
	}
	
	@Override
	public double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {
        return modifier.getAmount();
    }

}
