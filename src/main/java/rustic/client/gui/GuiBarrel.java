package rustic.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Optional;
import rustic.common.tileentity.ContainerBarrel;
import rustic.common.tileentity.TileEntityBarrel;
import rustic.compat.Compat;
import rustic.core.Rustic;
import vazkii.quark.api.IChestButtonCallback;
import vazkii.quark.api.IItemSearchBar;

@Optional.Interface(modid = Compat.QUARK, iface = "vazkii.quark.api.IItemSearchBar", striprefs = true)
@Optional.Interface(modid = Compat.QUARK, iface = "vazkii.quark.api.IChestButtonCallback", striprefs = true)
public class GuiBarrel extends GuiContainer implements IItemSearchBar, IChestButtonCallback {

	public static final int WIDTH = 176;
    public static final int HEIGHT = 166;

    private static final ResourceLocation background = new ResourceLocation(Rustic.MODID, "textures/gui/generic_27.png");
    
    private TileEntityBarrel te;
    private InventoryPlayer playerInv;

	public GuiBarrel(ContainerBarrel container, InventoryPlayer playerInv) {
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
        this.fontRenderer.drawString(this.playerInv.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(background);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}

	@Override
	public void onSearchBarAdded(GuiTextField guiTextField) {
		guiTextField.y++;
	}

    @Override
    public boolean onAddChestButton(GuiButton guiButton, int i) {
        return true;
    }
}
