package rustic.common.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.models.FluidBottleModel;
import rustic.client.util.ClientUtils;
import rustic.common.blocks.fluids.FluidBooze;
import rustic.common.blocks.fluids.FluidDrinkable;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.util.RusticUtils;
import rustic.core.ClientProxy;
import rustic.core.Rustic;

public class ItemFluidBottle extends ItemFluidContainer implements IColoredItem {

	protected static final CreativeTabs[] creative_tabs = { Rustic.farmingTab, Rustic.alchemyTab, };
	
	public static List<Fluid> VALID_FLUIDS = new ArrayList<Fluid>();
	public static final String FLUID_NBT_KEY = "Fluid";

	public static void addFluid(Fluid fluid) {
		VALID_FLUIDS.add(fluid);
	}

	private final ItemStack empty;

	public ItemFluidBottle() {
		super(1000);
		setRegistryName("fluid_bottle");
		setUnlocalizedName(Rustic.MODID + "." + "fluid_bottle");
		setCreativeTab(Rustic.farmingTab);
		GameRegistry.findRegistry(Item.class).register(this);
		empty = new ItemStack(Items.GLASS_BOTTLE);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ItemMeshDefinition meshDefinition = new ItemMeshDefinition() {
			@Override
			public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
				return FluidBottleModel.LOCATION;
			}
		};
		ModelLoader.setCustomMeshDefinition(this, meshDefinition);
		ModelLoader.registerItemVariants(this, FluidBottleModel.LOCATION);
		ClientProxy.addColoredItem(this);
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getDefaultInstance() {
		NBTTagCompound nbt = new FluidStack(ModFluids.OLIVE_OIL, this.getCapacity()).writeToNBT(new NBTTagCompound());
		ItemStack stack = super.getDefaultInstance();
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag(FLUID_NBT_KEY, nbt);
		stack.setTagCompound(nbt);
		return stack;
	}

