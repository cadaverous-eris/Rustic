package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerCondenserAdvanced extends Container {

private TileEntityCondenserAdvancedBottom te;
	
	public ContainerCondenserAdvanced(IInventory playerInventory, TileEntityCondenserAdvancedBottom te) {
		this.te = te;
		addOwnSlots();
		addPlayerSlots(playerInventory);
	}

	public TileEntityCondenserAdvancedBottom getTile() {
		return te;
	}

	private void addPlayerSlots(IInventory playerInventory) {
		for (int row = 0; row < 3; ++row) {
			for (int col = 0; col < 9; ++col) {
				int x = 8 + col * 18;
				int y = row * 18 + 84;
				this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 9, x, y));
			}
		}

		for (int row = 0; row < 9; ++row) {
			int x = 8 + row * 18;
			int y = 142;
			this.addSlotToContainer(new Slot(playerInventory, row, x, y));
		}
	}

	private void addOwnSlots() {
		IItemHandler itemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		addSlotToContainer(new SlotItemHandler(itemHandler, TileEntityCondenserBase.SLOT_RESULT, 105, 35) {
			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				return false;
			}
		});
		addSlotToContainer(new SlotItemHandler(itemHandler, TileEntityCondenserBase.SLOT_FUEL, 66, 62) {
			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				return super.isItemValid(stack) && TileEntityFurnace.isItemFuel(stack);
			}
		});
		addSlotToContainer(new SlotItemHandler(itemHandler, TileEntityCondenserBase.SLOT_BOTTLE, 105, 7));
		addSlotToContainer(new SlotItemHandler(itemHandler, TileEntityCondenserBase.SLOT_INGREDIENTS_START, 66, 7)); // modifier
		addSlotToContainer(new SlotItemHandler(itemHandler, TileEntityCondenserBase.SLOT_INGREDIENTS_START + 1, 27, 11));
		addSlotToContainer(new SlotItemHandler(itemHandler, TileEntityCondenserBase.SLOT_INGREDIENTS_START + 2, 27, 35));
		addSlotToContainer(new SlotItemHandler(itemHandler, TileEntityCondenserBase.SLOT_INGREDIENTS_START + 3, 27, 59));
	}
	
	@Nullable
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = (Slot)this.inventorySlots.get(index);
        
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < TileEntityCondenserAdvancedBottom.SLOT_NUM) {
                if (!this.mergeItemStack(itemstack1, TileEntityCondenserAdvancedBottom.SLOT_NUM, TileEntityCondenserAdvancedBottom.SLOT_NUM+36, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index >= TileEntityCondenserAdvancedBottom.SLOT_NUM) {
                if (itemstack1.getItem().equals(Items.GLASS_BOTTLE)) {
                    if (!this.mergeItemStack(itemstack1, TileEntityCondenserBase.SLOT_BOTTLE, TileEntityCondenserBase.SLOT_BOTTLE + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (TileEntityFurnace.isItemFuel(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, TileEntityCondenserBase.SLOT_FUEL, TileEntityCondenserBase.SLOT_FUEL + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.mergeItemStack(itemstack1, TileEntityCondenserBase.SLOT_INGREDIENTS_START, TileEntityCondenserAdvancedBottom.SLOT_NUM, false)) {
                    return ItemStack.EMPTY;
                } else if (index < TileEntityCondenserAdvancedBottom.SLOT_NUM + 27) {
                    if (!this.mergeItemStack(itemstack1, 34, 43, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= TileEntityCondenserAdvancedBottom.SLOT_NUM + 27 && index < TileEntityCondenserAdvancedBottom.SLOT_NUM  + 36 && !this.mergeItemStack(itemstack1, TileEntityCondenserAdvancedBottom.SLOT_NUM, TileEntityCondenserAdvancedBottom.SLOT_NUM + 27, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, TileEntityCondenserAdvancedBottom.SLOT_NUM, TileEntityCondenserAdvancedBottom.SLOT_NUM + 36, false)) {
                return ItemStack.EMPTY;
            }
            
            if (itemstack1.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }
        
		return itemstack;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
