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
	public static final Potion FEATHER_POTION = new PotionFeather();
	public static final Potion BLAZING_TRAIL_POTION = new PotionBlazingTrail();
	public static final Potion SHAME_POTION = new PotionShame();
	
	public static final Potion FULL_POTION = new PotionBase(false, 6563840, "full").setIconIndex(5, 0);
	public static final Potion MAGIC_RESISTANCE_POTION = new PotionBase(false, 10511560, "magic_resistance").setIconIndex(6, 0);
	public static final Potion WITHER_WARD_POTION = new PotionBase(false, 11842760, "wither_ward").setIconIndex(7, 0);
	
	public static final Potion TIPSY = new PotionTipsy();
	
	public static final PotionType IRON_SKIN = new PotionType("ironskin", new PotionEffect[] { new PotionEffect(IRON_SKIN_POTION, 3600, 0, false, true) });
	public static final PotionType LONG_IRON_SKIN = new PotionType("ironskin", new PotionEffect[] { new PotionEffect(IRON_SKIN_POTION, 9600, 0, false, true) });
	public static final PotionType STRONG_IRON_SKIN = new PotionType("ironskin", new PotionEffect[] { new PotionEffect(IRON_SKIN_POTION, 1800, 1, false, true) });
	
	public static void init() {
		//Potions
		GameRegistry.register(IRON_SKIN_POTION, new ResourceLocation("rustic:ironskin"));
		GameRegistry.register(FEATHER_POTION, new ResourceLocation("rustic:feather"));
		GameRegistry.register(BLAZING_TRAIL_POTION, new ResourceLocation("rustic:blazing_trail"));
		GameRegistry.register(SHAME_POTION, new ResourceLocation("rustic:shame"));
		
		GameRegistry.register(FULL_POTION, new ResourceLocation("rustic:full"));
		GameRegistry.register(MAGIC_RESISTANCE_POTION, new ResourceLocation("rustic:magic_resistance"));
		GameRegistry.register(WITHER_WARD_POTION, new ResourceLocation("rustic:wither_ward"));
		
		GameRegistry.register(TIPSY, new ResourceLocation("rustic:tipsy"));
		
		//Potion Types
		GameRegistry.register(IRON_SKIN, new ResourceLocation("rustic:ironskin"));
		GameRegistry.register(LONG_IRON_SKIN, new ResourceLocation("rustic:long_ironskin"));
		GameRegistry.register(STRONG_IRON_SKIN, new ResourceLocation("rustic:strong_ironskin"));
	}

}