	@Nonnull
	public ItemStack getFilledBottle(Fluid fluid) {
		ItemStack bottle = new ItemStack(this);

		if (ItemFluidBottle.VALID_FLUIDS.contains(fluid) && FluidRegistry.getFluidName(fluid) != null) {
			NBTTagCompound fluidTag = new NBTTagCompound();
			FluidStack fluidStack = new FluidStack(fluid, this.getCapacity());
			fluidStack.writeToNBT(fluidTag);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag(FLUID_NBT_KEY, fluidTag);
			bottle.setTagCompound(tag);
		}

		return bottle;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IItemColor getItemColor() {
		return new IItemColor() {
			@Override
			public int colorMultiplier(ItemStack stack, int tintIndex) {
				if (tintIndex == 2) {
					FluidStack fluidstack = ItemFluidBottle.this.getFluid(stack);
					Fluid fluid = fluidstack.getFluid() != null ? fluidstack.getFluid() : null;
					if ((fluidstack.getFluid() != null) && (fluidstack.getFluid() instanceof FluidBooze)) {
						float quality = 0.5f;
						if (fluidstack.tag != null && fluidstack.tag.hasKey(FluidBooze.QUALITY_NBT_KEY, 5)) {
							quality = fluidstack.tag.getFloat(FluidBooze.QUALITY_NBT_KEY);
						}
						return ClientUtils.getQualityLabelColor(quality);
					}
				}
				return 0xFFFFFF;
			}
		};
	}

	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		EntityPlayer entityplayer = entityLiving instanceof EntityPlayer ? (EntityPlayer) entityLiving : null;
		FluidStack fluidstack = this.getFluid(stack);
		Fluid fluid = fluidstack.getFluid() != null ? fluidstack.getFluid() : null;

		if (!worldIn.isRemote) {
			if (fluid instanceof FluidDrinkable && entityplayer != null) {
				((FluidDrinkable) fluid).onDrank(worldIn, entityplayer, stack, fluidstack);
			}
		}

		if (entityplayer != null) {
			entityplayer.addStat(StatList.getObjectUseStats(this));
			
			if (entityplayer instanceof EntityPlayerMP) {
	            CriteriaTriggers.CONSUME_ITEM.trigger((EntityPlayerMP) entityplayer, stack);
	        }
		}

		if ((entityplayer == null) || !entityplayer.capabilities.isCreativeMode) {
			stack.shrink(1);
			
			if (stack.isEmpty()) {
				return new ItemStack(Items.GLASS_BOTTLE);
			} else if (entityplayer != null) {
				RusticUtils.givePlayerItem(entityplayer, new ItemStack(Items.GLASS_BOTTLE));
				//entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
			}
		}

		return stack;
	}

	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}

	public EnumAction getItemUseAction(ItemStack stack) {
		if (getFluid(stack) != null && getFluid(stack).getFluid() != null
				&& getFluid(stack).getFluid() instanceof FluidDrinkable) {
			return EnumAction.DRINK;
		}
		return EnumAction.NONE;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.setActiveHand(handIn);
		//System.out.println(playerIn.getHeldItem(handIn).getTagCompound());
		return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Nullable
	public FluidStack getFluid(@Nonnull ItemStack container) {
		if (container.hasTagCompound() && container.getTagCompound().hasKey(FLUID_NBT_KEY)) {
			return FluidStack.loadFluidStackFromNBT(container.getTagCompound().getCompoundTag(FLUID_NBT_KEY));
		}
		return null;
	}

	public int getCapacity() {
		return capacity;
	}

	@Override
	public boolean hasContainerItem(@Nonnull ItemStack stack) {
		return true;
	}

	@Nonnull
	@Override
	public ItemStack getContainerItem(@Nonnull ItemStack itemStack) {
		return this.empty;
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		FluidStack fluidStack = getFluid(stack);
		if ((fluidStack != null) && (fluidStack.getFluid() != null)) {
			return fluidStack.getFluid().getRarity(fluidStack);
		}
		return super.getRarity(stack);
	}
	
	@Override
	@Nonnull
	public String getItemStackDisplayName(@Nonnull ItemStack stack) {
		FluidStack fluidStack = getFluid(stack);
		if (fluidStack == null) {
			return empty.getDisplayName();
		}

		String unloc = this.getUnlocalizedName(stack);

		return I18n.translateToLocalFormatted(unloc + ".name", fluidStack.getLocalizedName());
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		FluidStack fluidStack = getFluid(stack);
		if ((fluidStack != null) && (fluidStack.getFluid() != null) && (fluidStack.getFluid() instanceof FluidDrinkable)) {
			tooltip.add(TextFormatting.DARK_GRAY + "" + TextFormatting.ITALIC + I18n.translateToLocalFormatted("tooltip.rustic.drinkable"));
		}
	}
	
	@Override
	public CreativeTabs[] getCreativeTabs() {
		return creative_tabs;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab)) {
			if (tab == Rustic.farmingTab) {
				for (Fluid fluid : VALID_FLUIDS) {
					ItemStack stack = getFilledBottle(fluid);
					if (fluid instanceof FluidBooze) {
						if (stack.hasTagCompound() && stack.getTagCompound().hasKey(FluidHandlerItemStack.FLUID_NBT_KEY)) {
							NBTTagCompound fluidTag = stack.getTagCompound()
									.getCompoundTag(FluidHandlerItemStack.FLUID_NBT_KEY);
							if (!fluidTag.hasKey("Tag")) {
								fluidTag.setTag("Tag", new NBTTagCompound());
							}
							if (!fluidTag.getCompoundTag("Tag").hasKey(FluidBooze.QUALITY_NBT_KEY)) {
								fluidTag.getCompoundTag("Tag").setFloat(FluidBooze.QUALITY_NBT_KEY, 0.75F);
							}
						}
					}
					subItems.add(stack);
				}
			} else if (tab == Rustic.alchemyTab) {
				ItemStack oilStack = getFilledBottle(ModFluids.VANTA_OIL);
				subItems.add(oilStack);
			}
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
		FluidHandlerItemStack.SwapEmpty handler = new FluidHandlerItemStack.SwapEmpty(stack, empty.copy(), capacity) {
			@Override
			public boolean canFillFluidType(FluidStack fluidstack) {
				return ItemFluidBottle.VALID_FLUIDS.contains(fluidstack.getFluid());
			}

			@Override
			public int fill(FluidStack resource, boolean doFill) {
				if (resource == null || resource.amount < this.capacity || getFluid() != null
						|| !canFillFluidType(resource)) {
					return 0;
				}
				if (doFill) {
					setFluid(resource.copy());
				}
				return this.capacity;
			}

			protected void setFluid(@Nullable FluidStack fluid) {
				if (fluid == null) {
					container = this.emptyContainer;
				} else {
					container = new ItemStack(ItemFluidBottle.this, 1);
					FluidStack fs = fluid.copy();
					fs.amount = this.capacity;
					NBTTagCompound fluidTag = fs.writeToNBT(new NBTTagCompound());
					NBTTagCompound tag = new NBTTagCompound();
					tag.setTag(FLUID_NBT_KEY, fluidTag);
					container.setTagCompound(tag);
				}
			}

			@Override
			public FluidStack drain(FluidStack resource, boolean doDrain) {
				if (resource == null || resource.amount < this.capacity) {
					return null;
				}
				return super.drain(resource, doDrain);
			}

			@Override
			public FluidStack drain(int maxDrain, boolean doDrain) {
				if (maxDrain < this.capacity) {
					return null;
				}
				return super.drain(maxDrain, doDrain);
			}

			@Nonnull
			@Override
			public ItemStack getContainer() {
				FluidStack contained = getFluid();
				if (contained == null || contained.getFluid() == null || contained.amount <= 0) {
					return this.emptyContainer;
				}
				return container;
			}

			@SuppressWarnings("unchecked")
			@Override
			@Nullable
			public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
				return capability == CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY ? (T) this : null;
			}
		};
		return handler;
	}

}
