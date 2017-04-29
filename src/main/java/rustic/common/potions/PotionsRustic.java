package rustic.common.potions;

import java.util.UUID;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionsRustic {

	public static final Potion IRON_SKIN_POTION = new PotionIronSkin();
	
	public static final PotionType IRON_SKIN = new PotionType("ironskin", new PotionEffect[] { new PotionEffect(IRON_SKIN_POTION, 3600, 0, false, true) });
	public static final PotionType LONG_IRON_SKIN = new PotionType("ironskin", new PotionEffect[] { new PotionEffect(IRON_SKIN_POTION, 9600, 0, false, true) });
	public static final PotionType STRONG_IRON_SKIN = new PotionType("ironskin", new PotionEffect[] { new PotionEffect(IRON_SKIN_POTION, 1800, 1, false, true) });
	
	public static void init() {
		GameRegistry.register(IRON_SKIN_POTION, new ResourceLocation("rustic:ironskin"));
		
		GameRegistry.register(IRON_SKIN, new ResourceLocation("rustic:ironskin"));
		GameRegistry.register(LONG_IRON_SKIN, new ResourceLocation("rustic:long_ironskin"));
		GameRegistry.register(STRONG_IRON_SKIN, new ResourceLocation("rustic:strong_ironskin"));
	}

}
