package rustic.client.util;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class FluidClientUtil {
	public static Map<Fluid, TextureAtlasSprite> stillTextures = Maps.newHashMap();

	public static void drawGuiLiquid(FluidStack fluid, int capacity, int x, int y, int width, int height,
			ResourceLocation ogTexture) {

		if (fluid != null && fluid.getFluid() != null) {
			int fluidHeight = (int) (height * (fluid.amount / (float) capacity));
			drawFluidStack(fluid, x, y + height - fluidHeight, width, fluidHeight);
			Minecraft.getMinecraft().getTextureManager().bindTexture(ogTexture);
			GlStateManager.color(1, 1, 1, 1);
		}

	}

	public static void drawFluidStack(FluidStack fluid, int x, int y, int width, int height) {

		if (fluid == null || fluid.getFluid() == null || fluid.amount <= 0) {
			return;
		}

		TextureAtlasSprite sprite = stillTextures.get(fluid.getFluid());
		if (sprite == null) {
			return;
		}

		int color = fluid.getFluid().getColor();

		float minU = sprite.getMinU();
		float maxU = sprite.getMaxU();
		float minV = sprite.getMinV();
		float maxV = sprite.getMaxV();

		float uDiff = maxU - minU;
		float vDiff = maxV - minV;

		int spriteWidth = sprite.getIconWidth();
		int spriteHeight = sprite.getIconHeight();

		if (spriteWidth <= 0 || spriteHeight <= 0) {
			return;
		}

		int iterationsY = (int) (height / spriteHeight);
		float leftOverY = (height % spriteHeight);
		float leftOverNormY = leftOverY / (float) spriteHeight;
		int iterationsX = (int) (width / spriteWidth);
		float leftOverX = (width % spriteWidth);
		float leftOverNormX = leftOverX / (float) spriteWidth;
		

		Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

		for (int i = 0; i < iterationsY; i++) {
			for (int j = 0; j < iterationsX; j++) {
				ClientUtils.drawTexturedColoredRect(x + (spriteWidth * j), y + (spriteHeight * i), spriteWidth,
						spriteHeight, minU, minV, maxU, maxV, color);
			}
			if (leftOverX > 0) {
				ClientUtils.drawTexturedColoredRect(x + (spriteWidth * iterationsX), y + (spriteHeight * i), leftOverX,
						spriteHeight, minU, minV, minU + (uDiff * leftOverNormX), maxV, color);
			}
		}
		if (leftOverY > 0) {
			for (int j = 0; j < iterationsX; j++) {
				ClientUtils.drawTexturedColoredRect(x + (spriteWidth * j), y + (spriteHeight * iterationsY), spriteWidth,
						leftOverY, minU, minV, maxU, minV + (vDiff * leftOverNormY), color);
			}
			if (leftOverX > 0) {
				ClientUtils.drawTexturedColoredRect(x + (spriteWidth * iterationsX), y + (spriteHeight * iterationsY), leftOverX,
						leftOverY, minU, minV, minU + (uDiff * leftOverNormX), minV + (vDiff * leftOverNormY), color);
			}
		}

	}

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
