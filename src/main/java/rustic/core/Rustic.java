package rustic.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.EventHandlerClient;
import rustic.common.Config;
import rustic.common.EventHandlerCommon;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.crafting.Recipes;
import rustic.common.items.ModItems;
import rustic.common.potions.EventHandlerPotions;
import rustic.compat.Compat;
import rustic.compat.crafttweaker.CraftTweakerHelper;

@Mod(modid = Rustic.MODID, name = Rustic.NAME, version = Rustic.VERSION, dependencies = Rustic.DEPENDENCIES)
public class Rustic {
	public static final String MODID = "rustic";
	public static final String NAME = "Rustic";
	public static final String VERSION = "1.0.7";
	public static final String DEPENDENCIES = "after:dynamictrees@[1.12.2-0.7.8,);after:dynamictreesbop;before:dynamictreestc";

	@SidedProxy(clientSide = "rustic.core.ClientProxy", serverSide = "rustic.core.CommonProxy")
	public static CommonProxy proxy;

	public static CreativeTabs decorTab = new CreativeTabs("rustic.decor") {
		@Override
		public String getTabLabel() {
			return "rustic.decor";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return new ItemStack(ModBlocks.VASE);
		}
	};
	
	public static CreativeTabs farmingTab = new CreativeTabs("rustic.farming") {
		@Override
		public String getTabLabel() {
			return "rustic.farming";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return new ItemStack(ModItems.OLIVES);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void displayAllRelevantItems(NonNullList<ItemStack> p_78018_1_) {
			for (Object obj : Item.REGISTRY) {
				Item item = (Item) obj;
				if (item == null) {
					continue;
				}
				for (CreativeTabs tab : item.getCreativeTabs()) {
					if (tab == this) {
						item.getSubItems(this, p_78018_1_);
					}
				}
			}
			
			for (Fluid fluid : ModFluids.getFluids()) {
				FluidStack fs = new FluidStack(fluid,
						ForgeModContainer.getInstance().universalBucket.getCapacity());
				ItemStack stack = new ItemStack(ForgeModContainer.getInstance().universalBucket);
				IFluidHandlerItem fluidHandler = new FluidBucketWrapper(stack);
				if (fluidHandler.fill(fs, true) == fs.amount) {
					ItemStack filled = fluidHandler.getContainer();
					p_78018_1_.add(filled);
				}
			}
		}
	};
	
	public static CreativeTabs alchemyTab = new CreativeTabs("rustic.alchemy") {
		@Override
		public String getTabLabel() {
			return "rustic.alchemy";
		}

		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return ModItems.ELIXIR.getDefaultInstance();
		}
	};

	@Mod.Instance
	public static Rustic instance;
	
	public static final Logger logger = LogManager.getLogger(MODID);

	static {
		FluidRegistry.enableUniversalBucket();
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new EventHandlerCommon());
		MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
		
		MinecraftForge.EVENT_BUS.register(new EventHandlerPotions());
		
		MinecraftForge.EVENT_BUS.register(this);
		
		proxy.preInit(event);
		
		if(Loader.isModLoaded("crafttweaker")) {
			CraftTweakerHelper.preInit();
		}
	}
	
	@SubscribeEvent
	public void initRecipes(RegistryEvent.Register<IRecipe> event) {
		Recipes.init();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
		
		if (Loader.isModLoaded("forestry") && Config.ENABLE_FORESTRY_COMPAT) {
			Compat.doForestryCompat();
		}
	}
}
