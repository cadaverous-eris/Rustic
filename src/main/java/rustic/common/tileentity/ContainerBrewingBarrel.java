package rustic.common.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import rustic.common.blocks.fluids.FluidBooze;

public class ContainerBrewingBarrel extends Container {
	
	private TileEntityBrewingBarrel te;

	public ContainerBrewingBarrel(IInventory playerInventory, TileEntityBrewingBarrel te) {
		this.te = te;
		addOwnSlots();
		addPlayerSlots(playerInventory);
	}
	
	public TileEntityBrewingBarrel getTile() {
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
		addSlotToContainer(new SlotItemHandler(itemHandler, 0, 62, 7) {
			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				if (!super.isItemValid(stack)) {
					return false;
				}
				return FluidUtil.getFluidHandler(stack) != null ||  stack.getItem() instanceof UniversalBucket || stack.getItem() instanceof ItemFluidContainer || stack.getItem() == Items.GLASS_BOTTLE || stack.getItem() instanceof ItemBucket;
			}
		});
		addSlotToContainer(new SlotItemHandler(itemHandler, 1, 116, 7) {
			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				if (!super.isItemValid(stack)) {
					return false;
				}
				return stack.getItem() == Items.GLASS_BOTTLE;
			}
		});
		addSlotToContainer(new SlotItemHandler(itemHandler, 2, 26, 15) {
			@Override
			public boolean isItemValid(@Nonnull ItemStack stack) {
				if (!super.isItemValid(stack)) {
					return false;
				}
				if (FluidUtil.getFluidHandler(stack) != null) {
					FluidStack fluid = FluidUtil.getFluidContained(stack);
					return fluid != null && fluid.getFluid() != null && fluid.getFluid() instanceof FluidBooze;
				} else {
					return stack.getItem() == Items.GLASS_BOTTLE;
				}
			}
		});
		addSlotToContainer(new OutputSlot(itemHandler, 3, 62, 63));
		addSlotToContainer(new OutputSlot(itemHandler, 4, 116, 63));
		addSlotToContainer(new OutputSlot(itemHandler, 5, 26, 55));
	}
	
	@Nullable
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < 6) {
                if (!this.mergeItemStack(itemstack1, 6, 42, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(itemstack1, itemstack);
            } else {
                if (itemstack1.getItem().equals(Items.GLASS_BOTTLE)) {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                    	if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    		if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
                    			return ItemStack.EMPTY;
                    		}
                    	}
                    } else if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                		if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
                			return ItemStack.EMPTY;
                		}
                	} else if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
            			return ItemStack.EMPTY;
            		}
                } else if (itemstack1.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null)) {
                	FluidStack fluid = FluidUtil.getFluidContained(itemstack1);
                	if (fluid == null || fluid.getFluid() == null) {
                    	if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
                        	if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                        		if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
                        			return ItemStack.EMPTY;
                        		}
                        	}
                        } else if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    		if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
                    			return ItemStack.EMPTY;
                    		}
                    	} else if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
                			return ItemStack.EMPTY;
                		}
                    } else if (fluid.getFluid() instanceof FluidBooze) {
                    	if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
                    		return ItemStack.EMPTY;
                    	}
                    } else {
                    	if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
                    		return ItemStack.EMPTY;
                    	}
                    }
                } else if (index > 5 && index < 33) {
                    if (!this.mergeItemStack(itemstack1, 33, 42, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index > 32 && index < 42 && !this.mergeItemStack(itemstack1, 6, 33, false)) {
                    return ItemStack.EMPTY;
                }
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
	
	public class OutputSlot extends SlotItemHandler {

		public OutputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
			super(itemHandler, index, xPosition, yPosition);
		}
		
		@Override
		public boolean isItemValid(@Nonnull ItemStack stack) {
			return false;
		}
		
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
