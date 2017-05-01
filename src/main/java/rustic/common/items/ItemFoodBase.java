package rustic.common.items;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemFood;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.core.Rustic;

public class ItemFoodBase extends ItemFood {
	
	public ItemFoodBase(String name, int hunger, float saturation, boolean wolfFood) {
		super(hunger, saturation, wolfFood);
		setRegistryName(name);
		setUnlocalizedName(Rustic.MODID + "." + name);
		setCreativeTab(Rustic.farmingTab);
		GameRegistry.register(this);
		initFood();
	}
	
	public void initFood() {}
	
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName().toString()));
	}

}
