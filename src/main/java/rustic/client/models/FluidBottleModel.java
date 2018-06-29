package rustic.client.models;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import rustic.core.Rustic;

public class FluidBottleModel implements IModel {

	public static final ModelResourceLocation LOCATION = new ModelResourceLocation(
			new ResourceLocation(Rustic.MODID, "fluid_bottle"), "inventory");

	private static final float NORTH_Z_FLUID = 7.498f / 16f;
	private static final float SOUTH_Z_FLUID = 8.502f / 16f;

	public static final IModel MODEL = new FluidBottleModel();

	@Nullable
	private final ResourceLocation liquidLocation;
	@Nullable
	private final ResourceLocation bottleLocation;
	@Nullable
	private final Fluid fluid;

	public FluidBottleModel() {
		liquidLocation = new ResourceLocation("minecraft", "items/potion_overlay");
		bottleLocation = new ResourceLocation("minecraft", "items/potion_bottle_drinkable");
		fluid = null;
	}

	public FluidBottleModel(Fluid fluid) {
		liquidLocation = new ResourceLocation("minecraft", "items/potion_overlay");
		bottleLocation = new ResourceLocation("minecraft", "items/potion_bottle_drinkable");
		this.fluid = fluid;
	}

	@Override
	public IModel retexture(ImmutableMap<String, String> textures) {
		/*ResourceLocation liquid = liquidLocation;
		ResourceLocation bottle = bottleLocation;

		if (textures.containsKey("fluid")) {
			liquid = new ResourceLocation(textures.get("fluid"));
		}
		if (textures.containsKey("bottle")) {
			bottle = new ResourceLocation(textures.get("bottle"));
		}*/

		return new FluidBottleModel(fluid);
	}

	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		String fluidName = customData.get("fluid");
		Fluid fluid = FluidRegistry.getFluid(fluidName);
		if (fluid == null) {
			fluid = this.fluid;
		}
		return new FluidBottleModel(fluid);
	}

	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableList.of();
	}

	@Override
	public Collection<ResourceLocation> getTextures() {
		ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
		if (liquidLocation != null)
			builder.add(liquidLocation);
		if (bottleLocation != null)
			builder.add(bottleLocation);
		return builder.build();
	}

	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);

		TRSRTransformation transform = state.apply(Optional.empty()).orElse(TRSRTransformation.identity());
		TextureAtlasSprite fluidSprite = null;
		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

		if (fluid != null) {
			fluidSprite = bakedTextureGetter.apply(fluid.getStill());
		}

		if (bottleLocation != null) {
			IBakedModel model = (new ItemLayerModel(ImmutableList.of(bottleLocation))).bake(state, format,
					bakedTextureGetter);
			builder.addAll(model.getQuads(null, null, 0));
		}

		if (liquidLocation != null && fluidSprite != null) {
			TextureAtlasSprite liquid = bakedTextureGetter.apply(liquidLocation);
			builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite,
					NORTH_Z_FLUID, EnumFacing.NORTH, fluid.getColor()));
			builder.addAll(ItemTextureQuadConverter.convertTexture(format, transform, liquid, fluidSprite,
					SOUTH_Z_FLUID, EnumFacing.SOUTH, fluid.getColor()));
		}

		return new BakedFluidBottle(this, builder.build(), fluidSprite, format, Maps.immutableEnumMap(transformMap),
				Maps.<String, IBakedModel>newHashMap());

	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}

	public enum LoaderFluidBottle implements ICustomModelLoader {
		INSTANCE;

		@Override
		public boolean accepts(ResourceLocation modelLocation) {
			return modelLocation.getResourceDomain().equals(Rustic.MODID)
					&& modelLocation.getResourcePath().contains("fluid_bottle");
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation) {
			return MODEL;
		}

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {
		}
	}

	private static final class BakedFluidBottleOverrideHandler extends ItemOverrideList {
		public static final BakedFluidBottleOverrideHandler INSTANCE = new BakedFluidBottleOverrideHandler();

		private BakedFluidBottleOverrideHandler() {
			super(ImmutableList.<ItemOverride>of());
		}

		@Override
		@Nonnull
		public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack,
				@Nullable World world, @Nullable EntityLivingBase entity) {

			FluidStack fluidStack = null;
			if (stack.hasTagCompound() && stack.getTagCompound().hasKey(FluidHandlerItemStack.FLUID_NBT_KEY)) {
				fluidStack = FluidStack.loadFluidStackFromNBT(stack.getTagCompound().getCompoundTag(FluidHandlerItemStack.FLUID_NBT_KEY));
			}

			if (fluidStack == null) {
				return originalModel;
			}

			BakedFluidBottle model = (BakedFluidBottle) originalModel;

			Fluid fluid = fluidStack.getFluid();
			String name = fluid.getName();

			if (!model.cache.containsKey(name)) {
				IModel parent = model.parent.process(ImmutableMap.of("fluid", name));
				Function<ResourceLocation, TextureAtlasSprite> textureGetter;
				textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
					public TextureAtlasSprite apply(ResourceLocation location) {
						return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
					}
				};
				IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format,
						textureGetter);
				model.cache.put(name, bakedModel);
				return bakedModel;
			}

			return model.cache.get(name);
		}
	}

	private static final class BakedFluidBottle implements IBakedModel {

		private final FluidBottleModel parent;
		private final Map<String, IBakedModel> cache;
		private final ImmutableMap<TransformType, TRSRTransformation> transforms;
		private final ImmutableList<BakedQuad> quads;
		private final TextureAtlasSprite particle;
		private final VertexFormat format;

		public BakedFluidBottle(FluidBottleModel parent, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle,
				VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms,
				Map<String, IBakedModel> cache) {
			this.quads = quads;
			this.particle = particle;
			this.format = format;
			this.parent = parent;
			this.transforms = itemTransforms();
			this.cache = cache;
		}

		@Override
		public ItemOverrideList getOverrides() {
			return BakedFluidBottleOverrideHandler.INSTANCE;
		}

		@Override
		public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
			return PerspectiveMapWrapper.handlePerspective(this, transforms, cameraTransformType);
		}
		
		private static ImmutableMap<TransformType, TRSRTransformation> itemTransforms() {
			TRSRTransformation thirdperson = get(0, 3, 1, 0, 0, 0, 0.55f);
            TRSRTransformation firstperson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
            ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
            builder.put(TransformType.GROUND,                  get(0, 2, 0, 0, 0, 0, 0.5f));
            builder.put(TransformType.HEAD,                    get(0, 13, 7, 0, 180, 0, 1));
            builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
            builder.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
            builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
            builder.put(TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstperson));
            return (ImmutableMap) builder.build();
		}

		private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
			return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(
					new Vector3f(tx / 16, ty / 16, tz / 16),
					TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null));
		}

		private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1),
				null);

		private static TRSRTransformation leftify(TRSRTransformation transform) {
			return TRSRTransformation.blockCenterToCorner(
					flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
		}

		@Override
		public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
			if (side == null)
				return quads;
			return ImmutableList.of();
		}

		public boolean isAmbientOcclusion() {
			return true;
		}

		public boolean isGui3d() {
			return false;
		}

		public boolean isBuiltInRenderer() {
			return false;
		}

		public TextureAtlasSprite getParticleTexture() {
			return particle;
		}

		public ItemCameraTransforms getItemCameraTransforms() {
			return ItemCameraTransforms.DEFAULT;
		}
	}

}
