package rustic.common.items;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDynBucket;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.ItemFluidContainer;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.models.FluidBottleModel;
import rustic.common.blocks.fluids.FluidDrinkable;
import rustic.common.blocks.fluids.ModFluids;
import rustic.core.Rustic;

public class ItemFluidBottle extends ItemFluidContainer {

	public static List<Fluid> VALID_FLUIDS = new ArrayList<Fluid>();

	public static void addFluid(Fluid fluid) {
		VALID_FLUIDS.add(fluid);
	}

	private final ItemStack empty;

	public ItemFluidBottle() {
		super(1000);
		setRegistryName("fluid_bottle");
		setUnlocalizedName(Rustic.MODID + "." + "fluid_bottle");
		setCreativeTab(Rustic.tab);
		GameRegistry.register(this);
		empty = new ItemStack(Items.GLASS_BOTTLE);
	}

	public void initModel() {
		//ItemMeshDefinition meshDefinition = new ItemMeshDefinition() {
		//	@Override
		//	public ModelResourceLocation getModelLocation(ItemStack stack) {
		//		return new ModelResourceLocation(new ResourceLocation(Rustic.MODID, "fluid_bottle"), "inventory");
		//	}
		//};
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
		stack.setTagCompound(nbt);
		return stack;
	}

	@Nonnull
	public ItemStack getFilledBottle(@Nonnull ItemFluidBottle item, Fluid fluid) {
		ItemStack bottle = new ItemStack(item);

		if (this.VALID_FLUIDS.contains(fluid)) {
			NBTTagCompound tag = new NBTTagCompound();
			FluidStack fluidStack = new FluidStack(fluid, item.getCapacity());
			fluidStack.writeToNBT(tag);
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
				((FluidDrinkable) fluid).onDrank(worldIn, entityplayer, stack);
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
		return EnumAction.DRINK;
	}

	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		playerIn.setActiveHand(handIn);
		return new ActionResult(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
	}

	@Nullable
	public FluidStack getFluid(@Nonnull ItemStack container) {
		return FluidStack.loadFluidStackFromNBT(container.getTagCompound());
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

	@SideOnly(Side.CLIENT)
	public void getSubItems(Item itemIn, CreativeTabs tab, NonNullList<ItemStack> subItems) {
		for (Fluid fluid : VALID_FLUIDS) {
			ItemStack stack = new ItemStack(this);
			NBTTagCompound nbt = new FluidStack(fluid, 1000).writeToNBT(new NBTTagCompound());
			stack.setTagCompound(nbt);
			subItems.add(stack);
		}
	}

	@Override
	public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable NBTTagCompound nbt) {
		FluidHandlerItemStack handler = new FluidHandlerItemStack(stack, capacity);
		return handler;
	}

}
