package rustic.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.EventHandlerClient;
import rustic.client.renderer.EventHandlerCommon;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.crafting.Recipes;

@Mod(modid = Rustic.MODID, name = Rustic.NAME, version = Rustic.VERSION, useMetadata = true)
public class Rustic {
	public static final String MODID = "rustic";
	public static final String NAME = "Rustic";
	public static final String VERSION = "0.2";

	@SidedProxy(clientSide = "rustic.core.ClientProxy", serverSide = "rustic.core.CommonProxy")
	public static CommonProxy proxy;

	public static CreativeTabs tab = new CreativeTabs("rustic") {
		@Override
		public String getTabLabel() {
			return "rustic";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return new ItemStack(ModBlocks.VASE);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void displayAllRelevantItems(NonNullList<ItemStack> p_78018_1_) {
			for (Item item : Item.REGISTRY) {
				if (item == null) {
					continue;
				}
				for (CreativeTabs tab : item.getCreativeTabs()) {
					if (tab == this) {
						item.getSubItems(item, this, p_78018_1_);
					}
				}
			}

			FluidStack fs = new FluidStack(ModFluids.OLIVE_OIL,
					ForgeModContainer.getInstance().universalBucket.getCapacity());
			ItemStack stack = new ItemStack(ForgeModContainer.getInstance().universalBucket);
			IFluidHandlerItem fluidHandler = new FluidBucketWrapper(stack);
			if (fluidHandler.fill(fs, true) == fs.amount) {
				ItemStack filled = fluidHandler.getContainer();
				p_78018_1_.add(filled);
			}

			if (this.getRelevantEnchantmentTypes() != null) {
				this.addEnchantmentBooksToList(p_78018_1_, this.getRelevantEnchantmentTypes());
			}
		}
	};

	@Mod.Instance
	public static Rustic instance;

	static {
		FluidRegistry.enableUniversalBucket();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}
}
