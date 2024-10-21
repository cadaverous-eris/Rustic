package rustic.common.potions;

import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PotionsRustic {

	public static final Potion IRON_SKIN_POTION = new PotionIronSkin().setRegistryName(new ResourceLocation("rustic:ironskin"));
	public static final Potion FEATHER_POTION = new PotionFeather().setRegistryName(new ResourceLocation("rustic:feather"));
	public static final Potion BLAZING_TRAIL_POTION = new PotionBlazingTrail().setRegistryName(new ResourceLocation("rustic:blazing_trail"));
	public static final Potion SHAME_POTION = new PotionShame().setRegistryName(new ResourceLocation("rustic:shame"));
	public static final Potion FULLMETAL_POTION = new PotionFullmetal().setRegistryName(new ResourceLocation("rustic:fullmetal"));
	public static final Potion FIRE_POWER_POTION = new PotionBase(false, 0xFFCE6D, "fire_power").setIconIndex(2, 1).setRegistryName(new ResourceLocation("rustic:fire_power"));
	
	public static final Potion FULL_POTION = new PotionBase(false, 6563840, "full").setIconIndex(5, 0).setRegistryName(new ResourceLocation("rustic:full"));
	public static final Potion MAGIC_RESISTANCE_POTION = new PotionBase(false, 10511560, "magic_resistance").setIconIndex(6, 0).setRegistryName(new ResourceLocation("rustic:magic_resistance"));
	public static final Potion WITHER_WARD_POTION = new PotionBase(false, 11842760, "wither_ward").setIconIndex(7, 0).setRegistryName(new ResourceLocation("rustic:wither_ward"));
	public static final Potion UNDYING_POTION = new PotionBase(false, 0xEADB84, "undying").setIconIndex(1, 1).setRegistryName(new ResourceLocation("rustic:undying"));;
	
	public static final Potion TIPSY = new PotionTipsy().setRegistryName(new ResourceLocation("rustic:tipsy"));
	
	
	public static void init() {
		//Potions
		GameRegistry.findRegistry(Potion.class).register(IRON_SKIN_POTION);
		GameRegistry.findRegistry(Potion.class).register(FEATHER_POTION);
		GameRegistry.findRegistry(Potion.class).register(BLAZING_TRAIL_POTION);
		GameRegistry.findRegistry(Potion.class).register(SHAME_POTION);
		GameRegistry.findRegistry(Potion.class).register(FULLMETAL_POTION);
		GameRegistry.findRegistry(Potion.class).register(FIRE_POWER_POTION);
		
		GameRegistry.findRegistry(Potion.class).register(FULL_POTION);
		GameRegistry.findRegistry(Potion.class).register(MAGIC_RESISTANCE_POTION);
		GameRegistry.findRegistry(Potion.class).register(WITHER_WARD_POTION);
		GameRegistry.findRegistry(Potion.class).register(UNDYING_POTION);

		GameRegistry.findRegistry(Potion.class).register(TIPSY);
	}

}
