package rustic.core;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.EventHandlerClient;
import rustic.common.EventHandlerCommon;
import rustic.common.blocks.ModBlocks;
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
    	public String getTabLabel(){
    		return "rustic";
    	}
		@Override
		@SideOnly(Side.CLIENT)
		public ItemStack getTabIconItem() {
			return new ItemStack(ModBlocks.VASE);
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
