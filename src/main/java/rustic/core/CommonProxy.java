package rustic.core;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import rustic.client.GuiProxy;
import rustic.common.Config;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.book.BookManager;
import rustic.common.crafting.Recipes;
import rustic.common.entities.ModEntities;
import rustic.common.items.ItemFluidBottle;
import rustic.common.items.ModItems;
import rustic.common.network.PacketHandler;
import rustic.common.potions.PotionsRustic;
import rustic.common.util.DispenseRope;
import rustic.common.world.WorldGeneratorRustic;
import rustic.compat.Compat;

public class CommonProxy {
	
	public static Configuration config;

	public void preInit(FMLPreInitializationEvent event) {
		
		PacketHandler.registerMessages();
		
		File directory = event.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "rustic.cfg"));
        Config.readConfig();
        
        ModFluids.init();
        ModBlocks.init();
        ModItems.init();
        ModEntities.init();
        
        PotionsRustic.init();

        Recipes.initOres();
        
        GameRegistry.registerWorldGenerator(new WorldGeneratorRustic(), 0);
        
        if (Loader.isModLoaded("dynamictrees")) {
			Compat.preInitDynamicTreesCompat();
		}
        
    }

    public void init(FMLInitializationEvent event) {
    	NetworkRegistry.INSTANCE.registerGuiHandler(Rustic.instance, new GuiProxy());
    	initFluidBottle();
    	
    	if (Loader.isModLoaded("dynamictrees")) {
			Compat.initDynamicTreesCompat();
		}
    }

    public void postInit(FMLPostInitializationEvent event) {
    	if (config.hasChanged()) {
            config.save();
        }
    	
    	BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(Item.getItemFromBlock(ModBlocks.ROPE), DispenseRope.getInstance());
    	
    	BookManager.init();
    }
    
    private void initFluidBottle() {
    	ItemFluidBottle.addFluid(ModFluids.OLIVE_OIL);
    	ItemFluidBottle.addFluid(ModFluids.IRONBERRY_JUICE);
    	ItemFluidBottle.addFluid(ModFluids.WILDBERRY_JUICE);
    	ItemFluidBottle.addFluid(ModFluids.GRAPE_JUICE);
    	ItemFluidBottle.addFluid(ModFluids.APPLE_JUICE);
    	ItemFluidBottle.addFluid(ModFluids.ALE_WORT);
    	ItemFluidBottle.addFluid(FluidRegistry.getFluid(ModFluids.HONEY.getName()));
    	if (FluidRegistry.isFluidRegistered("for.honey")) {
    		ItemFluidBottle.addFluid(FluidRegistry.getFluid("for.honey"));
    	}
    	
    	ItemFluidBottle.addFluid(ModFluids.ALE);
    	ItemFluidBottle.addFluid(ModFluids.CIDER);
    	ItemFluidBottle.addFluid(ModFluids.IRON_WINE);
    	ItemFluidBottle.addFluid(ModFluids.MEAD);
    	ItemFluidBottle.addFluid(ModFluids.WILDBERRY_WINE);
    	ItemFluidBottle.addFluid(ModFluids.WINE);
    }
    
    public void spawnAlchemySmokeFX(World world, int brewTime, double x, double y, double z, double xVel, double yVel, double zVel) {
    }
    
    public void disableIronSkinRenderer(String entityName) {
    }
	
}
