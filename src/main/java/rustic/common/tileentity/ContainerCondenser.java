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

public class ContainerCondenser extends Container {

	private TileEntityCondenser te;
	
	public ContainerCondenser(IInventory playerInventory, TileEntityCondenser te) {
		this.te = te;
		addOwnSlots();
		addPlayerSlots(playerInventory);
	}

	public TileEntityCondenser getTile() {
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
		addSlotToContainer(new SlotItemHandler(itemHandler, 0, 27, 47));
		addSlotToContainer(new SlotItemHandler(itemHandler, 1, 27, 23));
		addSlotToContainer(new SlotItemHandler(itemHandler, 2, 66, 62) {
			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				return super.isItemValid(stack) && TileEntityFurnace.isItemFuel(stack);
			}
		});
		addSlotToContainer(new SlotItemHandler(itemHandler, 3, 105, 7) {
			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				return super.isItemValid(stack) && stack.getItem().equals(Items.GLASS_BOTTLE);
			}
		});
		addSlotToContainer(new SlotItemHandler(itemHandler, 4, 105, 35) {
			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				return false;
			}
		});
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
            if (index < 5) {
                if (!this.mergeItemStack(itemstack1, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else if (index > 4) {
                if (itemstack1.getItem().equals(Items.GLASS_BOTTLE)) {
                    if (!this.mergeItemStack(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (TileEntityFurnace.isItemFuel(itemstack1)) {
                    if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.mergeItemStack(itemstack1, 0, 2, false)) {
                    return ItemStack.EMPTY;
                } else if (index >= 5 && index < 32) {
                    if (!this.mergeItemStack(itemstack1, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 32 && index < 41 && !this.mergeItemStack(itemstack1, 5, 32, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 5, 41, false)) {
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
