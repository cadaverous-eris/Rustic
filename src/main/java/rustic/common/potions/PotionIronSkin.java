package rustic.common.potions;

import java.util.Collection;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionIronSkin extends Potion {
	
	public static ResourceLocation POTION_ICONS = new ResourceLocation("rustic:textures/gui/potion_icons.png");

	protected PotionIronSkin() {
		super(false, 16777148);
		setPotionName("effect.ironskin");
		setBeneficial();
		setIconIndex(0, 0);
		registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR, "D666C8fD-8AC4-451D-9A06-777947832156", 3F, 0);
		registerPotionAttributeModifier(SharedMonsterAttributes.ARMOR_TOUGHNESS, "D774E354-E3AB-42C4-9716-d2280CD7D988", 2F, 0);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getStatusIconIndex()
    {
		Minecraft.getMinecraft().getTextureManager().bindTexture(POTION_ICONS);
        return super.getStatusIconIndex();
    }

	public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
		super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		if (/*!(entityLivingBaseIn instanceof EntityPlayer) && */entityLivingBaseIn.isServerWorld()) {
			((WorldServer) entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketRemoveEntityEffect(entityLivingBaseIn.getEntityId(), this));
			((WorldServer) entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketEntityProperties(entityLivingBaseIn.getEntityId(), (Collection<IAttributeInstance>) entityLivingBaseIn.getAttributeMap().getAllAttributes()));
		}
	}

	public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
		super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
		if (/*!(entityLivingBaseIn instanceof EntityPlayer) && */entityLivingBaseIn.isServerWorld()) {
			((WorldServer) entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketEntityEffect(entityLivingBaseIn.getEntityId(), entityLivingBaseIn.getActivePotionEffect(this)));
			((WorldServer) entityLivingBaseIn.world).getEntityTracker().sendToTracking(entityLivingBaseIn, new SPacketEntityProperties(entityLivingBaseIn.getEntityId(), (Collection<IAttributeInstance>) entityLivingBaseIn.getAttributeMap().getAllAttributes()));
		}
	}

}
