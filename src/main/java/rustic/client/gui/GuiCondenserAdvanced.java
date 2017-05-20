package rustic.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidStack;
import rustic.client.util.FluidClientUtil;
import rustic.common.tileentity.ContainerCondenserAdvanced;
import rustic.common.tileentity.TileEntityCondenserAdvanced;
import rustic.core.Rustic;

public class GuiCondenserAdvanced extends GuiContainer {
	
	public static final int WIDTH = 176;
	public static final int HEIGHT = 166;

	private static final ResourceLocation background = new ResourceLocation(Rustic.MODID, "textures/gui/condenser_advanced.png");

	TileEntityCondenserAdvanced te;
	InventoryPlayer playerInv;
	
	public GuiCondenserAdvanced(ContainerCondenserAdvanced container, InventoryPlayer playerInv) {
		super(container);

		this.playerInv = playerInv;
		this.te = container.getTile();
		xSize = WIDTH;
		ySize = HEIGHT;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		//this.fontRendererObj.drawString(this.te.getDisplayName().getUnformattedText(), 8, 6, 4210752);
		//this.fontRendererObj.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2,
		//		4210752);
		if (isPointInRegion(133, 27, 16, 32, mouseX, mouseY)) {
			FluidStack fluid = null;
			if (te.getFluid() != null && te.getAmount() > 0) {
				fluid = new FluidStack(te.getFluid(), te.getAmount());
			}
			drawFluidTooltip(fluid, te.getCapacity(), mouseX, mouseY);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		if (te.getAmount() > 0) {
			FluidClientUtil.drawGuiLiquid(new FluidStack(te.getFluid(), te.getAmount()), te.getCapacity(), guiLeft + 133, guiTop + 27, 16, 32, background);
		}

		if (te.isBurning()) {
			int k = this.getBurnLeftScaled(13);
			this.drawTexturedModalRect(guiLeft + 67, guiTop + 46 + 12 - k, 176, 12 - k, 14, k + 1);
		}

		int l = this.getBrewProgressScaled(50);
		this.drawTexturedModalRect(guiLeft + 44, guiTop + 17, 176, 14, l, 53);
	}

	private int getBrewProgressScaled(int pixels) {
		int i = this.te.brewTime;
		int j = this.te.totalBrewTime;
		return j != 0 && i != 0 ? (i * pixels / j) + 1 : 0;
	}

	private int getBurnLeftScaled(int pixels) {
		int i = this.te.currentItemBurnTime;

		if (i == 0) {
			i = 200;
		}
		
		return (this.te.condenserBurnTime * pixels) / i;
	}
	
	protected void drawFluidTooltip(FluidStack fluid, int capacity, int x, int y) {
		List<String> lines = new ArrayList<String>();
		if (fluid == null || fluid.getFluid() == null || fluid.amount <= 0) {
			lines.add(TextFormatting.GRAY + I18n.format("tooltip.rustic.empty"));
			lines.add(TextFormatting.GRAY + "" + 0 + "/" + capacity);
		} else {
			lines.add(fluid.getFluid().getRarity(fluid).rarityColor + fluid.getLocalizedName());
			lines.add(TextFormatting.GRAY + "" + fluid.amount + "/" + capacity);
		}
		drawHoveringText(lines, x - guiLeft, y - guiTop);
	}

}
