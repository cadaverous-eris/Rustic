package rustic.client.renderer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.common.potions.PotionsRustic;
import rustic.core.ClientProxy;

@SideOnly(Side.CLIENT)
public class LayerIronSkin<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
	
	public static boolean FORCE_IRONSKIN_RENDER() { // for debugging
		return false;
	}
	
	private RenderLivingBase<? extends EntityLivingBase> renderer;
	private T model;
	private boolean initializedModelRendererOverriders = false;
	private List<ModelRendererOverrider> modelRendererOverriderList = new ArrayList<>();
	
	//private Method renderModelMethod;
	private Method bindEntityTextureMethod;
	
	protected static final FloatBuffer WHITE_COLOR_BUFFER;
	static {
		WHITE_COLOR_BUFFER = GLAllocation.createDirectFloatBuffer(4);
		WHITE_COLOR_BUFFER.put(0, 1F);
		WHITE_COLOR_BUFFER.put(1, 1F);
		WHITE_COLOR_BUFFER.put(2, 1F);
		WHITE_COLOR_BUFFER.put(3, 1F);
		WHITE_COLOR_BUFFER.rewind();
	}

	public LayerIronSkin(RenderLivingBase<? extends EntityLivingBase> renderer, T model) {
		this.renderer = renderer;
		this.model = (T) model;

		//this.renderModelMethod = this.getPrivateRendererMethod("renderModel", "func_77036_a", EntityLivingBase.class, float.class, float.class, float.class, float.class, float.class, float.class);
		this.bindEntityTextureMethod = this.getPrivateRendererMethod("bindEntityTexture", "func_180548_c", Entity.class);
	}
	
	
	private Method getPrivateRendererMethod(String methodName, String obfName, Class<?>... paramTypes) {
		Class<?> rendererClass = this.renderer.getClass();
		while (Render.class.isAssignableFrom(rendererClass)) {
			try {
				Method m = ReflectionHelper.findMethod(rendererClass, methodName, obfName, paramTypes);
				if (m != null) return m;
			} catch (Exception e) {}
			rendererClass = rendererClass.getSuperclass();
		}
		
		throw new ReflectionHelper.UnableToFindMethodException(new RuntimeException(
			"Unable to find method " + methodName + " [" + obfName + "] " + " in class " + this.renderer.getClass().getName()
		));
	}
	
	private void generateModelRendererOverriders() {
		modelRendererOverriderList.clear();
		model.boxList.stream().map(ModelRendererOverrider::new).forEach(modelRendererOverriderList::add);
		initializedModelRendererOverriders = true;
	}
	
	public static void preRenderEntity(EntityLivingBase entity) {
		if (entity.isInvisible() || (
				!entity.isPotionActive(PotionsRustic.IRON_SKIN_POTION) &&
				!entity.isPotionActive(PotionsRustic.FULLMETAL_POTION) &&
				!LayerIronSkin.FORCE_IRONSKIN_RENDER()
		)) return;
		
		GlStateManager.enablePolygonOffset();
		GlStateManager.doPolygonOffset(1.0F, 1.0F);
	}
	
	public static void postRenderEntity(EntityLivingBase entity) {
		if (entity.isInvisible() || (
				!entity.isPotionActive(PotionsRustic.IRON_SKIN_POTION) &&
				!entity.isPotionActive(PotionsRustic.FULLMETAL_POTION) &&
				!LayerIronSkin.FORCE_IRONSKIN_RENDER()
		)) return;
		
		GlStateManager.disablePolygonOffset();
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}

	@Override
	public void doRenderLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		if (ClientProxy.IRONSKIN_LAYER_DISABLED_RENDERERS.contains(renderer.getClass()))
			return;
		
		if (entityLivingBaseIn.isInvisible())
			return;
		
		PotionEffect ironSkinEffect = entityLivingBaseIn.getActivePotionEffect(PotionsRustic.IRON_SKIN_POTION);
		final int ironSkinAmplifier = (ironSkinEffect == null) ? -1 : ironSkinEffect.getAmplifier();
		final boolean hasFullmetalEffect = entityLivingBaseIn.isPotionActive(PotionsRustic.FULLMETAL_POTION);
		if ((ironSkinEffect == null) && !hasFullmetalEffect && !FORCE_IRONSKIN_RENDER())
			return;
		
		if (!initializedModelRendererOverriders && modelRendererOverriderList.isEmpty()) {
			generateModelRendererOverriders();
		}
		
		modelRendererOverriderList.forEach(ModelRendererOverrider::enableOverride);
		
		ResourceLocation metalTexture;
		{
			int x = model.textureWidth;
	        int y = model.textureHeight;
			
	        int sizeX = 128, sizeY = 128;
	        if (x >= 128) sizeX = 128;
	        else if (x >= 64) sizeX = 64;
	        else if (x >= 32) sizeX = 32;
	        else sizeX = 16;
	        if (y >= 128) sizeY = 128;
	        else if (y >= 64) sizeY = 64;
	        else if (y >= 32) sizeY = 32;
	        else sizeY = 16;
			
	        metalTexture = new ResourceLocation("rustic", "textures/entity/layer/fullmetal_" + sizeX + "_" + sizeY + ".png");
		
	        //metalTexture = new ResourceLocation("textures/blocks/iron_block.png");
		}
		
		final int entityTextureUnit = OpenGlHelper.GL_TEXTURE2 + 1;
		
		
		boolean boundEntityTexture = false;
		
		float opacity = 0.9F;
		if (hasFullmetalEffect) {
			opacity = 0.92F;
		} else if (ironSkinEffect != null) {
			if (ironSkinAmplifier == 0) opacity = 0.875F;
			else if (ironSkinAmplifier == 1) opacity = 0.89F;
			else if (ironSkinAmplifier == 2) opacity = 0.91F;
			else opacity = 0.92F;
		}
		
		GlStateManager.disablePolygonOffset();
		
		GlStateManager.setActiveTexture(entityTextureUnit);
		try {
			Object result = this.bindEntityTextureMethod.invoke(this.renderer, entityLivingBaseIn);
			boundEntityTexture = ((Boolean) result).booleanValue();
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace(); // TODO Auto-generated catch block
			boundEntityTexture = false;
		}
		
		if (boundEntityTexture) {
			GL11.glTexEnv(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_COLOR, WHITE_COLOR_BUFFER);
			GlStateManager.enableTexture2D();
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_RGB, GL13.GL_CONSTANT);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_RGB, GL13.GL_PREVIOUS);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
	        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, GL13.GL_TEXTURE3);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_ALPHA, GL13.GL_PREVIOUS);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
	        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
			
			
			GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			//OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
			this.renderer.bindTexture(metalTexture);
			/*try {
				this.bindEntityTextureMethod.invoke(this.renderer, entityLivingBaseIn);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace(); // TODO Auto-generated catch block
			}*/
			GlStateManager.enableTexture2D();
			
			// TODO: remove?
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, GL11.GL_TEXTURE_ENV_MODE, GL13.GL_COMBINE);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, GL11.GL_MODULATE);
			GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
            GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, GL13.GL_PRIMARY_COLOR);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_RGB, GL11.GL_SRC_COLOR);
	        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_RGB, GL11.GL_SRC_COLOR);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_ALPHA, GL11.GL_MODULATE);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE0_ALPHA, GL13.GL_TEXTURE3);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_SOURCE1_ALPHA, GL13.GL_PRIMARY_COLOR);
			GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND0_ALPHA, GL11.GL_SRC_ALPHA);
	        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_OPERAND1_ALPHA, GL11.GL_SRC_ALPHA);
			
			
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.DST_ALPHA);
			
			GlStateManager.color(1F, 1F, 1F, opacity);
			
			
	        this.doRenderBaseModel(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, 1.0F * scale);
	        
			
	        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
			//OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
	        GlStateManager.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
	        
	        
	        GlStateManager.color(1F, 1F, 1F, 1F);
	        
	        GlStateManager.disableBlend();
        
	        GlStateManager.setActiveTexture(entityTextureUnit);
	        GlStateManager.glTexEnvi(GL11.GL_TEXTURE_ENV, OpenGlHelper.GL_COMBINE_RGB, 8448);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PREVIOUS);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
	        GlStateManager.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
	        GlStateManager.disableTexture2D();
	        //GlStateManager.bindTexture(prevBoundTexture);
		}
        
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		//OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
		
		modelRendererOverriderList.forEach(ModelRendererOverrider::disableOverride);
	}
	
	protected void doRenderBaseModel(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {		
		ModelBase mainModel = this.model;
		if (mainModel instanceof ModelPlayer) {
			ModelPlayer modelPlayer = (ModelPlayer) mainModel;
			
			final ModelRenderer[] parts = {
				modelPlayer.bipedBody,
				modelPlayer.bipedHead,
				modelPlayer.bipedLeftArm, modelPlayer.bipedRightArm,
				modelPlayer.bipedLeftLeg, modelPlayer.bipedLeftLeg,
				modelPlayer.bipedBodyWear,
				modelPlayer.bipedHeadwear,
				modelPlayer.bipedLeftArmwear, modelPlayer.bipedRightArmwear,
				modelPlayer.bipedLeftLegwear, modelPlayer.bipedRightLegwear,
			};
			final int numParts = parts.length / 2;
			final int wearOffset = numParts;
			
			final int bodyIndex = 0;
			final int headIndex = 1;
			final int leftArmIndex = 2, rightArmIndex = 3;
			final int leftLegIndex = 4, rightLegIndex = 5;
			
			final boolean[] partsHiddenVal = new boolean[parts.length];
			for (int i = 0; i < numParts; i++) {
				partsHiddenVal[i] = parts[i].isHidden;
				partsHiddenVal[i + wearOffset] = parts[i + wearOffset].isHidden;
			}
			
			// TODO: set some parts to hidden?
			//modelPlayer.bipedHead.isHidden = true;
			
			modelPlayer.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
			
			for (int i = 0; i < numParts; i++) {
				parts[i].isHidden = partsHiddenVal[i];
				parts[i + wearOffset].isHidden = partsHiddenVal[i + wearOffset];
			}
		} else {
			mainModel.render(entityLivingBaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
		}
	}
	
	
	public static class ModelRendererOverrider {
		
		private ModelRenderer modelRendererRef;
		private boolean overridden = false;
		
		private boolean origCompiled;
		private int origDisplayList;
		private List<ModelBox> origCubeList;

		private boolean altCompiled;
		private int altDisplayList;
		private List<ModelBox> altCubeList;
	    
	    ModelRendererOverrider(ModelRenderer modelRenderer) {
	    	modelRendererRef = modelRenderer;
	    	
	    	origCompiled = modelRenderer.compiled;
	    	origDisplayList = modelRenderer.displayList;
	    	origCubeList = modelRenderer.cubeList;
	    	
	    	generateCubeList();
	    }
	    
	    public boolean isOverridden() {
	    	return overridden;
	    }
	    
	    public void setActive(boolean active) {
	    	if (active == overridden) return;
	    	
	    	if (active) {
	    		origCompiled = modelRendererRef.compiled;
	    		origDisplayList = modelRendererRef.displayList;
	    		
	    		modelRendererRef.compiled = altCompiled;
	    		modelRendererRef.displayList = altDisplayList;
	    		modelRendererRef.cubeList = altCubeList;
	    	} else {
	    		altCompiled = modelRendererRef.compiled;
	    		altDisplayList = modelRendererRef.displayList;
	    		
	    		modelRendererRef.compiled = origCompiled;
	    		modelRendererRef.displayList = origDisplayList;
	    		modelRendererRef.cubeList = origCubeList;
	    	}
	    	overridden = active;
	    }
	    
	    public void enableOverride() {
	    	setActive(true);
	    }
	    
	    public void disableOverride() {
	    	setActive(false);
	    }
		
	    private void generateCubeList() {
	    	this.altCubeList = new ArrayList<>();
	    	for (ModelBox origBox : origCubeList) {
	    		this.altCubeList.add(new ModelBoxOverride(modelRendererRef, origBox));
	    	}
	    }
		
	}
	
	public static class ModelBoxOverride extends ModelBox {
		
		public ModelBoxOverride(ModelRenderer renderer, ModelBox orig) {
			// garbage in for tex coords and scale because it's annoying to calculate and will be overridden anyway
			super(renderer, 0, 0, orig.posX1, orig.posY1, orig.posZ1, (int) (orig.posX2 - orig.posX1), (int) (orig.posY2 - orig.posY1), (int) (orig.posZ2 - orig.posZ1), 0);
			this.posX1 = orig.posX1;
			this.posY1 = orig.posY1;
			this.posZ1 = orig.posZ1;
			this.posX2 = orig.posX2;
			this.posY2 = orig.posY2;
			this.posZ2 = orig.posZ2;
			this.boxName = orig.boxName;
			this.vertexPositions[0] = orig.vertexPositions[0];
			this.vertexPositions[1] = orig.vertexPositions[1];
			this.vertexPositions[2] = orig.vertexPositions[2];
			this.vertexPositions[3] = orig.vertexPositions[3];
			this.vertexPositions[4] = orig.vertexPositions[4];
			this.vertexPositions[5] = orig.vertexPositions[5];
			this.vertexPositions[6] = orig.vertexPositions[6];
			this.vertexPositions[7] = orig.vertexPositions[7];
			this.quadList[0] = new MultiTexturedQuad(orig.quadList[0]);
			this.quadList[1] = new MultiTexturedQuad(orig.quadList[1]);
			this.quadList[2] = new MultiTexturedQuad(orig.quadList[2]);
			this.quadList[3] = new MultiTexturedQuad(orig.quadList[3]);
			this.quadList[4] = new MultiTexturedQuad(orig.quadList[4]);
			this.quadList[5] = new MultiTexturedQuad(orig.quadList[5]);
		}
		
	}
	
	public static class MultiTexturedQuad extends TexturedQuad {
		
		public MultiTexturedQuad(TexturedQuad orig) {
			super(orig.vertexPositions);
			this.invertNormal = orig.invertNormal;
		}
		
		/**
	     * Draw this primitve. This is typically called only once as the generated drawing instructions are saved by the
	     * renderer and reused later.
	     */
	    @SideOnly(Side.CLIENT)
	    public void draw(BufferBuilder renderer, float scale) {
	        Vec3d vec3d = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
	        Vec3d vec3d1 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D);
	        Vec3d vec3d2 = vec3d1.crossProduct(vec3d).normalize();
	        float f = (float)vec3d2.x;
	        float f1 = (float)vec3d2.y;
	        float f2 = (float)vec3d2.z;

	        if (this.invertNormal) {
	            f = -f;
	            f1 = -f1;
	            f2 = -f2;
	        }

	        renderer.begin(7, VertexFormatMultiTex.OLDMODEL_POSITION_MULTITEX_NORMAL);

	        for (int i = 0; i < 4; ++i) {
	            PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
	            renderer.pos(positiontexturevertex.vector3D.x * (double)scale, positiontexturevertex.vector3D.y * (double)scale, positiontexturevertex.vector3D.z * (double)scale).tex((double)positiontexturevertex.texturePositionX, (double)positiontexturevertex.texturePositionY).normal(f, f1, f2).endVertex();
	        }

	        Tessellator.getInstance().draw();
	    }
		
	}

}