package rustic.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import rustic.common.tileentity.ContainerApiary;
import rustic.common.tileentity.TileEntityApiary;
import rustic.core.Rustic;

public class GuiApiary extends GuiContainer {

	public static final int WIDTH = 176;
	public static final int HEIGHT = 166;

	private static final ResourceLocation background = new ResourceLocation(Rustic.MODID, "textures/gui/apiary.png");

	TileEntityApiary te;
	InventoryPlayer playerInv;

	public GuiApiary(ContainerApiary container, InventoryPlayer playerInv) {
		super(container);

		this.playerInv = playerInv;
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
		this.fontRenderer.drawString(this.te.getDisplayName().getUnformattedText(), 8, 6, 4210752);
		this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2,
				4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

}
