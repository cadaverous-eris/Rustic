package rustic.client.models;

import java.util.Collections;
import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.TRSRTransformation;
import rustic.common.blocks.ModBlocks;

public class LiquidBarrelItemModel implements IBakedModel {
	
	private IBakedModel baseLiquidBarrelModel;
	private LiquidBarrelItemOverrideList liquidBarrelItemOverrideList;
	public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(ModBlocks.LIQUID_BARREL.getRegistryName(), "inventory");
	
	public LiquidBarrelItemModel(IBakedModel baseModel) {
		baseLiquidBarrelModel = baseModel;
		liquidBarrelItemOverrideList = new LiquidBarrelItemOverrideList(Collections.EMPTY_LIST);
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return baseLiquidBarrelModel.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return baseLiquidBarrelModel.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return baseLiquidBarrelModel.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return baseLiquidBarrelModel.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return baseLiquidBarrelModel.getParticleTexture();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return baseLiquidBarrelModel.getItemCameraTransforms();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return liquidBarrelItemOverrideList;
	}

	@Override
	public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
		if (baseLiquidBarrelModel instanceof IBakedModel) {
			 Matrix4f matrix4f = ((IBakedModel)baseLiquidBarrelModel).handlePerspective(cameraTransformType).getRight();
			 return Pair.of(this, matrix4f);
		}
		ItemCameraTransforms itemCameraTransforms = baseLiquidBarrelModel.getItemCameraTransforms();
		ItemTransformVec3f itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType);
		TRSRTransformation tr = new TRSRTransformation(itemTransformVec3f);
		Matrix4f mat = null;
		if (tr != null) {
			mat = tr.getMatrix();
		}
		return Pair.of(this, mat);
	}

}
