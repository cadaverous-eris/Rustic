package rustic.client.models;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.SimpleModelFontRenderer;
import rustic.common.items.ModItems;

public class BookBakedModel implements IBakedModel {

	private static final ResourceLocation font = new ResourceLocation("minecraft", "textures/font/ascii.png");
	private static final ResourceLocation font2 = new ResourceLocation("minecraft", "font/ascii");
	public static final ModelResourceLocation modelResourceLocation = new ModelResourceLocation(
			ModItems.BOOK.getRegistryName().toString());

	private final IBakedModel parent;
	private List<BakedQuad> textQuads;

	public BookBakedModel(IBakedModel parent) {
		this.parent = parent;
		this.textQuads = new ArrayList<BakedQuad>();
		initText();
	}

	private void initText() {
		Matrix4f m = new Matrix4f();
		m.m20 = 1f / 128f;
		m.m01 = m.m12 = -m.m20;
		m.m33 = 1;
		Matrix3f rotation = new Matrix3f();
		m.getRotationScale(rotation);
		Matrix3f angleZ = new Matrix3f();
		angleZ.rotZ(-1.5708F);
		rotation.mul(rotation, angleZ);
		m.setRotationScale(rotation);
		m.setScale(1F * m.getScale());
		m.setTranslation(new Vector3f(0.25F, 0.2505F, 0.328125F));

		SimpleModelFontRenderer fontRenderer = new SimpleModelFontRenderer(Minecraft.getMinecraft().gameSettings, font,
				Minecraft.getMinecraft().getTextureManager(), false, m, DefaultVertexFormats.ITEM) {
			@Override
			protected float renderUnicodeChar(char c, boolean italic) {
				return super.renderDefaultChar(126, italic);
			}
		};

		int maxLineWidth = 64;
		TextureAtlasSprite fontSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(font2.toString());
		fontRenderer.setSprite(fontSprite);
		fontRenderer.setFillBlanks(false);

		int yOffset = 2;
		String title = "Almanac";
		List<String> lines = fontRenderer.listFormattedStringToWidth(title, maxLineWidth);
		for (int line = 0; line < lines.size(); line++) {
			int offset = ((maxLineWidth - fontRenderer.getStringWidth(lines.get(line))) / 2);
			fontRenderer.drawString(lines.get(line), offset, yOffset, 0x414141);
			yOffset += (fontRenderer.FONT_HEIGHT);
		}

		textQuads = fontRenderer.build();
	}

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		List<BakedQuad> quads = new ArrayList<BakedQuad>();
		quads.addAll(parent.getQuads(state, side, rand));
		quads.addAll(textQuads);
		return quads;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return parent.isAmbientOcclusion();
	}

	@Override
	public boolean isGui3d() {
		return parent.isGui3d();
	}

	@Override
	public boolean isBuiltInRenderer() {
		return parent.isBuiltInRenderer();
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return parent.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return parent.getOverrides();
	}

	@Override
	public ItemCameraTransforms getItemCameraTransforms() {
		return parent.getItemCameraTransforms();
	}

	// @Override
	// public Pair<? extends IBakedModel, javax.vecmath.Matrix4f>
	// handlePerspective(ItemCameraTransforms.TransformType cameraTransformType)
	// {
	// return parent.handlePerspective(cameraTransformType);
	// }

}
