package rustic.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.particle.ParticleCloud;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemLingeringPotion;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import rustic.client.models.FluidBottleModel;
import rustic.client.renderer.LayerIronSkin;
import rustic.client.renderer.LiquidBarrelRenderer;
import rustic.common.blocks.IColoredBlock;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.entities.ModEntities;
import rustic.common.items.IColoredItem;
import rustic.common.items.ModItems;
import rustic.common.potions.PotionsRustic;
import rustic.common.tileentity.TileEntityLiquidBarrel;

public class ClientProxy extends CommonProxy {

	private static List<Block> coloredBlocks = new ArrayList<Block>();
	private static List<Item> coloredItems = new ArrayList<Item>();

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		ModelLoaderRegistry.registerLoader(FluidBottleModel.LoaderFluidBottle.INSTANCE);
		ModFluids.initModels();
		ModBlocks.initModels();
		ModItems.initModels();
		ModEntities.initRenderers();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		initColorizer();
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {

		Collection<Render<? extends Entity>> renderers = Minecraft.getMinecraft().getRenderManager().entityRenderMap
				.values();
		Collection<RenderPlayer> playerRenderers = Minecraft.getMinecraft().getRenderManager().getSkinMap().values();

		for (Render renderer : renderers) {
			if (renderer instanceof RenderLivingBase) {
				RenderLivingBase renderLivingBase = (RenderLivingBase) renderer;
				renderLivingBase.addLayer(new LayerIronSkin(renderLivingBase, renderLivingBase.getMainModel()));
			}
		}

		for (RenderPlayer renderPlayer : playerRenderers) {
			renderPlayer.addLayer(new LayerIronSkin(renderPlayer, renderPlayer.getMainModel()));
		}

	}

	public static void addColoredBlock(Block block) {
		if (block instanceof IColoredBlock) {
			coloredBlocks.add(block);
		}
	}

	public static void addColoredItem(Item item) {
		if (item instanceof IColoredItem) {
			coloredItems.add(item);
		}
	}

	private void initColorizer() {
		for (Block block : coloredBlocks) {
			if (block instanceof IColoredBlock) {
				IColoredBlock coloredBlock = (IColoredBlock) block;
				if (coloredBlock.getBlockColor() != null) {
					Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(coloredBlock.getBlockColor(),
							block);
				}
				if (coloredBlock.getItemColor() != null) {
					Minecraft.getMinecraft().getItemColors().registerItemColorHandler(coloredBlock.getItemColor(),
							block);
				}
			}
		}
		for (Item item : coloredItems) {
			if (item instanceof IColoredItem) {
				IColoredItem coloredItem = (IColoredItem) item;
				if (coloredItem.getItemColor() != null) {
					Minecraft.getMinecraft().getItemColors().registerItemColorHandler(coloredItem.getItemColor(), item);
				}
			}
		}
	}
	
	@Override
	public void spawnAlchemySmokeFX(World world, int brewTime, double x, double y, double z, double xVel, double yVel, double zVel) {
		Particle smoke = new ParticleSmokeNormal.Factory().createParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), world, x, y, z, xVel, yVel, zVel);
    	float colorScale = 0.4F;
    	float r = colorScale * ((MathHelper.sin(30 + brewTime / 16F) * 0.5F) + 0.5F);
    	float g = colorScale * ((MathHelper.sin(brewTime / 16F) * 0.5F) + 0.5F);
    	float b = colorScale * ((MathHelper.sin(60 + brewTime / 16F) * 0.5F) + 0.5F);
		smoke.setRBGColorF(r, g, b);
    	smoke.setMaxAge(10);
    	Minecraft.getMinecraft().effectRenderer.addEffect(smoke);
    }

}
