package rustic.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderArmorStand;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import rustic.client.models.FluidBottleModel;
import rustic.client.models.LatticeModel;
import rustic.client.renderer.LayerIronSkin;
import rustic.client.renderer.VertexFormatMultiTex;
import rustic.client.util.ItemColorCache;
import rustic.common.Config;
import rustic.common.blocks.IColoredBlock;
import rustic.common.blocks.ModBlocks;
import rustic.common.blocks.fluids.ModFluids;
import rustic.common.entities.ModEntities;
import rustic.common.items.IColoredItem;
import rustic.common.items.ModItems;
import rustic.compat.Compat;

public class ClientProxy extends CommonProxy {

	private static List<Block> coloredBlocks = new ArrayList<Block>();
	private static List<Item> coloredItems = new ArrayList<Item>();
	
	public static final Set<Class<?>> IRONSKIN_LAYER_DISABLED_RENDERERS = new HashSet<>();

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		VertexFormatMultiTex.init();
		ModelLoaderRegistry.registerLoader(new LatticeModel.LatticeModelLoader());
		ModelLoaderRegistry.registerLoader(FluidBottleModel.LoaderFluidBottle.INSTANCE);
		ModFluids.initModels();
		ModBlocks.initModels();
		ModItems.initModels();
		ModEntities.initRenderers();
		
		if (Loader.isModLoaded("dynamictrees")) {
			Compat.preInitDynamicTreesClientCompat();
		}
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);
		
		if (Loader.isModLoaded("dynamictrees")) {
			Compat.initDynamicTreesClientCompat();
		}
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
		
		//if (!Minecraft.getMinecraft().getFramebuffer().isStencilEnabled())
			//Minecraft.getMinecraft().getFramebuffer().enableStencil();
		
		//ForgeRegistries.ENTITIES.getEntries().forEach(entry -> System.out.println(entry.getKey().toString() + " -> " + entry.getValue().getEntityClass().getName()));
		
		
		Collection<Render<? extends Entity>> renderers = Minecraft.getMinecraft().getRenderManager().entityRenderMap.values();
		Collection<RenderPlayer> playerRenderers = Minecraft.getMinecraft().getRenderManager().getSkinMap().values();
		
		for (String entityName : Config.IRONSKIN_RENDER_ENTITY_BLACKLIST) {
			disableIronSkinRenderer(entityName);
		}
		
		for (Render<? extends Entity> renderer : renderers) {
			if (!IRONSKIN_LAYER_DISABLED_RENDERERS.contains(renderer.getClass()) && (renderer instanceof RenderLivingBase) && !(renderer instanceof RenderArmorStand)) {
				RenderLivingBase<? extends EntityLivingBase> renderLivingBase = (RenderLivingBase<?>) renderer;
				try {
					renderLivingBase.addLayer(new LayerIronSkin<>(renderLivingBase, renderLivingBase.getMainModel()));
				} catch (Exception e) {
					IRONSKIN_LAYER_DISABLED_RENDERERS.add(renderLivingBase.getClass());
				}
			}
		}

		for (RenderPlayer renderPlayer : playerRenderers) {
			try {
				renderPlayer.addLayer(new LayerIronSkin<>(renderPlayer, renderPlayer.getMainModel()));
			} catch (Exception e) {
				IRONSKIN_LAYER_DISABLED_RENDERERS.add(renderPlayer.getClass());
			}
		}
		
		initColorizer();
		
		if (Minecraft.getMinecraft().getResourceManager() instanceof IReloadableResourceManager) {
			((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).registerReloadListener(ItemColorCache.INSTANCE);
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
	
	@Override
	public void disableIronSkinRenderer(String entityName) {
		if ((entityName == null) || entityName.isEmpty()) return;
		
		ResourceLocation entityId = new ResourceLocation(entityName);
		if (!ForgeRegistries.ENTITIES.containsKey(entityId)) return;
		
		Class<? extends Entity> entityClass = ForgeRegistries.ENTITIES.getValue(entityId).getEntityClass();
		if (!EntityLivingBase.class.isAssignableFrom(entityClass)) return;
		
		Render<Entity> renderer = Minecraft.getMinecraft().getRenderManager().getEntityClassRenderObject(entityClass);
		if (renderer == null) return;
		
		IRONSKIN_LAYER_DISABLED_RENDERERS.add(renderer.getClass());
    }

}
