package rustic.common.advancements;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.advancements.critereon.ItemPredicates;
import rustic.core.Rustic;

public class ModAdvancements {
	
	public static void preInit() {
		ItemPredicates.register(new ResourceLocation(Rustic.MODID, "alcohol"), AlcoholItemPredicate::deserialize);
	}
	
}
