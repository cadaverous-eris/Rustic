package rustic.client.models;

import java.awt.Color;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.primitives.Ints;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import rustic.client.util.FluidClientUtil;

public class LiquidBarrelFilledItemModel implements IPerspectiveAwareModel {

	private int amount;
	private int capacity;
	private Fluid fluid;
	private IBakedModel parentModel;

	public LiquidBarrelFilledItemModel(IBakedModel parentModel, int amount, int capacity, Fluid fluid) {
		this.parentModel = parentModel;
		this.amount = amount;
		this.capacity = capacity;
		this.fluid = fluid;
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		List<BakedQuad> combinedQuadsList = new ArrayList(parentModel.getQuads(state, side, rand));
		combinedQuadsList.addAll(getLiquidQuads(amount, capacity, fluid));
		return combinedQuadsList;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return parentModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return parentModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return parentModel.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return parentModel.getParticleTexture();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return parentModel.getItemCameraTransforms();
	}

	@Override
	public ItemOverrideList getOverrides() {
		throw new UnsupportedOperationException("The finalized model does not have an override list.");
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		if (parentModel instanceof IPerspectiveAwareModel) {
			Matrix4f matrix4f = ((IPerspectiveAwareModel) parentModel).handlePerspective(cameraTransformType).getRight();
			return Pair.of(this, matrix4f);
		}
		ItemCameraTransforms itemCameraTransforms = parentModel.getItemCameraTransforms();
		ItemTransformVec3f itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType);
		TRSRTransformation tr = new TRSRTransformation(itemTransformVec3f);
		Matrix4f mat = null;
		if (tr != null) {
			mat = tr.getMatrix();
		}
		return Pair.of(this, mat);
	}

	private int[] vertexToInts(float x, float y, float z, int color, TextureAtlasSprite texture, float u, float v) {
		return new int[] { Float.floatToRawIntBits(x), Float.floatToRawIntBits(y), Float.floatToRawIntBits(z), color, Float.floatToRawIntBits(texture.getInterpolatedU(u)), Float.floatToRawIntBits(texture.getInterpolatedV(v)), 0 };
	}

	private List<BakedQuad> getLiquidQuads(int amount, int capacity, Fluid fluid) {

		TextureAtlasSprite fluidTexture = FluidClientUtil.stillTextures.get(fluid);
		List<BakedQuad> returnList = new ArrayList<BakedQuad>();

		float x1, x2, x3, x4;
		float y1, y2, y3, y4;
		float z1, z2, z3, z4;
		int blue, green, red, alpha;

		x1 = x2 = 0.8125F;
		x3 = x4 = 0.1875F;
		z1 = z4 = 0.8125F;
		z2 = z3 = 0.1875F;
		y1 = y2 = y3 = y4 = 0.125F + (0.8125F * ((float) amount / (float) capacity));

		int c = fluid.getColor();
		blue = c & 0xFF;
		green = (c >> 8) & 0xFF;
		red = (c >> 16) & 0xFF;
		alpha = (c >> 24) & 0xFF;
		int color = (red&0x0ff)<<24|(green&0x0ff)<<16|(blue&0x0ff)<<8|alpha&0x0ff;

		int[] vertexData = Ints.concat(vertexToInts(x1, y1, z1, color, fluidTexture, 16, 16), vertexToInts(x2, y2, z2, color, fluidTexture, 16, 0), vertexToInts(x3, y3, z3, color, fluidTexture, 0, 0), vertexToInts(x4, y4, z4, color, fluidTexture, 0, 16));

		returnList.add(new BakedQuad(vertexData, 0, EnumFacing.UP, fluidTexture, true, DefaultVertexFormats.BLOCK));

		return returnList;
	}

}
