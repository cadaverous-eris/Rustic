package rustic.common.tileentity;

import java.util.Random;
import java.lang.Math;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import rustic.common.Config;
import rustic.common.items.ModItems;

public class TileEntityApiary extends TileEntity implements ITickable {

	private ItemStackHandler beeItemStackHandler = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			TileEntityApiary.this.markDirty();
		}
		@Override
	    @Nonnull
	    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (stack.getItem().equals(ModItems.BEE)) {
				return super.insertItem(slot, stack, simulate);
			}
			return stack;
		}
	};
	private ItemStackHandler honeyCombItemStackHandler = new ItemStackHandler(1) {
		@Override
		protected void onContentsChanged(int slot) {
			TileEntityApiary.this.markDirty();
		}
		@Override
	    @Nonnull
	    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (stack.getItem().equals(ModItems.HONEYCOMB)) {
				return super.insertItem(slot, stack, simulate);
			}
			return stack;
		}
	};
	private int reproductionTime = 1200;
	private int reproductionTimer = 0;
	private int productionTime = 600;
	private int productionTimer = 0;
	private Random random = new Random();
	
	public ITextComponent getDisplayName() {
        return (ITextComponent)new TextComponentTranslation(this.getName(), new Object[0]);
    }
	
	public String getName() {
        return "container.rustic.apiary";
    }

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("items")) {
			ItemStackHandler tempHandler = new ItemStackHandler(2);
			tempHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
			beeItemStackHandler.setStackInSlot(0, tempHandler.getStackInSlot(0));
			honeyCombItemStackHandler.setStackInSlot(0, tempHandler.getStackInSlot(1));
		}
		if (compound.hasKey("bees")) {
			beeItemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("bees"));
		}
		if (compound.hasKey("honeyComb")) {
			honeyCombItemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("honeyComb"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("bees", beeItemStackHandler.serializeNBT());
		compound.setTag("honeyComb", honeyCombItemStackHandler.serializeNBT());
		return compound;
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == EnumFacing.DOWN) {
				return (T) honeyCombItemStackHandler;
			}
			return (T) beeItemStackHandler;
		}
		return super.getCapability(capability, facing);
	}

	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		this.invalidate();
		if (beeItemStackHandler != null && !world.isRemote) {
			for (int i = 0; i < beeItemStackHandler.getSlots(); i++) {
				if (beeItemStackHandler.getStackInSlot(i) != null) {
					Block.spawnAsEntity(world, pos, beeItemStackHandler.getStackInSlot(i));
				}
			}
		}
		if (honeyCombItemStackHandler != null && !world.isRemote) {
			for (int i = 0; i < honeyCombItemStackHandler.getSlots(); i++) {
				if (honeyCombItemStackHandler.getStackInSlot(i) != null) {
					Block.spawnAsEntity(world, pos, honeyCombItemStackHandler.getStackInSlot(i));
				}
			}
		}
		world.setTileEntity(pos, null);
	}

	@Override
	public void update() {

		int numBees = 0;
		if (beeItemStackHandler.getStackInSlot(0).getItem().equals(ModItems.BEE)) {
			numBees = beeItemStackHandler.getStackInSlot(0).getCount();
		}
		int numHoneycomb = 0;
		if (honeyCombItemStackHandler.getStackInSlot(0).getItem().equals(ModItems.HONEYCOMB)) {
			numHoneycomb = honeyCombItemStackHandler.getStackInSlot(0).getCount();
		}

		if (numBees > 0 && !this.getWorld().isRemote) {
			reproductionTime = (int) (Config.BEE_REPRODUCTION_MULTIPLIER * (1600F / ((numBees / 20F) + 1F)));
			productionTime = (int) (Config.BEE_HONEYCOMB_MULTIPLIER * (800F / ((numBees / 20F) + 1F)));

			reproductionTimer++;
			productionTimer++;
			if (reproductionTimer >= reproductionTime) {
				reproductionTimer = 0;
				if (numBees < 64) {
					beeItemStackHandler.getStackInSlot(0).grow(1);
				}
			}
			if (productionTimer >= productionTime) {
				productionTimer = 0;
				if (numHoneycomb == 0) {
					honeyCombItemStackHandler.setStackInSlot(0, new ItemStack(ModItems.HONEYCOMB, 1));
				} else if (numHoneycomb < 64) {
					honeyCombItemStackHandler.getStackInSlot(0).grow(1);
				}
			}

			if (Config.BEE_GROWTH_MULTIPLIER != 0 && random.nextInt((int) Math.ceil(2048F / (numBees * Config.BEE_GROWTH_MULTIPLIER))) == 0) {
				//System.out.println("apiary growth");
				int randX = random.nextInt(9) - 4;
				int randZ = random.nextInt(9) - 4;
				int x = this.getPos().getX();
				int y = this.getPos().getY();
				int z = this.getPos().getZ();
				for (int i = 0; i < 3; i++) {
					BlockPos pos = new BlockPos(x + randX, y + 1 - i, z + randZ);
					Block block = this.getWorld().getBlockState(pos).getBlock();
					if (block instanceof IGrowable || block instanceof IPlantable) {
						block.updateTick(world, pos, world.getBlockState(pos), world.rand);
						world.updateBlockTick(pos, block, 0, 1);
						break;
					}
				}
			}

			markDirty();

		}

	}

}
