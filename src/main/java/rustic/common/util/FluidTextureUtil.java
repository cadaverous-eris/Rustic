package rustic.common.util;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class FluidTextureUtil {
	public static Map<Fluid, TextureAtlasSprite> stillTextures = Maps.newHashMap();

	// This code is borrowed and tweaked from Fancy Fluid Storage by LordMau5
	// Find the source here:
	// https://github.com/Lordmau5/FFS/blob/1.10.2/src/main/java/com/lordmau5/ffs/client/FluidHelper.java

	public static void initTextures(TextureMap map) {
		for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) {
			if (fluid.getStill() != null) {
				String still = fluid.getStill().toString();
				TextureAtlasSprite sprite;
				if (map.getTextureExtry(still) != null) {
					sprite = map.getTextureExtry(still);
				} else {
					sprite = map.registerSprite(fluid.getStill());
				}
				stillTextures.put(fluid, sprite);
			}
		}
	}
}
