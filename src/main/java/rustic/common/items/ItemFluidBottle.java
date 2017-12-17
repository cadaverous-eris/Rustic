package rustic.common.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.models.FluidBottleModel;
import rustic.common.blocks.fluids.FluidBooze;
import rustic.common.blocks.fluids.FluidDrinkable;
import rustic.common.blocks.fluids.ModFluids;
import rustic.core.Rustic;

public class ItemFluidBottle extends ItemFluidContainer {

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
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getDefaultInstance() {
		NBTTagCompound nbt = new FluidStack(ModFluids.OLIVE_OIL, 1000).writeToNBT(new NBTTagCompound());
		ItemStack stack = super.getDefaultInstance();
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag(FLUID_NBT_KEY, nbt);
		stack.setTagCompound(nbt);
		return stack;
	}

	@Nonnull
	public ItemStack getFilledBottle(Fluid fluid) {
		ItemStack bottle = new ItemStack(this);

		if (this.VALID_FLUIDS.contains(fluid) && FluidRegistry.getFluidName(fluid) != null) {
			NBTTagCompound fluidTag = new NBTTagCompound();
			FluidStack fluidStack = new FluidStack(fluid, this.getCapacity());
			fluidStack.writeToNBT(fluidTag);
			NBTTagCompound tag = new NBTTagCompound();
			tag.setTag(FLUID_NBT_KEY, fluidTag);
			bottle.setTagCompound(tag);
		}

		return bottle;
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
		}

		if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
			stack.shrink(1);
		}

		if (entityplayer == null || !entityplayer.capabilities.isCreativeMode) {
			if (stack.isEmpty()) {
				return new ItemStack(Items.GLASS_BOTTLE);
			}

			if (entityplayer != null) {
				entityplayer.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
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
		System.out.println(playerIn.getHeldItem(handIn).getTagCompound());
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
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (isInCreativeTab(tab)) {
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
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
		FluidHandlerItemStack.SwapEmpty handler = new FluidHandlerItemStack.SwapEmpty(stack, empty, capacity) {
			@Override
			public boolean canFillFluidType(FluidStack fluidstack) {
				return ItemFluidBottle.VALID_FLUIDS.contains(fluidstack.getFluid());
			}

			@Override
			public int fill(FluidStack resource, boolean doFill) {
				if (resource == null || resource.amount < Fluid.BUCKET_VOLUME || getFluid() != null
						|| !canFillFluidType(resource)) {
					return 0;
				}
				if (doFill) {
					setFluid(resource.copy());
				}
				return Fluid.BUCKET_VOLUME;
			}

			protected void setFluid(@Nullable FluidStack fluid) {
				if (fluid == null) {
					container = new ItemStack(Items.GLASS_BOTTLE);
				} else {
					container = new ItemStack(ModItems.FLUID_BOTTLE);
					FluidStack fs = fluid.copy();
					fs.amount = 1000;
					NBTTagCompound fluidTag = fs.writeToNBT(new NBTTagCompound());
					NBTTagCompound tag = new NBTTagCompound();
					tag.setTag(FLUID_NBT_KEY, fluidTag);
					container.setTagCompound(tag);
				}
			}

			@Override
			public FluidStack drain(FluidStack resource, boolean doDrain) {
				if (resource == null || resource.amount < Fluid.BUCKET_VOLUME) {
					return null;
				}
				return super.drain(resource, doDrain);
			}

			@Override
			public FluidStack drain(int maxDrain, boolean doDrain) {
				if (maxDrain < Fluid.BUCKET_VOLUME) {
					return null;
				}
				return super.drain(maxDrain, doDrain);
			}

			@Nonnull
			@Override
			public ItemStack getContainer() {
				FluidStack contained = getFluid();
				if (contained == null || contained.getFluid() == null || contained.amount <= 0) {
					return new ItemStack(Items.GLASS_BOTTLE);
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
