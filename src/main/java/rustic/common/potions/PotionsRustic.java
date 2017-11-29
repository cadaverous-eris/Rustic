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

	public static final Potion IRON_SKIN_POTION = new PotionIronSkin().setRegistryName(new ResourceLocation("rustic:ironskin"));
	public static final Potion FEATHER_POTION = new PotionFeather().setRegistryName(new ResourceLocation("rustic:feather"));
	public static final Potion BLAZING_TRAIL_POTION = new PotionBlazingTrail().setRegistryName(new ResourceLocation("rustic:blazing_trail"));
	public static final Potion SHAME_POTION = new PotionShame().setRegistryName(new ResourceLocation("rustic:shame"));
	
	public static final Potion FULL_POTION = new PotionBase(false, 6563840, "full").setIconIndex(5, 0).setRegistryName(new ResourceLocation("rustic:full"));
	public static final Potion MAGIC_RESISTANCE_POTION = new PotionBase(false, 10511560, "magic_resistance").setIconIndex(6, 0).setRegistryName(new ResourceLocation("rustic:magic_resistance"));
	public static final Potion WITHER_WARD_POTION = new PotionBase(false, 11842760, "wither_ward").setIconIndex(7, 0).setRegistryName(new ResourceLocation("rustic:wither_ward"));
	
	public static final Potion TIPSY = new PotionTipsy().setRegistryName(new ResourceLocation("rustic:tipsy"));
	
	public static void init() {
		//Potions
		GameRegistry.findRegistry(Potion.class).register(IRON_SKIN_POTION);
		GameRegistry.findRegistry(Potion.class).register(FEATHER_POTION);
		GameRegistry.findRegistry(Potion.class).register(BLAZING_TRAIL_POTION);
		GameRegistry.findRegistry(Potion.class).register(SHAME_POTION);
		
		GameRegistry.findRegistry(Potion.class).register(FULL_POTION);
		GameRegistry.findRegistry(Potion.class).register(MAGIC_RESISTANCE_POTION);
		GameRegistry.findRegistry(Potion.class).register(WITHER_WARD_POTION);
		
		GameRegistry.findRegistry(Potion.class).register(TIPSY);
	}

}
