package rustic.common.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.renderer.RenderTomato;
import rustic.common.items.ModItems;
import rustic.core.Rustic;

public class ModEntities {
	
	public static void init() {
		int id = 0;
		EntityRegistry.registerModEntity(new ResourceLocation(Rustic.MODID + ":tomato"), EntityTomato.class, "tomato", id++, Rustic.instance, 64, 1, true);
	}
	
	@SideOnly(Side.CLIENT)
	public static void initRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityTomato.class, new RenderTomato.Factory());
	}

}
