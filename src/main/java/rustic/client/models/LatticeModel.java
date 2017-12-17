package rustic.client.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.common.property.IExtendedBlockState;
import rustic.common.blocks.BlockLattice;
import rustic.core.Rustic;

public class LatticeModel implements IModel {

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		IBakedModel base = null;
		IBakedModel[] bars = new IBakedModel[6];
		IBakedModel leaves = null;
		IBakedModel[] leaf_bars = new IBakedModel[6];

		// d u n s w e
		ModelRotation[] rotations = new ModelRotation[] { ModelRotation.X90_Y0, ModelRotation.X270_Y0,
				ModelRotation.X0_Y0, ModelRotation.X0_Y180, ModelRotation.X0_Y270, ModelRotation.X0_Y90 };

		try {
			IModel baseModel = ModelLoaderRegistry
					.getModel(new ResourceLocation(Rustic.MODID, "block/lattice_iron_base"));
			IModel barModel = ModelLoaderRegistry
					.getModel(new ResourceLocation(Rustic.MODID, "block/lattice_iron_bar"));
			IModel leavesModel = ModelLoaderRegistry
					.getModel(new ResourceLocation(Rustic.MODID, "block/lattice_leaves_base"));
			IModel leaf_barModel = ModelLoaderRegistry
					.getModel(new ResourceLocation(Rustic.MODID, "block/lattice_leaves_bar"));

			for (int i = 0; i < 6; i++) {
				base = baseModel.bake(new TRSRTransformation(rotations[i]), DefaultVertexFormats.BLOCK,
						ModelLoader.defaultTextureGetter());
				bars[i] = barModel.bake(new TRSRTransformation(rotations[i]), DefaultVertexFormats.BLOCK,
						ModelLoader.defaultTextureGetter());
				leaves = leavesModel.bake(new TRSRTransformation(rotations[i]), DefaultVertexFormats.BLOCK,
						ModelLoader.defaultTextureGetter());
				leaf_bars[i] = leaf_barModel.bake(new TRSRTransformation(rotations[i]), DefaultVertexFormats.BLOCK,
						ModelLoader.defaultTextureGetter());
			}
		} catch (Exception e) {
			Rustic.logger.warn(e.getMessage());
		}

		if (base == null) {
			return ModelLoaderRegistry.getMissingModel().bake(state, format, bakedTextureGetter);
		}

		return new BakedLatticeModel(base, bars, leaves, leaf_bars);
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableList.of(new ResourceLocation(Rustic.MODID, "block/lattice_iron_base"),
				new ResourceLocation(Rustic.MODID, "block/lattice_iron_bar"),
				new ResourceLocation(Rustic.MODID, "block/lattice_leaves_base"),
				new ResourceLocation(Rustic.MODID, "block/lattice_leaves_bar"));
	}

	public static class LatticeModelLoader implements ICustomModelLoader {

		private static LatticeModel model;

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
			model = new LatticeModel();
		}

		@Override
		public boolean accepts(ResourceLocation modelLocation) {
			return modelLocation.getResourceDomain().equals(Rustic.MODID)
					&& modelLocation.getResourcePath().equals("iron_lattice");
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation) throws Exception {
			if (model == null) {
				model = new LatticeModel();
			}
			return model;
		}

	}

	public static class BakedLatticeModel implements IBakedModel {

		private IBakedModel base;
		private IBakedModel[] bars;
		private IBakedModel leaves;
		private IBakedModel[] leaf_bars;

		public BakedLatticeModel(IBakedModel base, IBakedModel[] bars, IBakedModel leaves, IBakedModel[] leaf_bars) {
			this.base = base;
			this.bars = bars;
			this.leaves = leaves;
			this.leaf_bars = leaf_bars;
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			if (state instanceof IExtendedBlockState) {
				List<IBakedModel> subModels = getSubModels((IExtendedBlockState) state);
				List<BakedQuad> quads = new ArrayList<BakedQuad>();

				for (IBakedModel model : subModels) {
					quads.addAll(model.getQuads(state, side, rand));
				}

				return quads;
			}
			return Collections.EMPTY_LIST;
		}

		private List<IBakedModel> getSubModels(IExtendedBlockState state) {
			List<IBakedModel> subModels = new ArrayList<IBakedModel>();

			subModels.add(base);
			boolean foliage = state.getValue(BlockLattice.LEAVES);
			if (foliage) {
				subModels.add(this.leaves);
			}
			try {
				for (int i = 0; i < 6; i++) {
					if (state.getValue(BlockLattice.CONNECTIONS[i])) {
						subModels.add(bars[i]);
						if (foliage) {
							subModels.add(leaf_bars[i]);
						}
					}
				}
			} catch (NullPointerException e) {
				
			}

			return subModels;
		}

		@Override
		public boolean isAmbientOcclusion() {
			return true;
		}

		@Override
		public boolean isGui3d() {
			return true;
		}

		@Override
		public boolean isBuiltInRenderer() {
			return false;
		}

		@Override
		public TextureAtlasSprite getParticleTexture() {
			return base.getParticleTexture();
		}

		@Override
		public ItemOverrideList getOverrides() {
			return ItemOverrideList.NONE;
		}

	}

}
