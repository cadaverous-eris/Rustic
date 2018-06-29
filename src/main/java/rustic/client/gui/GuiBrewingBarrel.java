package rustic.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import rustic.client.util.FluidClientUtil;
import rustic.common.blocks.fluids.FluidBooze;
import rustic.common.tileentity.ContainerBrewingBarrel;
import rustic.common.tileentity.TileEntityBrewingBarrel;
import rustic.core.Rustic;

public class GuiBrewingBarrel extends GuiContainer {
	
	public static final int WIDTH = 176;
	public static final int HEIGHT = 166;
	private static final ResourceLocation background = new ResourceLocation(Rustic.MODID, "textures/gui/brewing_barrel.png");

	private TileEntityBrewingBarrel te;
	//private IInventory playerInv;
	
	public GuiBrewingBarrel(ContainerBrewingBarrel container, IInventory playerInv) {
		super(container);
		
		//this.playerInv = playerInv;
		this.te = container.getTile();
		xSize = WIDTH;
		ySize = HEIGHT;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (isPointInRegion(62, 27, 16, 32, mouseX, mouseY)) {
			drawFluidTooltip(te.getInputFluid(), te.getInputCapacity(), mouseX, mouseY);
		}
		if (isPointInRegion(116, 27, 16, 32, mouseX, mouseY)) {
			drawFluidTooltip(te.getOutputFluid(), te.getOutputCapacity(), mouseX, mouseY);
		}
		if (isPointInRegion(26, 35, 16, 16, mouseX, mouseY)) {
			drawFluidTooltip(te.getAuxiliaryFluid(), te.getAuxiliaryCapacity(), mouseX, mouseY);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if (te.getInputAmount() > 0) {
			FluidClientUtil.drawGuiLiquid(new FluidStack(te.getInputFluid(), te.getInputAmount()), te.getInputCapacity(), guiLeft + 62, guiTop + 27, 16, 32, background);
		}
		if (te.getOutputAmount() > 0) {
			FluidClientUtil.drawGuiLiquid(new FluidStack(te.getOutputFluid(), te.getOutputAmount()), te.getOutputCapacity(), guiLeft + 116, guiTop + 27, 16, 32, background);
		}
		if (te.getAuxiliaryAmount() > 0) {
			FluidClientUtil.drawGuiLiquid(new FluidStack(te.getAuxiliaryFluid(), te.getAuxiliaryAmount()), te.getAuxiliaryCapacity(), guiLeft + 26, guiTop + 35, 16, 16, background);
		}
		
		if (te.slot0Empty()) {
			this.drawTexturedModalRect(guiLeft + 62, guiTop + 7, 176, 54, 16, 16);
		}
		if (te.slot1Empty()) {
			this.drawTexturedModalRect(guiLeft + 116, guiTop + 7, 176, 70, 16, 16);
		}
		if (te.slot2Empty()) {
			this.drawTexturedModalRect(guiLeft + 26, guiTop + 15, 176, 70, 16, 16);
		}
		
		if (te.isBrewing()) {
			int k = this.getBubbles();
			this.drawTexturedModalRect(guiLeft + 139, guiTop + 29 + 28 - k, 176, 28 - k, 11, k);
		}
		
		int l = this.getBrewProgressScaled(24);
		this.drawTexturedModalRect(guiLeft + 85, guiTop + 35, 176, 28, l, 16);
		int m = this.getBrewProgressScaled(10);
		this.drawTexturedModalRect(guiLeft + 45, guiTop + 38, 176, 44, m, 10);
	}
	
	private int getBrewProgressScaled(int pixels) {
		int i = this.te.getBrewTime();
		int j = this.te.getMaxBrewTime();
		return j != 0 && i != 0 ? (i * pixels / j) + 1 : 0;
	}
	
	private int getBubbles() {
		int i = this.te.getBrewTime();
		int j = this.te.getMaxBrewTime();
		return j != 0 && i != 0 ? (int) ((i / (float) j) * 5600) % 28 : 0;
	}
	
	protected void drawFluidTooltip(FluidStack fluid, int capacity, int x, int y) {
		List<String> lines = new ArrayList<String>();
		if (fluid == null || fluid.getFluid() == null || fluid.amount <= 0) {
			lines.add(TextFormatting.GRAY + I18n.format("tooltip.rustic.empty"));
			lines.add(TextFormatting.GRAY + "" + 0 + "/" + capacity);
		} else {
			lines.add(fluid.getFluid().getRarity(fluid).rarityColor + fluid.getLocalizedName());
			lines.add(TextFormatting.GRAY + "" + fluid.amount + "/" + capacity);
			if (fluid.getFluid() instanceof FluidBooze && fluid.tag != null && fluid.tag.hasKey(FluidBooze.QUALITY_NBT_KEY, 5)) {
				float quality = fluid.tag.getFloat(FluidBooze.QUALITY_NBT_KEY);
				lines.add(TextFormatting.GOLD + "" + I18n.format("tooltip.rustic.quality") + quality);
			}
		}
		drawHoveringText(lines, x - guiLeft, y - guiTop);
	}

}
