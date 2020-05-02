package rustic.client.util;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.item.ItemStack;
import rustic.client.util.ClientUtils;

public class ItemColorCache implements IResourceManagerReloadListener {

	public static final ItemColorCache INSTANCE = new ItemColorCache();
	
	protected final Map<String, Integer> itemColors = new HashMap<String, Integer>();
	
	public int getColor(ItemStack stack) {
		String itemKey = stack.getItem().getUnlocalizedName() + "@" + stack.getItemDamage();
		
		if (!this.itemColors.containsKey(itemKey)) {
			this.itemColors.put(itemKey, ClientUtils.getItemColor(stack));
		}
		
		return this.itemColors.get(itemKey);
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager resourceManager) {
		this.itemColors.clear();
	}

}
