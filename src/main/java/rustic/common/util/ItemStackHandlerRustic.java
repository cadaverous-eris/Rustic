package rustic.common.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.items.ItemStackHandler;

public class ItemStackHandlerRustic extends ItemStackHandler {
	
	public ItemStackHandlerRustic(int size) {
		super(size);
	}
	
	public NonNullList<ItemStack> getStacks() {
		return this.stacks;
	}

}
